package com.ganbook.services;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.IBinder;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.ImageView;

import com.ganbook.amazinglist.special.CustomToast;
import com.ganbook.app.MyApp;
import com.ganbook.interfaces.GanbookApiInterface;
import com.ganbook.models.OnPostS3Answer;
import com.ganbook.models.PictureAnswer;
import com.ganbook.models.SuccessAnswer;
import com.ganbook.notifications.NotificationManagerMy;
import com.ganbook.user.User;
import com.ganbook.utils.Const;
import com.project.ganim.R;

import java.util.ArrayList;
import java.util.Stack;
import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.project.ganim.R.string.pictures;

public class SupportUploadService extends Service {

    private static final String TAG = SupportUploadService.class.getName();
    ArrayList<PictureAnswer> pictureAnswers;
    private NotificationManager mNotifyManager;
    private NotificationCompat.Builder mBuilder;
    private int maxItems;
    private int successPicturesUploaded;
    private LocalBroadcastManager broadcastManager;
    String ganId, classId;
    private AlertDialog.Builder builder1;

    @BindView(R.id.try_again_btn)
    ImageView tryAgain;

    private int notifId = 1337;

    Stack<PictureAnswer> uploadStack;

    @Inject
    @Named("POST")
    GanbookApiInterface ganbookApiInterfacePOST;
    private PictureAnswer currentItem;

    public SupportUploadService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        LocalBroadcastManager.getInstance(this).registerReceiver((uploadingReceiver),
                new IntentFilter(Const.UPLOADING_INTENT));
        LocalBroadcastManager.getInstance(this).registerReceiver((appendUploadReceiver),
                new IntentFilter(Const.APPEND_UPLOAD_INTENT));

        ((MyApp) getApplication()).getGanbookApiComponent().inject(this);

        broadcastManager = LocalBroadcastManager.getInstance(this);

        mNotifyManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setContentTitle(this.getString(R.string.uploading_now_notif))
                .setSmallIcon(NotificationManagerMy.getSmallIcon())
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_launcher))
                .setAutoCancel(true);
    }

    private BroadcastReceiver appendUploadReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent != null && intent.getExtras() != null) {

                ArrayList<PictureAnswer> pictures = intent.getExtras().getParcelableArrayList(Const.PICTURES_PARCELABLE_LIST);

                Log.d(TAG, "append: appeend pictures = \" + pictures);\nnd pictures = " + pictures);

                for (PictureAnswer p :
                        pictures) {

                    if (uploadStack != null && !uploadStack.contains(p) && !p.equals(currentItem)) {

                        uploadStack.push(p);

                        maxItems += 1;
                    }
                }
            }
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d(TAG, "support service started");

        uploadStack = new Stack<>();

        if (intent != null && intent.getExtras() != null) {

            successPicturesUploaded = 0;

            pictureAnswers = intent.getExtras().getParcelableArrayList(Const.PICTURES_PARCELABLE_LIST);
            ganId = intent.getExtras().getString(Const.GAN_ID);
            classId = intent.getExtras().getString(Const.CLASS_ID);

            Log.d(TAG, "onStartCommand: pictures = " + pictureAnswers);

            for (PictureAnswer p :
                 pictureAnswers) {
                 uploadStack.push(p);
            }

            maxItems = uploadStack.size();

            if (pictureAnswers.size() > 0)
            uploadFileToServer(uploadStack.pop());
        }


        return Service.START_STICKY_COMPATIBILITY;
    }

    public void uploadFileToServer(PictureAnswer pictureAnswer) {

//        pictureAnswer.setStatus(0);
//
//        adapter.updateItem(pos, pictureAnswer);

        currentItem = pictureAnswer;

        Log.d(TAG, "stack count = " + uploadStack.size());

        Intent intent = new Intent(this, UploadService.class);
        intent.putExtra(Const.PICTURE_PARCELABLE, pictureAnswer);
        intent.putExtra(Const.GAN_ID, ganId);
        intent.putExtra(Const.CLASS_ID, classId);
        startService(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.d(TAG, "onDestroy: upload support service destroyed");
        LocalBroadcastManager.getInstance(this).unregisterReceiver(uploadingReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(appendUploadReceiver);

        mNotifyManager.cancelAll();

        stopSelf();
    }

    BroadcastReceiver uploadingReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Bundle extras = intent.getExtras();

            if(extras!=null) {

                OnPostS3Answer successAnswer = extras.getParcelable(Const.SUCCESS_STATUS);

                PictureAnswer pictureAnswer = extras.getParcelable(Const.PICTURE_PARCELABLE);

                Log.d(TAG, "onReceive: successful uploaded picture to adapter = " + pictureAnswer);

                if (successAnswer != null && pictureAnswer != null) {

                    Log.d(TAG, "onReceive: Received upload answer " + successAnswer);

                    if (successAnswer.isSuccess()) {

                        successPicturesUploaded++;
                    } else {


                    }

                    // Sets the progress indicator to a max value, the
                    // current completion percentage, and "determinate"
                    // state
                    mBuilder.setProgress(maxItems, successPicturesUploaded, false);
                    mBuilder.setContentTitle(successPicturesUploaded + " " + getString(R.string.of_hint)
                            + " " + maxItems + " " + getString(pictures) + " "
                            + getString(R.string.uploaded));

                    // Displays the progress bar for the first time.
                    mNotifyManager.notify(notifId, mBuilder.build());

                    //if we have pending pictures in stack - go to upload next
                    if (uploadStack != null && uploadStack.size() > 0) {

                        PictureAnswer nextPicture = uploadStack.pop();

                        uploadFileToServer(nextPicture);

                    } else {
                        /** we processed all pictures now we need to refresh counter
                         * and send pushafterupload method
                         */

                        Log.d(TAG, "onReceive: success picture count === " + successPicturesUploaded);
                        CustomToast.show(context.getApplicationContext(), "Successfully uploaded!");
                        // When the loop is finished, updates the notification
                        mBuilder.setContentTitle(getString(R.string.operation_succeeded));
                        mBuilder.setContentText(getString(R.string.upload_completed))
                                // Removes the progress bar
                                .setProgress(0,0,false);
                        mNotifyManager.notify(notifId, mBuilder.build());

                        //if last uploaded has video duration - its video
                        String type = pictureAnswer.getVideoDuration() == null
                                || pictureAnswer.getVideoDuration().equals("") ? "0" : "1";

                        if (successPicturesUploaded > 0) {
                            Call<SuccessAnswer> pushAfterUpload = ganbookApiInterfacePOST
                                    .pushAfterUpload(pictureAnswer.getAlbumId(), User.current.getCurrentClassId(),
                                            successPicturesUploaded, UUID.randomUUID().toString(), type);

                            pushAfterUpload.enqueue(new Callback<SuccessAnswer>() {
                                @Override
                                public void onResponse(Call<SuccessAnswer> call, Response<SuccessAnswer> response) {

                                    SuccessAnswer pushAnswer = response.body();
                                    Log.d(TAG, "onResponse: push answer " + pushAnswer);

                                }

                                @Override
                                public void onFailure(Call<SuccessAnswer> call, Throwable t) {

                                    Log.e(TAG, "onFailure: error while send pushafterupload "
                                            + Log.getStackTraceString(t));
                                }
                            });
                        }

                        Intent allProcessedIntent = new Intent(Const.UPLOADING_DONE_INTENT);

                        intent.putExtra(Const.SUCCESS_COUNT, successPicturesUploaded);

                        broadcastManager.sendBroadcast(allProcessedIntent);

                        Log.d(TAG, "onReceive: stopping service");

                        stopSelf();

                        stopService(new Intent(context, SupportUploadService.class));
                    }
                }
            }
        }
    };
}

package com.ganbook.services;

import android.app.IntentService;
import android.content.Intent;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.util.Log;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.ganbook.app.MyApp;
import com.ganbook.communication.json.createkid_Response;
import com.ganbook.interfaces.GanbookApiInterface;
import com.ganbook.models.SuccessAnswer;
import com.ganbook.models.events.UpdateKidsEvent;
import com.ganbook.s3transferutility.Constants;
import com.ganbook.s3transferutility.Util;
import com.ganbook.utils.Const;
import com.ganbook.utils.ImageUtils;
import com.project.ganim.R;

import org.greenrobot.eventbus.EventBus;

import java.io.File;

import javax.inject.Inject;
import javax.inject.Named;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateKidPicService extends IntentService {

    private static final String TAG = UpdateKidPicService.class.getName();
    public static String name = "com.ganbook.services.UpdateKidPicService";

    public static final String EXTRA_KID_ID = "com.ganbook.services.extra.PARAM1";
    public static final String EXTRA_KID_PIC_PATH = "com.ganbook.services.extra.PARAM2";
    public static final String EXTRA_KID_PIC_NAME = "com.ganbook.services.extra.PARAM3";

    String kidId;
    String picPath;
    String picName;
    createkid_Response _res;

    private TransferUtility transferUtility;

    private LocalBroadcastManager broadcastManager;

    @Inject
    @Named("POST")
    GanbookApiInterface ganbookApiInterfacePOST;
    private int id;

    public UpdateKidPicService() {
        super("UpdateKidPicService");
    }

    public UpdateKidPicService(String name) {
        super(name);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        ((MyApp) getApplication()).getGanbookApiComponent().inject(this);

        broadcastManager = LocalBroadcastManager.getInstance(this);

        transferUtility = Util.getTransferUtility(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        kidId = intent.getExtras().getString(EXTRA_KID_ID);
        picPath = intent.getExtras().getString(EXTRA_KID_PIC_PATH);
        picName = intent.getExtras().getString(EXTRA_KID_PIC_NAME);
        id = intent.getExtras().getInt("id");

        Log.d(TAG, "onHandleIntent: upload picture for kid id = " + kidId +
        " file path = " + picPath + " and pic name = " + picName);

        uploadFile();
    }

    private String getTmpFilePath(File orig_file) {
        String file_name = orig_file.getName();

        File outputDir = MyApp.context.getCacheDir(); // context being the Activity pointer

        File outputFile = new File(outputDir, file_name);
        return outputFile.getAbsolutePath();
    }

    private void uploadFile() {

        File file = new File(picPath);

        String resizedFilePath = getTmpFilePath(file);
        final File local_file = ImageUtils.resizeToFile(picPath, resizedFilePath);

        if (local_file != null) {
            TransferObserver originalPhotoObserver = transferUtility.upload(Constants.BUCKET_NAME,
                    "ImageStore/" + "users/" + picName + ".png", local_file);

            Log.d(TAG, "uploadFile: full file = " + Constants.BUCKET_NAME + "/users/" + picName + ".png");

            originalPhotoObserver.setTransferListener(new TransferListener() {
                @Override
                public void onStateChanged(int id, TransferState state) {

                    Log.d(TAG, "onStateChanged ORIGINAL: id = " + id + " state = " + state);

                    switch (state) {

                        case COMPLETED:

                            Log.d(TAG, "onStateChanged: success sent to S3 amazon, ping server");

                            //If completed we send onposts3 to server
                            Call<SuccessAnswer> updateKidPicCall = ganbookApiInterfacePOST
                                    .updateKidPic(kidId, picName);

                            updateKidPicCall.enqueue(new Callback<SuccessAnswer>() {
                                @Override
                                public void onResponse(Call<SuccessAnswer> call, Response<SuccessAnswer> response) {

                                    SuccessAnswer successAnswer = response.body();

                                    Log.d(TAG, "onResponse: success UPLOADED KID PIC = " + successAnswer);

                                    if (successAnswer != null && successAnswer.isSuccess()) {

                                        Log.d(TAG, "onResponse: success upload pic = ");
                                        EventBus.getDefault().postSticky(new UpdateKidsEvent(true));

                                        postToUISuccess();

                                    } else {

                                        Log.e(TAG, "onResponse: error while upload pic");

                                        postToUIFailed(getString(R.string.pic_upload_failed));
                                    }
                                }

                                @Override
                                public void onFailure(Call<SuccessAnswer> call, Throwable t) {

                                    Log.e(TAG, "onResponse: error while send kid pic " + Log.getStackTraceString(t));

                                    postToUIFailed(getString(R.string.pic_upload_failed));
                                }
                            });
                            break;

                        case IN_PROGRESS:

                            break;

                        case FAILED:

                            postToUIFailed(getString(R.string.pic_upload_failed));
                            Log.d(TAG, "onStateChanged: FAILED ORIGINAL");

                            break;

                    }
                }

                @Override
                public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {

                    Log.d(TAG, "onProgressChanged: orig cur " + bytesCurrent + " total = " + bytesTotal);

                }

                @Override
                public void onError(int id, Exception ex) {

                    Log.d(TAG, "onStateChanged: FAILED ORIGINAL");

                    postToUIFailed(getString(R.string.pic_upload_failed));
                }
            });
        } else {

            postToUIFailed(getString(R.string.photo_not_found));
        }
    }

    private void postToUISuccess() {

        Intent intent = new Intent(Const.UPDATE_KID_PIC_INTENT);

        intent.putExtra(Const.SUCCESS_STATUS, true);
        intent.putExtra("id", id);
        intent.putExtra(EXTRA_KID_PIC_NAME, picName);

        broadcastManager.sendBroadcast(intent);
    }

    private void postToUIFailed(String string) {

        Intent intent = new Intent(Const.UPDATE_KID_PIC_INTENT);

        intent.putExtra(Const.SUCCESS_STATUS, false);
        intent.putExtra(Const.UPDATE_MESSAGE, string);

        broadcastManager.sendBroadcast(intent);

        stopSelf();
    }
}

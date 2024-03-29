package com.ganbook.services;

import android.app.IntentService;
import android.content.Intent;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.util.Log;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.ganbook.app.MyApp;
import com.ganbook.interfaces.GanbookApiInterface;
import com.ganbook.models.SuccessAnswer;
import com.ganbook.s3transferutility.Constants;
import com.ganbook.s3transferutility.Util;
import com.ganbook.user.User;
import com.ganbook.utils.Const;
import com.ganbook.utils.ImageUtils;

import java.io.File;

import javax.inject.Inject;
import javax.inject.Named;

import retrofit2.Call;
import retrofit2.Callback;

public class UploadTeacherImageService extends IntentService {

    private TransferUtility transferUtility;
    private LocalBroadcastManager broadcastManager;
    private String picPath, picName;

    public static final String EXTRA_TEACHER_IMAGE_PATH = "teacherImagePath";
    public static final String EXTRA_TEACHER_IMAGE_NAME = "teacherImageName";

    @Inject
    @Named("POST")
    GanbookApiInterface ganbookApiInterfacePOST;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */

    public UploadTeacherImageService() {
        super("UploadTeacherImageService");
    }

    public UploadTeacherImageService(String name) {
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
    protected void onHandleIntent(@Nullable Intent intent) {
        picPath = intent.getStringExtra(EXTRA_TEACHER_IMAGE_PATH);
        picName = intent.getStringExtra(EXTRA_TEACHER_IMAGE_NAME);

        Log.d(UploadTeacherImageService.this.getClass().getName(), "onHandleIntent: upload picture" +
                " file path = " + picPath + " and pic name = " + picName);

        uploadTeacherImage();
    }

    private String getTmpFilePath(File orig_file) {
        String file_name = orig_file.getName();

        File outputDir = MyApp.context.getCacheDir(); // context being the Activity pointer

        File outputFile = new File(outputDir, file_name);
        return outputFile.getAbsolutePath();
    }

    private void uploadTeacherImage() {
        File file = new File(picPath);

        String resizedFilePath = getTmpFilePath(file);
        final File local_file = ImageUtils.resizeToFile(picPath, resizedFilePath);

        if(local_file != null) {
            TransferObserver transferObserver = transferUtility.upload(Constants.BUCKET_NAME,
                    "ImageStore/" + "users/" + picName + ".png", local_file);

            Log.d("INSTITUTION LOGO", "uploadedLogo: full file = " + Constants.BUCKET_NAME + "/users/" + picName + ".png");

            transferObserver.setTransferListener(new TransferListener() {
                @Override
                public void onStateChanged(int id, TransferState state) {
                    Log.d("ON STATE CHANGE", "UPLOAD TEACHER IMAGE");
                    postToUISuccess();

                    switch (state) {
                        case COMPLETED:
                            Call<SuccessAnswer> uploadInstitutionLogo = ganbookApiInterfacePOST
                                    .uploadTeacherPhoto(User.current.getCurrentGanId(), picName + ".png");
                            Log.d("IMAGE PHOTO", User.current.getCurrentGanId());

                            uploadInstitutionLogo.enqueue(new Callback<SuccessAnswer>() {

                                @Override
                                public void onResponse(Call<SuccessAnswer> call, retrofit2.Response<SuccessAnswer> response) {
                                    if(response.body() != null) {
                                        Log.d("TEACHER PHOTO", response.body().toString());
                                    }
                                }

                                @Override
                                public void onFailure(Call<SuccessAnswer> call, Throwable t) {
                                    Log.d("TEACHER PHOTO ERROR", t.getLocalizedMessage());

                                }
                            });

                    }

                }

                @Override
                public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {

                }

                @Override
                public void onError(int id, Exception ex) {

                }
            });


        }
    }

    private void postToUISuccess() {

        Intent intent = new Intent(Const.UPLOAD_TEACHERS_IMAGE);

        intent.putExtra(Const.SUCCESS_STATUS, true);
        intent.putExtra(EXTRA_TEACHER_IMAGE_NAME, picName);

        broadcastManager.sendBroadcast(intent);
    }
}

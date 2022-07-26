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
import com.ganbook.s3transferutility.Constants;
import com.ganbook.s3transferutility.Util;
import com.ganbook.utils.Const;

import java.io.File;

import javax.inject.Inject;
import javax.inject.Named;

public class UploadAudioService extends IntentService {

    private TransferUtility transferUtility;
    private LocalBroadcastManager broadcastManager;
    private String drawingAudioPath, drawingAudioName, ganId, kidId, classId;

    public static final String EXTRA_AUDIO_PATH = "drawingAudioPath";
    public static final String EXTRA_AUDIO_NAME = "drawingAudioName";

    @Inject
    @Named("POST")
    GanbookApiInterface ganbookApiInterfacePOST;


    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public UploadAudioService(String name) {
        super(name);
    }

    public UploadAudioService() {
        super("UploadAudioService");
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
        drawingAudioPath = intent.getStringExtra(EXTRA_AUDIO_PATH);
        drawingAudioName = intent.getStringExtra(EXTRA_AUDIO_NAME);
        ganId = intent.getStringExtra(Const.GAN_ID);
        kidId = intent.getStringExtra(Const.KID_ID);
        classId = intent.getStringExtra(Const.CLASS_ID);

        Log.d(UploadAudioService.this.getClass().getName(), "onHandleIntent: upload audio" +
                " file path = " + drawingAudioPath + " and audio name = " + drawingAudioName);

        uploadDrawingAudio();
    }



    private void uploadDrawingAudio() {
        if(drawingAudioPath != null) {
            File file = new File(drawingAudioPath);

            TransferObserver transferObserver = transferUtility.upload(Constants.BUCKET_NAME,
                    "Drawings/" + ganId + "/" + classId + "/" + kidId + "/" + drawingAudioName, file);

            Log.d("DRAWING AUDIO", "uploadedAudio: full file = " + Constants.BUCKET_NAME +
                    "/Drawings/" + ganId + "/" + classId + "/" + kidId + "/" + drawingAudioName);

            transferObserver.setTransferListener(new TransferListener() {
                @Override
                public void onStateChanged(int id, TransferState state) {
                    if (state == TransferState.COMPLETED) {
                        Log.d("ON STATE CHANGE", "UPLOAD AUDIO");
                        postToUISuccess();
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

        Intent intent = new Intent(Const.UPLOAD_INSTITUTION_LOGO);

        intent.putExtra(Const.SUCCESS_STATUS, true);
        intent.putExtra(EXTRA_AUDIO_NAME, drawingAudioName);

        broadcastManager.sendBroadcast(intent);
    }
}

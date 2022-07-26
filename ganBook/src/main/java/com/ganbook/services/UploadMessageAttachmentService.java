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
import com.ganbook.models.events.AttachmentFileEvent;
import com.ganbook.models.events.ProgressEvent;
import com.ganbook.s3transferutility.Constants;
import com.ganbook.s3transferutility.Util;
import com.ganbook.user.User;

import org.greenrobot.eventbus.EventBus;

import java.io.File;

public class UploadMessageAttachmentService extends IntentService {

    private TransferUtility transferUtility;
    private LocalBroadcastManager broadcastManager;
    private TransferObserver transferObserver;
    private String attachmentFilePath, attachmentFileName;
    private static final long MAX_ATTACHMENT_SIZE = 20_000_000;

    public UploadMessageAttachmentService(String name) {
        super(name);
    }

    public UploadMessageAttachmentService() {
        super("UploadMessageAttachmentService");
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
        assert intent != null;
        attachmentFilePath = intent.getStringExtra("attachmentFilePath");
        attachmentFileName = intent.getStringExtra("attachmentFileName");

        uploadFileAttachment();

    }

    private void uploadFileAttachment(){

        File fileAttachment = new File(attachmentFilePath);
        long totalBytesToUpload = fileAttachment.length();

        transferObserver = transferUtility.upload(Constants.BUCKET_NAME, "Documents/" + User.current.getCurrentGanId() + "/" + User.current.getCurrentClassId() + "/" + attachmentFileName, fileAttachment);

        transferObserver.setTransferListener(new TransferListener() {
            @Override
            public void onStateChanged(int id, TransferState state) {

                switch (state) {
                    case COMPLETED:
                        Log.d("COMPLETED", Constants.BUCKET_NAME + "Documents/" + User.current.getCurrentGanId() + "/" + User.current.getCurrentClassId() + "/" + attachmentFileName);
                        EventBus.getDefault().postSticky(new AttachmentFileEvent(true, attachmentFileName));
                }

            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                EventBus.getDefault().postSticky(new ProgressEvent((int) bytesCurrent, (int) bytesTotal));
            }

            @Override
            public void onError(int id, Exception ex) {

            }
        });


    }
}

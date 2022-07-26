package com.ganbook.services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.ganbook.app.MyApp;
import com.ganbook.interfaces.GanbookApiInterface;
import com.ganbook.models.DrawingAnswer;
import com.ganbook.models.SuccessAnswer;
import com.ganbook.models.events.UploadDrawingEvent;
import com.ganbook.s3transferutility.Constants;
import com.ganbook.s3transferutility.Util;
import com.ganbook.utils.Const;
import com.ganbook.utils.ImageUtils;

import org.apache.commons.io.FilenameUtils;
import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UploadDrawingService extends IntentService {

    private DrawingAnswer drawing;
    private String kidId, ganId, classId, drawingDescription, drawingAlbumId, drawingAudioPath, drawingAudioName;
    private TransferUtility transferUtility;
    private LocalBroadcastManager broadcastManager;
    private TransferObserver transferObserver;
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
    public UploadDrawingService(String name) {
        super(name);
    }

    public UploadDrawingService() {
        super("UploadDrawingService");
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
        drawing = intent.getExtras().getParcelable(Const.PICTURES_PARCELABLE_LIST);
        ganId = intent.getStringExtra(Const.GAN_ID);
        kidId = intent.getStringExtra(Const.KID_ID);
        classId = intent.getStringExtra(Const.CLASS_ID);
        drawingDescription = intent.getStringExtra(Const.DRAWING_DESCRIPTION);
        drawingAlbumId = intent.getStringExtra(Const.DRAWING_ALBUM_ID);
        drawingAudioPath = intent.getStringExtra(EXTRA_AUDIO_PATH);
        drawingAudioName = intent.getStringExtra(EXTRA_AUDIO_NAME);

        uploadDrawing();
    }

    private String getTmpFilePath(File orig_file) {
        String file_name = orig_file.getName();

        File outputDir = MyApp.context.getCacheDir(); // context being the Activity pointer

        File outputFile = new File(outputDir, file_name);
        return outputFile.getAbsolutePath();
    }

    private void uploadDrawing() {
        File file = new File(drawing.getLocaFilePath());
        File audioFile = new File(drawingAudioPath);
        String resizedFilePath = getTmpFilePath(file);
        File local_file = ImageUtils.resizeToFile(drawing.getLocaFilePath(), resizedFilePath);
        String fileName = null;
        List<File> fileList = new ArrayList<>();

        if(local_file != null && audioFile != null) {
            fileList.add(audioFile);
            fileList.add(local_file);


            
            for(File f : fileList) {
                if(FilenameUtils.getExtension(f.getAbsolutePath()).equals("wav")) {
                    fileName = drawingAudioName;
                } else {
                    fileName = drawing.getDrawingName();
                }

                transferObserver = transferUtility.upload(Constants.BUCKET_NAME, "Drawings/" + ganId + "/" + classId + "/" + kidId + "/" + fileName, f);
            }

            transferObserver.setTransferListener(new TransferListener() {
                @Override
                public void onStateChanged(int id, TransferState state) {

                    switch (state) {
                        case COMPLETED:
                            addDrawingsToAlbum(drawingAlbumId, drawing.getDrawingName(), drawingDescription, drawingAudioName);
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

    private void addDrawingsToAlbum(final String drawingAlbumId, final String drawingName, final String drawingDescription, final String drawingAudio) {
        Call<SuccessAnswer> call = ganbookApiInterfacePOST.uploadDrawings(drawingAlbumId, drawingName, drawingDescription, drawingAudio);

        call.enqueue(new Callback<SuccessAnswer>() {
            @Override
            public void onResponse(Call<SuccessAnswer> call, Response<SuccessAnswer> response) {
                if(response.body() != null) {

                    DrawingAnswer drawingAnswer = new DrawingAnswer();
                    drawingAnswer.setDrawingDescription(drawingDescription);
                    drawingAnswer.setLocaFilePath(drawing.getLocaFilePath());
                    drawingAnswer.setDrawingName(drawingName);
                    drawingAnswer.setDrawingAudio(drawingAudio);
                    drawingAnswer.setDrawingAlbumId(drawingAlbumId);
                    EventBus.getDefault().postSticky(new UploadDrawingEvent(drawingAnswer));
                }
            }

            @Override
            public void onFailure(Call<SuccessAnswer> call, Throwable t) {
                EventBus.getDefault().postSticky(new UploadDrawingEvent(null));

            }
        });
    }
}

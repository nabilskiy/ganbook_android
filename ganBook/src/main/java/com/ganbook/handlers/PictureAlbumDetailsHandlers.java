package com.ganbook.handlers;

import android.util.Log;
import android.view.View;

import com.ganbook.interfaces.AlbumDetailsInterface;
import com.ganbook.models.PictureAnswer;

/**
 * Created by dmytro_vodnik on 6/17/16.
 * working on ganbook1 project
 */
public class PictureAlbumDetailsHandlers {


    private static final String TAG = PictureAlbumDetailsHandlers.class.getName();
    private final PictureAnswer pictureAnswer;
    private final AlbumDetailsInterface albumDetailsInterface;
    private final int itemPosition;

    public PictureAlbumDetailsHandlers(PictureAnswer pictureAnswer,
                                       AlbumDetailsInterface albumDetailsInterface,
                                       int itemPos) {

        this.pictureAnswer = pictureAnswer;
        this.albumDetailsInterface = albumDetailsInterface;
        this.itemPosition = itemPos;
    }

    public void onRetryButtonClicked(View view) {

        Log.d(TAG, "onRetryButtonClicked: ");

        albumDetailsInterface.initMaxUploadCount();

        albumDetailsInterface.retryUploadFile(itemPosition, pictureAnswer);
    }
}

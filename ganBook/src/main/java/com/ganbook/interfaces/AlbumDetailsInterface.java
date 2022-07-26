package com.ganbook.interfaces;

import com.ganbook.models.PictureAnswer;

import java.util.List;

/**
 * Created by dmytro_vodnik on 6/13/16.
 * working on ganbook1 project
 */
public interface AlbumDetailsInterface {

    void uploadFileToServer(int pos, PictureAnswer file);
    void retryUploadFile(int pos, PictureAnswer file);
    boolean getSelectionState();
    List<PictureAnswer> getPicturesToDelete();
    List<String> getAllPicturesUrls();

    void initMaxUploadCount();

    void retryAll();

    void hideProgress();

    void downloadPictures(List<String> pics);
}

package com.ganbook.models.factories;

import com.ganbook.models.PictureAnswer;

import org.apache.commons.io.FilenameUtils;

import java.util.Date;

/**
 * Created by dmytro_vodnik on 6/14/16.
 * working on ganbook1 project
 */
public class PictureAnswerFactory {

    /**
     * Creates {@link PictureAnswer} image object
     * @param albumId - albumid of future image at server
     * @param duration - duration of video (if video)
     * @param filePath - path of file in phone memory
     * @return {@link PictureAnswer} object
     */
    public static PictureAnswer getPictureAnswer(String albumId, String duration, String filePath) {

        PictureAnswer pictureAnswer = new PictureAnswer();
        pictureAnswer.setPictureName(duration.equals("") ?
                System.currentTimeMillis() + "." + "jpg" :
                "VID_"+System.currentTimeMillis() + "." + FilenameUtils.getExtension(filePath));
        pictureAnswer.setPictureDate(new Date());
        pictureAnswer.setAlbumId(albumId);
        pictureAnswer.setFavorite(false);
        pictureAnswer.setSelected(false);
        pictureAnswer.setStatus(0);
        pictureAnswer.setLocaFilePath(filePath);

        if (!duration.equals(""))
            pictureAnswer.setVideoDuration(duration);

        //for unique filenames
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return pictureAnswer;
    }

    public static PictureAnswer getPictureAnswer(String albumId, String duration, String filePath, String id) {

        PictureAnswer pictureAnswer = new PictureAnswer();
        pictureAnswer.setPictureName(duration.equals("") ?
                System.currentTimeMillis() + "." + "jpg" :
                "VID_"+System.currentTimeMillis() + "." + FilenameUtils.getExtension(filePath));
        pictureAnswer.setPictureDate(new Date());
        pictureAnswer.setAlbumId(albumId);
        pictureAnswer.setFavorite(false);
        pictureAnswer.setSelected(false);
        pictureAnswer.setStatus(0);
        pictureAnswer.setLocaFilePath(filePath);
        pictureAnswer.setResid(id);

        if (!duration.equals(""))
            pictureAnswer.setVideoDuration(duration);

        //for unique filenames
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return pictureAnswer;
    }
}

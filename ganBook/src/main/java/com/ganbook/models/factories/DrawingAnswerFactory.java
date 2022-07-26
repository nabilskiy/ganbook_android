package com.ganbook.models.factories;

import com.ganbook.models.DrawingAnswer;

public class DrawingAnswerFactory {

    public static DrawingAnswer getDrawingAnswer(String albumId, String filePath) {

        DrawingAnswer drawingAnswer = new DrawingAnswer();
        drawingAnswer.setDrawingName(System.currentTimeMillis() + "." + "jpg");
        drawingAnswer.setDrawingAlbumId(albumId);
        drawingAnswer.setLocaFilePath(filePath);

        //for unique filenames
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return drawingAnswer;
    }
}

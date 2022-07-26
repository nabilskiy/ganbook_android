package com.ganbook.models;

import com.google.gson.annotations.SerializedName;

public class CreateDrawingAnswer {

    @SerializedName("drawing_album_id")
    String drawingAlbumId;


    public String getDrawingAlbumId() {
        return drawingAlbumId;
    }

    public void setDrawingAlbumId(String drawingAlbumId) {
        this.drawingAlbumId = drawingAlbumId;
    }

    @Override
    public String toString() {
        return "CreateAlbumAnswer{" +
                "drawingAlbumId='" + drawingAlbumId + '\'' +
                '}';
    }
}

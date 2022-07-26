package com.ganbook.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by dmytro_vodnik on 6/8/16.
 * working on ganbook1 project
 */
public class CreateAlbumAnswer {

    @SerializedName("album_id")
    String albumId;

    public String getAlbumId() {
        return albumId;
    }

    public void setAlbumId(String albumId) {
        this.albumId = albumId;
    }

    @Override
    public String toString() {
        return "CreateAlbumAnswer{" +
                "albumId='" + albumId + '\'' +
                '}';
    }
}

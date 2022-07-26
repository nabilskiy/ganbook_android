package com.ganbook.models.events;

import com.ganbook.models.AlbumsAnswer;

/**
 * Created by dmytro_vodnik on 6/8/16.
 * working on ganbook1 project
 */
public class CreateAlbumEvent {

    public final AlbumsAnswer albumsAnswer;

    public CreateAlbumEvent(AlbumsAnswer albumsAnswer) {

        this.albumsAnswer = albumsAnswer;
    }

    @Override
    public String toString() {
        return "CreateAlbumEvent{" +
                "albumsAnswer=" + albumsAnswer +
                '}';
    }
}

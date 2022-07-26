package com.ganbook.models.events;

import com.ganbook.models.AlbumsAnswer;

/**
 * Created by Noa on 15/06/2016.
 */
public class RenameAlbumEvent {

    public final AlbumsAnswer albumsAnswer;
    public final int pos;

    public RenameAlbumEvent(AlbumsAnswer albumsAnswer, int pos) {
        this.albumsAnswer = albumsAnswer;
        this.pos = pos;
    }

    @Override
    public String toString() {
        return "RenameAlbumEvent{" +
                "albumsAnswer=" + albumsAnswer +
                ", pos='" + pos +
                '}';
    }
}

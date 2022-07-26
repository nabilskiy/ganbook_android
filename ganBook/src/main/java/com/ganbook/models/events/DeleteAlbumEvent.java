package com.ganbook.models.events;

import com.ganbook.models.AlbumsAnswer;

/**
 * Created by Noa on 15/06/2016.
 */
public class DeleteAlbumEvent {

    public final int pos;

    public DeleteAlbumEvent(int pos) {
        this.pos = pos;
    }

    @Override
    public String toString() {
        return "DeleteAlbumEvent{" +
                "pos=" + pos +
                '}';
    }
}

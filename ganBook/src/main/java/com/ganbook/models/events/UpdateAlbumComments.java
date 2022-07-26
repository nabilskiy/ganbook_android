package com.ganbook.models.events;

/**
 * Created by dmytro_vodnik on 8/8/16.
 * working on ganbook1 project
 */
public class UpdateAlbumComments {
    public final int pos;
    public int size;

    public UpdateAlbumComments(int pos, int size) {
        this.pos = pos;
        this.size =size;
    }

    @Override
    public String toString() {
        return "UpdateAlbumComments{" +
                ", pos=" + pos +
                ", size=" + size +
                '}';
    }
}

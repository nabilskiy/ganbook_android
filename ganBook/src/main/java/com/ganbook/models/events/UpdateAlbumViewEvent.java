package com.ganbook.models.events;

/**
 * Created by dmytro_vodnik on 7/4/16.
 * working on ganbook1 project
 */
public class UpdateAlbumViewEvent {

    public int seen_photos;

    public UpdateAlbumViewEvent(int seen_photos) {
        this.seen_photos = seen_photos;
    }

    @Override
    public String toString() {
        return "UpdateAlbumViewEvent{" +
                "seen_photos=" + seen_photos +
                '}';
    }
}

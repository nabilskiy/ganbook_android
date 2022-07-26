package com.ganbook.models.events;

/**
 * Created by dmytro_vodnik on 6/20/16.
 * working on ganbook1 project
 */
public class DeletePicturesEvent {

    public DeletePicturesEvent(boolean deleted) {
        this.deleted = deleted;
    }

    public boolean deleted;

    @Override
    public String toString() {
        return "DeletePicturesEvent{" +
                "deleted=" + deleted +
                '}';
    }
}

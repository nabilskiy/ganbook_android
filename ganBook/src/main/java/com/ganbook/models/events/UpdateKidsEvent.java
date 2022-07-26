package com.ganbook.models.events;

/**
 * Created by dmytro_vodnik on 8/30/16.
 * working on ganbook1 project
 */
public class UpdateKidsEvent {
    private boolean updated;

    public UpdateKidsEvent(boolean updated) {
        this.updated = updated;
    }

    public boolean getUpdated() {
        return this.updated;
    }
}

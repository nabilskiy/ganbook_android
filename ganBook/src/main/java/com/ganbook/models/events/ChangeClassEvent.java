package com.ganbook.models.events;

/**
 * Created by dmytro_vodnik on 7/18/16.
 * working on ganbook1 project
 */
public class ChangeClassEvent {
    public final boolean changed;

    public ChangeClassEvent(boolean changed, String kidId) {
        this.changed = changed;
        this.kidId = kidId;
    }

    public String kidId;

    public boolean isChanged() {
        return changed;
    }

    @Override
    public String toString() {
        return "ChangeClassEvent{" +
                "changed=" + changed +
                '}';
    }
}

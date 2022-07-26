package com.ganbook.models.events;

/**
 * Created by dmytro_vodnik on 6/8/16.
 * working on ganbook1 project
 */
public class SelectDrawerItemEvent {

    public final boolean isSet;
    public final String title;

    public SelectDrawerItemEvent(boolean isSet, String title) {
        this.isSet = isSet;
        this.title = title;
    }

    @Override
    public String toString() {
        return "SelectDrawerItemEvent{" +
                "isSet=" + isSet +
                ", title='" + title + '\'' +
                '}';
    }
}

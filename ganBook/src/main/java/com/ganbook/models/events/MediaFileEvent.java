package com.ganbook.models.events;

/**
 * Created by dmytro_vodnik on 6/21/16.
 * working on ganbook1 project
 */
public class MediaFileEvent {

    public MediaFileEvent(String filePath, String time, String id) {
        this.filePath = filePath;
        this.time = time;
        this.id = id;
    }

    public String filePath;
    public String time;
    public String id;

    @Override
    public String toString() {
        return "MediaFileEvent{" +
                "filePath='" + filePath + '\'' +
                ", time='" + time + '\'' +
                ", id='" + id + '\'' +
                '}';
    }
}

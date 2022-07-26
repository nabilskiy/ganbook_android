package com.ganbook.models.events;

public class NoInternetEvent {

    public boolean isInternetAvailable;

    public NoInternetEvent(boolean isInternetAvailable) {
        this.isInternetAvailable = isInternetAvailable;
    }


    @Override
    public String toString() {
        return "NoInternetEvent{" +
                "filePath='" + isInternetAvailable + '\'' +
                '}';
    }
}

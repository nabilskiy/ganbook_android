package com.ganbook.models.events;

public class ProgressEvent {

    private int currentByte, totalBytes;

    public ProgressEvent(int currentByte, int totalBytes) {
        this.currentByte = currentByte;
        this.totalBytes = totalBytes;
    }

    public int getCurrentByte() {
        return currentByte;
    }

    public void setCurrentByte(int currentByte) {
        this.currentByte = currentByte;
    }

    public int getTotalBytes() {
        return totalBytes;
    }

    public void setTotalBytes(int totalBytes) {
        this.totalBytes = totalBytes;
    }
}

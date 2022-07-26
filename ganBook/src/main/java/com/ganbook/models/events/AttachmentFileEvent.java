package com.ganbook.models.events;

public class AttachmentFileEvent {

    private boolean isUploaded;
    private String fileUrl;

    public AttachmentFileEvent(boolean isUploaded, String fileUrl) {
        this.isUploaded = isUploaded;
        this.fileUrl = fileUrl;
    }

    public boolean isUploaded() {
        return isUploaded;
    }

    public void setUploaded(boolean uploaded) {
        isUploaded = uploaded;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

}

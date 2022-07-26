package com.ganbook.models;

import com.google.gson.annotations.SerializedName;

public class MeetingAttendeesModel {

    @SerializedName("id")
    private String id;

    @SerializedName("meeting_id")
    private String meetingId;

    @SerializedName("parent_id")
    private String parentId;

    @SerializedName("parent_name")
    private String parentName;

    @SerializedName("meeting_slot")
    private String meetingSlot;

    @SerializedName("active")
    private String meetingActive;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMeetingId() {
        return meetingId;
    }

    public void setMeetingId(String meetingId) {
        this.meetingId = meetingId;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public String getMeetingSlot() {
        return meetingSlot;
    }

    public void setMeetingSlot(String meetingSlot) {
        this.meetingSlot = meetingSlot;
    }

    public String getMeetingActive() {
        return meetingActive;
    }

    public void setMeetingActive(String meetingActive) {
        this.meetingActive = meetingActive;
    }
}

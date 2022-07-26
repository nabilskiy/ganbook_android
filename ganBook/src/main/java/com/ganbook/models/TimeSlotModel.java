package com.ganbook.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class TimeSlotModel implements Parcelable {

    @SerializedName("id")
    private String timeSlotId;

    @SerializedName("meeting_id")
    private String timeSlotMeetingId;

    @SerializedName("parent_id")
    private String parentId;

    @SerializedName("parent_name")
    private String parentName;

    @SerializedName("meeting_slot")
    private String meetingSlot;

    @SerializedName("active")
    private String active;

    protected TimeSlotModel(Parcel in) {
        timeSlotId = in.readString();
        timeSlotMeetingId = in.readString();
        parentId = in.readString();
        parentName = in.readString();
        meetingSlot = in.readString();
        active = in.readString();
    }

    public static final Creator<TimeSlotModel> CREATOR = new Creator<TimeSlotModel>() {
        @Override
        public TimeSlotModel createFromParcel(Parcel in) {
            return new TimeSlotModel(in);
        }

        @Override
        public TimeSlotModel[] newArray(int size) {
            return new TimeSlotModel[size];
        }
    };

    public String getTimeSlotId() {
        return timeSlotId;
    }

    public void setTimeSlotId(String timeSlotId) {
        this.timeSlotId = timeSlotId;
    }

    public String getTimeSlotMeetingId() {
        return timeSlotMeetingId;
    }

    public void setTimeSlotMeetingId(String timeSlotMeetingId) {
        this.timeSlotMeetingId = timeSlotMeetingId;
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

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(timeSlotId);
        dest.writeString(timeSlotMeetingId);
        dest.writeString(parentId);
        dest.writeString(parentName);
        dest.writeString(meetingSlot);
        dest.writeString(active);
    }

    @Override
    public String toString() {
        return "TimeSlotModel{" +
                "timeSlotId='" + timeSlotId + '\'' +
                ", timeSlotMeeting=" + meetingSlot +
                '}';
    }
}

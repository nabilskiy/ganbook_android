package com.ganbook.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MeetingEventModel implements Parcelable {

    @SerializedName("id")
    private String meetingId;

    @SerializedName("title")
    private String meetingTitle;

    @SerializedName("meeting_start_date")
    private String meetingStartDate;

    @SerializedName("meeting_end_date")
    private String meetingEndDate;

    @SerializedName("class_id")
    private String meetingClassId;

    @SerializedName("comments")
    private String meetingComment;

    @SerializedName("meeting_duration")
    private String meetingDuration;

    @SerializedName("active")
    private String meetingActive;

    @SerializedName("time_slots")
    private List<TimeSlotModel> timeSlotModel;

    protected MeetingEventModel(Parcel in) {
        meetingId = in.readString();
        meetingTitle = in.readString();
        meetingStartDate = in.readString();
        meetingEndDate = in.readString();
        meetingClassId = in.readString();
        meetingComment = in.readString();
        meetingDuration = in.readString();
        meetingActive = in.readString();

        if (timeSlotModel != null) {
            timeSlotModel = in.createTypedArrayList(TimeSlotModel.CREATOR);
        }
    }

    public static final Creator<MeetingEventModel> CREATOR = new Creator<MeetingEventModel>() {
        @Override
        public MeetingEventModel createFromParcel(Parcel in) {
            return new MeetingEventModel(in);
        }

        @Override
        public MeetingEventModel[] newArray(int size) {
            return new MeetingEventModel[size];
        }
    };

    public String getMeetingId() {
        return meetingId;
    }

    public void setMeetingId(String meetingId) {
        this.meetingId = meetingId;
    }

    public String getMeetingTitle() {
        return meetingTitle;
    }

    public void setMeetingTitle(String meetingTitle) {
        this.meetingTitle = meetingTitle;
    }

    public String getMeetingStartDate() {
        return meetingStartDate;
    }

    public void setMeetingStartDate(String meetingStartDate) {
        this.meetingStartDate = meetingStartDate;
    }

    public String getMeetingEndDate() {
        return meetingEndDate;
    }

    public void setMeetingEndDate(String meetingEndDate) {
        this.meetingEndDate = meetingEndDate;
    }

    public String getMeetingClassId() {
        return meetingClassId;
    }

    public void setMeetingClassId(String meetingClassId) {
        this.meetingClassId = meetingClassId;
    }

    public String getMeetingComment() {
        return meetingComment;
    }

    public void setMeetingComment(String meetingComment) {
        this.meetingComment = meetingComment;
    }

    public String getMeetingDuration() {
        return meetingDuration;
    }

    public void setMeetingDuration(String meetingDuration) {
        this.meetingDuration = meetingDuration;
    }

    public String getMeetingActive() {
        return meetingActive;
    }

    public void setMeetingActive(String meetingActive) {
        this.meetingActive = meetingActive;
    }

    public List<TimeSlotModel> getTimeSlotModel() {
        return timeSlotModel;
    }

    public void setTimeSlotModel(List<TimeSlotModel> timeSlotModel) {
        this.timeSlotModel = timeSlotModel;
    }


    @Override
    public String toString() {
        return "MeetingEvent{" +
                "meetingId=" + meetingId +
                "meetingTitle=" + meetingTitle +
                "meetingStartDate=" + meetingStartDate +
                "meetingEndDate=" + meetingEndDate +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(meetingId);
        dest.writeString(meetingTitle);
        dest.writeString(meetingStartDate);
        dest.writeString(meetingEndDate);
        dest.writeString(meetingClassId);
        dest.writeString(meetingComment);
        dest.writeString(meetingDuration);
        dest.writeString(meetingActive);

        if (timeSlotModel != null) {
            dest.writeTypedList(timeSlotModel);

        }
    }


}

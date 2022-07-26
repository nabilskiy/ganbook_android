package com.ganbook.models.answers;

import android.os.Parcel;
import android.os.Parcelable;

import com.ganbook.models.EventModel;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by dmytro_vodnik on 7/19/16.
 * working on ganbook1 project
 */
public class EventsAnswer implements Parcelable {

    @SerializedName("event_date")
    String eventDate;
    @SerializedName("events")
    List<EventModel> eventModelList;

    protected EventsAnswer(Parcel in) {
        eventDate = in.readString();
        eventModelList = in.createTypedArrayList(EventModel.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(eventDate);
        dest.writeTypedList(eventModelList);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<EventsAnswer> CREATOR = new Creator<EventsAnswer>() {
        @Override
        public EventsAnswer createFromParcel(Parcel in) {
            return new EventsAnswer(in);
        }

        @Override
        public EventsAnswer[] newArray(int size) {
            return new EventsAnswer[size];
        }
    };

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public List<EventModel> getEventModelList() {
        return eventModelList;
    }

    public void setEventModelList(List<EventModel> eventModelList) {
        this.eventModelList = eventModelList;
    }

    @Override
    public String toString() {
        return "EventsAnswer{" +
                "eventDate='" + eventDate + '\'' +
                ", eventModelList=" + eventModelList +
                '}';
    }
}

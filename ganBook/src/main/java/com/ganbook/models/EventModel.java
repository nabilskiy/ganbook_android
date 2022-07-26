package com.ganbook.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.ganbook.app.MyApp;
import com.google.gson.annotations.SerializedName;
import com.project.ganim.R;

import java.util.List;

/**
 * Created by dmytro_vodnik on 7/19/16.
 * working on ganbook1 project
 */
public class EventModel implements Parcelable {

    @SerializedName("event_id")
    public String event_id;
    @SerializedName("kid_pic")
    public String kid_pic;
    @SerializedName("class_id")
    public String class_id;
    @SerializedName("all_day")
    public String all_day;
    @SerializedName("day_off")
    public String day_off;
    @SerializedName("event_title")
    public String event_title;
    @SerializedName("event_type")
    public String event_type;
    @SerializedName("all_kids")
    public String all_kids;
    @SerializedName("event_start_date")
    public String event_start_date;
    @SerializedName("event_end_date")
    public String event_end_date;
    @SerializedName("active")
    public String active;
    @SerializedName("event_comments")
    public String event_comments;
    @SerializedName("kids")
    public List<KidModel> kids;

    protected EventModel(Parcel in) {
        event_id = in.readString();
        kid_pic = in.readString();
        class_id = in.readString();
        all_day = in.readString();
        day_off = in.readString();
        event_title = in.readString();
        event_type = in.readString();
        all_kids = in.readString();
        event_start_date = in.readString();
        event_end_date = in.readString();
        active = in.readString();
        event_comments = in.readString();
        kids = in.createTypedArrayList(KidModel.CREATOR);
    }

    public static final Creator<EventModel> CREATOR = new Creator<EventModel>() {
        @Override
        public EventModel createFromParcel(Parcel in) {
            return new EventModel(in);
        }

        @Override
        public EventModel[] newArray(int size) {
            return new EventModel[size];
        }
    };

    public String [] getEventKidsNames() {

        String[] names = null;
        if("1".equals(all_kids))
        {
            names = new String[1];
            names[0] = MyApp.context.getResources().getString(R.string.all_kids);
        }
        else {
            if (kids != null) {
                names = new String[kids.size()];
                int ind = 0;
                for (KidModel kid : kids) {
                    names[ind] = kid.getKid_name();
                    ind++;
                }
            }
        }
        return names;
    }

    public String[] getEventKidsIds() {

        String[] ids = null;
        if(kids != null) {
            ids = new String[kids.size()];
            int ind = 0;
            for (KidModel kid : kids) {
                ids[ind] = kid.getKid_id();
                ind++;
            }
        }

        return ids;
    }

    @Override
    public String toString() {
        return "EventModel{" +
                "event_id='" + event_id + '\'' +
                ", kid_pic='" + kid_pic + '\'' +
                ", class_id='" + class_id + '\'' +
                ", all_day='" + all_day + '\'' +
                ", day_off='" + day_off + '\'' +
                ", event_title='" + event_title + '\'' +
                ", event_type='" + event_type + '\'' +
                ", all_kids='" + all_kids + '\'' +
                ", event_start_date='" + event_start_date + '\'' +
                ", event_end_date='" + event_end_date + '\'' +
                ", active='" + active + '\'' +
                ", event_comments='" + event_comments + '\'' +
                ", kids=" + kids +
                '}';
    }

    public String getEvent_id() {
        return event_id;
    }

    public void setEvent_id(String event_id) {
        this.event_id = event_id;
    }

    public String getKid_pic() {
        return kid_pic;
    }

    public void setKid_pic(String kid_pic) {
        this.kid_pic = kid_pic;
    }

    public String getClass_id() {
        return class_id;
    }

    public void setClass_id(String class_id) {
        this.class_id = class_id;
    }

    public String getAll_day() {
        return all_day;
    }

    public void setAll_day(String all_day) {
        this.all_day = all_day;
    }

    public String getDay_off() {
        return day_off;
    }

    public void setDay_off(String day_off) {
        this.day_off = day_off;
    }

    public String getEvent_title() {
        return event_title;
    }

    public void setEvent_title(String event_title) {
        this.event_title = event_title;
    }

    public String getEvent_type() {
        return event_type;
    }

    public void setEvent_type(String event_type) {
        this.event_type = event_type;
    }

    public String getAll_kids() {
        return all_kids;
    }

    public void setAll_kids(String all_kids) {
        this.all_kids = all_kids;
    }

    public String getEvent_start_date() {
        return event_start_date;
    }

    public void setEvent_start_date(String event_start_date) {
        this.event_start_date = event_start_date;
    }

    public String getEvent_end_date() {
        return event_end_date;
    }

    public void setEvent_end_date(String event_end_date) {
        this.event_end_date = event_end_date;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public String getEvent_comments() {
        return event_comments;
    }

    public void setEvent_comments(String event_comments) {
        this.event_comments = event_comments;
    }

    public List<KidModel> getKids() {
        return kids;
    }

    public void setKids(List<KidModel> kids) {
        this.kids = kids;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(event_id);
        dest.writeString(kid_pic);
        dest.writeString(class_id);
        dest.writeString(all_day);
        dest.writeString(day_off);
        dest.writeString(event_title);
        dest.writeString(event_type);
        dest.writeString(all_kids);
        dest.writeString(event_start_date);
        dest.writeString(event_end_date);
        dest.writeString(active);
        dest.writeString(event_comments);
        dest.writeTypedList(kids);
    }


//    public void setKidNames(HashMap<String,String> kids_names) {
//
//        kids = new KidM[kids_names.keySet().size()];
//        int ind = 0;
//        for (String id : kids_names.keySet()) {
//            kids[ind] = new Kid();
//            kids[ind].kid_id = id;
//            kids[ind].kid_name = kids_names.get(id);
//
//            ind++;
//        }
//    }
}

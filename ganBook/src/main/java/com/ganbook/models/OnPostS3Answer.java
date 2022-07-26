package com.ganbook.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by dmytro_vodnik on 6/20/16.
 * working on ganbook1 project
 */
public class OnPostS3Answer implements Parcelable {

    protected OnPostS3Answer(Parcel in) {
        success = in.readByte() != 0;
        pictureId = in.readString();
    }

    public static final Creator<OnPostS3Answer> CREATOR = new Creator<OnPostS3Answer>() {
        @Override
        public OnPostS3Answer createFromParcel(Parcel in) {
            return new OnPostS3Answer(in);
        }

        @Override
        public OnPostS3Answer[] newArray(int size) {
            return new OnPostS3Answer[size];
        }
    };

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getPictureId() {
        return pictureId;
    }

    public void setPictureId(String pictureId) {
        this.pictureId = pictureId;
    }

    @SerializedName("success")
    boolean success;

    @SerializedName("picture_id")
    String pictureId;

    public OnPostS3Answer(boolean success) {
        this.success = success;
    }


    @Override
    public String toString() {
        return "OnPostS3Answer{" +
                "success=" + success +
                ", pictureId='" + pictureId + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (success ? 1 : 0));
        dest.writeString(pictureId);
    }
}

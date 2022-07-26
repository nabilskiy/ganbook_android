package com.ganbook.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class AlbumViewerModel implements Parcelable {

    @SerializedName("user_id")
    String parentId;

    @SerializedName("first_name")
    String parentFirstName;

    @SerializedName("last_name")
    String parentLastName;

    public AlbumViewerModel() {}

    public AlbumViewerModel(Parcel in) {
        parentId = in.readString();
        parentFirstName = in.readString();
        parentLastName = in.readString();
    }


    public static final Creator<AlbumViewerModel> CREATOR = new Creator<AlbumViewerModel>() {
        @Override
        public AlbumViewerModel createFromParcel(Parcel in) {
            return new AlbumViewerModel(in);
        }

        @Override
        public AlbumViewerModel[] newArray(int size) {
            return new AlbumViewerModel[size];
        }
    };

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getParentFirstName() {
        return parentFirstName;
    }

    public void setParentFirstName(String parentFirstName) {
        this.parentFirstName = parentFirstName;
    }

    public String getParentLastName() {
        return parentLastName;
    }

    public void setParentLastName(String parentLastName) {
        this.parentLastName = parentLastName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(parentId);
        dest.writeString(parentFirstName);
        dest.writeString(parentLastName);
    }
}

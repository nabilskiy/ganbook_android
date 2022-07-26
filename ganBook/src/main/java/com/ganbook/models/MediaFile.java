package com.ganbook.models;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by dmytro_vodnik on 6/14/16.
 * working on ganbook1 project
 */
public class MediaFile extends BaseObservable implements Parcelable {

    public MediaFile(String filePath) {
        this.filePath = filePath;
    }

    protected MediaFile(Parcel in) {
        filePath = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(filePath);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MediaFile> CREATOR = new Creator<MediaFile>() {
        @Override
        public MediaFile createFromParcel(Parcel in) {
            return new MediaFile(in);
        }

        @Override
        public MediaFile[] newArray(int size) {
            return new MediaFile[size];
        }
    };

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    String filePath;

    public boolean isHighlight() {
        return highlight;
    }

    public void setHighlight(boolean highlight) {
        this.highlight = highlight;
    }

    @Bindable
    boolean highlight = false;

    @Override
    public String toString() {
        return "MediaFile{" +
                "filePath='" + filePath + '\'' +
                '}';
    }
}

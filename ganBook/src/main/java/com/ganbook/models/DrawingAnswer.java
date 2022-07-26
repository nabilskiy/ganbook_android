package com.ganbook.models;

import androidx.databinding.BaseObservable;
import androidx.databinding.library.baseAdapters.BR;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;

public class DrawingAnswer extends BaseObservable implements Parcelable {

    public DrawingAnswer() {}

    @SerializedName("drawing_id")
    String drawingId;

    @SerializedName("drawing_name")
    String drawingName;

    @SerializedName("kid_album_id")
    String drawingAlbumId;

    @SerializedName("drawing_description")
    String drawingDescription;

    @SerializedName("audio_name")
    String drawingAudio;

    @DatabaseField
    String locaFilePath;

    private boolean isSelected;

    protected DrawingAnswer(Parcel in) {
        drawingId = in.readString();
        drawingName = in.readString();
        locaFilePath = in.readString();
        drawingDescription = in.readString();
        drawingAudio = in.readString();
    }

    public static final Creator<DrawingAnswer> CREATOR = new Creator<DrawingAnswer>() {
        @Override
        public DrawingAnswer createFromParcel(Parcel in) {
            return new DrawingAnswer(in);
        }

        @Override
        public DrawingAnswer[] newArray(int size) {
            return new DrawingAnswer[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(drawingId);
        dest.writeString(drawingName);
        dest.writeString(locaFilePath);
        dest.writeString(drawingDescription);
        dest.writeString(drawingAudio);
    }


    public String getDrawingId() {
        return drawingId;
    }

    public void setDrawingId(String drawingId) {
        this.drawingId = drawingId;
    }

    public String getDrawingName() {
        return drawingName;
    }

    public void setDrawingName(String drawingName) {
        this.drawingName = drawingName;
    }

    public String getLocaFilePath() {
        return locaFilePath;
    }

    public void setLocaFilePath(String locaFilePath) {
        this.locaFilePath = locaFilePath;
    }

    public String getDrawingDescription() {
        return drawingDescription;
    }

    public void setDrawingDescription(String drawingDescription) {
        this.drawingDescription = drawingDescription;
    }

    public String getDrawingAlbumId() {
        return drawingAlbumId;
    }

    public void setDrawingAlbumId(String drawingAlbumId) {
        this.drawingAlbumId = drawingAlbumId;
    }

    public void setDrawingAudio(String drawingAudio) {
        this.drawingAudio = drawingAudio;
    }

    public String getDrawingAudio() {
        return this.drawingAudio;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
        notifyPropertyChanged(BR.isSelected);
    }

    @Override
    public String toString() {
        return "DrawingAnswer{" +
                ", drawingId='" + drawingId + '\'' +
                ", drawingName='" + drawingName + '\'' +
                ", drawingDescription='" + drawingDescription + '\'' +
                ", drawingAudio='" + drawingAudio + '\'' +
                '}';
    }


}

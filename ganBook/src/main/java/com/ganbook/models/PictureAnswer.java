package com.ganbook.models;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.library.baseAdapters.BR;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

/**
 * Created by dmytro_vodnik on 6/12/16.
 * working on ganbook1 project
 */
@DatabaseTable
public class PictureAnswer extends BaseObservable implements Parcelable {

    public static final String ALBUM_ID_FIELD = "albumId";
    @DatabaseField(generatedId = true, unique = true)
    int id;

    @SerializedName("picture_id")
    @DatabaseField
    String pictureId;

    @SerializedName("picture_name")
    @DatabaseField
    String pictureName;

    @SerializedName("picture_date")
    @DatabaseField
    Date pictureDate;

    @SerializedName("album_id")
    @DatabaseField
    String albumId;

    @SerializedName("favorite")
    @Bindable
    @DatabaseField
    boolean favorite;

    @SerializedName("picture_active")
    @Bindable
    @DatabaseField
    /**
     * 0 deleted
     * 1 active
     * 2 is video
     */
    int pictureActive;

    @SerializedName("video_duration")
    @DatabaseField
    String videoDuration;

    @SerializedName("pic_description")
    @DatabaseField
    String pictureDescription;

    @SerializedName("create_date")
    @DatabaseField
    float createDate = System.currentTimeMillis();

    @Bindable
    boolean isSelected;

    /**
     * 0 waiting
     * 1 successful
     * 2 failed
     */
    @Bindable
    @DatabaseField
    int status = 1;

    @DatabaseField
    String locaFilePath;

    @DatabaseField
    private String resid;

    protected PictureAnswer(Parcel in) {
        id = in.readInt();
        pictureId = in.readString();
        pictureName = in.readString();
        albumId = in.readString();
        favorite = in.readByte() != 0;
        pictureActive = in.readInt();
        videoDuration = in.readString();
        createDate = in.readFloat();
        isSelected = in.readByte() != 0;
        status = in.readInt();
        locaFilePath = in.readString();
        resid = in.readString();
        progress = in.readInt();
        pictureDescription = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(pictureId);
        dest.writeString(pictureName);
        dest.writeString(albumId);
        dest.writeByte((byte) (favorite ? 1 : 0));
        dest.writeInt(pictureActive);
        dest.writeString(videoDuration);
        dest.writeFloat(createDate);
        dest.writeByte((byte) (isSelected ? 1 : 0));
        dest.writeInt(status);
        dest.writeString(locaFilePath);
        dest.writeString(resid);
        dest.writeInt(progress);
        dest.writeString(pictureDescription);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PictureAnswer> CREATOR = new Creator<PictureAnswer>() {
        @Override
        public PictureAnswer createFromParcel(Parcel in) {
            return new PictureAnswer(in);
        }

        @Override
        public PictureAnswer[] newArray(int size) {
            return new PictureAnswer[size];
        }
    };

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
        notifyPropertyChanged(BR.progress);
    }

    @Bindable
    int progress = 0;

    //required empty constructor by OrmLite
    public PictureAnswer() {}

    public String getLocaFilePath() {
        return locaFilePath;
    }

    public void setLocaFilePath(String locaFilePath) {
        this.locaFilePath = locaFilePath;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
        notifyPropertyChanged(BR.isSelected);
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
        notifyPropertyChanged(BR.status);
    }

    public float getCreateDate() {
        return createDate;
    }

    public void setCreateDate(long createDate) {
        this.createDate = createDate;
    }

    public String getVideoDuration() {
        return videoDuration;
    }

    public void setVideoDuration(String videoDuration) {
        this.videoDuration = videoDuration;
    }

    public int getPictureActive() {
        return pictureActive;
    }

    public void setPictureActive(int pictureActuve) {
        this.pictureActive = pictureActuve;
        notifyPropertyChanged(BR.pictureActive);
    }

    public Boolean getFavorite() {
        return favorite;
    }

    public void setFavorite(Boolean favorite) {
        this.favorite = favorite;
        notifyPropertyChanged(BR.favorite);
    }

    public String getAlbumId() {
        return albumId;
    }

    public void setAlbumId(String albumId) {
        this.albumId = albumId;
    }

    public Date getPictureDate() {
        return pictureDate;
    }

    public void setPictureDate(Date pictureDate) {
        this.pictureDate = pictureDate;
    }

    public String getPictureName() {
        return pictureName;
    }

    public void setPictureName(String pictureName) {
        this.pictureName = pictureName;
    }

    public String getPictureId() {
        return pictureId;
    }

    public void setPictureId(String pictureId) {
        this.pictureId = pictureId;
    }

    public void setResid(String resid) {
        this.resid = resid;
    }

    public String getResid() {
        return resid;
    }

    public String getPictureDescription() {
        return pictureDescription;
    }

    public void setPictureDescription(String pictureDescription) {
        this.pictureDescription = pictureDescription;
    }

    @Override
    public String toString() {
        return "PictureAnswer{" +
                "id=" + id +
                ", pictureId='" + pictureId + '\'' +
                ", pictureName='" + pictureName + '\'' +
                ", pictureDate=" + pictureDate +
                ", albumId='" + albumId + '\'' +
                ", favorite=" + favorite +
                ", pictureActive=" + pictureActive +
                ", videoDuration='" + videoDuration + '\'' +
                ", createDate=" + createDate +
                ", isSelected=" + isSelected +
                ", status=" + status +
                ", locaFilePath='" + locaFilePath + '\'' +
                ", resid='" + resid + '\'' +
                ", progress=" + progress +
                ", description= " + pictureDescription +
                '}';
    }

    @Override
    public int hashCode() {

        final int prime = 31;
        int result = 1;

        result = prime * result + ((pictureName == null) ? 0 : pictureName.hashCode());
        result = prime * result + id;

        return result;
    }

    @Override
    public boolean equals(Object v) {
        boolean retVal = false;

        if (v instanceof PictureAnswer) {

            PictureAnswer ptr = (PictureAnswer) v;
            retVal = ptr.id == this.id;
        }

        return retVal;
    }
}

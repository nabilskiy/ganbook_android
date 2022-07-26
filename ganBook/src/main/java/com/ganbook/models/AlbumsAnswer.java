package com.ganbook.models;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import android.os.Parcel;
import android.os.Parcelable;
import com.ganbook.interfaces.RecyclerViewItem;
import com.google.gson.annotations.SerializedName;
import com.project.ganim.BR;

import java.util.Date;

/**
 * Created by dmytro_vodnik on 6/6/16.
 * working on ganbook1 project
 */
public class AlbumsAnswer extends BaseObservable implements Parcelable, RecyclerViewItem {

    @SerializedName("album_id")
    String albumId;

    @SerializedName("album_name")
    String albumName;

    @SerializedName("album_description")
    String albumDescription;

    @SerializedName("picture")
    @Bindable
    String picPath;

    @SerializedName("album_date")
    Date albumDate;

    @SerializedName("pic_count")
    int picCount;

    @SerializedName("user_album_like")
    boolean liked;

    @SerializedName("videos_count")
    int videosCount;

    @SerializedName("album_views")
    int albumViews;

    @SerializedName("album_comments")
    int commentsCount;

    @SerializedName("album_likes")
    @Bindable
    int likesCount;

    @SerializedName("views")
    int views;

    @SerializedName("unseen_photos")
    @Bindable
    int unseenPhotos;

    @SerializedName("first_picture")
    @Bindable
    String firstPicture;

    private boolean isCommercial;
    private Commercial commercial;

    public Commercial getCommercial() {
        return commercial;
    }

    public void setCommercial(Commercial commercial) {
        this.commercial = commercial;
    }

    public boolean getIsCommercial() {
        return isCommercial;
    }

    public void setIsCommercial(boolean commercial) {
        isCommercial = commercial;
    }

    public String getGanId() {
        return ganId;
    }

    public void setGanId(String ganId) {
        this.ganId = ganId;
    }

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    String ganId;
    String classId;

    public AlbumsAnswer(){}

    public AlbumsAnswer(String albumId, String albumName, String albumDescription, String picPath, Date albumDate, int picCount,
                        int videosCount, int albumViews, int commentsCount, int likesCount, int views, int unseenPhotos) {
        this.albumId = albumId;
        this.albumName = albumName;
        this.picPath = picPath;
        this.albumDate = albumDate;
        this.picCount = picCount;
        this.videosCount = videosCount;
        this.albumViews = albumViews;
        this.commentsCount = commentsCount;
        this.likesCount = likesCount;
        this.views = views;
        this.unseenPhotos = unseenPhotos;
        this.albumDescription = albumDescription;
    }

    protected AlbumsAnswer(Parcel in) {
        albumId = in.readString();
        albumName = in.readString();
        albumDescription = in.readString();
        picPath = in.readString();
        picCount = in.readInt();
        liked = in.readByte() != 0;
        videosCount = in.readInt();
        albumViews = in.readInt();
        commentsCount = in.readInt();
        likesCount = in.readInt();
        views = in.readInt();
        unseenPhotos = in.readInt();
        firstPicture = in.readString();
    }

    public static final Creator<AlbumsAnswer> CREATOR = new Creator<AlbumsAnswer>() {
        @Override
        public AlbumsAnswer createFromParcel(Parcel in) {
            return new AlbumsAnswer(in);
        }

        @Override
        public AlbumsAnswer[] newArray(int size) {
            return new AlbumsAnswer[size];
        }
    };

    public int getUnseenPhotos() {
        return unseenPhotos;
    }

    public void setUnseenPhotos(int unseenPhotos) {
        this.unseenPhotos = unseenPhotos;
        notifyPropertyChanged(BR.unseenPhotos);
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public String getAlbumId() {
        return albumId;
    }

    public void setAlbumId(String albumId) {
        this.albumId = albumId;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public String getPicPath() {
        return picPath;
    }

    public void setPicPath(String picPath) {
        this.picPath = picPath;
        notifyPropertyChanged(BR.picPath);
    }

    public String getFirstPicture() {
        return this.firstPicture;
    }

    public void setFirstPicture(String firstPicture) {
        this.firstPicture = firstPicture;
    }

    public String getAlbumDescription() {
        return this.albumDescription;
    }

    public void setAlbumDescription(String albumDescription) {
        this.albumDescription = albumDescription;
    }

    public Date getAlbumDate() {
        return albumDate;
    }

    public void setAlbumDate(Date albumDate) {
        this.albumDate = albumDate;
    }

    public int getPicCount() {
        return picCount;
    }

    public void setPicCount(int picCount) {
        this.picCount = picCount;
    }

    public int getVideosCount() {
        return videosCount;
    }

    public void setVideosCount(int videosCount) {
        this.videosCount = videosCount;
    }

    public int getAlbumViews() {
        return albumViews;
    }

    public void setAlbumViews(int albumViews) {
        this.albumViews = albumViews;
    }

    public int getCommentsCount() {
        return commentsCount;
    }

    public void setCommentsCount(int commentsCount) {
        this.commentsCount = commentsCount;
    }

    public int getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
        notifyPropertyChanged(BR.likesCount);
    }

    public boolean isLiked() {
        return liked;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }

    @Override
    public String toString() {
        return "AlbumsAnswer{" +
                "albumId='" + albumId + '\'' +
                ", albumName='" + albumName + '\'' +
                ", albumDescription='" + albumDescription + '\'' +
                ", picPath='" + picPath + '\'' +
                ", firstPicture='" + firstPicture + '\'' +
                ", albumDate=" + albumDate +
                ", picCount=" + picCount +
                ", liked=" + liked +
                ", videosCount=" + videosCount +
                ", albumViews=" + albumViews +
                ", commentsCount=" + commentsCount +
                ", likesCount=" + likesCount +
                ", views=" + views +
                ", unseenPhotos=" + unseenPhotos +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(albumId);
        dest.writeString(albumName);
        dest.writeString(albumDescription);
        dest.writeString(picPath);
        dest.writeInt(picCount);
        dest.writeByte((byte) (liked ? 1 : 0));
        dest.writeInt(videosCount);
        dest.writeInt(albumViews);
        dest.writeInt(commentsCount);
        dest.writeInt(likesCount);
        dest.writeInt(views);
        dest.writeInt(unseenPhotos);
    }
}

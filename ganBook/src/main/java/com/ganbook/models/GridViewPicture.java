package com.ganbook.models;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import android.os.Parcel;
import android.os.Parcelable;

import com.project.ganim.BR;

/**
 * Created by dmytro_vodnik on 7/4/16.
 * working on ganbook1 project
 */
public class GridViewPicture extends BaseObservable implements Parcelable {

    private String path;
    private boolean isDirectory;
    String fileName;

    @Bindable
    boolean selected;

    public GridViewPicture(boolean selected, String fileName, boolean isDirectory, String path) {
        this.selected = selected;
        this.fileName = fileName;
        this.isDirectory = isDirectory;
        this.path = path;
    }

    protected GridViewPicture(Parcel in) {
        path = in.readString();
        isDirectory = in.readByte() != 0;
        fileName = in.readString();
        selected = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(path);
        dest.writeByte((byte) (isDirectory ? 1 : 0));
        dest.writeString(fileName);
        dest.writeByte((byte) (selected ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<GridViewPicture> CREATOR = new Creator<GridViewPicture>() {
        @Override
        public GridViewPicture createFromParcel(Parcel in) {
            return new GridViewPicture(in);
        }

        @Override
        public GridViewPicture[] newArray(int size) {
            return new GridViewPicture[size];
        }
    };

    public String getPath() {
        return path;
    }

    public boolean isDirectory() {
        return isDirectory;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setDirectory(boolean directory) {
        isDirectory = directory;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
        notifyPropertyChanged(BR.selected);
    }

    @Override
    public String toString() {
        return "GridViewPicture{" +
                "path='" + path + '\'' +
                ", isDirectory=" + isDirectory +
                ", fileName='" + fileName + '\'' +
                ", selected=" + selected +
                '}';
    }

    @Override
    public boolean equals(Object v) {
        boolean retVal = false;

        if (v instanceof GridViewPicture){
            GridViewPicture ptr = (GridViewPicture) v;

            try {

                retVal = ptr.fileName.equals(this.fileName);
            }catch (Exception e){
                retVal =false;
            }
        }

        return retVal;
    }
}

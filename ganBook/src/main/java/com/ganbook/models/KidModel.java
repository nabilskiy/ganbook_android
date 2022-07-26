package com.ganbook.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by dmytro_vodnik on 7/19/16.
 * working on ganbook1 project
 */
public class KidModel implements Parcelable {

    @SerializedName("kid_id")
    public String kid_id;
    @SerializedName("kid_name")
    public String kid_name;
    @SerializedName("kid_pic")
    public String kid_pic;

    protected KidModel(Parcel in) {
        kid_id = in.readString();
        kid_name = in.readString();
        kid_pic = in.readString();
    }

    public KidModel(){}

    public static final Creator<KidModel> CREATOR = new Creator<KidModel>() {
        @Override
        public KidModel createFromParcel(Parcel in) {
            return new KidModel(in);
        }

        @Override
        public KidModel[] newArray(int size) {
            return new KidModel[size];
        }
    };

    public String getKid_id() {
        return kid_id;
    }

    public void setKid_id(String kid_id) {
        this.kid_id = kid_id;
    }

    public String getKid_name() {
        return kid_name;
    }

    public void setKid_name(String kid_name) {
        this.kid_name = kid_name;
    }

    public String getKid_pic() {
        return kid_pic;
    }

    public void setKid_pic(String kid_pic) {
        this.kid_pic = kid_pic;
    }

    @Override
    public String toString() {
        return "KidModel{" +
                "kid_id='" + kid_id + '\'' +
                ", kid_name='" + kid_name + '\'' +
                ", kid_pic='" + kid_pic + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(kid_id);
        dest.writeString(kid_name);
        dest.writeString(kid_pic);
    }
}

package com.ganbook.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by dmytro_vodnik on 6/8/16.
 * working on ganbook1 project
 */
public class SuccessAnswer implements Parcelable {

    protected SuccessAnswer(Parcel in) {
        success = in.readByte() != 0;
    }

    public static final Creator<SuccessAnswer> CREATOR = new Creator<SuccessAnswer>() {
        @Override
        public SuccessAnswer createFromParcel(Parcel in) {
            return new SuccessAnswer(in);
        }

        @Override
        public SuccessAnswer[] newArray(int size) {
            return new SuccessAnswer[size];
        }
    };

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public SuccessAnswer(boolean success) {
        this.success = success;
    }

    @SerializedName("success")
    boolean success;

    public Errors getErrors() {
        return errors;
    }

    public void setErrors(Errors errors) {
        this.errors = errors;
    }

    public String getApiKey() {
        return this.apiKey;
    }

    @SerializedName("errors")
    Errors errors;

    @SerializedName("api_key")
    String apiKey;

    @Override
    public String toString() {
        return "SuccessAnswer{" +
                "success=" + success +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (success ? 1 : 0));
    }
}

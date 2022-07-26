package com.ganbook.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by dmytro_vodnik on 7/8/16.
 * working on ganbook1 project
 */
public class MessageAnswer implements Parcelable {

    @SerializedName("total_active_parents")
    int totalActiveParents;

    @SerializedName("messages")
    List<MessageModel> messageModels;

    protected MessageAnswer(Parcel in) {
        totalActiveParents = in.readInt();
        messageModels = in.createTypedArrayList(MessageModel.CREATOR);
    }

    public static final Creator<MessageAnswer> CREATOR = new Creator<MessageAnswer>() {
        @Override
        public MessageAnswer createFromParcel(Parcel in) {
            return new MessageAnswer(in);
        }

        @Override
        public MessageAnswer[] newArray(int size) {
            return new MessageAnswer[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(totalActiveParents);
        dest.writeTypedList(messageModels);
    }

    public int getTotalActiveParents() {
        return totalActiveParents;
    }

    public void setTotalActiveParents(int totalActiveParents) {
        this.totalActiveParents = totalActiveParents;
    }

    public List<MessageModel> getMessageModels() {
        return messageModels;
    }

    public void setMessageModels(List<MessageModel> messageModels) {
        this.messageModels = messageModels;
    }

    @Override
    public String toString() {
        return "MessageAnswer{" +
                "totalActiveParents=" + totalActiveParents +
                ", messageModels=" + messageModels +
                '}';
    }
}

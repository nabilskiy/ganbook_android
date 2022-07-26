package com.ganbook.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.ganbook.utils.DateFormatter;
import com.google.gson.annotations.SerializedName;

/**
 * Created by dmytro_vodnik on 7/8/16.
 * working on ganbook1 project
 */
public class MessageModel implements Parcelable {

    @SerializedName("message_id")
    String messageId;
    @SerializedName("message_text")
    String messageText;
    @SerializedName("message_date")
    String messageDate;
    @SerializedName("class_id")
    String classId;
    @SerializedName("class_name")
    String className;
    @SerializedName("user_id")
    String userId;
    @SerializedName("user_type")
    int userType;
    @SerializedName("user_first_name")
    String userFirstName;
    @SerializedName("user_last_name")
    String userLastName;
    @SerializedName("views")
    int views;

    protected MessageModel(Parcel in) {
        messageId = in.readString();
        messageText = in.readString();
        messageDate = in.readString();
        classId = in.readString();
        className = in.readString();
        userId = in.readString();
        userType = in.readInt();
        userFirstName = in.readString();
        userLastName = in.readString();
        views = in.readInt();
    }

    public MessageModel() {}

    public static final Creator<MessageModel> CREATOR = new Creator<MessageModel>() {
        @Override
        public MessageModel createFromParcel(Parcel in) {
            return new MessageModel(in);
        }

        @Override
        public MessageModel[] newArray(int size) {
            return new MessageModel[size];
        }
    };

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }


    public String getMessageDate() {
        return messageDate;
    }

    public void setMessageDate(String messageDate) {
        this.messageDate = messageDate;
    }

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getUserType() {
        return userType;
    }

    public void setUserType(int userType) {
        this.userType = userType;
    }

    public String getUserFirstName() {
        return userFirstName;
    }

    public void setUserFirstName(String userFirstName) {
        this.userFirstName = userFirstName;
    }

    public String getUserLastName() {
        return userLastName;
    }

    public void setUserLastName(String userLastName) {
        this.userLastName = userLastName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(messageId);
        dest.writeString(messageText);
        dest.writeString(messageDate);
        dest.writeString(classId);
        dest.writeString(className);
        dest.writeString(userId);
        dest.writeInt(userType);
        dest.writeString(userFirstName);
        dest.writeString(userLastName);
        dest.writeInt(views);
    }

    @Override
    public String toString() {
        return "MessageModel{" +
                "messageId='" + messageId + '\'' +
                ", messageText='" + messageText + '\'' +
                ", messageDate='" + messageDate + '\'' +
                ", classId='" + classId + '\'' +
                ", className='" + className + '\'' +
                ", userId='" + userId + '\'' +
                ", userType=" + userType +
                ", userFirstName='" + userFirstName + '\'' +
                ", userLastName='" + userLastName + '\'' +
                ", views='" + views + '\'' +
                '}';
    }

    public static MessageModel createMessage(String message, String message_id) {

        MessageModel m = new MessageModel();

        m.setMessageText(message);
        m.setMessageDate(DateFormatter.getCurrent_In_Message_Format());
        m.setMessageId(message_id);
        m.setViews(0);

        return m;
    }
}

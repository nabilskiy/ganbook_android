package com.ganbook.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by dmytro_vodnik on 7/13/16.
 * working on ganbook1 project
 */
public class MessageStatus {

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    @SerializedName("message_id")
    String messageId;

    @Override
    public String toString() {
        return "MessageStatus{" +
                "messageId='" + messageId + '\'' +
                '}';
    }
}

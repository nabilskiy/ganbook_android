package com.ganbook.models;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Commercial implements Serializable {
    @SerializedName("id")
    private String id;

    @SerializedName("clientId")
    private int clientId;

    @SerializedName("commercialNote")
    private String commercialNote;

    @SerializedName("commercialUrl")
    private String commercialUrl;

    @SerializedName("imageName")
    private String imageName;

    @SerializedName("userType")
    private String userType;

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public String getCommercialNote() {
        return commercialNote;
    }

    public void setCommercialNote(String commercialNote) {
        this.commercialNote = commercialNote;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCommercialUrl() {
        return commercialUrl;
    }

    public void setCommercialUrl(String commercialUrl) {
        this.commercialUrl = commercialUrl;
    }

    @NonNull
    @Override
    public String toString() {
        return "COMMERCIAL{ " +
                "imageName: " + imageName + " }";
    }
}

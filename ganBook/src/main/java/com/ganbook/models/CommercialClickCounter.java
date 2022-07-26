package com.ganbook.models;

import com.google.gson.annotations.SerializedName;

public class CommercialClickCounter {

    @SerializedName("clicksCounter")
    private int clicksCounter;

    public int getClicksCounter() {
        return clicksCounter;
    }

    public void setClicksCounter(int clicksCounter) {
        this.clicksCounter = clicksCounter;
    }
}
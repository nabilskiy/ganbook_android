package com.ganbook.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by dmytro_vodnik on 6/20/16.
 * working on ganbook1 project
 */
public class OKAnswer {

    public String getOk() {
        return ok;
    }

    public void setOk(String ok) {
        this.ok = ok;
    }

    @SerializedName("OK")
    String ok;

    @Override
    public String toString() {
        return "OKAnswer{" +
                "ok='" + ok + '\'' +
                '}';
    }
}

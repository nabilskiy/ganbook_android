package com.ganbook.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by dmytro_vodnik on 8/9/16.
 * working on ganbook1 project
 */
public class Errors {

    @SerializedName("code")
    String code;
    @SerializedName("msg")
    String msg;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "Errors{" +
                "code='" + code + '\'' +
                ", msg='" + msg + '\'' +
                '}';
    }
}

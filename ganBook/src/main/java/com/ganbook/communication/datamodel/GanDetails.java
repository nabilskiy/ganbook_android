package com.ganbook.communication.datamodel;

import com.google.gson.annotations.SerializedName;

public class GanDetails {

    @SerializedName("gan_id")
    public String gan_id; //": "1589",
    @SerializedName("gan_name")
    public String gan_name; //":
    @SerializedName("gan_code")
    public String gan_code; //": "2095",
    @SerializedName("gan_phone")
    public String gan_phone; //": null,
    @SerializedName("gan_address")
    public String gan_address; //": "0542405495",
    @SerializedName("gan_city")
    public String gan_city; //": 

    public String getGan_id() {
        return gan_id;
    }

    public void setGan_id(String gan_id) {
        this.gan_id = gan_id;
    }

    public String getGan_name() {
        return gan_name;
    }

    public void setGan_name(String gan_name) {
        this.gan_name = gan_name;
    }

    public String getGan_code() {
        return gan_code;
    }

    public void setGan_code(String gan_code) {
        this.gan_code = gan_code;
    }

    public String getGan_phone() {
        return gan_phone;
    }

    public void setGan_phone(String gan_phone) {
        this.gan_phone = gan_phone;
    }

    public String getGan_address() {
        return gan_address;
    }

    public void setGan_address(String gan_address) {
        this.gan_address = gan_address;
    }

    public String getGan_city() {
        return gan_city;
    }

    public void setGan_city(String gan_city) {
        this.gan_city = gan_city;
    }

    @Override
    public String toString() {
        return "GanDetails{" +
                "gan_id='" + gan_id + '\'' +
                ", gan_name='" + gan_name + '\'' +
                ", gan_code='" + gan_code + '\'' +
                ", gan_phone='" + gan_phone + '\'' +
                ", gan_address='" + gan_address + '\'' +
                ", gan_city='" + gan_city + '\'' +
                '}';
    }
}
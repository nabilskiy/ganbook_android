package com.ganbook.communication.json;

import com.google.gson.annotations.SerializedName;

public class InstitutionLogoResponse extends BaseResponse {

    @SerializedName("kindergarten_logo")
    String kindergartenLogo;

    public String getKindergartenLogo() {
        return kindergartenLogo;
    }

    public void setKindergartenLogo(String kindergartenLogo) {
        this.kindergartenLogo = kindergartenLogo;
    }

    @Override
    public String toString() {
        return "GET INSTITUTION LOGO RESPONSE{" +
                "kindergarten logo='" + kindergartenLogo + '\'' +
                '}';
    }
}

package com.ganbook.communication.json;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Grigory on 6/20/2017.
 */

public class getparentstosendnotification_Response extends BaseResponse {
    @SerializedName("data")
    public String[] data;
}

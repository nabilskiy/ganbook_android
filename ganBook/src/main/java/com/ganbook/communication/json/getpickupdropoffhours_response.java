package com.ganbook.communication.json;

import com.ganbook.utils.ActiveUtils;
import com.ganbook.utils.GenderUtils;
import com.google.gson.annotations.SerializedName;

public class getpickupdropoffhours_response extends BaseResponse {
    @SerializedName("kiddropoffhour_from")
    public String kiddropoffhour_from;
    @SerializedName("kiddropoffhour_to")
    public String kiddropoffhour_to;
    @SerializedName("kidpickuphour_from")
    public String kidpickuphour_from;
    @SerializedName("kidpickuphour_to")
    public String kidpickuphour_to;
}

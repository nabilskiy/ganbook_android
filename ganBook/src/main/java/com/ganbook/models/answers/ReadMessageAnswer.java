package com.ganbook.models.answers;

import com.ganbook.app.MyApp;
import com.ganbook.utils.StrUtils;
import com.google.gson.annotations.SerializedName;

/**
 * Created by dmytro_vodnik on 7/12/16.
 * working on ganbook1 project
 */
public class ReadMessageAnswer {

    @SerializedName("user_id")
    public String user_id;
    @SerializedName("first_name")
    public String first_name;
    @SerializedName("last_name")
    public String last_name;
    @SerializedName("read")
    public boolean read;

    public transient boolean isHeader = false;
    public transient boolean isViewedheader = false;

    public static ReadMessageAnswer createHeaderItem(int captionResId, boolean isViewedheader) {
        // used for display only!
        ReadMessageAnswer header = new ReadMessageAnswer();
        header.isHeader = true;
        header.read = false;
        header.isViewedheader = isViewedheader;
        header.first_name = MyApp.context.getResources().getString(captionResId);
        return header;
    }

    public CharSequence getName() {
        return StrUtils.emptyIfNull(first_name) + " " + StrUtils.emptyIfNull(last_name);
    }
}

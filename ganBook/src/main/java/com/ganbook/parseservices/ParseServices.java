package com.ganbook.parseservices;

import com.ganbook.app.MyApp;
import com.ganbook.sharedprefs.SPReader;
import com.ganbook.utils.StrUtils;
import com.parse.ParseInstallation;

/**
 * Created by Noa on 25/11/2015.
 */
public class ParseServices {
    public static synchronized String getObjectId() {
        String objectId = new SPReader(MyApp.PARSE_SERVICES).getString(MyApp.PROPERTY_OBJECT_ID, "");

        if (StrUtils.isEmpty(objectId)) {
            objectId = ParseInstallation.getCurrentInstallation().getObjectId();
        }

        return objectId;

    }
}

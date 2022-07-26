package com.ganbook.communication.json;

import com.ganbook.communication.upload.state.UploadStatus;

public class getfavorite_Response extends getpicture_Response {
    public String year;
    public String class_id;
    public String gan_id;

    public getfavorite_Response(getpicture_Response getpicture_response, String class_id, String gan_id, String year)
    {
        super(getpicture_response);

        this.class_id = class_id;
        this.gan_id = gan_id;
        this.year = year;
    }
}
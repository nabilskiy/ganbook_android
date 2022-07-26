package com.ganbook.communication.json;

import com.ganbook.communication.upload.state.UploadStatus;

public class getpicture_Response extends BaseResponse {
    public String picture_id; //": "419727",
    public String picture_name; //": "15892778156891189366545.jpeg",
    public String picture_date; //": "2015-01-26 17:15:34",
    public String album_id; //": "15689",
    public String picture_active; //": "1",
    public String create_date; //": "0.000000"
    public String video_duration;
    public boolean favorite;
    
    public transient boolean isChecked;    
	public transient UploadStatus uploadState = null;
	public transient String uploadImgFilePath;
	public transient String uploadThumbFilePath;

    public getpicture_Response() {

    }

    public getpicture_Response(getpicture_Response getpicture_response)
    {
        this.picture_id = getpicture_response.picture_id;
        this.picture_name = getpicture_response.picture_name;
        this.picture_date = getpicture_response.picture_date;
        this.album_id = getpicture_response.album_id;
        this.picture_active = getpicture_response.picture_active;
        this.create_date = getpicture_response.create_date;
        this.video_duration = getpicture_response.video_duration;
        this.favorite = getpicture_response.favorite;
    }
}
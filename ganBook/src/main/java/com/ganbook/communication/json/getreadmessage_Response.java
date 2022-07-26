package com.ganbook.communication.json;

import com.ganbook.app.MyApp;
import com.ganbook.utils.StrUtils;

public class getreadmessage_Response extends BaseResponse {
    public String user_id; //": "16100",
    public String first_name; //": "P900",
    public String last_name; //": "",
    public boolean read; //": true
    public transient boolean isHeader = false;
    public transient boolean isViewedheader = false;
    
    public static getreadmessage_Response createHeaderItem(int captionResId, boolean isViewedheader) {
    	// used for display only!
    	getreadmessage_Response header = new getreadmessage_Response();
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
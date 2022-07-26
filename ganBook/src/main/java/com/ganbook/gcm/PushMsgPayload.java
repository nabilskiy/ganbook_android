package com.ganbook.gcm;

import org.json.JSONObject;



public class PushMsgPayload { // noanoa push payload class

	public String msg_to_show;
	public String class_id;
	public String kid_id;
	public String loc_key;

	public static final String WAIT_CLASS_APRVL = "WAIT_CLASS_APRVL";
	public static final String CONFIRM_CLASS    = "CONFIRM_CLASS";
	public static final String MSG              = "MSG";
	public static final String MSG_PTA          = "MSG_PTA";
	public static final String NEW_PICS_UPLD    = "NEW_PICS_UPLD";
	public static final String NEW_PICS_UPLD_1  = "NEW_PICS_UPLD_1";
	public static final String NEW_VID_UPLD     = "NEW_VID_UPLD";
	public static final String EVNT_BD     = "EVNT_BD";
	public static final String EVNT     = "EVNT";
	public static final String MEETING_MSG     = "MEETING_MSG";

	public PushMsgPayload(String class_id, String loc_key)
	{
		this.class_id = class_id;
		this.loc_key = loc_key;
	}
	


	public static String extractMessage(String orig_json) {
		try { 
			JSONObject obj = new JSONObject(orig_json);
			String msg = obj.getString("msg");
			return msg;
		}
		catch (Exception e) {
			int jj=234;
			jj++;
		}
		return "";
	}


}


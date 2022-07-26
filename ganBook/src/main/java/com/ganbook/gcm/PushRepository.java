package com.ganbook.gcm;


import android.util.Log;

public class PushRepository { // noanoa
	private PushRepository() {}

	public static final String PUSH_JSON_MSG = "PUSH_JSON_MSG";
	public static final String PUSH_CLASS_ID = "PUSH_CLASS_ID";
	public static final String PUSH_LOC_KEY = "PUSH_LOC_KEY";

	private static final long MAX_NOTIF_ACTIVE_TIME = 4222;
	
	private static volatile ActivePushNotif clicked_notif;
	
	static class ActivePushNotif {
		public final long start_time;
		public final PushMsgPayload inner_msg;
		
		ActivePushNotif(String class_id, String loc_key) {
			inner_msg = new PushMsgPayload(class_id, loc_key);//PushMsgPayload.from_string(raw_json);
			start_time = System.currentTimeMillis();
		}
	}
	
	public static PushMsgPayload extractActiveNotif() {
		ActivePushNotif _notif = clicked_notif;
		clicked_notif = null;
		PushMsgPayload msg = null;
		if (_notif != null) {
			msg = _notif.inner_msg;

		}
		return msg;
	}

	public static void setActivePushNotif(String class_id, String loc_key) {
		clicked_notif = null;
		try { 
			clicked_notif = new ActivePushNotif(class_id, loc_key);
			Log.d("CLICKED NOTIFICATION", loc_key);
		}
		catch (Exception e) {
			int jj=234;
			jj++;
		}
	} 

}

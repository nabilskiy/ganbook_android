package com.ganbook.notifications;


public class PushNotificationUtils {
	private PushNotificationUtils() {}

	private static final int NOTIFICATION_ID = 100;


	public static void notify(String msg, String class_id, String loc_key, String pic_url) {
		NotificationManagerMy.write(NOTIFICATION_ID, msg, class_id, loc_key, pic_url);
	}

}



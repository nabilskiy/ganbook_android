package com.ganbook.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

import com.ganbook.app.MyApp;

public class FeedbackUtils {
	private FeedbackUtils() {}

	public static String getEmailBody() {
		Context c = MyApp.context;
		PackageManager manager = c.getPackageManager();
		int versionCode = 0;
		String versionName = "";
		try {
			PackageInfo info = manager.getPackageInfo(c.getPackageName(), 0);
			versionCode = info.versionCode;
			versionName = info.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			// go on
		}
		int sdkVer = android.os.Build.VERSION.SDK_INT;
		String deviceModel = DeviceNameGetter.get();
		
		String body = 
				"\n\nApp Ver: " + versionName + "(" + versionCode + ")" + 
				"\nOS: Android " + sdkVer + 
				"\nDevice Model: " + deviceModel;
		return body;
	}

}

package com.ganbook.sharedprefs;

import com.ganbook.app.MyApp;

import android.content.Context;
import android.content.SharedPreferences;

public class SPReader {
	
	private final SharedPreferences sprefs;
	
	public SPReader(String fileName) { 
		Context context = MyApp.context;
		sprefs = context.getSharedPreferences(fileName, 0);
	}
	
	public String getString(String fieldName, String defaultt) {
		return sprefs.getString(fieldName, defaultt);
	}
	
	public int getInt(String fieldName, int defaultt) {
		return sprefs.getInt(fieldName, defaultt);
	}
	
	public boolean getBool(String fieldName, boolean defaultt) {
		return sprefs.getBoolean(fieldName, defaultt);
	}
	
	public long getLong(String fieldName, long defaultt) {
		return sprefs.getLong(fieldName, defaultt);
	}
	

}

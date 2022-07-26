package com.ganbook.sharedprefs;

import com.ganbook.app.MyApp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;


public class SPWriter {

	private final SharedPreferences sprefs;
	private final SharedPreferences.Editor editor;

	@SuppressLint("CommitPrefEdits")
	public SPWriter(String fileName) {
		Context context = MyApp.context;
		sprefs = context.getSharedPreferences(fileName, 0);
		editor = sprefs.edit();
	}
	
	public SPWriter putString(String fieldName, String val) {
		editor.putString(fieldName, val);
		return this;
	}

	public SPWriter putInt(String fieldName, int val) {
		editor.putInt(fieldName, val);
		return this;
	}

	public SPWriter putLong(String fieldName, long val) {
		editor.putLong(fieldName, val);
		return this;
	}

	public SPWriter putBool(String fieldName, boolean val) {
		editor.putBoolean(fieldName, val);
		return this;
	}
	
	public SPWriter putFloat(String fieldName, float val) {
		editor.putFloat(fieldName, val);
		return this;
	}
	
	public void commit() {
		editor.commit();
	}

}

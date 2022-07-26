package com.ganbook.utils;

import android.app.Activity;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class KeyboardUtils {
	private KeyboardUtils() {}

	public static void close(Activity a, EditText editText) {
		InputMethodManager imm = (InputMethodManager)a.getSystemService(
				Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);	
	}

}

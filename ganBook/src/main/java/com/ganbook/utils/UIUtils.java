package com.ganbook.utils;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.view.View;

public class UIUtils {
	private UIUtils() {}

	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	public static void setBackgroundDrawable(View view, Drawable drawable) {
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
			view.setBackground(drawable);
		} else {
			view.setBackgroundDrawable(drawable);			
		}
	}

}

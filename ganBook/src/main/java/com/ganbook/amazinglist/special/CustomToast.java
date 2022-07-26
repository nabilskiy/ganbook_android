package com.ganbook.amazinglist.special;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.project.ganim.R;

public class CustomToast {
	private CustomToast() {}
	
	public static void show(Activity a, int msgResId) {
		try {
			String msg = a.getResources().getString(msgResId);
			show(a, msg);
		} catch (NullPointerException npe) {

            Log.e("t", "show: error while get res" + Log.getStackTraceString(npe) );
        }
	}

	public static void show(Context c, int msgResId) {
		String msg = c.getResources().getString(msgResId);
		show(c, msg);
	}

	public static void show(Context a, String msg) {
		if (a != null) {
			LayoutInflater inflater = LayoutInflater.from(a);
			View layout = inflater.inflate(R.layout.ganbook_toast, null);
			TextView text = (TextView) layout.findViewById(R.id.toast_text);
			text.setText(msg);

			Toast toast = new Toast(a.getApplicationContext());
			toast.setGravity(Gravity.CENTER, 0, 0);
//		toast.setDuration(Toast.LENGTH_LONG);
			toast.setDuration(Toast.LENGTH_SHORT);
			toast.setView(layout);
			toast.show();
		}
	}
	
	public static void show(Activity a, String msg) {
		if (a != null) {
			LayoutInflater inflater = a.getLayoutInflater();
			View layout = inflater.inflate(R.layout.ganbook_toast,
					(ViewGroup) a.findViewById(R.id.toast_layout_root));
			TextView text = (TextView) layout.findViewById(R.id.toast_text);
			text.setText(msg);

			Toast toast = new Toast(a.getApplicationContext());
			toast.setGravity(Gravity.CENTER, 0, 0);
//		toast.setDuration(Toast.LENGTH_LONG);
			toast.setDuration(Toast.LENGTH_SHORT);
			toast.setView(layout);
			toast.show();
		}
	}

	public static void showLong(Activity a, String msg) {
		if (a != null) {
			LayoutInflater inflater = a.getLayoutInflater();
			View layout = inflater.inflate(R.layout.ganbook_toast,
					(ViewGroup) a.findViewById(R.id.toast_layout_root));
			TextView text = (TextView) layout.findViewById(R.id.toast_text);
			text.setText(msg);

			Toast toast = new Toast(a.getApplicationContext());
			toast.setGravity(Gravity.CENTER, 0, 0);
//		toast.setDuration(Toast.LENGTH_LONG);
			toast.setDuration(Toast.LENGTH_LONG);
			toast.setView(layout);
			toast.show();
			toast.show();
		}
	}
}

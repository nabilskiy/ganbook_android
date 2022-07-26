package com.ganbook.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.ganbook.user.User;

import java.net.URLEncoder;
import java.security.spec.ECField;

public class UrlUtils {
	public static final String TAG = "UrlUtils";

	private UrlUtils() {}

	public static String urlToName(final String _url) {
		if (_url==null) {
			return null;
		}
		int ind = _url.lastIndexOf("/");
		if (ind < 0 || ind > (_url.length()-3)) {
//			fileName = System.currentTimeMillis() + ".jpeg";
			return null;
		} 
		String name = _url.substring(ind+1);
		return name;
	}

	public static void openWebURL(Context ctx, String inURL) {
		Intent browse = new Intent( Intent.ACTION_VIEW , Uri.parse( inURL ) );
		ctx.startActivity( browse );
	}

	public static void openBitApp(Context ctx) {
		try {
			String bitUrl = "http://www.ganbook.co.il/bit.php?email=";
			bitUrl += URLEncoder.encode(User.current.mail);

			bitUrl="https://www.partyshop.co.il/";
			Log.d(TAG, bitUrl);
			openWebURL(ctx, bitUrl);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

}

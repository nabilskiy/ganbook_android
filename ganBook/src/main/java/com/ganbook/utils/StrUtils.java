package com.ganbook.utils;

import android.util.Log;

import com.ganbook.communication.json.transmitter.JsonTransmitter;
import com.ganbook.interfaces.GanbookApiInterface;
import com.ganbook.user.User;

import java.io.IOException;

import okhttp3.RequestBody;
import okio.Buffer;

public class StrUtils {
	private static final String TAG = StrUtils.class.getName();

	private StrUtils() {}
	
	public static boolean notEmpty(String s) {
		return !isEmpty(s);
	}

	public static boolean isEmpty(String s) {
		return s==null || s.isEmpty();
	}

	public static String nullIfEmpty(String s) {
		if (s==null) {
			return null;
		}
		return s.trim().isEmpty() ? null : s;
	}

	public static String bodyToString(final RequestBody request){
		try {
			final RequestBody copy = request;
			final Buffer buffer = new Buffer();
			if(copy != null)
				copy.writeTo(buffer);
			else
				return "";
			return buffer.readUtf8();
		}
		catch (final IOException e) {
			return "did not work";
		}
	}
	
	public static String emptyIfNull(String s) {
		return (s == null || s.isEmpty()) ? "" : s;
	}

	public static String trim(String s) {
		if (s != null) {
			s = s.trim();
		}
		return s;
	}

	public static String generateTitle() {

		String first_name = "";
		String titleTxt = "";

		try {
			if (User.current.isParent()) {
				first_name = StrUtils.emptyIfNull(User.current.getCurrentKid().kid_name);

			} else {
				first_name = StrUtils.emptyIfNull(User.current.getCurrentGanName());
			}
			String class_name = StrUtils.emptyIfNull(User.current.getCurrentClassName());

			titleTxt = first_name;

			if (!StrUtils.isEmpty(class_name)) {
				titleTxt += " - " + class_name;
			}


		} catch (Exception e) {

			Log.e(TAG, "generateTitle: error while generate title = " + titleTxt);
		}
		return titleTxt;
	}

	public static String getAlbumFullSizeUrl(String picture, String album_id, String class_id, String gan_id) {

		String url = GanbookApiInterface.PICTURE_HOST +
				gan_id + "/" +
				class_id + "/" +
				album_id + "/" + //JsonTransmitter.TMB +
				picture;//.replace("jpeg", "jpg");
		return url;
	}

	public static String getDrawingAlbumFullSizeUrl(String picture, String kid_id, String class_id, String gan_id) {

		String url = GanbookApiInterface.DRAWING_HOST +
				gan_id + "/" +
				class_id + "/" +
				kid_id + "/" +
				picture;
		return url;
	}

	public static String getAlbumTmbUrl(String picture, String album_id, String class_id, String gan_id) {

		String url = GanbookApiInterface.PICTURE_HOST +
				gan_id + "/" +
				class_id + "/" +
				album_id + "/" + JsonTransmitter.TMB +
				picture.replace(".mp4", ".jpeg");
		return url;
	}


}

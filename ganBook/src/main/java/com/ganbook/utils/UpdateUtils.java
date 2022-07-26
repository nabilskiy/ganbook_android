package com.ganbook.utils;

import com.ganbook.user.User;

public class UpdateUtils {
	public static final String NO_UPDATE = "0";
	public static final String RECOMMEND_UPDATE = "1";
	public static final String MANDATORY_UPDATE = "2";

	public static String get() {
		return User.getUpdate();
	}
}

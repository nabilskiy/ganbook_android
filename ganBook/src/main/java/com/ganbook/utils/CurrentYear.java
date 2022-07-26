package com.ganbook.utils;

import com.ganbook.user.User;

public class CurrentYear {
	private CurrentYear() {}
	
	private static String thisYear; 
	
	public static String get() {
		return User.getCurrentYear();
	}

}

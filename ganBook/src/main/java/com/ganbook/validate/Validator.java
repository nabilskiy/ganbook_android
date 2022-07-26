package com.ganbook.validate;

import android.util.Patterns;

public class Validator {
	private Validator() {}
	
	public static boolean validName(String name) {
		return name != null && name.length() > 1; 		
	}

	public static boolean validPassword(String name) {
		return name != null && name.length() > 3; 		
	}
	
	public static boolean validEmail(String email) {
		//return name != null && name.length() > 3 && name.contains("@") && name.contains(".");
		return email != null && Patterns.EMAIL_ADDRESS.matcher(email).matches(); 
	}

	public static boolean validPhoneNo(String phoneNo) {
		return phoneNo != null && Patterns.PHONE.matcher(phoneNo).matches(); 
	}
	
}

package com.ganbook.name;

import com.ganbook.validate.Validator;

public class NameUtils {
	private NameUtils() {}
	
	public static String[] breakName(String fullName) {
		if (!Validator.validName(fullName)) {
			return null;
		}
		fullName = fullName.trim();
		int ind = fullName.indexOf(" ");
		if (ind < 0) {
			return new String[] { fullName, "" }; // single name user
		}
		String givenName = fullName.substring(0, ind).trim();
		String surname = fullName.substring(ind+1);
		return new String[] { givenName, surname };		
	}

}

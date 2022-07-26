package com.ganbook.utils;

public class ActiveUtils {

	public static final String DISCONNECTED = "0";
	public static final String APPROVED = "1";
	public static final String WAITING_APPROVAL = "2";

	public static boolean isActive(String kid_active) {
		if (kid_active==null) {
			return false;
		}
		if (APPROVED.equals(kid_active)) {
			return true;
		}
		
		return false;				
		
	}
}

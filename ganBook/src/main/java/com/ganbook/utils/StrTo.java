package com.ganbook.utils;

public class StrTo {
	private StrTo() {}

	public static int Int(String s) {
		if (s==null) {
			return 0;
		}
		try { 
			int num = Integer.parseInt(s);
			return num;
		} catch (Exception e) {
			return 0;
		}
	}

}

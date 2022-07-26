package com.ganbook.datamodel;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import com.ganbook.communication.json.GetUserKids_Response;
import com.ganbook.user.User;

public class UserKids {
	private UserKids() {}
	
	public static ArrayList<GetUserKids_Response> allKids;
	
	public static Collection<String> getAllYears(boolean includeCurrent) {
		HashMap<String,String> yearMap = new HashMap<String,String>(); 
		if (includeCurrent) {
			String curYear = User.getCurrentYear();
			yearMap.put(curYear, curYear);
		}
		for (GetUserKids_Response kid: allKids) {
			String class_year = kid.class_year;
			yearMap.put(class_year,class_year);
		}
		return yearMap.values();
	}

	public static void set(Object resultArr) {
		allKids = new ArrayList<GetUserKids_Response>();
		int length = Array.getLength(resultArr);
	    for (int i = 0; i < length; i++) { 
	        Object singleResult = Array.get(resultArr, i);
			if (!(singleResult instanceof GetUserKids_Response)) {
				throw new RuntimeException();
			}
			GetUserKids_Response kidRes = (GetUserKids_Response) singleResult; 
			allKids.add(kidRes);
	    }
	}
}

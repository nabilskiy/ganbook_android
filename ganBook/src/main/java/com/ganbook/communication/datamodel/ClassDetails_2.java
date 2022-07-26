package com.ganbook.communication.datamodel;

import com.ganbook.utils.StrTo;

public class ClassDetails_2 {
	
	
	// see User.classList	
	
	public String class_id; //": "2073",
	public String class_name; //": "
	public String pending_parents;
	public String class_num_kids; //": "3",
	public String class_num_parents; //": "3"
	public String class_year; //": "
	public String meetings_active;
	public ClassHistory[] history; //": "3"

	public ClassDetails_2() {
	}
	
	public int getNumParents() {
		return StrTo.Int(class_num_parents);
	}

	
	public ClassDetails_2(ClassDetails other) {
		class_id = other.class_id;
		class_name = other.class_name;
		class_num_kids = "0";
		class_num_parents = "0";
		class_year = other.class_year;
		meetings_active = other.meetings_active;
	}
	
	public class ClassHistory {
		public String class_id; //": "2073",
		public String class_name; //": "
		public String class_year; //": "3"
	}

}
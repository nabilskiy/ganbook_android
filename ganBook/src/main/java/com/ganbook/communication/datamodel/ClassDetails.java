package com.ganbook.communication.datamodel;

import com.google.gson.annotations.SerializedName;

public class ClassDetails {

	public String getClass_name() {
		return class_name;
	}

	public void setClass_name(String class_name) {
		this.class_name = class_name;
	}

	public String getClass_id() {
		return class_id;
	}

	public void setClass_id(String class_id) {
		this.class_id = class_id;
	}

	public String getClass_year() {
		return class_year;
	}

	public void setClass_year(String class_year) {
		this.class_year = class_year;
	}

	public String getClass_num_kids() {
		return class_num_kids;
	}

	public void setClass_num_kids(String class_num_kids) {
		this.class_num_kids = class_num_kids;
	}

	public String getClass_num_parents() {
		return class_num_parents;
	}

	public void setClass_num_parents(String class_num_parents) {
		this.class_num_parents = class_num_parents;
	}

	@SerializedName("class_name")
	public String class_name; //":
	@SerializedName("class_id")
	public String class_id; //": "2778",
	@SerializedName("class_year")
	public String class_year; //": "2015"
	public String class_num_kids;
	public String class_num_parents;

	@SerializedName("meetings_active")
	public String meetings_active;

	@Override
	public String toString() {
		return "ClassDetails{" +
				"class_name='" + class_name + '\'' +
				", class_id='" + class_id + '\'' +
				", class_year='" + class_year + '\'' +
				", class_num_kids='" + class_num_kids + '\'' +
				", class_num_parents='" + class_num_parents + '\'' +
				'}';
	}
}

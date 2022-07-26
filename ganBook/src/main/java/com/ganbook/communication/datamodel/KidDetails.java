package com.ganbook.communication.datamodel;

import com.ganbook.communication.json.GetUserKids_Response;

public class KidDetails {
	
	public KidDetails() {}
	
	public KidDetails(GetUserKids_Response kid_response, String android_pic_path) {
		this.id = kid_response.kid_id;
		this.gender = kid_response.kid_gender;
		this.name = kid_response.kid_name;
		this.birth_date = kid_response.kid_bd;
		this.android_pic_path = android_pic_path;
	}
	
	public String id;// " : "5805",
	public String gender;// ": "1",
	public String name;// ": "noa",
	public String birth_date;// ": “2013-11-12" 
	public String android_pic_path;
	public boolean to_update_pic; 
}

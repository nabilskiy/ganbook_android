package com.ganbook.communication.json;

public class createuser_Response extends BaseResponse implements Response_With_User {
	public String id; // --- user_id
	public String current_year;
	public String token_id;

	// user set params
	public transient String name; 
	public transient String type; 
	public transient String mail; 
	public transient String password; 
	public transient String lang_region; 
	public transient String app_name;
}


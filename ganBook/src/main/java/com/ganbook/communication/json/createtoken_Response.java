package com.ganbook.communication.json;

public class createtoken_Response extends BaseResponse {
	public String token_id;
	@Override
	public String toString() {
		return "createtoken_Response with token == <" + token_id + ">"; 
	}
}

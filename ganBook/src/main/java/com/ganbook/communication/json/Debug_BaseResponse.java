package com.ganbook.communication.json;

public class Debug_BaseResponse extends BaseResponse {
	public final String error_message;
	
	public Debug_BaseResponse(String error_message) {
		this.error_message = error_message;
	}

}

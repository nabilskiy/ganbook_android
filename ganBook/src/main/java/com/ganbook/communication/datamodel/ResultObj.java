package com.ganbook.communication.datamodel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.ganbook.communication.json.BaseResponse;
import com.ganbook.communication.json.createtoken_Response;

public class ResultObj {
	public final boolean succeeded;
	public final boolean server_error;
	public final String result;
	public final Exception exception;
	public final int errorCode;
	
	private final ArrayList<BaseResponse> responseArr = new ArrayList<BaseResponse>();
	
	private ResultObj(
			boolean succeeded,
			boolean server_error,
			String result,
			Exception exception, 
			int errorCode) {		
		this.succeeded = succeeded;
		this.server_error = server_error;
		this.result = result;
		this.exception = exception;
		this.errorCode = errorCode;
	}


	public static ResultObj success(String msg) {
		return new ResultObj(true, false, msg, null, -1);
	}


	public static ResultObj failure(boolean server_error, String msg) {
		return failure(server_error, msg, null, -1);
	}
	
	public static ResultObj failure(boolean server_error, String msg, Exception e, int errorCode) {
		return new ResultObj(false, server_error, msg, e, errorCode);
	}
	

	public boolean internalErrorOccurred() {
		return !succeeded && !server_error; 
	}


	public void addResponse(BaseResponse response) {
		responseArr.add(response);
	}


	public boolean hasResponse() {
		return !responseArr.isEmpty();
	}

	public int getNumResponses() {
		return responseArr.size(); 
	}

	public ArrayList<BaseResponse> getResponseArray() {
		return responseArr;
	} 

	public BaseResponse getResponse(int pos) {
		if (pos >= responseArr.size()) {
			throw new RuntimeException("Bad pos = " + pos + ";  Arr len = " + responseArr.size());
		}
		return responseArr.get(pos);
	}
	
}

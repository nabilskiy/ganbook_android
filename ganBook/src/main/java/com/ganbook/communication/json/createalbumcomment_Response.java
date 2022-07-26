package com.ganbook.communication.json;

import com.google.gson.annotations.SerializedName;

public class createalbumcomment_Response extends BaseResponse {

	public String getComment_id() {
		return comment_id;
	}

	public void setComment_id(String comment_id) {
		this.comment_id = comment_id;
	}

	@SerializedName("comment_id")
	public String comment_id;

	@Override
	public String toString() {
		return "createalbumcomment_Response{" +
				"comment_id='" + comment_id + '\'' +
				'}';
	}
}

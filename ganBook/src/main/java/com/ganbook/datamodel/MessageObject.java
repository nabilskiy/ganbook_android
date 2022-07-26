package com.ganbook.datamodel;

public class MessageObject {
	private String msg;
	private String date;
	private String viewed;

	public MessageObject(String mssg, String dates, String v) {
		this.msg = mssg;
		this.date = dates;
		this.viewed = v;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getViewed() {
		return viewed;
	}

	public void setViewed(String viewed) {
		this.viewed = viewed;
	}

}

package com.ganbook.communication.json;

import com.ganbook.communication.datamodel.ClassDetails;
import com.ganbook.communication.datamodel.GanDetails;

import java.io.Serializable;

public class createkid_Response extends BaseResponse implements Serializable {
	public String kid_id; //": "16163",
	public String current_year; //": "2015"
	public GanDetails gan;
	public ClassDetails[] classes;
}
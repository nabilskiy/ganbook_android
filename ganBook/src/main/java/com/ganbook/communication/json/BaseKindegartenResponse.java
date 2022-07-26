package com.ganbook.communication.json;

import com.ganbook.communication.datamodel.ClassDetails;
import com.ganbook.communication.datamodel.GanDetails;


public abstract class BaseKindegartenResponse extends BaseResponse {
	public GanDetails gan;
	public ClassDetails[] classes;
}
package com.ganbook.communication.json;

import com.ganbook.utils.StrUtils;

public class getparentsclass_response extends BaseResponse {
	public String parent_id;
	public String parent_first_name;
	public String parent_last_name;
	public String type;
	public String vaad_type;

	public String getName() {
		return StrUtils.emptyIfNull(parent_first_name) + " " + StrUtils.emptyIfNull(parent_last_name);
	}
}
package com.ganbook.communication.json;

import com.ganbook.app.MyApp;
import com.ganbook.utils.StrUtils;

public class getvaadclasses_response extends BaseResponse {
	public String class_id;
	public String class_name;
	public String parent_name;

	public ParentDetails[] parents;

	public transient boolean isHeader = false;

	public class ParentDetails {
		public String parent_first_name;
		public String parent_last_name;
		public String vaad_type;
		public String type;

		public String getName() {
			return StrUtils.emptyIfNull(parent_first_name) + " " + StrUtils.emptyIfNull(parent_last_name);
		}
	}

	public static getvaadclasses_response createHeaderItem(String class_name, String class_id) {
		// used for display only!
		getvaadclasses_response header = new getvaadclasses_response();
		header.isHeader = true;
		header.class_name = class_name;
		header.class_id = class_id;
		return header;
	}

	public static getvaadclasses_response createItem(getvaadclasses_response.ParentDetails response) {
		// used for display only!
		getvaadclasses_response item = new getvaadclasses_response();
		item.isHeader = false;
		item.parent_name = response.getName();
		return item;
	}

	public static getvaadclasses_response createEmptyItem(String msg) {
		// used for display only!
		getvaadclasses_response item = new getvaadclasses_response();
		item.isHeader = false;
		item.parent_name = msg;
		return item;
	}



}
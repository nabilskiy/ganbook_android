package com.ganbook.communication.json;

public class HistoryDetails extends BaseResponse {
	
	public String kid_id;// ": "16160",
	public String gan_id;// ": "1589",
	public String gan_name;// ": "�� ������",
	public String gan_max;// ": "50",
	public String gan_address;// ": "0542405495",
	public String gan_phone;// ": "035515408",
	public String gan_city;// ": "��",
	public String gan_code;// ": "2095",
	public String class_name;// ": "�����",
	public String class_id;// ": "2073",
	public String teacher_id;// ": "12769",
	public String teacher_mail;// ": "t700@test.com",
	public String teacher_first_name;// ": "���",
	public String teacher_last_name;// ": "���",
	public String teacher_mobile;// ": "0542566446",
	public String teacher_phone;// ": null,
	public String kid_active;// ": "1",
	public String class_year;// ": "2014",
	public String type;// ": "3"
	public boolean like_forbidden;
	public boolean comment_forbidden;
	
	public HistoryDetails(HistoryDetails history)
	{
		this.kid_id = history.kid_id;
		this.gan_id = history.gan_id;
		this.gan_name = history.gan_name;
		this.gan_max = history.gan_max;
		this.gan_address = history.gan_address;
		this.gan_phone = history.gan_phone;
		this.gan_city = history.gan_city;
		this.gan_code = history.gan_code;
		this.class_name = history.class_name;
		this.class_id = history.class_id;
		this.teacher_id = history.teacher_id;
		this.teacher_mail = history.teacher_mail;
		this.teacher_first_name = history.teacher_first_name;
		this.teacher_last_name = history.teacher_last_name;
		this.teacher_mobile = history.teacher_mobile;
		this.teacher_phone = history.teacher_phone;
		this.kid_active = history.kid_active;
		this.class_year = history.class_year;
		this.type = history.type;
		this.like_forbidden = history.like_forbidden;
		this.comment_forbidden = history.comment_forbidden;
	}
}
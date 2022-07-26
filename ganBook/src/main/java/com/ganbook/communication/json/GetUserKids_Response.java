package com.ganbook.communication.json;

import com.ganbook.utils.ActiveUtils;
import com.ganbook.utils.GenderUtils;
import com.google.gson.annotations.SerializedName;

import java.util.Arrays;

public class GetUserKids_Response extends BaseResponse {

	// see User.userkids	
	
	// returned array!
	@SerializedName("kid_name")
	public String kid_name; // ": "d901",
	@SerializedName("kid_bd")
	public String kid_bd; // ": "2009-12-31",
	@SerializedName("kid_id")
	public String kid_id; // ;;": "16160",
	@SerializedName("kid_pic")
	public String kid_pic; // ; //": "1422971712.567310-1027264439.jpg",
	@SerializedName("kid_gender")
	public String kid_gender; // ": "0",
	@SerializedName("gan_id")
	public String gan_id; // ": "1589",
	@SerializedName("gan_name")
	public String gan_name; // ": "גן פרפרים",
	@SerializedName("gan_max")
	public String gan_max; // ": "50",
	@SerializedName("gan_address")
	public String gan_address; // ": "0542405495",
	@SerializedName("gan_phone")
	public String gan_phone; // ": "035515408",
	@SerializedName("gan_city")
	public String gan_city; // ": "תא",
	@SerializedName("gan_code")
	public String gan_code; // ": "2095",
	@SerializedName("class_name")
	public String class_name; // ": "גחליליות",
	@SerializedName("class_id")
	public String class_id; // ": "2973",
	@SerializedName("teacher_id")
	public String teacher_id; // ": "12769",
	@SerializedName("teacher_mail")
	public String teacher_mail; // ": "t700@test.com",
	@SerializedName("teacher_first_name")
	public String teacher_first_name; // ": "דנה",
	@SerializedName("teacher_last_name")
	public String teacher_last_name; // ": "כהן",
	@SerializedName("teacher_mobile")
	public String teacher_mobile; // ": "0542566446",
	@SerializedName("teacher_phone")
	public String teacher_phone; // ": null,
	@SerializedName("kid_active")
	public String kid_active; // ": "1"==APPROVED  "2"==WAITINT-APPROVA
	@SerializedName("class_year")
	public String class_year; // ": "2015",
	@SerializedName("type")
	public String type; // ": "3",
	@SerializedName("current_year")
	public String current_year; // ": "2015",
	@SerializedName("unread_messages")
	public String unread_messages; // ": "0",
	@SerializedName("like_forbidden")
	public boolean like_forbidden; // ": "30",
	@SerializedName("comment_forbidden")
	public boolean comment_forbidden; // ": "30",
	@SerializedName("contact_forbidden")
	public boolean contactsForbidden; // ": "30",
	@SerializedName("unseen_photos")
	public String unseen_photos; // ": "30",
	@SerializedName("vaad_type")
	public String vaad_type; // ": "0",
	@SerializedName("history")
	public HistoryDetails[] history;

	@SerializedName("class_latitude")
	public Double latitude;
	@SerializedName("class_longitude")
	public Double longitude;

	@SerializedName("album_name")
	public String drawingName;

	@SerializedName("drawing_album_id")
	public String drawingAlbumId;

	@SerializedName("meetings_active")
	public String meetingActive;
	
//	public boolean code_was_sent;
	
	public GetUserKids_Response(GetUserKids_Response kid)
	{
		this.kid_name = kid.kid_name;
		this.kid_bd = kid.kid_bd;
		this.kid_id = kid.kid_id;
		this.kid_pic = kid.kid_pic;
		this.kid_gender = kid.kid_gender;
		this.gan_id = kid.gan_id;
		this.gan_name = kid.gan_name;
		this.gan_max = kid.gan_max;
		this.gan_address = kid.gan_address;
		this.gan_phone = kid.gan_phone;
		this.gan_city = kid.gan_city;
		this.gan_code = kid.gan_code;
		this.class_name = kid.class_name;
		this.class_id = kid.class_id;
		this.teacher_id = kid.teacher_id;
		this.teacher_mail = kid.teacher_mail;
		this.teacher_first_name = kid.teacher_first_name;
		this.teacher_last_name = kid.teacher_last_name;
		this.teacher_mobile = kid.teacher_mobile;
		this.teacher_phone = kid.teacher_phone;
		this.kid_active = kid.kid_active;
		this.class_year = kid.class_year;
		this.type = kid.type;
		this.current_year = kid.current_year;
		this.unread_messages = kid.unread_messages;
		this.unseen_photos = kid.unseen_photos;
		this.vaad_type = kid.vaad_type;
		this.like_forbidden = kid.like_forbidden;
		this.comment_forbidden = kid.comment_forbidden;
		this.drawingName = kid.drawingName;
		this.contactsForbidden = kid.contactsForbidden;
		this.meetingActive = kid.meetingActive;

		if(kid.history != null)
		{
			this.history = new HistoryDetails[kid.history.length]; 
			for (int i = 0; i < kid.history.length; i++) {
				this.history[i] = new HistoryDetails(kid.history[i]);
			}
		}
	}
	
	public boolean codeWasSent() {
		return kid_active != null;
	}
	
	public boolean isActive() {
		return ActiveUtils.isActive(kid_active);
	}

	
	@Override
	public void postLoad() {
		if (history != null) {
			for (HistoryDetails h: history) {
				h.postLoad();
			}
		}			
	} 

	public void markAsWaitingApproval() {
		kid_active = ActiveUtils.WAITING_APPROVAL;
	}

	public boolean isBoy() {
		if (GenderUtils.BOY.equals(kid_gender)) {
			return true;
		}
		else {
			return false;
		}
	}

	@Override
	public String toString() {
		return "GetUserKids_Response{" +
				"kid_name='" + kid_name + '\'' +
				", kid_bd='" + kid_bd + '\'' +
				", kid_id='" + kid_id + '\'' +
				", kid_pic='" + kid_pic + '\'' +
				", kid_gender='" + kid_gender + '\'' +
				", gan_id='" + gan_id + '\'' +
				", gan_name='" + gan_name + '\'' +
				", gan_max='" + gan_max + '\'' +
				", gan_address='" + gan_address + '\'' +
				", gan_phone='" + gan_phone + '\'' +
				", gan_city='" + gan_city + '\'' +
				", gan_code='" + gan_code + '\'' +
				", class_name='" + class_name + '\'' +
				", class_id='" + class_id + '\'' +
				", teacher_id='" + teacher_id + '\'' +
				", teacher_mail='" + teacher_mail + '\'' +
				", teacher_first_name='" + teacher_first_name + '\'' +
				", teacher_last_name='" + teacher_last_name + '\'' +
				", teacher_mobile='" + teacher_mobile + '\'' +
				", teacher_phone='" + teacher_phone + '\'' +
				", kid_active='" + kid_active + '\'' +
				", class_year='" + class_year + '\'' +
				", type='" + type + '\'' +
				", current_year='" + current_year + '\'' +
				", unread_messages='" + unread_messages + '\'' +
				", like_forbidden=" + like_forbidden +
				", comment_forbidden=" + comment_forbidden +
				", contact_forbidden=" + contactsForbidden +
				", unseen_photos='" + unseen_photos + '\'' +
				", vaad_type='" + vaad_type + '\'' +
				", drawing_name='" + drawingName + '\'' +
				", meeting_active='" + meetingActive + '\'' +
				", history=" + Arrays.toString(history) +
				'}';
	}
}


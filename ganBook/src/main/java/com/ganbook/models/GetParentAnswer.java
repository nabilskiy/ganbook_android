package com.ganbook.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.ganbook.communication.json.BaseResponse;
import com.ganbook.user.User;
import com.ganbook.utils.GenderUtils;
import com.ganbook.utils.StrUtils;
import com.google.gson.annotations.SerializedName;
import com.project.ganim.R;

public class GetParentAnswer extends BaseResponse implements Parcelable {

	@SerializedName("parent_id")
	public String parent_id; //": "16222",
	@SerializedName("parent_first_name")
	public String parent_first_name; //": "P901",
	@SerializedName("parent_last_name")
	public String parent_last_name; //": "",
	@SerializedName("parent_mail")
	public String parent_mail; //": "p901@test.com",
	@SerializedName("parent_phone")
	public String parent_phone; //": "5555555",
	@SerializedName("parent_mobile")
	public String parent_mobile; //": "5555555",
	@SerializedName("parent_address")
	public String parent_address; //":
	@SerializedName("parent_city")
	public String parent_city; //":
	@SerializedName("type")
	public String type; //": "3",
	@SerializedName("kid_id")
	public String kid_id; //": "16160",
	@SerializedName("kid_active")
	public String kid_active; //": "1",
	@SerializedName("kid_name")
	public String kid_name; //": "d901",
	@SerializedName("kid_pic")
	public String kid_pic; //": "1422971712.567310-1027264439.jpg",
	@SerializedName("kid_gender")
	public String kid_gender; //": "0",
	public String kid_bd; //": "2009-12-31",
	@SerializedName("class_id")
	public String class_id;
	@SerializedName("kindergarten_id")
	public String ganId; //": "2973",
	@SerializedName("class_name")
	public String class_name; //":
	@SerializedName("vaad_type")
	public String vaad_type; //": "0"
	@SerializedName("teacher_photo")
	public String teacherPhoto; //": "0"
	@SerializedName("attendance")
	public String attendance; //": "0"

	public GetParentAnswer(){}

	protected GetParentAnswer(Parcel in) {
		parent_id = in.readString();
		parent_first_name = in.readString();
		parent_last_name = in.readString();
		parent_mail = in.readString();
		parent_phone = in.readString();
		parent_mobile = in.readString();
		parent_address = in.readString();
		parent_city = in.readString();
		type = in.readString();
		kid_id = in.readString();
		kid_active = in.readString();
		kid_name = in.readString();
		kid_pic = in.readString();
		kid_gender = in.readString();
		kid_bd = in.readString();
		class_id = in.readString();
		class_name = in.readString();
		vaad_type = in.readString();
		teacherPhoto = in.readString();
		attendance = in.readString();
	}

	public static final Creator<GetParentAnswer> CREATOR = new Creator<GetParentAnswer>() {
		@Override
		public GetParentAnswer createFromParcel(Parcel in) {
			return new GetParentAnswer(in);
		}

		@Override
		public GetParentAnswer[] newArray(int size) {
			return new GetParentAnswer[size];
		}
	};

	public String getName() {
		if (StrUtils.isEmpty(parent_first_name)) {
			return StrUtils.emptyIfNull(parent_last_name);
		}
		if (StrUtils.isEmpty(parent_last_name)) {
			return StrUtils.emptyIfNull(parent_first_name);
		}
		return StrUtils.emptyIfNull(parent_first_name) + " " + StrUtils.emptyIfNull(parent_last_name); 
	}

	public boolean isMother() {
		return User.isMotherType(type); 
	}
	
	public int getDeafultKidImg()
	{
		int defImageResId;
		
		if(kid_gender == null)
		{
			defImageResId = R.drawable.kindergarten_icon;
		}
		else if(kid_gender.equals(GenderUtils.BOY))
		{
			defImageResId = R.drawable.boydefault;
		}
		else if(kid_gender.equals(GenderUtils.GIRL))
		{
			defImageResId = R.drawable.girldefault;
		}
		else
		{
			defImageResId = R.drawable.add_pic_default;
		}
		
		return defImageResId;
	}

	public String getParent_id() {
		return parent_id;
	}

	public void setParent_id(String parent_id) {
		this.parent_id = parent_id;
	}

	public String getParent_first_name() {
		return parent_first_name;
	}

	public void setParent_first_name(String parent_first_name) {
		this.parent_first_name = parent_first_name;
	}

	public String getParent_last_name() {
		return parent_last_name;
	}

	public void setParent_last_name(String parent_last_name) {
		this.parent_last_name = parent_last_name;
	}

	public String getParent_mail() {
		return parent_mail;
	}

	public void setParent_mail(String parent_mail) {
		this.parent_mail = parent_mail;
	}

	public String getParent_phone() {
		return parent_phone;
	}

	public void setParent_phone(String parent_phone) {
		this.parent_phone = parent_phone;
	}

	public String getParent_mobile() {
		return parent_mobile;
	}

	public void setParent_mobile(String parent_mobile) {
		this.parent_mobile = parent_mobile;
	}

	public String getParent_address() {
		return parent_address;
	}

	public void setParent_address(String parent_address) {
		this.parent_address = parent_address;
	}

	public String getParent_city() {
		return parent_city;
	}

	public void setParent_city(String parent_city) {
		this.parent_city = parent_city;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getKid_id() {
		return kid_id;
	}

	public void setKid_id(String kid_id) {
		this.kid_id = kid_id;
	}

	public String getKid_active() {
		return kid_active;
	}

	public void setKid_active(String kid_active) {
		this.kid_active = kid_active;
	}

	public String getKid_name() {
		return kid_name;
	}

	public void setKid_name(String kid_name) {
		this.kid_name = kid_name;
	}

	public String getKid_pic() {
		return kid_pic;
	}

	public void setKid_pic(String kid_pic) {
		this.kid_pic = kid_pic;
	}

	public String getKid_gender() {
		return kid_gender;
	}

	public void setKid_gender(String kid_gender) {
		this.kid_gender = kid_gender;
	}

	public String getKid_bd() {
		return kid_bd;
	}

	public void setKid_bd(String kid_bd) {
		this.kid_bd = kid_bd;
	}

	public String getClass_id() {
		return class_id;
	}

	public void setClass_id(String class_id) {
		this.class_id = class_id;
	}

	public String getClass_name() {
		return class_name;
	}

	public void setClass_name(String class_name) {
		this.class_name = class_name;
	}

	public String getVaad_type() {
		return vaad_type;
	}

	public void setVaad_type(String vaad_type) {
		this.vaad_type = vaad_type;
	}

	@Override
	public String toString() {
		return "GetParentAnswer{" +
				"parent_id='" + parent_id + '\'' +
				", parent_first_name='" + parent_first_name + '\'' +
				", parent_last_name='" + parent_last_name + '\'' +
				", parent_mail='" + parent_mail + '\'' +
				", parent_phone='" + parent_phone + '\'' +
				", parent_mobile='" + parent_mobile + '\'' +
				", parent_address='" + parent_address + '\'' +
				", parent_city='" + parent_city + '\'' +
				", type='" + type + '\'' +
				", kid_id='" + kid_id + '\'' +
				", kid_active='" + kid_active + '\'' +
				", kid_name='" + kid_name + '\'' +
				", kid_pic='" + kid_pic + '\'' +
				", kid_gender='" + kid_gender + '\'' +
				", kid_bd='" + kid_bd + '\'' +
				", class_id='" + class_id + '\'' +
				", class_name='" + class_name + '\'' +
				", vaad_type='" + vaad_type + '\'' +
				//", attendance='" + attendance + '\'' +
				'}';
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(parent_id);
		dest.writeString(parent_first_name);
		dest.writeString(parent_last_name);
		dest.writeString(parent_mail);
		dest.writeString(parent_phone);
		dest.writeString(parent_mobile);
		dest.writeString(parent_address);
		dest.writeString(parent_city);
		dest.writeString(type);
		dest.writeString(kid_id);
		dest.writeString(kid_active);
		dest.writeString(kid_name);
		dest.writeString(kid_pic);
		dest.writeString(kid_gender);
		dest.writeString(kid_bd);
		dest.writeString(class_id);
		dest.writeString(class_name);
		dest.writeString(vaad_type);
	}
}
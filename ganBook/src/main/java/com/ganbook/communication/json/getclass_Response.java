package com.ganbook.communication.json;

import com.ganbook.communication.datamodel.ClassDetails;
import com.ganbook.communication.datamodel.ClassDetails_2;
import com.ganbook.communication.datamodel.GanDetails;
import com.ganbook.user.User;

import java.util.Arrays;

public class getclass_Response extends BaseResponse implements Response_With_User {
	

	public String gan_video;
	public String gan_max;
	public String gan_id;
	public String gan_name;
	public String gan_code;
	public String pic;
	public String gan_phone;
	public String gan_address;
	public String gan_city;
	public String teacher_id;
	public String teacher_mail;
	public String teacher_first_name;
	public String teacher_last_name;
	public String teacher_mobile;
	public String teacher_phone;
	public String teacher_address;
	public String teacher_city;
	public String current_year;
	public String meetings_active;
	public ClassDetails_2[] classes;
	
	public static getclass_Response fromKindegartenResponse(BaseKindegartenResponse response) {
		if (response==null) {
			return null;
		}
		getclass_Response c_res = new getclass_Response();
		GanDetails orig_gan = response.gan;
		if (orig_gan != null) {
			c_res.gan_id = orig_gan.gan_id;
			c_res.gan_name = orig_gan.gan_name;
			c_res.gan_code = orig_gan.gan_code;
			c_res.gan_phone = orig_gan.gan_phone;
			c_res.gan_address = orig_gan.gan_address;
			c_res.gan_city = orig_gan.gan_city;
		}
		
		ClassDetails[] orig_classes = response.classes;
		int len = orig_classes.length;
		c_res.classes = new ClassDetails_2[len];
		
		if (orig_classes != null) {
			int ind = 0;
			if(User.current.classList != null) {
				for (ClassDetails_2 classDetails_2 : User.current.classList.classes) {
					c_res.classes[ind] = classDetails_2;

					ind++;
				}
			}
			
			
			for (ClassDetails classDetails : orig_classes) {
				boolean found = false;
				
				for (ClassDetails_2 classDetails_2 : c_res.classes) 
				{
					if(classDetails_2 != null)
					{
						if(classDetails.class_id.equals(classDetails_2.class_id))
						{
							found = true;
							break;
						}
					}
					
				}
				if(!found)
				{
					c_res.classes[len-1] = new ClassDetails_2(classDetails);
					break;
				}
				
			}

		}		
		
		return c_res;
	}

	@Override
	public String toString() {
		return "getclass_Response{" +
				"gan_video='" + gan_video + '\'' +
				", gan_max='" + gan_max + '\'' +
				", gan_id='" + gan_id + '\'' +
				", gan_name='" + gan_name + '\'' +
				", gan_code='" + gan_code + '\'' +
				", pic='" + pic + '\'' +
				", gan_phone='" + gan_phone + '\'' +
				", gan_address='" + gan_address + '\'' +
				", gan_city='" + gan_city + '\'' +
				", teacher_id='" + teacher_id + '\'' +
				", teacher_mail='" + teacher_mail + '\'' +
				", teacher_first_name='" + teacher_first_name + '\'' +
				", teacher_last_name='" + teacher_last_name + '\'' +
				", teacher_mobile='" + teacher_mobile + '\'' +
				", teacher_phone='" + teacher_phone + '\'' +
				", teacher_address='" + teacher_address + '\'' +
				", teacher_city='" + teacher_city + '\'' +
				", current_year='" + current_year + '\'' +
				", meetings_active='" + meetings_active + '\'' +
				", classes=" + Arrays.toString(classes) +
				'}';
	}
}

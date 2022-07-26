package com.ganbook.communication.json;

import com.ganbook.utils.GenderUtils;
import com.project.ganim.R;


public class getgankids_response extends BaseResponse{
	
		public String class_id;
		public String class_name;
		public KidDetails[] kids;
		
		public class KidDetails {
			public String kid_id;
			public String kid_name;
			public String kid_pic;
			public String kid_gender;
				
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
		}
}

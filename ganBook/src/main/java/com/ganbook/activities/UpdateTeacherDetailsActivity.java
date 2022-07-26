package com.ganbook.activities;

import android.app.ActionBar;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ganbook.amazinglist.special.CustomToast;
import com.ganbook.app.MyApp;
import com.ganbook.communication.ICompletionHandler;
import com.ganbook.communication.datamodel.KidDetails;
import com.ganbook.communication.datamodel.ResultObj;
import com.ganbook.communication.json.GetUserKids_Response;
import com.ganbook.communication.json.transmitter.JsonTransmitter;
import com.ganbook.debug.InDebug;
import com.ganbook.fragments.ContactListFragment;
import com.ganbook.models.events.RefreshDrawerEvent;
import com.ganbook.models.events.UpdateKidsEvent;
import com.ganbook.services.UpdateKidPicService;
import com.ganbook.ui.CircleImageView;
import com.ganbook.user.NameAndId;
import com.ganbook.user.User;
import com.ganbook.utils.Const;
import com.ganbook.utils.GenderUtils;
import com.ganbook.utils.ImageUtils;
import com.ganbook.utils.KeyboardUtils;
import com.ganbook.utils.StrUtils;

import com.project.ganim.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

public class UpdateTeacherDetailsActivity extends BaseAppCompatActivity implements OnClickListener {
	
	// edit_parent_fragment 
	
	
	private EditText schoolName, name, home_number, mobile, address, city;
	
	private String _name ; 
	private String _phone ;
	private String _mobile ;
	private String _address ;
	private String _city ;
	private String _schoolName;
	private boolean isParent;
	private EditText [] classEditTextArr;
	
	private LinearLayout class_cap_1, class_cap_2, class_cap_3, class_cap_4, class_cap_5, 
				class_cap_6, class_cap_7, class_cap_8, school_root, main;
	private EditText class_1, class_2, class_3, class_4, class_5, class_6, class_7, class_8;
	
	
	private RelativeLayout kid_cap_1, kid_cap_2, kid_cap_3, kid_cap_4, kid_cap_5;
	private CircleImageView profile_image_1,profile_image_2,profile_image_3,profile_image_4,profile_image_5;
	private TextView kid_name_1, kid_bd_1, kid_name_2, kid_bd_2, kid_name_3, kid_bd_3, kid_name_4, kid_bd_4, kid_name_5, kid_bd_5;
	private TextView kid_gender_1, kid_gender_2, kid_gender_3, kid_gender_4, kid_gender_5;
	
	public static GetUserKids_Response[] updateKids;
	public static boolean[] kids_pic_updated;
	private String TAG = UpdateTeacherDetailsActivity.class.getName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.edit_parent_fragment);
		setActionBar("", true);

		school_root = (LinearLayout) findViewById(R.id.school_root);
		main = findViewById(R.id.main);

		schoolName = (EditText) findViewById(R.id.school_name);
		name = (EditText) findViewById(R.id.name); 
		home_number = (EditText) findViewById(R.id.home_number);
		mobile = (EditText) findViewById(R.id.mobile);
		address = (EditText) findViewById(R.id.address);
		city = (EditText) findViewById(R.id.city); 
		
        TextView home_number_caption = (TextView) findViewById(R.id.home_number_caption);
        if (User.isTeacher()) {
        	home_number_caption.setText(R.string.kindergarten_phone2);
        }
		
		__gone(class_cap_1 = (LinearLayout) findViewById(R.id.class_cap_1));
		__gone(class_cap_2 = (LinearLayout) findViewById(R.id.class_cap_2));
		__gone(class_cap_3 = (LinearLayout) findViewById(R.id.class_cap_3));
		__gone(class_cap_4 = (LinearLayout) findViewById(R.id.class_cap_4));
		__gone(class_cap_5 = (LinearLayout) findViewById(R.id.class_cap_5));
		__gone(class_cap_6 = (LinearLayout) findViewById(R.id.class_cap_6));
		__gone(class_cap_7 = (LinearLayout) findViewById(R.id.class_cap_7));
		__gone(class_cap_8 = (LinearLayout) findViewById(R.id.class_cap_8));
		
		//__gone(kid_cap_1 = (RelativeLayout) findViewById(R.id.kid_cap_1));
		__gone(kid_cap_1 = (RelativeLayout) findViewById(R.id.kid_cap_1));
		__gone(kid_cap_2 = (RelativeLayout) findViewById(R.id.kid_cap_2));
		__gone(kid_cap_3 = (RelativeLayout) findViewById(R.id.kid_cap_3));
		__gone(kid_cap_4 = (RelativeLayout) findViewById(R.id.kid_cap_4));
		__gone(kid_cap_5 = (RelativeLayout) findViewById(R.id.kid_cap_5));
		
		profile_image_1 = (CircleImageView) findViewById(R.id.profile_image_1);
		kid_name_1 = (TextView) findViewById(R.id.kid_name_1);
		kid_bd_1 = (TextView) findViewById(R.id.kid_bd_1);
		kid_gender_1 = (TextView) findViewById(R.id.kid_gender_1);
		
		profile_image_2 = (CircleImageView) findViewById(R.id.profile_image_2);
		kid_name_2 = (TextView) findViewById(R.id.kid_name_2);
		kid_bd_2 = (TextView) findViewById(R.id.kid_bd_2);
		kid_gender_2 = (TextView) findViewById(R.id.kid_gender_2);
		
		profile_image_3 = (CircleImageView) findViewById(R.id.profile_image_3);
		kid_name_3 = (TextView) findViewById(R.id.kid_name_3);
		kid_bd_3 = (TextView) findViewById(R.id.kid_bd_3);
		kid_gender_3 = (TextView) findViewById(R.id.kid_gender_3);
		
		profile_image_4 = (CircleImageView) findViewById(R.id.profile_image_4);
		kid_name_4 = (TextView) findViewById(R.id.kid_name_4);
		kid_bd_4 = (TextView) findViewById(R.id.kid_bd_4);
		kid_gender_4 = (TextView) findViewById(R.id.kid_gender_4);
		
		profile_image_5 = (CircleImageView) findViewById(R.id.profile_image_5);
		kid_name_5 = (TextView) findViewById(R.id.kid_name_5);
		kid_bd_5 = (TextView) findViewById(R.id.kid_bd_5);
		kid_gender_5 = (TextView) findViewById(R.id.kid_gender_5);
		
		__gone(class_1 = (EditText) findViewById(R.id.class_1));
		__gone(class_2 = (EditText) findViewById(R.id.class_2));
		__gone(class_3 = (EditText) findViewById(R.id.class_3));
		__gone(class_4 = (EditText) findViewById(R.id.class_4));
		__gone(class_5 = (EditText) findViewById(R.id.class_5));
		__gone(class_6 = (EditText) findViewById(R.id.class_6));
		__gone(class_7 = (EditText) findViewById(R.id.class_7));
		__gone(class_8 = (EditText) findViewById(R.id.class_8));
		
		profile_image_1 = (CircleImageView) findViewById(R.id.profile_image_1);
		kid_name_1 = (TextView) findViewById(R.id.kid_name_1);
	    kid_bd_1 = (TextView) findViewById(R.id.kid_bd_1);
	    kid_gender_1 = (TextView) findViewById(R.id.kid_gender_1);
		
		if (InDebug.NO_NETWORK) {
			String[] classNames = {"beginn sdf", "sdfsdf", "sdfsd", "sofff"};
			if (classNames != null) {
				int len = classNames.length;
				for (int i = 0; i < len; i++) {
					setClassControls(i+1, classNames[i]);
				} 
			}		
		}
		else {
			
			isParent = User.isParent();
			
			_name = User.getName();
			
			if(isParent)
			{
				setUpdateKids();
				setkidsPicUpdated();
				
				_phone = User.current.phone;
				_mobile = User.current.mobile;
				_address = User.current.address;
				_city = User.current.city;
				
				kid_cap_1.setOnClickListener(this);
				kid_cap_2.setOnClickListener(this);
				kid_cap_3.setOnClickListener(this);
				kid_cap_4.setOnClickListener(this);
				kid_cap_5.setOnClickListener(this);
			}
			else
			{
				school_root.setVisibility(View.VISIBLE);

				String[] classNames = User.current.getClassNameArray();

				RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 130);
				layoutParams.setMargins(60, 20, 60, 0);
				layoutParams.addRule(RelativeLayout.BELOW, R.id.city);

				if (classNames != null) {
					int len = classNames.length;
					classEditTextArr = new EditText[len];
					for (int i = 0; i < len; i++) {
						TextView textView = new TextView(this);
						textView.append(getString(R.string.class_name));
						textView.setGravity(Gravity.CENTER_HORIZONTAL);
						textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
						textView.setTextColor(Color.parseColor("#66CCFF"));
						main.addView(textView);
						classEditTextArr[i] = new EditText(this);
						classEditTextArr[i].setLayoutParams(layoutParams);
						classEditTextArr[i].setText(classNames[i]);
						classEditTextArr[i].setBackgroundResource(R.drawable.edit_bg);
						classEditTextArr[i].setPadding(10, 10, 10, 10);
						classEditTextArr[i].setHintTextColor(getResources().getColor(R.color.mdtp_dark_gray));
						main.addView(classEditTextArr[i]);

					}
				}
				
				_phone = User.current.classList.gan_phone;
				_mobile = User.current.classList.teacher_mobile;
				_address = User.current.classList.gan_address;
				_city = User.current.classList.gan_city;
				_schoolName = User.current.classList.gan_name;
			}
			
		}
		
		name.setText(_name); 
		home_number.setText(_phone);
		mobile.setText(_mobile);
		address.setText(_address);
		city.setText(_city);
		schoolName.setText(_schoolName);
		
		int captionResId;
		if(User.isStaff()) {
			captionResId = R.string.edit_staff_header;
		} else if (User.isStudent()) {
			captionResId = R.string.edit_student_text;
		} else if (User.isParent()) {
			captionResId = R.string.edit_parent_header;
		} else {
			captionResId = R.string.edit_teacher_details;
		}		
		setToolbarTitle(getString(captionResId));
	}

	private int convertPixelsToDp(float px, Context context){
		Resources resources = context.getResources();
		DisplayMetrics metrics = resources.getDisplayMetrics();
		int dp = (int) (px / (metrics.densityDpi / 160f));
		return dp;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.save_button_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {

			case R.id.save_button:

				_name = name.getText().toString().trim();
				_phone = home_number.getText().toString().trim();
				_mobile = mobile.getText().toString().trim();
				_address = address.getText().toString().trim();
				_city = city.getText().toString().trim();
				_schoolName = schoolName.getText().toString().trim();
				KeyboardUtils.close(UpdateTeacherDetailsActivity.this, name);

				if (isParent) {
					call_updateParent();
//					call_uploadKidsPic();
				} else {
					call_updateTeacher();
				}
				break;
			case android.R.id.home:
				onBackPressed();
				break;
		}

		return super.onOptionsItemSelected(item);
	}
	
	private void setkidsPicUpdated() {
		kids_pic_updated = new boolean[User.current.userkids.length];
		
		for (int i = 0; i < kids_pic_updated.length; i++) {
			kids_pic_updated[i] = false;			
		} 
	}

	private void fillFields()
	{
		int ind = 0;
		
		for (GetUserKids_Response kid : updateKids) {
			
			setKidsControls(ind, kid.kid_name, kid.kid_bd, kid.kid_pic, kid.kid_gender);
			ind++;
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		if(isParent)
		{
			fillFields();
		}
	}
	
	private void setUpdateKids()
	{

		updateKids = new GetUserKids_Response[User.current.userkids.length];
		
		 for ( int i = 0; i < updateKids.length; ++i ) {
			 updateKids[i] = new GetUserKids_Response(User.current.userkids[i]);
		 }
		
	}
	
	private void getKidGender(String kid_gender, TextView btn)
	{
		if(GenderUtils.BOY.equals(kid_gender))
		{
			btn.setBackgroundResource(R.drawable.boy_bg);
			btn.setText(getString(R.string.boy));
		}
		else if(GenderUtils.GIRL.equals(kid_gender))
		{
			btn.setBackgroundResource(R.drawable.girl_bg);
			btn.setText(getString(R.string.girl));
		}
		else
		{
			btn.setBackgroundResource(R.drawable.girl_bg);
			btn.setText(getString(R.string.girl));
		}
	}
	
	private void setKidPic(int ind, String kidPic, CircleImageView imageView, String kidGender)
	{
		if(kids_pic_updated[ind])
		{
			if (kidPic != null) {
				File file = new File(kidPic);
				Bitmap bitmap = ImageUtils.decodeFile(file);
				imageView.setImageBitmap(bitmap);
			}
		}
		else
		{
			if(kidPic == null)
			{
				int defImgRes = User.getDeafultKidImg(kidGender);
				imageView.setImageResource(defImgRes);
			}
			else
			{
				String url = ImageUtils.kidPicToUrl(kidPic);
				ImageUtils.getKidPicture(url,imageView,0);
			}
		}
	}
	
	private void setKidsControls(int ind, String kidName, String kidBd, String kidPic, String kidGender) {
		switch (ind) {
		case 0:
			__makeVisible(kid_cap_1);
			
			setKidPic(ind, kidPic, profile_image_1, kidGender);
			
			kid_name_1.setText(kidName);
			kid_bd_1.setText(kidBd);	
			getKidGender(kidGender, kid_gender_1);
			break;
			
		case 1:
			__makeVisible(kid_cap_2);
			
			setKidPic(ind, kidPic, profile_image_2, kidGender);
			
			kid_name_2.setText(kidName);
			kid_bd_2.setText(kidBd);	
			getKidGender(kidGender, kid_gender_2);
			break;
			
		case 2:
			__makeVisible(kid_cap_3);
			
			setKidPic(ind, kidPic, profile_image_3, kidGender);
			
			kid_name_3.setText(kidName);
			kid_bd_3.setText(kidBd);	
			getKidGender(kidGender, kid_gender_3);
			break;
			
		case 3:
			__makeVisible(kid_cap_4);
			
			setKidPic(ind, kidPic, profile_image_4, kidGender);
			
			kid_name_4.setText(kidName);
			kid_bd_4.setText(kidBd);	
			getKidGender(kidGender, kid_gender_4);
			break;
			
		case 4:
			__makeVisible(kid_cap_5);
			
			setKidPic(ind, kidPic, profile_image_5, kidGender);
			
			kid_name_5.setText(kidName);
			kid_bd_5.setText(kidBd);	
			getKidGender(kidGender, kid_gender_5);
			break;
			
		default:
			int jj=234;
			jj++;
		}
	}
		
	
	private void setClassControls(int ind, String className) {
		switch (ind) {
		case 1:
			__makeVisible(class_cap_1);
			__makeVisible(class_1);
			class_1.setText(className);
			break;
			
		case 2:
			__makeVisible(class_cap_2);
			__makeVisible(class_2);
			class_2.setText(className);
			break;
			
		case 3:
			__makeVisible(class_cap_3);
			__makeVisible(class_3);
			class_3.setText(className);
			break;
			
		case 4:
			__makeVisible(class_cap_4);
			__makeVisible(class_4);
			class_4.setText(className);
			break;
			
		case 5:
			__makeVisible(class_cap_5);
			__makeVisible(class_5);
			class_5.setText(className);
			break;
			
		case 6:
			__makeVisible(class_cap_6);
			__makeVisible(class_6);
			class_6.setText(className);
			break;
			
		case 7:
			__makeVisible(class_cap_7);
			__makeVisible(class_7);
			class_7.setText(className);
			break;
			
		case 8:
			__makeVisible(class_cap_8);
			__makeVisible(class_8);
			class_8.setText(className);
			break;

		default:
			int jj=234;
			jj++;
		}
	}


	private static void __makeVisible(View v) {
		v.setVisibility(View.VISIBLE);		
	}


	private static void __gone(View v) {
		v.setVisibility(View.GONE);   
	}


	protected void call_updateTeacher() {
		call_updatekindergarten();
	}
	
	private void call_updatekindergarten() {
		final String gan_id = User.current.getCurrentGanId();
		final String gan_name = _schoolName;
		final String teacher_name = _name;
		final String teacher_mobile = _mobile; 
		final String gan_phone = _phone;
		final String gan_address = _address;
		final String gan_city = _city; 
		final JSONArray classes = getJsonClass();
		
		startProgress(R.string.operation_proceeding);
				
		final UpdateTeacherDetailsActivity a = this;
		
		JsonTransmitter.send_updatekindergarten(
				gan_id, gan_name, gan_phone, gan_address,
				gan_city, classes, teacher_name, teacher_mobile, 
				new ICompletionHandler() {			
			@Override
			public void onComplete(ResultObj result) {
				stopProgress();
				if (!result.succeeded) {
					if(JsonTransmitter.NO_NETWORK_MODE.equals(result.result))
					{
						showNotInternetAlert();
						return;
					}
					CustomToast.show(a, result.result); 
				} else {
					String[] classNames = getClassNames();  
					User.updateTeacher(gan_name, teacher_name, teacher_mobile, gan_phone,
							gan_address, gan_city, classNames);
					CustomToast.show(a, R.string.operation_succeeded);
					updateData();
					EventBus.getDefault().postSticky(new RefreshDrawerEvent());
					a.finish();
				}
			}
		});
		
	}



	@Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
	public void onReceiveUpdateKids(UpdateKidsEvent updateKidsEvent) {

		Log.d(TAG, "onReceiveUpdateKids: upd = " + updateKidsEvent);

		call_updateParent();
		stopProgress();
		EventBus.getDefault().removeStickyEvent(updateKidsEvent);
	}

	protected String[] getClassNames() {
		String[] orig_classNames = User.current.getClassNameArray();
		if (orig_classNames==null  || orig_classNames.length==0) {
			return new String[0];
		}
		int len = orig_classNames.length;
		String[] new_classNames = new String[len]; 
		for (int i = 0; i < len; i++) {
			new_classNames[i] = getNewClassName(i);
		} 
		return new_classNames;
	}
	
	private String getNewClassName(int pos) {
		EditText[] classNameArr = classEditTextArr;
		if (pos >= classNameArr.length) {
			return "";
		}
		return classNameArr[pos].getText().toString().trim();
	}
	
	
	private JSONArray getJsonClass() {
		NameAndId[] classArr = User.current.getClassNameIdArray();
		JSONArray jsonArray = new JSONArray();
		
		int ind = 0;
		
		for (EditText c: classEditTextArr) {
			
			if (c.getVisibility() == View.VISIBLE) {
				String c_name = c.getText().toString().trim();
				if (StrUtils.notEmpty(c_name)) {
					JSONObject jsonObject = new JSONObject();
					
					try {
						jsonObject.put("id", classArr[ind].id);
						jsonObject.put("name", JsonTransmitter.UtfEncode(c_name));
						
						jsonArray.put(jsonObject);
						
					} catch (JSONException e) {

						e.printStackTrace();
					}
				}
			}
			
			ind++;
		}
		
		return jsonArray;
	}

	private BroadcastReceiver updateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {

			boolean status =intent.getExtras().getBoolean(Const.SUCCESS_STATUS);

			Log.d(TAG, "onReceive: received update status = " + status);

			stopProgress();

			int id = intent.getExtras().getInt("id");
			updateKids[id].kid_pic = intent.getExtras().getString(UpdateKidPicService.EXTRA_KID_PIC_NAME);
			User.updateWithUserkids(updateKids);
			updateData();
			CustomToast.show(UpdateTeacherDetailsActivity.this, R.string.operation_succeeded);
			kids_pic_updated[id] = false;

			if (status) {

			} else {

				CustomToast.show(UpdateTeacherDetailsActivity.this, intent.getExtras().getString(Const.UPDATE_MESSAGE));
			}
		}
	};

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onStop() {

		super.onStop();
	}

	protected void call_uploadKidsPic() {
		final UpdateTeacherDetailsActivity a = this;
		for (int i = 0; i < kids_pic_updated.length; i++) {
			
			final int ind = i;
			
			if(kids_pic_updated[i])
			{
				final String picName = updateKids[i].kid_id + System.currentTimeMillis();

				Intent intent = new Intent(this, UpdateKidPicService.class);
				intent.putExtra(UpdateKidPicService.EXTRA_KID_PIC_NAME, picName);
				intent.putExtra(UpdateKidPicService.EXTRA_KID_ID, updateKids[i].kid_id);
				intent.putExtra(UpdateKidPicService.EXTRA_KID_PIC_PATH, updateKids[i].kid_pic);
				intent.putExtra("id", i);
				startService(intent);

			}
		}
	}

	protected void  call_updateParent() {
		final UpdateTeacherDetailsActivity a = this;
		String parent_id = User.getId();
		final String parent_name = _name;
		final String parent_mobile = _mobile;
		final String parent_phone = _phone;
		final String parent_address = _address;
		final String parent_city = _city;


		startProgress(R.string.operation_proceeding);
		JsonTransmitter.send_updateparent(parent_id, parent_name, parent_mobile,
				parent_phone, parent_address, parent_city, getJsonKid(), 
				new ICompletionHandler() {			
			@Override
			public void onComplete(ResultObj result) {
				
				if (!result.succeeded) {
					stopProgress();
					if(JsonTransmitter.NO_NETWORK_MODE.equals(result.result))
					{
						showNotInternetAlert();
						return;
					}
					
					CustomToast.show(a, result.result); 
					return;
				} else {
					stopProgress();
					User.updateParent(parent_name, parent_mobile, parent_phone, parent_address, parent_city);
					User.updateWithUserkids(updateKids);

					updateData();
					
					boolean to_exit = true;
					
					for (boolean to_update : kids_pic_updated) {
						if(to_update)
						{
							to_exit = false;
							break;
						}
					}
					
					if(to_exit)
					{	
						stopProgress();
						CustomToast.show(a, R.string.operation_succeeded);
						a.finish();
					}
					else
					{
						stopProgress();
						call_uploadKidsPic();
					}
				}
			}
		});
	}
	
	private JSONArray getJsonKid() {
		RelativeLayout[] kidLayoutArr = { kid_cap_1, kid_cap_2, kid_cap_3, kid_cap_4, kid_cap_5 };
		JSONArray jsonArray = new JSONArray();
		
		int ind = 0;
		
		for (RelativeLayout c: kidLayoutArr) {
			
			if (c.getVisibility() == View.VISIBLE) {
				
				JSONObject jsonObject = new JSONObject();
				
				try {
					jsonObject.put("id", updateKids[ind].kid_id);
					jsonObject.put("name", JsonTransmitter.UtfEncode(updateKids[ind].kid_name));
					jsonObject.put("birth_date", updateKids[ind].kid_bd);
					jsonObject.put("gender", updateKids[ind].kid_gender);
					
					if(!kids_pic_updated[ind])
					{
						jsonObject.put("pic", updateKids[ind].kid_pic);	
					}
					else
					{
						jsonObject.put("pic", "");	
					}								
					
					jsonArray.put(jsonObject);
					
				} catch (JSONException e) {

					e.printStackTrace();
				}
			}
			
			ind++;
		}
		
		return jsonArray;
	}
	
	private ArrayList<KidDetails> getArrKid() {
		RelativeLayout[] kidLayoutArr = { kid_cap_1, kid_cap_2, kid_cap_3, kid_cap_4, kid_cap_5 };
		ArrayList<KidDetails> kidDetails = new ArrayList<KidDetails>();
		
		int ind = 0;
		
		for (RelativeLayout c: kidLayoutArr) {
			
			if (c.getVisibility() == View.VISIBLE) {
				
				KidDetails kidDetail = new KidDetails(); 
				kidDetail.id =  updateKids[ind].kid_id;
				kidDetail.name =  JsonTransmitter.UtfEncode(updateKids[ind].kid_name);
				kidDetail.birth_date =  updateKids[ind].kid_bd;
				kidDetail.gender =  updateKids[ind].kid_gender;
				kidDetail.android_pic_path =  updateKids[ind].kid_pic;
				kidDetail.to_update_pic = kids_pic_updated[ind];
				
				kidDetails.add(kidDetail);
			}
			
			ind++;
		}
		
		return kidDetails;
	}
	
	private void updateData() {
		MainActivity.updateDrawerContent();
		// force teacher name in contactList to refresh
		ContactListFragment.forceSetTeacherName();
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.kid_cap_1:
			AddKidActivity.ind = 0; 
			AddKidActivity.selKid = updateKids[0];
			addKidTracker();
			start(AddKidActivity.class);
			break;
			
		case R.id.kid_cap_2:
			AddKidActivity.ind = 1; 
			AddKidActivity.selKid = updateKids[1];
			addKidTracker();
			start(AddKidActivity.class);
			break;
			
		case R.id.kid_cap_3:
			AddKidActivity.ind = 2; 
			AddKidActivity.selKid = updateKids[2];
			addKidTracker();
			start(AddKidActivity.class);
			break;
			
		case R.id.kid_cap_4:
			AddKidActivity.ind = 3; 
			AddKidActivity.selKid = updateKids[3];
			addKidTracker();
			start(AddKidActivity.class);
			break;
			
		case R.id.kid_cap_5:
			AddKidActivity.ind = 4; 
			AddKidActivity.selKid = updateKids[4];
			addKidTracker();
			start(AddKidActivity.class);
			break;
		default:
			int jj=234;
			jj++;
		}
	}
	
	private void addKidTracker()
	{

		MyApp.sendAnalytics("settings-edit-kid-ui", "settings-edit-kid-ui"+ User.getId(), "settings-edit-kid-ui", "SettingsEditKid");

	}


}

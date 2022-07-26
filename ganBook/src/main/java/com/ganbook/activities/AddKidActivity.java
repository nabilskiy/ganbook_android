package com.ganbook.activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ganbook.amazinglist.special.CustomToast;
import com.ganbook.app.MyApp;
import com.ganbook.communication.ICompletionHandler;
import com.ganbook.communication.datamodel.ResultObj;
import com.ganbook.communication.json.GetUserKids_Response;
import com.ganbook.communication.json.transmitter.JsonTransmitter;
import com.ganbook.models.events.RefreshDrawerEvent;
import com.ganbook.models.events.UpdateKidsEvent;
import com.ganbook.services.UpdateKidPicService;
import com.ganbook.ui.CircleImageView;
import com.ganbook.user.User;
import com.ganbook.utils.GenderUtils;
import com.ganbook.utils.ImageUtils;
import com.ganbook.utils.RealPathUtil;
import com.ganbook.utils.StrUtils;

import com.project.ganim.R;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class AddKidActivity extends BaseAppCompatActivity implements OnClickListener, com.wdullaer.materialdatetimepicker.date.DatePickerDialog.OnDateSetListener {

	private static final String TAG = AddKidActivity.class.getName();
	private static Activity inst;
	public static GetUserKids_Response selKid = null;
	public static int ind;
	public static final String tag = "AddKid";
	private static final int GALLERY_CODE = 999;
	private GetUserKids_Response kid;
	private int _ind;
	private int sel_year = -1;
	private int sel_month; 
	private int sel_day;
	public static String kidName;
	public static String kidGender;
	public static String kidBirthdate;
	public static String kidPicture;
	private TextView set_birth_date;	
    private EditText enter_kid_name;
    private RadioGroup rg_group;
    private RadioButton boy_staff_radio, girl_staff_radio, boy_radio, girl_radio;
    private CircleImageView profile_image;
    private Button disconnect_btn;
	private TextView class_name;
	private LinearLayout disconnect;
	private String parentFirstName, parentLastName, parentMobile, parentPhone, parentCity, parentAddress;
	private JSONArray kidArray;
    private String birth_date, kidId, kidPic, kidDate, kidGend;
    private boolean isKidPicUploading = false;
	private LocalBroadcastManager broadcastManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		inst = this;
		
		setContentView(R.layout.add_kid_screen);
		setActionBar("", false);

		kid = AddKidActivity.selKid;
		kidPicture = null;
		_ind = ind;

		__listen(findViewById(R.id.calander));
		__listen(findViewById(R.id.date_layout));
		__listen(findViewById(R.id.add_pic_layout));
		__listen(findViewById(R.id.profile_image));
		
	    enter_kid_name = (EditText)findViewById(R.id.enter_kid_name);
	    rg_group = (RadioGroup)findViewById(R.id.rg_group);
		boy_staff_radio = findViewById(R.id.boy_staff);
		girl_staff_radio = findViewById(R.id.girl_staff);
		boy_radio = findViewById(R.id.boy);
		girl_radio = findViewById(R.id.girl);
		set_birth_date = (TextView)findViewById(R.id.set_birth_date);
		profile_image = (CircleImageView)findViewById(R.id.profile_image);
		disconnect_btn = (Button)findViewById(R.id.disconnect_btn);

		class_name = (TextView)findViewById(R.id.class_name);

		disconnect = (LinearLayout)findViewById(R.id.disconnect);

		Intent intent = getIntent();
		if(intent.getExtras() != null) {
			kidId = intent.getStringExtra("id");
			String kidName = intent.getStringExtra("name");
			kidDate = intent.getStringExtra("birth_date");
			kidGend = intent.getStringExtra("gender");
			kidPic = intent.getStringExtra("pic");
			String className = intent.getStringExtra("className");
			String ganName = intent.getStringExtra("ganName");

			parentAddress = intent.getStringExtra("parent_address");
			parentCity = intent.getStringExtra("parent_city");
			parentMobile = intent.getStringExtra("parent_mobile");
			parentPhone = intent.getStringExtra("parent_phone");
            parentFirstName = intent.getStringExtra("parent_firstName");
			parentLastName = intent.getStringExtra("parent_lastName");

			invalidateOptionsMenu();
			setToolbarTitle(getString(R.string.edit_kid));
			class_name.setText(className + " - " + ganName);
			disconnect.setVisibility(View.VISIBLE);
			enter_kid_name.setText(kidName);
			set_birth_date.setText(kidDate);

			if(GenderUtils.GIRL.equals(kidGend))
			{
				if(User.isStaff()) {
					rg_group.check(R.id.girl_staff);
				} else {
					rg_group.check(R.id.girl);
				}
			}
			else if(GenderUtils.BOY.equals(kidGend))
			{
				if(User.isStaff()) {
					rg_group.check(R.id.boy_staff);
				} else {
					rg_group.check(R.id.boy);
				}
			}

				String url = ImageUtils.kidPicToUrl(kidPic);
				ImageUtils.getKidPicture(url,profile_image,0);

		}
		if(User.isStaff()) {

			enter_kid_name.setText(User.getName());
			enter_kid_name.setEnabled(false);

			girl_radio.setVisibility(View.GONE);
			girl_staff_radio.setVisibility(View.VISIBLE);

			boy_radio.setVisibility(View.GONE);
			boy_staff_radio.setVisibility(View.VISIBLE);
		} else if(User.isStudent()) {
			enter_kid_name.setHint(R.string.enter_student_name);
			girl_radio.setVisibility(View.VISIBLE);
			girl_staff_radio.setVisibility(View.GONE);

			boy_radio.setVisibility(View.VISIBLE);
			boy_staff_radio.setVisibility(View.GONE);
		} else {
			enter_kid_name.setHint(R.string.enter_kid_name);
			girl_radio.setVisibility(View.VISIBLE);
			girl_staff_radio.setVisibility(View.GONE);

			boy_radio.setVisibility(View.VISIBLE);
			boy_staff_radio.setVisibility(View.GONE);
		}

		disconnect_btn.setOnClickListener(new OnClickListener() {
			  @Override
			  public void onClick(View v) {
					openDisconnectPopup();
			  }
		  }
		);

		if(kid != null)
		{
            class_name.setText(kid.class_name + " - " + kid.gan_name);
            disconnect.setVisibility(View.VISIBLE);
			invalidateOptionsMenu();
			setToolbarTitle(getString(R.string.edit_kid));

			enter_kid_name.setText(kid.kid_name);
			set_birth_date.setText(kid.kid_bd);

			if(GenderUtils.GIRL.equals(kid.kid_gender))
			{
				if(User.isStaff()) {
					rg_group.check(R.id.girl_staff);
				} else {
					rg_group.check(R.id.girl);
				}
			}
			else if(GenderUtils.BOY.equals(kid.kid_gender))
			{
				if(User.isStaff()) {
					rg_group.check(R.id.boy_staff);
				} else {
					rg_group.check(R.id.boy);
				}
			}		
			
			if(UpdateTeacherDetailsActivity.kids_pic_updated[ind])
			{
				File file = new File(kid.kid_pic);
				Bitmap bitmap = ImageUtils.decodeFile(file);
				profile_image.setImageBitmap(bitmap);
			}
			else
			{
				String url = ImageUtils.kidPicToUrl(kid.kid_pic); 
				ImageUtils.getKidPicture(url,profile_image,0);
			}
		}
		else
		{
			if(getIntent().getExtras() != null) {
				disconnect.setVisibility(View.VISIBLE);
			} else {
				disconnect.setVisibility(View.GONE);
			}

            if(User.isStaff()) {
				setToolbarTitle(getString(R.string.add_staff));

			} else if(User.isStudent()) {
				setToolbarTitle(getString(R.string.add_student_text));
			} else {
				setToolbarTitle(getString(R.string.add_kid));
			}

		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.save_button_menu, menu);

		return super.onCreateOptionsMenu(menu);
	}

	private String setGender(String gender) {
        int sel_type = rg_group.getCheckedRadioButtonId();
        if (sel_type != -1) {
            if(User.isStaff()) {
                gender = (sel_type == R.id.girl_staff ? GenderUtils.GIRL : GenderUtils.BOY);
            } else {
                gender = (sel_type == R.id.girl ? GenderUtils.GIRL : GenderUtils.BOY);
            }
        }

        return gender;
    }
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {

			case R.id.save_button:

				if (kid == null && getIntent().getExtras() == null) {
					if(User.current.last_name == null) {
						kidName = enter_kid_name.getText().toString().trim();
					} else {
						kidName = enter_kid_name.getText().toString().trim() + " " + User.current.last_name;
					}

                    kidGender = setGender(kidGender);
					kidBirthdate = getDate();


					if (anyFieldEmpty()) {
						CustomToast.show(AddKidActivity.this, R.string.kid_data_incompete);
						return false;
					}

					ParentDetailsActivity.part_of_init_process = true;

					MyApp.sendAnalytics("register-parent-details-ui", "register-parent-details-ui", "register-parent-details-ui-" + User.getId(), "AddKid");

					start(ParentDetailsActivity.class);
				} else {

					if (User.isParent() || User.isStaff()) {
						if(getIntent().getExtras() == null) {
							updateUpdateKids();
							EventBus.getDefault().postSticky(new UpdateKidsEvent(true));

							finish();
						} else {
					        String gender = setGender(kidGend);
							startProgress("Updating");
							kidDate = getDate();
							kidArray = getJsonKid(kidId, enter_kid_name.getText().toString(), kidDate, gender, kidPic);

							String parent_id = User.getId();
							final String parentName = parentFirstName + " " + parentLastName;
							JsonTransmitter.send_updateparent(parent_id, parentName, parentMobile,
									parentPhone, parentAddress, parentCity, kidArray,
									result -> {

										if (!result.succeeded) {
											stopProgress();
											if(JsonTransmitter.NO_NETWORK_MODE.equals(result.result))
											{
												showNotInternetAlert();
												return;
											}

											CustomToast.show(AddKidActivity.this, result.result);
											stopProgress();
											return;
										} else {
											stopProgress();
											User.updateParent(parentName, parentMobile, parentPhone, parentAddress, parentCity);
											EventBus.getDefault().postSticky(new UpdateKidsEvent(true));
											if(isKidPicUploading) {
												uploadKidPic();
											}
											finish();
										}
									});
						}
					}
				}
				break;
		}

		return super.onOptionsItemSelected(item);
	}


	private JSONArray getJsonKid(String kidId, String kidName, String kidBirthdate, String kidGender, String kidPic) {
			JSONArray jsonArray = new JSONArray();

			JSONObject jsonObject = new JSONObject();

				try {
					jsonObject.put("id", kidId);
					jsonObject.put("name", kidName);
					jsonObject.put("birth_date", kidBirthdate);
					jsonObject.put("gender", kidGender);
					jsonObject.put("pic", kidPic);

					jsonArray.put(jsonObject);

				} catch (JSONException e) {

					e.printStackTrace();
				}

			return jsonArray;

	}

	private void openDisconnectPopup() {
		AlertDialog.Builder adb = new AlertDialog.Builder(this);
		//		adb.setView(alertDialogView);
		adb.setTitle(R.string.disconnect_text);
		adb.setIcon(R.drawable.ic_launcher);
		adb.setPositiveButton(R.string.popup_btn_ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {

				performDisconnect();
			} });
		adb.setNegativeButton(R.string.popup_btn_cancel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// no op
			} });

		adb.show();
	}

	private void performDisconnect() {
		JsonTransmitter.send_updateclass(kid.kid_id, "", result -> {
			if(result.succeeded) {
				User.updateKidDisconnect(kid.kid_id);
				MainActivity.updateDrawerContent();
				MainActivity.refreshApp();
				disconnect.setVisibility(View.GONE);
				UpdateTeacherDetailsActivity.updateKids[_ind].class_id = "";
				UpdateTeacherDetailsActivity.updateKids[_ind].kid_active = "0";
				EventBus.getDefault().postSticky(new RefreshDrawerEvent());
			}
		});
	}


	protected boolean anyFieldEmpty() {
		return StrUtils.isEmpty(kidName) || 
				StrUtils.isEmpty(kidGender) ||
				StrUtils.isEmpty(kidBirthdate);
	}
	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		selKid = null;
		if (this == inst) {
			inst = null;
		}
	}


	@Override
	public void onBackPressed() {

		super.onBackPressed();
	}

	private void updateUpdateKids() {
		UpdateTeacherDetailsActivity.updateKids[_ind].kid_name = enter_kid_name.getText().toString().trim();
		if(birth_date != null)
		{
			UpdateTeacherDetailsActivity.updateKids[_ind].kid_bd = birth_date;
		}
		
		int sel_type = rg_group.getCheckedRadioButtonId();
		UpdateTeacherDetailsActivity.updateKids[_ind].kid_gender = (sel_type == R.id.girl ? GenderUtils.GIRL : GenderUtils.BOY);
		
		if(kidPicture != null)
		{
			UpdateTeacherDetailsActivity.updateKids[_ind].kid_pic = kidPicture;
		}
	}

	
	private String getDate() {
		if (sel_year == -1) {
			return "";
		}
		String selDate = new StringBuilder().append(sel_year).
				append("-").append(sel_month + 1).append("-").append(sel_day).toString();
		return selDate;
	}

	private View __listen(View view) {
		if (view != null) {
			view.setOnClickListener(this);
		}
		return view;
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.calander:
		case R.id.date_layout:
			openDatePicker();
			break;

		case R.id.profile_image:
		case R.id.add_pic_layout:
			AddKidActivityPermissionsDispatcher.openGalleryWithCheck(this);
			break;
			
		default:
			break;
		}
	}

	@NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
	public void openGallery() {

		Intent gallery = new Intent(Intent.ACTION_PICK);
		gallery.setType("image/*");
		startActivityForResult(gallery, GALLERY_CODE);
	}

	private void openDatePicker() {
		final Calendar c = Calendar.getInstance();
		sel_year = c.get(Calendar.YEAR);
		sel_month = c.get(Calendar.MONTH);
		sel_day = c.get(Calendar.DAY_OF_MONTH);

		Calendar now = Calendar.getInstance();


		com.wdullaer.materialdatetimepicker.date.DatePickerDialog dpd = com.wdullaer.materialdatetimepicker.date.DatePickerDialog.newInstance(
				AddKidActivity.this,
				now.get(Calendar.YEAR),
				now.get(Calendar.MONTH),
				now.get(Calendar.DAY_OF_MONTH)
		);

		dpd.setVersion(DatePickerDialog.Version.VERSION_2);
		dpd.setMaxDate(now);
		dpd.showYearPickerFirst(true);
		dpd.show(getFragmentManager(), "Datepickerdialog");

	}

	@Override
	public void onDateSet(com.wdullaer.materialdatetimepicker.date.DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {

		sel_year = year;
		sel_month = monthOfYear;
		sel_day = dayOfMonth;

		GregorianCalendar calendar = new GregorianCalendar(year, monthOfYear, dayOfMonth);
		SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM, yyyy");
		formatter.setCalendar(calendar);
		String _date = formatter.format(calendar.getTime());
		set_birth_date.setText(_date);


		formatter = new SimpleDateFormat("yyyy-MM-dd");
		formatter.setCalendar(calendar);
		birth_date = formatter.format(calendar.getTime());
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == GALLERY_CODE) {
			if (resultCode == Activity.RESULT_OK && data != null) {

				Log.d(TAG, "onActivityResult: received pic = " + data.getData());
				// ?

				Uri selectedImage = data.getData();
				String realPath;

				// SDK < API11
				if (Build.VERSION.SDK_INT < 11)
					realPath = RealPathUtil.getRealPathFromURI_BelowAPI11(this, data.getData());

					// SDK >= 11 && SDK < 19
				else if (Build.VERSION.SDK_INT < 19)
					realPath = RealPathUtil.getRealPathFromURI_API11to18(this, data.getData());

					// SDK > 19 (Android 4.4)
				else
					realPath = RealPathUtil.getPath(this, selectedImage);

				String path = realPath;

				Log.d(TAG, "path = " + realPath);

				if(path == null)
				{
					return;
				}

				File file=new File(path);
				Bitmap bitmap = null;
				try {
					bitmap = ImageUtils.decodeUri(this, selectedImage, 148);
					profile_image.setImageBitmap(bitmap);
				} catch (FileNotFoundException e) {
					e.printStackTrace();

					Toast.makeText(this, R.string.error_load_pic, Toast.LENGTH_SHORT).show();
				}

				if(kid != null)
				{
					UpdateTeacherDetailsActivity.kids_pic_updated[_ind] = true;
				}
				kidPicture = path;
				kidPic = path;
				this.isKidPicUploading = true;
			}
		}
	}

	protected void uploadKidPic() {

				final String picName = kidId + System.currentTimeMillis();

				Intent intent = new Intent(this, UpdateKidPicService.class);
				intent.putExtra(UpdateKidPicService.EXTRA_KID_PIC_NAME, picName);
				intent.putExtra(UpdateKidPicService.EXTRA_KID_ID, kidId);
				intent.putExtra(UpdateKidPicService.EXTRA_KID_PIC_PATH, kidPic);
				startService(intent);

	}


	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		// NOTE: delegate the permission handling to generated method
		AddKidActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
	}




	@SuppressWarnings("unused")
	private String getRealPathFromURI(Uri uri, Context cnt) {
		String result = "";
		System.out.println("getactivity" + cnt.toString());
		Cursor cursor = cnt.getContentResolver().query(uri, null, null, null,
				null);
		if (cursor == null) {
			result = uri.getPath();
			System.out.println(result);
		}
		else {
			cursor.moveToFirst();
			int idx = cursor
					.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
			result = cursor.getString(idx);
		}
		return result;
	}

	public static void callFinish() {
		if (inst != null) {
			inst.finish();
		}
	}

	public static void cleanFields() {
		if (inst != null) {
			kidName = null;
			kidGender = null;
			kidBirthdate = null;
			kidPicture = null;
		}
	}
}

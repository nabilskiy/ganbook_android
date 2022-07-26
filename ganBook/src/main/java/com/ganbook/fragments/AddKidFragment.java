package com.ganbook.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.ganbook.activities.SignupActivity;
import com.ganbook.activities.UpdateTeacherDetailsActivity;
import com.ganbook.amazinglist.special.CustomToast;
import com.ganbook.communication.json.transmitter.JsonTransmitter;
import com.ganbook.models.SuccessAnswer;
import com.ganbook.user.User;
import com.ganbook.utils.GenderUtils;
import com.project.ganim.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressLint("SimpleDateFormat")
public class AddKidFragment extends BaseFragment implements OnClickListener {
	
	// add_kid_screen
	
	public static final String tag = "AddKid";
	private static final int GALLERY_CODE = 999;
	private static final String TAG = AddKidFragment.class.getName();

	private Context context;
	private int sel_year = -1; 
	private int sel_month;
	private int sel_day;

	private TextView set_birth_date;
	
    private EditText enter_kid_name;
    private RadioGroup rg_group;

	
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		context = activity;
	}



	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		setActionBarValues();
		

		View _view = inflater
				.inflate(R.layout.add_kid_screen, container, false);
		__listen(_view.findViewById(R.id.calander));
		__listen(_view.findViewById(R.id.date_layout));
		__listen(_view.findViewById(R.id.add_pic_layout));
		__listen(_view.findViewById(R.id.profile_image));
		
	    enter_kid_name = (EditText) _view.findViewById(R.id.enter_kid_name);
	    rg_group = (RadioGroup) _view.findViewById(R.id.rg_group);
		
		set_birth_date = (TextView) _view.findViewById(R.id.set_birth_date);		
		base_onCreateView();
		
		save_btn.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				final Activity a = activity();
				final String msg = getResources().getText(R.string.saving_kid).toString();
				String parent_name = User.getName();
				String parent_id = User.getId();
				String parent_address = User.getAddress();
				String parent_phone = User.getPhone();
				String parent_mobile = User.getMobile();
				String parent_city = User.getCity(); 
				
				String gan_code = User.current.getCurrentGanId(); 
				String kid_name = enter_kid_name.getText().toString().trim();
				int selType = rg_group.getCheckedRadioButtonId();
				String kid_gender = (selType == R.id.girl ? GenderUtils.GIRL : GenderUtils.BOY);
				String kid_bd = getDate();
				String selected_kidPic = null; //ggggggggggggFix add
				
				startProgress(R.string.operation_proceeding);

				Call<SuccessAnswer> call = ganbookApiInterfacePOST.createKid(gan_code, parent_name, parent_id, parent_address,
						parent_phone, parent_mobile, parent_city, kid_name, kid_gender, kid_bd, selected_kidPic, "");

				call.enqueue(new Callback<SuccessAnswer>() {
					@Override
					public void onResponse(Call<SuccessAnswer> call, Response<SuccessAnswer> response) {

						stopProgress();

						if (response.body() != null) {

							if (response.body().isSuccess()) {

								if (SignupActivity.createNewInProgress) {
//										SignupActivity.createNewInProgress = false; not here
									performSilentLogin();
//										activity().moveToTab(FragmentType.Edit_Parent);
									start(UpdateTeacherDetailsActivity.class);
								}
							} else {

								CustomToast.show(getActivity(), R.string.error_create_kid);
							}
						}
					}

					@Override
					public void onFailure(Call<SuccessAnswer> call, Throwable t) {

						stopProgress();
						Log.e(TAG, "onFailure: error while create kid = " + Log.getStackTraceString(t));

						CustomToast.show(getActivity(), R.string.error_create_kid);
					}
				});
			}
		});
		
		
		return _view;
	}
	
	
	private void setActionBarValues() {
		titleTextId = R.string.add_kid_here;
		saveBtnVisibility = View.VISIBLE; 
	}

	private void performSilentLogin() {
		String email = SignupActivity._email;
		String password = SignupActivity._password;

		if (email != null && password != null) {
			JsonTransmitter.send_loginnew(email, password, null);
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
			Intent gallery = null;
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
				gallery = new Intent(
						Intent.ACTION_PICK,
						android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
			} 
			else {
				gallery = new Intent();
				gallery.setType("image/*");
				gallery.setAction(Intent.ACTION_GET_CONTENT);
			}
			activity().startActivityForResult(gallery, GALLERY_CODE);
			break;
			
		default:
			break;
		}
	}

	private void openDatePicker() {
		final Calendar c = Calendar.getInstance();
		sel_year = c.get(Calendar.YEAR);
		sel_month = c.get(Calendar.MONTH);
		sel_day = c.get(Calendar.DAY_OF_MONTH);

		DatePickerDialog dpd = new DatePickerDialog(context,
				new DatePickerDialog.OnDateSetListener() {
					@Override
					public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
						sel_year = year;
						sel_month = monthOfYear;
						sel_day = dayOfMonth;
												 
						GregorianCalendar calendar = new GregorianCalendar(year, monthOfYear, dayOfMonth);						
						SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM, yyyy"); 
						formatter.setCalendar(calendar);						
						String _date = formatter.format(calendar.getTime());
						set_birth_date.setText(_date);
					}
				}, sel_year, sel_month, sel_day);
		dpd.show();
	}
	
	public void setResult(Context cnt, int requestCode, int resultCode, Intent data) {
		if (requestCode == GALLERY_CODE) {
			if (resultCode == Activity.RESULT_OK && data != null) {
				// ?	             
			}
		}
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
}

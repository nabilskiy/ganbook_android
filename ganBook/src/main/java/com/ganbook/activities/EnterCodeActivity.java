package com.ganbook.activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.ganbook.amazinglist.special.CustomToast;
import com.ganbook.app.MyApp;
import com.ganbook.communication.ICompletionHandler;
import com.ganbook.communication.datamodel.ClassDetails;
import com.ganbook.communication.datamodel.ResultObj;
import com.ganbook.communication.json.GetUserKids_Response;
import com.ganbook.communication.json.createkid_Response;
import com.ganbook.communication.json.transmitter.JsonTransmitter;
import com.ganbook.services.UpdateKidPicService;
import com.ganbook.sharedprefs.SPWriter;
import com.ganbook.user.User;
import com.ganbook.utils.Const;
import com.ganbook.utils.KeyboardUtils;
import com.ganbook.utils.StrUtils;

import com.project.ganim.R;

public class EnterCodeActivity extends BaseAppCompatActivity {
	private static final String TAG = EnterCodeActivity.class.getName();

	// activity_enter_code
	
	// test Code: 2095
	public static Activity inst;
	
	public static String kid_id;
	
    private EditText add_code;
    
    private String gan_code = "";
	boolean creating = false;
	private createkid_Response kidRes;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		inst = this;
		setContentView(R.layout.activity_enter_code);

		setActionBar(getString(R.string.caption_enter_code), false);

		add_code = (EditText) findViewById(R.id.add_code);

		invalidateOptionsMenu();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.save_button_menu, menu);

		menu.findItem(R.id.save_button).setTitle(getString(R.string.add));

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {

			case R.id.save_button:
				KeyboardUtils.close(this, add_code);
				if (add_code.length() > 2) {
					gan_code = add_code.getText().toString();
				}
				call_createKid();
				break;
		}

		return super.onOptionsItemSelected(item);
	}

	
	protected void call_createKid() {

		if (!creating) {

			creating = true;
			final EnterCodeActivity a = this;
			final String msg = getResources().getText(R.string.saving_kid).toString();
			final String parent_name = ParentDetailsActivity._name;// User.getName();
			final String parent_id = User.getId();
			final String parent_address = ParentDetailsActivity._address; //User.getAddress();
			final String parent_phone = ParentDetailsActivity._phone; //User.getPhone();
			final String parent_mobile = ParentDetailsActivity._mobile; //User.getMobile();
			final String parent_city = ParentDetailsActivity._city; //User.getCity();

			String kid_name = AddKidActivity.kidName; //enter_kid_name.getText().toString().trim();
			String kid_gender = AddKidActivity.kidGender; //(selType == R.id.girl ? PersonType.GIRL : PersonType.BOY);
			String kid_bd = AddKidActivity.kidBirthdate;

			startProgress(R.string.operation_proceeding);
			JsonTransmitter.send_createkid(
					gan_code, parent_name, parent_id, parent_address,
					parent_phone, parent_mobile, parent_city, kid_name,
					kid_gender, kid_bd, null, new ICompletionHandler() {
						@Override
						public void onComplete(ResultObj result) {
							creating = false;
							if (!result.succeeded) {
								if (JsonTransmitter.NO_NETWORK_MODE.equals(result.result)) {
									showNotInternetAlert();
									return;
								}
								String errmsg = result.result;
								MyApp.toast(errmsg, Toast.LENGTH_SHORT);
								stopProgress();
							} else {
								saveRegisterProcess();
								if (result.getNumResponses() > 0) {
									User.updateParent(parent_name, parent_mobile, parent_phone, parent_address, parent_city);

									createkid_Response _res = (createkid_Response) result.getResponse(0);
									kidRes = _res;
									kid_id = _res.kid_id;

									Log.d("KID ID", kid_id);
									String kid_pic = AddKidActivity.kidPicture;
									AddKidActivity.kidPicture = null;
									if (kid_pic != null) {
										call_uploadKidPic(kid_pic, _res);
									} else {
										contWithCodeGan(_res);
									}
								}
							}
						}
					});
		}
	}

	private void contWithCodeGan(createkid_Response _res)
	{
		final EnterCodeActivity a = this;

		if(!"".equals(gan_code))
		{
			if(_res.gan != null)
			{
				MyApp.gan_name = _res.gan.gan_name;

				MyApp.class_ids = new String[_res.classes.length];
				MyApp.class_names = new String[_res.classes.length];

				for (int i = 0; i < _res.classes.length; i++) {
					ClassDetails classDetails = _res.classes[i];

					MyApp.class_ids[i] = classDetails.class_id;
					MyApp.class_names[i] = classDetails.class_name;
				}

				MyApp.sendAnalytics("register-choose-class-ui", "register-choose-class-ui-" + User.getId(), "register-choose-class-ui", "EnterCode");

				start(ChooseClassActivity.class);
			}
			else
			{
//											CustomToast.show(a, a.getString(R.string.enter_valid_code_msg));
				MyApp.toast(a.getString(R.string.enter_valid_code_msg), Toast.LENGTH_SHORT);
				call_getKids();
				//										silent_loginnew();
			}
		}
		else
		{
			//									silent_loginnew();
			call_getKids();
		}
	}
	
	private void saveRegisterProcess() {
  		new AsyncTask<Void, Void, Void>() {
			@Override 
			protected Void doInBackground(Void... params) {
				new SPWriter("registerProcessFile").putBool("in_process", false).commit();		
				return null; 
			}
		}.execute();

	}
	
//	private void silent_loginnew() {
//		final Activity a = this;
//		String email = SignupActivity._email;
//		String password = SignupActivity._password;
//		JsonTransmitter.send_loginnew(
//				email, password,
//				new ICompletionHandler() {
//			@Override
//			public void onComplete(ResultObj result) {
//				stopProgress();
//				if (!result.succeeded) {
//					if(JsonTransmitter.NO_NETWORK_MODE.equals(result.result))
//					{
//						setContentView(R.layout.no_internet_layout);
//						return;
//					}
//					CustomToast.show(a, result.result);
//				}
//				else {
//					call_getKids();
//				}
//			}
//		});
//	}

	private BroadcastReceiver updateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {

			boolean status =intent.getExtras().getBoolean(Const.SUCCESS_STATUS);

			Log.d(TAG, "onReceive: received update status = " + status);

			contWithCodeGan(kidRes);

			if (status) {

			} else {

				CustomToast.show(EnterCodeActivity.this, intent.getExtras().getString(Const.UPDATE_MESSAGE));
			}
		}
	};

	@Override
	public void onStart() {
		super.onStart();

		LocalBroadcastManager.getInstance(this).registerReceiver((updateReceiver),
				new IntentFilter(Const.UPDATE_KID_PIC_INTENT));
	}

	@Override
	public void onStop() {

		LocalBroadcastManager.getInstance(this).unregisterReceiver(updateReceiver);
		super.onStop();
	}
	
	protected void call_uploadKidPic(String kid_pic, final createkid_Response _res) {
			if(kid_pic != null)
			{
				final String picName = kid_id + System.currentTimeMillis();
//				String kidPicture = AddKidActivity.kidPicture;
//				AddKidActivity.kidPicture = null;

				Intent intent = new Intent(this, UpdateKidPicService.class);
				intent.putExtra(UpdateKidPicService.EXTRA_KID_PIC_NAME, picName);
				intent.putExtra(UpdateKidPicService.EXTRA_KID_ID, kid_id);
				intent.putExtra(UpdateKidPicService.EXTRA_KID_PIC_PATH, kid_pic);
				startService(intent);
				
//				JsonTransmitter.send_uploadpic(kid_pic,kid_id,picName,
//						new ICompletionHandler() {
//					@Override
//					public void onComplete(ResultObj result)
//					{
//						contWithCodeGan(_res);
////						User.updateKidPic(kid_id,picName);
////						MainActivity.updateDrawerContent();
////						if(JsonTransmitter.NO_NETWORK_MODE.equals(result.result))
////						{
////							showNotInternetAlert();
////							return;
////						}
////						call_getKids();
//
//					}
//					});
			}
	}
	

 
	protected void call_getKids() {
		final EnterCodeActivity a = this;
		String userId = User.getId();
		JsonTransmitter.send_getuserkids(userId, new ICompletionHandler() {
			@Override
			public void onComplete(ResultObj result) {
				stopProgress();
				if (!result.succeeded) {
					if(JsonTransmitter.NO_NETWORK_MODE.equals(result.result))
					{
						showNotInternetAlert();
						return;
					}
					String errmsg = result.result;
//					CustomToast.show(a, errmsg);
					MyApp.toast(errmsg, Toast.LENGTH_SHORT);
					return;
				}
				int num = result.getNumResponses();
				GetUserKids_Response[] responses = new GetUserKids_Response[num]; 
				for (int i = 0; i < num; i++) {
					responses[i] = (GetUserKids_Response) result.getResponse(i);
				}
				User.updateWithUserkids(responses);
				
				if(MainActivity.inst == null)
				{
					start(MainActivity.class);
				}
				else
				{
					MainActivity.updateDrawerContent();
					
					AddKidActivity.callFinish();
					ParentDetailsActivity.callFinish();
					
					MainActivity.openDrawer();
					callFinish();
				}
			}
		});
				
	}


	private void sendCode() {
		final String gan_code = add_code.getText().toString().trim();
		if (StrUtils.isEmpty(gan_code)) {
//			CustomToast.show(this, R.string.enter_code_text);
			MyApp.toast(getString(R.string.enter_code_text), Toast.LENGTH_SHORT);
			return;
		}
		startProgress(R.string.operation_proceeding);
		final Activity a = this;
		 
//		JsonTransmitter.send_getkindergarten(gan_code, new ICompletionHandler() {			
//			@Override
//			public void onComplete(ResultObj result) {
//				stopProgress();
//				if (!result.succeeded) {
//					CustomToast.show(EnterCodeActivity.this, result.result);
//				} else {
//					SignupActivity.createNewInProgress = false; // newUser now fully created
//					start(MainActivity.class); 
//				}
//				
//			}
//		});

	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (this == inst) {
			inst = null;
		}
	}
	
	public static void callFinish() {
		if (inst != null) {
			inst.finish();
		}
	}
}

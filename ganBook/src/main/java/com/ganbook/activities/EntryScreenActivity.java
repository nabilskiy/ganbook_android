package com.ganbook.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.codemybrainsout.onboarder.AhoyOnboarderCard;
import com.ganbook.amazinglist.special.CustomToast;
import com.ganbook.app.MyApp;
import com.ganbook.communication.ICompletionHandler;
import com.ganbook.communication.datamodel.ResultObj;
import com.ganbook.communication.json.BaseResponse;
import com.ganbook.communication.json.GetUserKids_Response;
import com.ganbook.communication.json.getclass_Response;
import com.ganbook.communication.json.loginnew_Response_Parent;
import com.ganbook.communication.json.loginnew_Response_Teacher;
import com.ganbook.communication.json.transmitter.JsonTransmitter;
import com.ganbook.sharedprefs.SPReader;
import com.ganbook.sharedprefs.SPWriter;
import com.ganbook.user.User;
import com.ganbook.utils.NetworkUtils;
import com.ganbook.utils.UpdateUtils;

import com.google.gson.Gson;
import com.project.ganim.R;
import org.json.JSONException;
import org.json.JSONObject;

public class EntryScreenActivity extends BaseAppCompatActivity implements OnClickListener {

	public static EntryScreenActivity inst; 
	
	private Button login_btn;
	private Button register_btn;
	private SharedPreferences prefs;
	private final Initializer initializer = new Initializer();
	
	class Initializer {
		@SuppressLint("ResourceAsColor")
		public void onCreate() {

			MyApp.sendAnalytics("entry", "entry", "entry-ui", "EntryScreen");


			inst = EntryScreenActivity.this;
			if (!NetworkUtils.isConnected()) {
				set_no_internet();
				return;
			}


			setContentView(R.layout.activity_home_screen);
			login_btn = (Button) findViewById(R.id.login_btn);
			login_btn.setOnClickListener(EntryScreenActivity.this);
			register_btn = (Button) findViewById(R.id.register_btn);
			register_btn.setOnClickListener(EntryScreenActivity.this);


		}
		
	}


	private void call_loginmigrateandroid(String user_id)
	{
		String new_user_id = String.valueOf((Integer.valueOf(user_id) - Integer.valueOf(User.USER_ID_KEY)));
		JsonTransmitter.send_loginmigrateandroid(new_user_id, new ICompletionHandler() {						
			@Override
			public void onComplete(ResultObj result) {
				if (result.succeeded) {
					issueNextRequest(result);
				} 					
			}
		});
	}
	
	public static void loginmigrateandroid(String user_id)
	{
		if(inst == null)
		{
			return;
		}
		inst.call_loginmigrateandroid(user_id);
	}

	private boolean isParentResponse;
	
	private void issueNextRequest(ResultObj result) {
		String rawJson = result.result;
		Class<?> responseType = getResponseType(rawJson); 
		if (responseType == null) {
			return;
		}
		
		BaseResponse response;
		try { 
			response = (BaseResponse) new Gson().fromJson(rawJson, responseType); 
			if (response==null) {
//				throw new RuntimeException("Empty response");
				CustomToast.show(this, R.string.operation_failed);
				return;
			}
			JsonTransmitter.handleResponseWithUser(response); 
			
			new SPWriter("GANIM").putString("user_id_new", "0").commit(); // no need old anymore
			
			String userid = User.getId();
			 
			if (isParentResponse) {
				JsonTransmitter.send_getuserkids(userid, new ICompletionHandler() {					
					@Override
					public void onComplete(ResultObj result) {
						if (!result.succeeded) {
							if(JsonTransmitter.NO_NETWORK_MODE.equals(result.result))
							{
								set_no_internet();
								return;
							}
							String errmsg = result.result;
							CustomToast.show(inst, errmsg);
							return;
						}
						int num = result.getNumResponses();
						GetUserKids_Response[] responses = new GetUserKids_Response[num]; 
						for (int i = 0; i < num; i++) {
							responses[i] = (GetUserKids_Response) result.getResponse(i);
						}
						User.updateWithUserkids(responses);
						gotoMain();
					}
				});
			} else {
				int jj=234;
				jj++;
				JsonTransmitter.send_getclass(userid, new ICompletionHandler() {					
					@Override
					public void onComplete(ResultObj result) {
						int jj=234;
						jj++;
						if (!result.succeeded) {
							if(JsonTransmitter.NO_NETWORK_MODE.equals(result.result))
							{
								set_no_internet();
								return;
							}
							String errmsg = result.result;
							CustomToast.show(inst, errmsg);
							return;
						}
						getclass_Response response = (getclass_Response) result.getResponse(0);   
						User.updateWithClasses(response);
						gotoMain();
					}
				});
			}
			int jj=234;
			jj++;
		}
		catch (Exception e) {
			int jj=234;
			jj++;
			return;
		}		
	}
	
	private Class<?> getResponseType(String rawJson) {
		String typeVal;
		try {
			typeVal = new JSONObject(rawJson).getString("type");
		} catch (JSONException e1) {
			e1.printStackTrace();			
			int jj=234;
			jj++;
			return null;
		}
		isParentResponse = User.isParentType(typeVal);
		Class<?> responseType;
		if (isParentResponse) {
			responseType = loginnew_Response_Parent.class;
		} else {
			responseType = loginnew_Response_Teacher.class;
		}
		return responseType;
	}
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initializer.onCreate();

	}
	
	private void set_no_internet()
	{
		setContentView(R.layout.no_internet_layout);
		register_btn = (Button) findViewById(R.id.register_btn);
		
		register_btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				finish();
				startActivity(getIntent());
			}
		});
	}


	boolean doubleBackToExitPressedOnce = false;

	@Override
	public void onBackPressed() {
		if (doubleBackToExitPressedOnce) {
			super.onBackPressed();
			return;
		}

		this.doubleBackToExitPressedOnce = true;
		Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				doubleBackToExitPressedOnce = false;
			}
		}, 2000);
	}

	private void showRecommendUpdateErrorDialog(String update) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);

        
        dialogBuilder.setNeutralButton(R.string.update, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            	Intent intent = new Intent(Intent.ACTION_VIEW);
    			intent.setData(Uri.parse("market://details?id=com.project.ganim"));
    			startActivity(intent);
            }
        });
        
        
        if(UpdateUtils.RECOMMEND_UPDATE.equals(update))
		{
        	dialogBuilder.setMessage(R.string.new_version_avail);
	        dialogBuilder.setNegativeButton(R.string.notnow, new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int whichButton) {
	            	dialog.dismiss();
//	            	gotoMain();
	            }
	        });
	    }
        else
        {
        	dialogBuilder.setMessage(getString(R.string.new_version_avail) + "\n" + getString(R.string.new_version_critical));
        }
        
        dialogBuilder.setTitle(R.string.app_name);
        
        dialogBuilder.setCancelable(false);

        dialogBuilder.show();
    }
	
	private boolean getRegisterProcess() {
		return new SPReader("registerProcessFile").getBool("in_process",false);
	}
	 

	private void gotoMain() {
		if(getRegisterProcess())
		{
			SplashActivity.callFinish();
			gotoRegistration();
		}
		else
		{
//			SplashActivity.callFinish();
			start(MainActivity.class);
//			overridePendingTransition(R.anim.fadein, R.anim.fadeout);
//			this.finish();
		}
	}
	
	@Override
	protected void onPause() {

		super.onPause();
//		overridePendingTransition(R.anim.fadein, R.anim.fadeout);
	}
	
	private void gotoRegistration() {
		start(RegistrationSucceededActivity.class);
		this.finish();
	}

	@Override
	public void onClick(View v) {
		MyApp.loggingOut = false; // prepare for login
		switch (v.getId()) {
		case R.id.login_btn:
			start(LoginActivity.class); 
			break;
		case R.id.register_btn:
			start(EnterEmailActivity.class);
			break;

		default:
			int jj=234;
			jj++;
			break;
		}
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

package com.ganbook.googleservices;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.ganbook.activities.EntryScreenActivity;
import com.ganbook.activities.MainActivity;
import com.ganbook.activities.SplashActivity;
import com.ganbook.amazinglist.special.CustomToast;
import com.ganbook.app.MyApp;
import com.ganbook.communication.ICompletionHandler;
import com.ganbook.communication.datamodel.ResultObj;
import com.ganbook.communication.json.BaseResponse;
import com.ganbook.communication.json.GetUserKids_Response;
import com.ganbook.communication.json.createtoken_Response;
import com.ganbook.communication.json.getclass_Response;
import com.ganbook.communication.json.loginnew_Response_Parent;
import com.ganbook.communication.json.loginnew_Response_Teacher;
import com.ganbook.communication.json.transmitter.JsonTransmitter;
import com.ganbook.debug.InDebug;
import com.ganbook.sharedprefs.SPReader;
import com.ganbook.sharedprefs.SPWriter;
import com.ganbook.user.User;
import com.ganbook.utils.LowPriorityThread;
import com.ganbook.utils.PackageManagerUtils;
import com.ganbook.utils.StrUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
//import com.google.android.gms.gcm.GoogleCloudMessaging;
//import com.google.android.gms.iid.InstanceID;
import com.google.gson.Gson;
import com.project.ganim.R;

public class GoogleServices {
	private GoogleServices() {}
	
	private static final int GPS_DIALOG = 101;
	
	
//	private static GoogleCloudMessaging gcmInst;
	private static String gcmRegid;

	private static final String GANBOOK_SENDER_ID = "955079058183";
	private static final String GOOGLE_SERVICES = "GOOGLE_SERVICES";
	public static final String PROPERTY_REG_ID = "PROPERTY_REG_ID2";
	private static final String PROPERTY_APP_VERSION = "PROPERTY_APP_VERSION2";
	private static final String CREATE_TOKEN = "CREATE_TOKEN";

	private static volatile String cached_registrationId;
	
	public static enum RegStatus { SUCCESS, RETRY, FATAL };
	
	public static Activity __activity;

	public static RegStatus init(Activity activity) {
		if (InDebug.NO_NETWORK) {
			return null;
		}
		
		__activity = activity;
		Context context = MyApp.context;  
		int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
		if (resultCode != ConnectionResult.SUCCESS) {
			if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
				Dialog dialog = GooglePlayServicesUtil.getErrorDialog(resultCode, activity, GPS_DIALOG);
				if (dialog != null) {
					dialog.show(); 
					return RegStatus.RETRY;
				} 
			}  
			return RegStatus.FATAL;
		}
		
//		gcmInst = GoogleCloudMessaging.getInstance(context);
		String token_id = getRegistrationId();  
		if (StrUtils.isEmpty(token_id) || checkIfAppUpdated()) { //if app was updated
			async_performGcmRegistration();
		}
		else {
//			checkUserLogin();
			String user_id = new SPReader("GANIM").getString("user_id_new", "0");
			if(!"0".equals(user_id)) //old
			{
				EntryScreenActivity.loginmigrateandroid(user_id);
			}
//			else
//			{
//				async_performGcmRegistration();
//			}
		}
		return RegStatus.SUCCESS;
	}

	
	private static synchronized boolean checkIfAppUpdated() { // == token GCM !!!
		int storedAppVer = new SPReader(GOOGLE_SERVICES).getInt(PROPERTY_APP_VERSION, -1);
		int currentAppVer = PackageManagerUtils.getAppVersion();
		if (storedAppVer != currentAppVer) {
			return true;
		}
		return false;
	}
	
	
	public static synchronized String getRegistrationId() { // == token_id
//		return "2092"; /== for debug only!;
//		Context context = MyApp.context;
		
		String tmpId = new SPReader(GOOGLE_SERVICES).getString(PROPERTY_REG_ID, null);
		if (StrUtils.isEmpty(tmpId)) {
			return null;
		}
		
		return tmpId;
		// clear registration ID if app was updated
//		int storedAppVer = new SPReader(GOOGLE_SERVICES).getInt(PROPERTY_APP_VERSION, -1);
//		int currentAppVer = PackageManagerUtils.getAppVersion();
//		if (storedAppVer != currentAppVer) {
//			return null;
//		}
//		cached_registrationId = tmpId;
//		return cached_registrationId;
	}

	
	private static void async_performGcmRegistration() { // before registerInBackground()
		if (InDebug.NO_NETWORK) {
			return;
		}
		new LowPriorityThread("GanBook:performGcmRegistration") {
			public void run() {
				try { 
					Context context = MyApp.context;
//					if (gcmInst == null) { 
//						gcmInst = GoogleCloudMessaging.getInstance(context); 
//					}
//					InstanceID instanceID = InstanceID.getInstance(context);
//					gcmRegid = instanceID.getToken(GANBOOK_SENDER_ID,
//					        GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
					
//					gcmRegid = gcmInst.register(GANBOOK_SENDER_ID);
					Log.i("take22" , "gcmRegid ok = " + gcmRegid);
					sendRegistrationIdToBackend();
				} 
				catch (Exception ex) {
					int jj = 453;
					jj++;
				}
			}
		}.start();
	}

	
	
	private static void sendRegistrationIdToBackend() {
		if (InDebug.NO_NETWORK) {
			return;
		}
		boolean create = new SPReader(GOOGLE_SERVICES).getBool(CREATE_TOKEN, false);		
		if (!create) {
			new SPWriter(GOOGLE_SERVICES).putBool(GoogleServices.CREATE_TOKEN, true).commit();						
			JsonTransmitter.send_createtoken(gcmRegid, new ICompletionHandler() {				
				@Override
				public void onComplete(ResultObj result) {
					if (result != null && result.succeeded && result.hasResponse()) {
						createtoken_Response res = (createtoken_Response) result.getResponse(0); 
						storeRegistrationId(res.token_id);
						
						String user_id = new SPReader("GANIM").getString("user_id_new", "0");
						if(!"0".equals(user_id)) //old
						{
							EntryScreenActivity.loginmigrateandroid(user_id);
						}
					}
				}
			});
			
		}
		else {
			String token_id = getRegistrationId();
			String token = gcmRegid;
			JsonTransmitter.send_updatetoken(token_id, token);
		}
	}
	
	

	private static void storeRegistrationId(String token_id) {
		synchronized (GoogleServices.class) {
			cached_registrationId = token_id;
		}
  		new AsyncTask<Void, Void, Void>() {
			@Override 
			protected Void doInBackground(Void... params) {
				int currentAppVer = PackageManagerUtils.getAppVersion();
				new SPWriter(GOOGLE_SERVICES).putString(PROPERTY_REG_ID, cached_registrationId).commit();
				new SPWriter(GOOGLE_SERVICES).putInt(PROPERTY_APP_VERSION, currentAppVer).commit();
				return null; 
			}
		}.execute();
	}

	
	private static void checkUserLogin() { //gggggggg -- do i need this func??
//		user = Utils.initUser(mPrefs);
//		String user_id = mPrefs.getString(Utils.USER_ID, "0");
//		if(Utils._0.equals(user_id)) {
//			if(Utils._0.equals(user.getId()))
//			{
//				setContentView(R.layout.main);
//	
//				ImageView imageLogin = (ImageView)findViewById(R.id.loginButton);		
//	
//				imageLogin.setOnClickListener(new OnClickListener() 
//				{
//					@Override
//					public void onClick(View arg0) 
//					{
//						Intent in = new Intent(context, LoginActivity.class);
//						startActivity(in);
//	//					MainActivity.this.finish();
//					}
//				});
//	
//				ImageView imageSignUp = (ImageView)findViewById(R.id.signUpButton);		
//	
//				imageSignUp.setOnClickListener(new OnClickListener() 
//				{
//					@Override
//					public void onClick(View arg0) 
//					{
//						Intent in = new Intent(context, ChooseSignUp.class);
//						startActivity(in);
//					}
//				});
//			}
//			else //new
//			{
//				createSession();	
//			}
//		}
//		else
//		{
////			openMainScreen();
////			initClasses();
//			if(!Utils._0.equals(user.getId())) //new
//			{
//				createSession();	
//			}
//			else
//			{
//			
//				boolean first_login = mPrefs.getBoolean("FIRST_LOGIN", true);
//				
//				if(!first_login) {
//					createSession();	
//				}
//				else {
//					JsonObject json = new JsonObject();					
//					json.addProperty("user_id",user_id);					
//					json = Utils.getLoginProps(mPrefs,json,context);					
//					mAsyncTaskManager.setupTask(new JsonSenderTask(getResources(),
//							CommonUtilities.HTTP_LOGIN_MIGRATE,json,true),context);					
//					SharedPreferences.Editor editor = mPrefs.edit();
//					editor.putBoolean("FIRST_LOGIN", false);
//					editor.putString(Utils.USER_ID, "0");					
//					editor.commit();
//				}
//			}
//			
//		}
	}
	
}

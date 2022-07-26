package com.ganbook.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.ganbook.activities.SplashActivity;
import com.ganbook.communication.ICompletionHandler;
import com.ganbook.communication.datamodel.ResultObj;
import com.ganbook.communication.json.createtoken_Response;
import com.ganbook.communication.json.transmitter.JsonTransmitter;
import com.ganbook.googleservices.GoogleServices;
import com.ganbook.sharedprefs.SPReader;
import com.ganbook.sharedprefs.SPWriter;
import com.ganbook.utils.StrUtils;

//import com.google.android.gms.gcm.GoogleCloudMessaging;
//import com.google.android.gms.iid.InstanceID;


public class RegistrationIntentService extends IntentService {

	private static final String GANBOOK_SENDER_ID = "955079058183";
	private static final String GOOGLE_SERVICES = "GOOGLE_SERVICES";
	private static final String CREATE_TOKEN = "CREATE_TOKEN";
	
	public RegistrationIntentService() {
		super("RegistrationIntentService");
	}

	@Override
	protected void onHandleIntent(Intent arg0) {
		// Initially this call goes out to the network to retrieve the token, subsequent calls
		// are local.
//		InstanceID instanceID = InstanceID.getInstance(this);
		String token = null;
//		try {
//			token = instanceID.getToken(GANBOOK_SENDER_ID,
//			        GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
//		} catch (IOException e) {

//			e.printStackTrace();
//		}
		Log.i("take22", "GCM Registration Token: " + token);


//		if(token != null)
//		{
			sendRegistrationToServer(token);
//		}

		// Subscribe to topic channels
//		subscribeTopics(token);

		// You should store a boolean that indicates whether the generated token has been
		// sent to your server. If the boolean is false, send the token to your server,
		// otherwise your server should have already received the token.
//		sharedPreferences.edit().putBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, true).apply();
	}

	private void sendRegistrationToServer(String token) {

		
		String user_id = new SPReader("GANIM").getString("user_id_new", "0");
		if(!"0".equals(user_id)) //old
		{
			String old_token_id = new SPReader("GANIM").getString("registration_id", null);
			if(old_token_id != null)
			{
				new SPWriter(GOOGLE_SERVICES).putString(GoogleServices.PROPERTY_REG_ID, old_token_id).commit();
				new SPWriter("GANIM").putString("registration_id", null).commit();
			}
			
			SplashActivity.loginmigrateandroid(user_id);
			JsonTransmitter.send_updatetoken(old_token_id, token);
			return;
		}
		
		String token_id = GoogleServices.getRegistrationId();
		
		if (!StrUtils.isEmpty(token_id)) {
			JsonTransmitter.send_updatetoken(token_id, token);

//			ParseInstallation parseInstallation = ParseInstallation.getCurrentInstallation();
//			parseInstallation.put("token_id",token_id);
//			parseInstallation.saveInBackground();
		}
		else
		{
			boolean create = new SPReader(GOOGLE_SERVICES).getBool(CREATE_TOKEN, false);		
			if (!create) {
								
				JsonTransmitter.send_createtoken(token, new ICompletionHandler() {				
					@Override
					public void onComplete(ResultObj result) {
						if (result != null && result.succeeded && result.hasResponse()) {
							new SPWriter(GOOGLE_SERVICES).putBool(CREATE_TOKEN, true).commit();		
							
							createtoken_Response res = (createtoken_Response) result.getResponse(0); 
							new SPWriter(GOOGLE_SERVICES).putString(GoogleServices.PROPERTY_REG_ID, res.token_id).commit();

//							ParseInstallation parseInstallation = ParseInstallation.getCurrentInstallation();
//							parseInstallation.put("token_id",res.token_id);
//							parseInstallation.saveInBackground();
							
//							String user_id = new SPReader("GANIM").getString("user_id_new", "0");
//							if(!"0".equals(user_id)) //old
//							{
//								EntryScreenActivity.loginmigrateandroid(user_id);
//							}
						}
					}
				});
				
			}
			else {
				JsonTransmitter.send_updatetoken(token_id, token);
			}
		}
	}

}

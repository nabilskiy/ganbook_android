package com.ganbook.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ganbook.amazinglist.special.CustomToast;
import com.ganbook.app.MyApp;
import com.ganbook.communication.CommConsts;
import com.ganbook.communication.ICompletionHandler;
import com.ganbook.communication.datamodel.ResultObj;
import com.ganbook.communication.json.BaseResponse;
import com.ganbook.communication.json.GetUserKids_Response;
import com.ganbook.communication.json.getclass_Response;
import com.ganbook.communication.json.loginnew_Response_Parent;
import com.ganbook.communication.json.loginnew_Response_Teacher;
import com.ganbook.communication.json.transmitter.JsonTransmitter;
import com.ganbook.debug.IsLab;
import com.ganbook.dialogs.Dialogs;
import com.ganbook.models.SuccessAnswer;
import com.ganbook.sharedprefs.SPReader;
import com.ganbook.sharedprefs.SPWriter;
import com.ganbook.user.User;
import com.ganbook.utils.AlertUtils;
import com.ganbook.utils.StrUtils;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.safetynet.SafetyNet;
import com.google.android.gms.safetynet.SafetyNetApi;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.Gson;
import com.parse.ParseInstallation;
import com.parse.ParseQuery;
import com.project.ganim.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;


import java.security.*;
import javax.net.ssl.SSLContext;
import com.google.android.gms.security.ProviderInstaller;

import com.google.android.gms.common.*;


public class LoginActivity extends BaseAppCompatActivity {

	private static final String SAFETY_NET_API_SITE_KEY = "6LfK-5IUAAAAAImGM6qkoHoD8aJEoJMjqE_T9qFi";
	private static Activity inst;
    private EditText enter_email;
    private EditText password;
    private TextView forgot_password;
	private int counter = 5;
    private Button loginBtn;

private final Initializer initializer = new Initializer();
    
    class Initializer {

		public void onCreate() {
			try {
				ProviderInstaller.installIfNeeded(getApplicationContext());
				SSLContext sslContext;
				sslContext = SSLContext.getInstance("TLSv1.2");
				sslContext.init(null, null, null);
				sslContext.createSSLEngine();
			} catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException
					| NoSuchAlgorithmException | KeyManagementException e) {
				e.printStackTrace();
			}

			MyApp.sendAnalytics("login", "login-ui", "login-ui", FirebaseAnalytics.Event.LOGIN);


			inst = LoginActivity.this;
			setContentView(R.layout.activity_login);

			enter_email = (EditText) findViewById(R.id.enter_email);
			password = (EditText) findViewById(R.id.password);
			loginBtn = findViewById(R.id.log_in_btn);
			forgot_password = (TextView) findViewById(R.id.forgot_password);
			forgot_password.setOnClickListener(new View.OnClickListener() {			
				@Override
				public void onClick(View v) {
					AlertUtils.onForgotPassword(LoginActivity.this);
				}
			});

			setActionBar(getString(R.string.login), false);
			
			String lastEmail = getlastEmail();

			if (lastEmail != null) {
				enter_email.setText(lastEmail);
			}

			//block user for 1h
			SharedPreferences  settings = getSharedPreferences("timePreference", MODE_PRIVATE);
			long storedTime = settings.getLong("click_time", 0L);
			Date currentDate = new Date();

			long diff = currentDate.getTime() - storedTime;
			long diffMinutes = diff / (60 * 1000);

			if (diffMinutes <= 15) {
				loginBtn.setEnabled(false);
				enter_email.setEnabled(false);
				password.setEnabled(false);
			} else {
				loginBtn.setEnabled(true);
				enter_email.setEnabled(true);
				password.setEnabled(true);
			}


			
			loginBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {								
					final Activity a = LoginActivity.this;
					final String emailText = enter_email.getText().toString().trim(); 
					final String passwordText = password.getText().toString();
					final String msg = getResources().getString(R.string.login_progress);

					login(emailText, passwordText, msg);

				}
			});
		}

		private void reCaptcha() {

			SafetyNet.getClient(LoginActivity.this).verifyWithRecaptcha(SAFETY_NET_API_SITE_KEY)
					.addOnSuccessListener(LoginActivity.this, new OnSuccessListener<SafetyNetApi.RecaptchaTokenResponse>() {
						@Override
						public void onSuccess(SafetyNetApi.RecaptchaTokenResponse recaptchaTokenResponse) {
							if (!recaptchaTokenResponse.getTokenResult().isEmpty()) {

								retrofit2.Call<SuccessAnswer> recaptchaCall = ganbookApiInterfacePOST.reCaptchaCheck("6LfK-5IUAAAAAM0tmdL0v4V_WsJUFns5PIuoI-Rx", recaptchaTokenResponse.getTokenResult());
								recaptchaCall.enqueue(new retrofit2.Callback<SuccessAnswer>() {
									@Override
									public void onResponse(retrofit2.Call<SuccessAnswer> call, retrofit2.Response<SuccessAnswer> response) {
										if (response.message().equals("OK")) {
											runOnUiThread(new Runnable() {
												@Override
												public void run() {
													Toast.makeText(LoginActivity.this, getString(R.string.fifteen_mins_block_text), Toast.LENGTH_LONG).show();

													SharedPreferences  settings = getSharedPreferences("timePreference", MODE_PRIVATE);
													SharedPreferences.Editor editor = settings.edit();
													Date currentTime = new Date();
													editor.putLong("click_time", currentTime.getTime());
													editor.apply();

												}
											});
											counter = 5;
										}
									}

									@Override
									public void onFailure(retrofit2.Call<SuccessAnswer> call, Throwable throwable) {

									}
								});

							}
						}
					})
					.addOnFailureListener(LoginActivity.this, new OnFailureListener() {
						@Override
						public void onFailure(@NonNull Exception e) {
							if (e instanceof ApiException) {
								ApiException apiException = (ApiException) e;
								Log.d("LoginActivity", CommonStatusCodes.getStatusCodeString(apiException.getStatusCode()));
							} else {
								Log.d("LoginActivity", "Unknown type of error: " + e.getMessage());
							}
						}
					});
		}

		private Call post(String url, String response, String secret, Callback callback) {
			OkHttpClient client = new OkHttpClient();
			RequestBody body = new FormBody.Builder().add("response", response).add("secret", secret).build();
			Request request = new Request.Builder()
					.url(url)
					.post(body)
					.build();
			Call call = client.newCall(request);
			call.enqueue(callback);
			return call;
		}

		private void login(final String emailText, String passwordText, final String msg) {

			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					startProgress(msg);
				}
			});

			JsonTransmitter.send_loginnew(emailText, passwordText, new ICompletionHandler() {
				@Override
				public void onComplete(ResultObj result) {
					if (result.succeeded) {
						ParseQuery.clearAllCachedResults();
						ParseInstallation parseInstallation = ParseInstallation.getCurrentInstallation();
						parseInstallation.put("user_id", String.valueOf(Integer.valueOf(User.getId()) - Integer.valueOf(User.USER_ID_KEY)));

						parseInstallation.saveInBackground();

						saveLastEmail(emailText);
						issueNextRequest(result);
						stopProgress();
					} else {
						counter--;
						stopProgress();
						if(JsonTransmitter.NO_NETWORK_MODE.equals(result.result))
						{
							showNotInternetAlert();
							return;
						}

						String errmsg = result.result;
						Dialogs.errorDialogWithButton(LoginActivity.this, getString(R.string.error), errmsg, getString(R.string.ok));

						if (counter == 0) {
						    loginBtn.setEnabled(false);
						    enter_email.setEnabled(false);
						    password.setEnabled(false);
							reCaptcha();
						}
					}

				}
			});
		}
    }

 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initializer.onCreate();

	}
	
	
	private String getlastEmail() {
		String email = new SPReader("savedEmailFile").getString("savedEmail", null);
		return StrUtils.nullIfEmpty(email);
	}

	@SuppressLint("StaticFieldLeak")
	private void saveLastEmail(final String emailText) {
  		new AsyncTask<Void, Void, Void>() {
			@Override 
			protected Void doInBackground(Void... params) {
				new SPWriter("savedEmailFile").putString("savedEmail", emailText);		
				return null; 
			}
		}.execute();

	}
	
	private boolean isParentResponse;
	
	private void issueNextRequest(ResultObj result) {
		final Activity a = this;
		String rawJson = result.result;
		Class<?> responseType = getResponseType(rawJson); 
		if (responseType == null) {
			return;
		}
		
		BaseResponse response;
		try { 
			response = (BaseResponse) new Gson().fromJson(rawJson, responseType); 
			if (response==null) {
				stopProgress();
				CustomToast.show(a, R.string.operation_failed);
				return;
			}
			JsonTransmitter.handleResponseWithUser(response); 
			
			String userid = User.getId();

			if (isParentResponse) {
				JsonTransmitter.send_getuserkids(userid, new ICompletionHandler() {					
					@Override
					public void onComplete(ResultObj result) {
						stopProgress();
						if (!result.succeeded) {
							if(JsonTransmitter.NO_NETWORK_MODE.equals(result.result))
							{
								showNotInternetAlert();
								return;
							}
							else if (CommConsts.EMPTY_RESULT.equals(result.result))
							{
								start(RegistrationSucceededActivity.class);
								finish();
							}
							return;
						}
						int num = result.getNumResponses();
						GetUserKids_Response[] responses = new GetUserKids_Response[num]; 
						for (int i = 0; i < num; i++) {
							responses[i] = (GetUserKids_Response) result.getResponse(i);
						}
						User.updateWithUserkids(responses);
						goToMain();
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
								showNotInternetAlert();
								return;
							}

							String errmsg = result.result;
							CustomToast.show(a, errmsg);
							return;
						}
						getclass_Response response = (getclass_Response) result.getResponse(0);
						if(response.classes == null)
						{
							start(RegistrationSucceededActivity.class);
							finish();
						} else {
							User.updateWithClasses(response);
							goToMain();
						}
					}
				});
			}
			int jj=234;
			jj++;
		}
		catch (Exception e) {
			stopProgress();
			int jj=234;
			jj++;
			return;
		}		
	}

	private void goToMain() {

		long now = System.currentTimeMillis();
		new SPWriter("DRAWER_REFRESH").putLong("DRAWER_REFRESH", now).commit();

		boolean was_shown = new SPReader("NEW_YEAR").getBool("was_shown", false);
		if(!was_shown && User.current.new_year)
		{
			start(NewYearActivity.class);
		}
		else {
			start(MainActivity.class);
		}
		overridePendingTransition(R.anim.fadein, R.anim.fadeout);

	}

	private Class<?> getResponseType(String rawJson) {
		String typeVal;
		try {
			typeVal = new JSONObject(rawJson).getString("type");
		} catch (JSONException e1) {
			stopProgress();
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
	protected void onStart() {   
		super.onStart();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		stopProgress();
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

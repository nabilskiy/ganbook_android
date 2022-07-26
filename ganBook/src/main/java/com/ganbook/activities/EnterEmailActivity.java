package com.ganbook.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.ganbook.amazinglist.special.CustomToast;
import com.ganbook.communication.ICompletionHandler;
import com.ganbook.communication.datamodel.ResultObj;
import com.ganbook.communication.json.transmitter.JsonTransmitter;
import com.ganbook.debug.IsLab;
import com.ganbook.validate.Validator;
import com.project.ganim.R;

public class EnterEmailActivity extends BaseAppCompatActivity {
	
	// activity_registration_1

	
	private static Activity inst;
	
	public static String emailAddress;

	private EditText emailText;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		inst = this;
		setContentView(R.layout.activity_registration_1);
		
		emailText = (EditText) findViewById(R.id.enter_email);
		
		if (IsLab.Device()) {
			emailText.setText("test_" + System.currentTimeMillis() + "@blabla.com");
		}
		
		((Button)findViewById(R.id.continue_btn)).setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) { 
				final Activity a = EnterEmailActivity.this;
				emailAddress = emailText.getText().toString().trim(); 
				if (!Validator.validEmail(emailAddress)) {
					CustomToast.show(a, R.string.bad_email_msg); 
					return;
				}
//				if (InDebug.Mode) {
//					gotoSignupActivity();
//					return;
//				}				
				startProgress(R.string.operation_proceeding);
				JsonTransmitter.send_retention(emailAddress, new ICompletionHandler() {
					@Override
					public void onComplete(ResultObj result) {
						stopProgress();
//						if (result.succeeded) {
//							start(SignupActivity.class);
//						} else {
//							if(JsonTransmitter.NO_NETWORK_MODE.equals(result.result))
//							{
//								showNotInternetAlert();
//								return;
//							}
//							String errmsg = result.result;
//							CustomToast.show(a, errmsg);
//						}

						if (!result.succeeded && JsonTransmitter.NO_NETWORK_MODE.equals(result.result)) {
							showNotInternetAlert();
							return;
						}
						else {
							start(SignupActivity.class);
						}
					}
				});
				
			}
		});		
		setActionBar(getString(R.string.signup), false);
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

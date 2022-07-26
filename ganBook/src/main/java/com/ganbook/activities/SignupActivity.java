package com.ganbook.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.ganbook.amazinglist.special.CustomToast;
import com.ganbook.app.MyApp;
import com.ganbook.communication.ICompletionHandler;
import com.ganbook.communication.datamodel.ResultObj;
import com.ganbook.communication.json.transmitter.JsonTransmitter;
import com.ganbook.debug.IsLab;
import com.ganbook.sharedprefs.SPWriter;
import com.ganbook.user.User;
import com.ganbook.utils.PersonType;
import com.ganbook.utils.StrUtils;
import com.ganbook.validate.Validator;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.project.ganim.R;

public class SignupActivity extends BaseAppCompatActivity {
	
	// activity_registration_2

	
	private static Activity inst;

	public static volatile boolean createNewInProgress;


	public static String _username;
	public static String _email;
	public static String _password;

	private EditText usernameEdit;
	private EditText emailEdit;
	private EditText passwordEdit;
	private Button registerBtn;
	private RadioGroup rg_group;
	private RadioButton mom;
	private RadioButton dad;
	private RadioButton student;
	boolean registerStarted = false;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		MyApp.sendAnalytics("register", "register", "register", FirebaseAnalytics.Event.SIGN_UP);

		inst = this;
		createNewInProgress = true;
		setContentView(R.layout.activity_registration_2);

		findViewById(R.id.privacy_terms).setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				WebViewActivity.start(SignupActivity.this, WebViewActivity.TERMS);				
			}
		});
		
		findViewById(R.id.terms_of_service).setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				WebViewActivity.start(SignupActivity.this, WebViewActivity.TERMS);				
			}
		});
  
 
		registerBtn = (Button)findViewById(R.id.register_btn);
		emailEdit = (EditText) findViewById(R.id.enter_email);
		emailEdit.setText(EnterEmailActivity.emailAddress);
		usernameEdit = (EditText) findViewById(R.id.enter_full_name);
		passwordEdit = (EditText) findViewById(R.id.password);
	    rg_group = (RadioGroup) findViewById(R.id.rg_group);
	    mom = (RadioButton) findViewById(R.id.mom);
	    dad = (RadioButton) findViewById(R.id.dad);
		student = findViewById(R.id.student);

	    if(getPackageName().equals("com.project.ganim")) {
	    	student.setVisibility(View.GONE);
		} else {
	    	student.setVisibility(View.VISIBLE);
		}
	    
	    if (IsLab.Device()) {
	    	usernameEdit.setText("");
	    	passwordEdit.setText("123456");
	    }
	    
		registerBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				if (!registerStarted) {
					String msg = getResources().getString(R.string.register_progress);

					_username = usernameEdit.getText().toString().trim();
					_email = emailEdit.getText().toString().trim();
					_password = passwordEdit.getText().toString();

					int selType = rg_group.getCheckedRadioButtonId();
					String type = getType(selType);
					if (StrUtils.isEmpty(_username)) {
						CustomToast.show(SignupActivity.this, R.string.username_not_specified);
						return;
					}
					if (!Validator.validPassword(_password)) {
						CustomToast.show(SignupActivity.this, R.string.illegal_password);
						return;
					}
					if (type == null) {
						CustomToast.show(SignupActivity.this, R.string.whoami_not_set);
						return;
					}

					if(type.equals(User.Type_Staff)) {
						SharedPreferences myPrefs = getSharedPreferences("staff_prefs", MODE_PRIVATE);
						SharedPreferences.Editor prefsEditor = myPrefs.edit();
						prefsEditor.putString("staff_username", _username);
						prefsEditor.commit();
					}

					startProgress(msg);
					JsonTransmitter.send_createuser( // hhhh make sure correct type for parent
							_username, type, _email, _password, new ICompletionHandler() {
								@Override
								public void onComplete(ResultObj result) {
									if (!result.succeeded) {
										registerStarted = false;
										stopProgress();
										if (JsonTransmitter.NO_NETWORK_MODE.equals(result.result)) {
											SignupActivity.this.showNotInternetAlert();
											return;
										}

										String errmsg = result.result;
										CustomToast.show(SignupActivity.this, errmsg);
									} else {
										registerStarted = false;
										silent_loginnew();
//									killAllRegScreens();
//									start(RegistrationSucceededActivity.class);
									}
								}
							});

					registerStarted = true;
				}

			}
		});

		setActionBar(getString(R.string.signup), false);

		invalidateOptionsMenu();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.save_button_menu, menu);

		menu.findItem(R.id.save_button).setVisible(false);

		return super.onCreateOptionsMenu(menu);
	}
	
	
	private void silent_loginnew() {
		final Activity a = this;
		String email = SignupActivity._email;
		String password = SignupActivity._password;

		JsonTransmitter.send_loginnew(
				email, password,
				new ICompletionHandler() {			
			@Override
			public void onComplete(ResultObj result) {
				stopProgress();
				if (!result.succeeded) {
					if(JsonTransmitter.NO_NETWORK_MODE.equals(result.result))
					{
						SignupActivity.this.showNotInternetAlert();
						return;
					}
					CustomToast.show(a, result.result); 
				} 
				else {
					saveRegisterProcess();
					killAllRegScreens();
					start(RegistrationSucceededActivity.class);
				}
			} 
		});
	}
	
	@SuppressLint("StaticFieldLeak")
	private void saveRegisterProcess() {
  		new AsyncTask<Void, Void, Void>() {
			@Override 
			protected Void doInBackground(Void... params) {
				new SPWriter("registerProcessFile").putBool("in_process", true).commit();		
				return null; 
			}
		}.execute();

	}
	
	private static void killAllRegScreens() {
		EntryScreenActivity.callFinish();
		EnterEmailActivity.callFinish();
		SignupActivity.callFinish();	
	}

		
	private static String getType(int selType) {
		String type = null;
		if (selType == R.id.mom) {
			type = PersonType.MOM;
		} else if (selType == R.id.dad) {
			type = PersonType.DAD;
		} else if (selType == R.id.teacher) {
			type = PersonType.TEACHER;
		} else if (selType == R.id.staff) {
			type = PersonType.STUFF;
		} else if (selType == R.id.student) {
			type = PersonType.STUDENT;
		} else {
			int jj=234;
			jj++;
		}

		return type;
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

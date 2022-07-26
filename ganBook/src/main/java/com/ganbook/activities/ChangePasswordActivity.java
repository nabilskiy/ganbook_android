package com.ganbook.activities;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.ganbook.amazinglist.special.CustomToast;
import com.ganbook.communication.ICompletionHandler;
import com.ganbook.communication.datamodel.ResultObj;
import com.ganbook.communication.json.transmitter.JsonTransmitter;
import com.ganbook.models.SuccessAnswer;
import com.ganbook.user.User;
import com.ganbook.utils.AlertUtils;
import com.ganbook.utils.KeyboardUtils;
import com.ganbook.utils.LocaleGetter;
import com.ganbook.utils.StrUtils;
import com.ganbook.validate.Validator;
import com.project.ganim.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePasswordActivity extends BaseAppCompatActivity {

	private static final String TAG = ChangePasswordActivity.class.getName();

	// change_password_fragment 
	
	private EditText current_pass, new_pass, verify_pass, lost_pass_email;
	private LinearLayout forgot_pass, send_email_btn;
	private static final String GOOGLE_SERVICES = "GOOGLE_SERVICES";
	private static final String CREATE_TOKEN = "CREATE_TOKEN";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.change_password_fragment);
		setActionBar(getString(R.string.change_password), true);


		current_pass = (EditText) findViewById(R.id.current_pass); 
		new_pass = (EditText) findViewById(R.id.new_pass);
		verify_pass = (EditText) findViewById(R.id.verify_pass);
		lost_pass_email = (EditText) findViewById(R.id.lost_pass_email);
		forgot_pass = (LinearLayout) findViewById(R.id.forgot_pass);
		send_email_btn = (LinearLayout) findViewById(R.id.send_email_btn);
		
		forgot_pass.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				AlertUtils.onForgotPassword(ChangePasswordActivity.this);
			}
		});
		
		send_email_btn.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				sendResetEmail(); 
			}
		});
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
				final String _current = current_pass.getText().toString().trim();
				final String _new = new_pass.getText().toString().trim();
				String _verify = verify_pass.getText().toString().trim();

				KeyboardUtils.close(this, current_pass);

				if (StrUtils.isEmpty(_current) ||
						StrUtils.isEmpty(_new) ||
						StrUtils.isEmpty(_verify) ) {
					CustomToast.show(this, R.string.set_all_pass);
					return false;
				}
				if (!_new.equals(_verify)) {
					CustomToast.show(this, R.string.not_verify_pass);
					return false;
				}

				startProgress(R.string.operation_proceeding);

				Call<SuccessAnswer> successAnswerCall = ganbookApiInterfacePOST.updatePassword(_current,
						_new, (LocaleGetter.isHebrew(getResources().getConfiguration())? "he_IL" : LocaleGetter.get()), User.getId());

				successAnswerCall.enqueue(new Callback<SuccessAnswer>() {
					@Override
					public void onResponse(Call<SuccessAnswer> call, Response<SuccessAnswer> response) {
						SuccessAnswer successAnswer = response.body();

						if(response.errorBody() != null) {
							try {
								JSONObject errorResponse = new JSONObject(response.errorBody().string());
								JSONObject errorObject = errorResponse.getJSONObject("errors");
								String errorMessage = errorObject.getString("msg");
								CustomToast.showLong(ChangePasswordActivity.this, errorMessage);
							} catch (IOException e) {
								e.printStackTrace();
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}

						if (successAnswer != null) {
							if (successAnswer.getErrors() != null) {
								CustomToast.showLong(ChangePasswordActivity.this, successAnswer.getErrors().getMsg());
							} else {
								CustomToast.show(ChangePasswordActivity.this, R.string.operation_succeeded);
								JsonTransmitter.send_loginnew(User.getEmail(), _new, null);
								ChangePasswordActivity.this.finish();
							}
						}

						stopProgress();
					}

					@Override
					public void onFailure(Call<SuccessAnswer> call, Throwable t) {

						stopProgress();

						Log.e(TAG, "onFailure: error while update pass = " + Log.getStackTraceString(t));

						CustomToast.show(ChangePasswordActivity.this, R.string.error_update_password);
					}
				});
				break;
			case android.R.id.home:
				onBackPressed();
				break;
		}

		return super.onOptionsItemSelected(item);
	}



	protected void sendResetEmail() {
		final String _email = lost_pass_email.getText().toString().trim();
		
		KeyboardUtils.close(this, current_pass);
		
		if (!Validator.validEmail(_email)) {
			CustomToast.show(this, R.string.bad_email_msg);
			return;
		}

		final Activity a = this;
		
		startProgress(R.string.operation_proceeding);
		JsonTransmitter.send_resetpassword(_email, 
				new ICompletionHandler() {			 
			@Override
			public void onComplete(ResultObj result) {
				stopProgress(); 
				if (!result.succeeded) {
					CustomToast.show(a, result.result); 
				} else {
					CustomToast.show(a, R.string.lost_pass_notif);
				}
			}
		});
	}
	
	

}

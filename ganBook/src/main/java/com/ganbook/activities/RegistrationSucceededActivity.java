package com.ganbook.activities;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ganbook.amazinglist.special.CustomToast;
import com.ganbook.app.MyApp;
import com.ganbook.communication.ICompletionHandler;
import com.ganbook.communication.datamodel.ResultObj;
import com.ganbook.communication.json.transmitter.JsonTransmitter;
import com.ganbook.sharedprefs.SPWriter;
import com.ganbook.user.User;

import com.project.ganim.R;

public class RegistrationSucceededActivity extends BaseAppCompatActivity {
	
	// activity_registration_succeeded
  
	private static Activity inst;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		String type = "parent";
		
		if (User.isTeacher()) { 
			type = "teacher";	
		}

		MyApp.sendAnalytics("register-succeeded-ui", "register-"+type+"-succeeded-ui-"+User.getId(), "register-succeeded-ui", "RegisterSucceeded");

		inst = this;
		
		setContentView(R.layout.activity_registration_succeeded); 
	    Button btn = (Button) findViewById(R.id.add_kid_btn); 
	    TextView textView = (TextView) findViewById(R.id.welcome_text3); 
 
		 
		if (User.isTeacher()) { 
			btn.setText(R.string.add_class_btn);
			textView.setText(getString(R.string.welcome_text_line3_teacher));
		} else {
			if(User.isStaff()) {
				btn.setText(R.string.add_staff_btn);
				textView.setVisibility(View.GONE);
			} else if(User.isStudent()) {
				btn.setText(R.string.add_student_text);
				textView.setText("");
			} else {
				btn.setText(R.string.add_kid_btn);
				textView.setText(getString(R.string.welcome_text_line3));
			}

		}


		final Activity a = this;
		btn.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				int jj=234;
				jj++;
				if (User.isTeacher()) {
					start(KindergartenDetailsActivity.class);
				} else {

					MyApp.sendAnalytics("register-add-kid-ui", "register-add-kid-ui"+ User.getId(), "register-add-kid-ui", "RegisterAddKid");

					start(AddKidActivity.class);
				}
			}
		});		
		setActionBar(getString(R.string.welcome), false);
		
		TextView log_out_link = (TextView) findViewById(R.id.log_out_link);
//		log_out_link.setMovementMethod(LinkMovementMethod.getInstance());
		log_out_link.setText(Html.fromHtml(getResources().getString(R.string.log_out_link)));
		
		log_out_link.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				performLogout();
			}
		});

		invalidateOptionsMenu();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.save_button_menu, menu);

		menu.findItem(R.id.save_button).setVisible(false);

		return super.onCreateOptionsMenu(menu);
	}
	
	private void performLogout() {
		startProgress(R.string.operation_proceeding);
		final Activity a = this;
		JsonTransmitter.send_userLogout(new ICompletionHandler() {			
			@Override
			public void onComplete(ResultObj result) {
				stopProgress();

				new SPWriter("registerProcessFile").putBool("in_process", false).commit();

				a.finish();
				MainActivity.stop(); 
				start(EntryScreenActivity.class);
				CustomToast.show(a, R.string.logged_out);
				User.clear(); 
			}
		}); 
			
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

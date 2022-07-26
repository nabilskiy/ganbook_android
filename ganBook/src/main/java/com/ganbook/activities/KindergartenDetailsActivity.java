package com.ganbook.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.ganbook.amazinglist.special.CustomToast;
import com.ganbook.app.MyApp;
import com.ganbook.debug.IsLab;
import com.ganbook.user.User;
import com.ganbook.utils.KeyboardUtils;
import com.ganbook.utils.StrUtils;

import com.project.ganim.R;

public class KindergartenDetailsActivity extends BaseAppCompatActivity {
	
	// kindergarten_details_activity 
	
	private static Activity inst;

	private EditText 
			kindergarten_nameEdit,
			kindergarten_phoneEdit,
			kindergarten_addressEdit,
			kindergarten_cityEdit,
			teacher_nameEdit,
			teacher_mobileEdit;

	public static String 
		kindergarten_name,
		kindergarten_phone,
		kindergarten_address,
		kindergarten_city,
		teacher_name,
		teacher_mobile;


	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		MyApp.sendAnalytics("register-kindergarten-details-ui", "register-kindergarten-details-ui-"+User.getId(), "register-kindergarten-details-ui", "RegisterKindergarten");


		inst = this;
		
		setContentView(R.layout.kindergarten_details_activity);
		setActionBar(getString(R.string.kindergarten_details_title), false);
				
		kindergarten_nameEdit = (EditText) findViewById(R.id.kindergarten_name);
		kindergarten_phoneEdit = (EditText) findViewById(R.id.kindergarten_phone);
		kindergarten_addressEdit = (EditText) findViewById(R.id.kindergarten_address);
		kindergarten_cityEdit = (EditText) findViewById(R.id.kindergarten_city);
		teacher_nameEdit = (EditText) findViewById(R.id.teacher_name);
		teacher_mobileEdit = (EditText) findViewById(R.id.teacher_mobile);
		
		String set_name = SignupActivity._username;
		if (StrUtils.notEmpty(set_name)) {
			teacher_nameEdit.setText(set_name);
		}
		
	    if (IsLab.Device()) {
			kindergarten_phoneEdit.setText("111222333");
			kindergarten_addressEdit.setText("Hameginnim 3");
			kindergarten_cityEdit.setText("Tel-Aviv");
			teacher_nameEdit.setText("Same Spade");
			teacher_mobileEdit.setText("0544453356");
	    }
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

				kindergarten_name = kindergarten_nameEdit.getText().toString().trim();
				kindergarten_phone = kindergarten_phoneEdit.getText().toString().trim();
				kindergarten_address = kindergarten_addressEdit.getText().toString().trim();
				kindergarten_city = kindergarten_cityEdit.getText().toString().trim();
				teacher_name = teacher_nameEdit.getText().toString().trim();
				teacher_mobile = teacher_mobileEdit.getText().toString().trim();

				if (anyFieldEmpty()) {
					CustomToast.show(this, R.string.gan_data_incompete);
					return false;
				}

				// validations here!

				KeyboardUtils.close(this, kindergarten_nameEdit);

				MyApp.sendAnalytics("register-addclass-ui", "register-addclass-ui-"+User.getId(), "register-addclass-ui", "RegisterAddClass");


				AddClassActivity.part_of_init_process = true;

				start(AddClassActivity.class);
				break;

			case android.R.id.home:

				onBackPressed();

				break;
		}

		return super.onOptionsItemSelected(item);
	}

	protected boolean anyFieldEmpty() {
		return StrUtils.isEmpty(kindergarten_name) || 
				StrUtils.isEmpty(kindergarten_phone) ||
				StrUtils.isEmpty(kindergarten_address) || 
				StrUtils.isEmpty(kindergarten_city) ||
				StrUtils.isEmpty(teacher_name) ||
				StrUtils.isEmpty(teacher_mobile );
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



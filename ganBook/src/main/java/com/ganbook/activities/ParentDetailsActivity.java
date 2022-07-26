package com.ganbook.activities;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.ganbook.amazinglist.special.CustomToast;
import com.ganbook.app.MyApp;
import com.ganbook.communication.ICompletionHandler;
import com.ganbook.communication.datamodel.KidDetails;
import com.ganbook.communication.datamodel.ResultObj;
import com.ganbook.communication.json.transmitter.JsonTransmitter;
import com.ganbook.debug.InDebug;
import com.ganbook.fragments.ContactListFragment;
import com.ganbook.user.User;
import com.ganbook.utils.KeyboardUtils;

import com.project.ganim.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ParentDetailsActivity extends BaseAppCompatActivity {

	private static final String TAG = ParentDetailsActivity.class.getName();
	// parent_details_activity
	private static Activity inst;
	
	private EditText name, home_number, mobile, address, city;

	public static String _name ; 
	public static String _phone ;
	public static String _mobile ;
	public static String _address ;
	public static String _city ;

	protected static boolean part_of_init_process;
	
	private boolean part_of_init;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		inst = this;
		
		part_of_init = part_of_init_process;
		part_of_init_process = false;
		
		setContentView(R.layout.parent_details_activity); //edit_parent_fragment);
		if(User.isStaff()) {
			setActionBar(getString(R.string.staff_details_header), false);
		} else if(User.isStudent()) {
			setActionBar(getString(R.string.student_details_text), false);
		} else {
			setActionBar(getString(R.string.parent_details_header), false);
		}

		name = (EditText) findViewById(R.id.name); 
		home_number = (EditText) findViewById(R.id.home_number);
		mobile = (EditText) findViewById(R.id.mobile);
		address = (EditText) findViewById(R.id.address);
		city = (EditText) findViewById(R.id.city);
				
		if (InDebug.NO_NETWORK) {
			// no op
		}
		else {
			_name = User.getName();  
			_phone = User.current.phone;
			_mobile = User.current.mobile;
			_address = User.current.address;
			_city = User.current.city;
		}
		
		name.setText(_name); 
		home_number.setText(_phone);
		mobile.setText(_mobile);
		address.setText(_address);
		city.setText(_city);
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

				_name = name.getText().toString().trim();
				_phone = home_number.getText().toString().trim();
				_mobile = mobile.getText().toString().trim();
				_address = address.getText().toString().trim();
				_city = city.getText().toString().trim();

				KeyboardUtils.close(this, name);

				MyApp.sendAnalytics("register-enter-code-ui", "register-enter-code-ui-" + User.getId(), "register-enter-code-ui", "EnterCode");


				start(EnterCodeActivity.class);
				break;
		}

		return super.onOptionsItemSelected(item);
	}
	
	
	protected void call_updateParent() {
		final Activity a = this;
		String parent_id = User.getId();
		String parent_name = _name;
		String parent_mobile = _mobile; 
		String parent_phone = _phone;
		String parent_address = _address;
		String parent_city = _city;
		
		
		startProgress(R.string.operation_proceeding);
		JsonTransmitter.send_updateparent(parent_id, parent_name, parent_mobile, 
				parent_phone, parent_address, parent_city, getJsonKid(), 
				new ICompletionHandler() {			
			@Override
			public void onComplete(ResultObj result) {
				stopProgress();
				if (!result.succeeded) {
					if(JsonTransmitter.NO_NETWORK_MODE.equals(result.result))
					{
						setContentView(R.layout.no_internet_layout);
						return;
					}
					CustomToast.show(a, result.result); 
				} else {
					CustomToast.show(a, R.string.operation_succeeded);
					updateData();
					a.finish();
				}
			}
		});
	}
	
	private JSONArray getJsonKid() {
		KidDetails[] kidsArray = User.getKidDetails();
		
		JSONArray jsonArray = new JSONArray();
		
		int ind = 0;
		
		for (KidDetails k: kidsArray) {
				
			JSONObject jsonObject = new JSONObject();
			
			try {

				Log.d(TAG, "getJsonKid: kid id = " + k.id);

				jsonObject.put("id", k.id);
				jsonObject.put("name", JsonTransmitter.UtfEncode(k.name));
				jsonObject.put("gender", k.gender);
				jsonObject.put("birth_date", k.birth_date);
				
				jsonArray.put(jsonObject);
				
			} catch (JSONException e) {

				e.printStackTrace();
			}
			
			ind++;
		}
		
		return jsonArray;
	}
	
	private void updateData() {
		MainActivity.updateDrawerContent();
		// force teacher name in contactList to refresh
		ContactListFragment.forceContentRefresh();
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

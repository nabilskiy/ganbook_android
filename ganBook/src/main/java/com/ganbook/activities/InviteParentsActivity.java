package com.ganbook.activities;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.ganbook.amazinglist.special.CustomToast;
import com.ganbook.app.MyApp;
import com.ganbook.share.ShareManager;
import com.ganbook.user.User;

import com.project.ganim.R;

public class InviteParentsActivity extends BaseAppCompatActivity {
	
	private String gan_code = "";
	
	@Override 
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_inviteparents);
		setActionBar(getString(R.string.invite_parents), false);

		MyApp.sendAnalytics("invite-parents-ui", "invite-parents-ui-"+User.getId(), "invite-parents-ui", "InviteParents");

		String[] classNamesCodesArr = User.current.getClassNameArray();
		
		if(classNamesCodesArr == null || classNamesCodesArr.length == 0)
		{
			CustomToast.show(InviteParentsActivity.this, R.string.not_connected_yet);
			finish();
		}
		
		final String[] classNamesArr = new String [classNamesCodesArr.length] ;
		final String[] codes = new String [classNamesCodesArr.length] ;
		
		for (int i = 0; i < classNamesCodesArr.length; i++) {
			String[] arr = classNamesCodesArr[i].split("\\|");
			
			classNamesArr[i] = arr[0];
			codes[i] = arr[1];
		}
		
		Spinner spinner = (Spinner) findViewById(R.id.classes_spinner);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
			android.R.layout.simple_spinner_item, classNamesArr);
		// Create an ArrayAdapter using the string array and a default spinner layout
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		spinner.setAdapter(adapter);
		
		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			 @Override
			    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
			        // your code here
				 	gan_code = codes[position];
			    }

			    @Override
			    public void onNothingSelected(AdapterView<?> parentView) {
			        // your code here
			    	gan_code = codes[0];
			    }
		});
		
		Button invite_btn = (Button) findViewById(R.id.invite_btn); // in actionbar_mainscreen.xml
		
		invite_btn.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				String pref = getResources().getString(R.string.invitation_parent_body_part_1); 
				String _body = pref +  " " + gan_code;
				ShareManager.openShareMenu(InviteParentsActivity.this, R.string.invitation_parent_subject,
						-1, _body, null, null);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.save_button_menu, menu);
		menu.findItem(R.id.save_button).setTitle(getString(R.string.done));
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {

			case R.id.save_button:

				finish();
				break;

			case android.R.id.home:

				onBackPressed();

				break;
		}

		return super.onOptionsItemSelected(item);
	}
}

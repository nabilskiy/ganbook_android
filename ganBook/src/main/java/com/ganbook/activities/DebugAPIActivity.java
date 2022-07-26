package com.ganbook.activities;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.ganbook.communication.json.transmitter.JsonTransmitter;
import com.ganbook.googleservices.GoogleServices;
import com.project.ganim.R;

import org.json.JSONObject;

import java.util.HashMap;

public class DebugAPIActivity extends BaseAppCompatActivity {
	
	private Spinner spinner1;
	private EditText editText1;
	private Button start_btn;
	
	private Handler handler;
	
	private String selectedRequest;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_debug_apis);
		
		GoogleServices.init(this); 
		
		Toast.makeText(this, "Logging in as t700@test.com", Toast.LENGTH_LONG).show();
 
		// returns user_id = "1000378247"1000365478
//		JsonTransmitter.send_loginnew( 
//				"t700@test.com", //String Mail, 
//				"noa1981", //String password, 
//				"2", //String gmt, 
//				"1", //String scale, 
//				"1", //String app_name, // 1 == GABBOOK  
//				"2092", //String token_id,
//				"il" //String lang_region
//				) ;
		
		spinner1 = (Spinner) findViewById(R.id.spinner1);
		editText1 = (EditText) findViewById(R.id.editText1);
		start_btn = (Button) findViewById(R.id.start_btn);
		start_btn.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {

				if (selectedRequest != null) {  
					JsonTransmitter.execute_via_reflection(selectedRequest);
				}
			}
		});
				
		final String[] choices = JsonTransmitter.debug_allRequestNames; 
		
		ArrayAdapter<String> a = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, choices);
		spinner1.setAdapter(a);
		spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
	        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
	            selectedRequest = (String) parent.getItemAtPosition(pos);
//	            JsonTransmitter.execute_via_reflection(selectedRequest); 
	        }

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
	            int jj=234;
	            jj++;
				
			}

	    }); 
		
		handler = new Handler();
		
		handler.postDelayed(new DebugRunnable(), 222);

//		general test params
//			class_id תשתמש ב 2074
//			בyear=2015
//			album_id=11593
		  
		  
		  
//		JsonTransmitter.send_getuserkids("12023") ; //ok >> only for parent!

//ALL below OK -------------------		
//		JsonTransmitter.send_getparents("2074", "2015") ;//ok  
//		JsonTransmitter.send_getparentsclass("2074"); //ok   
//		JsonTransmitter.send_getalbum("2074", "2015"); //ok
//		JsonTransmitter.send_getpicture("11593" , "3"); //ok
//		JsonTransmitter.send_getmessage("2074", "2015"); //ok
		
//		JsonTransmitter.send_retention("noanika@gmail.com", "2902"); //ok  
//		JsonTransmitter.send_resetpassword("noanika@gmail.com"); //ok
//		JsonTransmitter.send_changepassword("noanika@gmail.com"); //ok
//		JsonTransmitter.send_updateAlbumView("11001", "2015", "5"); //ok
//		JsonTransmitter.send_updatepassword("111111", "noa1981", "he_IL", "12023"); //ok
//		JsonTransmitter.send_reportsavedpicture("492587");  //ok
		
//		// PARENT
//		JsonTransmitter.send_createkid("2095", "aa", "12023",  
//				"a", "5", "5", "ta", "noa", "1", "2010-10-10", "noa.jpg"); //ok
//		JsonTransmitter.send_setclass("11556", "2074"); //ok
//		JsonTransmitter.send_updateclass("11556", "2074"); //ok  
//		JsonTransmitter.send_getactivekids("2074"); //ok
		
//		// Teacher API
//		// Album API
//		JsonTransmitter.send_createalbum("myalbum", "2074"); //ok
//		JsonTransmitter.send_deletealbumj("11001"); //ok   
//		JsonTransmitter.send_editalbum("17389", "MyAlbum2"); //ok	
//		// Picture API
//		JsonTransmitter.send_deletepicturesj("15461416062076122.jpg"); //ok
//		JsonTransmitter.send_pushafterupload("1", "1", "17389", "2074", "0"); //ok
		
		// Message API   
//		JsonTransmitter.send_createmessage("bla bla", "2074"); //ok
//		JsonTransmitter.send_updatereadmessage("12023", "2074", "15808"); //ok
//		JsonTransmitter.send_deletemessage("13713"); //ok 
		// Token API
//		JsonTransmitter.send_deletetoken("123"); //ok
//		JsonTransmitter.send_createtoken("123"); //ok
//		JsonTransmitter.send_updatetoken("47180", "123456");//ok
//		JsonTransmitter.send_userLogout("2092");//ok
		
//		JsonTransmitter.send_getreadmessage("13282", "2074"); //ok  
//		JsonTransmitter.send_getclass("1000378247");  //ok
//		JsonTransmitter.send_activekid("11556");   //ok   
//		JsonTransmitter.send_senduserspush("12023", "15808", "2074"); //ok 
//		JsonTransmitter.send_uploadpic(file, id, tmb, name); was not tested 

// UNTIL HERE -------------------		 

				
		
//		String parent_ids = "{\"3977\":\"1\", \"2020\":\"1\"}";
		HashMap<String, String> parentMap = new HashMap<String, String>();
		parentMap.put("3977", "1"); 
		parentMap.put("2020", "1"); 
		JSONObject parent_ids = new JSONObject(parentMap); 
//		JsonTransmitter.send_setparentpermission(parent_ids, "2074");

  		
//		ArrayList<String> userList = new ArrayList<String>();
//		userList.add("12769");
//		userList.add("12023");
//		JSONArray users_ids = new JSONArray(userList);  
////		String users_ids = "12769,12023"; 
//		JsonTransmitter.send_senduserspush(users_ids, "15808", "2074");
				 		
		int jj=342;  
		jj++;

		
 
	} 
	
	 
	class DebugRunnable implements Runnable {
		@Override
		public void run() {
			String txt = JsonTransmitter.debug__shortDesc;
			editText1.setText(txt); 
			handler.postDelayed(new DebugRunnable(), 222);  
		}
	}
}

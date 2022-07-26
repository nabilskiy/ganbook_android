package com.ganbook.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.ganbook.amazinglist.special.CustomToast;
import com.ganbook.communication.ICompletionHandler;
import com.ganbook.communication.datamodel.ResultObj;
import com.ganbook.communication.json.getgankids_response;
import com.ganbook.communication.json.getgankids_response.KidDetails;
import com.ganbook.communication.json.transmitter.JsonTransmitter;
import com.ganbook.ui.CircleImageView;
import com.ganbook.universalloader.UILManager;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.project.ganim.R;

import java.util.HashMap;

public class NewYearActivity1 extends BaseAppCompatActivity {
	
	ListView kids_list;
	TextView title_class_name;
	ImageView back_btn;
	
	getgankids_response[] classes;
	int ind = 0;
	static HashMap<String, _Class> chosen_kids;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_year1);
		setActionBar(getString(R.string.new_year), false);
		
		back_btn = (ImageView) findViewById(R.id.back_btn);
		
		if(ind == 0)
		{
			back_btn.setVisibility(View.GONE);
		}
		else
		{
			back_btn.setVisibility(View.VISIBLE);
		}

		back_btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				go_back(true);
			}
		});
		
		((TextView)findViewById(R.id.actionbar_caption)).setText(getString(R.string.new_year));
		
		title_class_name = (TextView)findViewById(R.id.title_class_name);
		
		kids_list = (ListView) findViewById(R.id.kids_list);
		
		kids_list.setSelector(R.drawable.listselector);
		
		kids_list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		
		kids_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
		   @Override
		   public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		      View listItem = (View)parent.getChildAt(position);
		      CheckBox cb = (CheckBox) listItem.findViewById(R.id.check);
		      KidDetails kid_checked = classes[ind].kids[(Integer)(position)];
		      
		      if(cb.isChecked())
		      {
		    	  cb.setChecked(false);
		    	  chosen_kids.remove(kid_checked.kid_id);
		      }
		      else
		      {
		    	  cb.setChecked(true);
		    	  chosen_kids.put(kid_checked.kid_id, new _Class(classes[ind].class_id, classes[ind].class_name, kid_checked.kid_name, kid_checked.kid_pic, kid_checked.getDeafultKidImg()));
		      }
		   } 
		});
		
		if(chosen_kids == null)
		{
			chosen_kids = new HashMap<String, _Class>();
		}
		
		call_getgankids();
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

				go_back(false);
				break;
		}

		return super.onOptionsItemSelected(item);
	}
	
	private void go_back(boolean back)
	{
		if(back)
		{
			ind --;
		}
		else
		{
			ind++;
		}
		
		if(ind == 0)
		{
			back_btn.setVisibility(View.GONE);
		}
		else
		{
			back_btn.setVisibility(View.VISIBLE);
		}
		
		if(ind == classes.length)
		{
			ind--;
			//end screen
			start(NewYearEndScreenActivity.class);
		}
		else
		{
			// go to next screen
			title_class_name.setText(getString(R.string.new_year_title) + " " + classes[ind].class_name);
			
			kids_list.setAdapter(new GanKidsAdapter());
		}
	}
	
	public class _Class
	{
		public _Class(String class_id, String class_name, String kid_name, String kid_pic, int def_img) {
			this.class_id = class_id;
			this.class_name = class_name;
			this.kid_name = kid_name;
			this.kid_pic = kid_pic;
			this.def_img = def_img;
		}
		public String class_id;
		public String class_name;
		public String kid_name;
		public String kid_pic;
		public int def_img;
	}
	
	@Override
	public void onBackPressed() {

		
		if(ind == 0)
		{
			// to exit??
			openLogoutPopup();
		}
		else
		{
			go_back(true);
		}
	}
	
	private void call_getgankids() {
		final Activity a = this;	
		
		JsonTransmitter.send_getgankids(new ICompletionHandler() {	 		
			@Override
			public void onComplete(ResultObj result) {
//				stopProgress();
				if (!result.succeeded) {
					if(JsonTransmitter.NO_NETWORK_MODE.equals(result.result))
					{
						setContentView(R.layout.no_internet_layout);
						return;
					}
					stopProgress();
					CustomToast.show(a, result.result); 
				} 
				else {
					int num = result.getNumResponses();
					classes = new getgankids_response[num]; 
					for (int i = 0; i < num; i++) {
						classes[i] = (getgankids_response) result.getResponse(i);
					}
					
					ind = 0;
					
					title_class_name.setText(getString(R.string.new_year_title) + " " + classes[ind].class_name);
					
					kids_list.setAdapter(new GanKidsAdapter());
				}
			}
		});
	}
	
	private class GanKidsAdapter extends BaseAdapter {

		private LayoutInflater inflater;

		public GanKidsAdapter() {
			this.inflater = LayoutInflater.from(NewYearActivity1.this);
		} 

		@Override
		public int getCount() {
			return classes[ind].kids.length;
		}

		@Override
		public Object getItem(int position) {
			return  classes[ind].kids[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}
		
		class ViewHolder {
			CheckBox cb;
			CircleImageView im;
			TextView tv;
			
		};

		@SuppressLint("InflateParams")
		@Override
		public View getView(int position, View view, ViewGroup parent) {
			ViewHolder vholder;
			if (view == null) {
				view = inflater.inflate(R.layout.newyear_row_item, null);
				vholder = new ViewHolder();
				vholder.cb = (CheckBox) view.findViewById(R.id.check);
				vholder.im = (CircleImageView) view.findViewById(R.id.profile_image);
				vholder.tv = (TextView) view.findViewById(R.id.kid_name);
				view.setTag(vholder);
			}
			else {
				vholder = (ViewHolder) view.getTag();
			}
			KidDetails current = classes[ind].kids[position];
			
			getKidPicture(current.kid_pic,vholder.im,current.getDeafultKidImg());
			
			vholder.tv.setText(current.kid_name);
			vholder.cb.setTag(Integer.valueOf(position));
			
			if(chosen_kids.containsKey(current.kid_id))
			{
				vholder.cb.setChecked(true);
			}
			else
			{
				vholder.cb.setChecked(false);
			}
			
			
			return view;
		}

	}
	
	public static void getKidPicture(String kid_pic, CircleImageView imgView, int defImageResId) {
		String url = null;
		if(kid_pic != null)
		{
			url = kidPicToUrl(kid_pic);
		}
		
		DisplayImageOptions uilOptions = UILManager.createDefaultDisplayOpyions(defImageResId);
		
		UILManager.imageLoader.displayImage(url, imgView, uilOptions); 
	}
	
	public static String kidPicToUrl(String kid_pic) {
		return JsonTransmitter.PICTURE_HOST + JsonTransmitter.USERS_HOST + kid_pic + ".png";
	}
	
	private void openLogoutPopup() {
		AlertDialog.Builder adb = new AlertDialog.Builder(this);
		//		adb.setView(alertDialogView);
		adb.setTitle(R.string.logount_pop_text);
		adb.setIcon(R.drawable.ic_launcher);
		adb.setPositiveButton(R.string.popup_btn_ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				finish();
			} });
		adb.setNegativeButton(R.string.popup_btn_cancel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// no op
			} });

		adb.show();	
	}
}

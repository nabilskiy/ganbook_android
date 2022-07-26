package com.ganbook.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.ganbook.amazinglist.special.CustomToast;
import com.ganbook.communication.ICompletionHandler;
import com.ganbook.communication.datamodel.ResultObj;
import com.ganbook.communication.json.addclass_response;
import com.ganbook.communication.json.createkindergarten_response;
import com.ganbook.communication.json.getclass_Response;
import com.ganbook.communication.json.transmitter.JsonTransmitter;
import com.ganbook.datamodel.ClassDetails;
import com.ganbook.sharedprefs.SPWriter;
import com.ganbook.ui.CircleImageView;
import com.ganbook.user.User;
import com.ganbook.utils.KeyboardUtils;
import com.project.ganim.R;

import java.util.ArrayList;

public class AddClassActivity extends BaseAppCompatActivity {
	
	// activity_class_name
	
		
	private RecyclerView mRecyclerView;
	private ClassListAdapter mAdapter;
	private EditText add_class_name;
	
	private ClassDetails[] displayLisrArr;
	
	private ArrayList<String> classesToAdd;
	private ArrayList<String> classesToDelete;

	protected static boolean part_of_init_process;
	private boolean part_of_init;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_class_name);

		setActionBar(getString(R.string.add_class_caption), true);

		part_of_init = part_of_init_process;
		part_of_init_process = false;
		
		mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
		add_class_name = (EditText) findViewById(R.id.add_class_name);
		
		classesToAdd = new ArrayList<String>();
		classesToDelete = new ArrayList<String>();
		
		final AddClassActivity a = this;		
		((Button)findViewById(R.id.add_class_btn)).setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				final String newClassName = add_class_name.getText().toString().trim();
				if (newClassName.isEmpty()) {
					CustomToast.show(a, R.string.no_class_name); 
				} else {
					classesToAdd.add(newClassName);
					add_class_name.setText("");
					KeyboardUtils.close(a, add_class_name);
					addToDisplayList(newClassName);
				}
			}
		});
				
		String[] c_arr = User.current.getClassNameArray();
		if (c_arr==null) {
			c_arr = new String[0]; 
		}
		int len = c_arr.length; 
		displayLisrArr = new ClassDetails[len];  
		for (int i = 0; i < len; i++) { 
			displayLisrArr[i] = new ClassDetails(c_arr[i], R.drawable.default_img);
		}
		setClassList();
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

				saveAll();
				break;
			case android.R.id.home:
				onBackPressed();
				break;
		}

		return super.onOptionsItemSelected(item);
	}

	protected void saveAll() {
		if (classesToAdd.isEmpty() && classesToDelete.isEmpty()) {
			CustomToast.show(this, R.string.no_class_name); 
			return;
		}
		String classesToAddStr = "";
		for (String e: classesToAdd) {
			if (classesToAddStr.isEmpty()) {
				classesToAddStr += e;
			} else {
				classesToAddStr += ("," + e);
			}
		}
		
		if (!classesToAddStr.isEmpty()) {		
			sendClassesToAdd(classesToAddStr); // internally calling sendClassesToDelete();
		} 
		else {
			sendClassesToDelete();
		}
	}
	
	
	private void sendClassesToAdd(String classesStr) {
		final AddClassActivity a = this;
		if (part_of_init) {
			call_createkindergarten(classesStr);
		}
		else {
			call_addclass(classesStr);
		}
	}
	
	private void call_createkindergarten(final String classesStr) {
		startProgress(R.string.operation_proceeding);
		final String kindergarten_name = KindergartenDetailsActivity.kindergarten_name;
		final String kindergarten_phone = KindergartenDetailsActivity.kindergarten_phone;
		final String kindergarten_address = KindergartenDetailsActivity.kindergarten_address;
		final String kindergarten_city = KindergartenDetailsActivity.kindergarten_city;
		final String teacher_name = KindergartenDetailsActivity.teacher_name;
		final String teacher_mobile = KindergartenDetailsActivity.teacher_mobile;

		final Activity a = this;		
		JsonTransmitter.send_createkindergarten(
				teacher_name, kindergarten_address, teacher_mobile, 
				kindergarten_name, kindergarten_phone, kindergarten_address, kindergarten_city,
				classesStr,   // (names separated with commas))
				new ICompletionHandler() {	 		
			@Override
			public void onComplete(ResultObj result) {
//				stopProgress();
				if (!result.succeeded) {
					if(JsonTransmitter.NO_NETWORK_MODE.equals(result.result))
					{
						showNotInternetAlert();
						return;
					}
					stopProgress();
					CustomToast.show(a, result.result); 
				} 
				else {
					saveRegisterProcess();
					
					createkindergarten_response response = (createkindergarten_response) result.getResponse(0);
					User.updateKindegartenResponse(response);

					if (!classesToDelete.isEmpty()) {
						sendClassesToDelete(); 
					}
					else {
						performSilentLogin();
					}
				}
			} 
		});
				
	}
	
	private void saveRegisterProcess() {
  		new AsyncTask<Void, Void, Void>() {
			@Override 
			protected Void doInBackground(Void... params) {
				new SPWriter("registerProcessFile").putBool("in_process", false).commit();		
				return null; 
			}
		}.execute();

	}
	
	
	protected void performSilentLogin() {
		call_getclass();		
	}
 
	protected void call_getclass() {
		final Activity a = this;
		String teacher_id = User.getId();
		JsonTransmitter.send_getclass(teacher_id,
				new ICompletionHandler() {			
			@Override
			public void onComplete(ResultObj result) {
				stopProgress();
				if (!result.succeeded) {
					if(JsonTransmitter.NO_NETWORK_MODE.equals(result.result))
					{
						showNotInternetAlert();
						return;
					}
					CustomToast.show(a, result.result); 
				} 
				else {
					getclass_Response response = (getclass_Response) result.getResponse(0);   
					User.updateWithClasses(response);
					finishWithSuccess();
				}
			} 
		});
		
	}

	private void call_addclass(String classesStr) {
		final AddClassActivity a = this;
		String gan_id = User.current.getCurrentGanId();
		startProgress(R.string.operation_proceeding);
		JsonTransmitter.send_addclass( 
				gan_id, classesStr, new ICompletionHandler() {							
					@Override
					public void onComplete(ResultObj result) {
						int jj=234; 
						jj++; 
						stopProgress(); 
						if (!result.succeeded) {
							if(JsonTransmitter.NO_NETWORK_MODE.equals(result.result))
							{
								showNotInternetAlert();
								return;
							}
							String errmsg = result.result;
							CustomToast.show(a, errmsg);
						} else {
							if (!classesToDelete.isEmpty()) {
								sendClassesToDelete();
							}
							else {
								addclass_response response = (addclass_response) result.getResponse(0);
								User.updateKindegartenResponse(response);

								MainActivity.updateDrawerContent();
								finish();
//								finishWithSuccess();
							}
						}
					}
				});
	}
	

	private void sendClassesToDelete() {
//		sssssssss;
	}

	protected void finishWithSuccess() {
		startActivity(new Intent(this, MainActivity.class));
		finish();
	}


	private void addToDisplayList(String newClassName) { 
		ClassDetails[] orig = displayLisrArr;
		displayLisrArr = new ClassDetails[orig.length + 1];
		int i = 0;
		for (; i < orig.length; i++) {
			displayLisrArr[i] = orig[i];
		}
		displayLisrArr[i] = new ClassDetails(newClassName, R.drawable.default_img);
		setClassList();
	}
	
	private void removeFromDisplayList(String classToRemove) {
		ClassDetails[] orig = displayLisrArr;
		int num = 0;
		for (ClassDetails e: orig) {
			if (e.getTitle().equals(classToRemove)) {
				num++;
			}
		}
		int new_i = 0;
		displayLisrArr = new ClassDetails[orig.length - num];
		for (int orig_i = 0; orig_i < orig.length; orig_i++) {
			ClassDetails _class = orig[orig_i];		
			if (!_class.getTitle().equals(classToRemove)) {
				displayLisrArr[new_i++] = _class;
			}
		}
		setClassList();
	}
	
	
	private void setClassList() {
		mRecyclerView.setHasFixedSize(true);
		mRecyclerView.setLayoutManager(new LinearLayoutManager(this));		
		mAdapter = new ClassListAdapter(displayLisrArr);
		mRecyclerView.setAdapter(mAdapter);
		mRecyclerView.setItemAnimator(new DefaultItemAnimator());
	}


	class ClassListAdapter extends RecyclerView.Adapter<ClassListAdapter.ViewHolder> {
		private ClassDetails[] itemsData;

		public ClassListAdapter(ClassDetails[] itemsData) {
			this.itemsData = itemsData;
		}

		
		public class ViewHolder extends RecyclerView.ViewHolder {
			private TextView txtViewTitle;
			private CircleImageView imgViewIcon;
			private ImageButton delete_row;


			public ViewHolder(View itemLayoutView) {
				super(itemLayoutView);
				txtViewTitle = (TextView) itemLayoutView.findViewById(R.id.item_title);
				imgViewIcon = (CircleImageView) itemLayoutView.findViewById(R.id.profile_image);
				delete_row = (ImageButton) itemLayoutView.findViewById(R.id.delete_row);
			}
		}

		@Override
		public int getItemCount() {
			return itemsData.length;
		}

		@Override
		public void onBindViewHolder(ViewHolder viewHolder, final int position) {
			 final String this_className = itemsData[position].getTitle();
			 viewHolder.txtViewTitle.setText(this_className);
		     viewHolder.imgViewIcon.setImageResource(itemsData[position].getImageUrl());		     
		}

		protected ArrayList<String> removeFromArray(ArrayList<String> origArr, String toRemove) {
			ArrayList<String> newArr = new ArrayList<String>();
			for (String s: origArr) {
				if (!toRemove.equals(s)) {
					newArr.add(s);
				}
			}
			return newArr;
		}

		@SuppressLint("InflateParams")
		@Override
		public ViewHolder onCreateViewHolder(ViewGroup parent, int arg1) {
			View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_class_inflator, null);
			ViewHolder viewHolder = new ViewHolder(itemLayoutView);
			return viewHolder;
		}
	}
}

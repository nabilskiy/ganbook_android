package com.ganbook.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ganbook.amazinglist.special.CustomToast;
import com.ganbook.app.MyApp;
import com.ganbook.communication.ICompletionHandler;
import com.ganbook.communication.datamodel.ResultObj;
import com.ganbook.communication.json.GetUserKids_Response;
import com.ganbook.communication.json.transmitter.JsonTransmitter;
import com.ganbook.dialogs.Dialogs;
import com.ganbook.fragments.ContactListFragment;
import com.ganbook.fragments.UnattachedKidFragment;
import com.ganbook.interfaces.KidWithoutGanInterface;
import com.ganbook.models.SuccessAnswer;
import com.ganbook.models.events.ChangeClassEvent;
import com.ganbook.user.User;
import com.ganbook.utils.AlertUtils;
import com.ganbook.utils.Const;

import com.project.ganim.R;

import org.greenrobot.eventbus.EventBus;

import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChooseClassActivity extends BaseAppCompatActivity {

	private static final String TAG = ChooseClassActivity.class.getName();
	private static Activity inst;
	
	private ListView mListView;
//    private String[] mData = new String[] { "xxx", "yyy", "zzz", "aaa" };
//    private String[] mID = new String[] { "3620", "3620", "3620", "3620" };
    
    private BaseAdapter mAdapter;
    private int mLastCorrectPosition = -1;
    private int mButtonPosition = 0;
    
    private String kid_id;
	private String kid_name;
    
    private boolean fromUnattachedFragment = false;
	private boolean fromContactList = false;
	private KidWithoutGanInterface kidWithoutGanInterface;
	private String [] class_names;

	SweetAlertDialog progress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_choose_class);

		if(User.isParent() || User.isTeacher() || User.isStaff()) {
			Bundle bundle = getIntent().getExtras();

			if (bundle != null) {

				kid_id = bundle.getString(Const.KID_ID);
				kid_name = bundle.getString(Const.KID_NAME);
				fromContactList = bundle.getBoolean(Const.FROM_CONTACT_LIST);
				fromUnattachedFragment = bundle.getBoolean("withoutGan");
			}

			MyApp.sendAnalytics("choose-class-ui", "choose-class-ui-" + User.getId(), "choose-class-ui", "ChooseClass");


			inst = this;

//		fromUnattachedFragment = UnattachedKidFragment.fromUnattachedFragment;

			UnattachedKidFragment.fromUnattachedFragment = false;

			if (fromUnattachedFragment) {
				kid_id = User.current.getCurrentKidId();
			} else if (!fromContactList) {
				kid_id = EnterCodeActivity.kid_id;
			}




		String title = "";
		if(fromContactList)
		{
			title = getString(R.string.change_class);
		} else {
			title = getString(R.string.select_class);
		}

		setActionBar(title, false);

		if(fromContactList)
		{
			((TextView)(findViewById(R.id.please_choose_class_text))).setVisibility(View.GONE);
			((TextView)(findViewById(R.id.school_name))).setText(getString(R.string.choose_kid_class,kid_name));
		}
		else
		{
			((TextView)(findViewById(R.id.please_choose_class_text))).setVisibility(View.VISIBLE);
			((TextView)(findViewById(R.id.school_name))).setText(MyApp.gan_name);
		}

		mListView = (ListView) findViewById(R.id.class_list);
        mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        mAdapter = new ArrayAdapter<String>(this,
                        android.R.layout.simple_list_item_activated_1, MyApp.class_names != null
				? MyApp.class_names : new String[1]);
        mListView.setAdapter(mAdapter);

        mListView.setSelector(new ColorDrawable(0));

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                            int position, long id) {
            	if(position != mLastCorrectPosition)
            	{
            		mListView.setItemChecked(mLastCorrectPosition, false);

                	mLastCorrectPosition = position;

                	if (position == mButtonPosition) {
                		mListView.setItemChecked(mButtonPosition, true);
                	}
            	}

            }
        });
        
		progress = AlertUtils.createProgressDialog(this, getString(R.string.operation_proceeding));
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

				if(mLastCorrectPosition != -1)
				{
					if(fromContactList)
					{
						call_updateclass();
					}
					else {
						call_setclass();
					}
				}
				break;
		}

		return super.onOptionsItemSelected(item);
	}

	private void call_updateclass() {

		progress.show();

		Call<SuccessAnswer> updateClass = ganbookApiInterfacePOST.updateClass(kid_id,
				MyApp.class_ids[mLastCorrectPosition]);

		updateClass.enqueue(new Callback<SuccessAnswer>() {
			@Override
			public void onResponse(Call<SuccessAnswer> call, Response<SuccessAnswer> response) {

				progress.hide();

				SuccessAnswer successAnswer = response.body();

				if (successAnswer != null) {

					Log.d(TAG, "onResponse: update class = " + successAnswer);

					if (successAnswer.isSuccess()) {

						ContactListFragment.updateContactListAfterDisconnect(kid_id);
						EventBus.getDefault().postSticky(new ChangeClassEvent(true, kid_id));
						finish();
					} else {

						//unsucces
						Toast.makeText(ChooseClassActivity.this, R.string.error_update_class, Toast.LENGTH_SHORT).show();
					}
				}
			}

			@Override
			public void onFailure(Call<SuccessAnswer> call, Throwable t) {

				CustomToast.show(ChooseClassActivity.this, R.string.error_update_class);

				progress.hide();
				Log.e(TAG, "onFailure: error while update class = " + Log.getStackTraceString(t));
				
			}
		});
	}
	
	private void call_setclass() {

		progress.show();

		Call<SuccessAnswer> call = ganbookApiInterfacePOST.setClass(kid_id, MyApp.class_ids[mLastCorrectPosition]);

		call.enqueue(new Callback<SuccessAnswer>() {
			@Override
			public void onResponse(Call<SuccessAnswer> call, Response<SuccessAnswer> response) {

				progress.hide();

				SuccessAnswer successAnswer = response.body();

				if (successAnswer != null) {

					if (successAnswer.isSuccess()) {

						call_getKids();
					} else {

						Toast.makeText(ChooseClassActivity.this, R.string.error_set_class, Toast.LENGTH_SHORT).show();
					}
				}
			}

			@Override
			public void onFailure(Call<SuccessAnswer> call, Throwable t) {

				CustomToast.show(ChooseClassActivity.this, R.string.error_set_class);
				Log.e(TAG, "onFailure: error while set class = " + Log.getStackTraceString(t));
			}
		});
	}



	public void call_getKids() {

		startProgress(R.string.operation_proceeding);
		String userId = User.getId();
		JsonTransmitter.send_getuserkids(userId, new ICompletionHandler() {
			@Override
			public void onComplete(ResultObj result) {
				stopProgress();

				if (!result.succeeded) {
					if(JsonTransmitter.NO_NETWORK_MODE.equals(result.result))
					{
						CustomToast.show(ChooseClassActivity.this, R.string.internet_offline);
						Dialogs.errorDialogWithButton(ChooseClassActivity.this, "Error!", getString(R.string.internet_offline), "OK");
						return;
					}
					else
					{
						String errmsg = result.result;
						CustomToast.show(ChooseClassActivity.this, errmsg);
						return;
					}
				}
				int num = result.getNumResponses();
				GetUserKids_Response[] responses = new GetUserKids_Response[num];
				for (int i = 0; i < num; i++) {
					responses[i] = (GetUserKids_Response) result.getResponse(i);
				}
				User.updateWithUserkids(responses);

				if(MainActivity.inst == null)
					start(MainActivity.class);
				else {
					MainActivity.updateDrawerContent();

					AddKidActivity.callFinish();
					ParentDetailsActivity.callFinish();
					EnterCodeActivity.callFinish();

					Intent returnIntent = new Intent();
					setResult(Activity.RESULT_OK, returnIntent);
					finish();
					inst = null;
				}

				//				if(MainActivity.inst == null)
				//				{
				//					start(MainActivity.class);
				//				}
				//				else
				//				{
				//					MainActivity.updateDrawerContent();
				//
				//					AddKidActivity.callFinish();
				//					ParentDetailsActivity.callFinish();
				//
				//					MainActivity.openDrawer();
				//					callFinish();
				//				}

				MainActivity.refresh();
			}
		});

	}
	
//	protected void call_getKids() {
//		final ChooseClassActivity a = this;
//		String userId = User.getId();
//
//		Call<List<GetUserKids_Response>> call = ganbookApiInterfaceGET.getUserKids(userId);
//
//		call.enqueue(new Callback<List<GetUserKids_Response>>() {
//			@Override
//			public void onResponse(Call<List<GetUserKids_Response>> call, Response<List<GetUserKids_Response>> response) {
//
//				progress.hide();
//
//				List<GetUserKids_Response> responsesList = response.body();
//
//				Log.d(TAG, "onResponse: kids = " + responsesList);
//				Log.d(TAG, "onResponse: size "+ " size = " + responsesList.size());
//
//				if (responsesList != null) {
//
//				int num = responsesList.size();
//				GetUserKids_Response[] responses = new GetUserKids_Response[num];
//				for (int i = 0; i < num; i++) {
//					responses[i] = responsesList.get(i);
//					Log.d(TAG, "onResponse: kid = " + responsesList.get(i));
//				}
//
//				User.updateWithUserkids(responses);
//
//				if(MainActivity.inst == null)
//					start(MainActivity.class);
//				else {
//					MainActivity.updateDrawerContent();
//
//					AddKidActivity.callFinish();
//					ParentDetailsActivity.callFinish();
//					EnterCodeActivity.callFinish();
//
//					Intent returnIntent = new Intent();
//					setResult(Activity.RESULT_OK, returnIntent);
//					finish();
//					inst = null;
//				}
//				} else {
//
//					CustomToast.show(ChooseClassActivity.this, R.string.no_data);
//				}
//			}
//
//			@Override
//			public void onFailure(Call<List<GetUserKids_Response>> call, Throwable t) {
//
//				CustomToast.show(ChooseClassActivity.this, R.string.error_get_kids);
//				Log.e(TAG, "onFailure: error while get user kids = " + Log.getStackTraceString(t));
//				progress.hide();
//			}
//		});
//	}
	
	@Override
	public void onBackPressed() {

		if(fromUnattachedFragment || fromContactList)
		{
			super.onBackPressed();
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (this == inst) {
			inst = null;
		}
		progress.dismiss();
	}

	public static void callFinish() {
		if (inst != null) {
			inst.finish();
		}
	}
}

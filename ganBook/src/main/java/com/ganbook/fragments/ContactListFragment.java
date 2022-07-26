package com.ganbook.fragments;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ganbook.activities.MainActivity;
import com.ganbook.amazinglist.special.CustomToast;
import com.ganbook.app.MyApp;
import com.ganbook.communication.ICompletionHandler;
import com.ganbook.communication.datamodel.ResultObj;
import com.ganbook.communication.json.BaseResponse;
import com.ganbook.communication.json.transmitter.JsonTransmitter;
import com.ganbook.models.GetParentAnswer;
import com.ganbook.ui.ContactListAdapter;
import com.ganbook.user.User;
import com.ganbook.utils.ActiveUtils;
import com.ganbook.utils.CurrentYear;

import com.project.ganim.R;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

public class ContactListFragment extends BaseFragment implements OnRefreshListener {
	private Context context;
	private SwipeRefreshLayout mSwipeRefreshLayout;
	private ListView contact_list;
	private EditText search_bar;
	private ContactListAdapter _adapter;
	private Button select;
	private TextView totalcontacts;
	private TextView teacher_name;
	private Button cancel_search ;
	View headerview;

	ProgressBar pbHeaderProgress;

	private String totalPrefStr;

	private static ArrayList<GetParentAnswer> parentArr;

	private static ContactListFragment inst;

	private boolean forceRefresh = false;

	private final Initializer initializer = new Initializer();
	
	private boolean justCreated = false;

//	public static boolean fromContactList;
//	public static String kid_id;
//	public static String kid_name;

	class Initializer {
		public void onCreate() {
			inst = ContactListFragment.this;
			
			justCreated = true;
			
			if (User.isTeacher()) {
				totalPrefStr = getResources().getString(R.string.total_str);
			} else {
				totalPrefStr = getResources().getString(R.string.total_parents_str);
			}
		}

		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

			MyApp.sendAnalytics("contacts-gan-ui", "contacts-gan"+User.current.getCurrentGanId()+"-class"+User.current.getCurrentClassId()+"-ui"+ User.getId(), "contacts-gan-ui", "ContactsGan");


			setActionBarValues();		
			View _view = inflater.inflate(R.layout.contact_frag, container, false);		
			base_onCreateView();

			pbHeaderProgress = (ProgressBar)  _view.findViewById(R.id.pbHeaderProgress);
			mSwipeRefreshLayout = (SwipeRefreshLayout) _view.findViewById(R.id.refresh_layout);

			mSwipeRefreshLayout.setOnRefreshListener(ContactListFragment.this);
			mSwipeRefreshLayout.setColorScheme(android.R.color.holo_blue_bright, 
					android.R.color.holo_green_light, 
					android.R.color.holo_orange_light, 
					android.R.color.holo_red_light);

			contact_list = (ListView) _view.findViewById(R.id.contact_list);
			search_bar  = (EditText) _view.findViewById(R.id.search_bar);
			
			search_bar.addTextChangedListener(new TextWatcher() {
				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
					if (_adapter != null) {
						_adapter.getFilter().filter(s);
					}
				}

				@Override
				public void beforeTextChanged(CharSequence s, int start, int count, int after) {

				}

				@Override
				public void afterTextChanged(Editable s) { 
//					String text = search_bar.getText().toString().toLowerCase(Locale.getDefault());
//					if(!"".equals(text))
//					{
//						if (_adapter != null) {
//							_adapter.filter(text);
//						}
//					}
				}
			});
			
			cancel_search  = (Button) _view.findViewById(R.id.cancel_search);

			cancel_search.setOnClickListener(new View.OnClickListener() {			
				@Override
				public void onClick(View v) {
					search_bar.setText("");				
				}
			});

			select = (Button) getActivity().findViewById(R.id.select);
			select.setVisibility(View.GONE);
			totalcontacts = new TextView(context);
			inflater = LayoutInflater.from(getActivity());
			headerview = inflater.inflate(R.layout.contact_list_header, null);
			teacher_name = (TextView) headerview.findViewById(R.id.contact_header_text);

			teacher_name.setOnClickListener(new View.OnClickListener() {			
				@Override
				public void onClick(View v) {
					//					if (User.isTeacher()) {gg
					//						start(UpdateTeacherDetailsActivity.class);
					//					}  
					ContactListFragment.onContactEntered();
					Log.d("TEACHER CLICKED", "CLICKED");
//					SingleContactProfileFragement.selContact = getTeacherDetails();
					
					MyApp.selContact = getTeacherDetails(); 
					MyApp.async_writeParentToLocaCache();
					
					((MainActivity)context).moveToTab(FragmentType.Contact_Profile); 
				}
			});

//			setTeacherName();

//			if (forceRefresh) {
//				forceRefresh = false;
//				refreshContent(true);
//			}		
			
			handleFragmentVisible();
			Log.i("noanoa","contact list onCreateView ");
			return _view;
		}

		public void onBecomingVisible() {
			Log.i("noanoa","contacts list onBecomingVisible ");
//			handleFragmentVisible();
			refreshContent(false);
		}

		public void onActivityCreated(Bundle savedInstanceState) {
			totalcontacts = new TextView(context);
			totalcontacts.setTextColor(getResources().getColor(R.color.text_color));
			totalcontacts.setGravity(Gravity.START);
			totalcontacts.setTypeface(Typeface.SANS_SERIF);
			totalcontacts.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
			contact_list.addHeaderView(headerview);
			contact_list.addFooterView(totalcontacts);

		}
		
		public void onStart() {
			boolean _justCreated = justCreated;
			justCreated = false; //!
			refreshContent(_justCreated);
		}

	};
	
	public static void clearContacts() {
		parentArr = null; // force refresh
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
        Log.d("ON ATTACH", "ATTACH");



	}







	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initializer.onCreate();
	}

	@Override
	public void onDestroy() {

		super.onDestroy();
		EventBus.getDefault().unregister(this);
		if (this == inst) {
			inst = null;
		}

	}

	@SuppressWarnings("deprecation")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		return initializer.onCreateView(inflater, container, savedInstanceState);
	}


	private void setActionBarValues() {
		titleTextId = R.string.contact_list_header;
		saveBtnVisibility = View.GONE;
		drawerMenuVisibility = View.VISIBLE;
	}

	private void setTeacherName() {
		String _name = "";
		//		if (User.isTeacher()) {
		//			_name = User.getName();
		//		}

		_name = User.current.getCurrentTeacherName();
		teacher_name.setText(_name);   
	}

	public static void updateContactListAfterDisconnect(String kid_id)
	{
		if (inst == null)
			return;
		inst._adapter.updateContactListAfterDisconnect(kid_id);
	}


	@Override
	public void onStart() {
		super.onStart();
		initializer.onStart();

        Log.d("START CONTACT", "START CONTACT");

    }

	@Override
	public void onBecomingVisible() {
		super.onBecomingVisible();
		initializer.onBecomingVisible();
		Log.d("VISIBLE", "VISIBLE");
	}


	@Override
	public void onRefresh() {
		mSwipeRefreshLayout.setRefreshing(false);
		refreshContent(true);
	} 

	@Override
	public void onResume() {

		super.onResume();
        Log.d("RESUME CONTACT", "RESUME");

        int jj=234;
		jj++;
	}



	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);		
		initializer.onActivityCreated(savedInstanceState);
	}

	private void __setTotalText() {
		if (parentArr != null) {
			int _size = parentArr.size(); 
			totalcontacts.setText(totalPrefStr + " " + _size); 
		}
	}


	public static void forceContentRefresh() {
		if (inst != null) {
			inst.base_forceContentRefresh();
			inst.refreshContent(true);
		}
	}

	public static void forceSetTeacherName() {
		if (inst != null) {
			inst.setTeacherName();
		}
	}

	public static void __startProgress() {
		if (inst != null) {
			inst.startProgress(R.string.operation_proceeding);
		}
	}

	public static void __stopProgress() {
		if (inst != null) {
			inst.stopProgress();
		}
	}
	
	public static void showNoInternet()
	{
		if(inst == null)
		{
			return;
		}
				
		inst.showNotInternetAlert();
        
        inst.contact_list.addHeaderView(inst.tryAgainView);
        
//        inst._adapter = new ContactListAdapter(inst.context, parentArr);
		inst.contact_list.setAdapter(inst._adapter);
        
		inst.contact_list.removeHeaderView(inst.headerview);
		inst.contact_list.removeFooterView(inst.totalcontacts);
	}

	private void refreshContent(boolean _forceRefresh) {
		// gggFix below commented out due to occationally resulting in empty content
		if (mSwipeRefreshLayout==null) { // fragment not yet constructed
//			forceRefresh = true;
			return;
		}

		if (!_forceRefresh && (parentArr != null && parentArr.size() > 0)) { 
			mSwipeRefreshLayout.setRefreshing(false);
			__setDynamicValues();
			return;
		}

		mSwipeRefreshLayout.setRefreshing(false);
		parentArr = new ArrayList<GetParentAnswer>();
		String class_id = User.current.getCurrentClassId();
		String year = CurrentYear.get();
//		startProgress(R.string.operation_proceeding);


		pbHeaderProgress.setVisibility(View.VISIBLE);
		contact_list.setVisibility(View.GONE);


		JsonTransmitter.send_getparents(class_id, year, new ICompletionHandler() {
			@Override 
			public void onComplete(ResultObj result) {
//				stopProgress();

				pbHeaderProgress.setVisibility(View.GONE);
				contact_list.setVisibility(View.VISIBLE);

				if(JsonTransmitter.NO_NETWORK_MODE.equals(result.result))
				{
					showNoInternet();
				        
					return;
				}
				
				parentArr = new ArrayList<GetParentAnswer>(); //!
				mSwipeRefreshLayout.setRefreshing(false);
				if (!result.succeeded) {
					String errmsg = result.result;
					CustomToast.show(activity(), errmsg);
				} 

				ArrayList<BaseResponse> resArr = result.getResponseArray();
				for (BaseResponse br: resArr) {
					if(User.isParent())
					{
						if(ActiveUtils.isActive(((GetParentAnswer)br).kid_active))
						{
							parentArr.add((GetParentAnswer)br);
						}
					}
					else
					{
						parentArr.add((GetParentAnswer)br);

					}
				} 
				int len = parentArr.size();
				if (len == 0) {
//					CustomToast.show(activity(), R.string.empty_contact_list);
				} 
				__setDynamicValues();
				
				if(User.isTeacher())
				{
					int pending_parents = 0;
					
					for (GetParentAnswer response : parentArr) {
						if(!ActiveUtils.isActive(response.kid_active))
						{
							pending_parents ++;
						}
					}
					
					User.current.updatePendingParents(String.valueOf(pending_parents));
					MainScreenFragment.initContactTabBadge();
					MainActivity.updateDrawerContent();
				}
			}


		});
	} 

	private GetParentAnswer getTeacherDetails() {

		GetParentAnswer GetParentAnswer = new GetParentAnswer();

		GetParentAnswer.parent_first_name = User.current.getCurrentTeacherFirstName();
		GetParentAnswer.parent_last_name = User.current.getCurrentTeacherLastName();
		GetParentAnswer.parent_mobile = User.current.getCurrentTeacherMobile();
		GetParentAnswer.parent_phone = User.current.getCurrentGanPhone();
		GetParentAnswer.parent_mail = User.current.getCurrentTeacherMail();
		GetParentAnswer.parent_address = User.current.getCurrentGanAddress();
		GetParentAnswer.parent_city = User.current.getCurrentGanCity();
		GetParentAnswer.type = User.Type_Teacher;

		return GetParentAnswer;
	}

	protected void __setDynamicValues() {
		contact_list.removeHeaderView(tryAgainView);
		setTeacherName();
//		_adapter = new ContactListAdapter(context, parentArr);
		_adapter.notifyDataSetChanged();
		contact_list.setAdapter(_adapter);
		__setTotalText();
	}


	public static void call_handleFragmentVisible() {
		if (inst != null) {
			inst.handleFragmentVisible();
		}
	}


	public static void setFragmentVisible() {
		if (inst != null) {
			inst.handleFragmentVisible();
		}
	}


	public static void onContactEntered() {
		if (inst != null) {
			inst.search_bar.setText("");
		}		
	}


	public static void setTitleVisibility(int visible) {
		if (inst != null) {
			if(visible == View.VISIBLE)
			{
				inst.handleFragmentVisible();
			}
			inst.doSetTitleVisibility(visible); 
		}
	}

}

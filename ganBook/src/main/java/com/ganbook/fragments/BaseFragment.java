package com.ganbook.fragments;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.ganbook.activities.MainActivity;
import com.ganbook.app.MyApp;
import com.ganbook.dialogs.Dialogs;
import com.ganbook.interfaces.GanbookApiInterface;
import com.project.ganim.R;

import javax.inject.Inject;
import javax.inject.Named;

public abstract class BaseFragment extends Fragment {
	
	protected static final int SHOW_ADD_BUTTON = 0;
	protected static final int HIDE_ADD_BUTTON = -1;
	
	protected LinearLayout action_bar_main_title;
//	protected TextView title;
	protected Button save_btn;
	protected ImageButton menu;
	protected ImageButton up_navigation;
	protected ImageView add_btn;

	
	protected int titleTextId;
	public String titleTextText;  
	protected int saveBtnTextId = HIDE_ADD_BUTTON;
	protected int saveBtnVisibility = View.GONE;

	protected int upNavigationVisibility = View.GONE;
	
	protected int drawerMenuVisibility = View.INVISIBLE;
	
	private boolean forceSetTitle = false;
	
	View tryAgainView;

	@Inject
	@Named("POST")
	GanbookApiInterface ganbookApiInterfacePOST;

	@Inject
	@Named("GET")
	GanbookApiInterface ganbookApiInterfaceGET;


	public void onBecomingVisible() {
		
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		((MyApp) getActivity().getApplication()).getGanbookApiComponent().inject(this);
	}

	public void showNotInternetAlert()
	{
		Dialogs.errorDialogWithButton(activity(), "Error!", getString(R.string.internet_offline), "OK");

	}
	
	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

		tryAgainView = inflater.inflate(R.layout.header_error_layout, null);
		return super.onCreateView(inflater, container, savedInstanceState);
	}
	
	protected void base_onCreateView() {
		// actionbar_mainscreen.xml
		if (activity()==null) return;
//		title = (TextView) activity().findViewById(R.id.title);
		save_btn = (Button) activity().findViewById(R.id.select);
		menu = (ImageButton) activity().findViewById(R.id.menu);
		add_btn = (ImageView) activity().findViewById(R.id.add_btn);
		action_bar_main_title = (LinearLayout) activity().findViewById(R.id.action_bar_main_title);
		up_navigation = (ImageButton) activity().findViewById(R.id.up_navigation);
//		add_btn.setVisibility(View.GONE);


	}
	
	protected void base_forceContentRefresh() {
		Log.i("noanoa","base_forceContentRefresh ");
//		handleFragmentVisible();
	}

	public void handleFragmentVisible() {
		int jj=234;
		jj++;		
//		this.onBecomingVisible();
		
		if (menu != null) {
			menu.setVisibility(drawerMenuVisibility);
		}
		
		Log.i("noanoa","titleTextText "+titleTextText);
		
//		if (title != null) {
//			doSetTitleVisibility(View.VISIBLE);
//			if (StrUtils.notEmpty(titleTextText)) {
//				title.setText(titleTextText);
//			} else {
//				String first_name = "";
//				if(User.current.isParent())
//				{
//					first_name = StrUtils.emptyIfNull(User.current.getCurrentKid().kid_name);
//				}
//				else
//				{
//					first_name = StrUtils.emptyIfNull(User.current.getCurrentGanName());
//				}
//				String class_name = StrUtils.emptyIfNull(User.current.getCurrentClassName());
//
//				String titleTxt = first_name;
//
//				if(!StrUtils.isEmpty(class_name))
//				{
//					titleTxt += " - " + class_name;
//				}
//
//				title.setText(titleTxt);
//			}
//		}
		
		if (save_btn != null) {
			if (saveBtnTextId == SHOW_ADD_BUTTON) {
				saveBtnVisibility = View.GONE;
				save_btn.setVisibility(saveBtnVisibility);
				add_btn.setVisibility(View.VISIBLE);
			}
			else { 
				add_btn.setVisibility(View.GONE);  
				save_btn.setVisibility(saveBtnVisibility);
				if (saveBtnTextId == -1) {
					save_btn.setText("");
				} else {
					save_btn.setText(saveBtnTextId);
				}
			}
		}

		if(up_navigation != null) {
			if(upNavigationVisibility == View.VISIBLE)
			{
				up_navigation.setVisibility(View.VISIBLE);
				up_navigation.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View arg0) {
						activity().onBackPressed();
					}
				});
			}
			else
			{
				up_navigation.setVisibility(View.GONE);
			}
		}
	}
	 
	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		
		if (isVisibleToUser) {
//			String name = this.getClass().getSimpleName();
			Log.i("noanoa","base fragment handleFragmentVisible " + this);
			onBecomingVisible();
		}
	}

	@Override
	public void setMenuVisibility(final boolean visible) {
		Log.i("noanoa","setMenuVisibility ");
		super.setMenuVisibility(visible);
//		if (visible) {
//			String name = this.getClass().getSimpleName();
//			handleFragmentVisible();
//			int jj=24;
//			jj++; 
//		}
	}	
	
	protected MainActivity activity() {
		return (MainActivity) getActivity();
	}

	protected void start(Class<?> activityToOpen) {
		activity().startActivity(new Intent(activity(), activityToOpen));
	}
	
	
	protected void startProgress(int msgResId) {
		activity().startProgress(msgResId);
	}
	
	protected void startProgress(String msg) {
		if (activity()==null) return;
		activity().startProgress(msg);
	}
	
	
	protected void stopProgress() {
		if (activity()==null) return;
		activity().stopProgress();
	}

	public boolean forceSetTitle() {
		return forceSetTitle;
	}

	public void setForceSetTitle() {
		forceSetTitle = true;
	}
	
	public void clearSetTitle() {
		forceSetTitle = false;
	}
	
	
	protected void doSetTitleVisibility(int visibility) {
//		if (title != null) {
//			title.setVisibility(visibility);
//		}
//		if (add_btn != null && User.isTeacher()) {
//			add_btn.setVisibility(visibility);
//		}
		
	}


}

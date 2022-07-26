package com.ganbook.activities;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.widget.TextView;

import com.ganbook.amazinglist.special.CustomToast;
import com.ganbook.app.MyApp;
import com.ganbook.dialogs.Dialogs;
import com.ganbook.fragments.FragmentType;
import com.ganbook.interfaces.GanbookApiInterface;
import com.ganbook.ui.CustomProgressDialog;
import com.project.ganim.R;

import javax.inject.Inject;
import javax.inject.Named;

public abstract class BaseAppCompatActivity extends AppCompatActivity {
//	private static final String PROGRESS_TITLE = "Ganbook";

	@Inject
	@Named("POST")
	GanbookApiInterface ganbookApiInterfacePOST;

	@Inject
	@Named("GET")
	GanbookApiInterface ganbookApiInterfaceGET;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		((MyApp) getApplication()).getGanbookApiComponent().inject(this);
	}

	protected volatile CustomProgressDialog progressDialog;
	public Fragment cur_fragment;
	public FragmentManager fragmentManager = getSupportFragmentManager();;
	public static volatile FragmentType fragmentToMoveTo;
	TextView toolbarTitle;

	protected void setActionBar(String title, boolean showBackButton) {

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

		setSupportActionBar(toolbar);

		if (showBackButton) {

			getSupportActionBar().setDisplayHomeAsUpEnabled(true);

			Drawable upArrow = getResources().getDrawable(R.drawable.up_navigation);

			getSupportActionBar().setHomeAsUpIndicator(upArrow);
		}

		getSupportActionBar().setDisplayShowTitleEnabled(false);

		toolbarTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);

		setToolbarTitle(title);
	}

	public void setToolbarTitle(String text) {

		if (toolbarTitle != null) {

			toolbarTitle.setText(text);
		}
	}

	public static void configActionBar(ActionBar bar, int customViewId) {
		bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#F7F7F7")));
		bar.setDisplayShowHomeEnabled(false);
		bar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		bar.setCustomView(customViewId);
	}


	@SuppressWarnings("rawtypes")
	public void start(Class _activity) {
		this.startActivity(new Intent(this, _activity));
	}
	
	@SuppressWarnings("rawtypes")
	void startForResult(Class _activity, int code) {
		this.startActivityForResult(new Intent(this, _activity), code);
	}

	public void timedProgress(int msgResId, long interval) {
		startProgress(msgResId);
		new Handler().postDelayed(new Runnable() {			
			@Override
			public void run() {
				stopProgress();
			}
		}, interval);
	}
	
	public void startProgress(int msgResId) {
		startProgress(getResources().getString(msgResId));
	}

	public void startProgress(String msg) {
		stopProgress(); // close prior
		(progressDialog = new CustomProgressDialog(Color.BLACK)).show(this, msg); 
	}
	
	public void stopProgress() {
		if (progressDialog != null) {
			progressDialog.stopProgress();
			progressDialog = null;
		}
	}
	
	
	@Override
	protected void onStop() {
		super.onStop();
		stopProgress();
//		((MyApp)this.getApplication()).startActivityTransitionTimer();
	}

	@Override
	protected void onPause() {
		super.onPause();
		stopProgress();
		((MyApp)this.getApplication()).startActivityTransitionTimer();
	}
	
	
	void toast(String msg) {
		CustomToast.show(this, msg);
	}
	
	public void showNotInternetAlert()
	{
		Dialogs.errorDialogWithButton(this, "Error!", getString(R.string.internet_offline), "OK");

	}

	@Override
	protected void onStart() {
		super.onStart();

//		MyApp myApp = (MyApp)this.getApplication();
//		if (myApp.wasInBackground)
//		{
//			int i=0;
//			MainActivity.refreshApp();
////			FavoriteActivity.wasShown = false;
//			//Do specific came-here-from-background code
//		}
//
//		myApp.stopActivityTransitionTimer();

	}

	@Override
	protected void onResume() {
		super.onResume();

		MyApp myApp = (MyApp)this.getApplication();
		if (myApp.wasInBackground)
		{
			int i=0;
			MainActivity.refreshApp();
//			FavoriteActivity.wasShown = false;
			//Do specific came-here-from-background code
		}

		myApp.stopActivityTransitionTimer();
	}

	public Fragment moveToTab(FragmentType type) {
		String tag = type.name();
		Fragment fragInst = getSupportFragmentManager().findFragmentByTag(tag);
		fragmentManager = getSupportFragmentManager();
		if (null == fragInst) {
			fragInst = __createFragment(type);
			FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
			fragmentTransaction.add(R.id.content_frame, fragInst, tag);
			fragmentTransaction.addToBackStack(tag);
			fragmentTransaction.commit();
		}
		else {
			fragmentManager.popBackStack(tag, 0);
		}

		FragmentType _fragmentToMoveTo = fragmentToMoveTo;
		fragmentToMoveTo = null;
		if (_fragmentToMoveTo != null) {
			if (_fragmentToMoveTo == FragmentType.Single_Image) {
//				getActionBar().hide();
			}
		}
		return (cur_fragment = fragInst);
	}

	private static Fragment __createFragment(FragmentType f_type) {
		Class clazz = f_type.clazz;
		try {
			return (Fragment)clazz.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

}

package com.ganbook.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;

import com.ganbook.app.MyApp;
import com.ganbook.ui.CustomProgressDialog;
import com.project.ganim.R;

abstract class BaseActivity extends Activity{
	
	protected volatile CustomProgressDialog progressDialog;

	protected void setActionBar(int customViewId) {
		android.app.ActionBar bar = getActionBar();
		BaseAppCompatActivity.configActionBar(bar, customViewId);
	}
	
	void start(Class<?> _activity) {
		this.startActivity(new Intent(this, _activity));
	}
	
 
	protected void startProgress(int msgResId) {
		startProgress(getResources().getString(msgResId));
	}

	protected void startProgress(String msg) {
		stopProgress(); // close prior
		(progressDialog = new CustomProgressDialog(Color.BLACK)).show(this, msg); 
	}
	
	protected void stopProgress() {
		if (progressDialog != null) {
			progressDialog.stopProgress();
			progressDialog = null;
		}
	}

	public void showNotInternetAlert()
	{
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        
        dialogBuilder.setNeutralButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
//            	dialogBuilder.dism
            }
        });
        
        dialogBuilder.setMessage(getString(R.string.internet_offline));
        
        dialogBuilder.setTitle(R.string.error_offline);
        
       // dialogBuilder.setCancelable(false);

        dialogBuilder.show();
	}

	@Override
	protected void onStart() {
		super.onStart();

		MyApp myApp = (MyApp)this.getApplication();
		if (myApp.wasInBackground)
		{
			int i=0;
			MainActivity.refreshApp();
			//Do specific came-here-from-background code
		}

		myApp.stopActivityTransitionTimer();

	}

	@Override
	protected void onStop() {
		super.onStop();

		((MyApp)this.getApplication()).startActivityTransitionTimer();
	}
}

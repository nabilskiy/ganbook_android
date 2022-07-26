package com.ganbook.ui;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.project.ganim.R;

public class CustomProgressDialog { 

	private ProgressDialog dialog;

	public CustomProgressDialog() {		
	}
	
	public CustomProgressDialog(int textColor) {
	} 


	public void show(Context context, String msg) {
		showTimed(context, msg, -1, null); 
	}
	 
	@SuppressLint("InflateParams")
	public void showTimed(Context context, String msg, long interval, final Runnable onComplete) { 
		dialog = new ProgressDialog(context, R.style.MyTheme1);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		LayoutInflater inflater = LayoutInflater.from(context);
		View v = inflater.inflate(R.layout.custom_progress_layout, null);
	    TextView progress_msg = (TextView) v.findViewById(R.id.progress_msg);
	    progress_msg.setText(msg);

		dialog.getWindow().setGravity(Gravity.CENTER);
		dialog.setCancelable(false);
		dialog.setMessage(msg);
		dialog.setIndeterminate(true);
		dialog.show();  
		dialog.setContentView(v);
		
		if (interval > 0) {
			new Handler().postDelayed(new Runnable() {				
				@Override
				public void run() {
					stopProgress();
					if (onComplete != null) {
						onComplete.run();
					}
				}
			}, interval);
		}
	}
	

	public void stopProgress() {
		if (dialog != null && dialog.isShowing()) {
			try { dialog.dismiss(); } catch (Exception e) {}
		}
		dialog = null;
	}

}



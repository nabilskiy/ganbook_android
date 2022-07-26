package com.ganbook.share;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.LabeledIntent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.StrictMode;
import android.text.Html;
import android.util.Log;

import com.ganbook.app.MyApp;
import com.ganbook.utils.StrUtils;
import com.project.ganim.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ShareManager {
	private ShareManager() {}

	public static void openShareMenu(Activity caller, int subjectResId, String body) {
		openShareMenu(caller, subjectResId, -1, body, null, null);
	}
	
	public static void openShareMenu(Activity caller, int subjectResId, int bodyResId) {
		openShareMenu(caller, subjectResId, bodyResId, null, null, null);
	}
	
	public static void openShareMenu(Activity caller, String theSubject, 
			int bodyResId, String theBody, String emailAddress, File imgFile) {
		final Context context = MyApp.context;
		Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND); 
		sharingIntent.setType("text/html");
		
		String subject = "";
		String body = "";
		if (StrUtils.notEmpty(theSubject)) {
			subject = theSubject;
		}
		if (bodyResId != -1) {
			body = caller.getResources().getText(bodyResId).toString();
		}
		if (StrUtils.notEmpty(theBody)) {
			body = theBody;
		}
		String via = caller.getResources().getText(R.string.share_via).toString();

		if (emailAddress != null) {
			sharingIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{emailAddress}); 
		}
		sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
		sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, Html.fromHtml(body));
		if (imgFile != null) {
			Uri imgUri = Uri.fromFile(imgFile);
			sharingIntent.putExtra(android.content.Intent.EXTRA_STREAM, imgUri);
			sharingIntent.setType("message/rfc822");
		}
		StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
		StrictMode.setVmPolicy(builder.build());
		sharingIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
		caller.startActivity(Intent.createChooser(sharingIntent, via));
	}
	
	public static void onShareClick(Activity caller, String theSubject, 
			int bodyResId, String theBody, String emailAddress, File imgFile) {

		String subject = "";
		String body = "";
		if (StrUtils.notEmpty(theSubject)) {
			subject = theSubject;
		}
		if (bodyResId != -1) {
			body = caller.getResources().getText(bodyResId).toString();
		}
		if (StrUtils.notEmpty(theBody)) {
			body = theBody;
		}
		String via = caller.getResources().getText(R.string.share_via).toString();

		PackageManager pm = caller.getPackageManager();
		
	    Intent emailIntent = new Intent();
	    emailIntent.setAction(Intent.ACTION_SEND);
	    // Native email client doesn't currently support HTML, but it doesn't hurt to try in case they fix it
	    emailIntent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(body));
	    emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
	    if (imgFile != null && !imgFile.getAbsolutePath().contains("mp4")) {
			Uri imgUri = Uri.fromFile(imgFile);
			emailIntent.putExtra(android.content.Intent.EXTRA_STREAM, imgUri);
		}
	    emailIntent.setType("message/rfc822");
	    
	    
	    Intent waIntent = new Intent(Intent.ACTION_SEND);

        waIntent.setPackage("com.whatsapp");
        
        waIntent.putExtra(Intent.EXTRA_TEXT, body);
        if (imgFile != null) {
			Uri imgUri = Uri.fromFile(imgFile);
			if(imgUri.toString().contains("mp4"))
			{
				waIntent.setType("video/mp4");
			}
			else
			{
				waIntent.setType("image/jpeg");
			}

			waIntent.putExtra(android.content.Intent.EXTRA_STREAM, imgUri);
		}
		else {
//			waIntent.setType("text/html");
//			waIntent.putExtra(Intent.EXTRA_TEXT,Html.fromHtml(body));

		}
        
        waIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

	    
	    Intent sendIntent = new Intent(Intent.ACTION_SEND);     
	    sendIntent.setType("text/plain");

	    Intent openInChooser = Intent.createChooser(waIntent, via);

	    List<ResolveInfo> resInfo = pm.queryIntentActivities(sendIntent, 0);
	    List<LabeledIntent> intentList = new ArrayList<LabeledIntent>();        
	    for (int i = 0; i < resInfo.size(); i++) {
	        // Extract the label, append it, and repackage it in a LabeledIntent
	        ResolveInfo ri = resInfo.get(i);
	        String packageName = ri.activityInfo.packageName;
	        if(packageName.contains("android.email")) {
	            emailIntent.setPackage(packageName);
	        } else if(packageName.contains("twitter") || packageName.contains("android.gm")) {
	            Intent intent = new Intent();
	            intent.setComponent(new ComponentName(packageName, ri.activityInfo.name));
	            intent.setAction(Intent.ACTION_SEND);
	            intent.setType("text/plain");
	            if(packageName.contains("twitter")) {
	                intent.putExtra(Intent.EXTRA_TEXT, body);
	            }

	            else if(packageName.contains("android.gm")) { // If Gmail shows up twice, try removing this else-if clause and the reference to "android.gm" above
	                intent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(body));
	                intent.putExtra(Intent.EXTRA_SUBJECT, subject);               
	                intent.setType("message/rfc822");
	            }
	            
	            if (imgFile != null  && !imgFile.getAbsolutePath().contains("mp4")) {
	    			Uri imgUri = Uri.fromFile(imgFile);
	    			intent.putExtra(android.content.Intent.EXTRA_STREAM, imgUri);
	    		}

	            intentList.add(new LabeledIntent(intent, packageName, ri.loadLabel(pm), ri.icon));
	        }
	    }
	    
	    
	    // convert intentList to array
	    LabeledIntent[] extraIntents = intentList.toArray( new LabeledIntent[ intentList.size() ]);

	    openInChooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, extraIntents);
		StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
		StrictMode.setVmPolicy(builder.build());
	    caller.startActivity(openInChooser);
	}

	public static void openShareMenu(Activity caller, int subjectResId,
			int bodyResId, String theBody, String emailAddress, File imgFile) {
		PackageManager pm = MyApp.context.getPackageManager();
//		final Context context = MyApp.context;
		Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
		Intent whatsAppIntent = new Intent(android.content.Intent.ACTION_SEND);
		whatsAppIntent.setType("text/plain");
		sharingIntent.setType("text/plain");

		String subject = "";
		String body = "";
		if (subjectResId != -1) {
			subject = caller.getResources().getText(subjectResId).toString();
		}
		if (bodyResId != -1) {
			body = caller.getResources().getText(bodyResId).toString();
		}
		if (StrUtils.notEmpty(theBody)) {
			body = theBody;
		}
		String via = caller.getResources().getText(R.string.share_via).toString();
		if (emailAddress != null) {
			sharingIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{emailAddress}); 
		}
		sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
		sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, body);
		if (imgFile != null) {
			Uri imgUri = Uri.fromFile(imgFile);
			sharingIntent.putExtra(android.content.Intent.EXTRA_STREAM, imgUri);
			sharingIntent.setType("message/rfc822");
		}


		try {
			caller.startActivity(Intent.createChooser(sharingIntent, "Share via"));

		} catch (ActivityNotFoundException e) {
			Log.d("ACTIVITY NOT FOUND", e.getMessage());
		}




		//caller.startActivity(Intent.createChooser(sharingIntent, via));
	}


	public static void openEmailApps(Activity caller, int subjectResId, int bodyResId) {
//		final Context context = MyApp.context;
		Intent sharingIntent = new Intent(android.content.Intent.ACTION_SENDTO); 
		sharingIntent.setType("text/plain");
		
		String subject = "";
		String body = "";
		if (subjectResId != -1) {
			subject = caller.getResources().getText(subjectResId).toString();
		}
		if (bodyResId != -1) {
			body = caller.getResources().getText(bodyResId).toString();
		}
		String via = caller.getResources().getText(R.string.share_via).toString();
		
		sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
		sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, body);

		sharingIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
		caller.startActivity(Intent.createChooser(sharingIntent, via));
	}

	
}

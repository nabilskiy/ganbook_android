package com.ganbook.app;

import android.content.ContentResolver;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;

import androidx.multidex.MultiDexApplication;
import android.view.Gravity;
import android.widget.Toast;


import com.ganbook.communication.json.getalbum_response;
import com.ganbook.dagger.components.DaggerGanbookApiComponent;
import com.ganbook.dagger.components.DaggerNetComponent;
import com.ganbook.dagger.components.GanbookApiComponent;
import com.ganbook.dagger.components.NetComponent;
import com.ganbook.dagger.modules.AppModule;
import com.ganbook.dagger.modules.GanbookModule;
import com.ganbook.dagger.modules.NetModule;
import com.ganbook.datamodel.SingleImageObject;
import com.ganbook.interfaces.GanbookApiInterface;
import com.ganbook.interfaces.IGanbookApiCommercial;
import com.ganbook.models.GetParentAnswer;
import com.ganbook.sharedprefs.SPReader;
import com.ganbook.sharedprefs.SPWriter;
import com.ganbook.universalloader.UILManager;
import com.ganbook.user.User;
import com.ganbook.utils.DBUtils.HelperFactory;
import com.ganbook.utils.StrUtils;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.Gson;
import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParseQuery;

import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

public class MyApp extends MultiDexApplication {
	public static Context context;	
	private static Handler uiHandler;
	
	@SuppressWarnings("unused")
	private static final String PKGNAME_APPPARENT = "com.apparent";
	private static final String PKGNAME_GANIM = "com.project.ganim";

	public static final String PARSE_SERVICES = "PARSE_SERVICES";
	public static final String PROPERTY_OBJECT_ID = "PROPERTY_OBJECT_ID";

	public static String APP_NAME;

	public static GetParentAnswer selContact;
	public static getalbum_response selAlbum;
	public static SingleImageObject singleImageObject;

	public static volatile boolean loggingOut = false;

	public static volatile boolean sessionWasCreated;

	public static String gan_name;
	public static String[] class_names;
	public static String[] class_ids;

	public static FirebaseAnalytics firebaseAnalytics;

	private Timer mActivityTransitionTimer;
	private TimerTask mActivityTransitionTimerTask;
	public boolean wasInBackground;
	private final long MAX_ACTIVITY_TRANSITION_TIME_MS = 2000;

	NetComponent netComponent;
	GanbookApiComponent ganbookApiComponent;

	public GanbookApiComponent getGanbookApiComponent() {
		return ganbookApiComponent;
	}

	@Override
	public void onCreate() { 
		super.onCreate();

		loggingOut = false;
		context = getApplicationContext();
		uiHandler = new Handler();

		HelperFactory.setHelper(getApplicationContext());
		Parse.initialize(new Parse.Configuration.Builder(getApplicationContext())
				.applicationId("YVyJJy3WNvIHKl579qQMFm6dqNDZeRtyYLOxBHsa")
				.clientKey("rpiIDBC4JzX412Ai1ADy1FmNefaUOfIgIk5MnpQy")
				.server("https://parseapi.back4app.com")
				.build()
		);



		ParseQuery.clearAllCachedResults();

		final ParseInstallation parseInstallation = ParseInstallation.getCurrentInstallation();
		parseInstallation.put("GCMSenderId", JSONObject.NULL);
		parseInstallation.put("active","1");
		parseInstallation.saveInBackground(e -> {
			if (e == null) {
				String objectId = parseInstallation.getObjectId();
				new SPWriter(PARSE_SERVICES).putString(PROPERTY_OBJECT_ID, objectId).commit();
			} else {
				// The save failed.

			}
		});



		String this_pkgName = context.getPackageName();
		if (PKGNAME_GANIM.equals(this_pkgName)) {
			APP_NAME = "1";
		} else {
			APP_NAME = "2";
		}
		
		if(selAlbum == null)
		{
			blocking_loadFromLocalCache();
		}
		
		if(singleImageObject == null)
		{
			blocking_loadImageFromLocalCache();
		}
		
		if(selContact == null)
		{
			blocking_loadParentFromLocalCache();
		}
				
		UILManager.initImageLoader();
		
		firebaseAnalytics = FirebaseAnalytics.getInstance(this);

//		analytics = GoogleAnalytics.getInstance(this);
//	    analytics.setLocalDispatchPeriod(1800);
//
//	    tracker = analytics.newTracker("UA-32881563-34"); // Replace with actual tracker/property Id
//	    tracker.enableExceptionReporting(true);
//	    tracker.enableAdvertisingIdCollection(true);
//	    tracker.enableAutoActivityTracking(true);

		netComponent = DaggerNetComponent.builder()
				.appModule(new AppModule(this))
				.netModule(new NetModule(GanbookApiInterface.BASE_URL, IGanbookApiCommercial.COMMERCIAL_BASE_URL))
				.build();

		ganbookApiComponent = DaggerGanbookApiComponent.builder()
				.netComponent(netComponent)
				.ganbookModule(new GanbookModule())
				.build();
	}

	public NetComponent getNetComponent() {
		return netComponent;
	}

	@Override
	public void onTerminate() {
		HelperFactory.releaseHelper();
		super.onTerminate();
	}
	

	public static void runOnUIThread(Runnable runnable) {
		if (runnable != null) {
			uiHandler.post(runnable);
		}
	}

	public static ContentResolver contentResolver() {
		return context.getContentResolver();
	}

	public static void runOnUIThreadDelayed(Runnable runnable, long delayMillis) {
		if (runnable != null) {
			uiHandler.postDelayed(runnable, delayMillis);
		}
	}

	public static void sendAnalytics(String id, String name, String contentType, String selectContent) {
		Bundle bundle = new Bundle();
		bundle.putString(FirebaseAnalytics.Param.ITEM_ID, id);
		bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, name);
		bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, contentType);
		firebaseAnalytics.logEvent(selectContent, bundle);
	}

	public static void toast(int msgResId, String param) {
		String msg = context.getResources().getString(msgResId,param); 
		toast(msg, Toast.LENGTH_LONG);
	}
	
	public static void toast(int msgResId) {
		String msg = context.getResources().getString(msgResId); 
		toast(msg, Toast.LENGTH_LONG);
	}
	
	public static void toast(final String msg, final int duration) {
		uiHandler.post(() -> {
			Toast toast = Toast.makeText(context, msg, duration);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
		});
	}
	
	public static void async_writeUserToLocaCache() {
  		new AsyncTask<Void, Void, Void>() {
			@Override 
			protected Void doInBackground(Void... params) {
				String json = new Gson().toJson(selAlbum);
				if (StrUtils.notEmpty(json)) {
					new SPWriter("SINGLE_ALBUM").putString("THIS_ALBUM_COLUMN", json).commit();
				}
				return null; 
			}
		}.execute();

	}
	
	public static User blocking_loadFromLocalCache() {
		try {  
			String json = new SPReader("SINGLE_ALBUM").getString("THIS_ALBUM_COLUMN", null);
			if (StrUtils.notEmpty(json)) { 
				int jj=234;
				jj++;
				selAlbum = new Gson().fromJson(json, getalbum_response.class);
			}
		}
		catch (Exception e) {
			int jj=234;
			jj++;
		}
		return null;
				
	}
	
	public static void async_writeImageToLocaCache() {
  		new AsyncTask<Void, Void, Void>() {
			@Override 
			protected Void doInBackground(Void... params) {
				String json = new Gson().toJson(singleImageObject);
				if (StrUtils.notEmpty(json)) {
					new SPWriter("SINGLE_IMAGE").putString("THIS_IMAGE_COLUMN", json).commit();
				}
				return null; 
			}
		}.execute();

	}
	
	public static SingleImageObject blocking_loadImageFromLocalCache() {
		try {  
			String json = new SPReader("SINGLE_IMAGE").getString("THIS_IMAGE_COLUMN", null);
			if (StrUtils.notEmpty(json)) { 
				int jj=234;
				jj++;
				singleImageObject = new Gson().fromJson(json, SingleImageObject.class);
			}
		}
		catch (Exception e) {
			int jj=234;
			jj++;
		}
		return null;
				
	}
	
	public static void async_writeParentToLocaCache() {
  		new AsyncTask<Void, Void, Void>() {
			@Override 
			protected Void doInBackground(Void... params) {
				String json = new Gson().toJson(selContact);
				if (StrUtils.notEmpty(json)) {
					new SPWriter("SINGLE_PARENT").putString("THIS_PARENT_COLUMN", json).commit();
				}
				return null; 
			}
		}.execute();

	}
	
	public static GetParentAnswer blocking_loadParentFromLocalCache() {
		try {  
			String json = new SPReader("SINGLE_PARENT").getString("THIS_PARENT_COLUMN", null);
			if (StrUtils.notEmpty(json)) { 
				int jj=234;
				jj++;
				selContact = new Gson().fromJson(json, GetParentAnswer.class);
			}
		}
		catch (Exception e) {
			int jj=234;
			jj++;
		}
		return null;
				
	}


	public void startActivityTransitionTimer() {
		this.mActivityTransitionTimer = new Timer();
		this.mActivityTransitionTimerTask = new TimerTask() {
			public void run() {
				MyApp.this.wasInBackground = true;
			}
		};

		this.mActivityTransitionTimer.schedule(mActivityTransitionTimerTask,
				MAX_ACTIVITY_TRANSITION_TIME_MS);
	}

	public void stopActivityTransitionTimer() {
		if (this.mActivityTransitionTimerTask != null) {
			this.mActivityTransitionTimerTask.cancel();
		}

		if (this.mActivityTransitionTimer != null) {
			this.mActivityTransitionTimer.cancel();
		}

		this.wasInBackground = false;
	}
	


	
}


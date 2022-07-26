package com.ganbook.debug;

import java.util.Arrays;

import android.provider.Settings.Secure;

import com.ganbook.app.MyApp;
import com.ganbook.sharedprefs.SPReader;
import com.ganbook.sharedprefs.SPWriter;

public class IsLab {
	private IsLab() {}
	
	private static int isLab = -1;
	
	private static String myDeviceId;
	
		
	private static String[] labDevices = {
		  "f290342ac11fb47b", 
		  "b3abef30808e1149",
		  "4bbf76d43fcedeb6", 
		  "jsf56sddfsse1sfg",
		  "e3be535a08c710fc",  
		  "re4ghhdsdf45gf4c",   
		  "8e62b7cc1c92d850",  
		  "ec3af3cb072f0911",  // noa2 
		  "929c9035b1b8699c", // noa
		  "c9e3fecde6f086b0", 
	};   
	  
	public static boolean Device() {  
		if (isLab < 0) {   
			String thisId = Secure.getString(MyApp.context.getContentResolver(), Secure.ANDROID_ID);
			isLab = Arrays.asList(labDevices).contains(thisId) ? 1 : 0; 
		}
		return isLab > 0; 
	}


	public static String getDeviceId() {
		if (myDeviceId==null) {
			myDeviceId = Secure.getString(MyApp.context.getContentResolver(), Secure.ANDROID_ID);
		}
		return myDeviceId;
	}


	private static final String __chars = "abcdefghijklmnopqrstuvwxyz";
	
	public static String getTaskName() {
		if (!IsLab.Device()) {
			return "Uploader";
		}
		int prior = new SPReader("uploaderFile").getInt("lastUploadTaskName", -1);
		int curInd = prior + 1 ;
		if (curInd >= __chars.length()) {
			curInd = 0;
		}
		char res = __chars.charAt(curInd);
		new SPWriter("uploaderFile").putInt("lastUploadTaskName", curInd).commit();		
		return "" + res;
	}

}

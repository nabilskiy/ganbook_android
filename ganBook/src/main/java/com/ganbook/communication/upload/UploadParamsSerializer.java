package com.ganbook.communication.upload;

import java.lang.reflect.Type;
import java.util.ArrayList;

import android.os.AsyncTask;

import com.ganbook.app.MyApp;
import com.ganbook.sharedprefs.SPReader;
import com.ganbook.sharedprefs.SPWriter;
import com.ganbook.user.User;
import com.ganbook.utils.StrUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


public class UploadParamsSerializer {
	private static final String UPLOAD_PARAMS = "UploadParams";
	private static final String RAW_JSON = "upload_str";

	private UploadParamsSerializer() {} 
	 
	private static volatile ArrayList<UploadParams> allUploads;

	public static synchronized UploadParams fetchNextUpload() {

		readFromDiskIfNeeded();
		UploadService.debugUpload(null, allUploads.size() + " tasks to DOOOO");
		if (allUploads==null || allUploads.isEmpty()) {
			return null; 
		}
		boolean found = false;
		int ind = 0;
		for (int i = 0; i < allUploads.size(); i++) {
			if(MyApp.selAlbum.album_id.equals(allUploads.get(i).album_id))
			{
				ind = i;
				found = true;
				break;
			}
		} 
			
		UploadParams cur = null;
		
		if(found)
		{
			cur = allUploads.remove(ind);
		}
//		UploadParams cur = allUploads.remove(0); 
		int __num = cur==null ? 0 : cur.numFiles();
		writeToDisk();   
		return cur;		
	}
	
	public static synchronized UploadParams fetchNextUpload(String album_id) {
		readFromDiskIfNeeded(); 
		if (allUploads==null || allUploads.isEmpty()) {
			return null; 
		}
		int ind = 0;
		for (int i = 0; i < allUploads.size(); i++) {
			if(album_id.equals(allUploads.get(i).album_id))
			{
				ind = i;
				break;
			}
		} 
			
		UploadParams cur = allUploads.remove(ind); 
		int __num = cur==null ? 0 : cur.numFiles();
		writeToDisk();   
		return cur;		
	}

	public static synchronized void returnProcessed(UploadParams changedUpload) {
		int __num = changedUpload==null ? 0 : changedUpload.numFiles();
		readFromDiskIfNeeded();
		if (allUploads==null) { 
			allUploads = new ArrayList<UploadParams>(); 
		}
		
		if (changedUpload != null && changedUpload.hasMoreFiles()) {
			allUploads.add(changedUpload);
		}
		writeToDisk();
	}
	

	private static void writeToDisk() {
  		new AsyncTask<Void, Void, Void>() {
			@Override 
			protected Void doInBackground(Void... params) {
				synchronized (UploadParamsSerializer.class) {
					String jsonStr = (allUploads == null ? "" : new Gson().toJson(allUploads));
					new SPWriter(UPLOAD_PARAMS).putString(RAW_JSON, jsonStr).commit();
				}
				return null; 
			}
		}.execute();
	}


	private static void readFromDiskIfNeeded() {
		synchronized (UploadParamsSerializer.class) {
			if (allUploads != null) {
				return;
			}
			String jsonStr = new SPReader(UPLOAD_PARAMS).getString(RAW_JSON, null);
			if (StrUtils.isEmpty(jsonStr)) {
				return;
			}
			Type t_token = new TypeToken<ArrayList<UploadParams>>(){}.getType();
			allUploads = new Gson().fromJson(jsonStr, t_token);
			int jj=234;
			jj++;
		}
		
	}
	
}

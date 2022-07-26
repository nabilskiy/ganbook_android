package com.ganbook.communication.upload;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import androidx.core.app.NotificationCompat;
import android.util.Log;

import com.ganbook.activities.MainActivity;
import com.ganbook.app.MyApp;
import com.ganbook.communication.ICompletionHandler;
import com.ganbook.communication.datamodel.ResultObj;
import com.ganbook.communication.json.transmitter.JsonTransmitter;
import com.ganbook.communication.upload.state.UploadStateRepository;
import com.ganbook.debug.IsLab;
import com.ganbook.notifications.NotificationManagerMy;
import com.ganbook.user.User;
import com.ganbook.utils.DirectoryUtils;
import com.ganbook.utils.ImageUtils;
import com.ganbook.utils.NetworkUtils;
import com.project.ganim.R;

import org.apache.http.HttpStatus;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UploadService extends Service {
	
	///ggggg cleanup file

//	gggggggggggggggggFix: File upload logic
//		 1. select from gallery/camera
//		 2. show preview to user
//		 3. allow user to scroll and delete if not needed
//		 4. click upload
//		 5. for each
//		        createpicture -- for each file in the fileUpload procedure
		

	
	private static int UPLOAD_SERVICE_ID = 138;
	
	private static final int THUMBNAIL_SIZE_PIX = 144;
	
	private static final int BUFFER_SIZE = 4096;
	private static final String NEW_LINE = "\r\n";
	private static final String TWO_HYPHENS = "--";

//	private static final String GANIM = "/GANIM/";
    private static final String APPLICATION = "application/";
    private static final String APPLICATION_OCTET_STREAM = APPLICATION + "octet-stream";
    private static final String TMB = JsonTransmitter.TMB;
 
	private static final int CONNECTION_TIMEOUT_MILLI = 1000*100;

	private static final int UPLOAD_COMPLETED_NOTIF = 23424;

	private static volatile UploadParams g_params;
	
	private static volatile ArrayList<UploadParams> errorUploads;

	
	// do the work serially; safer 
	private final static ExecutorService exec = Executors.newSingleThreadExecutor();

	
// ggggFix upload using image name only
//	public static void startUpload(ArrayList<String> imageNameList, String album_id, String class_id) {
//		final String albumDir = DirectoryUtils.getAlbumDirPath();		
//		FileToUpload img, thumb;
//		ArrayList<FileToUpload> filesToUpload = new ArrayList<FileToUpload>(); 
//		for (String imageName: imageNameList) {
//			String imageFilePath = albumDir + "/" + imageName;
//			img = new FileToUpload(imageFilePath, "file", imageName, APPLICATION_OCTET_STREAM);
//			filesToUpload.add(img);
//			String thumbName = TMB + imageName;
//			String thumbFilePath = albumDir + "/" + thumbName;
//			thumb = new FileToUpload(thumbFilePath, "tmb_file", thumbName, APPLICATION_OCTET_STREAM);
//			filesToUpload.add(thumb);
//		}
//		
//		params = new UploadParams(filesToUpload, album_id, class_id);
//		Context c = MyApp.context;
//		c.startService(new Intent(c, UploadService.class));		
//	}
	
	

	public static void startUpload(final ArrayList<String> imagePathList) {
		//FtpManager.debug_ctr = 0;
//		exec.execute(new Runnable() {			
//			@Override 
//			public void run() {
				// prepare for service start thread
				inner_performUpload(imagePathList); 				
//			}
//		});
	}
		

	private static void inner_performUpload(final ArrayList<String> imagePathList) {
		final String album_id = MyApp.selAlbum.album_id;
		final String gan_id = User.current.getCurrentGanId();
		final String class_id = User.current.getCurrentClassId();
		
		final String albumDirPath = DirectoryUtils.getAlbumDirPath();
		final File albumDir = new File(albumDirPath);
		
		albumDir.mkdirs(); //!
		
		final String[] thumbPathArr = new String[imagePathList.size()];

		boolean is_video = false;

		FileToUpload img, thumb;
		ArrayList<FileToUpload> filesToUpload = new ArrayList<FileToUpload>();
		ArrayList<CopyFileRecord> filesToCopy = new ArrayList<CopyFileRecord>(); 
		for (int ind = 0; ind < imagePathList.size(); ind++) {
			String orig_file_path = imagePathList.get(ind);
			final String f_name = new File(orig_file_path).getName();
			final File cur_dir = new File(orig_file_path).getParentFile();
			if (!albumDir.equals(cur_dir)) {
				// copy fo album folder
//				f_path = copyFileTo(f_path, f_name, albumDir);
				filesToCopy.add(new CopyFileRecord(orig_file_path, f_name, albumDir));
//				if (!new File(f_path).exists()) {
//					int jj=324;
//					jj++;
//				}
			}
			final String thumbName = TMB + f_name;
			String thumbFilePath = albumDir + "/" + thumbName;

			if(orig_file_path.contains("mp4"))
			{
//				try{
				thumbFilePath = thumbFilePath.replaceAll("mp4","jpeg");
				is_video = true;
//					FileOutputStream dst = new FileOutputStream(thumbFilePath);
//					Bitmap bmThumbnail = ThumbnailUtils.createVideoThumbnail(orig_file_path, MediaStore.Video.Thumbnails.MINI_KIND);
//					bmThumbnail.compress(Bitmap.CompressFormat.JPEG, 30, dst);
//					dst.close();
//
//					_thumbFile = new File(thumbFilePath);
//				}
//				catch (Throwable e)
//				{
//					//Log.e(e.getMessage());
//				}
			}
//			else {
//
//			}
			File _thumbFile = new File(thumbFilePath);
			_thumbFile = ImageUtils.create_thumb(orig_file_path, thumbFilePath, THUMBNAIL_SIZE_PIX, true);

			if (_thumbFile == null) { 
				int jj=324;
				jj++; 
			}
			
//			String imageFilePath = albumDir + "/" + imageName;
//			img = new FileToUpload(imageFilePath, "file", imageName, APPLICATION_OCTET_STREAM);
			String tmb_file_path = validFile(_thumbFile) ? _thumbFile.getAbsolutePath() : null;
			thumbPathArr[ind] = tmb_file_path;
			img = new FileToUpload(orig_file_path, "file", f_name, APPLICATION_OCTET_STREAM, orig_file_path);
			filesToUpload.add(img);
			thumb = new FileToUpload(thumbFilePath, "tmb_file", thumbName, APPLICATION_OCTET_STREAM, orig_file_path); 
			filesToUpload.add(thumb); 
		}		

		g_params = new UploadParams(filesToUpload, filesToCopy, album_id, gan_id, class_id, is_video);
		UploadParamsSerializer.returnProcessed(g_params);
		Context c = MyApp.context;
		String uploadTaskId = g_params.uploadTaskId;
		sendCreateTask(g_params);
		UploadStateRepository.onStartUpload(album_id, uploadTaskId, imagePathList, thumbPathArr); 
		MainActivity.refreshSingleAlbumView(album_id); //!
		
		c.startService(new Intent(c, UploadService.class)); 		
	}
	

	private static boolean validFile(File f) {
		return f != null && f.exists();
	}


	static class CopyFileRecord {
		final String f_path;
		final String f_name;
		final String albumDirPath;
		
		CopyFileRecord(String f_path, String f_name, File albumDir) {
			this.f_path = f_path;
			this.f_name = f_name;
			this.albumDirPath = albumDir.getAbsolutePath();
			
		}
	};



//	private static void createThumbIfNeeded(final String largeImgPath, String thumbFilePath) {
//		final File largeImg = new File(largeImgPath);
//		if (!largeImg.exists()) {
//			int jj=234;
//			jj++;
//			return;
//		}
//		if (!largeImg.isFile()) {
//			int jj=234;
//			jj++;
//			return;
//		}
//
//		final File thumb = new File(thumbFilePath);
//		if (thumb.exists() && thumb.isFile() && thumb.length() > 20) {
//			int jj=234;
//			jj++;
//			return;		
//		}
//
//		// actually create the thumb:
//
//		try  {
//			//            FileInputStream fis = new FileInputStream(lagreImg);
//			Options bitmapOptions = new Options();
//			bitmapOptions.inJustDecodeBounds = true; 
//			Bitmap imageBitmap = BitmapFactory.decodeFile(largeImgPath, bitmapOptions);
//
//			int sampleSize = getDesiredSampleSize(bitmapOptions);
//
//			bitmapOptions.inSampleSize = sampleSize; 
//			bitmapOptions.inJustDecodeBounds = false; 
//			Bitmap thumbnail = BitmapFactory.decodeFile(largeImgPath, bitmapOptions);
//
//
//			// and save thumb:
//			saveBitmapToFile(thumbnail, thumbFilePath);
//
//			thumbnail.recycle();            
//
//			//            int thumbSize = THUMBNAIL_SIZE_PIX;
//			//            imageBitmap = Bitmap.createScaledBitmap(imageBitmap, thumbSize, thumbSize, false);            
//			//            ByteArrayOutputStream baos = new ByteArrayOutputStream();  
//			//            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//			//            imageData = baos.toByteArray();
//		}
//		catch(Exception ex) {
//			int jj=234;
//			jj++;
//		}		
//	}


//	private static void saveBitmapToFile(Bitmap thumbnail, String thumbFilePath) throws Exception {
//        FileOutputStream fos = new FileOutputStream(thumbFilePath);
//        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, fos);
//        fos.flush();
//        fos.close();
//	}
//
//
//	private static int getDesiredSampleSize(Options bitmapOptions) {
//		int desiredWidth = THUMBNAIL_SIZE_PIX;
//		int desiredHeight = THUMBNAIL_SIZE_PIX;
//		float widthScale = (float)bitmapOptions.outWidth/desiredWidth;
//		float heightScale = (float)bitmapOptions.outHeight/desiredHeight;
//		float scale = Math.min(widthScale, heightScale);
//		int sampleSize = 1;
//		while (sampleSize < scale) {
//		    sampleSize *= 2;
//		}	
//		return sampleSize;
//	}

	
	public static void completePendingUploads(String album_id) {
//		UploadParams pendingTask = UploadParamsSerializer.fetchNextUpload(album_id);
//		if (pendingTask != null) {
//			MainActivity.hideCameraButton();
			debugUpload(null, "try again detected > has pending tasks");
//			g_params = pendingTask;
			Context c = MyApp.context;
			c.startService(new Intent(c, UploadService.class));					
//		}		
//		else {
//			debugUpload(null, "NOOO pending tasks, byebye");
//		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
//		if (g_params == null || !g_params.hasMoreFiles()) {
////			throw new RuntimeException("Params not set!");
//			return Service.START_STICKY;
//		}
		
		if (!NetworkUtils.isConnected()) {
//			UploadParamsSerializer.returnProcessed(g_params);
			return Service.START_STICKY;
		}
		
//		final UploadParams cur_params = g_params;
//		int __num = cur_params==null ? 0 : cur_params.numFiles();		
//		g_params = null; // prepare for next upload request 
		errorUploads = null;
		
		debugUpload(null, "Entering Service:onStartCommand");
		
		moveServiceToForeground(); 
		
//		new LowPriorityThread("Ganbook:UploadThread") {
//			public void run() {
//				perform_service_bg_action(cur_params);
//			}
//		}.start();
		 
		exec.execute(new Runnable() { 			
			@Override
			public void run() { 
				perform_service_bg_action();
			}
		});
		
		return Service.START_STICKY;
	}
	
	
		        	
	private void perform_service_bg_action() {
		// copy files to album dir
//		MainActivity.forced_refreshSingleAlbumView();

		UploadParams cur_params = UploadParamsSerializer.fetchNextUpload();
		
		try { 
			for (CopyFileRecord rec: cur_params.filesToCopy) {
				copyFileTo(rec.f_path, rec.f_name, rec.albumDirPath); 
			}
		}
		catch (Exception e) {
			UploadParamsSerializer.returnProcessed(cur_params);
			return;
		}
		
		int jj=24;
		jj++;
//		for (;;) {
			debugUpload(cur_params, "perform_service_bg_action - about to ftp connect... " );
//			boolean goOn = false;
			boolean goOn = true;

			while (goOn)
			{
				try {
					ftp_handleFileUpload(cur_params);
				}
				catch (Exception e) {
					int jjj=234;
					jjj++;
					debugUpload(cur_params, "goOn ftp_handleFileUpload except: " + e);
				}

				cur_params = UploadParamsSerializer.fetchNextUpload();
				if (cur_params != null) {
					debugUpload(cur_params, " >> has another task");
					// recurse to perform next upload task:
				}
				else {
					// if not - kill service
					try { UploadService.this.stopSelf(); } catch (Exception e) {}

//					MainActivity.showCameraButton();
					if(errorUploads != null)
					{
						String uploadCompletedMsg = getResources().getString(R.string.upload_completed_with_errors);
						NotificationManagerMy.write(UPLOAD_COMPLETED_NOTIF, uploadCompletedMsg, null, null, null);
					}
					else
					{
						String uploadCompletedMsg = getResources().getString(R.string.upload_completed);
						NotificationManagerMy.write(UPLOAD_COMPLETED_NOTIF, uploadCompletedMsg, null, null, null);
					}
					debugUpload(cur_params, " >> no more tasks");
					goOn =  false; // completed
				}
			}
			
			if(errorUploads != null)
			{
				UploadStateRepository.markAllPendingAsFailed();
				for (UploadParams errorUpload : errorUploads) {
					UploadParamsSerializer.returnProcessed(errorUpload);
				}
			}
			
//			if (!goOn) {
//				UploadParamsSerializer.returnProcessed(cur_params); //!
//				debugUpload(cur_params, "ftp_handleFileUpload failed - breaking from loop"); 
//				return;
//			}
//			try { Thread.sleep(1000*30);} catch (Exception e) {}
			// and retry
//		} 
		 
	}


	private void moveServiceToForeground() {
//		final long now = System.currentTimeMillis();
		Intent notificationIntent = new Intent(this, MainActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

		Notification notification = new NotificationCompat.Builder(this)
				.setSmallIcon(R.drawable.ic_launcher)
//				.setLargeIcon(getB)
				.setContentText(getText(R.string.uploading_now_notif))
				.setContentIntent(pendingIntent).build();
//		Notification(R.drawable.ic_launcher,
//				getText(R.string.uploading_now_notif), now);

//		notification.setLatestEventInfo(this, getText(R.string.app_name),
//		        getText(R.string.uploading_now_notif), pendingIntent);
		startForeground(UPLOAD_SERVICE_ID, notification);
	
	}


	private void terminateService() {
		stopForeground(true);
		this.stopSelf();
	}
	 
	private String fullUrl;		
	private String apiKey;
	private String userId;
	
	private String boundary;
	private byte[] boundaryBytes;
	private int _total; // num images, not including thumbs!
 
	 
	private void ftp_handleFileUpload(final UploadParams cur_params) {
		// perform actual upload
		//FtpManager.uploadFiles(cur_params);//, new ICompletionHandler()
//		{
//			@Override
//			public void onComplete(ResultObj result) {
//				boolean task_completed = cur_params.filesToUpload.isEmpty();
//
//				sendCompletionPushNotif(cur_params);
//
//				if (!task_completed) {
//					addTaskToErrorList(cur_params);
//				}
//			}
//		});
		
		boolean task_completed = cur_params.filesToUpload.isEmpty();
		
//		sendUpdateTask(cur_params.uploadTaskId,String.valueOf(cur_params.num_uploaded/2),"0");
		
		sendCompletionPushNotif(cur_params);
//
		if (task_completed) {
			
			
//			return false;
			// if more tasks are pending - perform them
//			UploadParams nextUploadTask = UploadParamsSerializer.fetchNextUpload();
//			if (nextUploadTask != null) {
//				debugUpload(nextUploadTask, " >> has another task");
//				// recurse to perform next upload task:
//				return ftp_handleFileUpload(nextUploadTask);
//			}   
//			else {
//				// if not - kill service
//				try { UploadService.this.stopSelf(); } catch (Exception e) {}
//				String uploadCompletedMsg = getResources().getString(R.string.upload_completed);
//				MainActivity.showCameraButton();
//				NotificationManagerMy.write(UPLOAD_COMPLETED_NOTIF, uploadCompletedMsg);
//				debugUpload(nextUploadTask, " >> no more tasks");
//				return false; // completed
//			}
		}
		else {
			// serialize request and use alarm for later notifs;
//			UploadParamsSerializer.returnProcessed(cur_params);
////			return true; // try later:
			addTaskToErrorList(cur_params);
		}
		 
	}
	
	public static synchronized void addTaskToErrorList(UploadParams errorUpload) {
		int __num = errorUpload==null ? 0 : errorUpload.numFiles();
		if (errorUploads==null) { 
			errorUploads = new ArrayList<UploadParams>(); 
		}
		
		if (errorUpload != null && errorUpload.hasMoreFiles()) {
			errorUploads.add(errorUpload);
		}
	}
 
	  
	private static synchronized void sendCompletionPushNotif(UploadParams cur_params) {
//		if (cur_params.completionPushWasSent) {
//			return;
//		}
		debugUpload(cur_params, "TASK HAS COMPLETED!!");
		try { 
			boolean is_video = cur_params.is_video;
			String album_id = cur_params.album_id;
			String class_id = cur_params.class_id;
			int total_num = (cur_params.num_uploaded) / 2; // without thumbs!
			cur_params.num_uploaded = 0;
			if(total_num > 0)
			{
				handlePush(cur_params.uploadTaskId, is_video, total_num, album_id, class_id);
			}
//			cur_params.completionPushWasSent = true;
		}
		catch (Exception e) { 
			int jj=234;
			jj++;
		} 
	}
	
	private static synchronized void sendCreateTask(UploadParams cur_params) {
		try { 
				handleCraeteTask(cur_params.uploadTaskId, cur_params.album_id, String.valueOf(cur_params.total_num/2));
		}
		catch (Exception e) { 
			int jj=234;
			jj++;
		} 
	}
	
	private static synchronized void sendUpdateTask(String upload_task_id,String num_uploaded,String push_sent) {
		try { 
				handleUpdateTask(upload_task_id, num_uploaded, push_sent);
		}
		catch (Exception e) { 
			int jj=234;
			jj++;
		} 
	}



	public static void debugUpload(UploadParams cur_params, String msg) {
		if (IsLab.Device()) {
			String pref = cur_params==null ? "" : cur_params.getDebugPref();
			Log.i("concon",  pref + " " + msg); 
//			MyApp.toast(msg, Toast.LENGTH_LONG);  
		}
	}


//	private void inner_handleFileUpload() {
//		
////		//gggggggggggggggggggggFixxx remove below!!!
////		String dirPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
////		String f_path = dirPath + "/" +  "xxx.jpeg"; 
////		String t_path = dirPath + "/" + "xxx_ico.jpeg";
////		params.filesToUpload = new ArrayList<FileToUpload>();
////		params.filesToUpload.add(new FileToUpload(f_path, "file", "xxx", APPLICATION_OCTET_STREAM));
////		params.filesToUpload.add(new FileToUpload(t_path, "tmb_file", "xxx", APPLICATION_OCTET_STREAM));
//				
//		boundary = getBoundary();  
//		try {
//			boundaryBytes = getBoundaryBytes(boundary);
//		} catch (UnsupportedEncodingException e1) {
//			// log error
//			//gggggggggggggggFix ssss notif error;
//			e1.printStackTrace();
//			return;
//		}
//
//
////		int ind = 0;
//		_total = params.filesToUpload.size() / 2;
//		
//		final int size_with_thumbs = params.filesToUpload.size();
//		
//		boolean error = false;
//				
//		fullUrl = JsonTransmitter.ApiToUrl(JsonTransmitter.ApiTypes.createpicture);		
//		apiKey = User.getApiKey();
//		userId = User.getId();
//		
//		int num_success = 0;
//		int num_fails = 0;
//		
//		
//		for (int ind = 0; ind < (size_with_thumbs-1); /**/) {
//			boolean ok = handleSingleFileUpload(ind);
//			if (ok) {
//				num_success++;
//			}  else {
//				num_fails++;
//			}
////			gggggggggggggggFix upadte notification;
//			ind += 2;
//		}
//
//		try {
//			int curInd = 0;
//			handlePush(num_success, null, null, "POST", null, new ArrayList<NameValue>(),
//					null, curInd, _total, params.album_id, params.class_id);
//
//		} catch (Exception e) {
//			int jj=24;
//			jj++;
//		}
//		//gggggggggggggggFix ssssssss set some sort of push to server with num success
//		int jj=234;
//		jj++;
//	}  

	private static String copyFileTo(String f_path, String file_name, String albumDirPath) {
		String sourceFile = f_path;
		String destFile = new File(new File(albumDirPath), file_name).getAbsolutePath();
		FileInputStream fis = null;
		FileOutputStream fos = null;
		try { 
			fis = new FileInputStream(sourceFile);
			fos = new FileOutputStream(destFile);
			byte[] buffer = new byte[6*1024];
			int noOfBytes = 0;
			while ((noOfBytes = fis.read(buffer)) != -1) {
				fos.write(buffer, 0, noOfBytes);
			}
		}
		catch (Exception ioe) {
			int jj=24;
			jj++;
		}
		finally {
			// close the streams using close method
			try {
				if (fis != null) {
					fis.close();
				}
				if (fos != null) {
					fos.close();
				}
			}
			catch (IOException ioe) {
				System.out.println("Error while closing stream: " + ioe);
			}
		}
		return destFile;
	}

	
//	private boolean handleSingleFileUpload(final int ind) {
//		final File img_file = params.filesToUpload.get(ind).getFile(); //   Image
//		final File thumb_file = params.filesToUpload.get(ind+1).getFile(); // Thumb
//		
//		boolean ok;
//		ok = handleSingleFileUpload_single(img_file, thumb_file);
////		if (!ok) {
////			return false;
////		}
////		ok = handleSingleFileUpload_single(img_file, false); 
//		return ok;		
//	} 
	
//	public static void main(String[] args) {
//		String ts = "" + System.currentTimeMillis();
//		String sig = Utils.sig_request(user.getApi_key(), CommonUtilities.HTTP_CREATE_PICTURE, true, ts, user.getId());
//		
//		String urlString = CommonUtilities.HTTP_CREATE_PICTURE;
//		MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
//		HttpClient client = new DefaultHttpClient();
//		client.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
//		int i = 1;
//		int size = imagesToAddToServer.size() / 2;
//
//		for(String imageName : imagesToAddToServer)
//		{
//			try
//			{
//				HttpPost post = new HttpPost(urlString);
//
//				File file = new File(albumDir + File.separator + imageName);
//				FileBody bin = new FileBody(file);
//				
//				File tmb_file = new File(albumDir + File.separator + "tmb" + imageName);
//				FileBody tmb_bin = new FileBody(tmb_file);
//				
//				reqEntity = new MultipartEntity();
//				reqEntity.addPart("file", bin);
//				reqEntity.addPart("tmb_file", tmb_bin);
//				reqEntity.addPart("album_id", new StringBody(album_id));
//				reqEntity.addPart("gan_id", new StringBody(gan_id));
//				reqEntity.addPart("class_id", new StringBody(class_id));
//				reqEntity.addPart("name", new StringBody(imageName));
//				reqEntity.addPart("current_pic", new StringBody(String.valueOf(i)));
//				reqEntity.addPart("total_pics", new StringBody(String.valueOf(size)));
//				reqEntity.addPart("user_id", new StringBody(user.getId()));
//				reqEntity.addPart("ts", new StringBody(ts));
//				reqEntity.addPart("sig", new StringBody(sig));
//				
//				post.setEntity(reqEntity);
//
//				HttpResponse response = client.execute(post);		
//	}
	 
	
//	private boolean handleSingleFileUpload_single(final File img_file, final File tmb_file) {		
//		 
//		//gggFix correct url:
//		final String urlString = JsonTransmitter.ApiToUrl(JsonTransmitter.ApiTypes.createpicture);
//		
//		final String imageName = img_file.getName();
//		 
//		try {		 	 
//			MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
//
//			String ts = "" + System.currentTimeMillis();
//			String sig = JsonTransmitter.sig_request(apiKey, fullUrl, true, ts, userId);
//			
//			String album_id = MyApp.selAlbum.album_id; 
//			String class_id = User.current.getCurrentClassId();
//			String gan_id = User.current.getCurrentGanId();
// 
//			HttpPost post = new HttpPost(urlString);
//			 
//			if (!img_file.exists() || !img_file.isFile()) {
//				int jj=24;
//				jj++; 
//			}
//			if (!tmb_file.exists() || !tmb_file.isFile()) {
//				int jj=24;
//				jj++;
//			}
//			
//			long img_file_len = img_file.length();
//			long tmb_file_len = tmb_file.length(); 
//			
//			FileBody img_bin = new FileBody(img_file);			  
//			FileBody tmb_bin = new FileBody(tmb_file);			
//			
//			reqEntity = new MultipartEntity();  
//			reqEntity.addPart("file", img_bin);   
//			reqEntity.addPart("tmb_file", tmb_bin);      
//			
//			reqEntity.addPart("album_id", new StringBody(album_id));
//			reqEntity.addPart("gan_id", new StringBody(gan_id));
//			reqEntity.addPart("class_id", new StringBody(class_id));
//			reqEntity.addPart("name", new StringBody(imageName)); //gggFix
//			//			reqEntity.addPart("current_pic", new StringBody(String.valueOf(i)));
//			//			reqEntity.addPart("total_pics", new StringBody(String.valueOf(size)));
//			//			reqEntity.addPart("user_id", new StringBody(userId));
//			//			reqEntity.addPart("ts", new StringBody(ts));
//			//			reqEntity.addPart("sig", new StringBody(sig));
// 
//			post.setEntity(reqEntity);
//
//			HttpClient client = new DefaultHttpClient();
//
//			HttpResponse response = client.execute(post);
//			HttpEntity resEntity = response.getEntity();
//			String response_str = EntityUtils.toString(resEntity);
////			if (CommConsts.OK.equals(response_str)) {
//			if (isSuccessResponse(response_str)) {
//				int jj=234;
//				jj++; 
//				return true;
////				if (!imageName.contains(TMB))
////				{
////					String message = mResources.getString(R.string.upload_pictures);
////					String messageFormat = String.format(message, i, size);  
////
////					publishProgress(String.valueOf(i));	
////					i++; 
////				}
//			} 
//			else
//			{ 
////				return CommonUtilities.ERROR;
//				int jj=234;
//				jj++;
//			}
//		}
//		catch (Exception ex) {
//			int jj=234;
//			jj++;
//		}
//		return false;
//	}
//

	
//	private static boolean isSuccessResponse(String response_str) {
//		// {"success":true,"upload_success":["15892073166361042073747.jpeg.jpeg"]}
//		try {
//			JSONObject res = new JSONObject(response_str);
//			Boolean result = res.getBoolean("success");
//			if (result != null && result.booleanValue()==true) {
//				return true;
//			}
//		} 
//		catch (Exception e) {

////			e.printStackTrace();
//			int jj=234;
//			jj++;
//		} 
//		return false;
//	}


//	private boolean ggggggggggggggFix_old_handleSingleFileUpload(int ind) {
//		HttpURLConnection conn = null;
//		OutputStream requestStream = null;
//		InputStream istream = null;
//		boolean success = false; 
//		try {				
//			conn = getMultipartHttpURLConnection(params.url, params.method, boundary);
//			setRequestHeaders(conn, params.requestHeaders);//ggggggggFix suspect
//			requestStream = conn.getOutputStream();				
//			String ts = "" + System.currentTimeMillis();
//			String sig = JsonTransmitter.sig_request(apiKey, fullUrl, true, ts, userId);
//			
//			String gan_id = User.current.getCurrentGanId();
//			String class_id = User.current.getCurrentClassId();
//			String album_id = MyApp.selAlbum.album_id;
//			 
//			FileToUpload _file = params.filesToUpload.get(ind);
//			String file_name = (_file == null ? "" : _file.getFileName());
//			ArrayList<NameValue> requestParameters = new ArrayList<NameValue>();
//			requestParameters.add(new NameValue("gan_id", gan_id));
//			requestParameters.add(new NameValue("class_id", class_id)); 
//			requestParameters.add(new NameValue("album_id", album_id));
//			requestParameters.add(new NameValue("total_pics", "1"));//String.valueOf(imagesToAddToServer.size()));
//			requestParameters.add(new NameValue("name", file_name)); 
////			requestParameters.add(new NameValue("sig", sig)); ggggFix to remove?
////			requestParameters.add(new NameValue("user_id", userId)); ggggFix to remove? 
////			requestParameters.add(new NameValue("ts", "" + ts)); ggggFix to remove?
//			
//			//ggggFix reduce image size to <= 1 MBytes  
//			
//			setRequestParameters(requestStream, requestParameters, boundaryBytes); //gggFix suspect
//			
//			ArrayList<FileToUpload> currFilesToUpload = new ArrayList<FileToUpload>();
//			
//			currFilesToUpload.add(params.filesToUpload.get(ind)); //   Image
//			currFilesToUpload.add(params.filesToUpload.get(ind+1)); // Thumb
//
//			uploadFiles(params.uploadId, requestStream, 
//					currFilesToUpload, boundaryBytes, ind, _total);
//			
//			success = true;
//			 
//			final byte[] trailer = getTrailerBytes(boundary);
//			int t_len = trailer.length;
//			requestStream.write(trailer, 0, t_len); 
//			final int serverResponseCode = conn.getResponseCode();
//			//            final String serverResponseMessage = conn.getResponseMessage();
//
//			//ggggFix remove below
//			istream = conn.getInputStream();
//			String ss = JsonTransmitter.try_reading(istream, null);
//			
//			int jj=234;
//			jj++;
//
//			
//			if (serverResponseCode < HttpStatus.SC_BAD_REQUEST) {
//				istream = conn.getInputStream();
//				//			Response response = new Gson().fromJson(serverResponseMessage, Response.class);
//				//			JSONObject outerObj = new JSONObject(serverResponseMessage); 
//				//			JSONArray jsonMainArr = new JSONArray(outerObj.getJSONArray("upload_success"));
//
//				if (istream != null) {
//					String serverResponseMessage = JsonTransmitter.try_reading(istream, "UploadFile");
//					StringArrContainer response = new Gson().fromJson(serverResponseMessage, StringArrContainer.class);
//					if (response != null) {
//						for (String uploadedImage : response.upload_success) {
//							ArrayList<String> imagesSuccToUploadedToServer = new ArrayList<String>();
//							imagesSuccToUploadedToServer.add(uploadedImage);
//							//				Utils.updateSuccUploadToServer(mPrefs, -- write success to SPrefs 
//							//					   imagesSuccToUploadedToServer, album_id);
//							//				updatedMap(mPrefs,album_id,class_id);	
//						}
//					}
//				} 
//			}
//			
//			jj=13;
//			jj++;
////			broadcastCompleted(uploadId, serverResponseCode, serverResponseMessage, curr, _total);
//			
////			if (ind == _total) {
////				if (serverResponseCode >= 200 && serverResponseCode <= 299) {
////					handlePush(num_success, null, null, "POST", null, new ArrayList<NameValue>(), 
////							null, curInd, _total, params.album_id, params.class_id);
////				}
////			}
//						
//		}				 
//		catch (Exception exception) {
//			;//ggggggggFix suspect
//			int jj=23;
//			jj++;
//			success = false;
////			error = true;				
////			broadcastError(uploadId, exception, curr, _total);				
////			try  {
////				handlePush(num_success, null, null, "POST", null, new ArrayList<NameValue>(), 
////						null, curInd, _total, params.album_id, params.class_id);
////			} 
////			catch (IOException e) {
////				e.printStackTrace();
////			}
//		}			
//		finally {
//			closeOutputStream(requestStream);
//			closeConnection(conn);
////			terminateService();  //gggFix??
//		}
//		return success;
//	}
//

	private static void handlePush(
			final String uploadId,
			boolean isVideo,
			final int num_success,
//			final String uploadId, 
//			final String url, 
//			final String method,
//			final ArrayList<FileToUpload> filesToUpload,
//			final ArrayList<NameValue> requestHeaders, 
//			final ArrayList<NameValue> requestParameters,
//			final int current,
//			final int total, 
			final String album_id, 
			final String class_id) throws IOException {
		int jj=234;
		jj++;
		JsonTransmitter.send_pushafterupload(album_id, class_id, num_success, uploadId, isVideo,
				new ICompletionHandler() {					
					@Override
					public void onComplete(ResultObj result) {
						if(result.succeeded)
						{
//							sendUpdateTask(uploadId,String.valueOf(num_success),"1");
						}
					}
				});
	}
	
	private static void handleCraeteTask(
			final String upload_task_id,
			final String album_id, 
			final String total_num) throws IOException {
		int jj=234;
		jj++;
		JsonTransmitter.send_createuploadtask(upload_task_id,album_id, total_num);
	}
	
	private static void handleUpdateTask(
			final String upload_task_id,
			final String uploaded_num, 
			final String push_sent) throws IOException {
		int jj=234;
		jj++;
		JsonTransmitter.send_updateuploadtask(upload_task_id,uploaded_num, push_sent);
	}


		
//	hhhh
	private static void orig___handlePush(
			boolean isVideo,
			final int num_success,
//			final String uploadId, 
//			final String url, 
//			final String method,
//			final ArrayList<FileToUpload> filesToUpload,
//			final ArrayList<NameValue> requestHeaders, 
//			final ArrayList<NameValue> requestParameters,
//			final int current,
//			final int total, 
			final String album_id, 
			final String class_id) throws IOException {

//		SharedPreferences sharedPreferences = getSharedPreferences(Utils.PREFS_NAME, 0);
		
//		Map<String, String> map = Utils.loadMap(sharedPreferences);
//		String class_id_num = map.get(album_id);
//		String[] arr = class_id_num.split("_");
		
		final String boundary = getBoundary();
		final byte[] boundaryBytes = getBoundaryBytes(boundary);

		HttpURLConnection conn = null;
		OutputStream requestStream = null;
		InputStream is = null;

		try {
//			HTTP_PUSH_AFTER_UPLOAD = XXXHOST + REQUEST_HOST + "pushafterupload.json";
			final String fullUrl = JsonTransmitter.ApiToUrl(JsonTransmitter.ApiTypes.pushafterupload);
			
//			conn = getMultipartHttpURLConnection(fullUrl, method, boundary);

//			setRequestHeaders(conn, requestHeaders);//ggggggggFix suspect

			requestStream = conn.getOutputStream();
			
			String ts = "" + System.currentTimeMillis();

			String sig = JsonTransmitter.sig_request(User.getApiKey(),
					fullUrl,
					true,  
					ts, 
					User.getId());
			
			ArrayList<NameValue> requestParameters1 = new ArrayList<NameValue>();
			requestParameters1.add(new NameValue("album_id", album_id));
			requestParameters1.add(new NameValue("class_id", class_id));
			requestParameters1.add(new NameValue("num_success", "" + num_success));
			requestParameters1.add(new NameValue("isVideo", isVideo ? "1" : "0"));
			
			requestParameters1.add(new NameValue("sig", sig));
			requestParameters1.add(new NameValue("user_id", User.getId()));
			requestParameters1.add(new NameValue("ts", "" + ts));
			
			try {
				setRequestParameters(requestStream, requestParameters1, boundaryBytes);
			} catch (Exception e) {
				// log error
				e.printStackTrace();
				return;
			}

			byte[] trailer;
			try {
				trailer = getTrailerBytes(boundary);
			} catch (Exception e) {
				// log error
				e.printStackTrace();
				return;
			} 
			 
			requestStream.write(trailer, 0, trailer.length);
			final int serverResponseCode = conn.getResponseCode();
			
			if (serverResponseCode < HttpStatus.SC_BAD_REQUEST) {
				is = conn.getInputStream();
//				Utils.removeFromMap(getSharedPreferences(Utils.PREFS_NAME, 0),album_id);
			}

		} finally {
			closeOutputStream(requestStream);
			closeConnection(conn);
		}
	}
	
	
	private static int debug_file_len;
	
	private	static void uploadFiles(final String uploadId, final OutputStream requestStream,
			final ArrayList<FileToUpload> filesToUpload, final byte[] boundaryBytes, 
			final int current, final int total) throws Exception {
		final long totalBytes = getTotalBytes(filesToUpload);
		long uploadedBytes = 0;
		int prog = 0;
		int two_files = filesToUpload.size();
		for (int i = 0; i < two_files; i++) {
			FileToUpload _file = filesToUpload.get(i);
			requestStream.write(boundaryBytes, 0, boundaryBytes.length);
			byte[] headerBytes = _file.getMultipartHeader();
			requestStream.write(headerBytes, 0, headerBytes.length);

			final InputStream stream = _file.getStream();
			byte[] buffer = new byte[BUFFER_SIZE];
			long bytesRead;
			debug_file_len = 0;
			try  {
				while ((bytesRead = stream.read(buffer, 0, buffer.length)) > 0) {
					requestStream.write(buffer, 0, buffer.length);
					uploadedBytes += bytesRead;
					debug_file_len += bytesRead;; 
				}
				int jj=234;
				jj++;
				if (!filesToUpload.get(i).getFileName().contains("tmb")) {
					prog++;
//					broadcastProgress(uploadId, current, total);
				}
			} 
			finally  {
				closeInputStream(stream);
			}
		}
	}


	private static HttpURLConnection getMultipartHttpURLConnection(final String url, final String method, final String boundary)
			throws IOException {
		final HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
		conn.setDoInput(true);
		conn.setDoOutput(true);
		conn.setUseCaches(false);
		conn.setChunkedStreamingMode(0);
		conn.setConnectTimeout(CONNECTION_TIMEOUT_MILLI);
		conn.setRequestMethod(method);
		conn.setRequestProperty("Connection", "Keep-Alive");
		conn.setRequestProperty("ENCTYPE", "multipart/form-data");
		conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

		return conn;
	}

	private static void closeOutputStream(final OutputStream stream) {
		if (stream != null) {
			try {
				stream.flush();
				stream.close();
			} catch (Exception exc) {
			}
		}
	}

	private static void closeConnection(final HttpURLConnection connection) {
		if (connection != null) {
			try {
				connection.disconnect();
			} catch (Exception exc) {
			}
		}
	}

	private static byte[] getBoundaryBytes(final String boundary) throws UnsupportedEncodingException {
		final StringBuilder builder = new StringBuilder();
		builder.append(NEW_LINE).append(TWO_HYPHENS).append(boundary).append(NEW_LINE);

		return builder.toString().getBytes("US-ASCII");
	}

	private static String getBoundary() {
		final StringBuilder builder = new StringBuilder();
		builder.append("---------------------------").append(System.currentTimeMillis());
		return builder.toString();
	}

	private static void setRequestHeaders(final HttpURLConnection conn, final ArrayList<NameValue> requestHeaders) {
		if (requestHeaders != null && !requestHeaders.isEmpty()) {
			for (final NameValue param : requestHeaders) {
				conn.setRequestProperty(param.getName(), param.getValue());
			}
		}
	}

 
	 
	private static void setRequestParameters(final OutputStream requestStream, 
			final ArrayList<NameValue> requestParameters,
			final byte[] boundaryBytes) throws Exception {
		if (!requestParameters.isEmpty()) {
			for (final NameValue parameter : requestParameters) {
				requestStream.write(boundaryBytes, 0, boundaryBytes.length);
				byte[] formitembytes = parameter.getBytes();
				requestStream.write(formitembytes, 0, formitembytes.length);
			}
		}
	}

	private static byte[] getTrailerBytes(final String boundary) throws Exception {
		final StringBuilder builder = new StringBuilder();
		builder.append(NEW_LINE).append(TWO_HYPHENS).append(boundary).append(TWO_HYPHENS).append(NEW_LINE);
		return builder.toString().getBytes("US-ASCII");
	}

	static class StringArrContainer {
		public String[] upload_success;
	};
	
	private static long getTotalBytes(final ArrayList<FileToUpload> filesToUpload) {
		long total = 0;
		for (FileToUpload file : filesToUpload) {
			if (file != null) {
				total += file.length();
			}
		}
		return total;
	}

	private static void closeInputStream(final InputStream stream) {
		if (stream != null) {
			try {
				stream.close();
			} catch (Exception exc) {
			}
		}
	}


	@Override
	public IBinder onBind(Intent intent) {

		return null;
	}
	
	public static boolean isUploadInProcess(String album_id)
	{
		UploadParams nextUploadTask = UploadParamsSerializer.fetchNextUpload();
		if (nextUploadTask != null) {
			return true;
		}
		else
		{
			return false;
		}
	}
	
}


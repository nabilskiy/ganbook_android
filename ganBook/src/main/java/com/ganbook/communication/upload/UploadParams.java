package com.ganbook.communication.upload;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.ganbook.communication.json.transmitter.JsonTransmitter;
import com.ganbook.communication.upload.UploadService.CopyFileRecord;
import com.ganbook.debug.IsLab;

public class UploadParams {		
	public final String uploadId = UUID.randomUUID().toString(); 
	public final String url = JsonTransmitter.ApiToUrl(JsonTransmitter.ApiTypes.createpicture);
	public final String method = "POST";
	public ArrayList<FileToUpload> filesToUpload;
	public ArrayList<String> resizedFiles;
	public final ArrayList<NameValue> requestHeaders = null;
	public final ArrayList<CopyFileRecord> filesToCopy;
//	public ArrayList<NameValue> requestParameters;
//	public int current;
//	public int total;
	public final String album_id; 
	public final String gan_id;
	public final String class_id;
	public final int total_num;
	public int num_uploaded;
	public String task_name = IsLab.getTaskName();
	
	//public String remote_dirName; // to be used by FtpManager
	public final boolean is_video;
	
	public transient boolean completionPushWasSent = false;
	
	public final String uploadTaskId = UUID.randomUUID().toString();
	
	
	UploadParams(ArrayList<FileToUpload> filesToUpload, 
			ArrayList<CopyFileRecord> filesToCopy, 
			String album_id, 
			String gan_id, 
			String class_id,
			boolean is_video) {
		this.filesToUpload = filesToUpload;
		this.filesToCopy = filesToCopy;
		this.album_id = album_id;
		this.gan_id = gan_id;
		this.class_id = class_id;
		this.total_num = filesToUpload.size();
		this.is_video = is_video;
	}

	public int numFiles() {
		return filesToUpload.size();
	}

	public String getFilePath(int ind) {
		return filesToUpload.get(ind).getFile().getAbsolutePath();
	}

	public String getOrigFilePath(int ind) {
		return filesToUpload.get(ind).origFilePath;
	}

	public String getResizedFilePath(int ind) {
		if (resizedFiles==null || ind < 0 || ind >=  resizedFiles.size()) {
			return null;
		}
		return resizedFiles.get(ind);
	}
	
	public void setResizedFilePath(int fileInd, String absolutePath) {
		if (resizedFiles==null) {
			resizedFiles = new ArrayList<String>();
		}
		while (resizedFiles.size() < fileInd+2) {
			resizedFiles.add(null);
		}
		resizedFiles.add(fileInd, absolutePath);
	}

	
	public void removeFilesFromList(int f1_ind, int f2_ind) {
		ArrayList<FileToUpload> new_fileArr = new ArrayList<FileToUpload>();
		int len = filesToUpload.size();
		for (int ii = 0; ii < len; ii++) {
			if (ii != f1_ind && ii != f2_ind) {
				new_fileArr.add(filesToUpload.get(ii));
			}
		}
		filesToUpload = new_fileArr;
		num_uploaded += 2; //!
	}

	public boolean hasMoreFiles() {
		return filesToUpload != null && !filesToUpload.isEmpty();
	}

	public String getDebugPref() {
		return task_name + "(" + num_uploaded + "/" + total_num + ") ";
	}

}


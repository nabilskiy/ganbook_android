package com.ganbook.communication.upload.state;

import java.util.Iterator;
import java.util.LinkedList;

import com.ganbook.activities.MainActivity;

public class SingleAlbumUpload {
	
	public final String album_id;
	public final String uploadTaskId;
	public final LinkedList<SingleFileState> filesToUpload;
	public final int origNumUploads;
	
	SingleAlbumUpload(String album_id, String uploadTaskId, LinkedList<SingleFileState> filesToUpload) {
		this.album_id = album_id;
		this.uploadTaskId = uploadTaskId;
		this.filesToUpload = filesToUpload;
		this.origNumUploads = filesToUpload==null ? 0 : filesToUpload.size(); 
	}
	
	public void onUploadStatusChanged(String album_id, String filePath, UploadStatus new_status, String remote_name) {
		for (SingleFileState f_state: filesToUpload) {
			if (f_state.imgPath.equals(filePath)) { 
				f_state.status = new_status;
				if(f_state.status == UploadStatus.SUCCESS)
				{
					f_state.uploadedName = remote_name;
				}
				MainActivity.refreshSingleAlbumView(album_id);
				return;
			}
		}		
	}
	
	public void onAlbumRefreshed() {
		// remove all successfully uploaded files (henceforth s/b obtained from server!)
		final int orig_size = filesToUpload.size();
		Iterator<SingleFileState> itor = filesToUpload.iterator();
		int del_ctr = 0;
		while (itor.hasNext()) {
			SingleFileState file = itor.next();
			if (file.status == UploadStatus.SUCCESS) {
				itor.remove();
				del_ctr++;
			}
		}
		int post_size = filesToUpload.size();
		if (orig_size  != post_size + del_ctr) {
			int jj=234;
			jj++;
		}
	}
	

	public void changeMarkGlobally(boolean pending2failed) {
		if (filesToUpload==null) return;
		for (SingleFileState file: filesToUpload) {
			if (pending2failed) {
				if (file.status == UploadStatus.PENDING) { 
					file.status = UploadStatus.FAILED;
				}
			} else {
				if (file.status == UploadStatus.FAILED) {
					file.status = UploadStatus.PENDING;
				}				
			}
		}
	}
}


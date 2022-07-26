package com.ganbook.communication.upload.state;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;

import com.ganbook.activities.MainActivity;
import com.ganbook.communication.json.getpicture_Response;
import com.ganbook.utils.FileDescriptor;


public class UploadStateRepository {
	private UploadStateRepository() {}

	
	private static final HashMap<String, LinkedList<SingleAlbumUpload>> state = 
			new HashMap<String, LinkedList<SingleAlbumUpload>>();


	public static void onStartUpload(String album_id, String uploadTaskId,
			ArrayList<String> filesToUpload, String[] thumbPathArr) {		
		LinkedList<SingleAlbumUpload> albumEntry = state.get(album_id);
		if (albumEntry == null) {
			state.put(album_id, new LinkedList<SingleAlbumUpload>());
			albumEntry = state.get(album_id);
		}		
		LinkedList<SingleFileState> filesToUploadList = new LinkedList<SingleFileState>();
		for (int i = 0; i < filesToUpload.size(); i++) {
			String imgPath = filesToUpload.get(i);
			String thumbPath = thumbPathArr[i];
			filesToUploadList.add(new SingleFileState(imgPath, thumbPath, UploadStatus.PENDING, null));
		}
		albumEntry.addLast(new SingleAlbumUpload(album_id, uploadTaskId, filesToUploadList));				
	}

	public static void updateAlbumUploads(
			String album_id, String imgPath) {
		LinkedList<SingleAlbumUpload> albumEntry = state.get(album_id);
		if (albumEntry == null) {
			int jj=234;
			jj++;
			return;
		}

		for (SingleAlbumUpload upload: albumEntry) {
			if (upload.filesToUpload == null) {
				continue;
			}
			int ind=0;
			for (SingleFileState file : upload.filesToUpload) {
				if(file.imgPath.equals(imgPath))
				{
					upload.filesToUpload.remove(ind);
					break;
				}
				ind++;
			}
		}

		state.put(album_id,albumEntry);
	}

	
	public static ArrayList<getpicture_Response> expandWithUploads(
			String album_id, ArrayList<getpicture_Response> imagesArr) {
		LinkedList<SingleAlbumUpload> albumEntry = state.get(album_id);
		if (albumEntry == null) {
			int jj=234;
			jj++;
			return imagesArr;
		}
		
		for (SingleAlbumUpload upload: albumEntry) {
			if (upload.filesToUpload == null) {
				continue;
			}

			for (SingleFileState file: upload.filesToUpload) {

				getpicture_Response pict = new getpicture_Response();
				pict.uploadState = file.status;
				pict.uploadImgFilePath = file.imgPath;
				pict.uploadThumbFilePath = file.thumbPath;
				pict.picture_name = file.uploadedName;

				boolean found = false;

				for (getpicture_Response getpicture_response: imagesArr) {
					if(getpicture_response.picture_name != null && getpicture_response.picture_name.equals(file.uploadedName))
					{
						found = true;
						break;
					}
				}

				if(!found) {
					imagesArr.add(pict);
				}
			}
		}		
		int jj=234;
		jj++;
		return imagesArr;
	}
	
	
	public static void expandWithUploads_FD(String album_id, ArrayList<FileDescriptor> all_images) {
		LinkedList<SingleAlbumUpload> albumEntry = state.get(album_id);
		if (albumEntry == null) {
			int jj=234;
			jj++;
			return;
		}
		
		for (SingleAlbumUpload upload: albumEntry) {
			if (upload.filesToUpload == null) {
				continue;
			}
			for (SingleFileState file: upload.filesToUpload) {
//				getpicture_Response pict = new getpicture_Response();
//				pict.uploadState = file.status;
//				pict.uploadImgFilePath = file.imgPath;
//				pict.uploadThumbFilePath = file.thumbPath;

				String url = file.imgPath;
				File file_d = null;

				if(file.imgPath.contains("VID"))
				{
					file_d = new File(file.thumbPath);
//					url = file.imgPath;
				}
				else
				{
					String path = file.imgPath;
//					path = path.replace("tmb","");
					file_d = new File(path);
				}
				all_images.add( new FileDescriptor(url, file_d, file.status));
			}
		}		
	}
		
	
	public static int count_All_Successfully_Uploaded_Images(String album_id) {
		LinkedList<SingleAlbumUpload> albumEntry = state.get(album_id);
		if (albumEntry == null) {
			return 0;
		}		
		
		int ctr = 0;
		for (SingleAlbumUpload upload: albumEntry) {
			if (upload.filesToUpload != null) {
				for (SingleFileState f: upload.filesToUpload) {
					if (f.status == UploadStatus.SUCCESS) ctr++;					
				}
//				int _size = upload.filesToUpload.size();
//				ctr += _size; 
//				ctr += upload.numUploadImages();
			}
		}
		return ctr;
	}

	

	public static void onUploadStatusChanged(String album_id, String uploadTaskId, 
			String filePath, UploadStatus new_status, String remote_name) {
		LinkedList<SingleAlbumUpload> albumEntry = state.get(album_id);
		if (albumEntry == null) {
			int jj=234;
			jj++;
			return;
		}
		
		for (SingleAlbumUpload upload: albumEntry) {
			if (upload.uploadTaskId.equals(uploadTaskId)) {
				upload.onUploadStatusChanged(album_id, filePath, new_status, remote_name);
				return; 
			}
		}		
		int jj=234;
		jj++;
	}
		

	public static void onAlbumRefreshed(String album_id) {
		LinkedList<SingleAlbumUpload> albumEntry = state.get(album_id);
		if (albumEntry != null) {
			for (SingleAlbumUpload upload: albumEntry) {
				upload.onAlbumRefreshed();
			}		
		}		
	}
	
	
	public static void markFailedAsPending() {
		changeMarkGlobally(false);
	}

	public static void markAllPendingAsFailed() {
		changeMarkGlobally(true); 
	}
	
	private static void changeMarkGlobally(boolean pending2failed) {
		Collection<LinkedList<SingleAlbumUpload>> allUploads = state.values();
		for (LinkedList<SingleAlbumUpload> perAlbumUploads: allUploads) {
			if (perAlbumUploads==null) {
				continue;
			}
			String album_id = null; 
			for (SingleAlbumUpload upload: perAlbumUploads) {
				upload.changeMarkGlobally(pending2failed);
				if (album_id==null) {
					album_id = upload.album_id;
				}
			}
			
			if (album_id != null) {
				MainActivity.refreshSingleAlbumView(album_id);
			}
		}
		
	}

	
}

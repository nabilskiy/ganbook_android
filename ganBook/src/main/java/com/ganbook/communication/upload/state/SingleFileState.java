package com.ganbook.communication.upload.state;

public class SingleFileState {
	public final String imgPath;
	public final String thumbPath;
	public String uploadedName;
	public UploadStatus status;
	
	SingleFileState(String imgPath, String thumbPath, UploadStatus status, String uploadedName) {
		this.imgPath = imgPath;
		this.thumbPath = thumbPath;
		this.status = status;
		this.uploadedName = uploadedName;
	}
}
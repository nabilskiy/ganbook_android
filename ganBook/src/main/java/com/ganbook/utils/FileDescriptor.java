package com.ganbook.utils;

import com.ganbook.communication.upload.state.UploadStatus;

import java.io.File;

public class FileDescriptor {
	public final String img_url;
	public final File img_file;
	public final UploadStatus upload_state;
	
	public FileDescriptor(String url, File file, UploadStatus upload_state) {
		this.img_url = url;
		this.img_file = file;
		this.upload_state = upload_state;
	}

}

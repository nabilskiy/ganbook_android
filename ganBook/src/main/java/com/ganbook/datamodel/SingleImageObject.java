package com.ganbook.datamodel;

import java.util.ArrayList;

import com.ganbook.communication.json.getfavorite_Response;
import com.ganbook.communication.json.getpicture_Response;
import com.ganbook.utils.FileDescriptor;

public class SingleImageObject {
	public int numImages;
	public boolean modeShare = true;
	public int curPosition;
	public ArrayList<FileDescriptor> fullSizeImageUrls;
	public ArrayList<getpicture_Response> albumPictureArr;
	public String year;
	public String gan_id;
	public String class_id;
	
	public SingleImageObject(int position, boolean modeShare, ArrayList<FileDescriptor> all_images, ArrayList<getpicture_Response> albumPictureArr, String year, String class_id, String gan_id)
	{
		curPosition = position;
		this.modeShare = modeShare;
		fullSizeImageUrls = (ArrayList<FileDescriptor>) all_images.clone();
		this.albumPictureArr = (ArrayList<getpicture_Response>) albumPictureArr.clone();
		numImages = fullSizeImageUrls.size();
		this.year = year;
		this.class_id = class_id;
		this.gan_id = gan_id;
	}
}

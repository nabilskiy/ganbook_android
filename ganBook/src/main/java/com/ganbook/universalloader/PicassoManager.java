package com.ganbook.universalloader;

import android.app.Activity;
import android.widget.ImageView;

import com.project.ganim.R;
import com.squareup.picasso.Picasso;

import java.io.File;

public class PicassoManager {
 
	// Picasso Defaults:
	//   memory cache of 15% the available application RAM
	//   Disk cache of 2% storage space up to 50MB but no less than 5MB. 	
	//   Three download threads for disk and network access.
	
	public static void displayImage(Activity a, String _url, ImageView _view, int placeholderResId) {
		int w = _view.getWidth();
		int h = _view.getHeight(); 
		Picasso.with(a) 
			.load(_url)  
			.placeholder(placeholderResId) 
			//	        .error(R.drawable.user_placeholder_error)
//			.resize(50,50) 
//			.resize(w, h)  
//			.centerCrop()  
			.into(_view);

	}

	public static void displayImage(Activity a, String _url, ImageView _view) {
		int w = _view.getWidth();
		int h = _view.getHeight();
		Picasso.with(a)
				.load(_url)
				.placeholder(R.drawable.empty_image_album)
						//	        .error(R.drawable.user_placeholder_error)
//			.resize(50,50)
//			.resize(w, h)
//			.centerCrop()
				.into(_view);

	}


	public static void displayImage(Activity a, File imgFile, ImageView _view, int placeholderResId) {
		int w = _view.getWidth();
		int h = _view.getHeight(); 
		Picasso.with(a) 
			.load(imgFile)  
			.placeholder(placeholderResId) 
			//	        .error(R.drawable.user_placeholder_error)
//			.resize(50,50) 
//			.resize(w, h)  
//			.centerCrop()  
			.into(_view);

	}
	
	public static void displayImage_CenterCrop(Activity a, String _url, 
			ImageView _view, int dim, int placeholderResId) {
//		int w = _view.getWidth();
//		int h = _view.getHeight(); 
		Picasso.with(a) 
			.load(_url)  
			.placeholder(placeholderResId) 
			//	        .error(R.drawable.user_placeholder_error)
//			.resize(50,50) 
//			.resize(dim, dim)
			.centerCrop()  
			.into(_view);

	}


}

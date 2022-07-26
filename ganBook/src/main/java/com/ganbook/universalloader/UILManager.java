package com.ganbook.universalloader;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;

import com.ganbook.app.MyApp;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.project.ganim.R;

import java.io.File;

public class UILManager {
	private UILManager() {}

	
	public static ImageLoader imageLoader;
	
	private static String _CACHE_DIR; // = sdcard + "/.temp_tmp";
	
	public static String getUILCacheDir() {
		if (_CACHE_DIR == null) {
			try { 
				String sdcard = Environment.getExternalStorageDirectory().getAbsolutePath();
				_CACHE_DIR = sdcard + "/.temp_tmp";
			}
			catch (Exception e) {
				int jj=234;
				jj++;
			}
		}
		return _CACHE_DIR;
	}
	


	public static void initImageLoader() { 
		try {
			Context context = MyApp.context;
			String cacheDirPath = getUILCacheDir();
			if (cacheDirPath==null) {
				return;
			}
			new File(cacheDirPath).mkdirs();

			File cacheDir = StorageUtils.getOwnCacheDirectory(context, cacheDirPath);

//			DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
//					.cacheOnDisc(true)
//					.imageScaleType(ImageScaleType.EXACTLY)
//					.bitmapConfig(Bitmap.Config.RGB_565)
//					.build();
			
			ImageLoaderConfiguration.Builder builder = 
					new ImageLoaderConfiguration.Builder(context)
//					.defaultDisplayImageOptions(defaultOptions)
					.discCache(new UnlimitedDiscCache(cacheDir))
					.threadPoolSize(3) // default
                    .threadPriority(Thread.NORM_PRIORITY - 2) // default
					.memoryCache(new WeakMemoryCache());

			ImageLoaderConfiguration config = builder.build();
			imageLoader = ImageLoader.getInstance();
			imageLoader.init(config);
			
		} catch (Exception e) {
			int jj=3453;
			jj++;
			imageLoader = null;
		}
	}

	public static DisplayImageOptions createDefaultDisplayOpyions(int emptyImgId) {
		DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
		.cacheOnDisc(true)
		.considerExifParams(true)
		.imageScaleType(ImageScaleType.EXACTLY)
		.bitmapConfig(Bitmap.Config.RGB_565)
		.showImageOnLoading(emptyImgId) 
        .showImageForEmptyUri(emptyImgId)
        .showImageOnFail(emptyImgId)
		.displayer(new FadeInBitmapDisplayer(150))
        .cacheInMemory(true)
		.build();
		return defaultOptions;
	}
	
	public static DisplayImageOptions createDefaultDisplayOpyions() {
		DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
		.cacheOnDisc(true)
		.considerExifParams(true)
		.imageScaleType(ImageScaleType.EXACTLY)
		.bitmapConfig(Bitmap.Config.RGB_565)
//		.showImageOnLoading(R.drawable.empty_image) 
        .showImageForEmptyUri(R.drawable.empty_image)
//        .showImageOnFail(emptyImgId)
        //.cacheInMemory(true)
		.build();
		return defaultOptions;
	}


	
}

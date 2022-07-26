package com.ganbook.utils;

import java.io.File;

import android.os.Environment;

import com.ganbook.app.MyApp;
import com.ganbook.communication.CommConsts;

public class DirectoryUtils {
	private DirectoryUtils() {}
	
	public static File getAlbumDir() {
		return new File(getAlbumDirPath() + "/");
	}


	public static String getAlbumDirPath() {
		String album_id = MyApp.selAlbum.album_id;
		return getAlbumDirPath(album_id);
	}
	
	public static String getAlbumDirPath(String album_id) {
		String state = Environment.getExternalStorageState();
		if (!Environment.MEDIA_MOUNTED.equals(state)) {
			return null;
		}
		String root = Environment.getExternalStorageDirectory().toString();
		if (album_id==null) {
			return null;
		}
		final String albumDir = root + CommConsts.GANIM + album_id;
		return albumDir;
	}

}

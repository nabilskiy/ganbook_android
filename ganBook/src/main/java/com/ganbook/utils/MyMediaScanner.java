package com.ganbook.utils;

import java.io.File;

import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;
import android.net.Uri;

import com.ganbook.app.MyApp;

public class MyMediaScanner implements MediaScannerConnectionClient {

	private final File file;
	private MediaScannerConnection scanner;

	public MyMediaScanner(String filePath) {
		file = new File(filePath);
		scanner = new MediaScannerConnection(MyApp.context, this);
		scanner.connect();
	}

	@Override
	public void onMediaScannerConnected() {
		if (scanner != null) {
			scanner.scanFile(file.getAbsolutePath(), null);
		}
	}

	@Override
	public void onScanCompleted(String path, Uri uri) {
		try { scanner.disconnect(); } catch (Exception e) {}
		scanner = null;
	}

}

package com.ganbook.asynctasks;

import android.os.AsyncTask;

import com.ganbook.communication.ICompletionDownloadHandler;
import com.ganbook.utils.ImageUtils;

import java.io.File;

/**
 * Created by dmytro_vodnik on 6/22/16.
 * working on ganbook1 project
 */
public class DownloadTask extends AsyncTask<String,Void,File> {
    ICompletionDownloadHandler handler;
    String imgUrl;

    public DownloadTask(String imgUrl, ICompletionDownloadHandler handler)
    {
        this.imgUrl = imgUrl;
        this.handler = handler;
    }

    @Override
    protected File doInBackground(String... params) {
        return ImageUtils.downloadToSdcard(imgUrl);
    }

    @Override
    protected void onPostExecute(File file) {
        super.onPostExecute(file);
        handler.onComplete(file);
    }
}

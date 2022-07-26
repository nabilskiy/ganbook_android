package com.ganbook.handlers;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;

import com.ganbook.amazinglist.special.CustomToast;
import com.ganbook.asynctasks.DownloadTask;
import com.ganbook.communication.ICompletionDownloadHandler;
import com.ganbook.models.PictureAnswer;
import com.ganbook.share.ShareManager;
import com.ganbook.user.User;
import com.ganbook.utils.StrUtils;
import com.project.ganim.R;

import java.io.File;

import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by dmytro_vodnik on 6/22/16.
 * working on ganbook1 project
 */
public class PictureDetailsHandlers {

    private static final String TAG = PictureDetailsHandlers.class.getName();
    private final PictureAnswer pictureAnswer;
    Context context;
    String classId, ganId;

    public PictureDetailsHandlers(PictureAnswer pictureAnswer, Context context, String ganId, String classId) {

        this.pictureAnswer = pictureAnswer;
        this.context = context;
        this.classId = classId;
        this.ganId = ganId;
    }

    public void onFavoriteClick(View view) {

        Log.d(TAG, "onFavoriteClick: ");


    }

    public void onSavePictureClick(View view) {

        Log.d(TAG, "onSavePictureClick: sharoni 1");

        final String imgUrl = StrUtils.getAlbumFullSizeUrl(pictureAnswer.getPictureName(),
                pictureAnswer.getAlbumId(), classId, ganId);
        if (imgUrl == null) {
            CustomToast.show((Activity) context, R.string.cannot_perform_op);
            return;
        }

        final SweetAlertDialog progress = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);
        progress.setTitleText(context.getString(R.string.operation_proceeding));
        progress.setContentText(context.getString(R.string.processing_wait));
        progress.setCancelable(false);
        progress.show();

        new DownloadTask(imgUrl, new ICompletionDownloadHandler() {
            @Override
            public void onComplete(File f) {

                progress.hide();
                long f_len = (f==null ? 0 : f.length());
                boolean success = (f != null);

                if (success) {
                    CustomToast.show((Activity) context, R.string.operation_succeeded);
                } else {
                    CustomToast.show((Activity) context, R.string.cannot_perform_op);
                }
            }
        }).execute();

    }

    public void onShareClick(View view) {


        final String imgUrl = StrUtils.getAlbumFullSizeUrl(pictureAnswer.getPictureName(),
                pictureAnswer.getAlbumId(), classId, ganId);

        Log.d(TAG, "onShareClick: img url = " + imgUrl);

        if (imgUrl == null) {
            CustomToast.show((Activity) context, R.string.cannot_perform_op);
            return;
        }

        new DownloadTask(imgUrl, new ICompletionDownloadHandler() {
            @Override
            public void onComplete(File f) {

                if (f==null) {
                    CustomToast.show((Activity) context, R.string.cannot_perform_op);
                    return;
                }

                String subject = "";

                String body = "";

                if(imgUrl.contains("VID")) {

                    String a_text = context.getResources().getString(R.string.watch_video);
                    String url = imgUrl.substring(imgUrl.indexOf("/ImageStore/"),imgUrl.length());
                    String href = "http://d1i2jsi02r7fxj.cloudfront.net" + url;
                    body = context.getResources().getString(R.string.share_pict_body) + "<br><a href=\""+href+"\">"+a_text+"</a>";

                    subject = context.getResources().getString(R.string.share_vid_subject);
                }
                else
                    subject = context.getResources().getString(R.string.share_pict_subject);


                ShareManager.onShareClick((Activity) context, subject + User.current.getCurrentGanName(),
                        R.string.share_pict_body, body, null, f);
            }
        }).execute();
    }
}

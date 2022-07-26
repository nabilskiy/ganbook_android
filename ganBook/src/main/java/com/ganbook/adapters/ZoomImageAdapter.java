package com.ganbook.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;

import androidx.viewpager.widget.PagerAdapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.ganbook.interfaces.PictureDetailsInterface;
import com.ganbook.models.PictureAnswer;
import com.ganbook.ui.zoomable.TouchImageView;
import com.ganbook.universalloader.PicassoManager;
import com.ganbook.universalloader.UILManager;
import com.ganbook.utils.StrUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.project.ganim.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by dmytro_vodnik on 6/22/16.
 * working on ganbook1 project
 */
public class ZoomImageAdapter extends PagerAdapter {

    private final String ganId;
    private final String classId;
    private LayoutInflater inf;
    private final DisplayImageOptions defaultOptions = UILManager.createDefaultDisplayOpyions();
    Context context;

    public ArrayList<PictureAnswer> getPictureAnswers() {
        return pictureAnswers;
    }

    ArrayList<PictureAnswer> pictureAnswers;
    PictureDetailsInterface pictureDetailsInterface;

    public ZoomImageAdapter(Context context, ArrayList<PictureAnswer> pictureAnswers,
                            PictureDetailsInterface pictureDetailsInterface, String ganId, String classId) {

        this.context = context;
        inf = LayoutInflater.from(context);
        this.pictureAnswers = pictureAnswers;
        this.pictureDetailsInterface = pictureDetailsInterface;
        this.ganId = ganId;
        this.classId = classId;
    }

    @Override
    public int getCount() {

        return pictureAnswers.size();
    }

    @Override
    public View instantiateItem(ViewGroup container, int position) {

        final PictureAnswer pictureAnswer = pictureAnswers.get(position);

        String url = StrUtils.getAlbumFullSizeUrl(pictureAnswer.getPictureName(), pictureAnswer.getAlbumId(),
                classId, ganId);
        String localPath = pictureAnswer.getLocaFilePath();
//        File file = new File(pictureAnswer.getLocaFilePath());

        View layout = null;

        if(url.contains("VID"))
        {
            layout = inf.inflate(R.layout.single_video, null);
            final ImageView play = (ImageView) layout.findViewById(R.id.play);
            final ImageView video = (ImageView) layout.findViewById(R.id.video);
            final ProgressBar spinner = (ProgressBar)layout.findViewById(R.id.upload_progress);

            String imageUrl = null;
            String playUrl = null;

            if(localPath != null) { // is uploading now...

                playUrl = Uri.fromFile(new File(url)).toString();

                PicassoManager.displayImage((Activity) context, localPath, video, R.drawable.empty_image);

                final boolean isUploadPicture = pictureAnswer.getStatus() == 0;
                if(isUploadPicture)
                {
                    boolean upload_suc = pictureAnswer.getStatus() == 1;
                    if(upload_suc)
                    {
//							final_url = url.replace("tmb","");
//							final_url = final_url.replace("jpeg","mp4");
                        play.setVisibility(View.VISIBLE);
                        spinner.setVisibility(View.GONE);
                    }
                    else
                    {
                        spinner.setVisibility(View.VISIBLE);
                        play.setVisibility(View.GONE);
                    }
                }

            }
            else // after uploaded
            {

                Picasso.with(context)
                        .load(StrUtils.getAlbumTmbUrl(pictureAnswer.getPictureName(),
                                pictureAnswer.getAlbumId(), classId, ganId))
                        .placeholder(R.drawable.empty_image)
                        .into(video, new Callback() {
                            @Override
                            public void onSuccess() {
                                spinner.setVisibility(View.GONE);
                                play.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onError() {

                                Picasso.with(context)
                                        .load(StrUtils.getAlbumTmbUrl(pictureAnswer.getPictureName(),
                                                pictureAnswer.getAlbumId(), classId, ganId).replace(".jpeg", ".jpg"))
                                        .placeholder(R.drawable.empty_image)
                                        .into(video, new Callback() {
                                            @Override
                                            public void onSuccess() {
                                                spinner.setVisibility(View.GONE);
                                                play.setVisibility(View.VISIBLE);
                                            }

                                            @Override
                                            public void onError() {


                                            }
                                        });
                            }
                        });

//                UILManager.imageLoader.displayImage(StrUtils.getAlbumTmbUrl(pictureAnswer.getPictureName(),
//                        pictureAnswer.getAlbumId(), classId, ganId),
//                        video, defaultOptions, new SimpleImageLoadingListener() {
//                    @Override
//                    public void onLoadingStarted(String imageUri, View view) {
//                        spinner.setVisibility(View.VISIBLE);
//                        play.setVisibility(View.GONE);
//                        view.setVisibility(View.GONE);
//                    }
//
//                    @Override
//                    public void onLoadingComplete(String imageUri, View view,
//                                                  Bitmap loadedImage) {
//
//                        super.onLoadingComplete(imageUri, view, loadedImage);
//                        spinner.setVisibility(View.GONE);
//                        play.setVisibility(View.VISIBLE);
//                        view.setVisibility(View.VISIBLE);
//                    }
//                });
            }

            final String finalUrl = url;


            play.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.parse(finalUrl), "video/*");
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);//DO NOT FORGET THIS EVER
                    context.startActivity(intent);
                }
            });
        } // end VIDEO
        else //if IMAGE
        {
            layout = inf.inflate(R.layout.single_pict, container, false);
            TouchImageView img = (TouchImageView)layout.findViewById(R.id.iv_image);
            final ProgressBar spinner = (ProgressBar)layout.findViewById(R.id.upload_progress);

            if(localPath != null) { //uploading

                url = Uri.fromFile(new File(localPath)).toString();
            }

            UILManager.imageLoader.displayImage(url, img, defaultOptions, new SimpleImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {
                    spinner.setVisibility(View.VISIBLE);
                    view.setVisibility(View.GONE);
                }

                @Override
                public void onLoadingComplete(String imageUri, View view,
                                              Bitmap loadedImage) {

                    super.onLoadingComplete(imageUri, view, loadedImage);
                    spinner.setVisibility(View.GONE);
                    view.setVisibility(View.VISIBLE);
                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    super.onLoadingFailed(imageUri, view, failReason);
                }
            });

            img.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    pictureDetailsInterface.hideHeaderFooter();
                }
            });
        } // end IMAGE


        container.addView(layout);

        return layout;
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getItemPosition(Object object){
        return PagerAdapter.POSITION_NONE;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    public void updateItem(int pos, PictureAnswer pictureAnswer) {

        pictureAnswers.set(pos, pictureAnswer);
        notifyDataSetChanged();
    }

}

package com.ganbook.utils.binding_utils;

import android.content.Context;
import androidx.databinding.BindingAdapter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.ganbook.models.PictureAnswer;
import com.ganbook.user.User;
import com.ganbook.utils.StrUtils;
import com.project.ganim.R;
import com.squareup.picasso.Picasso;

import java.io.File;

/**
 * Created by dmytro_vodnik on 6/7/16.
 * working on ganbook1 project
 */
public class PictureBinding {

    private static final String TAG = "PICTURE BINDING";

    @BindingAdapter({"bind:picPath"})
    public static void loadPicFromPath(final ImageView view, String url){

        Context context = view.getContext();

        Picasso.with(context).cancelRequest(view);

        if (url != null)
            Picasso.with(context)
                    .load(url)
                    .placeholder(R.drawable.album_off1)
                    .error(R.drawable.album_off)
                    .into(view);
        else
            Picasso.with(context).load(R.drawable.album_off).into(view);
    }

    @BindingAdapter({"bind:mediaFile"})
    public static void loadMedia(final ImageView view, String filePath){

        Context context = view.getContext();

        Picasso.with(context).cancelRequest(view);

        int size = (int) view.getResources().getDimension(R.dimen.img_tmb_size);

        RequestOptions myOptions = new RequestOptions()
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.album_off1)
                .error(R.drawable.album_off1)
                .override(size, size);

        Glide.with(context)
                .load(filePath)
                .apply(myOptions)
                .into(view);
    }

    @BindingAdapter({"bind:albumPicPath", "bind:tmb"})
    public static void loadAlbumPicFromPath(final ImageView view, PictureAnswer pictureAnswer, boolean tmb){

        Context context = view.getContext();

        view.setImageDrawable(null);

        Log.d(TAG, "loadAlbumPicFromPath: picture answer = " + pictureAnswer);

        Picasso.with(context).cancelRequest(view);
        int size = (int) view.getResources().getDimension(R.dimen.img_tmb_size);

        if (pictureAnswer.getPictureName() != null) {
            String class_id = User.current.getCurrentClassId();
            String gan_id = User.current.getCurrentGanId();

            if (pictureAnswer.getLocaFilePath() != null) {

                Drawable drawable = null;


                if (pictureAnswer.getVideoDuration() == null || pictureAnswer.getVideoDuration().equals("")) {

                } else
                    drawable = new BitmapDrawable(view.getResources(), ThumbnailUtils.createVideoThumbnail(pictureAnswer.getLocaFilePath(),
                            MediaStore.Images.Thumbnails.MINI_KIND));

                Picasso.with(context)
                        .load(new File(pictureAnswer.getLocaFilePath()))
                        .placeholder(drawable)
                        .resize(size, size)
                        .into(view);

                ColorMatrix matrix = new ColorMatrix();
                matrix.setSaturation(0);

                ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);

                view.setColorFilter(filter);

            } else {

                view.setColorFilter(null);

                String url = StrUtils.getAlbumFullSizeUrl(pictureAnswer.getPictureName(), pictureAnswer.getAlbumId(), class_id, gan_id);

                if (pictureAnswer.getVideoDuration()!= null && !pictureAnswer.getVideoDuration().equals(""))
                    url = StrUtils.getAlbumTmbUrl(pictureAnswer.getPictureName(), pictureAnswer.getAlbumId(), class_id, gan_id);

                Picasso.with(context)
                        .load(url)
                        .resize(size, size)
                        .into(view);
            }
        }
        else
            Picasso.with(context).load(R.drawable.album_off).into(view);
    }


}

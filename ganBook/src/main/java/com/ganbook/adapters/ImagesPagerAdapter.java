package com.ganbook.adapters;

import android.content.Context;
import androidx.viewpager.widget.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.ganbook.models.MediaFile;
import com.ganbook.universalloader.UILManager;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.project.ganim.R;

import java.util.ArrayList;

/**
 * Created by dmytro_vodnik on 6/29/16.
 * working on ganbook1 project
 */
public class ImagesPagerAdapter extends PagerAdapter {

    Context mContext;
    LayoutInflater mLayoutInflater;
    ArrayList<MediaFile> mediaFiles;
    private final DisplayImageOptions defaultOptions = UILManager.createDefaultDisplayOpyions();

    public ImagesPagerAdapter (Context context, ArrayList<MediaFile> mediaFiles) {

        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        this.mediaFiles = mediaFiles;
    }

    @Override
    public int getCount() {
        return mediaFiles.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((LinearLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        MediaFile mediaFile = mediaFiles.get(position);

        View itemView = mLayoutInflater.inflate(R.layout.item_image_pager, container, false);

        ImageView imageView = (ImageView) itemView.findViewById(R.id.imageView);

        RequestOptions myOptions = new RequestOptions()
                .fitCenter()
                .diskCacheStrategy(DiskCacheStrategy.ALL);

        Glide.with(mContext)
                .load(mediaFile.getFilePath())
                .apply(myOptions)
                .into(imageView);

        container.addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }


    public void removeItem(int currentItem) {
        mediaFiles.remove(currentItem);
        notifyDataSetChanged();
    }
}
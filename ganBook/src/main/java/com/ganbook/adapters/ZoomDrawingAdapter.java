package com.ganbook.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.ganbook.models.DrawingAnswer;
import com.ganbook.ui.zoomable.TouchImageView;
import com.ganbook.universalloader.UILManager;
import com.ganbook.user.User;
import com.ganbook.utils.StrUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.project.ganim.R;

import java.io.File;
import java.util.List;

public class ZoomDrawingAdapter extends PagerAdapter {

    private Context context;
    private List<DrawingAnswer> drawingAnswers;
    private LayoutInflater inflater;
    private final DisplayImageOptions defaultOptions = UILManager.createDefaultDisplayOpyions();

    public ZoomDrawingAdapter(Context context, List<DrawingAnswer> drawingAnswers) {
        this.context = context;
        this.drawingAnswers = drawingAnswers;
        inflater = LayoutInflater.from(context);
    }


    @NonNull
    @Override
    public View instantiateItem(@NonNull ViewGroup container, int position) {
        DrawingAnswer drawingAnswer = drawingAnswers.get(position);
        String url = StrUtils.getDrawingAlbumFullSizeUrl(drawingAnswer.getDrawingName(), User.current.getCurrentKidId(), User.current.getCurrentClassId(),User.current.getCurrentGanId());
        String localPath = drawingAnswer.getLocaFilePath();

        View view = inflater.inflate(R.layout.single_pict, container, false);
        TouchImageView img = view.findViewById(R.id.iv_image);
        final ProgressBar spinner = view.findViewById(R.id.upload_progress);

        if(localPath != null) {
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

        container.addView(view);

        return view;
    }

    @Override
    public int getCount() {
        return drawingAnswers.size();
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

    public void updateItem(int pos, DrawingAnswer drawingAnswer) {

        drawingAnswers.set(pos, drawingAnswer);
        notifyDataSetChanged();
    }
}

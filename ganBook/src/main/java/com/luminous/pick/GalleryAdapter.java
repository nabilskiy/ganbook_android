package com.luminous.pick;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ganbook.app.MyApp;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.project.ganim.R;

import java.util.ArrayList;

public class GalleryAdapter extends BaseAdapter {

	private static final String TAG = GalleryAdapter.class.getName();
	private Context mContext;
	private LayoutInflater infalter;
	private ArrayList<SingleImage> data = new ArrayList<SingleImage>();
//	private ImageLoader xxx_imageLoader;
	
	private static final int MAX_SELECTED_ITEMS = 20;
	private static final int THUMBNAIL_SIZE_PIX = 144;
	
	private boolean isActionMultiplePick,imageMode;

	private String max;

	private final DisplayImageOptions defaultOptions;

	public GalleryAdapter(Context c, DisplayImageOptions defaultOptions) { //, ImageLoader imageLoader) {
		this.infalter = (LayoutInflater) c
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.mContext = c;
		this.defaultOptions = defaultOptions;
		// clearCache();
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public SingleImage getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void setMultiplePick(boolean isMultiplePick) {
		this.isActionMultiplePick = isMultiplePick;
	}

	public void setImageMode(boolean isImageMode) {
		this.imageMode = isImageMode;
	}

	public void setMax(String max) {
		this.max = max;
	}

	public void selectAll(boolean selection) {
		for (int i = 0; i < data.size(); i++) {
			data.get(i).isSeleted = selection;
		}
		notifyDataSetChanged();
	}

	public boolean isAllSelected() {
		boolean isAllSelected = true;

		for (int i = 0; i < data.size(); i++) {
			if (!data.get(i).isSeleted) {
				isAllSelected = false;
				break;
			}
		}
		return isAllSelected;
	}

	public boolean isAnySelected() {
		boolean isAnySelected = false;
		for (int i = 0; i < data.size(); i++) {
			if (data.get(i).isSeleted) {
				isAnySelected = true;
				break;
			}
		}
		return isAnySelected;
	}

	public ArrayList<SingleImage> getSelected() {
		ArrayList<SingleImage> dataT = new ArrayList<SingleImage>();
		for (int i = 0; i < data.size(); i++) {
			if (data.get(i).isSeleted) {
				dataT.add(data.get(i));
			}
		}
		return dataT;
	}

	private int getNumSelected() {
		int ctr=0;
		for (int i = 0; i < data.size(); i++) {
			if (data.get(i).isSeleted) ctr++;
		}
		return ctr;
	}
	
	public void addAll(ArrayList<SingleImage> files) {
		try {
			this.data.clear();
			this.data.addAll(files);

		} catch (Exception e) {
			e.printStackTrace();
		}
		notifyDataSetChanged();
	}


	public void changeSelection(View v, int position) {
		boolean sel_new_mode = !data.get(position).isSeleted;
		if (sel_new_mode && max != null && getNumSelected() >= Integer.valueOf(max)) {

			if(imageMode) {
				MyApp.toast(R.string.too_many_images_selected, max);
			}
			else
			{
//				MyApp.toast(R.string.too_many_videos_selected, max);
			}
			return;
		}
		if (data.get(position).isSeleted) {
			data.get(position).isSeleted = false;
			CustomGalleryActivity.updateTitle(false);
		} else {
			data.get(position).isSeleted = true;
			CustomGalleryActivity.updateTitle(true);
		}

		Log.d(TAG, "changeSelection: video = " + data.get(position));

		((ViewHolder) v.getTag()).imgQueueMultiSelected.setSelected(data
				.get(position).isSeleted);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		final ViewHolder holder;
		if (convertView == null) {

			convertView = infalter.inflate(R.layout.gallery_item, null);
			holder = new ViewHolder();
			holder.imgQueue = (ImageView) convertView.findViewById(R.id.imgQueue);
			holder.imgPlay = (ImageView) convertView.findViewById(R.id.play);
//			holder.duration = (TextView) convertView.findViewById(R.id.duration);

			holder.imgQueueMultiSelected = (ImageView) convertView
					.findViewById(R.id.imgQueueMultiSelected);

			if (isActionMultiplePick) {
				holder.imgQueueMultiSelected.setVisibility(View.VISIBLE);
			} else {
				holder.imgQueueMultiSelected.setVisibility(View.GONE);
			}

			if(imageMode)
			{
				holder.imgPlay.setVisibility(View.GONE);
//				holder.duration.setVisibility(View.GONE);
			}
			else
			{
//				holder.duration.setText(data.get(position).duration);
				holder.imgPlay.setVisibility(View.VISIBLE);
//				holder.duration.setVisibility(View.VISIBLE);
			}

			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}
//		holder.imgQueue.setTag(position);
		try {			
			String filePath = data.get(position).sdcardPath;
			String url = "file://" + filePath;

			ImageView view = holder.imgQueue;

			Log.d(TAG, "getView: file path = " + filePath);

			Glide.with(mContext).load(data.get(position).uri)
					.into(view);

//			UILManager.imageLoader.displayImage(url, view, defaultOptions);
			
//			MyApp.g_imageLoader.displayImage("file://" + data.get(position).sdcardPath,
//					holder.xxx_imgQueue, new SimpleImageLoadingListener() {
//						@Override
//						public void onLoadingStarted(String imageUri, View view) {
//							holder.xxx_imgQueue
//									.setImageResource(R.drawable.no_media);
//							super.onLoadingStarted(imageUri, view);
//						}
//					});

			if (isActionMultiplePick) {

				holder.imgQueueMultiSelected
						.setSelected(data.get(position).isSeleted);

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return convertView;
	}

	public class ViewHolder {
		ImageView imgQueue;
		ImageView imgQueueMultiSelected;
		ImageView imgPlay;
		TextView duration;
	}

	public void clearCache() {
//		MyApp.g_imageLoader.clearDiscCache();
//		MyApp.g_imageLoader.clearMemoryCache();
	}

	public void clear() {
		data.clear();
		notifyDataSetChanged();
	}
}

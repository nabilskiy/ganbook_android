package com.ganbook.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.ganbook.amazinglist.special.CustomToast;
import com.ganbook.app.MyApp;
import com.ganbook.communication.ICompletionDownloadHandler;
import com.ganbook.communication.json.getpicture_Response;
import com.ganbook.communication.json.transmitter.JsonTransmitter;
import com.ganbook.communication.upload.state.UploadStatus;
import com.ganbook.share.ShareManager;
import com.ganbook.ui.zoomable.ExtendedViewPager;
import com.ganbook.ui.zoomable.TouchImageView;
import com.ganbook.universalloader.UILManager;
import com.ganbook.user.User;
import com.ganbook.utils.FileDescriptor;
import com.ganbook.utils.ImageUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.project.ganim.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class SingleImageFragment extends BaseFragment{
	private static SingleImageFragment fragment_inst;
	private View v;
	private Context context;
	
	private ImageButton share_pict_btn;
	private ImageButton upload_btn;
	private ViewSwitcher switcher;
	
	public RelativeLayout zoom_header; 
    public RelativeLayout zoom_footer;
    
    public TextView zoom_title;
	
	private ExtendedViewPager mViewPager;
	
	private int numImages;
	private boolean modeShare = true;
	private int curPosition;
	private ArrayList<FileDescriptor> fullSizeImageUrls;
	
	
//	public static void start(int position, 
//			boolean shareMode, ArrayList<FileDescriptor> imageUrlArr) {
//		modeShare = shareMode;
//		curPosition = position;
//		fullSizeImageUrls = imageUrlArr;
//		numImages = imageUrlArr.size();
//	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		context = activity;
	}
	
	@Override
	public void onStop() {
		
		super.onStop();
	}

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		fragment_inst = this;	
		
		modeShare = MyApp.singleImageObject.modeShare;
		curPosition = MyApp.singleImageObject.curPosition;
		fullSizeImageUrls = MyApp.singleImageObject.fullSizeImageUrls;
		numImages = MyApp.singleImageObject.numImages;
		
//		defaultOptions = UILManager.createDefaultDisplayOpyions(R.drawable.empty_image);
	}
	
	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		  
		super.onActivityCreated(savedInstanceState);
	}
	
	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		getActivity().setRequestedOrientation(
				ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
		
		
		v = inflater.inflate(R.layout.zoom_activity_layout, null);
		
//		v.setContentView(R.layout.zoom_activity_layout);
//		setActionBar(R.layout.zoomscreen_actionbar);
//		base_onCreateView();
		
//		action_bar_main_title.setVisibility(View.GONE);

		zoom_footer = (RelativeLayout) v.findViewById(R.id.zoom_footer);
		zoom_header = (RelativeLayout) v.findViewById(R.id.zoom_header);
		upload_btn = (ImageButton) v.findViewById(R.id.upload_btn);
		zoom_title = (TextView) v.findViewById(R.id.title);
		upload_btn.setOnClickListener(new View.OnClickListener() {  			
			@Override
			public void onClick(View v) {
//				Activity _this = ZoomActivity.this;
//				MainActivity.fragmentToMoveTo = FragmentType.Show_Gallery; 
//				_this.finish();
			}
		});
		 
		v.findViewById(R.id.save_right_btn).setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				Log.w("tag","sharoni");
				SingleImageFragmentPermissionsDispatcher.saveCurrentImageWithCheck(SingleImageFragment.this);
			}
		});

		share_pict_btn = (ImageButton) v.findViewById(R.id.share_pict_btn);
		share_pict_btn.setOnClickListener(new View.OnClickListener() {  			
			@Override
			public void onClick(View v) {
				SingleImageFragmentPermissionsDispatcher.shareCurrentPicWithCheck(SingleImageFragment.this);
			}
		});
				
		upload_btn.setVisibility(modeShare ? View.GONE : View.VISIBLE);
		share_pict_btn.setVisibility(modeShare ? View.VISIBLE : View.GONE);
		
		
		mViewPager = (ExtendedViewPager) v.findViewById(R.id.view_pager);

//		mViewPager.setOffscreenPageLimit(0);

		mViewPager.setAdapter(new ZoomActivityAdapter());		
		
		mViewPager.setCurrentItem(curPosition);
		mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				int jj=234;
				jj++;
				zoom_title.setText(position+1 + "/" + numImages); // move from 0 to 1 index

				if(position < MyApp.singleImageObject.albumPictureArr.size() &&
						MyApp.singleImageObject.albumPictureArr.get(position).favorite)
				{
					switcher.setDisplayedChild(1);
				}
				else
				{
					switcher.setDisplayedChild(0);
				}

			}			
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
				int jj=234;
				jj++;
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				int jj=234;
				jj++;
			}
		});
		
		
		zoom_title.setText(curPosition+1 + "/" + numImages); // move from 0 to 1 index

		switcher =(ViewSwitcher) v.findViewById(R.id.switcher);

		if(mViewPager.getCurrentItem() < MyApp.singleImageObject.albumPictureArr.size() &&
				MyApp.singleImageObject.albumPictureArr.get(mViewPager.getCurrentItem()).favorite)
		{
			switcher.setDisplayedChild(1);
		}
		else
		{
			switcher.setDisplayedChild(0);
		}

		switcher.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				int curInd = mViewPager.getCurrentItem();

				if(switcher.getDisplayedChild() == 0) //inactive
				{
					switcher.showNext();
					MyApp.singleImageObject.albumPictureArr.get(curInd).favorite = true;
					CustomToast.show(getActivity(),getActivity().getString(R.string.saved_to_favorites));
				}
				else
				{
					switcher.showPrevious();
					MyApp.singleImageObject.albumPictureArr.get(curInd).favorite = false;
					CustomToast.show(getActivity(),getActivity().getString(R.string.removed_from_favorites));
				}

				getpicture_Response picture = MyApp.singleImageObject.albumPictureArr.get(curInd);

				JsonTransmitter.send_updatepicfavorite(picture.picture_id);
				User.current.updateFavoritePic(picture,MyApp.singleImageObject.year,MyApp.singleImageObject.class_id, MyApp.singleImageObject.gan_id);
			}
		});
		
		return v; 
	}

	@NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
	public void shareCurrentPic() {

		final Activity a = getActivity();
		final String imgUrl = getCurrentImgUrl();
		if (imgUrl == null) {
			CustomToast.show(a, R.string.cannot_perform_op);
			return;
		}
//				final String albumDirPath = DirectoryUtils.getAlbumDirPath();
//				if (albumDirPath==null) {
//					CustomToast.show(a, R.string.cannot_perform_op);
//					return;
//				}

		File local_file = null;

//				if(!imgUrl.contains("VID"))
//				{
//					local_file = downloadToSdcard(imgUrl);
//					if (local_file==null) {
//						CustomToast.show(a, R.string.cannot_perform_op);
//						return;
//					}
//				}
//				long f_len = local_file.length();


		new DownloadTask(imgUrl, new ICompletionDownloadHandler() {
			@Override
			public void onComplete(File f) {
				if (f==null) {
					CustomToast.show(a, R.string.cannot_perform_op);
					return;
				}

				String subject = "";

				String body = "";

				if(imgUrl.contains("VID"))
				{
					String a_text = a.getResources().getString(R.string.watch_video);
					String url = getCurrentImgUrl().substring(imgUrl.indexOf("/ImageStore/"),imgUrl.length());
					String href = "http://d1i2jsi02r7fxj.cloudfront.net" + url;
					body = a.getResources().getString(R.string.share_pict_body) + "<br><a href=\""+href+"\">"+a_text+"</a>";

					subject = a.getResources().getString(R.string.share_vid_subject);
				}
				else
				{
					subject = a.getResources().getString(R.string.share_pict_subject);
				}

				ShareManager.onShareClick(a, subject + User.current.getCurrentGanName(),
						R.string.share_pict_body, body, null, f);
			}
		}).execute();
	}

	@NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
	public void saveCurrentImage() {
		final Activity a = getActivity();
		final String imgUrl = getCurrentImgUrl();				
		if (imgUrl == null) {
			CustomToast.show(a, R.string.cannot_perform_op);
			return;  
		}
//		final String albumDirPath = DirectoryUtils.getAlbumDirPath();
//		if (albumDirPath==null) {
//			CustomToast.show(a, R.string.cannot_perform_op);
//			return;
//		}
		
//		final boolean _success = success;
//		MyApp.runOnUIThread(new Runnable() {
//				public void run() {
				

//				try {
//					File f = downloadToSdcard(imgUrl);
//					long f_len = (f==null ? 0 : f.length());
//					success = (f != null);
		final SweetAlertDialog progress = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);
		progress.setTitleText(context.getString(R.string.operation_proceeding));
		progress.setContentText(context.getString(R.string.processing_wait));
		progress.setCancelable(false);
		progress.show();

					new DownloadTask(imgUrl, new ICompletionDownloadHandler() {
						@Override
						public void onComplete(File f) {
							progress.dismiss();
							long f_len = (f==null ? 0 : f.length());
							boolean success = (f != null);

							if (success) {
								CustomToast.show(a, R.string.operation_succeeded);
							} else {
								CustomToast.show(a, R.string.cannot_perform_op);
							}
						}
					}).execute();

//				}
//				catch (Exception e) {
//					int jj=234;
//					jj++;
//				}
				

//			}
//		});
	}

	private class DownloadTask extends AsyncTask<String,Void,File>
	{
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





	protected String getCurrentImgUrl() { 
		int curInd = mViewPager.getCurrentItem();
		if (curInd < 0 || curInd >= fullSizeImageUrls.size()) {
			return null;
		}
		String _url = fullSizeImageUrls.get(curInd).img_url;
		File _file = fullSizeImageUrls.get(curInd).img_file;
		if (_url == null && _file != null) {
			_url =  Uri.fromFile(_file).toString();
		}
		return _url;
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);

		SingleImageFragmentPermissionsDispatcher.saveCurrentImageWithCheck(this);
	}

	class ZoomActivityAdapter extends PagerAdapter {
		private LayoutInflater inf;
		private final DisplayImageOptions defaultOptions = UILManager.createDefaultDisplayOpyions();

		@Override
		public int getCount() {
//			return images.length;
			return fullSizeImageUrls.size();
		}
		
		public ZoomActivityAdapter() {
			inf = LayoutInflater.from(MyApp.context);
		}
		
		@Override
		public View instantiateItem(ViewGroup container, int position) {
			
			String url = fullSizeImageUrls.get(position).img_url;
			File file = fullSizeImageUrls.get(position).img_file;
//			String final_url = url;

			
//			if(url == null) // just uploaded
//			{
//				file =
//				url =  Uri.fromFile(file).toString();
//			}
			
			View layout = null;
			
			if(url.contains("VID"))
			{
				layout = inf.inflate(R.layout.single_video, null);
				final ImageView play = (ImageView) layout.findViewById(R.id.play);
				final ImageView video = (ImageView) layout.findViewById(R.id.video);
				final ProgressBar spinner = (ProgressBar)layout.findViewById(R.id.upload_progress);

				String imageUrl = null;
				String playUrl = null;

				if(file != null) { // is uploading now...

					imageUrl = Uri.fromFile(file).toString();
					playUrl = Uri.fromFile(new File(url)).toString();
//					spinner.setVisibility(View.GONE);
//					url = url.replace("file://","");
//					file = new File(url.replace("file://",""));
//					PicassoManager.displayImage(getActivity(), file, video, R.drawable.empty_image);
					final String finalUrl = url;
					Picasso.with(getActivity())
							.load(url)
							.placeholder(R.drawable.empty_image)
							.into(video, new Callback() {
								@Override
								public void onSuccess() {

								}

								@Override
								public void onError() {

									Picasso.with(getActivity())
											.load(finalUrl.replace(".jpeg", ".jpg"))
											.placeholder(R.drawable.empty_image)
											.into(video, new Callback() {
												@Override
												public void onSuccess() {

												}

												@Override
												public void onError() {


												}
											});
								}
							});
//					UILManager.imageLoader.displayImage(Uri.fromFile(file).toString(), video, defaultOptions);

					final boolean isUploadPicture = (fullSizeImageUrls.get(position).upload_state != null);
					if(isUploadPicture)
					{
						boolean upload_suc = (fullSizeImageUrls.get(position).upload_state == UploadStatus.SUCCESS);
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
					imageUrl = url.substring(0, url.indexOf(".mp4"));
					String imageUrl_start = imageUrl.substring(0, imageUrl.lastIndexOf("/") + 1);
					String imageUrl_end = imageUrl.substring(imageUrl.lastIndexOf("/") + 1, imageUrl.length());
					imageUrl = imageUrl_start + "tmb" + imageUrl_end + ".jpeg";

					playUrl = url;

					final String finalImageUrl = imageUrl;

					Picasso.with(getActivity())
							.load(imageUrl)
							.placeholder(R.drawable.empty_image)
							.into(video, new Callback() {
								@Override
								public void onSuccess() {
									spinner.setVisibility(View.GONE);
									play.setVisibility(View.VISIBLE);
								}

								@Override
								public void onError() {

									Picasso.with(getActivity())
											.load(finalImageUrl.replace(".jpeg", ".jpg"))
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
				}

				final String play_url = playUrl;

				play.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {

						Intent intent = new Intent(Intent.ACTION_VIEW);
						String type = "video/mp4";
						intent.setDataAndType(Uri.parse(play_url), type);
						//						intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						context.startActivity(intent);
					}
				});
			} // end VIDEO
			else //if IMAGE
			{
				layout = inf.inflate(R.layout.single_pict, container, false);				
				TouchImageView img = (TouchImageView)layout.findViewById(R.id.iv_image);
				final ProgressBar spinner = (ProgressBar)layout.findViewById(R.id.upload_progress);

				if(file != null) { //uploading
//					spinner.setVisibility(View.GONE);
//					PicassoManager.displayImage(getActivity(), file, img, R.drawable.empty_image);
					url = Uri.fromFile(file).toString();
				}
//				else
//				if (url != null) {

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

//				}
				
				img.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {

						if (fragment_inst != null) {
							fragment_inst.showHideFooter();
						}
					}
				});
			} // end IMAGE
			
//			container.addView(img, LinearLayout.LayoutParams.MATCH_PARENT,
//					LinearLayout.LayoutParams.MATCH_PARENT);
			container.addView(layout);
			
			return layout;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}
		
	}
	
	public void showHideFooter()
	{
		if(zoom_footer.getVisibility() == View.GONE)
		{
			zoom_footer.setVisibility(View.VISIBLE);
			zoom_header.setVisibility(View.VISIBLE);
		}
		else
		{
			zoom_footer.setVisibility(View.GONE);
			zoom_header.setVisibility(View.GONE);
		}
	}
	
	@Override
	public void onDestroy() {

		super.onDestroy();
		if (this == fragment_inst) {
			fragment_inst = null;
		}
	}
	
	@Override
	public void onDestroyView() {

		super.onDestroyView();
		getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	}
}

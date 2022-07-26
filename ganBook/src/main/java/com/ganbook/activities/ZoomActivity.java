package com.ganbook.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ganbook.amazinglist.special.CustomToast;
import com.ganbook.app.MyApp;
import com.ganbook.fragments.FragmentType;
import com.ganbook.share.ShareManager;
import com.ganbook.ui.zoomable.ExtendedViewPager;
import com.ganbook.ui.zoomable.TouchImageView;
import com.ganbook.universalloader.UILManager;
import com.ganbook.user.User;
import com.ganbook.utils.DirectoryUtils;
import com.ganbook.utils.FileDescriptor;
import com.ganbook.utils.MyMediaScanner;
import com.ganbook.utils.UrlUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.core.download.ImageDownloader;
import com.nostra13.universalimageloader.utils.IoUtils;
import com.project.ganim.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class ZoomActivity extends BaseAppCompatActivity {

	// zoom_activity_layout
		
	private static boolean modeShare = true;
	private static int curPosition;
	private static ArrayList<FileDescriptor> fullSizeImageUrls;
	
	private ImageButton share_pict_btn;
	private ImageButton upload_btn;
	
    private TextView zoom_title; 

	
	private ExtendedViewPager mViewPager;
	
	private static int numImages;
	
	public static void start(Context context, int position, 
			boolean shareMode, ArrayList<FileDescriptor> imageUrlArr) {
		modeShare = shareMode;
		curPosition = position;
		fullSizeImageUrls = imageUrlArr;
		numImages = imageUrlArr.size();

		context.startActivity(new Intent(context, ZoomActivity.class));		
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.zoom_activity_layout);


		upload_btn = (ImageButton) findViewById(R.id.upload_btn);
		zoom_title = (TextView) findViewById(R.id.zoom_title);
		upload_btn.setOnClickListener(new View.OnClickListener() {  			
			@Override
			public void onClick(View v) {
				Activity _this = ZoomActivity.this;
				MainActivity.fragmentToMoveTo = FragmentType.Show_Gallery; 
				_this.finish();
			}
		});
		 
		findViewById(R.id.save_right_btn).setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				saveCurrentImage();
			}
		});

		share_pict_btn = (ImageButton) findViewById(R.id.share_pict_btn);
		share_pict_btn.setOnClickListener(new View.OnClickListener() {  			
			@Override
			public void onClick(View v) {
				final Activity a = ZoomActivity.this;
				final String imgUrl = getCurrentImgUrl();				
				if (imgUrl == null) {
					CustomToast.show(a, R.string.cannot_perform_op);
					return;  
				}
				final String albumDirPath = DirectoryUtils.getAlbumDirPath();
				if (albumDirPath==null) {
					CustomToast.show(a, R.string.cannot_perform_op); 
					return;					
				} 
				File local_file = downloadToSdcard(imgUrl);
				if (local_file==null) {
					CustomToast.show(a, R.string.cannot_perform_op); 
					return;										
				}
				long f_len = local_file.length(); 
				ShareManager.openShareMenu(a, R.string.share_pict_subject + User.current.getCurrentGanName(), 
						R.string.share_pict_body, null, null, local_file);
			}
		});
				
		upload_btn.setVisibility(modeShare ? View.GONE : View.VISIBLE);
		share_pict_btn.setVisibility(modeShare ? View.VISIBLE : View.GONE);
		
		
		mViewPager = (ExtendedViewPager) findViewById(R.id.view_pager);
		mViewPager.setAdapter(new ZoomActivityAdapter());		
		
		mViewPager.setCurrentItem(curPosition);
		mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				int jj=234;
				jj++;
				zoom_title.setText(position+1 + "/" + numImages); // move from 0 to 1 index
			}			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
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

	}
 
	
	protected void saveCurrentImage() {
		final ZoomActivity a = this;
		final String imgUrl = getCurrentImgUrl();				
		if (imgUrl == null) {
			CustomToast.show(a, R.string.cannot_perform_op);
			return;  
		}
		final String albumDirPath = DirectoryUtils.getAlbumDirPath();
		if (albumDirPath==null) {
			CustomToast.show(a, R.string.cannot_perform_op); 
			return;					
		}
		
//		final boolean _success = success;
		MyApp.runOnUIThread(new Runnable() {
			public void run() {
				
				boolean success = false;
				try { 
					File f = downloadToSdcard(imgUrl);
					long f_len = (f==null ? 0 : f.length()); 
					success = (f != null); 
				}
				catch (Exception e) {
					int jj=234;
					jj++;
				}
				
				if (success) {
					CustomToast.show(a, R.string.operation_succeeded); 
				} else {
					CustomToast.show(a, R.string.cannot_perform_op);
				}
			}
		});
	}


	private File downloadToSdcard(final String imageUrl) {
		String albumDirPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString()
				+ File.separator + "GanBook Images";
		File albumDir = new File(albumDirPath);
		albumDir.mkdirs();
		
		File cachedImage = ImageLoader.getInstance().getDiscCache().get(imageUrl);

		String fileName = UrlUtils.urlToName(imageUrl);
		if (fileName==null) {
			fileName = System.currentTimeMillis() + ".jpeg";
		}
		OutputStream targetStream = null;
		InputStream sourceStream = null;
		final String targetFilePath = albumDirPath + "/" + fileName;		
		
		File existingFile = new File(targetFilePath);
		if (existingFile.exists() && existingFile.length() > 400) {
			return existingFile; // use existing file!
		}
		 
		try {			
			if (cachedImage != null && cachedImage.exists()) { // if image was cached by UIL
				sourceStream = new FileInputStream(cachedImage);
			} else { // otherwise - download image
				ImageDownloader downloader = new BaseImageDownloader(this);
				sourceStream = downloader.getStream(imageUrl, null);
				if (sourceStream == null) {
					return null;
				}
			}
 
//			http://s3.ganbook.co.il/ImageStore/1589/2073/14664/158920731466469679500.jpeg
//			String filePath = albumDirPath + "/" + fileName;
			targetStream = new FileOutputStream(targetFilePath);
			IoUtils.copyStream(sourceStream, targetStream);
			File f = new File(targetFilePath);
			return f;
			
		} 
		catch (Exception e) {
			int jj=234;
			jj++;
		} 
		finally {
			if (targetStream != null) {
				try { targetStream.close(); } catch (Exception e) {}
			}
			if (sourceStream != null) {
				try { sourceStream.close(); } catch (Exception e) {}
			}
			
			new MyMediaScanner(targetFilePath); // gggFix force scan operation on new file
		}
		return null;
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

	
	

	static class ZoomActivityAdapter extends PagerAdapter {
		private final DisplayImageOptions defaultOptions = UILManager.createDefaultDisplayOpyions(R.drawable.empty_image);

		@Override
		public int getCount() {
//			return images.length;
			return fullSizeImageUrls.size();
		}

		@Override
		public View instantiateItem(ViewGroup container, int position) {
			TouchImageView img = new TouchImageView(container.getContext());
			String url = fullSizeImageUrls.get(position).img_url;
			File file = fullSizeImageUrls.get(position).img_file;
			if (url != null) {
				UILManager.imageLoader.displayImage(url, img, defaultOptions);
			} else if (file != null) {
				String f_uri =  Uri.fromFile(file).toString();
//				String decodedUri = Uri.decode(uri);
				UILManager.imageLoader.displayImage(f_uri, img, defaultOptions);
			} else {
				// no op
			}
			container.addView(img, LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.MATCH_PARENT);
			return img; 
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
	

}


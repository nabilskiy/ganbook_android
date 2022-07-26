package com.luminous.pick;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import androidx.fragment.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.ganbook.activities.TrimActivity;
import com.ganbook.amazinglist.special.CustomToast;
import com.ganbook.fragments.PreviewSelectedPicturesFragment;
import com.ganbook.models.MediaFile;
import com.ganbook.universalloader.UILManager;
import com.ganbook.user.User;
import com.ganbook.utils.Const;
import com.ganbook.utils.FragmentUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.PauseOnScrollListener;
import com.project.ganim.R;


import java.util.ArrayList;
import java.util.Collections;

public class CustomGalleryActivity extends FragmentActivity {

	// gallery
	private static final int IMAGE = 0;
	private static final int VIDEO = 2;
	private static final String TAG = CustomGalleryActivity.class.getName();

	private GridView gridGallery;
	private Handler handler;
	private GalleryAdapter the_adapter;

	private ImageView imgNoMedia;
	private Button btnUpload;
	
	public static Activity inst;
	
	public TextView tvTitleText;
	
	public static int chosen = 0;
	private String max;
	private int type;
	private String albumName;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.gallery);
		
		inst = this;

		Intent intent = getIntent();
		type = intent.getIntExtra("type",IMAGE);
		albumName = intent.getStringExtra("albumName");

//		action = getIntent().getAction();
//		if (action == null) {
//			finish();
//		}
//		initImageLoader();
				
		init();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (this == inst) inst = null;
	}

//	private void initImageLoader() { 
//		try {
//			String CACHE_DIR = Environment.getExternalStorageDirectory()
//					.getAbsolutePath() + "/.temp_tmp";
//			new File(CACHE_DIR).mkdirs();
//
//			File cacheDir = StorageUtils.getOwnCacheDirectory(getBaseContext(),
//					CACHE_DIR);
//
//			DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
//					.cacheOnDisc(true).imageScaleType(ImageScaleType.EXACTLY)
//					.bitmapConfig(Bitmap.Config.RGB_565).build();
//			ImageLoaderConfiguration.Builder builder = new ImageLoaderConfiguration.Builder(
//					getBaseContext())
//					.defaultDisplayImageOptions(defaultOptions)
//					.discCache(new UnlimitedDiscCache(cacheDir))
//					.memoryCache(new WeakMemoryCache());
//
//			ImageLoaderConfiguration config = builder.build();
//			g_imageLoader = ImageLoader.getInstance();
//			g_imageLoader.init(config);
//
//		} catch (Exception e) {
//			int jj=3453;
//			jj++;
//		}
//	}

	public static void updateTitle(boolean selected)
	{
		if(selected)
		{
			chosen++;
		}
		else
		{
			chosen--;
		}
		if (inst != null) {
			((CustomGalleryActivity) inst).updateTitle();
		}
	}
	
	private void updateTitle()
	{
		tvTitleText.setText(chosen + "/" + max);
	}
	
	private void init() {
		
		final int emptyImgId = R.drawable.no_media;
		DisplayImageOptions defaultOptions = UILManager.createDefaultDisplayOpyions(emptyImgId);

		chosen = 0;
		handler = new Handler();
		gridGallery = (GridView) findViewById(R.id.gridGallery);
		gridGallery.setFastScrollEnabled(true);
		the_adapter = new GalleryAdapter(getApplicationContext(), defaultOptions);//, MyApp.g_imageLoader);
		PauseOnScrollListener _listener = new PauseOnScrollListener(UILManager.imageLoader, true, true);
		gridGallery.setOnScrollListener(_listener);

//		if (action.equalsIgnoreCase(Action.ACTION_MULTIPLE_PICK)) {
			findViewById(R.id.llBottomContainer).setVisibility(View.VISIBLE);
			gridGallery.setOnItemClickListener(mItemMulClickListener);
			the_adapter.setMultiplePick(true);
			the_adapter.setImageMode(type == IMAGE);
//		} else if (action.equalsIgnoreCase(Action.ACTION_PICK)) {
//
//			findViewById(R.id.llBottomContainer).setVisibility(View.GONE);
//			gridGallery.setOnItemClickListener(mItemSingleClickListener);
//			adapter.setMultiplePick(false);
//
//		}

		gridGallery.setAdapter(the_adapter);
		imgNoMedia = (ImageView) findViewById(R.id.imgNoMedia);

		btnUpload = (Button) findViewById(R.id.btnUpload);
		btnUpload.setOnClickListener(mOkClickListener);

		if(type == IMAGE) {
			max = User.current.max_images_batch_upload_android;
		} else {
			max = "1";
			btnUpload.setText(getString(R.string.btn_upload_video));
		}

		the_adapter.setMax(max);
		
		tvTitleText = (TextView) findViewById(R.id.tvTitleText);
		tvTitleText.setText(chosen + "/" + max);

		Thread background = new Thread(new Runnable() {

			// After call for background.start this run method call
			public void run() {

				ArrayList<SingleImage> items = new ArrayList<>();

				String[] projection = new String[] {
						MediaStore.Video.Media.DATA,
						MediaStore.Video.Media._ID,
						MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
						MediaStore.Video.Media.DURATION
				};

				// content:// style URI for the "primary" external storage volume
				Uri images = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;

				// Make the query.
				Cursor cur = getContentResolver().query(
						images,
						projection,
						null,
						null,
						MediaStore.Video.Media.DATE_TAKEN + " DESC");

				if (cur != null && cur.moveToFirst()) {

					Log.i("ListingImages"," query count=" + cur.getCount());

					String bucket;
					String name;
					String filePath;
					String duration;

					int durationColumn = cur.getColumnIndex(MediaStore.Video.Media.DURATION);

					int bucketColumn = cur.getColumnIndex(
							MediaStore.Video.Media.BUCKET_DISPLAY_NAME);

					int nameColumn = cur.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME);

					int pathColumn = cur.getColumnIndex(MediaStore.Video.Media.DATA);

					do {
						// Get the field values
						bucket = cur.getString(bucketColumn);
						filePath = cur.getString(pathColumn);
						duration = cur.getString(durationColumn);
						// Do something with the values.
						Log.i(TAG, " sharoni bucket=" + bucket
								+ " duration = " + duration
								+ " path = " + filePath);

						SingleImage singleImage = new SingleImage();

						singleImage.id = cur.getString(1);
						singleImage.sdcardPath = filePath;
						singleImage.duration = duration;
						singleImage.uri = filePath;

						Log.i(TAG, "sharoni run: image = " + singleImage);

						items.add(singleImage);

					} while (cur.moveToNext());

					Collections.reverse(items);

					threadMsg(items);
					cur.close();
				}

			}

			private void threadMsg(ArrayList<SingleImage> items) {

				if (items!=null && items.size() > 0) {
					Message msgObj = handler.obtainMessage();
					Bundle b = new Bundle();
					b.putParcelableArrayList("pics", items);
					msgObj.setData(b);
					handler.sendMessage(msgObj);
				}
			}

			// Define the Handler that receives messages from the thread and update the progress
			private final Handler handler = new Handler() {

				public void handleMessage(Message msg) {

					ArrayList<SingleImage> pics = msg.getData().getParcelableArrayList("pics");

					if (pics != null && pics.size() > 0) {

						//add processed pics/folders to grid

						the_adapter.addAll(pics);
						checkImageStatus();
					} else {

						//error

					}

				}
			};

		});

		// Start Thread
		background.start();  //After call start method thread called run Method
	}

	private void checkImageStatus() {
		if (the_adapter.isEmpty()) {
			imgNoMedia.setVisibility(View.VISIBLE);
		} else {
			imgNoMedia.setVisibility(View.GONE);
		}
	}
 
	View.OnClickListener mOkClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {

			final Activity a = CustomGalleryActivity.this;

			final ArrayList<SingleImage> selected = the_adapter.getSelected();

			final int num_selected = selected.size();

			if (num_selected == 0) {
				CustomToast.show(a, R.string.no_images_selected);
				return;
			}

			if(type == IMAGE)
			{
				AlertDialog.Builder adb = new AlertDialog.Builder(a);
				adb.setTitle(R.string.upload_photo_text);

				adb.setIcon(R.drawable.ic_launcher);
				adb.setPositiveButton(R.string.popup_btn_ok, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {

						ArrayList<String> filePathArr = new ArrayList<String>();
						ArrayList<MediaFile> mediaFiles = new ArrayList<MediaFile>();

						for (int i = 0; i < num_selected; i++) {

							filePathArr.add(selected.get(i).sdcardPath);
							mediaFiles.add(new MediaFile(selected.get(i).sdcardPath));
						}

						if (type == IMAGE) {

							PreviewSelectedPicturesFragment previewSelectedPicturesFragment =
									PreviewSelectedPicturesFragment.newInstance(albumName, mediaFiles);

							FragmentUtils.openFragment(previewSelectedPicturesFragment, R.id.layout_root,
									PreviewSelectedPicturesFragment.TAG, CustomGalleryActivity.this, true);
						}

						if (type == VIDEO) {

							CustomToast.show(CustomGalleryActivity.this, R.string.upload_in_progress);

							Intent data = new Intent();

							data.putParcelableArrayListExtra(Const.FILE_ARRAY, mediaFiles);

							setResult(Activity.RESULT_OK, data);
							finish();
						}
					}
				});

				adb.setNegativeButton(R.string.popup_btn_cancel, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// no op
					} });

				adb.show();

			}
			else {
//				Uri uri = Uri.withAppendedPath(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, selected.get(0).id);
//				String path = getRealPathFromURI(CustomGalleryActivity.this, uri);

				Intent intent = new Intent(CustomGalleryActivity.this, TrimActivity.class);
				intent.setData(Uri.parse(selected.get(0).uri));
				intent.putExtra(Const.VIDEO_ID, selected.get(0).id);
				startActivity(intent);

				a.finish();
			}

		}
	};

	public String getRealPathFromURI(Context context, Uri contentUri) {
		Cursor cursor = null;
		try {
			String[] proj = { MediaStore.Video.Media.DATA };
			cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
			int column_index = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
			cursor.moveToFirst();
			return cursor.getString(column_index);
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}


	AdapterView.OnItemClickListener mItemMulClickListener = new AdapterView.OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> l, View v, int position, long id) {
			the_adapter.changeSelection(v, position);

		}
	};

	AdapterView.OnItemClickListener mItemSingleClickListener = new AdapterView.OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> l, View v, int position, long id) {
			SingleImage item = the_adapter.getItem(position);
			Intent data = new Intent().putExtra("single_path", item.sdcardPath);
			setResult(RESULT_OK, data);
			finish();
		}
	};
	
	private ArrayList<SingleImage> getGalleryPhotos() {
		ArrayList<SingleImage> galleryList = new ArrayList<SingleImage>();
		try {
			String[] columns = null;
			String orderBy = null;
			Uri uri = null;
			String selection = MediaStore.Video.Media.BUCKET_ID + " = ?";
			String[] selectionArgs = new String[]{String.valueOf(Environment.getExternalStorageDirectory().toString()
					+ "/DCIM/Camera".toLowerCase().hashCode())};


			if(type == IMAGE)
			{
				uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				columns = new String[]{MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID, MediaStore.Images.Media.BUCKET_DISPLAY_NAME};
				orderBy = MediaStore.Images.Media._ID;
			}
			else
			{
				uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
				columns = new String[]{MediaStore.Video.Media.DATA, MediaStore.Video.Media._ID, MediaStore.Video.Media.BUCKET_DISPLAY_NAME, MediaStore.Video.Media.DURATION};
				orderBy = MediaStore.Video.Media._ID;
			}

//			final String orderBy = MediaStore.Images.Media.DATE_TAKEN; 

			Cursor imagecursor = getContentResolver().query(uri , columns, null, null, orderBy);
//			managedQuery(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null, orderBy);

			if (imagecursor != null && imagecursor.getCount() > 0) {
				while (imagecursor.moveToNext()) {
					SingleImage item = new SingleImage();
//					int dataColumnIndex = imagecursor
//							.getColumnIndex(MediaStore.Video.Thumbnails.DATA);
					item.sdcardPath = imagecursor.getString(0);
					item.id = imagecursor.getString(1);
					String bucket = imagecursor.getString(2);


					if (type != IMAGE) {
						if ("Camera".equals(bucket)) {
							item.duration = imagecursor.getString(3);
							item.uri = imagecursor.getString(0);
//
//							String[] thumbColumns = {MediaStore.Video.Thumbnails.DATA};
//							String[] mediaColumns = {MediaStore.Video.Media._ID};
//
//							Cursor thumbCursor = getContentResolver().query(MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI, thumbColumns
//									, MediaStore.Video.Thumbnails.VIDEO_ID + " = " + item.id, null, null);
//
//							if (thumbCursor.moveToFirst()) {
//								String thumbPath = thumbCursor.getString(thumbCursor
//										.getColumnIndex(MediaStore.Video.Thumbnails.DATA));
//
//								item.sdcardPath = thumbPath;
//
//								Log.d(TAG, "getGalleryPhotos: thumbPath = " + thumbPath);
//							}
                            galleryList.add(item);
						}
					}
                    else {
                        galleryList.add(item);
                    }
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		// show newest photo at beginning of the list
		Collections.reverse(galleryList);
		return galleryList;
	}
}

package com.ganbook.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import androidx.annotation.Nullable;
import android.text.InputType;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.ganbook.activities.CommentsActivity;
import com.ganbook.activities.MainActivity;
import com.ganbook.amazinglist.special.CustomToast;
import com.ganbook.app.MyApp;
import com.ganbook.communication.ICompletionDownloadHandler;
import com.ganbook.communication.ICompletionHandler;
import com.ganbook.communication.datamodel.ResultObj;
import com.ganbook.communication.json.BaseResponse;
import com.ganbook.communication.json.HistoryDetails;
import com.ganbook.communication.json.getalbum_response;
import com.ganbook.communication.json.getpicture_Response;
import com.ganbook.communication.json.transmitter.JsonTransmitter;
import com.ganbook.communication.upload.UploadService;
import com.ganbook.communication.upload.state.UploadStateRepository;
import com.ganbook.communication.upload.state.UploadStatus;
import com.ganbook.datamodel.SingleImageObject;
import com.ganbook.models.AlbumsAnswer;
import com.ganbook.ui.GridViewWithHeaderAndFooter;
import com.ganbook.universalloader.PicassoManager;
import com.ganbook.user.User;
import com.ganbook.utils.CurrentYear;
import com.ganbook.utils.FfmpegUtils;
import com.ganbook.utils.FileDescriptor;
import com.ganbook.utils.ImageUtils;
import com.ganbook.utils.NetworkUtils;
import com.ganbook.utils.StrUtils;

import com.luminous.pick.CustomGalleryActivity;
import com.project.ganim.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

//import com.ganbook.s3_transfer_manager.network.TransferController;

public class SingleAlbumFragment extends BaseFragment /*implements OnClickListener*/ {

	// gallery_album_layout
	private static final String TMB = JsonTransmitter.TMB;
	// showing MyApp.selAlbum

	private static final int ALBUM_NAME_FLD_ID = 1433;
	private static final int OPEN_LIB_IND = 0;
	private static final int OPEN_CAM_IND = 1; 
	private static final int OPEN_VIDEO_IND = 3;
	private static final int CLOSE_MENU_IND = 4;
	private static final int OPEN_VIDEO_GALLERY_IND = 2;
	private static final String ARG_CLASS_ID = "classid";
	private static final String ARG_GAN_ID = "ganid";
	private static final String ARG_ALBUM_ID = "albumid";
	private static final String ARG_ALBUM = "album";
	private static final String TAG = SingleAlbumFragment.class.getName();


	public static SingleAlbumFragment fragment_inst;
	
	private Context context;
	private View v;
	private GridViewWithHeaderAndFooter gallery_gridview;
	private TextView textview;
	private ArrayList<String> images;
	private ImageView camara_btn;
	private ImageView comments_image;
	private ImageView try_again_btn;
	private ImageView download_btn;
	private PopupWindow _window;
	private ViewSwitcher switcher;
	private TextView num_likes,num_comments;

	private static final int CAMERA_APP = 34; 
	private static final int GALLERY_APP = 3;
    private static final int CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE = 200;
	
	private static final int MENU_0 = 0;
	private static final int MENU_1 = 1;
	private static final int MENU_2 = 2;

	private SingleAlbumAdapter adapter;
	

	private static int debug_counter;

	private static boolean inSelectMode = false;	
	private static boolean inUploadMode = false; //gggFix
	private static volatile boolean refreshNeeded = false;
	
	private String gan_id;
	private String class_id;
	private String album_id;

	AlbumsAnswer albumItem;

	private ArrayList<getpicture_Response> albumPictureArr;
	final static MediaPlayer mp = MediaPlayer.create(MyApp.context, R.raw.like);

	private ProgressBar progressBar;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		context = activity;
	}
	
	@Override
	public void onStop() {

		super.onStop();
	}

	public static SingleAlbumFragment newInstance(String class_id, String gan_id, AlbumsAnswer album) {

		SingleAlbumFragment singleAlbumFragment = new SingleAlbumFragment();

		Bundle args = new Bundle();

		args.putString(ARG_CLASS_ID, class_id);
		args.putString(ARG_GAN_ID, gan_id);
		args.putString(ARG_ALBUM_ID, album.getAlbumId());
		args.putParcelable(ARG_ALBUM, album);

		singleAlbumFragment.setArguments(args);

		return singleAlbumFragment;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setHasOptionsMenu(true);

		if(CurrentYear.get().equals(MyApp.selAlbum.year) || User.isTeacher())
		{
			gan_id = User.current.getCurrentGanId(); 
			class_id = User.current.getCurrentClassId();
		}
		else
		{
			HistoryDetails details = User.current.getCurrentKidHistoryByYear(MyApp.selAlbum.year);
			gan_id = details.gan_id; 
			class_id = details.class_id;
		}
		
		album_id = MyApp.selAlbum.album_id;
						
		fragment_inst = this;

		if (getArguments() != null) {

			class_id = getArguments().getString(ARG_CLASS_ID);
			gan_id = getArguments().getString(ARG_GAN_ID);
			album_id = getArguments().getString(ARG_ALBUM_ID);
			albumItem = getArguments().getParcelable(ARG_ALBUM);
		}
//		defaultOptions = UILManager.createDefaultDisplayOpyions(R.drawable.empty_image);
	}
	

	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		MyApp.sendAnalytics("album-gan-ui", "album-gan"+User.current.getCurrentGanId()+"-class"+User.current.getCurrentClassId()+"-album"+album_id+"-ui"+ User.getId(), "album-gan-ui", "AlbumGan");


		v = inflater.inflate(R.layout.gallery_album_layout, null);
		gallery_gridview = (GridViewWithHeaderAndFooter) v.findViewById(R.id.photos);
		
		Log.i("noanoa","onCreateView " + gallery_gridview);
		
		View footerView = inflater.inflate(R.layout.grid_footer_view, null);
		textview = (TextView) footerView.findViewById(R.id.tv_count);
		
		gallery_gridview.addFooterView(footerView);
		
//		textview = (TextView) v.findViewById(R.id.tv_count);
		
		base_onCreateView();

		save_btn = (Button) activity().findViewById(R.id.select); // in actionbar_mainscreen.xml
		save_btn.setText(R.string.select_btn);
		setSaveClickHandler();

		inSelectMode = false;
		setSelectText();

		switcher =(ViewSwitcher) v.findViewById(R.id.switcher);
		num_likes = (TextView) v.findViewById(R.id.num_likes);
		num_comments = (TextView) v.findViewById(R.id.num_comments);
		camara_btn = (ImageView) v.findViewById(R.id.camera_btn);
		try_again_btn = (ImageView) v.findViewById(R.id.try_again_btn);
		comments_image = (ImageView) v.findViewById(R.id.comments_image);
		download_btn = (ImageView) v.findViewById(R.id.download_btn);
		progressBar = (ProgressBar) v.findViewById(R.id.progressBar);

		switcher.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {


				String likes = num_likes.getText().toString();
				Integer likes_num = Integer.valueOf(likes);

				if(switcher.getDisplayedChild() == 0) //active
				{
					switcher.showNext();
					likes_num--;
					AlbumListFragment.updateAlbumLikes(album_id,false);
				}
				else
				{
					mp.start();
					switcher.showPrevious();
					likes_num++;
					AlbumListFragment.updateAlbumLikes(album_id,true);
				}

				num_likes.setText(likes_num.toString());

				JsonTransmitter.send_updatealbumlike(album_id);
			}
		});

		if(User.isParent())
		{
			hideCameraButton();
			try_again_btn.setVisibility(View.GONE);
			download_btn.setVisibility(View.GONE);
			/*download_btn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (!NetworkUtils.isConnected()) {
						showNotInternetAlert();
					}
					else
					{
						download_all_album();
					}
				}
			});*/
		}
		else
		{
			download_btn.setVisibility(View.GONE);
		}

		camara_btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!NetworkUtils.isConnected()) {
					showNotInternetAlert();
				}
				else
				{
					onLowerPanelBtnClicked();
				}
			}
		});
		
		try_again_btn.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				if (!NetworkUtils.isConnected()) {
					showNotInternetAlert();
				}
				else
				{
					UploadStateRepository.markFailedAsPending();
					UploadService.debugUpload(null, "try again button");
					UploadService.completePendingUploads(album_id);	
				}
			}
		});

		comments_image.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!NetworkUtils.isConnected()) {
					showNotInternetAlert();
				}
				else
				{
					Intent i = new Intent(MyApp.context, CommentsActivity.class);

					i.putExtra("album_id",album_id);

					startActivity(i);
				}
			}
		});
		
		populateSingleAlbumImages();
						
		setActionBarValues();Log.i("noanoa","single album handleFragmentVisible ");
		handleFragmentVisible(); // must be called explicitly since not part of pager!
		initBottomPanel();

		return v; 
	}

	private void initBottomPanel() {
		if(MyApp.selAlbum.user_album_like)
		{
			switcher.setDisplayedChild(0);
		}
		else
		{
			switcher.setDisplayedChild(1);
		}

		num_likes.setText(MyApp.selAlbum.album_likes);
		num_comments.setText(MyApp.selAlbum.album_comments);
	}

	@Override
	public void setMenuVisibility(final boolean visible) {
		Log.i("noanoa","setMenuVisibility single album");
		super.setMenuVisibility(visible);
		if (visible) {
			String name = this.getClass().getSimpleName();
			handleFragmentVisible();
			int jj=24;
			jj++; 
		}
	}	
	
	private void setActionBarValues() {
		titleTextId = -1;
		titleTextText = albumItem.getAlbumName();
//		saveBtnVisibility = View.VISIBLE;
		saveBtnVisibility = (User.isTeacher() ? View.GONE : View.GONE);
		saveBtnTextId = R.string.edit_btn;
		upNavigationVisibility = View.VISIBLE;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.clear();
		inflater.inflate(R.menu.single_album_menu, menu);

		menu.findItem(R.id.edit_menu).getActionView().setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
//				toggleSelectState();
				if(inSelectMode)
				{
					toggleSelectState();
				}
				else
				{
					openSingleAlbumMenu();
				}

			}
		});

		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public void onBecomingVisible() {
		super.onBecomingVisible();
		setSaveClickHandler();
	}

	private void download_all_album()
	{
		AlertDialog.Builder adb = new AlertDialog.Builder(activity());
		//		adb.setView(alertDialogView);
		adb.setTitle(R.string.download_all_album);
		adb.setIcon(R.drawable.ic_launcher);
		adb.setPositiveButton(R.string.popup_btn_ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {

				MyApp.sendAnalytics("download-album-ui", "download-album"+"-ui"+ User.getId(), "download-album-ui", "DownloadAlbum");


				ArrayList<FileDescriptor> all_images = getFullSizeImageArr();
				new DownloadTask(all_images, new ICompletionDownloadHandler() {
					@Override
					public void onComplete(File file) {
					}
				}).execute();
			} });
		adb.setNegativeButton(R.string.popup_btn_cancel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// no op
			} });

		adb.show();
	}

	private class DownloadTask extends AsyncTask<String,Integer,Void>
	{
		ICompletionDownloadHandler handler;
		ArrayList<FileDescriptor> all_images;
		ProgressDialog pDialog;

		public DownloadTask(ArrayList<FileDescriptor> all_images, ICompletionDownloadHandler handler)
		{
			this.all_images = all_images;
			this.handler = handler;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			pDialog = new ProgressDialog(activity());
			pDialog.setMessage(activity().getString(R.string.downloading_file_please_wait));
			pDialog.setIndeterminate(false);
			pDialog.setProgress(0);
			pDialog.setMax(all_images.size());
			pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected Void doInBackground(String... params) {
			int ind=1;
			for(FileDescriptor fileDescriptor : all_images)
			{
				publishProgress(ind);
				ImageUtils.downloadToSdcard(fileDescriptor.img_url);
				ind++;
			}
			return null;
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
			pDialog.setProgress(values[0]);
		}

		@Override
		protected void onPostExecute(Void param) {
			super.onPostExecute(param);
			pDialog.dismiss();
			handler.onComplete(null);
			if(activity() != null) {
				CustomToast.show(activity(), activity().getString(R.string.operation_succeeded));
			}
		}
	}

	private void setSaveClickHandler() {
		save_btn.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
//				toggleSelectState();
				if(inSelectMode)
				{
					toggleSelectState();
				}
				else
				{
					openSingleAlbumMenu();
				}
				
			}
		});
	}
	
	private boolean isEmpty = false;

	protected void openSingleAlbumMenu() {
//		if (albumPictureArr==null || albumPictureArr.isEmpty()) {
//			CustomToast.show(activity(), R.string.not_if_empty);  
//			return;
//		}


		ArrayList<getpicture_Response> imagesArr = (ArrayList<getpicture_Response>) albumPictureArr.clone();
		imagesArr = UploadStateRepository.expandWithUploads(album_id, imagesArr);

		isEmpty = (imagesArr==null || imagesArr.isEmpty());
		String[] items;
		if (isEmpty) { 
			items = new String[2];
			items[MENU_0] = context.getResources().getString(R.string.rename_opt);
			items[MENU_1] = context.getResources().getString(R.string.delete_opt);
		} else {
			items = new String[3];
			items[MENU_0] = context.getResources().getString(R.string.select_opt);
			items[MENU_1] = context.getResources().getString(R.string.delete_opt);
			items[MENU_2] = context.getResources().getString(R.string.rename_opt);
		}
	 
		AlertDialog.Builder builder = new AlertDialog.Builder(activity());
		builder.setTitle("");
		builder.setItems(items, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int index) {
				if (isEmpty) {
					if (index == MENU_0) {
						doRename();
					} else {
						doDelete();
					}
				} else {
					if (index == MENU_0) {
						doSelect();
					} else if (index == MENU_1){
						doDelete();
					} else {
						doRename();
					}
					
				}
			}
		});
		AlertDialog alert = builder.create();
		alert.show();	
		
	}

	private void doDelete() {
		AlertDialog.Builder adb = new AlertDialog.Builder(activity());
		//		adb.setView(alertDialogView);
		adb.setTitle(R.string.delete_album_text);
		adb.setIcon(R.drawable.ic_launcher);
		adb.setPositiveButton(R.string.popup_btn_ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {

				performDelete();
			}
		});
		adb.setNegativeButton(R.string.popup_btn_cancel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// no op
			}
		});

		adb.show();
	}

	private void performDelete()
	{
		startProgress(R.string.operation_proceeding);
		JsonTransmitter.send_deletealbumj(album_id, new ICompletionHandler() {
			@Override
			public void onComplete(ResultObj result) {
				stopProgress();
				if (!result.succeeded) {
					if(JsonTransmitter.NO_NETWORK_MODE.equals(result.result))
					{
						showNotInternetAlert();
						return;
					}
					String errmsg = result.result;
					CustomToast.show(activity(), errmsg);
				}
				else {
					User.current.deleteFromAlbumList(MyApp.selAlbum.year,album_id);
					AlbumListFragment.contentRefresh();
					activity().onBackPressed();
				}
			}
		});
	}

	protected void doRename() {

		int jj=234;
		jj++;
		AlertDialog.Builder builder = new AlertDialog.Builder(activity());
		builder.setTitle(R.string.rename_opt);
		builder.setMessage(R.string.rename_album_txt); 
		final EditText input = new EditText(activity());
		input.setId(ALBUM_NAME_FLD_ID);
		input.setInputType(InputType.TYPE_TEXT_FLAG_AUTO_CORRECT);
		input.setText(titleTextText);
		builder.setView(input); 
		builder.setPositiveButton(R.string.send_btn, new DialogInterface.OnClickListener() { 
			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
				String album_name = input.getText().toString().trim();
				
				JsonTransmitter.send_editalbum(album_id,album_name);
				
				titleTextText = album_name; 
				
				handleFragmentVisible();
//				AlbumListFragment.setToRefresh();
				AlbumListFragment.updateAlbumName(album_id,album_name);
				
				return;
			}
		}); 
		builder.setNegativeButton(R.string.cancel_btn, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// no op
			}
		});

		builder.show();		
		 
	} 

	protected void doSelect() {

		int jj=234;
		jj++;
		toggleSelectState();
	}

	private void toggleSelectState() {
		inSelectMode = !inSelectMode;
		setLowerPanelButton();
		setSelectText();
		refreshList(); 
	}
	
	private void setLowerPanelButton() {
		if (inSelectMode) {
			camara_btn.setImageDrawable(getResources().getDrawable(R.drawable.lower_panel_delete));
		} else { 
			camara_btn.setImageDrawable(getResources().getDrawable(R.drawable.lower_panel_camara));
		}
	}
	
	
	private void onLowerPanelBtnClicked() {
		if (inSelectMode) {
			deleteSelectedImages();
		} else {
			openContextMenu();
		}
	}


	private void deleteSelectedImages() {
		String nameList = "";
		int num_sel = 0;

		final ArrayList<getpicture_Response> imagesArr = adapter.getImagesArr();
//		imagesArr = UploadStateRepository.expandWithUploads(album_id, imagesArr);

		for (getpicture_Response img: imagesArr) {
			if (img.isChecked) {
				num_sel++;
				if (nameList.isEmpty()) {
					if(img.picture_name != null) {
						nameList = img.picture_name;
					}
				} else {
					if(img.picture_name != null) {
						nameList += ("," + img.picture_name);
					}
				}
			}  
		}
		
		if (num_sel==0) {
			CustomToast.show(activity(), R.string.no_images_sel);
			return;
		}

		if(nameList.isEmpty())
		{
			refreshAfterDelete(imagesArr);
			return;
		}
		
		startProgress(R.string.operation_proceeding);
//		final String album_id = selAlbum.album_id; 
		JsonTransmitter.send_deletepicturesj(nameList, 
				new ICompletionHandler() {
			@Override
			public void onComplete(ResultObj result) {
				stopProgress();
				if (!result.succeeded) {
					if(JsonTransmitter.NO_NETWORK_MODE.equals(result.result))
					{
						showNotInternetAlert();
						return;
					}
					String errmsg = result.result;
					CustomToast.show(activity(), errmsg);
				}
				else {
					CustomToast.show(activity(), R.string.operation_succeeded);
					refreshAfterDelete(imagesArr);
				}
								
			}
		});		
	}

	private void refreshAfterDelete(ArrayList<getpicture_Response> imagesArr)
	{
		ArrayList<getpicture_Response> afterDelete = new ArrayList<getpicture_Response>();
		for (getpicture_Response img: imagesArr) {
			if (!img.isChecked) {
				afterDelete.add(img);
			}
			else
			{
				UploadStateRepository.updateAlbumUploads(album_id,img.uploadImgFilePath);
			}
		}
		albumPictureArr = afterDelete;

//					textview.setText("Total: " + albumPictureArr.size());
		updateNumPictsInAlbum(albumPictureArr.size());
		setAlbumSize();
		toggleSelectState();
	}


	private void setSelectText() {
		save_btn.setText(inSelectMode ? R.string.btn_cancel_select : R.string.select_btn);
	}

	protected void openContextMenu() {
		final String[] items = new String[5];
		items[OPEN_LIB_IND] = context.getResources().getString(R.string.existing_library);
		items[OPEN_CAM_IND] = context.getResources().getString(R.string.take_new_pict);
		items[OPEN_VIDEO_GALLERY_IND] = context.getResources().getString(R.string.existing_video_library);
		items[OPEN_VIDEO_IND] = context.getResources().getString(R.string.take_new_vid);
		items[CLOSE_MENU_IND] = context.getResources().getString(R.string.cancel);

	
		AlertDialog.Builder builder = new AlertDialog.Builder(activity());
		builder.setTitle("");
		builder.setItems(items, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int index) {
				switch (index) {
				case OPEN_LIB_IND:
					openGalleryApp(OPEN_LIB_IND);
					break;
				case OPEN_CAM_IND:
					openCameraApp();
					break;
				case OPEN_VIDEO_GALLERY_IND:
					openGalleryApp(OPEN_VIDEO_GALLERY_IND);
					break;
				case OPEN_VIDEO_IND:
					openVideoApp();
					break;
				case CLOSE_MENU_IND:
					break;
				default:
					// ??
				}
			}
		});
		AlertDialog alert = builder.create();
		alert.show();	
	}

 
	@Override
	public void onResume() {
		super.onResume();
		getView().setFocusableInTouchMode(true);
		getView().setOnKeyListener( new OnKeyListener() {
		    @Override
		    public boolean onKey( View v, int keyCode, KeyEvent event) {
		        if (keyCode == KeyEvent.KEYCODE_BACK) {
		        	if (inSelectMode) { 
		        		toggleSelectState(); 
		        		return true;
		        	} 
		        }
		        return false;
		    }
		});		
	}
	
	
	private void populateSingleAlbumImages() {
		if (MyApp.selAlbum == null) {
			return;
		}
//		startProgress(R.string.operation_proceeding);

		if(progressBar != null)
		{
			progressBar.setVisibility(View.VISIBLE);
		}

		if(gallery_gridview != null)
		{
			gallery_gridview.setVisibility(View.GONE);
		}


		JsonTransmitter.send_getpicture(album_id, JsonTransmitter.ACTIVE_PICTURE, 
				new ICompletionHandler() {
			@Override
			public void onComplete(ResultObj result) {
//				stopProgress();

				if(progressBar != null)
				{
					progressBar.setVisibility(View.GONE);
				}
				gallery_gridview.setVisibility(View.VISIBLE);

				albumPictureArr = new ArrayList<getpicture_Response>();
				if (!result.succeeded) {
					if(JsonTransmitter.NO_NETWORK_MODE.equals(result.result))
					{
						showNotInternetAlert();
						
//						gallery_gridview.addHeaderView(tryAgainView);
//						gallery_gridview.setAdapter(new SingleAlbumAdapter(activity(), albumPictureArr));
						return;
					}
					String errmsg = result.result;
					CustomToast.show(activity(), errmsg);
				}
				else {
					ArrayList<BaseResponse> resArr = result.getResponseArray();
					for (BaseResponse br: resArr) {
						albumPictureArr.add((getpicture_Response)br);
					}
					if (albumPictureArr.isEmpty()) {
						CustomToast.show(activity(), R.string.empty_album);
					}
					
					final String year = CurrentYear.get();
					final String num_seen_photos = "" + albumPictureArr.size();
					
//					Log.i("noanoa","single_album"+User.current+"");
//					Log.i("noanoa","single_album"+User.current.getAlbumList()+"");
					
					if (User.isParent())
					{
						JsonTransmitter.send_updateAlbumView(album_id, year, num_seen_photos);
						AlbumListFragment.updateAlbumViewsUnseenPhotos(album_id);
					}
					
					// force parallel exec:
					if (User.isParent() && MyApp.selAlbum.unseen_photos != null && Integer.valueOf(MyApp.selAlbum.unseen_photos) > 0) {  // only!
						MainScreenFragment.updateAlbumTabBadge(num_seen_photos);
						MainActivity.updateDrawerContent();
					}
				}
				
//				textview.setText("Total Count: " + albumPictureArr.size());				
//				setOnClickListener();
				
				updateNumPictsInAlbum(albumPictureArr.size());
				
//				setAlbumSize();
				setOnClickListener();
//				
				UploadStateRepository.onAlbumRefreshed(album_id); //!
				
//				setCameraVisibility();
				
				refreshList();			
			}
		});		
	}

	
	protected void refreshList() {  
		if (activity() != null) {
			setCameraVisibility();

			gallery_gridview.removeHeaderView(tryAgainView);

			adapter = new SingleAlbumAdapter(activity(), albumPictureArr);

			gallery_gridview.setAdapter(adapter);
			setAlbumSize();
		}		 
	}
	
	protected void setCameraVisibility() {
		
		if(User.isTeacher())
		{
			ArrayList<getpicture_Response> imagesArr = (ArrayList<getpicture_Response>) albumPictureArr.clone();
			imagesArr = UploadStateRepository.expandWithUploads(album_id, imagesArr);

			if(imagesArr == null)
			{
				showCameraButton();
				return;
			}
			
			if(imagesArr.isEmpty())
			{
				showCameraButton();
				return;
			}

			for (getpicture_Response pic : imagesArr) {
				if(pic.uploadState == UploadStatus.PENDING)
				{

					hideCameraButton();
					return;
				}
			}

			showCameraButton();

			if(!CurrentYear.get().equals(MyApp.selAlbum.year))
			{
				camara_btn.setVisibility(View.GONE);
			}

		}
	}

	protected void updateNumPictsInAlbum(int new_numPicts) {
		getalbum_response album = User.getAlbumObject(album_id);
		if (album != null) {
			album.new_numPicts = new_numPicts;
		}

		
	}
	
	public static void forced_refreshsinglealbumview() {
		if (fragment_inst == null || fragment_inst.gallery_gridview == null || fragment_inst.albumPictureArr == null) {
			return;
		}
		fragment_inst.refreshList();
	}
	
	protected void setAlbumSize() { //gggFix
		String totalCountPref = activity().getResources().getString(R.string.total_str);
		int _size = albumPictureArr.size() + UploadStateRepository.count_All_Successfully_Uploaded_Images(album_id); 		
		textview.setText(totalCountPref + " " + _size);
	}


	public static void refreshSingleAlbumView(String changed_album_id) {
		if (changed_album_id == null) {
			return;
		}
		if (fragment_inst == null || fragment_inst.gallery_gridview == null || fragment_inst.albumPictureArr == null) {
			return;
		}
		if (changed_album_id.equals(fragment_inst.album_id)) {
			fragment_inst.refreshList();
		}
	}

	
	class SingleAlbumAdapter extends BaseAdapter {
		private LayoutInflater inf;
		private Context cnt;
		private ImageView album_image;
		private int count = 20;
		private ArrayList<getpicture_Response> imagesArr;
		private TextView textview;
		

		public SingleAlbumAdapter(Context cnt, ArrayList<getpicture_Response> orig_imagesArr) {
			this.cnt = cnt;
			inf = LayoutInflater.from(cnt);
			imagesArr = (ArrayList<getpicture_Response>) orig_imagesArr.clone();
			imagesArr = UploadStateRepository.expandWithUploads(album_id, imagesArr);
			boolean upload_failed = false;
			for (getpicture_Response res: imagesArr) {
				res.isChecked = false;
				upload_failed = (res.uploadState == UploadStatus.FAILED);
				if(upload_failed)
				{
					try_again_btn.setVisibility(View.VISIBLE);
					break;
				}
			}
			
			if(!upload_failed)
			{
				try_again_btn.setVisibility(View.GONE);
			}
		}

		public ArrayList<getpicture_Response> getImagesArr()
		{
			return imagesArr;
		}

		@Override
		public int getCount() {
			return imagesArr.size();
		}

		@Override
		public Object getItem(int pos) {
			return imagesArr.get(pos);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}
		
		class ViewHolder {
			ImageView album_image;
			ImageView play;
			TextView duration;
	        CheckBox selection_check;
			ImageView upload_state_image;
	        ProgressBar upload_progress;
		};

		
		@SuppressLint("InflateParams")
		@Override
		public View getView(final int position, View v, ViewGroup arg2) { // population of single album page
			final getpicture_Response current = imagesArr.get(position); 
			ViewHolder vholder;
			if (v == null) {
				v = inf.inflate(R.layout.single_pict_in_gallery, null);				
				vholder = new ViewHolder();
				vholder.album_image = (ImageView) v.findViewById(R.id.iv_image);
				vholder.duration = (TextView) v.findViewById(R.id.duration);
				vholder.play = (ImageView) v.findViewById(R.id.play);
				vholder.selection_check = (CheckBox) v.findViewById(R.id.selection_check);
				vholder.upload_progress = (ProgressBar) v.findViewById(R.id.upload_progress);
				vholder.upload_state_image = (ImageView) v.findViewById(R.id.upload_state_image);
				v.setTag(vholder);
			}
			else { 
				vholder = (ViewHolder) v.getTag();
			}  
			
			final ViewHolder f_vholder = vholder;
			
//			vholder.selection_check.setVisibility(inSelectMode ? View.VISIBLE : View.GONE); //ggg
			
			
//			vholder.selection_check.setOnClickListener(new View.OnClickListener() {				
//				@Override
//				public void onClick(View v) {
//					current.isChecked = f_vholder.selection_check.isChecked();
//				}
//			});
			
			vholder.selection_check.setVisibility(inSelectMode ? View.VISIBLE : View.GONE); //ggg
			
			vholder.selection_check.setOnClickListener(new View.OnClickListener() {				
				@Override
				public void onClick(View v) {
					current.isChecked = f_vholder.selection_check.isChecked();
//					boolean isChecked = f_vholder.selection_check.isSelected(); no!! 
					int jj=234;
					jj++; 
				}
			});
			
			final boolean isUploadPicture = (current.uploadState != null);
			boolean upload_in_progress = (current.uploadState == UploadStatus.PENDING);
			boolean upload_failed = (current.uploadState == UploadStatus.FAILED);
			
			vholder.upload_progress.setVisibility(upload_in_progress ? View.VISIBLE : View.GONE);
			
			vholder.upload_state_image.setVisibility(upload_failed ? View.VISIBLE : View.GONE);

			//hhhhhhhhhhhhhh ggggggggggggggggggggggFix also handle upload error
			
			ImageView view = vholder.album_image;
			String showed_url;

			if (isUploadPicture)  {
//				current.uploadThumbPath = createThumbIfNeeded(current);
				String thumb_path = current.uploadThumbFilePath;
				showed_url = thumb_path;
				if (thumb_path == null) {
					view.setImageResource(R.drawable.empty_image);
				} else {
					File thumbFile = new File(thumb_path);
					PicassoManager.displayImage(activity(), thumbFile, view, R.drawable.empty_image);
				}
			} else {
				String pictureName = current.picture_name; 			
				String url = ImageUtils.getThumbnail(gan_id, class_id, album_id, pictureName);		
				showed_url = url;
				PicassoManager.displayImage(activity(), url, view, R.drawable.empty_image);
			}

			if(showed_url != null && showed_url.contains("VID"))
			{
				if(current.video_duration != null) {
					long duration_long = Long.valueOf(current.video_duration).longValue();
					String duration = String.format("%02d:%02d",
							TimeUnit.SECONDS.toMinutes(duration_long),
							TimeUnit.SECONDS.toSeconds(duration_long));

					vholder.duration.setText(duration);
					vholder.duration.setVisibility(View.VISIBLE);
				}

				vholder.play.setVisibility(View.VISIBLE);
			}
			else
			{
				vholder.play.setVisibility(View.GONE);
				vholder.duration.setVisibility(View.GONE);
			}

			return v;
		} 
	}; // adapter
	
	
	public static ArrayList<String> getAllShownImagesPath(Activity activity) {
		int column_index_data, column_index_folder_name;
		final ArrayList<String> imgList = new ArrayList<String>();
		Uri uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

		String[] projection = { MediaColumns.DATA,
				MediaStore.Images.Media.BUCKET_DISPLAY_NAME };

		Cursor cursor = activity.getContentResolver().query(uri, projection, null, null, null);
		if (cursor == null) {
			return imgList;
		}
		column_index_data = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
		column_index_folder_name = cursor
				.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
		while (cursor.moveToNext()) {
			String absolutePathOfImage = cursor.getString(column_index_data);
			if (imgList.size() <= 10) {
				imgList.add(absolutePathOfImage);
			}
		}
		return imgList;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
//		populateSingleAlbumImages();
		images = getAllShownImagesPath(activity());
		System.out.println("images" + images);
	}
	 
	private void setOnClickListener() {
		
		Log.i("noanoa",gallery_gridview+"");
		
		if(gallery_gridview == null)
			return;
		
		gallery_gridview.setOnItemClickListener(new OnItemClickListener() { 
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				int jj=234;  
				jj++;
				ArrayList<FileDescriptor> all_images = getFullSizeImageArr();
				UploadStateRepository.expandWithUploads_FD(album_id, all_images);
//				ZoomActivity.start(context, position, true, all_images);
//				SingleImageFragment.start(position, true, all_images);
				
				MyApp.singleImageObject = new SingleImageObject(position, true, all_images, albumPictureArr, MyApp.selAlbum.year, class_id, gan_id);
				MyApp.async_writeImageToLocaCache();
				
				MainActivity.fragmentToMoveTo = FragmentType.Single_Image; 
				activity().moveToTab(FragmentType.Single_Image);
			}
		});
	}

	private ArrayList<FileDescriptor> fullSizeImageArr;

	private ArrayList<FileDescriptor> getFullSizeImageArr() {
//		if (fullSizeImageArr == null) {
			int len = albumPictureArr.size();
			fullSizeImageArr = new ArrayList<FileDescriptor>(); 
			for (int i = 0; i < len; i++) {
				getpicture_Response elem = albumPictureArr.get(i);
				String url = null;
				File file = null;
				if (elem.uploadState == null) {
					url = ImageUtils.getFullSizeImage(gan_id, class_id, album_id, elem.picture_name);
				} else if (isLegalFile(elem.uploadThumbFilePath)) {
					// uploading image 
					file = new File(elem.uploadImgFilePath);
				} else {
					// no op
				}
				fullSizeImageArr.add(i, new FileDescriptor(url, file, elem.uploadState));
			}
//		}
		return fullSizeImageArr;
	}
	
	private static boolean isLegalFile(String path) {
		try { 
			return StrUtils.notEmpty(path) && new File(path).exists();
		}
		catch (Exception e) {
			int jj=234;
			jj++;
		}
		return false;
	}



	@Override
	public void onDestroy() {
		super.onDestroy();
//		bitmap = null;
		gallery_gridview = null;
		if (this == fragment_inst) {
			fragment_inst = null;
		}
	}
	

	@Override
	public void onDestroyView() {
		super.onDestroyView();
//		bitmap = null;
		gallery_gridview.setAdapter(null);
		gallery_gridview = null;
	}

	protected void openGalleryApp(int openVideoGalleryInd) {

//		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
//			Intent pickIntent = new Intent(Intent.ACTION_PICK,
//					android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI );
//			pickIntent.setType("video/*");
////			pickIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
//			startActivityForResult(pickIntent, GALLERY_APP);
//		} else {
			Intent i = new Intent(MyApp.context, CustomGalleryActivity.class);
			i.putExtra("type",openVideoGalleryInd);
			startActivityForResult(i, GALLERY_APP);
//		}

	}

	public static final int MEDIA_TYPE_IMAGE = 1;
	public static final int MEDIA_TYPE_VIDEO = 2;
	
	
	private static Uri current_photoUri;
	
	protected void openCameraApp() {
		// http://developer.android.com/training/camera/photobasics.html
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//		File photo = new File(Environment.getExternalStorageDirectory(),  "Pic.jpg");
		File _file = new File(FfmpegUtils.getOutputMediaFilePath(MEDIA_TYPE_IMAGE));
		if (_file==null) {
			CustomToast.show(activity(), R.string.operation_cannot_be_performed);
			return;
		}
		current_photoUri = Uri.fromFile(_file);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, current_photoUri);
		startActivityForResult(intent, CAMERA_APP);
	}

    protected void openVideoApp() {
		//create new Intent
		Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

		current_photoUri = FfmpegUtils.getOutputMediaFileUri(MEDIA_TYPE_VIDEO);  // create a file to save the video
		intent.putExtra(MediaStore.EXTRA_OUTPUT, current_photoUri);  // set the image file name

		intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0); // set the video image quality to high

//        Long video_size = Long.valueOf(User.current.video_size).longValue();
//
		intent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, 30 * 1048576L);//X mb *1024*1024

		//intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 5); // set the video image quality to high

		// start the Video Capture Intent
		startActivityForResult(intent, CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE);
	}

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

 
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (resultCode != Activity.RESULT_OK) {
			int jj=234;
			jj++;
			// nothing to do
			return;
		}
		
		switch (requestCode) {
		case GALLERY_APP:
//			Uri uri = data.getData();
//			String path = getRealPathFromURI(getActivity(), uri);
//			current_photoUri = Uri.parse(path);
//			openHandllePictureTaken(false);
			break;
			
		    case CAMERA_APP:
			    openHandllePictureTaken(true);
			    break;
            case CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE:
				openHandllePictureTaken(false);
                break;
		}

	}

	public void saveInputStreamToFile(InputStream input, String filePath)
	{
		try {
			File file = new File(filePath);
			OutputStream output = new FileOutputStream(file);
			try {
				try {
					byte[] buffer = new byte[4 * 1024]; // or other buffer size
					int read;

					while ((read = input.read(buffer)) != -1) {
						output.write(buffer, 0, read);
					}
					output.flush();
				} finally {
					output.close();
				}
			} catch (Exception e) {
				e.printStackTrace(); // handle exception, define IOException and others
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				input.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void openHandllePictureTaken(boolean isPicture)
	{
		boolean ok = false;
//			Uri uri = (Uri)data.getData();
		if (current_photoUri != null && current_photoUri.getPath() != null) {
			File taken_pict = new File(current_photoUri.getPath());
//				String taken_pict_path = getRealPathFromURI(current_photoUri);
			if (taken_pict.exists() && taken_pict.length() > 100) {

				if(!isPicture) {
					startProgress(R.string.prepare_video_upload);
					new TranscdingBackground().execute();
					return;
				}
				else
				{
					handlePictureTaken(taken_pict,isPicture);
					ok = true;
				}
			}
		}
		if (!ok) {
			CustomToast.show(activity(), R.string.operation_cannot_be_performed);
		}
	}
	
//	private String getRealPathFromURI(Uri contentUri) {
//		Context c = MyApp.context;
//	    String[] projx = { MediaStore.Images.Media.DATA };
//	    Cursor cursor = activity().managedQuery(contentUri, projx, null, null, null);
//	    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//	    cursor.moveToFirst();
//	    return cursor.getString(column_index);
//	}	
	 
	private void handlePictureTaken(final File photo,boolean isPicture) {
		long len = photo.length();
		AlertDialog.Builder adb = new AlertDialog.Builder(activity());
		//		adb.setView(alertDialogView);
        if(isPicture) {
            adb.setTitle(R.string.upload_photo_text);
        }
        else {
            adb.setTitle(R.string.upload_video_text);
        }
		adb.setIcon(R.drawable.ic_launcher);
		adb.setPositiveButton(R.string.popup_btn_ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				perform_photo_upload(photo.getAbsolutePath());
			} });
		adb.setNegativeButton(R.string.popup_btn_cancel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// no op
			} });

		adb.show();	
	}
	  

	private void perform_photo_upload(String filePath) {
		ArrayList<String> filePathArr = new ArrayList<String>();
		filePathArr.add(filePath);		
		CustomToast.show(activity(), R.string.upload_in_progress);
//		SingleAlbumFragment.hideCameraButton();
		UploadService.startUpload(filePathArr);
//		TransferController.upload(MyApp.context, current_photoUri);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getActivity().getMenuInflater();
	    inflater.inflate(R.menu.single_albun_menu, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.option1:
			openGalleryApp(OPEN_VIDEO_GALLERY_IND);
			return true;
		case R.id.option2:
			openCameraApp();
			return true;
		case R.id.option3:
			return true;
		default:
			return false;
		} 
	}
	
 
	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		if (!hidden && refreshNeeded) { 
			refreshNeeded = false;
		}
	}
	
	

	public static void setUploadMode() {
		inUploadMode = true; 
		inSelectMode = false; // states are exclusive
		
		boolean refreshed = false;
		if (fragment_inst != null) {
			
			try {
				refreshed = true;
			}
			catch (Exception ee) {
				int jj=24;
				jj++;
			}
		}		
		refreshNeeded = !refreshed; 
	}
	
	public static void hideCameraButton()
	{
		if (fragment_inst != null) {
//			fragment_inst.camara_btn.setVisibility(View.GONE);
			fragment_inst.save_btn.setVisibility(View.GONE);
		}
	}
	
	public static void showCameraButton()
	{
		if (fragment_inst != null && User.isTeacher()) {
			fragment_inst.camara_btn.setVisibility(View.VISIBLE);
			fragment_inst.save_btn.setVisibility(View.VISIBLE);
		}
	}


	public class TranscdingBackground extends AsyncTask<String, Void, File> {

		protected File doInBackground(String... paths) {
			File file = new File("");//FfmpegUtils.getCompressedVideo(current_photoUri.getPath(),"-1","-1");
			return file;
		}

		@Override
		protected void onPostExecute(File newFile) {
			stopProgress();
			handlePictureTaken(newFile, false);
		}
	}

}

package com.ganbook.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.ganbook.activities.AddAlbumActivity;
import com.ganbook.activities.MainActivity;
import com.ganbook.amazinglist.special.CustomToast;
import com.ganbook.app.MyApp;
import com.ganbook.communication.ICompletionHandler;
import com.ganbook.communication.datamodel.ClassDetails_2.ClassHistory;
import com.ganbook.communication.datamodel.ResultObj;
import com.ganbook.communication.json.BaseResponse;
import com.ganbook.communication.json.HistoryDetails;
import com.ganbook.communication.json.getalbum_response;
import com.ganbook.communication.json.transmitter.JsonTransmitter;
import com.ganbook.ui.AlbumsAdapter;
import com.ganbook.ui.AlbumsAdapter.AlbumItem;
import com.ganbook.ui.AlbumsAdapter.HeaderItem;
import com.ganbook.ui.AlbumsAdapter.Item;
import com.ganbook.ui.AlbumsAdapter.RowType;
import com.ganbook.user.User;
import com.ganbook.utils.CurrentYear;

import com.project.ganim.R;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;

@SuppressLint("InflateParams")
public class AlbumListFragment extends BaseFragment implements OnRefreshListener,OnItemClickListener,OnItemLongClickListener{


	private SwipeRefreshLayout mSwipeRefreshLayout;
	private ListView mListView;
	private AlbumsAdapter adapter;
	private PopupWindow deleteitemwindow;
	Context mcontext;
	private Button no, yes;

	private String gan_id;
	private String class_id;
	private boolean justCreated = false;

	ProgressBar pbHeaderProgress;

	public static AlbumListFragment inst;


	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		mcontext = context;

	}

	private final Initializer initializer = new Initializer();
	
	
	class Initializer {

		public void onCreate() {
			inst = AlbumListFragment.this;
			setActionBarValues();
			justCreated = true;
		}

		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

			MyApp.sendAnalytics("albums-gan-ui", "albums-gan-"+User.current.getCurrentGanId()+"-class "+User.current.getCurrentClassId()+"-ui-"+ User.getId(), "albums-gan-ui", "AlbumsGan");

			View _view = inflater.inflate(R.layout.album_frag, container, false);

			Log.i("noanoa","album_list onCreateView " +forceSetTitle());
			
			findActionbarFields();
			handleFragmentVisible();

			pbHeaderProgress = (ProgressBar) _view.findViewById(R.id.pbHeaderProgress);
			mSwipeRefreshLayout = (SwipeRefreshLayout) _view.findViewById(R.id.refresh_layout);
			
			mSwipeRefreshLayout.setOnRefreshListener(AlbumListFragment.this);
			mSwipeRefreshLayout.setColorScheme(android.R.color.holo_blue_bright, 
					android.R.color.holo_green_light, 
					android.R.color.holo_orange_light, 
					android.R.color.holo_red_light);
			
			mListView = (ListView) _view.findViewById(R.id.feeds_list);
			//mListView.setPinnedHeaderView(LayoutInflater.from(activity()).inflate(R.layout.item_composer_header, mListView, false));
			mListView.setOnItemClickListener(AlbumListFragment.this);
			mListView.setOnItemLongClickListener(AlbumListFragment.this);
//			select = (Button) activity().findViewById(R.id.select);
//			save_btn.setText(R.string.add_album_btn);
//			findActionbarFields();
			adapter = new AlbumsAdapter(MyApp.context, activity());
			mListView.setAdapter(adapter);



			return _view;
		}


		public void onActivityCreated() {
//			findActionbarFields();
		}

		public void onStart() {
			boolean _justCreated = justCreated;
			justCreated = false; //!
			populateAlbumList(_justCreated);

		}

		public void onBecomingVisible() {

			if (mSwipeRefreshLayout != null) { 
				setSaveListener(); 
				populateAlbumList(false);
			}
		}
		
	}

	
	@Override 
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initializer.onCreate();
	}

		
	private void setActionBarValues() {
		titleTextId = R.string.albums;
		saveBtnVisibility = View.GONE;
		saveBtnTextId = (User.isTeacher() ? SHOW_ADD_BUTTON : HIDE_ADD_BUTTON);
		drawerMenuVisibility = View.VISIBLE; 
	}


	@Override
	public void onDestroy() {
		super.onDestroy();
		if (this == inst) {
			inst = null;
		}
	}
		
	
	@SuppressWarnings("deprecation")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
        return initializer.onCreateView(inflater, container, savedInstanceState);

	}

	private void findActionbarFields() {
		base_onCreateView();
		setSaveListener();
	}


	private void setSaveListener() {
		add_btn.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				int jj=234;  
				jj++;
				start(AddAlbumActivity.class); 
			}
		});
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initializer.onActivityCreated();
		EventBus.getDefault().register(this);
		Log.d("ALBUM LIST FRAGMENT", "ON CREATED");

    }

	@Override
	public void onStart() { 
		super.onStart();
		initializer.onStart();
		EventBus.getDefault().register(this);
	}
	
	
	@Override
	public void onResume() {
		super.onResume();
		EventBus.getDefault().register(this);

		int jj=345;
		jj++;

	}
	
	
	@Override
	public void onRefresh() {
		mSwipeRefreshLayout.setRefreshing(false);
		populateAlbumList(true);
	}
 	
	@Override
	public void onBecomingVisible() { 
		super.onBecomingVisible();
		initializer.onBecomingVisible();

    }
	
	public static void forceContentRefresh() {
		if (inst != null) {
            inst.base_forceContentRefresh();
			inst.populateAlbumList(true);
		}
	}

	public static void contentRefresh() {
		if (inst != null) {
			inst.base_forceContentRefresh();
			inst.populateAlbumList(false);
		}
	}
	
	public static void updateAlbumName(String album_id, String album_name)
	{
		HashMap<String, ArrayList<getalbum_response>> _albumList = User.current.getAlbumList();
		
		for (String year : _albumList.keySet()) 
		{
			ArrayList<getalbum_response> albumsArr = _albumList.get(year);
			for (getalbum_response album : albumsArr)
			{
				if(album.album_id.equals(album_id))
				{
					album.album_name = album_name;
					break;
				}
			}
		}
			
		User.current.setAlbumList(_albumList);
	}


	public static void updateAlbumViewsUnseenPhotos(String album_id)
	{
		HashMap<String, ArrayList<getalbum_response>> _albumList = User.current.getAlbumList();

		if(_albumList == null)
		{
			return;
		}
//		Log.i("noanoa","updateAlbumViewsUnseenPhotos" + _albumList);
		String seen_pics = null;
		
		for (String year : _albumList.keySet()) 
			{
			ArrayList<getalbum_response> albumsArr = _albumList.get(year);
			for (getalbum_response album : albumsArr)
			{
				if(album.album_id.equals(album_id))
				{
					album.album_views = String.valueOf((Integer.valueOf(album.album_views) + 1));
					album.unseen_photos = null;
					seen_pics = album.pic_count;
					break;
				}
			}
		}
		User.current.setCurrentKidUnseenPhotosBySeenPhotos(seen_pics);
		User.current.setAlbumList(_albumList);
	}

	@Override
	public void onPause() {
		super.onPause();
		adapter.clear();
        Log.d("ON PAUSE", "ALBUM LIST FRAGMENT");

    }

	public static void updateAlbumLikes(String album_id, boolean like)
	{
		HashMap<String, ArrayList<getalbum_response>> _albumList = User.current.getAlbumList();

		if(_albumList == null)
		{
			return;
		}
//		Log.i("noanoa","updateAlbumViewsUnseenPhotos" + _albumList);
		String likes = null;

		for (String year : _albumList.keySet())
		{
			ArrayList<getalbum_response> albumsArr = _albumList.get(year);
			for (getalbum_response album : albumsArr)
			{
				if(album.album_id.equals(album_id))
				{
					if(like) {
						album.album_likes = String.valueOf((Integer.valueOf(album.album_likes) + 1));
						album.user_album_like = true;
					}
					else
					{
						album.album_likes = String.valueOf((Integer.valueOf(album.album_likes) - 1));
						album.user_album_like = false;
					}

					break;
				}
			}
		}
		User.current.setAlbumList(_albumList);
	}

	public static void updateAlbumComments(String album_id,boolean add)
	{
		HashMap<String, ArrayList<getalbum_response>> _albumList = User.current.getAlbumList();

		if(_albumList == null)
		{
			return;
		}


		for (String year : _albumList.keySet())
		{
			ArrayList<getalbum_response> albumsArr = _albumList.get(year);
			for (getalbum_response album : albumsArr)
			{
				if(album.album_id.equals(album_id))
				{
					if(add) {
						album.album_comments = String.valueOf((Integer.valueOf(album.album_comments) + 1));
					}
					else {
						album.album_comments = String.valueOf((Integer.valueOf(album.album_comments) - 1));
					}
					break;
				}
			}
		}
		User.current.setAlbumList(_albumList);
	}


	public void populateAlbumList(boolean forceRefresh) {
		
		Log.i("noanoa","album_list"+User.current+"");
		Log.i("noanoa","album_list"+User.current.getAlbumList()+"");
		
		mSwipeRefreshLayout.setRefreshing(false);
		final String year = CurrentYear.get(); //gggggFix make it year specific to class/kid
		if (!forceRefresh && User.current.hasAlbumList()) {
			HashMap<String, ArrayList<getalbum_response>> _albumList = (HashMap<String, ArrayList<getalbum_response>>) User.current.getAlbumList();

			if(adapter == null) {
				adapter = new AlbumsAdapter(MyApp.context, _albumList, activity());

			}
			else {
				adapter.clear();
				adapter.addAll(adapter.mapToArrayList(_albumList));
				adapter.notifyDataSetChanged();
			}

			return;
		} 
		
		

		pbHeaderProgress.setVisibility(View.VISIBLE);
		mListView.setVisibility(View.GONE);

		gan_id = User.current.getCurrentGanId();  
		class_id = User.current.getCurrentClassId();

		
		JsonTransmitter.send_getalbum(class_id, year, new ICompletionHandler() {			
			@Override
			public void onComplete(ResultObj result) {

				pbHeaderProgress.setVisibility(View.GONE);
				mListView.setVisibility(View.VISIBLE);

				if(JsonTransmitter.NO_NETWORK_MODE.equals(result.result))
				{
					showNotInternetAlert();
					mListView.addHeaderView(tryAgainView);
					adapter = new AlbumsAdapter(MyApp.context,(HashMap<String, ArrayList<getalbum_response>>) User.current.getAlbumList(), activity());
					mListView.setAdapter(adapter);
					return;
				}
				mListView.removeHeaderView(tryAgainView);
				
				mSwipeRefreshLayout.setRefreshing(false);
				ArrayList<getalbum_response> _albumList = new ArrayList<getalbum_response>();
				if (!result.succeeded) {
					CustomToast.show(activity(), result.result);
				}
				else { 
					ArrayList<BaseResponse> arr = result.getResponseArray();
					for (BaseResponse r: arr) {
						_albumList.add((getalbum_response)r);
					} 
				} 
				User.current.setAlbumList(null); // clear prior list 
				 
				User.current.addtoAlbumList(year,_albumList);
				
//				Log.i("noanoa","album_list_after_clear "+User.current.getAlbumList()+"");
				
				if(User.isParent())
				{
					//check for history in current kid
					HistoryDetails[] historyDetails = User.current.getCurrentKidHistory();
					if(historyDetails != null)
					{
						for (HistoryDetails historyDetail : historyDetails) {
							User.current.addtoAlbumList(historyDetail.class_year,new ArrayList<getalbum_response>());
						}					
					}
				}
				else //teacher
				{
//					User.current.
					ClassHistory[] classHistory = User.current.getCurrentClassHistory();
					if(classHistory != null)
					{
						for (ClassHistory _classHistory : classHistory) {
							User.current.addtoAlbumList(_classHistory.class_year,new ArrayList<getalbum_response>());
						}					
					}
				}
				
//				adapter = new AlbumsAdapter(activity().getApplicationContext(),mapToArrayList((HashMap<String, ArrayList<getalbum_response>>) User.current.getAlbumList()));
//				adapter = new AlbumsAdapter(MyApp.context,(HashMap<String, ArrayList<getalbum_response>>) User.current.getAlbumList(), activity());
//
//				mListView.setAdapter(adapter);
				adapter.clear();
				adapter.addAll(adapter.mapToArrayList((HashMap<String, ArrayList<getalbum_response>>) User.current.getAlbumList()));
				adapter.notifyDataSetChanged();

				mListView.setAdapter(adapter);
//				if (_albumList==null || _albumList.isEmpty()) {
//					CustomToast.show(activity(), R.string.empty_albums_list);
//				}
				
				if(User.isParent())
    			{
					int unseen_photos = 0;
					
					for (getalbum_response album : User.current.getAlbumList().get(CurrentYear.get()))
		    		{
		    			if(album.unseen_photos != null && Integer.valueOf(album.unseen_photos) > 0)
		    			{
							unseen_photos += Integer.valueOf(album.unseen_photos);
		    			}
		    		}
					
					User.current.setCurrentKidUnseenPhotos(String.valueOf(unseen_photos));
					MainScreenFragment.initAlbumTabBadge();
					MainActivity.updateDrawerContent();
    			}
				
			}
		});		
	}
	
	protected void call_sendgetalbum(final String year,final String class_id) {
		startProgress(R.string.operation_proceeding);
		
		JsonTransmitter.send_getalbum(class_id, year, new ICompletionHandler() {			
			@Override
			public void onComplete(ResultObj result) {
				stopProgress();
				
				if(JsonTransmitter.NO_NETWORK_MODE.equals(result.result))
				{
					showNotInternetAlert();
					mListView.removeHeaderView(tryAgainView);
					mListView.addHeaderView(tryAgainView);
					adapter = new AlbumsAdapter(MyApp.context,(HashMap<String, ArrayList<getalbum_response>>) User.current.getAlbumList(), activity());
					mListView.setAdapter(adapter);
					return;
				}
				
				mListView.removeHeaderView(tryAgainView);
				
				mSwipeRefreshLayout.setRefreshing(false);
				ArrayList<getalbum_response> _albumList = new ArrayList<getalbum_response>();
				if (!result.succeeded) {
					CustomToast.show(activity(), result.result);
				}
				else { 
					ArrayList<BaseResponse> arr = result.getResponseArray();
					for (BaseResponse r: arr) {
						_albumList.add((getalbum_response)r);
					} 
				} 
				
//				Log.i("noanoa",User.current+"");
//				Log.i("noanoa",User.current.getAlbumList()+"");
				
				User.current.addtoAlbumList(year,_albumList);
				
//				adapter = new AlbumsAdapter(activity().getApplicationContext(),mapToArrayList((HashMap<String, ArrayList<getalbum_response>>) User.current.getAlbumList()));
//				adapter = new AlbumsAdapter(MyApp.context,(HashMap<String, ArrayList<getalbum_response>>) User.current.getAlbumList(), activity());
//
//				mListView.setAdapter(adapter);

				adapter.clear();
				adapter.addAll(adapter.mapToArrayList((HashMap<String, ArrayList<getalbum_response>>) User.current.getAlbumList()));
				adapter.notifyDataSetChanged();
				
				if (_albumList==null || _albumList.isEmpty()) {
					CustomToast.show(activity(), R.string.empty_albums_list);
				}
				
//				if (User.isParent()) {  // only!
//					MainScreenFragment.initAlbumTabBadge();
//					MainActivity.updateDrawerContent();
//				}
			}
		});		
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) { //setOnClickListener

		ArrayList<Item> items = AlbumsAdapter.mapToArrayList((HashMap<String, ArrayList<getalbum_response>>) User.current.getAlbumList());
		int type = items.get(position).getViewType();
		if(type == RowType.ALBUM_ITEM.ordinal())
		{
			AlbumItem item = (AlbumItem)items.get(position);
			MyApp.selAlbum = new getalbum_response(item.year, item.album_id, item.album_name, item.album_description, item.album_image, item.album_date, item.pic_count, item.video_count, item.album_views, item.unseen_photos, item.user_album_like, item.likes, item.album_comments);
			MyApp.async_writeUserToLocaCache();

			activity().moveToTab(FragmentType.Show_Gallery);
		}
		else if(type == RowType.HEADER_ITEM.ordinal())
		{
			HeaderItem item = (HeaderItem)items.get(position);
			if(!CurrentYear.get().equals(item.name))
			{
				String class_id = "";
				if(User.isTeacher())
				{
					class_id = User.current.getCurrentClassId();
				}
				else
				{
					HistoryDetails historyDetails = User.current.getCurrentKidHistoryByYear(item.name);
					class_id = historyDetails.class_id;
				}

				call_sendgetalbum(item.name, class_id);//add class id and class name and gan name
			}
		}
	}


	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		Toast.makeText(getActivity(), "TRUE", Toast.LENGTH_SHORT).show();
		return true;
	}	


	public static void call_handleFragmentVisible() {
		if (inst != null) {
			inst.handleFragmentVisible(); 
		}
	}

	public static void setTitleVisibility(int visible) {
		if (inst != null) {
			if(visible == View.VISIBLE)
			{
				inst.handleFragmentVisible();
			}
			inst.doSetTitleVisibility(visible); 
		}
	}


	@Override
	protected void doSetTitleVisibility(int visibility) {

		super.doSetTitleVisibility(visibility);
		if (add_btn != null && User.isTeacher()) {
			add_btn.setVisibility(visibility);
		}
	}
}

package com.ganbook.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.ganbook.activities.MainActivity;
import com.ganbook.amazinglist.special.CustomToast;
import com.ganbook.app.MyApp;
import com.ganbook.communication.ICompletionHandler;
import com.ganbook.communication.datamodel.ClassDetails;
import com.ganbook.communication.datamodel.ResultObj;
import com.ganbook.communication.json.BaseResponse;
import com.ganbook.communication.json.GetUserKids_Response;
import com.ganbook.communication.json.HistoryDetails;
import com.ganbook.communication.json.getalbum_response;
import com.ganbook.communication.json.getkindergarten_response;
import com.ganbook.communication.json.transmitter.JsonTransmitter;
import com.ganbook.models.AlbumsAnswer;
import com.ganbook.ui.AlbumsAdapter;
import com.ganbook.ui.AlbumsAdapter.AlbumItem;
import com.ganbook.ui.AlbumsAdapter.HeaderItem;
import com.ganbook.ui.AlbumsAdapter.Item;
import com.ganbook.ui.AlbumsAdapter.RowType;
import com.ganbook.user.User;
import com.ganbook.utils.CurrentYear;
import com.ganbook.utils.FragmentUtils;
import com.ganbook.utils.KeyboardUtils;

import com.project.ganim.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class UnattachedKidFragment extends BaseFragment implements OnRefreshListener,OnItemClickListener,OnItemLongClickListener{

	// unattached_kid_fragment

	private SwipeRefreshLayout mSwipeRefreshLayout;
	Context mcontext;
	private Button no, yes, cont_btn;
	private EditText gan_code;
	private TextView blue_header,or;

	private ListView mListView;
	private AlbumsAdapter adapter;
	View headerView;

	public static UnattachedKidFragment inst;

	public static boolean fromUnattachedFragment;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mcontext = activity;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setActionBarValues();
		inst = this;
	}

	@Override
	public void onDestroy() {

		super.onDestroy();
		if (this == inst) {
			inst = null;
		}
	}


	private void setActionBarValues() {
		//		titleTextId = R.string.albums;
		saveBtnVisibility = View.GONE;
		drawerMenuVisibility = View.VISIBLE;
		//		saveBtnTextId = SHOW_ADD_BUTTON;
	}


	@SuppressWarnings("deprecation")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View _view = inflater.inflate(R.layout.unattached_kid_fragment, container, false);


		MyApp.sendAnalytics("main-unattached-kid-ui", "main-unattached-kid-ui-"+User.getId(), "main-unattached-kid-ui", "UnattachedKid");


		findActionbarFields();
		//		if (forceSetTitle()) {
		clearSetTitle();
		handleFragmentVisible();
		//		}
		mSwipeRefreshLayout = (SwipeRefreshLayout) _view.findViewById(R.id.refresh_layout);

		mSwipeRefreshLayout.setOnRefreshListener(UnattachedKidFragment.this);
		mSwipeRefreshLayout.setColorScheme(android.R.color.holo_blue_bright,
				android.R.color.holo_green_light,
				android.R.color.holo_orange_light,
				android.R.color.holo_red_light);

		//		if (User.current.kidIsWaitingApproval()) {
		//			_view = inflater.inflate(R.layout.kid_waiting_approval_fragment, container, false);
		//		}
		//		else {
		//			_view = inflater.inflate(R.layout.unattached_kid_fragment, container, false);
		//			gan_code = (EditText) _view.findViewById(R.id.gan_code);
		//
		//			((Button)_view.findViewById(R.id.send_btn)).setOnClickListener(new View.OnClickListener() {
		//				@Override
		//				public void onClick(View v) {
		//					KeyboardUtils.close(activity(), gan_code);
		//					String code = gan_code.getText().toString();
		//					if (code.length() == 0) {
		//						CustomToast.show(activity(), R.string.enter_code_msg);
		//						return;
		//					}
		//					call_getKindergarten(code);
		//				}
		//			});
		//		}



		mListView = (ListView) _view.findViewById(R.id.feeds_list);
		//mListView.setPinnedHeaderView(LayoutInflater.from(activity()).inflate(R.layout.item_composer_header, mListView, false));
		mListView.setOnItemClickListener(this);



		if (User.current.kidIsWaitingApproval()) {
			headerView = inflater.inflate(R.layout.kid_waiting_approval_fragment, null);
		}
		else {
			headerView = inflater.inflate(R.layout.enter_code_unattached__kid_fragment, null);
			gan_code = (EditText) headerView.findViewById(R.id.gan_code);

			((Button)headerView.findViewById(R.id.send_btn)).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					KeyboardUtils.close(activity(), gan_code);
					String code = gan_code.getText().toString();
					if (code.length() == 0) {
						CustomToast.show(activity(), R.string.enter_code_msg);
						return;
					}
					call_getKindergarten(code);
				}
			});

			cont_btn = (Button)headerView.findViewById(R.id.cont_btn);
			or = (TextView)headerView.findViewById(R.id.or);

			if(User.current.getCurrentKidHistory() != null && User.current.getCurrentKidHistory().length > 0)
			{
				or.setVisibility(View.VISIBLE);
				cont_btn.setVisibility(View.VISIBLE);
			}
			else
			{
				or.setVisibility(View.GONE);
				cont_btn.setVisibility(View.GONE);
			}

			cont_btn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (User.current.getCurrentKidHistory() != null && User.current.getCurrentKidHistory().length > 0) {
						String code = User.current.getCurrentKidHistory()[0].gan_code;
						call_getKindergarten(code);
					}
				}
			});


		}

		blue_header = (TextView) headerView.findViewById(R.id.blue_header);
		String cur_year = CurrentYear.get();
		blue_header.setText(cur_year);

		mListView.addHeaderView(headerView);

		initListKidHistory();

		return _view;
	}


	public static void setFragmentVisible() {
		if (inst != null) {
			inst.handleFragmentVisible();
		}
	}

	private void initListKidHistory() {
		if(User.current.getCurrentKidHistory() != null)
		{
			for (HistoryDetails historyDetails : User.current.getCurrentKidHistory()) {
				User.current.addtoAlbumList(historyDetails.class_year,new ArrayList<getalbum_response>());
			}

			adapter = new AlbumsAdapter(MyApp.context,(HashMap<String, ArrayList<getalbum_response>>) User.current.getAlbumList(), activity());
		}
		else
		{
			adapter = new AlbumsAdapter(MyApp.context,activity());
		}

		//			adapter = new AlbumsAdapter(activity().getApplicationContext(),AlbumListFragment.mapToArrayList((HashMap<String, ArrayList<getalbum_response>>) User.current.getAlbumList()));

		mListView.setAdapter(adapter);
	}

	public void call_getKindergarten(final String ganCode) {
		startProgress(R.string.operation_proceeding);
		JsonTransmitter.send_getkindergarten(ganCode, new ICompletionHandler() {
			@Override
			public void onComplete(ResultObj result) {
				stopProgress();

				if (!result.succeeded)
				{
					if(JsonTransmitter.NO_NETWORK_MODE.equals(result.result))
					{
						showNotInternetAlert();
						mListView.removeHeaderView(headerView);
						mListView.addHeaderView(tryAgainView);
						adapter = new AlbumsAdapter(MyApp.context,(new HashMap<String, ArrayList<getalbum_response>>()), activity());
						mListView.setAdapter(adapter);
						return;
					}
					CustomToast.show(activity(), activity().getString(R.string.enter_valid_code_msg));
				}
				else
				{

					getkindergarten_response _res = (getkindergarten_response)result.getResponse(0);

					MyApp.gan_name = _res.gan.gan_name;

					MyApp.class_ids = new String[_res.classes.length];
					MyApp.class_names = new String[_res.classes.length];

					for (int i = 0; i < _res.classes.length; i++) {
						ClassDetails classDetails = _res.classes[i];

						MyApp.class_ids[i] = classDetails.class_id;
						MyApp.class_names[i] = classDetails.class_name;
					}

					fromUnattachedFragment = true;


					MyApp.sendAnalytics("menu-choose-class-unattached-kid-ui", "menu-choose-class-unattached-kid-ui-" + User.getId(), "menu-choose-class-unattached-kid-ui", "MenuChooseClassUnattechedKid");


					//start(ChooseClassActivity.class);
				}
				//					String kid_id = User.current.getCurrentKid().kid_id;
				//					User.addToSentCodeMap(kid_id);
				//					User.current.markCurrentKidAsWaitingApproval();
				//					activity().refresh_Unattached_Kid_Fragment(UnattachedKidFragment.this);
				//					SignupActivity.createNewInProgress = false; // newUser now fully created
				//					start(MainActivity.class);
			}

		});
	}

	private void findActionbarFields() {
		base_onCreateView();
		//		setSaveListener();
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		findActionbarFields();
	}


	@Override
	public void onRefresh() {
		//		refreshContent();

		mSwipeRefreshLayout.setRefreshing(false);
		call_getKids();
		//populateAlbumList(true);
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
								   int position, long id) {

		return false;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) { //setOnClickListener
		position --;

		if(position == -1)
		{
			call_getKids();
			return;
		}
		ArrayList<Item> items = AlbumsAdapter.mapToArrayList((HashMap<String, ArrayList<getalbum_response>>) User.current.getAlbumList());
		int type = items.get(position).getViewType();

		if(type == RowType.ALBUM_ITEM.ordinal())
		{
			AlbumItem item = (AlbumItem)items.get(position);
//			MyApp.selAlbum = new getalbum_response(item.year, item.album_id, item.album_name, item.album_image, item.album_date, item.pic_count, item.video_count, item.album_views, item.unseen_photos, item.user_album_like, item.likes, item.album_comments);
//
//			activity().moveToTab(FragmentType.Show_Gallery);

			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm");
			Date date = null;

			try {
				date = format.parse(item.album_date);
				System.out.println(date);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			int unseen_photos = 0;
			if(item.unseen_photos != null)
			{
				unseen_photos = Integer.valueOf(item.unseen_photos);
			}

			AlbumsAnswer albumsAnswer= new AlbumsAnswer(item.album_id, item.album_name,item.album_description, item.album_image, date, Integer.valueOf(item.pic_count),
					Integer.valueOf(item.video_count),
					Integer.valueOf(item.album_views), Integer.valueOf(item.album_comments),
					Integer.valueOf(item.likes),
					Integer.valueOf(item.album_views),
					unseen_photos);

			HistoryDetails details = User.current.getCurrentKidHistoryByYear(item.year);
			String gan_id = details.gan_id;
			String class_id = details.class_id;

			AlbumDetailsFragment albumDetailsFragment = AlbumDetailsFragment.newInstance(class_id, gan_id,
					albumsAnswer, position);

			FragmentUtils.openFragment(albumDetailsFragment, R.id.content_frame, "SINGLE", getActivity(), true);
		}
		else
		{
			HeaderItem item = (HeaderItem)items.get(position);
			if(!CurrentYear.get().equals(item.name))
			{
				HistoryDetails historyDetails = User.current.getCurrentKidHistoryByYear(item.name);
				call_sendgetalbum(item.name, historyDetails.class_id);//add class id and class name and gan name
			}
		}
	}

	public void call_getKids() {
		startProgress(R.string.operation_proceeding);
		String userId = User.getId();
		JsonTransmitter.send_getuserkids(userId, new ICompletionHandler() {
			@Override
			public void onComplete(ResultObj result) {
				stopProgress();

				if (!result.succeeded) {
					if(JsonTransmitter.NO_NETWORK_MODE.equals(result.result))
					{
						showNotInternetAlert();
						mListView.removeHeaderView(headerView);
						mListView.addHeaderView(tryAgainView);
						adapter = new AlbumsAdapter(MyApp.context,(new HashMap<String, ArrayList<getalbum_response>>()), activity());
						mListView.setAdapter(adapter);
						return;
					}
					else
					{
						String errmsg = result.result;
						CustomToast.show(activity(), errmsg);
						return;
					}
				}
				int num = result.getNumResponses();
				GetUserKids_Response[] responses = new GetUserKids_Response[num];
				for (int i = 0; i < num; i++) {
					responses[i] = (GetUserKids_Response) result.getResponse(i);
				}
				User.updateWithUserkids(responses);

				//				if(MainActivity.inst == null)
				//				{
				//					start(MainActivity.class);
				//				}
				//				else
				//				{
				//					MainActivity.updateDrawerContent();
				//
				//					AddKidActivity.callFinish();
				//					ParentDetailsActivity.callFinish();
				//
				//					MainActivity.openDrawer();
				//					callFinish();
				//				}

				MainActivity.refresh();
			}
		});

	}

	protected void call_sendgetalbum(final String year,final String class_id) {
		startProgress(R.string.operation_proceeding);

		JsonTransmitter.send_getalbum(class_id, year, new ICompletionHandler() {
			@Override
			public void onComplete(ResultObj result) {
				stopProgress();
				//				mSwipeRefreshLayout.setRefreshing(false);
				ArrayList<getalbum_response> _albumList = new ArrayList<getalbum_response>();
				if (!result.succeeded) {
					if(JsonTransmitter.NO_NETWORK_MODE.equals(result.result))
					{
						showNotInternetAlert();
						mListView.removeHeaderView(headerView);
						mListView.addHeaderView(tryAgainView);
						adapter = new AlbumsAdapter(MyApp.context,(new HashMap<String, ArrayList<getalbum_response>>()), activity());
						mListView.setAdapter(adapter);
						return;
					}
					else
					{
						String errmsg = result.result;
						CustomToast.show(activity(), errmsg);
						return;
					}
				}
				else {
					ArrayList<BaseResponse> arr = result.getResponseArray();
					for (BaseResponse r: arr) {
						_albumList.add((getalbum_response)r);
					}
				}
				User.current.addtoAlbumList(year,_albumList);

				//				adapter = new AlbumsAdapter(activity().getApplicationContext(),mapToArrayList((HashMap<String, ArrayList<getalbum_response>>) User.current.getAlbumList()));
				adapter = new AlbumsAdapter(MyApp.context,(HashMap<String, ArrayList<getalbum_response>>) User.current.getAlbumList(), activity());

				mListView.setAdapter(adapter);

				if (_albumList==null || _albumList.isEmpty()) {
					CustomToast.show(activity(), R.string.empty_albums_list);
				}
			}
		});
	}



}

package com.ganbook.fragments;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TabHost.TabContentFactory;
import android.widget.TextView;

import com.ganbook.activities.DrawingInformationAdding;
import com.ganbook.activities.MainActivity;
import com.ganbook.activities.MeetingEventActivity;
import com.ganbook.activities.MeetingEventListActivity;
import com.ganbook.activities.MeetingHoursActivity;
import com.ganbook.activities.StudentListActivity;
import com.ganbook.activities.UserAttachmentsActivity;
import com.ganbook.communication.ICompletionHandler;
import com.ganbook.communication.datamodel.ResultObj;
import com.ganbook.communication.json.GetUserKids_Response;
import com.ganbook.communication.json.transmitter.JsonTransmitter;
import com.ganbook.dialogs.Dialogs;
import com.ganbook.fragments.tabs.AlbumsFragment;
import com.ganbook.fragments.tabs.ContactsFragment;
import com.ganbook.fragments.tabs.EventsFragment;
import com.ganbook.fragments.tabs.KidWithoutGanFragment;
import com.ganbook.fragments.tabs.MessagesFragment;
import com.ganbook.interfaces.TitleIteractionListener;
import com.ganbook.models.CreateDrawingAnswer;
import com.ganbook.models.events.PermissionRefresh;
import com.ganbook.models.events.UpdateAlbumViewEvent;
import com.ganbook.user.User;
import com.ganbook.utils.FragmentUtils;
import com.ganbook.utils.StrUtils;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.project.ganim.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.ganbook.utils.Const.GALLERY_APP;


@SuppressLint("InflateParams")
public class MainScreenFragment extends BaseFragment implements
		TabHost.OnTabChangeListener , ViewPager.OnPageChangeListener {

	String TAG = MainScreenFragment.class.getName();

	private static final String UNATTACHED_TAB = "UnattachedKidTab";
	private static final String TAB_1 = "Tab1";
	private static final String TAB_2 = "Tab2";
	private static final String TAB_3 = "Tab3";
	private static final String TAB_4 = "Tab4";
	private int previousPosition = 0;
	private int nexPosition = 0;
	public static final int ALBUM_IND = 0;
	public static final int MESSAGE_IND = 1;
	public static final int CONTACT_IND = 2;
	public static final int EVENT_IND = 3;
	public static MainScreenFragment inst;
	
	public static boolean unattached_kid_mode = false;
	TitleIteractionListener titleIteractionListener;
	
	private Context context;
	public TabHost mTabHost;
	private ViewPager mViewPager;
	private HashMap<String, TabInfo> mapTabInfo = new HashMap<String, MainScreenFragment.TabInfo>();
	private PagerAdapter mPagerAdapter;
	private View _view;
	private FloatingActionMenu floatingActionMenu;
	private FloatingActionButton drawingsTab, studentListFab, attachmentsFab, eventsFab;
	private View _contact, _event;
	private Uri currentPhotoUri;

	@SuppressWarnings("unused")
	private class TabInfo {
		String tag;
		Class<?> clss;

		Bundle args;
		TabInfo(String tag, Class<?> clazz, Bundle args) {
			this.tag = tag;
			this.clss = clazz;
			this.args = args;
		}
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		inst = this;

	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		if (this == inst) {
			inst = null;
		}
		EventBus.getDefault().unregister(this);
	}
	
	
	static class TabFactory implements TabContentFactory {
		private final Context mContext;
		public TabFactory(Context context) {
			mContext = context;
		}
		public View createTabContent(String tag) {
			View v = new View(mContext);
			v.setMinimumWidth(0);
			v.setMinimumHeight(0);
			return v;
		}
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);

		this.context = context;
		if (context instanceof TitleIteractionListener) {

			titleIteractionListener = (TitleIteractionListener) context;
		}

		EventBus.getDefault().register(this);

		titleIteractionListener.setTitle(StrUtils.generateTitle());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 final Bundle savedInstanceState) {
		setActionBarValues();
		_view = inflater.inflate(R.layout.main_fragment, container, false);
		base_onCreateView();

		User usr = User.blocking_loadFromLocalCache(false);

		mViewPager = (ViewPager) _view.findViewById(R.id.viewpager);
		mViewPager.setOffscreenPageLimit(0);
		mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageSelected(int arg0) {
				int jj=234;
				jj++; 
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

		floatingActionMenu = _view.findViewById(R.id.floatingActionMenu);
		drawingsTab = _view.findViewById(R.id.floatingActionStatistic);
		studentListFab = _view.findViewById(R.id.studentListFab);
		attachmentsFab = _view.findViewById(R.id.attachmentsFab);
		eventsFab = _view.findViewById(R.id.eventsFab);
		studentListFab.setVisibility(View.GONE);

		if(User.isTeacher()) {
			drawingsTab.setVisibility(View.GONE);
			studentListFab.setVisibility(View.GONE);
			attachmentsFab.setVisibility(View.VISIBLE);
			eventsFab.setVisibility(View.VISIBLE);
			eventsFab.setLabelText(getString(R.string.events_text));
			attachmentsFab.setLabelText(getString(R.string.send_attachment_text));
		} else if (User.isStaff()) {
			drawingsTab.setVisibility(View.GONE);
			studentListFab.setVisibility(View.GONE);
			attachmentsFab.setVisibility(View.GONE);
		} else if (User.isStudent()) {
			studentListFab.setVisibility(View.GONE);
			drawingsTab.setVisibility(View.GONE);
            attachmentsFab.setVisibility(View.GONE);
		} else if(User.isParent()) {
			studentListFab.setVisibility(View.GONE);
            attachmentsFab.setVisibility(View.VISIBLE);
            eventsFab.setVisibility(View.VISIBLE);
            attachmentsFab.setLabelText(getString(R.string.attachments_string));
		}



		attachmentsFab.setOnClickListener(view -> {
			Intent intent = new Intent(getActivity(), UserAttachmentsActivity.class);
			startActivity(intent);
			floatingActionMenu.close(true);

		});

		studentListFab.setOnClickListener(view -> {
			Intent intent = new Intent(getActivity(), StudentListActivity.class);
			startActivity(intent);
			floatingActionMenu.close(true);
		});

		drawingsTab.setOnClickListener(v -> {
			if(User.current.getCurrentKidDrawingAlbumName() == null) {
				final AlertDialog.Builder builder =  new AlertDialog.Builder(getActivity());

				LayoutInflater inflater1 = getActivity().getLayoutInflater();
				final View dialogView = inflater1.inflate(R.layout.dialog_style, null);
				builder.setView(dialogView);


				Button yesButtonDialog = dialogView.findViewById(R.id.dialogYes);
				Button noButtonDialog = dialogView.findViewById(R.id.dialogNo);
				TextView drawingAlbumText = dialogView.findViewById(R.id.createDrawingText);
				final EditText drawingAlbumName = dialogView.findViewById(R.id.drawingNameText);
				drawingAlbumText.setText(getString(R.string.drawing_album_for_kid));
				final AlertDialog dialog = builder.show();
				dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
				yesButtonDialog.setOnClickListener(v1 -> {
					if(drawingAlbumName.getText().toString().isEmpty()) {
						Dialogs.errorDialogWithButton(getActivity(), getString(R.string.warning),getString(R.string.drawing_album_name_warning), getString(R.string.ok));
					} else {
						createDrawingAlbum(drawingAlbumName.getText().toString(), User.current.getCurrentKidId());
						dialog.dismiss();
						floatingActionMenu.close(true);
					}
				});

				noButtonDialog.setOnClickListener(new View.OnClickListener() {
					@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
					@Override
					public void onClick(View v) {
						dialog.dismiss();
						floatingActionMenu.close(true);
					}
				});

				dialogView.setOnTouchListener(new View.OnTouchListener() {
					@Override
					public boolean onTouch(View v, MotionEvent event) {
						InputMethodManager im = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
						assert im != null;
						im.hideSoftInputFromWindow(drawingAlbumName.getWindowToken(), 0);
						return true;
					}
				});

		} else {
			floatingActionMenu.close(true);
			albumDrawingsDetailsFragmentOpen(User.current.getCurrentDrawingAlbumId(), User.current.getCurrentKidDrawingAlbumName());
		}
	});

		eventsFab.setOnClickListener(v -> {
			floatingActionMenu.close(true);
			Intent intent = new Intent(getActivity(), MeetingEventListActivity.class);
			startActivity(intent);
/*			if (User.current.getCurrentClassMeeting() != null) {
				if (User.current.getCurrentClassMeeting().equals("0") && User.isTeacher()) {
					Intent intent = new Intent(getActivity(), MeetingEventActivity.class);
					startActivity(intent);
				} else {
					Intent intent = new Intent(getActivity(), MeetingHoursActivity.class);
					startActivity(intent);
				}
			}*/

		});


		return _view;
	}

	private void createDrawingAlbum(final String albumName, String kidId) {
		Call<CreateDrawingAnswer> call = ganbookApiInterfacePOST.createDrawingAlbum(albumName, kidId);

		call.enqueue(new Callback<CreateDrawingAnswer>() {
			@Override
			public void onResponse(Call<CreateDrawingAnswer> call, Response<CreateDrawingAnswer> response) {

				CreateDrawingAnswer answer = response.body();
				User.current.setCurrentKidDrawingAlbumName(albumName);
				Dialogs.sneakerSuccess(getActivity(), getString(R.string.drawing_album_succeed), getString(R.string.drawing_album_created));
				albumDrawingsDetailsFragmentOpen(answer.getDrawingAlbumId(), albumName);

			}

			@Override
			public void onFailure(Call<CreateDrawingAnswer> call, Throwable t) {
				stopProgress();
				Dialogs.errorDialogWithButton(getActivity(), getString(R.string.drawing_album_error),getString(R.string.drawing_album_server_error), getString(R.string.ok));
			}
		});
	}

	private void albumDrawingsDetailsFragmentOpen(String albumDrawingId, String drawingAlbumName) {

		AlbumDrawingsDetailsFragment drawingsDetailsFragment = AlbumDrawingsDetailsFragment.newInstance(albumDrawingId, drawingAlbumName);

		FragmentUtils.openFragment(drawingsDetailsFragment, R.id.content_frame, "SINGLE", context, true);
	}


	private void setActionBarValues() {

		saveBtnVisibility = View.GONE;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		this.initialiseTabHost(savedInstanceState);
		if (savedInstanceState != null) {
            mTabHost.setCurrentTabByTag(savedInstanceState.getString("tab"));
        }
		this.intialiseViewPager();

		final View shadowView = _view.findViewById(R.id.shadowView);

		if(User.isTeacher()) {
			floatingActionMenu.setVisibility(View.VISIBLE);
		} else if (User.isStaff()) {
			floatingActionMenu.setVisibility(View.GONE);
		} else if (User.isStudent()) {
			floatingActionMenu.setVisibility(View.GONE);
		} else if(User.isParent()) {
			floatingActionMenu.setVisibility(View.VISIBLE);
		} else {
			floatingActionMenu.setVisibility(View.GONE);
		}

        floatingActionMenu.setOnMenuToggleListener(new FloatingActionMenu.OnMenuToggleListener() {
            @Override
            public void onMenuToggle(boolean b) {
                if(b) {
                    shadowView.setVisibility(View.VISIBLE);
                } else {
                    shadowView.setVisibility(View.GONE);
                }
            }
        });
	}

	@Subscribe
	public void onPermissionRefreshEvent(PermissionRefresh permissionRefresh) {

		String parent_id = User.getId();
		final List<GetUserKids_Response> userKidsResponse = new ArrayList<>();
		JsonTransmitter.send_getuserkids(parent_id, new ICompletionHandler() {
			@Override
			public void onComplete(ResultObj result) {
				int num = result.getNumResponses();
				GetUserKids_Response[] responses = new GetUserKids_Response[num];
				for (int i = 0; i < num; i++) {
					responses[i] = (GetUserKids_Response) result.getResponse(i);
					if(responses[i].kid_name.equals(User.current.getCurrentKidName())) {
						if (responses[i].contactsForbidden) {
							_contact.setVisibility(View.GONE);
						} else {
							_contact.setVisibility(View.VISIBLE);
						}
					}
				}

			}
		});
	}



	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
			case GALLERY_APP:
//			Uri uri = data.getData();
//			String path = getRealPathFromURI(getActivity(), uri);
//			current_photoUri = Uri.parse(path);
//			openHandllePictureTaken(false);
				break;

			case 666:
				Intent i = new Intent(getActivity(), DrawingInformationAdding.class);
				i.putExtra("imagePath", currentPhotoUri);
				startActivity(i);
				break;

		}
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		int jj = 0;
		jj++;
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
 
	}

	@Override
	public void onPageSelected(int position) {
		this.mTabHost.setCurrentTab(position);
	}


	@Override
	public void onTabChanged(String tabId) {
		int currentPosition = this.mTabHost.getCurrentTab();

		if(_contact.getVisibility() == View.GONE) {
			if(currentPosition == 0) {
				this.mViewPager.setCurrentItem(0);
				previousPosition = 0;
				nexPosition = 1;
			} else if(currentPosition == 1 && previousPosition == 0) {
				this.mViewPager.setCurrentItem(1);
				previousPosition = 1;
				nexPosition = 2;
			} else if(currentPosition == 2) {
				this.mViewPager.setCurrentItem(3);
				previousPosition = 2;
				nexPosition = 3;
			} else if(currentPosition == 3 && previousPosition == 2 && nexPosition == 3) {
				this.mViewPager.setCurrentItem(1);
			} else {
				this.mViewPager.setCurrentItem(currentPosition);
			}
		} else {
			this.mViewPager.setCurrentItem(currentPosition);
		}
		forceCurrentTabToSetTitlebar();
	}
	
	
	private void intialiseViewPager() {
		List<Fragment> fragments = new Vector<Fragment>();
		if (User.current.kidWithoutGan()) {
			BaseFragment newKidFrag = (BaseFragment) Fragment.instantiate(context, UnattachedKidFragment.class.getName());
			newKidFrag.setForceSetTitle();
			newKidFrag.handleFragmentVisible();
			fragments.add(KidWithoutGanFragment.newInstance());
		} 
		else {
			AlbumsFragment albumsFragment = AlbumsFragment.newInstance();
			MessagesFragment messagesFragment = MessagesFragment.newInstance();
			ContactsFragment contactsFragment = ContactsFragment.newInstance();
			EventsFragment eventsFragment = EventsFragment.newInstance();
			fragments.add(albumsFragment);
			fragments.add(messagesFragment);
			fragments.add(contactsFragment);
			fragments.add(eventsFragment);
		}
		FragmentManager f_manager = activity().getSupportFragmentManager(); 
		this.mPagerAdapter  = new com.ganbook.ui.PagerAdapter(f_manager, fragments);
		this.mViewPager.setAdapter(mPagerAdapter);
		this.mViewPager.setOffscreenPageLimit(4);
		this.mViewPager.setOnPageChangeListener(this);
		
	}
	
	public static void updateMessageTabBadge()
	{
		if (inst != null && inst.mTabHost != null) {
			RelativeLayout layout = (RelativeLayout) inst.mTabHost.getTabWidget().getChildAt(1).findViewById(R.id.notify_layout);
			layout.setVisibility(View.GONE);
		}
	}
	
	public static void updateAlbumTabBadge(String seen_photos)
	{
		TextView num = inst.mTabHost.getTabWidget().getChildAt(0).findViewById(R.id.num);
		String unseen_photos = num.getText().toString();
		
		int unseen_photos_int = Integer.valueOf(unseen_photos) - Integer.valueOf(seen_photos);
		
		if(unseen_photos_int > 0)
		{
			num.setText(String.valueOf(unseen_photos_int));
		}		
		else
		{
			RelativeLayout layout = inst.mTabHost.getTabWidget().getChildAt(0).findViewById(R.id.notify_layout);
			layout.setVisibility(View.GONE);
		}		
	}

	@Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
	public void onUpdateAlbumViewEvent(UpdateAlbumViewEvent event) {

		TextView num = mTabHost.getTabWidget().getChildAt(0).findViewById(R.id.num);
		String unseen_photos = num.getText().toString();

		int unseen_photos_int = Integer.valueOf(unseen_photos) - event.seen_photos;

		if(unseen_photos_int > 0)
		{
			num.setText(String.valueOf(unseen_photos_int));
		}
		else
		{
			RelativeLayout layout = mTabHost.getTabWidget().getChildAt(0).findViewById(R.id.notify_layout);
			layout.setVisibility(View.GONE);
		}

		User.current.setCurrentKidUnseenPhotosBySeenPhotos(String.valueOf(event.seen_photos));
		MainActivity.updateDrawerContent();

		EventBus.getDefault().removeStickyEvent(event);
	}

	public static void updateAlbumTabBadgeUnseen(String unseen_photos)
	{
		TextView num = inst.mTabHost.getTabWidget().getChildAt(0).findViewById(R.id.num);
		num.setText(unseen_photos);
		
		RelativeLayout layout = inst.mTabHost.getTabWidget().getChildAt(0).findViewById(R.id.notify_layout);
		layout.setVisibility(View.VISIBLE);
	}
	
	public static void updateContactTabBadge()
	{
		TextView num = inst.mTabHost.getTabWidget().getChildAt(2).findViewById(R.id.num);
		String pending_parents = num.getText().toString();
		
		int pending_parents_int = Integer.valueOf(pending_parents) - 1;
		
		if(pending_parents_int > 0)
		{
			num.setText(String.valueOf(pending_parents_int));
		}		
		else
		{
			RelativeLayout layout = inst.mTabHost.getTabWidget().getChildAt(2).findViewById(R.id.notify_layout);
			layout.setVisibility(View.GONE);
		}		
	}

	private void initialiseTabHost(Bundle args) {

		mTabHost = _view.findViewById(android.R.id.tabhost);
		mTabHost.setup();
		TabInfo tabInfo = null;
		if (unattached_kid_mode) {   
			unattached_kid_mode = false;			
			int _layout;
			if (User.current.kidIsWaitingApproval()) {
				_layout = R.layout.kid_waiting_approval_fragment;
			} else {
				_layout = R.layout.unattached_kid_fragment;
			}
			View unattached = LayoutInflater.from(context).inflate(R.layout.unattached_kid_view, null, false);
			mTabHost.getTabWidget().setVisibility(View.GONE);
			__addTab(this, this.mTabHost, this.mTabHost.newTabSpec(UNATTACHED_TAB).setIndicator(unattached),  ( tabInfo = new TabInfo(UNATTACHED_TAB, UnattachedKidFragment.class, args)));
			this.mapTabInfo.put(tabInfo.tag, tabInfo);			
		}  
		else {

			mTabHost.getTabWidget().setVisibility(View.VISIBLE);
			View album = LayoutInflater.from(context).inflate(R.layout.album_view, null, false);

			
			__addTab(this, this.mTabHost, this.mTabHost.newTabSpec(TAB_1).setIndicator(album), ( tabInfo = new TabInfo(TAB_1, AlbumsFragment.class, args)));
			this.mapTabInfo.put(tabInfo.tag, tabInfo);
	
			View messView = LayoutInflater.from(context).inflate(R.layout.message_view, null, false);

			
			__addTab(this, this.mTabHost, this.mTabHost.newTabSpec(TAB_2).setIndicator(messView), ( tabInfo = new TabInfo(TAB_2, MessagesFragment.class, args)));
			this.mapTabInfo.put(tabInfo.tag, tabInfo);
	
			View contact = LayoutInflater.from(context).inflate(R.layout.contact_view, null, false);
			_contact = contact;
			
			__addTab(this, this.mTabHost, this.mTabHost.newTabSpec(TAB_3).setIndicator(contact), ( tabInfo = new TabInfo(TAB_3, ContactsFragment.class, args)));
			this.mapTabInfo.put(tabInfo.tag, tabInfo);


			View event = LayoutInflater.from(context).inflate(R.layout.event_view, null, false);
			_event = event;


			__addTab(this, this.mTabHost, this.mTabHost.newTabSpec(TAB_4).setIndicator(event), ( tabInfo = new TabInfo(TAB_4, EventsFragment.class, args)));
			this.mapTabInfo.put(tabInfo.tag, tabInfo);

		}


		mTabHost.setOnTabChangedListener(this);

	}


	public void checkContactTab() {
		if(User.isParent()) {
			if(User.current.getCurrentKidContactsForbidden()) {
				_contact.setVisibility(View.GONE);

			} else {
				_contact.setVisibility(View.VISIBLE);
			}
		}
	}


	private View initBadgeAlbumTab()
	{
		View album = LayoutInflater.from(context).inflate(R.layout.album_view, null, false);
		
		String unseen_photos = getAlbumTabBadge();
		if(unseen_photos != null && Integer.valueOf(unseen_photos) > 0)
		{
			RelativeLayout layout = (RelativeLayout)album.findViewById(R.id.notify_layout);
			layout.setVisibility(View.VISIBLE);
			
			TextView num = (TextView)album.findViewById(R.id.num);
			num.setText(unseen_photos);				
		}
		
		return album;
	}
	
	private void updateBadgeContactTab()
	{
		View view = inst.mTabHost.getTabWidget().getChildAt(2);
		if(view == null)
		{
			return;
		}
		RelativeLayout layout = (RelativeLayout)view.findViewById(R.id.notify_layout);

		String unseen_photos = getContactTabBadge();
		
		if(unseen_photos != null && Integer.valueOf(unseen_photos) > 0)
		{
			layout.setVisibility(View.VISIBLE);
			
			TextView num = (TextView)inst.mTabHost.getTabWidget().getChildAt(2).findViewById(R.id.num);
			num.setText(unseen_photos);	
		}
		else
		{
			layout.setVisibility(View.GONE);
		}
	}

	private void updateBadgeAlbumTab()
	{
		View view = inst.mTabHost.getTabWidget().getChildAt(0);
		if(view == null)
		{
			return;
		}
		RelativeLayout layout = (RelativeLayout)view.findViewById(R.id.notify_layout);

		if(layout == null)
		{
			return;
		}
		
		String unseen_photos = getAlbumTabBadge();
		
		if(unseen_photos != null && Integer.valueOf(unseen_photos) > 0)
		{
			layout.setVisibility(View.VISIBLE);
			
			TextView num = (TextView)inst.mTabHost.getTabWidget().getChildAt(0).findViewById(R.id.num);
			num.setText(unseen_photos);	
		}
		else
		{
			layout.setVisibility(View.GONE);
		}
	}
	
	private void updateBadgeMessageTab()
	{
		View view = inst.mTabHost.getTabWidget().getChildAt(1);
		if(view == null)
		{
			return;
		}
		RelativeLayout layout = (RelativeLayout)view.findViewById(R.id.notify_layout);
		
		String unread_messages = getMessageTabBadge();
		if(unread_messages != null && Integer.valueOf(unread_messages) > 0)
		{
			layout.setVisibility(View.VISIBLE);
			
			TextView num = (TextView)inst.mTabHost.getTabWidget().getChildAt(1).findViewById(R.id.num);
			num.setText(unread_messages);				
		}
		else
		{
			layout.setVisibility(View.GONE);
		}
	}
	
	public View initBadgeMessageTab()
	{
		View messView = LayoutInflater.from(context).inflate(R.layout.message_view, null, false);

		String unread_messages = null;

		try {

			unread_messages = getMessageTabBadge();
		} catch (Exception e) {

			Log.e(TAG, "initBadgeMessageTab: error = " + Log.getStackTraceString(e));
		}

		if(unread_messages != null && Integer.valueOf(unread_messages) > 0)
		{
			RelativeLayout layout = (RelativeLayout)messView.findViewById(R.id.notify_layout);
			layout.setVisibility(View.VISIBLE);
			
			TextView num = (TextView)messView.findViewById(R.id.num);
			num.setText(unread_messages);				
		}
		
		return messView;
	}
	
	private View initBadgeContactTab()
	{
		View contView = LayoutInflater.from(context).inflate(R.layout.contact_view, null, false);
		
		String pending_parents = getContactTabBadge();
		if(pending_parents != null && Integer.valueOf(pending_parents) > 0)
		{
			RelativeLayout layout = (RelativeLayout)contView.findViewById(R.id.notify_layout);
			layout.setVisibility(View.VISIBLE);
			
			TextView num = (TextView)contView.findViewById(R.id.num);
			num.setText(pending_parents);				
		}
		
		return contView;
	}


	/////////////

	private static void __addTab(MainScreenFragment fragment, TabHost tabHost, TabHost.TabSpec tabSpec, TabInfo tabInfo) {
		Activity activity = fragment.activity(); 
		tabSpec.setContent(new TabFactory(activity)); 
		tabHost.addTab(tabSpec);
	}

	
	public static void forceCurrentTabToSetTitlebar() {
//		if (inst != null) {
//			inst.forceSetTitlebar();
//		}


	}
	
	public static void forceCurrentTabToRefresh() {
		if (inst != null) {
			inst.forceCurrentToRefresh();
		}		
	}
	
	public String getAlbumTabBadge()
	{
		return User.current.getCurrentKidUnseenPhotos();
	}
	
	public String getContactTabBadge()
	{
		return User.current.getCurrentClassPendingParents();
	}
	
	public String getMessageTabBadge()
	{
		return User.current.getCurrentKidUnreadMessage();
	}
	 
	private void forceSetTitlebar() {
		if (User.current.kidWithoutGan()) {
			UnattachedKidFragment.setFragmentVisible();
		}
		int index = mViewPager.getCurrentItem();

		if (index == ALBUM_IND) {

		} else if (index == MESSAGE_IND) {
			MessageListFragment.setFragmentVisible();
		} else if (index == CONTACT_IND) {
			ContactListFragment.setFragmentVisible();
		} else if (index == EVENT_IND) {
			EventListFragment.setFragmentVisible();
		}else {
			int jj=234;
			jj++;
		}
	}

	private void forceCurrentToRefresh() {
		if (User.current.kidWithoutGan()) {
			return;
		}
		int index = mViewPager.getCurrentItem();
		if (index == ALBUM_IND) {
			AlbumListFragment.forceContentRefresh();
		} else if (index == MESSAGE_IND) {
			MessageListFragment.forceContentRefresh();			
		} else if (index == CONTACT_IND) {
			ContactListFragment.forceContentRefresh();			
		} else if (index == EVENT_IND) {
			EventListFragment.forceContentRefresh();
		}else {
			int jj=234;
			jj++;
		}
	}

	public static void forceCurrentTabToSetTitleVisibility(int visiblity) {

		int index = getCurrentItemIndex();

		if(index == -1)
		{
			return;
		}

		if (index == ALBUM_IND) {
			AlbumListFragment.setTitleVisibility(visiblity);
		} else if (index == MESSAGE_IND) {
			MessageListFragment.setTitleVisibility(visiblity);			
		} else if (index == CONTACT_IND) {
			ContactListFragment.setTitleVisibility(visiblity);			
		} else {
			int jj=234;
			jj++;
		}
	}

	public static int getCurrentItemIndex()
	{
		if (inst == null || inst.mViewPager == null) {
			return -1;
		}

		if (User.current.kidWithoutGan()) {
			return -1;
		}
		int index = inst.mViewPager.getCurrentItem();

		return index;
	}
	
	public static void initTabHost()
	{
		if (inst == null || inst.mViewPager == null) {
			return;
		}
		
		if(User.isParent())
		{
			inst.updateBadgeAlbumTab();
			inst.updateBadgeMessageTab();
		}
		else
		{
			inst.updateBadgeContactTab();
		}
	}
	
	public static void initAlbumTabBadge()
	{
		if (inst == null || inst.mViewPager == null) {
			return;
		}
		
		inst.updateBadgeAlbumTab();
	}
	
	public static void initContactTabBadge()
	{
		if (inst == null || inst.mViewPager == null) {
			return;
		}
		
		inst.updateBadgeContactTab();
	}

	public static void setCurrentTab(int itemIndex) {
		if (inst == null || inst.mViewPager == null) {
			return;
		}
		inst.mViewPager.setCurrentItem(itemIndex, true);

	}
}

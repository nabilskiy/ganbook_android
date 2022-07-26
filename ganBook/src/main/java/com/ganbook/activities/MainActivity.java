package com.ganbook.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.ganbook.amazinglist.special.CustomToast;
import com.ganbook.app.MyApp;
import com.ganbook.communication.CommConsts;
import com.ganbook.communication.ICompletionHandler;
import com.ganbook.communication.datamodel.ResultObj;
import com.ganbook.communication.json.GetUserKids_Response;
import com.ganbook.communication.json.InstitutionLogoResponse;
import com.ganbook.communication.json.getclass_Response;
import com.ganbook.communication.json.transmitter.JsonTransmitter;
import com.ganbook.dialogs.Dialogs;
import com.ganbook.fragments.ContactListFragment;
import com.ganbook.fragments.FragmentType;
import com.ganbook.fragments.MainScreenFragment;
import com.ganbook.fragments.MessageListFragment;
import com.ganbook.fragments.SingleAlbumFragment;
import com.ganbook.fragments.UnattachedKidFragment;
import com.ganbook.fragments.tabs.KidWithoutGanFragment;
import com.ganbook.gcm.PushMsgPayload;
import com.ganbook.gcm.PushRepository;
import com.ganbook.interfaces.TitleIteractionListener;
import com.ganbook.models.events.RefreshDrawerEvent;
import com.ganbook.models.events.SelectDrawerItemEvent;
import com.ganbook.services.UploadInstitutionLogoService;
import com.ganbook.services.WarningsService;
import com.ganbook.share.ShareManager;
import com.ganbook.sharedprefs.SPReader;
import com.ganbook.sharedprefs.SPWriter;
import com.ganbook.ui.CircleImageView;
import com.ganbook.ui.DrawerAdapter;
import com.ganbook.ui.NavDrawerItem;
import com.ganbook.user.NameAndId;
import com.ganbook.user.User;
import com.ganbook.utils.FragmentUtils;
import com.ganbook.utils.StrUtils;

import com.parse.ParseAnalytics;
import com.project.ganim.R;
import com.squareup.picasso.Picasso;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
public class MainActivity extends BaseAppCompatActivity implements OnClickListener, OnRefreshListener,
		 FragmentManager.OnBackStackChangedListener, TitleIteractionListener {


	String TAG = MainActivity.class.getName();
	public static MainActivity inst;
	public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;

	private static final long DRAWER_SELF_REFRESH_INTERVAL = 5*60*1000;

	private long g_lastDrawerRefresh = System.currentTimeMillis();
	
	private boolean fromRefresh = false;


	private DrawerLayout drawer_layout;
	private ListView drawer_childlistView;
	private DrawerAdapter drawer_childlistAdapter;
	private ArrayList<NavDrawerItem> drawer_listItems;


	private TextView drawer_line1, drawer_line2, drawer_line3, addKidSidebarText;
	private ImageView logoImage;

	private static WarningsService warningsService = null;

	private SwipeRefreshLayout mSwipeRefreshLayout;
	private String logoFileName;
	private boolean isDrawerOpened = false;
	private CircleImageView profile_image;
	private static final int LOAD_IMAGE_RESULTS = 1;
	private ImageView settingsMenu, inviteParentsMenu, addKidMenu, favoritesMenu;
	private RelativeLayout assignPtaLayout, addKidLayout;

	@Override
    protected void onPause() {
        super.onPause();
		if (User.isParent())
			warningsService.setBackground(true);
    }

    public static void refresh()
	{
		if (inst != null) {
			inst.fromRefresh = true;
			inst.restartActivity();
		}	
	}
	
	public static void openDrawer()
	{
		if (inst != null) {
			inst.drawer_layout.openDrawer(Gravity.LEFT);
		}
	}

	private  boolean checkAndRequestPermissions() {
		int camera = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA);
		int storage = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
		int loc = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION);
		int loc2 = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
		int microphone = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
		List<String> listPermissionsNeeded = new ArrayList<>();

		if (camera != PackageManager.PERMISSION_GRANTED) {
			listPermissionsNeeded.add(android.Manifest.permission.CAMERA);
		}
		if (storage != PackageManager.PERMISSION_GRANTED) {
			listPermissionsNeeded.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
		}
		if (loc2 != PackageManager.PERMISSION_GRANTED) {
			listPermissionsNeeded.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
		}
		if (loc != PackageManager.PERMISSION_GRANTED) {
			listPermissionsNeeded.add(android.Manifest.permission.ACCESS_COARSE_LOCATION);
		}
		if(microphone != PackageManager.PERMISSION_GRANTED) {
			listPermissionsNeeded.add(Manifest.permission.RECORD_AUDIO);
		}
		if (!listPermissionsNeeded.isEmpty())
		{
			ActivityCompat.requestPermissions(this,listPermissionsNeeded.toArray
					(new String[listPermissionsNeeded.size()]),REQUEST_ID_MULTIPLE_PERMISSIONS);
			return false;
		}
		return true;
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main_screen);
		SplashActivity.callFinish();

		overridePendingTransition(R.anim.fadein, R.anim.fadeout);

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


		MyApp.sendAnalytics("main-ui", "main-ui-"+User.getId(), "main-ui", "Main");


		MainActivity _this = MainActivity.this;

		User usr = User.blocking_loadFromLocalCache(false);
		if (usr == null) {
			CustomToast.show(_this, R.string.error_loading_user);
			_this.finish();
			return;
		}

		MainScreenFragment.unattached_kid_mode = false;

		if (User.current.kidWithoutGan()) {

			MainScreenFragment.unattached_kid_mode = true;
		}

		if (savedInstanceState == null) {
			_SetCurrentView(0);
		}

		inst = _this;

		setActionBar(StrUtils.generateTitle(), true);

		closeRegistrationScreens();

		drawer_layout = (DrawerLayout) findViewById(R.id.drawer_layout);
		drawer_childlistView = (ListView) findViewById(R.id.drawer_childlistView);
		drawer_childlistView.setChoiceMode(ListView.CHOICE_MODE_SINGLE); //!

		updateDrawerKidsOrClassesList(true);
		if (User.isParent())
		{
			if (warningsService == null) {
				warningsService = new WarningsService();
			}

			warningsService.start(this);
		}

		drawer_childlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				boolean wasHandledAsSpecial = drawer_childlistAdapter.handleSpecialType(position);
				if (wasHandledAsSpecial) {
					drawer_layout.closeDrawers();
				}
				else {
					drawer_handleItemClicked(view, position);
				}


			}
		});
		drawer_childlistView.setScrollContainer(false);

		ScrollView scroll = (ScrollView) findViewById(R.id.scroll);
		scroll.setOnTouchListener(new OnTouchListener() {

			@SuppressLint("ClickableViewAccessibility")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN);
				int action = event.getAction();
				switch (action) {
					case MotionEvent.ACTION_DOWN:
						v.getParent().requestDisallowInterceptTouchEvent(true);
						break;

					case MotionEvent.ACTION_UP:
						v.getParent().requestDisallowInterceptTouchEvent(false);
						break;
				}
				v.onTouchEvent(event);
				return true;
			}
		});
		drawer_line1 = (TextView) findViewById(R.id.drawer_type);
		drawer_line2 = (TextView) findViewById(R.id.drawer_email);
		drawer_line3 = (TextView) findViewById(R.id.gan_code);

		logoImage = findViewById(R.id.logoImage);
		settingsMenu = findViewById(R.id.settings_sidebar);
		inviteParentsMenu = findViewById(R.id.invite_parents_sidebar);
		addKidMenu = findViewById(R.id.add_kid_sidebar);
		favoritesMenu = findViewById(R.id.favorites_sidebar);
		addKidSidebarText = findViewById(R.id.add_kid_sidebar_text);

		assignPtaLayout = findViewById(R.id.assign_pta_sidebar_layout);
		addKidLayout = findViewById(R.id.add_kid_layout);

		settingsMenu.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				openSettingsScreen();
			}
		});

		inviteParentsMenu.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				InviteParent();
			}
		});

		addKidMenu.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				openAddClassScreen();
			}
		});

		favoritesMenu.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				openFavoritesScreen();
			}
		});

		profile_image = (CircleImageView) findViewById(R.id.profile_image);

        if(User.isTeacher()) {
			assignPtaLayout.setVisibility(View.VISIBLE);
			addKidSidebarText.setText(getString(R.string.add_class_sidemenu));
            logoImage.setVisibility(View.VISIBLE);
            logoImage.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent photoPickerIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(photoPickerIntent, LOAD_IMAGE_RESULTS);
                }
            });

            assignPtaLayout.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					openPtaScreen();
				}
			});
        } else {
			assignPtaLayout.setVisibility(View.GONE);
			logoImage.setVisibility(View.GONE);
        	if(User.isStudent()) {
				addKidLayout.setVisibility(View.GONE);
        		inviteParentsMenu.setVisibility(View.VISIBLE);
        		favoritesMenu.setVisibility(View.VISIBLE);
			} else if(User.isStaff()) {
				addKidLayout.setVisibility(View.VISIBLE);
				addKidSidebarText.setText("Add staff");
			} else {
				addKidSidebarText.setText(getString(R.string.add_kid));
			}
        }

		getInstitutionLogo();

		checkAndRequestPermissions();

		final ActionBarDrawerToggle mDrawerToggle;
		final ActionBar actionBar = getSupportActionBar();

		if (actionBar != null) {
			actionBar.setDisplayHomeAsUpEnabled(true);
			mDrawerToggle = new ActionBarDrawerToggle(this, drawer_layout, R.string.hello_world, R.string.hello_world) {


				@Override
				public void onDrawerClosed(View arg0) {
					isDrawerOpened = false;
					drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);

					MainScreenFragment.forceCurrentTabToSetTitleVisibility(View.VISIBLE);


					if (User.current.kidWithoutGan()) {
						if (KidWithoutGanFragment.inst != null) {
							refreshKidWithoutGanFragment(KidWithoutGanFragment.inst);
						} else {
							restartActivity();
						}
					} else if (KidWithoutGanFragment.inst != null) {
						restartActivity();
					} else {
						MainScreenFragment.initTabHost();
					}

				}

				@Override
				public void onDrawerOpened(View arg0) {
					isDrawerOpened = true;
					drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN);
					mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.drawerRefreshwe);
					mSwipeRefreshLayout.setRefreshing(false);
					MainScreenFragment.forceCurrentTabToSetTitleVisibility(View.GONE);

					mSwipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
						@Override
						public void onRefresh() {
							drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN);
							if (User.isTeacher()) {
								call_getclass();
                                getInstitutionLogo();
								mSwipeRefreshLayout.setRefreshing(false);
							} else {
								call_getUserKids();
								mSwipeRefreshLayout.setRefreshing(false);
							}
						}
					});
					if (!fromRefresh) {
						g_lastDrawerRefresh = new SPReader("DRAWER_REFRESH").getLong("DRAWER_REFRESH", 0);

						long now = System.currentTimeMillis();

					} else {
						fromRefresh = false;
					}

				}

				@Override
				public boolean isDrawerSlideAnimationEnabled() {
					return false;
				}

				@Override
				public void onDrawerSlide(View drawerView, float slideOffset) {
					if(slideOffset > .55 && !isDrawerOpened){
						onDrawerOpened(drawerView);
						isDrawerOpened = true;
					} else if(slideOffset < .45 && isDrawerOpened) {
						onDrawerClosed(drawerView);
						isDrawerOpened = false;
					}

				}

				@Override
				public void onDrawerStateChanged(int arg0) {

				}

			};
			mDrawerToggle.setDrawerIndicatorEnabled(true);
			drawer_layout.addDrawerListener(mDrawerToggle);
			mDrawerToggle.syncState();

		}

		if (User.isParent()) {
			setParentDrawerTitle();
		} else {
			setTeacherDrawerTitle();
		}

		Log.i("noanoa",  ""+savedInstanceState);

		fragmentManager.addOnBackStackChangedListener(this);

		getSupportActionBar().setDisplayShowTitleEnabled(false);

	}

	

	private void setParentDrawerTitle() {
//		String g_name = StrUtils.emptyIfNull(User.current.getCurrentGanName());
		String u_name = StrUtils.emptyIfNull(User.getName());
		String email = StrUtils.emptyIfNull(User.getEmail());
		drawer_line1.setText(u_name);		
		drawer_line2.setText(email);
		drawer_line3.setVisibility(View.GONE);
	}


	private void setTeacherDrawerTitle() {
		String g_name = StrUtils.emptyIfNull(User.current.getCurrentGanName());
		drawer_line1.setText(g_name);		
		String u_name = StrUtils.emptyIfNull(User.getName());
		drawer_line2.setText(u_name);
		drawer_line3.setVisibility(View.VISIBLE);
		String _codePref = getResources().getString(R.string.kindergarten_code_pref);
		String _ganCode = StrUtils.emptyIfNull(User.current.getCurrentGanCode());
		drawer_line3.setText(_codePref + " " + _ganCode);
	}


	@Override
	public void onRefresh() { // refresh of drawer!
		refresh_drawer();
	}
	
	

	public void call_getUserKids() {
		final MainActivity a = this;
		String parent_id = User.getId();
		JsonTransmitter.send_getuserkids(parent_id, new ICompletionHandler() {					
			@Override
			public void onComplete(ResultObj result) {
//				drawer_mSwipeRefreshLayout.setRefreshing(false); //!
				stopProgress();
				if (!result.succeeded) {
					if(JsonTransmitter.NO_NETWORK_MODE.equals(result.result))
					{
						a.showNotInternetAlert();
						return;
					}
					else if (CommConsts.EMPTY_RESULT.equals(result.result))
					{
						start(RegistrationSucceededActivity.class);
						finish();
					}
					String errmsg = result.result;
					CustomToast.show(a, errmsg);
					return;
				}
				int num = result.getNumResponses();
				GetUserKids_Response[] responses = new GetUserKids_Response[num]; 
				for (int i = 0; i < num; i++) {
					responses[i] = (GetUserKids_Response) result.getResponse(i);
				}
				User.updateWithUserkids(responses);
				/*updateDrawerKidsOrClassesList(false);
				initUI();
				if(!isDrawerOpened) {
					refresh();
				}*/
			}
		});
	}


	private void initUI() {
		MainScreenFragment.forceCurrentTabToRefresh();
		MainScreenFragment.initTabHost();

		setTitle(StrUtils.generateTitle());
	}


	private void call_getclass() {
		final Activity a = this;
		String teacher_id = User.getId();
		JsonTransmitter.send_getclass(teacher_id, new ICompletionHandler() {					
			@Override
			public void onComplete(ResultObj result) {
				stopProgress();
				if (!result.succeeded) {
					if(JsonTransmitter.NO_NETWORK_MODE.equals(result.result))
					{
						showNotInternetAlert();
						mSwipeRefreshLayout.setRefreshing(false);
						return;
					}
					String errmsg = result.result;
					CustomToast.show(a, errmsg);
					return;
				}
				getclass_Response response = (getclass_Response) result.getResponse(0);   
				User.updateWithClasses(response);

				initUI();
			}
		});
	}


	public static void updateDrawerContent() {
		if (inst != null) {
			inst.updateDrawerKidsOrClassesList(false);
		}		
	}

	private void updateDrawerKidsOrClassesList(boolean created) {
		NameAndId[] kidOrClassArr = getKidsOrClasses(); 
		TypedArray navMenuIcons = getResources().obtainTypedArray(R.array.icons);		    		
		populatedrawerList(kidOrClassArr, navMenuIcons); // drawer_listItems 		 		
		navMenuIcons.recycle();		
		__setDrawerAdapter();  
		
		if (created) {
			drawer_childlistView.setAdapter(drawer_childlistAdapter);
		}
		else
		{
			if(User.isTeacher())
			{
				setTeacherDrawerTitle();
			}

			drawer_childlistView.setAdapter(drawer_childlistAdapter);
		}
		
	}


	private void populatedrawerList(NameAndId[] kidOrClassArr, TypedArray navMenuIcons) {
		drawer_listItems = new ArrayList<NavDrawerItem>();
		if (kidOrClassArr != null) {
			int ctr = 0;
			for (NameAndId elem: kidOrClassArr) { 
				drawer_listItems.add(new NavDrawerItem(ctr, elem.name, elem, navMenuIcons.getResourceId(0, -1))); 
				ctr++;
			}
		}
	}

	private void getInstitutionLogo() {
        Call<List<InstitutionLogoResponse>> institutionLogoCall = ganbookApiInterfaceGET.getKindergartenLogo(User.current.getCurrentGanId());

        institutionLogoCall.enqueue(new Callback<List<InstitutionLogoResponse>>() {
            @Override
            public void onResponse(Call<List<InstitutionLogoResponse>> call, Response<List<InstitutionLogoResponse>> response) {
                if(response.body() != null) {
					if(response.body().get(0).getKindergartenLogo() != null){
						logoFileName = response.body().get(0).getKindergartenLogo();
						Picasso.with(MainActivity.this).load("http://s3.ganbook.co.il/ImageStore/users/" + logoFileName)
								.centerCrop()
								.fit()
								.into(logoImage);
					} else {
						Picasso.with(MainActivity.this).load(R.drawable.teacher_profile_image)
								.centerCrop()
								.fit()
								.into(logoImage);

					}

                }

            }

            @Override
            public void onFailure(Call<List<InstitutionLogoResponse>> call, Throwable t) {
                Dialogs.errorDialogWithButton(MainActivity.this, getString(R.string.error), getString(R.string.institution_logo_error_text), getString(R.string.ok));
            }
        });
    }
	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem) {
			if (menuItem.getItemId() == android.R.id.home) {

			if (fragmentManager.getBackStackEntryCount() == 1) {

				if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
					drawer_layout.closeDrawers();
				} else {
					drawer_layout.openDrawer(GravityCompat.START);

				}
			} else {

				FragmentUtils.popBackStack(MainActivity.this);
			}
		}
		return super.onOptionsItemSelected(menuItem);
	}

	private void __setDrawerAdapter() {
		if (drawer_listItems != null) {
			drawer_childlistAdapter = new DrawerAdapter(getApplicationContext(), drawer_listItems);
		}
	}


	private NameAndId[] getKidsOrClasses() {
		if (User.isParent()) {
			return User.current.getKidNameArray(); 
		} else {
			return User.current.getClassNameIdArray();
		}
	}


	@Override
	protected void onDestroy() {
		super.onDestroy();

		if (this == inst) {
			inst = null;
		}
		overridePendingTransition(R.anim.push_down_in, R.anim.push_down_out);

		EventBus.getDefault().unregister(this);

		if (warningsService != null)
			warningsService.stop();
	}

	@Override
	public void onStart() {
		super.onStart();
		if (!EventBus.getDefault().isRegistered(this)) {
			EventBus.getDefault().register(this);
		}

	}


	
	private void closeRegistrationScreens() {
		EntryScreenActivity.callFinish();
		LoginActivity.callFinish();
		EnterEmailActivity.callFinish();
		SignupActivity.callFinish();
		ChooseClassActivity.callFinish();
		EnterCodeActivity.callFinish();
		AddKidActivity.callFinish();
		ParentDetailsActivity.callFinish();
		RegistrationSucceededActivity.callFinish();
		KindergartenDetailsActivity.callFinish();
	}


	// **********************************************************************************************************************//

	@Override
	protected void onResume() {  
		super.onResume();
		handle_activation_from_notif();

		if (User.isParent()) {
			warningsService.setBackground(false);
			warningsService.showScheduledMessage();
		}
	}


	private void handle_activation_from_notif() {
		PushMsgPayload msg = PushRepository.extractActiveNotif();
		if (msg == null) {
			return;
		}

		ParseAnalytics.trackAppOpenedInBackground(getIntent());

		Log.d("MSG LOC KEY", msg.loc_key);

		if (PushMsgPayload.WAIT_CLASS_APRVL.equals(msg.loc_key))
		{
			call_handleItem_position_tab(msg.class_id, User.isTeacher(), MainScreenFragment.CONTACT_IND);

		}
		else if (PushMsgPayload.CONFIRM_CLASS.equals(msg.loc_key))
		{
			call_handleItem_position_tab(msg.class_id, User.isTeacher(), MainScreenFragment.ALBUM_IND);
		}
		else if (PushMsgPayload.MSG.equals(msg.loc_key) || PushMsgPayload.MSG_PTA.equals(msg.loc_key))
		{
			call_handleItem_position_tab(msg.class_id, User.isTeacher(), MainScreenFragment.MESSAGE_IND);
		}
		else if (PushMsgPayload.NEW_PICS_UPLD.equals(msg.loc_key) || PushMsgPayload.NEW_PICS_UPLD_1.equals(msg.loc_key) || PushMsgPayload.NEW_VID_UPLD.equals(msg.loc_key))
		{
			call_handleItem_position_tab(msg.class_id, User.isTeacher(), MainScreenFragment.ALBUM_IND);
		}
		else if (PushMsgPayload.EVNT.equals(msg.loc_key) || PushMsgPayload.EVNT_BD.equals(msg.loc_key))
		{
			call_handleItem_position_tab(msg.class_id, User.isTeacher(), MainScreenFragment.EVENT_IND);
		}
		else {
			int jj=234;
			jj++;
		}
	}

	private void call_handleItem_position_tab(String class_id, boolean isTeacher, int tabInd) {

		if (StrUtils.isEmpty(class_id)) return;

		int position;
		if(isTeacher)
		{
			position = User.getClassPosition(class_id);
		}
		else
		{
			position = User.getKidPosition(class_id);
		}

		if (position > -1) {

			drawer_handleItemClicked(null, position);


			MainScreenFragment.setCurrentTab(tabInd);
		}
	}

	private void drawer_handleItemClicked(View view, int position) {
		drawer_childlistView.setAdapter(drawer_childlistAdapter); // force data refresh

		User.current.clearAlbumList(); // force album list refresh
		MessageListFragment.clearMessages();
		ContactListFragment.clearContacts();

		if (User.isParent()) {
			User.current.setCurrentKidInd(position);
		} else {
			User.current.setCurrentClassInd(position);
		}

		//this magic line of code will help us to rid off garbage code of this class :)
		String newTitle = StrUtils.generateTitle();
		EventBus.getDefault().post(new SelectDrawerItemEvent(true, newTitle));
		setTitle(newTitle);

//		MainScreenFragment.forceCurrentTabToRefresh();
		MainActivity.refresh();
		__setDrawerAdapter();

		drawer_layout.closeDrawers();
	}

	private void _SetCurrentView(int _pos) {

		switch (_pos) {
			case 0:
				cur_fragment = moveToTab(FragmentType.Main);
				break;

			default:
				break;
		}
	} 
	
	@Override
	public void onClick(View v) {
    	int size = drawer_layout.getChildCount();

		switch (v.getId()) {
		case R.id.menu:
			Log.d("MENU CLICKED", "CLICKED");

			if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
				drawer_layout.closeDrawers();
			} else {
				drawer_layout.openDrawer(GravityCompat.START);
			}
			break;
			
		default:
			break;
		}
	}

	
	private String getInvitationBody() {
		if (User.current == null || User.current.classList==null) {
			return null;
		}
		String gan_code = User.current.classList.gan_code;
		String pref = getResources().getString(R.string.invitation_body_part_1); 
		String post = getResources().getString(R.string.invitation_body_part_2);
		return pref +  " " + gan_code + post;
	}


	@Override
	public void onBackPressed() {
		
		FragmentManager fragmentManager = getSupportFragmentManager();

		Log.i("noanoa",  "fragmentManager.getBackStackEntryCount() "+fragmentManager.getBackStackEntryCount());


		if ( (drawer_layout) != null && (drawer_layout.isDrawerOpen(GravityCompat.START) || drawer_layout.isDrawerOpen(GravityCompat.END))) {
			drawer_layout.closeDrawers();
		} 
		else { 
			if (fragmentManager != null && (fragmentManager.getBackStackEntryCount() == 1))
				{
					Log.i("noanoa",  "fragmentManager.getBackStackEntryCount() "+fragmentManager.getBackStackEntryCount());
					MainScreenFragment.forceCurrentTabToSetTitlebar();

					finish();
				}
				super.onBackPressed();

			}
	}



	@Override
	public void onBackStackChanged() {

		Log.d(TAG, "onBackStackChanged: stack count === " + fragmentManager.getBackStackEntryCount());

		Drawable upArrow = null;

		if (fragmentManager != null && (fragmentManager.getBackStackEntryCount() == 1)) {
			setTitle(StrUtils.generateTitle());
			upArrow = getResources().getDrawable(R.drawable.hamburger);
		} else {
			upArrow = getResources().getDrawable(R.drawable.arrow_back);
		}

		upArrow.setColorFilter(getResources().getColor(R.color.text_color), PorterDuff.Mode.SRC_ATOP);
		getSupportActionBar().setHomeAsUpIndicator(upArrow);
	}


	@Override
	protected void onActivityResult(int reqCode, int resultCode, Intent data) {
		super.onActivityResult(reqCode, resultCode, data);

		if (resultCode == RESULT_OK && reqCode == LOAD_IMAGE_RESULTS) {
				final Uri imageUri = data.getData();

                Picasso.with(MainActivity.this).load(imageUri)
						.centerCrop()
						.fit()
						.into(logoImage);
                String logoPath = getRealPathFromURI(imageUri, this);
                String logoName = String.valueOf(System.currentTimeMillis());

                Intent intent = new Intent(MainActivity.this, UploadInstitutionLogoService.class);
                intent.putExtra(UploadInstitutionLogoService.EXTRA_LOGO_NAME, logoName);
                intent.putExtra(UploadInstitutionLogoService.EXTRA_LOGO_PATH, logoPath);
                startService(intent);
		}
	}

	private String getRealPathFromURI(Uri uri, Context cnt) {
		String result = "";
		System.out.println("getactivity" + cnt.toString());
		Cursor cursor = cnt.getContentResolver().query(uri, null, null, null,
				null);
		if (cursor == null) {
			result = uri.getPath();
			System.out.println(result);
		}
		else {
			cursor.moveToFirst();
			int idx = cursor
					.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
			result = cursor.getString(idx);
		}
		return result;
	}


	public static void stop() {
		if (inst != null) {
			inst.finish();
		}
	}


	public static void openAddClassScreen() {
		if (inst == null) {
			return;
		}
		if (User.isParent()) {

			MyApp.sendAnalytics("menu-add-kid-ui", "menu-add-kid-ui"+ User.getId(), "menu-add-kid-ui", "MenuAddKid");

			inst.startActivity(new Intent(inst, AddKidActivity.class)); 
		} else {

			MyApp.sendAnalytics("menu-add-class-ui", "menu-add-class-ui"+ User.getId(), "menu-add-class-ui", "MenuAddClass");

			inst.startActivity(new Intent(inst, AddClassActivity.class));
		}		
	}

	public static void openFavoritesScreen() {
		if (inst == null) {
			return;
		}

		inst.startActivity(new Intent(inst, FavoriteActivity.class));

	}
	
	public static void openPtaScreen() {
		if (inst == null) {
			return;
		}
		if (User.isParent()) {
//			inst.cur_fragment = inst.moveToTab(FragmentType.Add_Kid);
//			inst.startActivity(new Intent(inst, AddKidActivity.class)); 
		} else {
			inst.startActivity(new Intent(inst, PtaActivity.class));
		}	
	}

	public static void openPickDrop() {
		if ((inst == null) || User.isTeacher()) {
			return;
		}

		inst.startActivity(new Intent(inst, PickDropActivity.class));
	}
 

	public static void InviteParent() {
		if (inst == null) {
			return;
		}
		String _body = inst.getInvitationBody();
		if (_body == null) {
			inst.startActivity(new Intent(inst, InviteParentsActivity.class));
		}
		else
		{
			ShareManager.openShareMenu(inst, R.string.invitation_subject,
					-1, _body, null, null);
		}		
	}


	public static void openSettingsScreen() {
		if (inst == null) {
			return;
		}

		inst.startActivity(new Intent(inst, SettingsActivity.class));
        inst.overridePendingTransition(R.anim.fadein, R.anim.fadeout);
	}

	public static void updateUserName(String new_name) {
		if (inst != null && inst.drawer_line1 != null) {
			if(User.isParent())
			{
				inst.drawer_line1.setText(new_name);
			}
			else
			{
				inst.drawer_line2.setText(new_name);
			}
		}		
	}


	public static void updateClassList() {
		if (inst != null) {
			inst.updateDrawerKidsOrClassesList(false);
		}		
	}
	
	public void refresh(View view)
	{
		refreshApp();
	}

	public static void refreshApp()
	{
		if(inst == null)
		{
			return;
		}

		long lastRefreshTime = new SPReader("DRAWER_REFRESH").getLong("DRAWER_REFRESH", 0);

		long now = System.currentTimeMillis();

		if (now - lastRefreshTime <= DRAWER_SELF_REFRESH_INTERVAL) {
			return;
		}

		inst.g_lastDrawerRefresh = now;
		new SPWriter("DRAWER_REFRESH").putLong("DRAWER_REFRESH", inst.g_lastDrawerRefresh).commit();

//		inst.startProgress(R.string.operation_proceeding);
		inst.refresh_drawer();
	}

	@Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
	public void onChangedClassEvent(RefreshDrawerEvent refreshDrawerEvent){

		Log.d(TAG, "onChangedClassEvent: received refresh drawer " + refreshDrawerEvent);

		refresh_drawer();
		updateDrawerContent();

		EventBus.getDefault().removeStickyEvent(refreshDrawerEvent);
	}


	private void refresh_drawer() {

		if(mSwipeRefreshLayout != null) {
			if (User.isTeacher()) {
				mSwipeRefreshLayout.setRefreshing(false);
				call_getclass();
			} else {
				mSwipeRefreshLayout.setRefreshing(false);
				call_getUserKids();
			}
		}

		int jj=234;
		jj++;
	}

	
	public void refresh_Unattached_Kid_Fragment(UnattachedKidFragment currentFragment) {
		if (fragmentManager==null) {
			return;
		}
		if (currentFragment==null) {
			currentFragment = UnattachedKidFragment.inst;
			if (currentFragment==null) {
				return;
			}
		}
		FragmentTransaction fragTransaction = fragmentManager.beginTransaction();
		fragTransaction.detach(currentFragment);
		fragTransaction.attach(currentFragment);
		fragTransaction.commit();	
	}

	public void refreshKidWithoutGanFragment(KidWithoutGanFragment currentFragment) {
		if (fragmentManager==null) {
			return;
		}
		if (currentFragment==null) {
			currentFragment = KidWithoutGanFragment.inst;
			if (currentFragment==null) {
				return;
			}
		}
		FragmentTransaction fragTransaction = fragmentManager.beginTransaction();
		fragTransaction.attach(currentFragment).commitAllowingStateLoss();
	}


	public void restartActivity() {
		finish();
		overridePendingTransition( 0, 0);
		startActivity(getIntent());
		overridePendingTransition( 0, 0);
	}

	public static void refreshSingleAlbumView(final String album_id) {
		MyApp.runOnUIThread(new Runnable() {			
			@Override
			public void run() {
				if (inst != null) {
					SingleAlbumFragment.refreshSingleAlbumView(album_id);
				}
			}
		});		
	}
	
	public static void showCameraButton() {
		MyApp.runOnUIThread(new Runnable() {			
			@Override
			public void run() {
				if (inst != null) {
					SingleAlbumFragment.showCameraButton();
				}
			}
		});		
	}
	
	public static void hideCameraButton() {
		MyApp.runOnUIThread(new Runnable() {			
			@Override
			public void run() {
				if (inst != null) {
					SingleAlbumFragment.hideCameraButton();
				}
			}
		});		
	}

	@Override
	public void setTitle(String text) {

		if(text.contains("null")) {
			text = text.replace("null", "");
		}
		setToolbarTitle(text);
	}

//	@Override
//	public void onEmojiconClicked(Emojicon emojicon) {
//		EmojiconsFragment.input(MessageListFragment.mEditEmojicon, emojicon);
//	}
//
//	@Override
//	public void onEmojiconBackspaceClicked(View v) {
//		EmojiconsFragment.backspace(MessageListFragment.mEditEmojicon);
//	}
}

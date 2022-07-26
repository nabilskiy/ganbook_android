package com.ganbook.fragments.tabs;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.ganbook.activities.AddAlbumActivity;
import com.ganbook.activities.CustomAdsActivity;
import com.ganbook.activities.SplashActivity;
import com.ganbook.adapters.TabAlbumsAdapter;
import com.ganbook.app.MyApp;
import com.ganbook.communication.datamodel.ClassDetails_2;
import com.ganbook.communication.json.HistoryDetails;
import com.ganbook.communication.json.getalbum_response;
import com.ganbook.dialogs.Dialogs;
import com.ganbook.dividers.SpaceItemDecoration;
import com.ganbook.fragments.MainScreenFragment;
import com.ganbook.interfaces.AlbumsInterface;
import com.ganbook.interfaces.GanbookApiInterface;
import com.ganbook.interfaces.IGanbookApiCommercial;
import com.ganbook.interfaces.TitleIteractionListener;
import com.ganbook.models.AlbumsAnswer;
import com.ganbook.models.AlbumsYearModel;
import com.ganbook.models.Commercial;
import com.ganbook.models.events.CreateAlbumEvent;
import com.ganbook.models.events.DeleteAlbumEvent;
import com.ganbook.models.events.NoInternetEvent;
import com.ganbook.models.events.PermissionRefresh;
import com.ganbook.models.events.RenameAlbumEvent;
import com.ganbook.models.events.SelectDrawerItemEvent;
import com.ganbook.models.events.SetPermissionEvent;
import com.ganbook.models.events.SingleCommercialEvent;
import com.ganbook.models.events.UpdateAlbumComments;
import com.ganbook.services.SingleCommercialService;
import com.ganbook.services.SupportUploadService;
import com.ganbook.user.User;
import com.ganbook.utils.CurrentYear;
import com.ganbook.utils.NetworkUtils;
import com.ganbook.utils.UrlUtils;
import com.project.ganim.R;

import org.apache.commons.lang3.StringUtils;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import net.vrgsoft.layoutmanager.RollingLayoutManager;

/**
 * Created by dmytro_vodnik on 6/6/16.
 * working on ganbook1 project
 */
public class AlbumsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener,
        AlbumsInterface {

    public static final String TAG = "ALBUM_FRAG";

    @BindView(R.id.albums_recycler)
    RecyclerView albumsRecycler;

    @BindView(R.id.albums_refresher)
    SwipeRefreshLayout albumsRefresher;

    TabAlbumsAdapter adapter;

    private TitleIteractionListener titleListener;
    private String titleText;
    public StringBuilder cameraPerm;

    @BindView(R.id.add_album)
    @Nullable
    View addButton;

    @Inject
    @Named("GET")
    GanbookApiInterface ganbookApiInterfaceGET;

    @Inject
    @Named("COMMERCIAL")
    IGanbookApiCommercial ganbookApiCommercial;

    ArrayList<AlbumsYearModel> albumsYearModels;

    public static AlbumsFragment newInstance() {

        Bundle args = new Bundle();

        AlbumsFragment fragment = new AlbumsFragment();
        fragment.setArguments(args);


        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((MyApp) getActivity().getApplication()).getGanbookApiComponent().inject(this);

        setHasOptionsMenu(true);

        adapter = new TabAlbumsAdapter(getActivity(), this);

        albumsYearModels = new ArrayList<>();

        final String id;

        if (User.isParent()) {
            id = User.current.getCurrentKid().class_id;
        } else {
            id = User.current.getCurrentClassId();
        }

        populateYearsList(id);

        loadAlbums(User.getCurrentYear(), id);
    }

    private void populateYearsList(String id) {

        albumsYearModels.clear();

        if(User.isParent()) {

            HistoryDetails[] historyDetails = User.current.getCurrentKidHistory();

            if(historyDetails != null) {

                for (HistoryDetails historyDetail : historyDetails) {
                    User.current.addtoAlbumList(historyDetail.class_year,new ArrayList<getalbum_response>());

                    Log.d(TAG, "onCreate: kid history = " + historyDetail);

                    albumsYearModels.add(new AlbumsYearModel(historyDetail.class_year, historyDetail.class_id,
                            historyDetail.gan_id, historyDetail.gan_name, historyDetail.class_name));
                }
            }
        } else {

            ClassDetails_2.ClassHistory[] classHistory = User.current.getCurrentClassHistory();

            if(classHistory != null) {

                for (ClassDetails_2.ClassHistory _classHistory : classHistory) {

                    Log.d(TAG, "onCreate: class history = " + _classHistory);
                    User.current.addtoAlbumList(_classHistory.class_year,new ArrayList<getalbum_response>());

                    albumsYearModels.add(new AlbumsYearModel(_classHistory.class_year, id,
                            User.current.getCurrentGanId(), User.current.getCurrentGanName(), User.current.getCurrentClassName()));
                }
            }
        }

        albumsYearModels.add(new AlbumsYearModel(CurrentYear.get(), id, User.current.getCurrentGanId(),
                User.current.getCurrentGanName(), User.current.getCurrentClassName()));

        Collections.reverse(albumsYearModels);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onReceiveNoInternetEvent(NoInternetEvent noInternetEvent) {

        Log.d("ALBUM LIST FRAGMENT", "onReceiveNoInternetEvent: " + noInternetEvent.isInternetAvailable);
        if(!noInternetEvent.isInternetAvailable) {
            Vibrator v = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                //deprecated in API 26
                v.vibrate(500);
            }
            Dialogs.sneakersNoInternetConnection(getActivity());
            albumsRefresher.setRefreshing(false);
        } else {
            onRefresh();
        }

        EventBus.getDefault().removeStickyEvent(noInternetEvent);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.albums_fragment, container, false);

        //Binding methods
        ButterKnife.bind(this, rootView);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        RollingLayoutManager rollingLayoutManager = new RollingLayoutManager(getActivity());
        albumsRecycler.setLayoutManager(rollingLayoutManager);

        albumsRecycler.setAdapter(adapter);
        albumsRecycler.addItemDecoration(new SpaceItemDecoration(8, 0));

        albumsRefresher.setOnRefreshListener(this);

    }


    @Override
    public void loadAlbums(String year, String id) {
            adapter.clearList();
            Call<List<AlbumsAnswer>> call = ganbookApiInterfaceGET.getAlbums(id, year);

            call.enqueue(new Callback<List<AlbumsAnswer>>() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onResponse(Call<List<AlbumsAnswer>> call, Response<List<AlbumsAnswer>> response) {

                    if (getActivity() != null) {
                        getActivity().invalidateOptionsMenu();
                    }

                    if (response.body() != null) {
                        List<AlbumsAnswer> albumsAnswerList = response.body();

                        if (User.isParent()) {
                            albumsAnswerList = albumsAnswerList.stream()
                                    .filter(album -> album.getPicCount() > 0)
                                    .collect(Collectors.toList());
                        }

                        adapter.addItems(albumsAnswerList);

                        if (adapter.getYearsCount() == 0)
                            adapter.addYears(albumsYearModels);
                        albumsRefresher.setRefreshing(false);
                        adapter.notifyDataSetChanged();

                        if (User.isParent() && !User.isStaff()) {
                            Call<List<Commercial>> call2 = ganbookApiCommercial.getCommercials("STARTED");

                            List<AlbumsAnswer> finalAlbumsAnswerList = albumsAnswerList;
                            call2.enqueue(new Callback<List<Commercial>>() {
                                @Override
                                public void onResponse(Call<List<Commercial>> call, Response<List<Commercial>> commercialResponse) {
                                    if(commercialResponse.body() != null && finalAlbumsAnswerList.size() > 0) {
                                        List<Commercial> commercials = commercialResponse.body();

                                        if (commercials.size() > 0) {
                                            int step = 2;
                                            for (int i = 0; i < commercials.size(); i++) {
                                                int index = (step + 1) * (i + 1) - 2;
                                                AlbumsAnswer albumsAnswer = new AlbumsAnswer();
                                                albumsAnswer.setIsCommercial(true);
                                                albumsAnswer.setCommercial(commercials.get(i));

                                                if (i == 0) {
                                                    adapter.addItem(albumsAnswer, 1);
                                                    finalAlbumsAnswerList.add(1, albumsAnswer);
                                                } else {
                                                    if (index < finalAlbumsAnswerList.size()) {
                                                        finalAlbumsAnswerList.add(index, albumsAnswer);
                                                        adapter.addItem(albumsAnswer, index);
                                                    } else {
                                                        finalAlbumsAnswerList.add(albumsAnswer);
                                                        adapter.addItem(albumsAnswer, finalAlbumsAnswerList.size() - 1);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<List<Commercial>> call, Throwable t) {
                                    Log.e("COMMERCIAL ERR", t.getLocalizedMessage());
                                }
                            });
                        }
                    }
                }

                @Override
                public void onFailure(Call<List<AlbumsAnswer>> call, Throwable t) {
                    albumsRefresher.setRefreshing(false);
                }
            });
    }

    @Override
    public void onRefresh() {
        String year = "";
        String classId = "";
        try {
            AlbumsYearModel albumsYearModel = adapter.getCurrentYearItem();

            year = albumsYearModel.getYear();
            classId = albumsYearModel.getClassId();
        } catch (Exception e) {

            year = User.getCurrentYear();
            classId = User.current.getCurrentClassId();
        }

        if(!NetworkUtils.isConnected()) {
            albumsRefresher.setRefreshing(false);
            Dialogs.errorDialogWithButton(getActivity(), "Error!", getString(R.string.internet_offline), getString(R.string.ok));
        } else {
            loadAlbums(year, classId);
            EventBus.getDefault().postSticky(new PermissionRefresh());
        }
    }


    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        menu.clear();

        if(User.isTeacher()) {
            inflater.inflate(R.menu.album_fragment_menu, menu);
        } else if(User.isStaff()) {
            inflater.inflate(R.menu.album_fragment_menu_parent, menu);

            getCamerraPermission(new GetDataCallback() {

                @Override
                public void getCemraPermission(String string) {
                    menu.clear();

                    if(string.equals("1")) {
                        inflater.inflate(R.menu.album_fragment_menu, menu);

                    } else {
                        inflater.inflate(R.menu.album_fragment_menu_parent, menu);
                    }
                }

                @Override
                public void onError() {

                }
            });

        } else {
            inflater.inflate(R.menu.album_fragment_menu_parent, menu);
        }

        super.onCreateOptionsMenu(menu, inflater);

    }


    private void getCamerraPermission(final GetDataCallback getDataCallback) {
        Call<ResponseBody> cameraPermission = ganbookApiInterfaceGET.getStaffPermissions(String.valueOf(Integer.valueOf(User.getId()) - Integer.valueOf(User.USER_ID_KEY)), User.current.getCurrentClassId(), User.current.getCurrentGanId());
        cameraPermission.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if(response.body() != null) {
                        JSONObject cameraPermissionObject = new JSONObject(response.body().string());
                        String cameraPermission = cameraPermissionObject.getString("camera_permission");
                        getDataCallback.getCemraPermission(cameraPermission);

                    }

                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {


                Log.e(TAG, "onFailure: error while load parents = " + Log.getStackTraceString(t));
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (id) {
            case R.id.add_album:
                Log.i(TAG, "add ALBUMS FRAGMENT album clicked ");
                startActivity(new Intent(getActivity(), AddAlbumActivity.class));
                return true;
//            case R.id.bit_app:
//                UrlUtils.openBitApp(this.getContext());
//                return true;
        }


        return super.onOptionsItemSelected(item);
    }

    @Subscribe
    public void onMessageEvent(SelectDrawerItemEvent selectDrawerItemEvent){

        Log.d(TAG, "onMessageEvent: " + selectDrawerItemEvent);

        if (selectDrawerItemEvent.isSet) {

            String id;


            if (User.isParent())
                id = User.current.getCurrentKid().class_id;
            else
                id = User.current.getCurrentClassId();

            adapter.clearYears();

            populateYearsList(id);

            loadAlbums(CurrentYear.get(), id);
            MainScreenFragment.inst.checkContactTab();
            titleText = selectDrawerItemEvent.title;
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onReceiveRenameAlbumEvent(RenameAlbumEvent renameAlbumEvent) {

        Log.d(TAG, "onReceiveRenameAlbumEvent: " + renameAlbumEvent);

        adapter.updateItem(renameAlbumEvent.pos, renameAlbumEvent.albumsAnswer);

        EventBus.getDefault().removeStickyEvent(renameAlbumEvent);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onReceiveUpdateAlbumComments(UpdateAlbumComments updateAlbumComments) {

        Log.d(TAG, "onReceiveUpdateAlbumComments: " + updateAlbumComments);

        AlbumsAnswer albumsAnswer = adapter.getItem(updateAlbumComments.pos);

        albumsAnswer.setCommentsCount(albumsAnswer.getCommentsCount() + updateAlbumComments.size);

        adapter.updateItem(updateAlbumComments.pos, albumsAnswer);

        EventBus.getDefault().removeStickyEvent(updateAlbumComments);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onReceiveDeleteAlbumEvent(DeleteAlbumEvent deleteAlbumEvent) {

        Log.d(TAG, "onReceiveDeleteAlbumEvent: " + deleteAlbumEvent);

        adapter.removeItem(deleteAlbumEvent.pos);

        EventBus.getDefault().removeStickyEvent(deleteAlbumEvent);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onReceiveAlbumEvent(CreateAlbumEvent createAlbumEvent) {

        Log.d(TAG, "onReceiveAlbumEvent: " + createAlbumEvent);

        adapter.addItem(createAlbumEvent.albumsAnswer);

        EventBus.getDefault().removeStickyEvent(createAlbumEvent);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onReceiveSetPermissionEvent(SetPermissionEvent setPermissionEvent) {

        Log.d(TAG, "onReceiveSetPermissionEvent: event = " + setPermissionEvent);

        //push forward to fragment
        adapter.notifyDataSetChanged();

        EventBus.getDefault().removeStickyEvent(setPermissionEvent);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onReceiveSingleCommercialEvent(SingleCommercialEvent singleCommercialEvent) {

        Log.d(TAG, "onReceiveSingleCommercialEvent: event = " + singleCommercialEvent);

        Intent intent = new Intent(getActivity(), CustomAdsActivity.class);
        intent.putExtra("commercial", singleCommercialEvent.getCommercial());
        startActivity(intent);

        EventBus.getDefault().removeStickyEvent(singleCommercialEvent);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof TitleIteractionListener) {

            titleListener = (TitleIteractionListener) context;
        } else throw new RuntimeException("activity " + getActivity().getClass().getName() + "should " +
                "implement TitleIteractionInterface !");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("ON RESUME", "ON RESUME");
        Intent intent = new Intent(getActivity(), SingleCommercialService.class);
        String userType;

        if (User.current.type.equals(User.Type_Father)
                || User.current.type.equals(User.Type_Mother)
                || User.current.type.equals(User.Type_Staff)
                ||  User.current.type.equals(User.Type_Parent)) {
            userType = User.Type_Parent;
        } else {
            userType = User.current.type;
        }
        intent.putExtra("userType", userType);
        if (getActivity() == null) {
            return;
        }
        getActivity().startService(intent);
    }
}

interface GetDataCallback {
    void getCemraPermission(String string);
    void onError();
}

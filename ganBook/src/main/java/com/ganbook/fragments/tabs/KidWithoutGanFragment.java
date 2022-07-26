package com.ganbook.fragments.tabs;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.FragmentManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ganbook.activities.ChooseClassActivity;
import com.ganbook.activities.MainActivity;
import com.ganbook.adapters.KidWithoutGanAdapter;
import com.ganbook.amazinglist.special.CustomToast;
import com.ganbook.app.MyApp;
import com.ganbook.communication.ICompletionHandler;
import com.ganbook.communication.datamodel.ClassDetails;
import com.ganbook.communication.datamodel.ResultObj;
import com.ganbook.communication.json.GetUserKids_Response;
import com.ganbook.communication.json.HistoryDetails;
import com.ganbook.communication.json.getalbum_response;
import com.ganbook.communication.json.getkindergarten_response;
import com.ganbook.communication.json.transmitter.JsonTransmitter;
import com.ganbook.dialogs.Dialogs;
import com.ganbook.dividers.SpaceItemDecoration;
import com.ganbook.fragments.BaseFragment;
import com.ganbook.interfaces.GanbookApiInterface;
import com.ganbook.interfaces.KidWithoutGanInterface;
import com.ganbook.models.AlbumsAnswer;
import com.ganbook.models.AlbumsYearModel;
import com.ganbook.user.User;

import com.project.ganim.R;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class KidWithoutGanFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener,
        KidWithoutGanInterface {

    private static final String TAG = KidWithoutGanFragment.class.getName();

    @BindView(R.id.albums_refresher)
    SwipeRefreshLayout albumsRefresher;

    @BindView(R.id.albums_recycler)
    RecyclerView albumsRecycler;

    ArrayList<AlbumsYearModel> albumsYearModels;

    KidWithoutGanAdapter adapter;

    @Inject
    @Named("GET")
    GanbookApiInterface ganbookApiInterfaceGET;

    public static KidWithoutGanFragment inst;
    private FragmentManager fragmentManager;

    public KidWithoutGanFragment() {
        // Required empty public constructor
    }


    public static KidWithoutGanFragment newInstance() {
        KidWithoutGanFragment fragment = new KidWithoutGanFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        inst = this;
        fragmentManager = getActivity().getSupportFragmentManager();

        ((MyApp) getActivity().getApplication()).getGanbookApiComponent().inject(this);

        albumsYearModels = new ArrayList<>();

        adapter = new KidWithoutGanAdapter(getContext(), this);

        if (getArguments() != null) {

        }

        HistoryDetails[] historyDetails = User.current.getCurrentKidHistory();

        if(historyDetails != null) {

            for (HistoryDetails historyDetail : historyDetails) {
                User.current.addtoAlbumList(historyDetail.class_year,new ArrayList<getalbum_response>());

                Log.d(TAG, "onCreate: kid history = " + historyDetail);

                albumsYearModels.add(new AlbumsYearModel(historyDetail.class_year, historyDetail.class_id, historyDetail.gan_id, historyDetail.gan_name, historyDetail.class_name));
            }
        }

        Log.d(TAG, "onCreate: years = " + albumsYearModels);

        adapter.addYears(albumsYearModels);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootView = inflater.inflate(R.layout.fragment_kid_without_gan, container, false);

        ButterKnife.bind(this, rootView);

        albumsRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        albumsRecycler.addItemDecoration(new SpaceItemDecoration(3, SpaceItemDecoration.VERTICAL));

        albumsRecycler.setAdapter(adapter);

        albumsRefresher.setOnRefreshListener(this);

        return rootView;
    }

    @Override
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

                    MyApp.sendAnalytics("menu-choose-class-unattached-kid-ui", "menu-choose-class-unattached-kid-ui-"+User.getId(), "menu-choose-class-unattached-kid-ui", "MenuChooseClassUnattachedKid");


                    Intent intent = new Intent(getActivity(), ChooseClassActivity.class);
                    intent.putExtra("withoutGan", true);
                    startActivityForResult(intent, 1);
                }
            }

        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, "onActivityResult: req = " + requestCode + " res = " + resultCode + " data = " + data);

//        if (resultCode == Activity.RESULT_OK) {

            if (requestCode == 1) {

                MainActivity.refresh();
            }
//        }
    }

    @Override
    public void onRefresh() {

        call_getKids();
    }

    @Override
    public void loadAlbums(final String year, final String classId, final String ganId,
                           final int insertAfter, final AlbumsYearModel albumsYearModel) {

        Call<List<AlbumsAnswer>> call = ganbookApiInterfaceGET.getAlbums(classId, year);

        call.enqueue(new Callback<List<AlbumsAnswer>>() {
            @Override
            public void onResponse(Call<List<AlbumsAnswer>> call, Response<List<AlbumsAnswer>> response) {


                List<AlbumsAnswer> albumsAnswers = response.body();
                Log.d(TAG, "onResponse: " + "loaded albums = " + albumsAnswers);

                if (albumsAnswers == null) {

                } else {

                    albumsYearModel.setSelected(false);

                    for (AlbumsAnswer a :
                            albumsAnswers) {
                    albumsYearModel.setSelected(true);
                     a.setClassId(classId);
                     a.setGanId(ganId);
                    }

                    adapter.addItemsAfter(insertAfter, albumsAnswers);
                }
            }

            @Override
            public void onFailure(Call<List<AlbumsAnswer>> call, Throwable t) {

                Log.e(TAG, "onFailure: load albums" + Log.getStackTraceString(t) );

                albumsRefresher.setRefreshing(false);
            }
        });
    }

    @Override
    public void call_getKids() {

        startProgress(R.string.operation_proceeding);
        String userId = User.getId();
        JsonTransmitter.send_getuserkids(userId, new ICompletionHandler() {
            @Override
            public void onComplete(ResultObj result) {
                stopProgress();
                albumsRefresher.setRefreshing(false);
                if (!result.succeeded) {
                    if(JsonTransmitter.NO_NETWORK_MODE.equals(result.result))
                    {
                        Dialogs.errorDialogWithButton(getActivity(), "Error!", getString(R.string.internet_offline), "OK");

                        return;
                    }
                    else
                    {
                        String errmsg = result.result;
                        Dialogs.errorDialogWithButton(getActivity(), "Error!", errmsg, "OK");

                        return;
                    }
                }
                int num = result.getNumResponses();
                GetUserKids_Response[] responses = new GetUserKids_Response[num];
                for (int i = 0; i < num; i++) {
                    responses[i] = (GetUserKids_Response) result.getResponse(i);
                }
                User.updateWithUserkids(responses);

                MainActivity.refresh();
            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        inst = null;
    }
}

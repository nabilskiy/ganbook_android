package com.ganbook.fragments.tabs;


import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ganbook.activities.MainActivity;
import com.ganbook.app.MyApp;
import com.ganbook.communication.json.TeacherPhotoResponse;
import com.ganbook.fragments.MainScreenFragment;
import com.ganbook.fragments.SingleContactProfileFragement;
import com.ganbook.interfaces.ContactsFragmentInterface;
import com.ganbook.interfaces.GanbookApiInterface;
import com.ganbook.models.GetParentAnswer;
import com.ganbook.models.events.ChangeClassEvent;
import com.ganbook.models.events.RefreshDrawerEvent;
import com.ganbook.models.events.SelectDrawerItemEvent;
import com.ganbook.models.events.UpdateKidsEvent;
import com.ganbook.ui.CircleImageView;
import com.ganbook.ui.ContactListAdapter;
import com.ganbook.user.User;
import com.ganbook.utils.ActiveUtils;
import com.ganbook.utils.CurrentYear;
import com.ganbook.utils.FragmentUtils;
import com.ganbook.utils.UrlUtils;
import com.project.ganim.R;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ContactsFragment extends Fragment implements ContactsFragmentInterface {

    public static final String TAG = ContactsFragment.class.getName();
    @BindView(R.id.contacts_refresher)
    SwipeRefreshLayout contactsRefsresher;

    @BindView(R.id.pbHeaderProgress)
    ProgressBar pbHeaderProgress;

    @BindView(R.id.contact_list)
    ListView contactList;

    @BindView(R.id.search_bar)
    EditText searchBar;

    @BindView(R.id.cancel_search)
    Button cancelSearch;

    private ContactListAdapter _adapter;
    private TextView totalContacts, totalAttendence;
    private String totalPrefStr;
    private View footerView;
    private CircleImageView teacherImage;

    @Inject
    @Named("GET")
    GanbookApiInterface ganbookApiInterfaceGET;
    private String teacherPhotoName;

    public ContactsFragment() {
        // Required empty public constructor
    }


    public static ContactsFragment newInstance() {
        ContactsFragment fragment = new ContactsFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        if (getArguments() != null) {


        }

        ((MyApp) getActivity().getApplication()).getGanbookApiComponent().inject(this);

        _adapter = new ContactListAdapter(getContext(), this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootView = inflater.inflate(R.layout.fragment_contacts, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (User.isParent()) {
            totalPrefStr = getResources().getString(R.string.total_str);
        } else {
            totalPrefStr = getResources().getString(R.string.total_parents_str);
        }

        contactsRefsresher.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getTeacherPhoto();
                loadContacts();
            }
        });

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (_adapter != null) {
                    _adapter.getFilter().filter(s);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        cancelSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchBar.setText("");
            }
        });

        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View headerview = inflater.inflate(R.layout.contact_list_header, null);
        TextView teacher_name = (TextView) headerview.findViewById(R.id.contact_header_text);
        teacherImage = headerview.findViewById(R.id.teacherImage);
        teacher_name.setText(User.current.getCurrentTeacherName());

        headerview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d(TAG, "onClick: open teacher details clicked");

                SingleContactProfileFragement singleContactProfileFragement =
                        SingleContactProfileFragement.newInstance(getTeacherDetails());

                FragmentUtils.openFragment(singleContactProfileFragement, R.id.content_frame,
                        SingleContactProfileFragement.TAG, getContext(), true);

            }
        });

        contactList.addHeaderView(headerview);

        contactList.setAdapter(_adapter);

        loadContacts();
        getTeacherPhoto();
    }

    private void getTeacherPhoto() {
        Call<List<TeacherPhotoResponse>> institutionLogoCall = ganbookApiInterfaceGET.getTeacherPhoto(User.current.getCurrentGanId());

        institutionLogoCall.enqueue(new Callback<List<TeacherPhotoResponse>>() {
            @Override
            public void onResponse(Call<List<TeacherPhotoResponse>> call, Response<List<TeacherPhotoResponse>> response) {
                if(response.body() != null) {
                    if(response.body().get(0).getTeacherPhoto() != null){
                        teacherPhotoName = response.body().get(0).getTeacherPhoto();
                        Picasso.with(getActivity()).load("http://s3.ganbook.co.il/ImageStore/users/" + teacherPhotoName)
                                .centerCrop()
                                .fit()
                                .into(teacherImage);
                    } else {
                        Picasso.with(getActivity()).load(R.drawable.teacher_profile_image)
                                .centerCrop()
                                .fit()
                                .into(teacherImage);

                    }

                }

            }

            @Override
            public void onFailure(Call<List<TeacherPhotoResponse>> call, Throwable t) {
                Log.d("INSTITUTION LOGO FAIL", t.toString());
            }
        });
    }

    private void loadContacts() {

        String class_id = User.current.getCurrentClassId();
        String year = CurrentYear.get();

        Call<List<GetParentAnswer>> getParentsCall = ganbookApiInterfaceGET.getParents(class_id, year);

        getParentsCall.enqueue(new Callback<List<GetParentAnswer>>() {
            @Override
            public void onResponse(Call<List<GetParentAnswer>> call, Response<List<GetParentAnswer>> response) {

                List<GetParentAnswer> parentsArr = response.body();

                _adapter.clear();

                if (parentsArr != null) {

                    contactsRefsresher.setRefreshing(false);
                    Log.d(TAG, "onResponse: parents = " + parentsArr);

                    ArrayList sortedParentsArr = new ArrayList<>();
                    sortedParentsArr.clear();
                    for(GetParentAnswer parentsArrayList : parentsArr) {
                        if(parentsArrayList.type.equals(User.Type_Staff)) {
                            sortedParentsArr.add(0, parentsArrayList);
                            continue;
                        }

                        sortedParentsArr.add(parentsArrayList);
                    }



                    _adapter.addAll(sortedParentsArr);

                    if(User.isTeacher())
                    {
                        int pending_parents = 0;

                        for (GetParentAnswer responseParent : parentsArr) {
                            if(!ActiveUtils.isActive(responseParent.kid_active))
                            {
                                pending_parents ++;
                            }
                        }

                        User.current.updatePendingParents(String.valueOf(pending_parents));
                        MainScreenFragment.initContactTabBadge();
                        MainActivity.updateDrawerContent();
                    }

                    int size = parentsArr.size();

                    Log.d(TAG, "onResponse: size = " + size + " parr size = " + parentsArr.size());

                    setTotalContacts(_adapter.getCount());
                }
            }

            @Override
            public void onFailure(Call<List<GetParentAnswer>> call, Throwable t) {

                contactsRefsresher.setRefreshing(false);

                Log.e(TAG, "onFailure: error while load parents = " + Log.getStackTraceString(t));
            }
        });
    }

    @Override
    public void setTotalContacts(int size) {

            if (totalContacts != null)
                contactList.removeFooterView(footerView);

            if (getActivity() != null) {
                LayoutInflater inflater = LayoutInflater.from(getActivity());

                footerView = inflater.inflate(R.layout.contact_list_footer, null);

                totalContacts = (TextView) footerView.findViewById(R.id.total_contacts);
                totalAttendence = footerView.findViewById(R.id.total_attendence);
                totalAttendence.setText(R.string.attendenceText);
                contactList.addFooterView(footerView);
                totalContacts.setText(totalPrefStr + " " + size);
            }
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

    @Subscribe
    public void onMessageEvent(SelectDrawerItemEvent selectDrawerItemEvent) {

        Log.d(TAG, "contacts fragment received drawer item clicked" + selectDrawerItemEvent);

        if (selectDrawerItemEvent.isSet) {

            loadContacts();
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onReceiveUpdateKidEvent(UpdateKidsEvent updateKidsEvent) {
        if(updateKidsEvent.getUpdated()) {
            loadContacts();
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onChangedClassEvent(ChangeClassEvent changeClassEvent){

        Log.d(TAG, "change class event received = " + changeClassEvent);

        if (changeClassEvent.changed) {

            loadContacts();
            _adapter.updateContactListAfterDisconnect(changeClassEvent.kidId);

            EventBus.getDefault().postSticky(new RefreshDrawerEvent());
            FragmentUtils.closeFragment(getContext(), SingleContactProfileFragement.TAG);
        }

        EventBus.getDefault().removeStickyEvent(changeClassEvent);
    }

    private GetParentAnswer getTeacherDetails() {

        GetParentAnswer GetParentAnswer = new GetParentAnswer();

        GetParentAnswer.parent_first_name = User.current.getCurrentTeacherFirstName();
        GetParentAnswer.parent_last_name = User.current.getCurrentTeacherLastName();
        GetParentAnswer.parent_mobile = User.current.getCurrentTeacherMobile();
        GetParentAnswer.parent_phone = User.current.getCurrentGanPhone();
        GetParentAnswer.parent_mail = User.current.getCurrentTeacherMail();
        GetParentAnswer.parent_address = User.current.getCurrentGanAddress();
        GetParentAnswer.parent_city = User.current.getCurrentGanCity();
        GetParentAnswer.type = User.Type_Teacher;
        GetParentAnswer.teacherPhoto = teacherPhotoName;

        return GetParentAnswer;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.contacts_fragment_menu, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            case R.id.bit_app:
                UrlUtils.openBitApp(this.getContext());
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

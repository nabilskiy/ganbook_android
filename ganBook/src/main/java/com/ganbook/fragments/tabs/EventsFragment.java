package com.ganbook.fragments.tabs;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.ganbook.activities.AddEventActivity;
import com.ganbook.activities.EventActivity;
import com.ganbook.app.MyApp;
import com.ganbook.interfaces.GanbookApiInterface;
import com.ganbook.models.EventModel;
import com.ganbook.models.answers.EventsAnswer;
import com.ganbook.models.events.SelectDrawerItemEvent;
import com.ganbook.models.events.UpdateEvent;
import com.ganbook.ui.EventsAdapter;
import com.ganbook.user.User;
import com.ganbook.utils.Const;
import com.ganbook.utils.UrlUtils;
import com.project.ganim.R;
import com.ramotion.foldingcell.FoldingCell;

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

public class EventsFragment extends Fragment implements AdapterView.OnItemClickListener {

    public static final String TAG = EventsFragment.class.getName();

    @BindView(R.id.events_refresher)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.events_list)
    ListView eventsListView;
    private EventsAdapter adapter;
    View noEventsView;
    ArrayList<EventsAnswer> eventsAnswerArrayList;
    public FoldingCell fc;

    @Inject
    @Named("GET")
    GanbookApiInterface ganbookApiInterfaceGET;

    public EventsFragment() {
        // Required empty public constructor
    }

    public static EventsFragment newInstance() {
        EventsFragment fragment = new EventsFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
        ((MyApp) getActivity().getApplication()).getGanbookApiComponent().inject(this);

        if (getArguments() != null) {

        }

        adapter = new EventsAdapter(getActivity());
        eventsAnswerArrayList = new ArrayList<>();

        loadEvents();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_events, container, false);



        ButterKnife.bind(this, rootView);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        eventsListView.setOnItemClickListener(EventsFragment.this);

        eventsListView.setAdapter(adapter);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                loadEvents();
            }
        });

        noEventsView = LayoutInflater.from(getContext()).inflate(R.layout.item_composer_text, null);
        ((TextView)noEventsView.findViewById(R.id.tv)).setText(getString(R.string.empty_events_list));


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
    public void onMessageEvent(SelectDrawerItemEvent selectDrawerItemEvent){

        Log.d(TAG, "events fragment received drawer item clicked" + selectDrawerItemEvent);

        if (selectDrawerItemEvent.isSet) {

            loadEvents();
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onUpdateEvent(UpdateEvent updateEvent){

        Log.d(TAG, "onUpdateEvent: event = " + updateEvent);

        if (updateEvent !=null) {

            eventsAnswerArrayList.set(updateEvent.getPos(), updateEvent.getEventsAnswer());
            addDataToList(eventsAnswerArrayList);
        }

        EventBus.getDefault().removeStickyEvent(updateEvent);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        if (User.isTeacher())
            inflater.inflate(R.menu.events_fragment_menu, menu);
        else
            inflater.inflate(R.menu.events_fragment_menu_parent, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {

            case R.id.add_event:

                Log.i(TAG, "add event clicked ");
                startActivityForResult(new Intent(getActivity(), AddEventActivity.class), Const.ADD_EVENT_CODE);
                return true;

            case R.id.bit_app:
                UrlUtils.openBitApp(this.getContext());
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, "onActivityResult: req code = " + requestCode + " res " + resultCode + " data = "
            + data);

        switch (resultCode) {

            case Activity.RESULT_OK:

                if (requestCode == Const.ADD_EVENT_CODE) {

                eventsAnswerArrayList.addAll(data.<EventsAnswer>getParcelableArrayListExtra("events"));
                addDataToList(eventsAnswerArrayList);

//                    loadEvents();
                }
                break;

            case Const.RESULT_DELETE_EVENT:

                Log.d(TAG, "onActivityResult: deleting " + data.getStringExtra("event_id") + " event");

                for (EventsAnswer eventsAnswer : eventsAnswerArrayList) {

                    for (EventModel eventModel : eventsAnswer.getEventModelList()) {

                        Log.d(TAG, "onActivityResult: looking event model = " + eventModel);

                        if (eventModel.event_id.equals(data.getStringExtra("event_id"))) {
                            eventsAnswer.getEventModelList().remove(eventModel);
                            break;
                        }
                    }
                }

                addDataToList(eventsAnswerArrayList);

                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        ArrayList<EventsAdapter.Item> items = adapter.getEvents();

        int type = items.get(position).getViewType();

        if(type == EventsAdapter.RowType.EVENT_ITEM.ordinal()) {

            EventsAdapter.EventItem item = (EventsAdapter.EventItem)items.get(position);

            Intent in = new Intent(getActivity(), EventActivity.class);

            in.putExtra("pos", findPositionByEventId(item.event_id));
            in.putExtra("event_id",item.event_id);
            in.putExtra("event_date",item.event_date);
            in.putExtra("event_title",item.event_title);
            in.putExtra("event_type",item.event_type);
            in.putExtra("start_hour",item.start_hour);
            in.putExtra("end_hour",item.end_hour);
            in.putExtra("start_date",item.start_date);
            in.putExtra("end_date",item.end_date);
            in.putExtra("day_off",item.dayOff);
            in.putExtra("event_comments",item.event_comments);
            in.putExtra("event_kids",item.kids);
            in.putExtra("all_kids",item.all_kids);
            in.putExtra("all_day",item.all_day);
            in.putExtra("kids_ids",item.kids_ids);
            in.putExtra("event_start_date",item.event_start_date);
            in.putExtra("event_end_date",item.event_end_date);

            startActivityForResult(in, Const.EVENT_ITEM_DETAILS_CODE);
        }
    }

    private int findPositionByEventId(String event_id) {

        for (int i = 0; i < eventsAnswerArrayList.size(); i++) {
            EventsAnswer eventsAnswer = eventsAnswerArrayList.get(i);

            for (EventModel eventModel : eventsAnswer.getEventModelList()) {

                if (eventModel.getEvent_id().equals(event_id))
                    return i;
            }
        }

        return 0;
    }

    private void loadEvents() {

        String class_id = User.current.getCurrentClassId();

        Call<List<EventsAnswer>> call = ganbookApiInterfaceGET.getEvents(class_id, "1");

        call.enqueue(new Callback<List<EventsAnswer>>() {
            @Override
            public void onResponse(Call<List<EventsAnswer>> call, Response<List<EventsAnswer>> response) {

                swipeRefreshLayout.setRefreshing(false);

                List<EventsAnswer> eventsAnswers = response.body();

                if (eventsAnswers != null) {

                    Log.d(TAG, "onResponse: call = " + eventsAnswers);

                    eventsAnswerArrayList.clear();
                    eventsAnswerArrayList.addAll(eventsAnswers);

                    addDataToList(eventsAnswerArrayList);
                } else {

                    Log.e(TAG, "onResponse: error while parse " + response);
                }
            }

            @Override
            public void onFailure(Call<List<EventsAnswer>> call, Throwable t) {

                swipeRefreshLayout.setRefreshing(false);

                Log.e(TAG, "onFailure: error while load events = " +  Log.getStackTraceString(t));
            }
        });
    }

    private void addDataToList(List<EventsAnswer> list) {

        adapter.clear();
        adapter.addAll(adapter.generateItemsList(list));
        adapter.notifyDataSetChanged();
    }
}

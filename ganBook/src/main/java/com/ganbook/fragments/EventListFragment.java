package com.ganbook.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ganbook.activities.AddEventActivity;
import com.ganbook.activities.EventActivity;
import com.ganbook.app.MyApp;
import com.ganbook.communication.ICompletionHandler;
import com.ganbook.communication.datamodel.ResultObj;
import com.ganbook.communication.json.getevents_Response;
import com.ganbook.communication.json.transmitter.JsonTransmitter;
import com.ganbook.ui.EventsAdapter;
import com.ganbook.user.User;

import com.project.ganim.R;

import java.util.ArrayList;
import java.util.TreeMap;

/**
 * Created by Noa on 29/11/2015.
 */
public class EventListFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener,AdapterView.OnItemClickListener {

    private static final String TAG = EventListFragment.class.getName();
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private static EventListFragment inst;
    private final Initializer initializer = new Initializer();
    private boolean justCreated = false;
    private EventsAdapter adapter;
    Context mcontext;
    private ListView mListView;
    View noEventsView;
    ProgressBar pbHeaderProgress;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        initializer.onAttach(activity);
    }

    class Initializer {

        public void onCreate() {
            inst = EventListFragment.this;
            setActionBarValues();
            justCreated = true;
        }

        public void onAttach(Activity activity) {
            mcontext = activity;
        }

        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


            MyApp.sendAnalytics("events-gan-ui", "events-gan"+ User.current.getCurrentGanId()+"-class"+User.current.getCurrentClassId()+"-ui"+ User.getId(), "events-gan-ui", "EventsGan");

            View _view = inflater.inflate(R.layout.event_frag, container, false);


            Log.i("noanoa", "event_list onCreateView " + forceSetTitle());

            findActionbarFields();

            Log.i("noanoa","event_list handleFragmentVisible ");
            handleFragmentVisible();

            pbHeaderProgress = (ProgressBar)  _view.findViewById(R.id.pbHeaderProgress);

            mSwipeRefreshLayout = (SwipeRefreshLayout) _view.findViewById(R.id.refresh_layout);

            mSwipeRefreshLayout.setOnRefreshListener(EventListFragment.this);
            mSwipeRefreshLayout.setColorScheme(android.R.color.holo_blue_bright,
                    android.R.color.holo_green_light,
                    android.R.color.holo_orange_light,
                    android.R.color.holo_red_light);

            mListView = (ListView) _view.findViewById(R.id.feeds_list);
            //mListView.setPinnedHeaderView(LayoutInflater.from(activity()).inflate(R.layout.item_composer_header, mListView, false));
            mListView.setOnItemClickListener(EventListFragment.this);

//			select = (Button) activity().findViewById(R.id.select);
//			save_btn.setText(R.string.add_album_btn);
//			findActionbarFields();
            adapter = new EventsAdapter(MyApp.context);
            mListView.setAdapter(adapter);

//            mListView.setSelection(8);
//            adapter.notifyDataSetChanged();

            noEventsView = inflater.inflate(R.layout.item_composer_text, null);
            ((TextView)noEventsView.findViewById(R.id.tv)).setText(activity().getString(R.string.empty_events_list));

            return _view;
        }

        public void onActivityCreated() {
//			findActionbarFields();
        }

        public void onStart() {
            Log.i("noanoa","onStart");
            boolean _justCreated = justCreated;
            justCreated = false; //!
            populateEventList(_justCreated);

        }

        public void onBecomingVisible() {
//			findActionbarFields();
//			if (forceSetTitle()) {
//				clearSetTitle();
            Log.i("noanoa","event_list handleFragmentVisible ");
//				handleFragmentVisible();
//			}

            if (mSwipeRefreshLayout != null) {
                setSaveListener();
                Log.i("noanoa","onBecomingVisible");
                populateEventList(false);
            }
        }

    };

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        ArrayList<EventsAdapter.Item> items = adapter.mapToArrayList(User.current.getEventList());

        int type = items.get(position).getViewType();

        if(type == EventsAdapter.RowType.EVENT_ITEM.ordinal())
        {
            EventsAdapter.EventItem item = (EventsAdapter.EventItem)items.get(position);

            Intent in = new Intent(activity(), EventActivity.class);

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

            startActivity(in);
        }


    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializer.onCreate();

        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();

        if (User.isTeacher())
            inflater.inflate(R.menu.album_fragment_menu, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {

            case R.id.add_album:

                Log.i(TAG, "add EVENT LIST FRAGMENT album clicked ");
                start(AddEventActivity.class);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void setActionBarValues() {
        titleTextId = R.string.albums;
        saveBtnVisibility = View.GONE;
//		saveBtnVisibility = (User.isTeacher() ? View.VISIBLE : View.INVISIBLE);
//		saveBtnTextId = SHOW_ADD_BUTTON;
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
//		add_btn.setVisibility(User.isTeacher() ? View.VISIBLE : View.INVISIBLE);
        setSaveListener();

    }

    private void setSaveListener() {
        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int jj=234;
                jj++;
                start(AddEventActivity.class);
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initializer.onActivityCreated();
    }

    @Override
    public void setUserVisibleHint(boolean visible)	{
        super.setUserVisibleHint(visible);
        int jj=53;
        jj++;
    }

    @Override
    public void onStart() {
        super.onStart();
        initializer.onStart();
    }


    @Override
    public void onResume() {
        int jj=345;
        jj++;
        super.onResume();
    }


    @Override
    public void onRefresh() {
//		refreshContent();
        mSwipeRefreshLayout.setRefreshing(false);
        populateEventList(true);
    }

    @Override
    public void onBecomingVisible() {
        super.onBecomingVisible();
        initializer.onBecomingVisible();
    }

    public static void forceContentRefresh() {
        if (inst != null) {
            inst.base_forceContentRefresh();
            inst.populateEventList(true);
        }
    }

    public static void contentRefresh() {
        if (inst != null) {
            inst.base_forceContentRefresh();
            inst.populateEventList(false);
        }
    }

    public static void setFragmentVisible() {
        if (inst != null) {
            inst.handleFragmentVisible();
        }
    }

    private void populateEventList(boolean forceRefresh)
    {
        mSwipeRefreshLayout.setRefreshing(false);

        if (!forceRefresh && User.current.hasEvetList()){
            TreeMap<String, ArrayList<getevents_Response.Event>> _eventList = (TreeMap<String, ArrayList<getevents_Response.Event>>) User.current.getEventList();

            refreshList(_eventList);

            return;
        }

//        startProgress(R.string.operation_proceeding);
//		mSwipeRefreshLayout.setRefreshing(true);


        pbHeaderProgress.setVisibility(View.VISIBLE);
        mListView.setVisibility(View.GONE);


        String class_id = User.current.getCurrentClassId();

        JsonTransmitter.send_getevents(class_id, new ICompletionHandler() {
            @Override
            public void onComplete(ResultObj result) {

//                stopProgress();
                mListView.setVisibility(View.VISIBLE);
                pbHeaderProgress.setVisibility(View.GONE);

                if (!result.succeeded) {
                    if (JsonTransmitter.NO_NETWORK_MODE.equals(result.result)) {
                        //                    showNotInternetAlert();
                        //                    mListView.addHeaderView(tryAgainView);
                        //                    adapter = new AlbumsAdapter(MyApp.context, (HashMap<String, ArrayList<getalbum_response>>) User.current.getAlbumList(), activity());
                        //                    mListView.setAdapter(adapter);
                        //                    return;
                    }
                }
                else
                {
                    mSwipeRefreshLayout.setRefreshing(false);

                    int num = result.getNumResponses();

                    if(num == 0)
                    {
                        mListView.addHeaderView(noEventsView);
                        adapter.notifyDataSetChanged();
                        return;
                    }

                    TreeMap<String, ArrayList<getevents_Response.Event>> map = new TreeMap<String, ArrayList<getevents_Response.Event>>();

                    for (int i = 0; i < num; i++)
                    {
                        getevents_Response response = (getevents_Response) result.getResponse(i);

                        ArrayList<getevents_Response.Event> events = new ArrayList<getevents_Response.Event>();

                        for(getevents_Response.Event event: response.events)
                        {
                            events.add(event);
                        }

                        map.put(response.event_date, events);


                    }

                    User.current.setEventList(map);

                    refreshList(map);
                }
            }
        });
    }

    private void refreshList(TreeMap<String, ArrayList<getevents_Response.Event>> map)
    {
        adapter.clear();
        adapter.addAll(adapter.mapToArrayList(map));
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void doSetTitleVisibility(int visibility) {

        super.doSetTitleVisibility(visibility);
        if (add_btn != null && User.isTeacher()) {
            add_btn.setVisibility(visibility);
        }
    }

    public static void setTitleVisibility(int visible) {
        if (inst != null) {
            if (visible == View.VISIBLE) {
                inst.handleFragmentVisible();
            }
            inst.doSetTitleVisibility(visible);
        }
    }

}

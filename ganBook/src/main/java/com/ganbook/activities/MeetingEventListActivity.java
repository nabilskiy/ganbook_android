package com.ganbook.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.ganbook.adapters.MeetingHoursAdapter;
import com.ganbook.adapters.MeetingsEventListAdapter;
import com.ganbook.models.MeetingEventModel;
import com.ganbook.models.SuccessAnswer;
import com.ganbook.models.TimeSlotModel;
import com.ganbook.user.User;
import com.project.ganim.R;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MeetingEventListActivity extends BaseAppCompatActivity {
    private List<MeetingEventModel> meetingEventModelList = new ArrayList<>();
    private MeetingsEventListAdapter meetingsEventListAdapter;
    private ListView meetingEventListView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting_event_list);
        setActionBar(getString(R.string.meetings_text), true);

        meetingEventListView = findViewById(R.id.meetinvEventsListView);
        meetingEventListView.setEmptyView(findViewById(R.id.meetingListEmpty));
        progressBar = findViewById(R.id.meetinvEventsListProgress);

        getMeetings();


        meetingEventListView.setOnItemClickListener((parent, view, position, id) -> {
            MeetingEventModel meetingEventModel = (MeetingEventModel) parent.getItemAtPosition(position);

            Intent intent = new Intent(MeetingEventListActivity.this, MeetingHoursActivity.class);
            intent.putExtra("meetingId", meetingEventModel.getMeetingId());
            intent.putExtra("meetingName", meetingEventModel.getMeetingTitle());
            startActivity(intent);

        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        if (User.isTeacher()) {
            getMenuInflater().inflate(R.menu.meetings_event_list_menu, menu);

            MenuItem menuItem = menu.findItem(R.id.add_menu);

            menuItem.getActionView().setOnClickListener(v -> {
                Intent intent = new Intent(MeetingEventListActivity.this, MeetingEventActivity.class);
                startActivity(intent);
            });
        }
        return true;
    }

    private void getMeetings() {
        Call<List<MeetingEventModel>> meetingEvent = ganbookApiInterfaceGET.getMeetings(User.current.getCurrentClassId());
        meetingEventModelList = new ArrayList<>();
        meetingEvent.enqueue(new Callback<List<MeetingEventModel>>() {
            @Override
            public void onResponse(Call<List<MeetingEventModel>> call, Response<List<MeetingEventModel>> response) {
                if (response.body() != null) {
                    progressBar.setVisibility(View.GONE);
                    meetingEventModelList = response.body();

                    meetingsEventListAdapter = new MeetingsEventListAdapter(MeetingEventListActivity.this, meetingEventModelList);
                    meetingEventListView.setAdapter(meetingsEventListAdapter);
                } else {
                    findViewById(R.id.meetingListEmpty).setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);

                }

            }

            @Override
            public void onFailure(Call<List<MeetingEventModel>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);

            }
        });
    }

    public void deleteMeeting(String meetingId) {
        Call<SuccessAnswer> meetingEvent = ganbookApiInterfacePOST.deleteMeeting(meetingId);

        meetingEvent.enqueue(new Callback<SuccessAnswer>() {
            @Override
            public void onResponse(Call<SuccessAnswer> call, Response<SuccessAnswer> response) {
                if (response.body() != null) {
                    if (response.isSuccessful()) {
                        meetingsEventListAdapter.clear();
                        getMeetings();
                        meetingsEventListAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onFailure(Call<SuccessAnswer> call, Throwable t) {

            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(MeetingEventListActivity.this, MainActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
}

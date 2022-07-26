package com.ganbook.activities;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.ganbook.adapters.MeetingAttendeesAdapter;
import com.ganbook.adapters.MeetingHoursAdapter;
import com.ganbook.models.MeetingAttendeesModel;
import com.ganbook.user.User;
import com.project.ganim.R;

import java.util.ArrayList;
import java.util.List;

public class MeetingAttendees extends BaseAppCompatActivity {

    private ListView meetingAttendeesListView;
    private MeetingAttendeesAdapter meetingAttendeesAdapter;
    private List<MeetingAttendeesModel> meetingAttendeesList = new ArrayList<>();
    private String slotId;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting_attendees);
        setActionBar(getString(R.string.attendees_list_title), true);

        meetingAttendeesListView = findViewById(R.id.meetingAttendeesListView);
        progressBar = findViewById(R.id.meetingListProgress);

        Intent intent = getIntent();
        slotId = intent.getStringExtra("slotId");
        getMeetingAttendees(slotId);
    }

    private void getMeetingAttendees(String parentId) {
        Call<List<MeetingAttendeesModel>> call = ganbookApiInterfaceGET.getMeetingAttendees(parentId);

        call.enqueue(new Callback<List<MeetingAttendeesModel>>() {
            @Override
            public void onResponse(Call<List<MeetingAttendeesModel>> call, Response<List<MeetingAttendeesModel>> response) {
                if (response.isSuccessful()) {
                    progressBar.setVisibility(View.GONE);
                    meetingAttendeesList = response.body();

                    meetingAttendeesAdapter = new MeetingAttendeesAdapter(MeetingAttendees.this, meetingAttendeesList);
                    meetingAttendeesListView.setAdapter(meetingAttendeesAdapter);
                } else {
                    progressBar.setVisibility(View.GONE);

                }

            }

            @Override
            public void onFailure(Call<List<MeetingAttendeesModel>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);

            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
}

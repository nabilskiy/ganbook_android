package com.ganbook.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;

import com.ganbook.adapters.MeetingHoursAdapter;
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

public class MeetingHoursActivity extends BaseAppCompatActivity {

    private MeetingHoursAdapter meetingHoursAdapter;
    private ListView meetingHourListView;
    private List<MeetingEventModel> meetingEventModelList = new ArrayList<>();
    private String meetingId, meetingName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting_hours);

        Intent intent = getIntent();
        meetingId = intent.getStringExtra("meetingId");
        meetingName = intent.getStringExtra("meetingName");

        setActionBar(meetingName, true);

        meetingHourListView = findViewById(R.id.meetingHoursList);

        getMeetingEvent();


        meetingHourListView.setOnItemClickListener((parent, view, position, id) -> {
            TimeSlotModel timeSlotModel = (TimeSlotModel) parent.getItemAtPosition(position);

            if (User.isParent()) {

                if (timeSlotModel.getActive().equals("1")) {
                    view.setEnabled(false);
                    view.setClickable(false);
                }

                Call<SuccessAnswer> reserveMeeting = ganbookApiInterfacePOST.reserveMeeting(timeSlotModel.getTimeSlotId(), User.getId(), User.getName());

                reserveMeeting.enqueue(new Callback<SuccessAnswer>() {
                    @Override
                    public void onResponse(Call<SuccessAnswer> call, Response<SuccessAnswer> response) {
                        if (response.isSuccessful()) {
                            getMeetingEvent();
                            meetingHoursAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onFailure(Call<SuccessAnswer> call, Throwable t) {

                    }
                });
            } else if (User.isTeacher()) {
                if (timeSlotModel.getActive().equals("1")) {
                    Intent inte = new Intent(MeetingHoursActivity.this, MeetingAttendees.class);
                    inte.putExtra("slotId", timeSlotModel.getTimeSlotId());
                    startActivity(inte);
                } else {
                    view.setEnabled(false);
                    view.setClickable(false);
                }

            }
        });

    }

    private void getMeetingEvent() {
        Call<List<MeetingEventModel>> meetingEvent = ganbookApiInterfaceGET.getMeetingEvent(User.current.getCurrentClassId(), meetingId);
        meetingEventModelList = new ArrayList<>();
        meetingEvent.enqueue(new Callback<List<MeetingEventModel>>() {
            @Override
            public void onResponse(Call<List<MeetingEventModel>> call, Response<List<MeetingEventModel>> response) {
                if (response.body() != null) {
                    meetingEventModelList = response.body();

                    meetingHoursAdapter = new MeetingHoursAdapter(MeetingHoursActivity.this, meetingEventModelList.get(0).getTimeSlotModel());
                    meetingHourListView.setAdapter(meetingHoursAdapter);
                }

            }

            @Override
            public void onFailure(Call<List<MeetingEventModel>> call, Throwable t) {

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

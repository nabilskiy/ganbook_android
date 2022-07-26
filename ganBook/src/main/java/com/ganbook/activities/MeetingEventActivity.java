package com.ganbook.activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ganbook.app.MyApp;
import com.ganbook.models.SuccessAnswer;
import com.ganbook.s3transferutility.Constants;
import com.ganbook.user.User;
import com.project.ganim.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MeetingEventActivity extends BaseAppCompatActivity {

    private int mYear, mMonth, mDay, mHour, mMinute;
    private String dayTime;
    private TextView meetingStartDate, meetingEndDate;
    private EditText meetingTitle, meetingDuration, meetingComment;
    private Spinner meetingDurationSpinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting_event);
        setActionBar(getString(R.string.add_meeting_title), true);

        meetingStartDate = findViewById(R.id.meetingStartDateEditText);
        meetingEndDate = findViewById(R.id.meetingEndDateEditText);
        meetingTitle = findViewById(R.id.meetingTitleEditText);
        meetingComment = findViewById(R.id.meetingCommentEditText);
        meetingDurationSpinner = findViewById(R.id.meetingTimeSlotDurationSpinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.duration_array, R.layout.spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        meetingDurationSpinner.setAdapter(adapter);


        meetingStartDate.setOnClickListener(v -> {
            DatePickerDialog dialog = datePicker(v.getId());
            dialog.show();
        });

        meetingEndDate.setOnClickListener(v -> {
            DatePickerDialog dialog = datePicker(v.getId());
            Date date = null;
            try {
                date = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US).parse(dayTime + " " + mHour + ":" + mMinute);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (date != null) {
                dialog.getDatePicker().setMaxDate(date.getTime());
                dialog.getDatePicker().setMinDate(date.getTime());
            }

            dialog.show();
        });


    }

    private DatePickerDialog datePicker(final int textViewId){

        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(this,
                (view, year, monthOfYear, dayOfMonth) -> {
                    dayTime = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                    tiemPicker(textViewId);
                }, mYear, mMonth, mDay);
    }

    private void tiemPicker(final int textViewId){

        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);


        TimePickerDialog timePickerDialog = new TimePickerDialog(MeetingEventActivity.this,
                (view, hourOfDay, minute) -> {

                    mHour = hourOfDay;
                    mMinute = minute;

                    Date date = null;
                    try {
                        date = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(dayTime + " " + hourOfDay + ":" + minute);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    String formattedDate = null;

                    if(textViewId == R.id.meetingStartDateEditText) {
                        formattedDate = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(date);
                        meetingStartDate.setText(formattedDate);
                    } else {
                        Calendar cal = Calendar.getInstance();
                        Calendar todayCal = Calendar.getInstance();

                        int today = todayCal.get(Calendar.DAY_OF_MONTH);
                        cal.setTime(date);

                        if (today == cal.get(Calendar.DAY_OF_MONTH)) {
                            if(cal.getTimeInMillis() < c.getTimeInMillis()) {
                                Toast.makeText(MeetingEventActivity.this, getString(R.string.end_time_error_text), Toast.LENGTH_LONG).show();

                            } else {
                                formattedDate = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US).format(date);
                                meetingEndDate.setText(formattedDate);
                            }
                        } else {

                            if (cal.before(c)) {
                                Toast.makeText(MeetingEventActivity.this, getString(R.string.end_time_error_text), Toast.LENGTH_LONG).show();
                            } else {
                                formattedDate = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US).format(date);
                                meetingEndDate.setText(formattedDate);

                            }
                        }

                    }



                }, mHour, mMinute, true);

        timePickerDialog.updateTime(mHour, mMinute);
        timePickerDialog.show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.single_meeting_menu, menu);

        MenuItem menuItem = menu.findItem(R.id.next_menu);


        menuItem.getActionView().setOnClickListener(v -> {
            String meetingDuration = meetingDurationSpinner.getSelectedItem().toString().replaceAll("\\D+","");
            createMeetingEvent(meetingTitle.getText().toString(), User.current.getCurrentClassId(), meetingStartDate.getText().toString(), meetingEndDate.getText().toString(), meetingDuration, meetingComment.getText().toString());
        });

        return true;
    }

    private void createMeetingEvent(String meetingTitle, String meetingClass, String meetingStart, String meetingEnd, String meetingDuration, String meetingComment) {

        Call<SuccessAnswer> call = ganbookApiInterfacePOST.addMeetingEvent(meetingTitle, meetingStart, meetingEnd, meetingClass, meetingComment, meetingDuration);

        call.enqueue(new Callback<SuccessAnswer>() {
            @Override
            public void onResponse(Call<SuccessAnswer> call, Response<SuccessAnswer> response) {
                SuccessAnswer answer = response.body();

                if(answer != null && answer.isSuccess()) {
                    Call<SuccessAnswer> pushCall = ganbookApiInterfacePOST.createMeetingPush(User.getUserId(), User.current.getCurrentClassId(), MyApp.APP_NAME, getString(R.string.meeting_push_text));

                    pushCall.enqueue(new Callback<SuccessAnswer>() {
                        @Override
                        public void onResponse(Call<SuccessAnswer> call, Response<SuccessAnswer> response) {
                            if (response.body() != null) {
                                if(response.isSuccessful()) {
                                    Log.d("RESPONSE", response.body().toString());
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<SuccessAnswer> call, Throwable t) {

                        }
                    });

                    User.current.setCurrentClassMeeting("1");
                    Intent intent = new Intent(MeetingEventActivity.this, MeetingEventListActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
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
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        Objects.requireNonNull(inputMethodManager).hideSoftInputFromWindow(
                Objects.requireNonNull(activity.getCurrentFocus()).getWindowToken(), 0);
    }

    @Override
    protected void onResume() {
        super.onResume();

        View rootView =((ViewGroup)findViewById(android.R.id.content)).
                getChildAt(0);
        rootView.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                hideSoftKeyboard(MeetingEventActivity.this);
            }
        });
    }
}

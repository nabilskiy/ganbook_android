package com.ganbook.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import androidx.collection.ArrayMap;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.ganbook.amazinglist.special.CustomToast;
import com.ganbook.app.MyApp;
import com.ganbook.models.KidModel;
import com.ganbook.models.answers.EventsAnswer;
import com.ganbook.models.events.UpdateEvent;
import com.ganbook.ui.EventsAdapter;
import com.ganbook.user.User;
import com.ganbook.utils.AlertUtils;
import com.ganbook.utils.DateFormatter;
import com.ganbook.utils.StrUtils;

import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;
import com.project.ganim.R;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Noa on 29/11/2015.
 */
public class AddEventActivity extends BaseAppCompatActivity implements TimePickerDialog.OnTimeSetListener,
        DatePickerDialog.OnDateSetListener{

    private static final String TAG = AddEventActivity.class.getName();
    private static int SELECT_KIDS = 112;

    TextView event_type_tv, start_event_date , end_event_date, end_event_hour, start_event_hour, num_selected_kids_tv;
    EditText event_title_et, et_edit_comment;
    RelativeLayout event_start_date_layout, event_end_date_layout, kids_layout;
    int start_year, start_month, start_day, start_hour, start_minute;
    int end_year, end_month, end_day, end_hour, end_minute;
    String start_date_string, end_date_string, start_hour_string, end_hour_string;
    Switch all_day_switch,day_off_switch;

    ViewSwitcher ks_switcher, bd_switcher, hl_switcher, tr_switcher, otr_switcher;
    SweetAlertDialog progress;

    boolean all_kids;
    HashMap<String,String> kids_names;
    String event_type, event_id;
    private int pos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MyApp.sendAnalytics("add-event-ui", "add-event-ui-"+User.getId(), "add-event-ui", "AddEvent");

        progress = AlertUtils.createProgressDialog(this, getString(R.string.operation_proceeding));

        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        setContentView(R.layout.activity_addevent);

        setActionBar(getString(R.string.add_event), false);

        final Activity a = this;

        start_event_date = (TextView) findViewById(R.id.start_event_date);
        end_event_date = (TextView) findViewById(R.id.end_event_date);
        start_event_hour = (TextView) findViewById(R.id.start_event_hour);
        end_event_hour = (TextView) findViewById(R.id.end_event_hour);

        event_type_tv = (TextView) findViewById(R.id.event_type_tv);
        kids_layout = (RelativeLayout) findViewById(R.id.kids_layout);
        et_edit_comment = (EditText) findViewById(R.id.et_edit_comment);
        num_selected_kids_tv = (TextView) findViewById(R.id.num_selected_kids_tv);
        event_title_et = (EditText) findViewById(R.id.event_title_et);

        all_day_switch = (Switch)findViewById(R.id.all_day_switch);
        day_off_switch = (Switch)findViewById(R.id.day_off_switch);
        ks_switcher = (ViewSwitcher) findViewById(R.id.ks_switcher);
        bd_switcher = (ViewSwitcher) findViewById(R.id.bd_switcher);
        hl_switcher = (ViewSwitcher) findViewById(R.id.hl_switcher);
        tr_switcher = (ViewSwitcher) findViewById(R.id.tr_switcher);
        otr_switcher = (ViewSwitcher) findViewById(R.id.otr_switcher);

        kids_names = new HashMap<String, String>();

        all_day_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    start_event_hour.setVisibility(View.GONE);
                    end_event_hour.setVisibility(View.GONE);
                }else{
                    start_event_hour.setVisibility(View.VISIBLE);
                    end_event_hour.setVisibility(View.VISIBLE);
                }
            }
        });

        initSwitchers();

        final Calendar c = Calendar.getInstance();

        Intent i = getIntent();
        event_id = i.getStringExtra("event_id");
        pos = i.getIntExtra("pos", -1);
        if(event_id != null) //edit
        {
            setToolbarTitle(getString(R.string.edit_event));

            event_title_et.setText(i.getStringExtra("event_title"));
            et_edit_comment.setText(i.getStringExtra("event_comments"));
            setSelectefSwitch(i.getStringExtra("event_type"));
            all_kids = "1".equals(i.getStringExtra("all_kids")) ? true : false;

            if(!all_kids)
            {
                String[] kids = i.getStringArrayExtra("kids_ids");
                num_selected_kids_tv.setText(String.valueOf(kids.length));

                for (String id : kids)
                {
                    kids_names.put(id,"");
                }
            }

            day_off_switch.setChecked("1".equals(i.getStringExtra("day_off")));

            boolean all_day = "1".equals(i.getStringExtra("all_day"));

            all_day_switch.setChecked(all_day);

//            in.putExtra("event_date",event_date);
//            in.putExtra("start_hour",start_hour);
//            in.putExtra("end_hour",end_hour);
//            in.putExtra("start_date",start_date);
//            in.putExtra("end_date",end_date);

            String event_start_date = i.getStringExtra("event_start_date");

            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            try {
                Date date = formatter.parse(event_start_date);
                c.setTime(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        else
        {
            setToolbarTitle(getString(R.string.add_event));
        }

        start_year = c.get(Calendar.YEAR);
        start_month = c.get(Calendar.MONTH);
        start_day = c.get(Calendar.DAY_OF_MONTH);
        start_hour = c.get(Calendar.HOUR_OF_DAY);
        start_minute = c.get(Calendar.MINUTE);

        GregorianCalendar calendar = new GregorianCalendar(start_year, start_month, start_day, start_hour, start_minute);
        SimpleDateFormat formatter = new SimpleDateFormat("EE,dd MMMM yyyy");
        formatter.setCalendar(calendar);
        start_date_string = formatter.format(calendar.getTime());

        start_event_date.setText(start_date_string);

        formatter = new SimpleDateFormat("HH:mm");
        formatter.setCalendar(calendar);
        start_hour_string = formatter.format(calendar.getTime());

        start_event_hour.setText(start_hour_string);

        if(event_id != null) //edit
        {
            String event_end_date = i.getStringExtra("event_end_date");
            formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");

            try {
                Date date = formatter.parse(event_end_date);
                calendar.setTime(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }
        else {
            calendar.add(Calendar.HOUR, 1);
        }

        end_year = calendar.get(Calendar.YEAR);
        end_month = calendar.get(Calendar.MONTH);
        end_day = calendar.get(Calendar.DAY_OF_MONTH);
        end_hour = calendar.get(Calendar.HOUR_OF_DAY);
        end_minute = calendar.get(Calendar.MINUTE);

        formatter = new SimpleDateFormat("EE,dd MMMM yyyy");
        formatter.setCalendar(calendar);
        end_date_string = formatter.format(calendar.getTime());

        end_event_date.setText(end_date_string);

        formatter = new SimpleDateFormat("HH:mm");
        formatter.setCalendar(calendar);
        end_hour_string = formatter.format(calendar.getTime());

        end_event_hour.setText(end_hour_string);

        start_event_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDatePicker(start_year, start_month, start_day, "start_event_date");
            }
        });

        start_event_hour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openTimePicker(start_hour, start_minute, "start_event_hour");
            }
        });

        end_event_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDatePicker(end_year, end_month, end_day, "end_event_date");
            }
        });

        end_event_hour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openTimePicker(end_hour, end_minute, "end_event_hour");
            }
        });


        kids_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(event_type == null)
                {
                    CustomToast.show(AddEventActivity.this, R.string.select_event_type);
                    return;
                }
                else {
                    Intent intent = new Intent(AddEventActivity.this, SelectKidsActivity.class);

                    intent.putExtra("all_kids", all_kids);
                    ArrayList<String> ids = new ArrayList<String>();
                    for(String id: kids_names.keySet())
                    {
                        ids.add(id);
                    }
                    intent.putExtra("kids_ids", ids);

                    startActivityForResult(intent, SELECT_KIDS);
                }
            }
        });

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.save_button_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.save_button:

                if(event_id != null) //edit
                {
                    editEvent();
                }
                else
                {
                    saveEvent();
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initSwitchers()
    {
        ks_switcher.setDisplayedChild(1);

        ks_switcher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ks_switcher.getDisplayedChild() == 1) //inactive
                {
                    ks_switcher.showPrevious();

                    bd_switcher.setDisplayedChild(1);
                    hl_switcher.setDisplayedChild(1);
                    tr_switcher.setDisplayedChild(1);
                    otr_switcher.setDisplayedChild(1);

                    event_type_tv.setTextColor(Color.BLACK);
                    event_type_tv.setText(getString(R.string.kabbalat_shabbat));

                    all_day_switch.setEnabled(false);
                    all_day_switch.setChecked(false);

                    day_off_switch.setEnabled(false);
                    day_off_switch.setChecked(false);

                    all_kids = false;
                    num_selected_kids_tv.setText("0");

                    event_type = EventsAdapter.SHABAT;
                }
            }


        });

        bd_switcher.setDisplayedChild(1);

        bd_switcher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (bd_switcher.getDisplayedChild() == 1) //inactive
                {
                    bd_switcher.showPrevious();

                    ks_switcher.setDisplayedChild(1);
                    hl_switcher.setDisplayedChild(1);
                    tr_switcher.setDisplayedChild(1);
                    otr_switcher.setDisplayedChild(1);

                    event_type_tv.setTextColor(Color.BLACK);
                    event_type_tv.setText(getString(R.string.birthday));

                    all_day_switch.setEnabled(false);
                    all_day_switch.setChecked(false);

                    day_off_switch.setEnabled(false);
                    day_off_switch.setChecked(false);

                    all_kids = false;
                    num_selected_kids_tv.setText("0");

                    event_type = EventsAdapter.BD;
                }
            }
        });

        hl_switcher.setDisplayedChild(1);

        hl_switcher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (hl_switcher.getDisplayedChild() == 1) //inactive
                {
                    hl_switcher.showPrevious();

                    ks_switcher.setDisplayedChild(1);
                    bd_switcher.setDisplayedChild(1);
                    tr_switcher.setDisplayedChild(1);
                    otr_switcher.setDisplayedChild(1);

                    event_type_tv.setTextColor(Color.BLACK);
                    event_type_tv.setText(getString(R.string.vacation));

                    all_day_switch.setEnabled(true);
                    day_off_switch.setEnabled(true);

                    all_kids = true;
                    num_selected_kids_tv.setText(getString(R.string.all_kids));

                    event_type = EventsAdapter.HOLIDAY;
                }
            }
        });

        tr_switcher.setDisplayedChild(1);

        tr_switcher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (tr_switcher.getDisplayedChild() == 1) //inactive
                {
                    tr_switcher.showPrevious();

                    ks_switcher.setDisplayedChild(1);
                    bd_switcher.setDisplayedChild(1);
                    hl_switcher.setDisplayedChild(1);
                    otr_switcher.setDisplayedChild(1);

                    event_type_tv.setTextColor(Color.BLACK);
                    event_type_tv.setText(getString(R.string.trip));

                    all_day_switch.setEnabled(true);
                    day_off_switch.setEnabled(true);

                    all_kids = true;
                    num_selected_kids_tv.setText(getString(R.string.all_kids));

                    event_type = EventsAdapter.TRIP;
                }
            }
        });

        otr_switcher.setDisplayedChild(1);

        otr_switcher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (otr_switcher.getDisplayedChild() == 1) //inactive
                {
                    otr_switcher.showPrevious();

                    ks_switcher.setDisplayedChild(1);
                    bd_switcher.setDisplayedChild(1);
                    tr_switcher.setDisplayedChild(1);
                    hl_switcher.setDisplayedChild(1);

                    event_type_tv.setTextColor(Color.BLACK);
                    event_type_tv.setText(getString(R.string.other));

                    all_day_switch.setEnabled(true);
                    day_off_switch.setEnabled(true);

                    all_kids = true;
                    num_selected_kids_tv.setText(getString(R.string.all_kids));

                    event_type = EventsAdapter.OTHER;
                }
            }
        });
    }

    private void setSelectefSwitch(String event_type)
    {
        switch (event_type)
        {
            case EventsAdapter.BD:
            {
                bd_switcher.performClick();
                break;
            }
            case EventsAdapter.SHABAT:
            {
                ks_switcher.performClick();
                break;
            }
            case EventsAdapter.HOLIDAY:
            {
                hl_switcher.performClick();
                break;
            }
            case EventsAdapter.TRIP:
            {
                tr_switcher.performClick();
                break;
            }
            case EventsAdapter.OTHER:
            {
                otr_switcher.performClick();
                break;
            }
        }

    }

    private void editEvent() {
        final Activity a = this;

        final String title = event_title_et.getText().toString();
        final String event_start_date = formatDateIntToString(start_year, start_month, start_day, start_hour, start_minute, "yyyy-MM-dd HH:mm");
        final String event_end_date = formatDateIntToString(end_year, end_month, end_day, end_hour, end_minute, "yyyy-MM-dd HH:mm");
        final String comments = et_edit_comment.getText().toString();
        final String all_day = all_day_switch.isChecked() ? "1" : "0";
        final String day_off = day_off_switch.isChecked() ? "1" : "0";
        final String _all_kids = all_kids ? "1" : "0";

        if (validate(title)) {
            CustomToast.show(AddEventActivity.this, R.string.event_data_incomplete);
            return;
        }

        if (!all_kids && kids_names.keySet().size() == 0) {
            CustomToast.show(AddEventActivity.this, R.string.select_one_kid);
            return;
        }

        progress.show();

        Map<String, Object> map = new ArrayMap<>();

        map.put("android", "1");
        map.put("event_id", event_id);
        map.put("title", title);
        map.put("class_id", User.current.getCurrentClassId());
        map.put("type", event_type);
        map.put("event_start_date", event_start_date);
        map.put("event_end_date", event_end_date);
        map.put("comments", comments);
        map.put("all_day", all_day);
        map.put("day_off", day_off);
        map.put("all_kids", _all_kids);
        map.put("kids", getJsonKid());

        JSONObject json = new JSONObject(map);

        Log.d(TAG, "saveEvent: json = " + json);

        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),json.toString());

        Call<List<EventsAnswer>> call = ganbookApiInterfaceGET.updateEvent(body);

        call.enqueue(new Callback<List<EventsAnswer>>() {
            @Override
            public void onResponse(Call<List<EventsAnswer>> call, Response<List<EventsAnswer>> response) {

                progress.hide();

                List<EventsAnswer> list = response.body();
                if (list == null) {

                } else {

                    Log.d(TAG, "onResponse: added event = " + list);

                    list = processList(list);

                    Log.d(TAG, "onResponse: list = " + list);

                    Intent in = new Intent();

                    in.putExtra("event_id",event_id);
                    in.putExtra("event_date",DateFormatter.formatStringDate(event_start_date,"yyyy-MM-dd HH:mm","yyyy-MM-dd"));
                    in.putExtra("event_title",title);
                    in.putExtra("event_type",event_type);
                    in.putExtra("start_hour",DateFormatter.formatStringDate(event_start_date,"yyyy-MM-dd HH:mm","HH:mm"));
                    in.putExtra("end_hour",DateFormatter.formatStringDate(event_end_date,"yyyy-MM-dd HH:mm","HH:mm"));
                    in.putExtra("start_date", DateFormatter.formatStringDate(event_start_date,"yyyy-MM-dd HH:mm","MMMM dd"));
                    in.putExtra("end_date",DateFormatter.formatStringDate(event_end_date,"yyyy-MM-dd HH:mm","MMMM dd"));
                    in.putExtra("day_off",day_off);
                    in.putExtra("event_comments",comments);

                    in.putExtra("all_kids",_all_kids);
                    in.putExtra("all_day",all_day);

                    String[] kids_ids = new String[kids_names.keySet().size()];
                    String[] kids = new String[kids_names.keySet().size()];

                    int ind = 0;
                    for(String id : kids_names.keySet())
                    {
                        kids_ids[ind] = id;
                        kids[ind] = kids_names.get(id);

                        ind++;
                    }

                    in.putExtra("kids_ids",kids_ids);
                    in.putExtra("event_kids",kids);

                    in.putExtra("event_start_date",event_start_date);
                    in.putExtra("event_end_date",event_end_date);

                    if (getParent() == null) {
                        setResult(Activity.RESULT_OK, in);
                    } else {
                        getParent().setResult(Activity.RESULT_OK, in);
                    }

                    a.finish();

                    EventBus.getDefault().postSticky(new UpdateEvent(list.get(0), pos));
                }
            }

            @Override
            public void onFailure(Call<List<EventsAnswer>> call, Throwable t) {

                progress.hide();

                Log.e(TAG, "onFailure: error while update = " + Log.getStackTraceString(t));
            }
        });
    }

    private void saveEvent() {
        final Activity a = this;

        String title = event_title_et.getText().toString();
        String event_start_date = formatDateIntToString(start_year, start_month, start_day, start_hour, start_minute, "yyyy-MM-dd HH:mm");
        String event_end_date = formatDateIntToString(end_year, end_month, end_day, end_hour, end_minute, "yyyy-MM-dd HH:mm");
        String comments = et_edit_comment.getText().toString();
        String all_day = all_day_switch.isChecked() ? "1" : "0";
        String day_off = day_off_switch.isChecked() ? "1" : "0";
        String _all_kids = all_kids ? "1" : "0";

        if (validate(title)) {
            CustomToast.show(AddEventActivity.this, R.string.event_data_incomplete);
            return;
        }

        if (!all_kids && kids_names.keySet().size() == 0) {
            CustomToast.show(AddEventActivity.this, R.string.select_one_kid);
            return;
        }

        progress.show();

        Map<String, Object> map = new ArrayMap<>();

        map.put("android", "1");
        map.put("title", title);
        map.put("class_id", User.current.getCurrentClassId());
        map.put("type", event_type);
        map.put("event_start_date", event_start_date);
        map.put("event_end_date", event_end_date);
        map.put("comments", comments);
        map.put("all_day", all_day);
        map.put("day_off", day_off);
        map.put("all_kids", _all_kids);
        map.put("kids", getJsonKid());

        JSONObject json = new JSONObject(map);

        Log.d(TAG, "saveEvent: json = " + json);

        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),json.toString());

        Call<List<EventsAnswer>> call = ganbookApiInterfaceGET.createEvent(body);

        call.enqueue(new Callback<List<EventsAnswer>>() {
            @Override
            public void onResponse(Call<List<EventsAnswer>> call, Response<List<EventsAnswer>> response) {

                progress.hide();

                List<EventsAnswer> list = response.body();
                if (list == null) {

                } else {

                    Log.d(TAG, "onResponse: added event = " + list);

                    list = processList(list);

                    Log.d(TAG, "onResponse: list = " + list);

                    Intent returnIntent = new Intent();
                    returnIntent.putParcelableArrayListExtra("events", new ArrayList<Parcelable>(list));

                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                }

            }

            @Override
            public void onFailure(Call<List<EventsAnswer>> call, Throwable t) {

                progress.hide();

                Log.e(TAG, "onFailure: error while create event = " + Log.getStackTraceString(t));
            }
        });
    }

    private List<EventsAnswer> processList(List<EventsAnswer> list) {

        if("0".equals(list.get(0).getEventModelList().get(0).all_kids)) {

            List<KidModel> kidsList = new ArrayList<>();

            String[] kids_ids = new String[kids_names.keySet().size()];
            String[] kids = new String[kids_names.keySet().size()];

            int ind = 0;
            for(String id : kids_names.keySet())
            {
                kids_ids[ind] = id;
                kids[ind] = kids_names.get(id);

                ind++;

                KidModel kidModel = new KidModel();
                kidModel.setKid_id(id);
                kidModel.setKid_name(kids_names.get(id));
                kidsList.add(kidModel);
            }

            list.get(0).getEventModelList().get(0).setKids(kidsList);
        }

        return list;
    }

    protected boolean validate(String title) {
        return StrUtils.isEmpty(title) ||
                StrUtils.isEmpty(event_type) ;
    }


    private JSONArray getJsonKid() {

        JSONArray jsonArray = new JSONArray();

        for (String kid_id: kids_names.keySet()) {

            jsonArray.put(kid_id);
        }

        Log.d(TAG, "getJsonKid: kids array = " + jsonArray.toString());

        return jsonArray;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SELECT_KIDS) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                Bundle res = data.getExtras();
                all_kids = res.getBoolean("all_kids");

                if(all_kids)
                {
                    num_selected_kids_tv.setText(getString(R.string.all_kids));
                }
                else
                {
                    kids_names =  (HashMap<String, String>)data.getSerializableExtra("kids_map");

                    Log.d(TAG, "onActivityResult: jobj = " + getJsonKid().toString());

                    num_selected_kids_tv.setText(String.valueOf(kids_names.keySet().size()));
                }

            }
        }
    }

    private void openDatePicker(int start_year, int start_month, int start_day, String tag) {
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                AddEventActivity.this,
                start_year,
                start_month,
                start_day
        );


        dpd.setMinDate(Calendar.getInstance());

        dpd.show(getFragmentManager(), tag);
    }

    private void openTimePicker(int start_hour, int start_minute, String tag) {
        TimePickerDialog dpd = TimePickerDialog.newInstance(
                AddEventActivity.this,
                start_hour,
                start_minute,
                true
        );
        Bundle args = new Bundle();
        args.putInt("num", 1);
        dpd.setArguments(args);

        dpd.show(getFragmentManager(), tag);
    }

    @Override
    public void onDateSet(DatePickerDialog datePicker, int year, int monthOfYear, int dayOfMonth) {
        String tag = datePicker.getTag();

        switch (tag)
        {
            case "start_event_date":
                start_year = year;
                start_month = monthOfYear;
                start_day = dayOfMonth;

                start_date_string = formatDateIntToString(start_year, start_month, start_day, 0, 0, "EE,dd MMMM yyyy");

                start_event_date.setText(start_date_string);

                checkValidDates(true);
                break;
            case "end_event_date":
                end_year = year;
                end_month = monthOfYear;
                end_day = dayOfMonth;

                end_date_string = formatDateIntToString(end_year, end_month, end_day, 0, 0, "EE,dd MMMM yyyy");

                end_event_date.setText(end_date_string);

                checkValidDates(false);
                break;
        }



    }


    private String formatDateIntToString(int year, int monthOfYear, int dayOfMonth, int hour, int minute, String format)
    {
        GregorianCalendar calendar = new GregorianCalendar(year, monthOfYear, dayOfMonth, hour, minute);
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        formatter.setCalendar(calendar);
        return formatter.format(calendar.getTime());
    }

    private String formatHourIntToString(int hour, int minute)
    {
        GregorianCalendar calendar = new GregorianCalendar(0, 0, 0, hour, minute);
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        formatter.setCalendar(calendar);
        return formatter.format(calendar.getTime());
    }

    private Date formatDateIntToDate(int year, int monthOfYear, int dayOfMonth, int hour, int minute)
    {
        GregorianCalendar calendar = new GregorianCalendar(year, monthOfYear, dayOfMonth, hour, minute);
        return calendar.getTime();
    }

    private void checkValidDates(boolean fromStart)
    {
        try {
            Date start_date = formatDateIntToDate(start_year, start_month, start_day, start_hour, start_minute);
            Date end_date = formatDateIntToDate(end_year, end_month, end_day, end_hour, end_minute);

            if(start_date.compareTo(end_date) > 0){
//              Log.v("app","Date1 is after Date2");

                if(fromStart) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(start_date);
                    calendar.add(Calendar.HOUR, 1);

                    end_year = calendar.get(Calendar.YEAR);
                    end_month = calendar.get(Calendar.MONTH);
                    end_day = calendar.get(Calendar.DAY_OF_MONTH);
                    end_hour = calendar.get(Calendar.HOUR_OF_DAY);
                    end_minute = calendar.get(Calendar.MINUTE);

                    end_date_string = formatDateIntToString(end_year, end_month, end_day, end_hour, end_minute, "EE,dd MMMM yyyy");

                    end_event_date.setText(end_date_string);

                    end_hour_string = formatHourIntToString(end_hour, end_minute);

                    end_event_hour.setText(end_hour_string);

                }
                else {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(end_date);
                    calendar.add(Calendar.HOUR, -1);

                    start_year = calendar.get(Calendar.YEAR);
                    start_month = calendar.get(Calendar.MONTH);
                    start_day = calendar.get(Calendar.DAY_OF_MONTH);
                    start_hour = calendar.get(Calendar.HOUR_OF_DAY);
                    start_minute = calendar.get(Calendar.MINUTE);

                    start_date_string = formatDateIntToString(start_year, start_month, start_day, start_hour, start_minute, "EE,dd MMMM yyyy");

                    start_event_date.setText(start_date_string);

                    start_hour_string = formatHourIntToString(start_hour, start_minute);

                    start_event_hour.setText(start_hour_string);
                }

            }

        } catch(Exception e) { }
    }

    @Override
    public void onTimeSet(TimePickerDialog timePicker, int hourOfDay, int minute, int second)
    {
        TimePickerDialog startPicker = (TimePickerDialog) getFragmentManager().findFragmentByTag("start_event_hour");

        TimePickerDialog endPicker = (TimePickerDialog) getFragmentManager().findFragmentByTag("end_event_hour");

        if(startPicker != null)
        {
            start_hour = hourOfDay;
            start_minute = minute;

            start_hour_string = formatHourIntToString(start_hour, start_minute);

            start_event_hour.setText(start_hour_string);

            checkValidDates(true);
        }
        else if(endPicker != null)
        {
            end_hour = hourOfDay;
            end_minute = minute;

            end_hour_string = formatHourIntToString(end_hour, end_minute);

            end_event_hour.setText(end_hour_string);

            checkValidDates(false);
        }


    }
}

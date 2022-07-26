package com.ganbook.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ganbook.app.MyApp;
import com.ganbook.interfaces.GanbookApiInterface;
import com.ganbook.models.SuccessAnswer;
import com.ganbook.ui.EventsAdapter;
import com.ganbook.user.User;
import com.ganbook.utils.AlertUtils;
import com.ganbook.utils.Const;

import com.project.ganim.R;

import javax.inject.Inject;
import javax.inject.Named;

import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Noa on 30/11/2015.
 */
public class EventActivity extends BaseAppCompatActivity {

    private static final String TAG = EventActivity.class.getName();
    private static int UPDATE_EVENT = 3056;

    public static EventActivity inst;
    private ListView kids_list;
    private ImageView event_iv;
    private TextView event_title_tv, start_date_tv, start_hour_tv, end_date_tv, end_hour_tv, day_off_tv, comments_tv;
    private RelativeLayout delete_view;

    String event_id;
    String event_date;
    String event_title;
    String event_type;
    String start_hour;
    String end_hour;
    String start_date;
    String end_date;
    String day_off;
    String event_comments;
    String all_kids;
    String all_day;
    String[] kids;
    String[] kids_ids;
    String event_start_date;
    String event_end_date;
    SweetAlertDialog progress;
    private int pos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        inst = this;

        progress = AlertUtils.createProgressDialog(this, getString(R.string.operation_proceeding));

        MyApp.sendAnalytics("event-activity-ui", "event-activity-ui"+ User.getId(), "event-activity-ui", "EventActivity");


        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        setContentView(R.layout.activity_event);
        setActionBar("", true);

//        TextView right_tv = (TextView) findViewById(R.id.right_tv);

        event_title_tv = (TextView) findViewById(R.id.event_title_tv);
        event_iv = (ImageView) findViewById(R.id.event_iv);
        start_date_tv = (TextView) findViewById(R.id.start_date_tv);
        start_hour_tv = (TextView) findViewById(R.id.start_hour_tv);
        end_date_tv = (TextView) findViewById(R.id.end_date_tv);
        end_hour_tv = (TextView) findViewById(R.id.end_hour_tv);
        day_off_tv = (TextView) findViewById(R.id.day_off_tv);
        comments_tv = (TextView) findViewById(R.id.comments_tv);

        kids_list = (ListView) findViewById(R.id.kids_list);

        delete_view = (RelativeLayout) findViewById(R.id.delete_view);

        if(User.isParent())
        {
            delete_view.setVisibility(View.GONE);
        }
        else
        {
            delete_view.setVisibility(View.VISIBLE);
        }

        Intent intent = getIntent();
        initFromIntent(intent);

        updateUI();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.save_button_menu, menu);
        menu.findItem(R.id.save_button).setTitle(getString(R.string.edit_btn));

        if (User.isParent())
            menu.findItem(R.id.save_button).setVisible(false);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.save_button:

                Intent in = new Intent(EventActivity.this, AddEventActivity.class);

                in.putExtra("pos", pos);
                in.putExtra("event_id",event_id);
                in.putExtra("event_date",event_date);
                in.putExtra("event_title",event_title);
                in.putExtra("event_type",event_type);
                in.putExtra("start_hour",start_hour);
                in.putExtra("end_hour",end_hour);
                in.putExtra("start_date",start_date);
                in.putExtra("end_date",end_date);
                in.putExtra("day_off",day_off);
                in.putExtra("event_comments",event_comments);
                in.putExtra("event_kids",kids);
                in.putExtra("all_kids",all_kids);
                in.putExtra("all_day",all_day);
                in.putExtra("kids_ids",kids_ids);
                in.putExtra("event_start_date",event_start_date);
                in.putExtra("event_end_date",event_end_date);

                startActivityForResult(in, UPDATE_EVENT);
                break;

            case android.R.id.home:

                onBackPressed();

                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initFromIntent(Intent intent) {
        pos = intent.getIntExtra("pos", -1);
        event_id = intent.getStringExtra("event_id");
        event_date = intent.getStringExtra("event_date");
        event_title = intent.getStringExtra("event_title");
        event_type = intent.getStringExtra("event_type");
        start_hour = intent.getStringExtra("start_hour");
        end_hour = intent.getStringExtra("end_hour");
        start_date = intent.getStringExtra("start_date");
        end_date = intent.getStringExtra("end_date");
        day_off = intent.getStringExtra("day_off");
        event_comments = intent.getStringExtra("event_comments");
        all_kids = intent.getStringExtra("all_kids");
        all_day = intent.getStringExtra("all_day");
        kids = intent.getStringArrayExtra("event_kids");
        kids_ids = intent.getStringArrayExtra("kids_ids");
        event_start_date = intent.getStringExtra("event_start_date");
        event_end_date = intent.getStringExtra("event_end_date");
    }

    private void updateUI()
    {
        event_title_tv.setText(event_title);
        int drawable = EventsAdapter.getEventTypeDrawable(event_type);
        event_iv.setBackgroundResource(drawable);

        if("1".equals(day_off))
        {
            day_off_tv.setText(getString(R.string.yes));
        }
        else
        {
            day_off_tv.setText(getString(R.string.no));
        }

        if("1".equals(all_day))
        {
            start_date_tv.setText(getString(R.string.from));
            start_hour_tv.setText(start_date);
            end_date_tv.setText(getString(R.string.until));
            end_hour_tv.setText(end_date);
        }
        else
        {
            start_date_tv.setText(start_date);
            start_hour_tv.setText(start_hour);
            end_date_tv.setText(end_date);
            end_hour_tv.setText(end_hour);
        }


        if(event_comments == null)
        {
            comments_tv.setText(getString(R.string.no_comments));
        }
        else {
            comments_tv.setText(event_comments);
        }

        ListAdapter adapter = new ListAdapter(inst,R.layout.event_list_item,kids);
        kids_list.setAdapter(adapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == UPDATE_EVENT) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                initFromIntent(data);
                updateUI();
            }
        }
    }

    private void doDelete() {

        Call<SuccessAnswer> call = ganbookApiInterfacePOST.deleteEvent(event_id);
        progress.show();
        call.enqueue(new Callback<SuccessAnswer>() {
            @Override
            public void onResponse(Call<SuccessAnswer> call, Response<SuccessAnswer> response) {

                progress.hide();
                SuccessAnswer successAnswer = response.body();

                if (successAnswer == null) {

                } else {

                    Log.d(TAG, "onResponse: success delete event " + successAnswer);

                    if (successAnswer.isSuccess()) {

                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("event_id", event_id);

                        setResult(Const.RESULT_DELETE_EVENT, returnIntent);
                        finish();
                    } else {

                    }
                }
            }

            @Override
            public void onFailure(Call<SuccessAnswer> call, Throwable t) {

                progress.hide();
                Log.e(TAG, "onFailure: error while delete event = " + Log.getStackTraceString(t));
            }
        });
    }

    public void deleteEvent(View view) {
        AlertDialog.Builder adb = new AlertDialog.Builder(EventActivity.this);
        //		adb.setView(alertDialogView);
        adb.setTitle(R.string.delete_event_text);
        adb.setIcon(R.drawable.ic_launcher);
        adb.setPositiveButton(R.string.popup_btn_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                doDelete();
            }
        });
        adb.setNegativeButton(R.string.popup_btn_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // no op
            }
        });

        adb.show();
    }

    public class ListAdapter extends ArrayAdapter<String>{

        String data[] = null;
        Context context;
        int layoutResourceId;

        public ListAdapter(Context context, int layoutResourceId,String[] data) {
            super(inst, layoutResourceId, data);
            this.data = data;
            this.context = context;
            this.layoutResourceId = layoutResourceId;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;
            ListHolder holder = null;

            if(row == null)
            {
                LayoutInflater inflater = (inst).getLayoutInflater();
                row = inflater.inflate(layoutResourceId, parent, false);

                holder = new ListHolder();
                holder.txtTitle = (TextView)row.findViewById(R.id.txtTitle);

                row.setTag(holder);
            }
            else
            {
                holder = (ListHolder)row.getTag();
            }

            String str = data[position];
            holder.txtTitle.setText(str);

            return row;
        }

        class ListHolder
        {
            TextView txtTitle;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (this == inst) {
            inst = null;
        }
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
    }

    @Override
    protected void onPause() {

        super.onPause();
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
    }
}

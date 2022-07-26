package com.ganbook.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.ganbook.amazinglist.special.CustomToast;
import com.ganbook.app.MyApp;
import com.ganbook.communication.ICompletionHandler;
import com.ganbook.communication.datamodel.ResultObj;
import com.ganbook.communication.json.getvaadclasses_response;
import com.ganbook.communication.json.transmitter.JsonTransmitter;
import com.ganbook.user.User;

import com.project.ganim.R;

import java.util.ArrayList;

/**
 * Created by Noa on 07/10/2015.
 */
public class PtaActivity extends BaseAppCompatActivity {

    ListView pta_list;

    private static final int HEADER = 0;
    private static final int ITEM = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MyApp.sendAnalytics("pta-ui", "pta"+"-ui"+ User.getId(), "pta-ui", "PTAScreen");

        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        setContentView(R.layout.activity_pta);
        setActionBar(getString(R.string.assign_pta), true);

        pta_list = (ListView) findViewById(R.id.pta_list);
        call_getvaadclasses();
        invalidateOptionsMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.save_button_menu, menu);

        menu.findItem(R.id.save_button).setVisible(false);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                onBackPressed();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        call_getvaadclasses();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
    }

    @Override
    protected void onPause() {

        super.onPause();
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
    }

    private void call_getvaadclasses()
    {
        final Activity a = this;

        JsonTransmitter.send_getvaadclasses(User.current.getCurrentGanId(),new ICompletionHandler() {
            @Override
            public void onComplete(ResultObj result) {
//				stopProgress();
                if (!result.succeeded) {
                    if (JsonTransmitter.NO_NETWORK_MODE.equals(result.result)) {
                        setContentView(R.layout.no_internet_layout);
                        return;
                    }
                    stopProgress();
                    CustomToast.show(a, result.result);
                } else {
                    int num = result.getNumResponses();

                    ArrayList<getvaadclasses_response> mergedArr = new ArrayList<getvaadclasses_response>();

                    for (int i = 0; i < num; i++) {
                        getvaadclasses_response response = (getvaadclasses_response) result.getResponse(i);

                        mergedArr.add(response.createHeaderItem(response.class_name,response.class_id));

                        if(response.parents == null)
                        {
                            mergedArr.add(response.createEmptyItem(getString(R.string.no_pta_defined)));
                        }
                        else {
                            for (getvaadclasses_response.ParentDetails parentDetail : response.parents) {
                                mergedArr.add(response.createItem(parentDetail));
                            }
                        }
                    }
//
//                    ind = 0;
//
//                    title_class_name.setText(getString(R.string.new_year_title) + " " + classes[ind].class_name);
//
//                    kids_list.setAdapter(new GanKidsAdapter());


                    ListWithCaptionAdapter adapter = new ListWithCaptionAdapter(MyApp.context, mergedArr);
                    pta_list.setAdapter(adapter);
                }
            }
        });
    }

    class ListWithCaptionAdapter extends ArrayAdapter<getvaadclasses_response> {
        private LayoutInflater mInflater;

        public ListWithCaptionAdapter(Context context, ArrayList<getvaadclasses_response> mergedArr) {
            super(context, 0, mergedArr);
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getViewTypeCount() {
            return 2; // list + header items
        }

        @Override
        public int getItemViewType(int position) {
            getvaadclasses_response cur = getItem(position);
            if (cur.isHeader) {
                return HEADER;
            } else {
                return ITEM;
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            getvaadclasses_response cur = getItem(position);
            if (cur.isHeader) {
                return getView_header(position, convertView, parent);
            } else {
                return getView_parent(position, convertView, parent);
            }
        }


        private View getView_parent(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                view = (View) mInflater.inflate(R.layout.my_list_item, null);
            }
            TextView parentTxt = (TextView) view.findViewById(R.id.list_content1);
            getvaadclasses_response cur = getItem(position);
            parentTxt.setText(cur.parent_name);

            return view;
        }


        private View getView_header(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                view = (View) mInflater.inflate(R.layout.class_vaad_header, null);
            }
            final getvaadclasses_response cur = getItem(position);
            TextView text = (TextView) view.findViewById(R.id.separator);
            String caption = cur.class_name;
            text.setText(caption);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MyApp.context,SelectPTAActivity.class);

                    intent.putExtra("class_id", cur.class_id);

                    startActivity(intent);
                }
            });

            return view;
        }

    };
}

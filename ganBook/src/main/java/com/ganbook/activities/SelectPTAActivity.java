package com.ganbook.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.ganbook.amazinglist.special.CustomToast;
import com.ganbook.app.MyApp;
import com.ganbook.communication.ICompletionHandler;
import com.ganbook.communication.datamodel.ResultObj;
import com.ganbook.communication.json.getparentsclass_response;
import com.ganbook.communication.json.transmitter.JsonTransmitter;
import com.ganbook.ui.CircleImageView;
import com.ganbook.user.User;

import com.project.ganim.R;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by Noa on 12/10/2015.
 */
public class SelectPTAActivity extends BaseAppCompatActivity {

    ListView pta_list;
    getparentsclass_response[] parents;
    HashMap<String, String> parentMap = new HashMap<String, String>();
    String class_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MyApp.sendAnalytics("select-pta-ui", "select_pta"+"-ui"+ User.getId(), "select-pta-ui", "SelectPta");


        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        setContentView(R.layout.activity_pta);
        setActionBar(getString(R.string.assign_pta), false);

        Intent i = getIntent();
        class_id = i.getStringExtra("class_id");

        pta_list = (ListView) findViewById(R.id.pta_list);

        pta_list.setSelector(R.drawable.listselector);

        pta_list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        pta_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListView lv = (ListView)parent;
                int firstVisible = lv.getFirstVisiblePosition();
                int realIndex = position - firstVisible;
                View listItem = (View)parent.getChildAt(realIndex);
                CheckBox cb = (CheckBox) listItem.findViewById(R.id.checkBox1);
                getparentsclass_response parent_checked = parents[position];

                if(cb.isChecked())
                {
                    cb.setChecked(false);
                    parentMap.put(parent_checked.parent_id, "0");
                }
                else
                {
                    cb.setChecked(true);
                    parentMap.put(parent_checked.parent_id,"1");
                }
            }
        });



        call_getparentsclass(class_id);
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
                JSONObject parent_ids = new JSONObject(parentMap);

                JsonTransmitter.send_setparentpermission(parent_ids, class_id, new ICompletionHandler() {
                    @Override
                    public void onComplete(ResultObj result) {
                        finish();
                    }
                });
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void call_getparentsclass(String class_id)
    {
        final Activity a = this;

        JsonTransmitter.send_getparentsclass(class_id, new ICompletionHandler() {
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
                    parents = new getparentsclass_response[num];
                    for (int i = 0; i < num; i++) {
                        parents[i] = (getparentsclass_response) result.getResponse(i);
                        if(parents[i].vaad_type.equals("0")) {
                            parentMap.put(parents[i].parent_id, "0");
                        }
                        else
                        {
                            parentMap.put(parents[i].parent_id, "1");
                        }
                    }

                    //title_class_name.setText(getString(R.string.new_year_title) + " " + classes[ind].class_name);

                    pta_list.setAdapter(new GanKidsAdapter());
                }
            }
        });
    }

    private class GanKidsAdapter extends BaseAdapter {

        private LayoutInflater inflater;

        public GanKidsAdapter() {
            this.inflater = LayoutInflater.from(SelectPTAActivity.this);
        }

        @Override
        public int getCount() {
            return parents.length;
        }

        @Override
        public Object getItem(int position) {
            return  parents[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        class ViewHolder {
            CheckBox cb;
            CircleImageView im;
            TextView tv;

        };

        @SuppressLint("InflateParams")
        @Override
        public View getView(int position, View view, ViewGroup parent) {
            ViewHolder vholder;
            if (view == null) {
                view = inflater.inflate(R.layout.my_list_item, null);
                vholder = new ViewHolder();
                vholder.cb = (CheckBox) view.findViewById(R.id.checkBox1);
                vholder.tv = (TextView) view.findViewById(R.id.list_content1);
                view.setTag(vholder);
            }
            else {
                vholder = (ViewHolder) view.getTag();
            }
            getparentsclass_response current = parents[position];

            //getKidPicture(current.kid_pic,vholder.im,current.getDeafultKidImg());

            vholder.cb.setVisibility(View.VISIBLE);

            vholder.tv.setText(current.getName());
            vholder.cb.setTag(Integer.valueOf(position));

            String isChecked = "0";
            if(parentMap != null && parentMap.containsKey((current.parent_id))) {
                isChecked = parentMap.get(current.parent_id);
            } else {
                isChecked = current.vaad_type;
            }

            if("0".equals(isChecked)) {
                vholder.cb.setChecked(false);
            } else {
                vholder.cb.setChecked(true);
            }


            return view;
        }

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
}

package com.ganbook.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import com.ganbook.amazinglist.special.CustomToast;
import com.ganbook.app.MyApp;
import com.ganbook.communication.ICompletionHandler;
import com.ganbook.communication.datamodel.ResultObj;
import com.ganbook.communication.json.getactivekids_Response;
import com.ganbook.communication.json.transmitter.JsonTransmitter;
import com.ganbook.ui.CircleImageView;
import com.ganbook.user.User;

import com.project.ganim.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Noa on 17/02/2016.
 */
public class SelectKidsActivity extends BaseAppCompatActivity {

    HashMap<String, String> kidsMap = new HashMap<String, String>();
    getactivekids_Response[] kids = new getactivekids_Response[0];
    ArrayList<String> selected_kids_ids;

    ListView kids_list;
    Switch all_kids_switch;

    boolean all_kids;
    boolean from_switch = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MyApp.sendAnalytics("select-kids-ui", "select-kids"+"-ui-"+ User.getId(), "select-kids-ui", "SelectKids");

        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        setContentView(R.layout.activity_kids);
        setActionBar("", false);

        kids_list = (ListView) findViewById(R.id.kids_list);

        kids_list.setSelector(R.drawable.listselector);

        kids_list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        kids_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int nFirstPos = kids_list.getFirstVisiblePosition();
                int nWantedPos = position - nFirstPos;

                View listItem = null;

                if ((nWantedPos >= 0) && (nWantedPos <= kids_list.getChildCount()))
                {
                    listItem = kids_list.getChildAt(nWantedPos);
                    if (listItem == null)
                        return;
                    // else we have the view we want

                    CheckBox cb = (CheckBox) listItem.findViewById(R.id.checkBox1);
                    getactivekids_Response kid_checked = kids[position];

                    if(cb.isChecked())
                    {
                        cb.setChecked(false);
                        kidsMap.put(kid_checked.kid_id, "0");
                        from_switch = false;
                        all_kids_switch.setChecked(false);
                    }
                    else
                    {
                        cb.setChecked(true);
                        kidsMap.put(kid_checked.kid_id,"1");
                        checkIfAllKids();
                    }
                }
            }
        });

        all_kids_switch = (Switch) findViewById(R.id.all_kids_switch);

        all_kids_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                String checked = "1";
                all_kids = true;

                if(!b)
                {
                    all_kids = false;
                    checked = "0";
                }

                if(from_switch) {
                    for (String kid_id : kidsMap.keySet()) {
                        kidsMap.put(kid_id, checked);
                    }

                    kids_list.setAdapter(new ClassKidsAdapter());
                }
            }
        });

        all_kids_switch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                from_switch = true;
                return false;
            }
        });

        Intent intent = getIntent();
        all_kids = intent.getBooleanExtra("all_kids",false);
        selected_kids_ids = intent.getStringArrayListExtra("kids_ids");

        all_kids_switch.setChecked(all_kids);

        call_getkidsclass();
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
                Intent data = new Intent();

                HashMap<String,String> kids_ids = getSelectedKidsIds();

                data.putExtra("kids_map",kids_ids);
                data.putExtra("all_kids",all_kids);

                if (getParent() == null) {
                    setResult(Activity.RESULT_OK, data);
                } else {
                    getParent().setResult(Activity.RESULT_OK, data);
                }

                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void checkIfAllKids()
    {
        boolean all = true;

        for (String kid_id :kidsMap.keySet())
        {
            if("0".equals(kidsMap.get(kid_id)))
            {
                all = false;
                break;
            }
        }

        if(all)
        {
            all_kids = true;
            all_kids_switch.setChecked(true);
        }
    }

    private HashMap<String,String> getSelectedKidsIds()
    {
        HashMap<String,String> kids_ids_names = new HashMap<String,String>();

        for (String kid_id :kidsMap.keySet())
        {
            if("1".equals(kidsMap.get(kid_id)))
            {
                for(getactivekids_Response getactivekids : kids)
                {
                    if(getactivekids.kid_id.equals(kid_id))
                    {
                        kids_ids_names.put(kid_id,getactivekids.kid_name);
                    }
                }
            }
        }

        return kids_ids_names;
    }

    private void call_getkidsclass()
    {
        final Activity a = this;

        JsonTransmitter.send_getactivekids(User.current.getCurrentClassId(), new ICompletionHandler() {
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
                    kids = new getactivekids_Response[num];
                    for (int i = 0; i < num; i++)
                    {
                        kids[i] = (getactivekids_Response) result.getResponse(i);

                        if(all_kids) {
                            kidsMap.put(kids[i].kid_id, "1");
                        }
                        else
                        {
                            if(selected_kids_ids.contains(kids[i].kid_id))
                            {
                                kidsMap.put(kids[i].kid_id, "1");
                            }
                            else
                            {
                                kidsMap.put(kids[i].kid_id, "0");
                            }
                        }
                    }

                    kids_list.setAdapter(new ClassKidsAdapter());
                }
            }
        });
    }

    private class ClassKidsAdapter extends BaseAdapter {

        private LayoutInflater inflater;

        public ClassKidsAdapter() {
            this.inflater = LayoutInflater.from(SelectKidsActivity.this);
        }

        @Override
        public int getCount() {
            return kids.length;
        }

        @Override
        public Object getItem(int position) {
            return  kids[position];
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
            getactivekids_Response current = kids[position];

            vholder.cb.setVisibility(View.VISIBLE);

            vholder.tv.setText(current.kid_name);
            vholder.cb.setTag(Integer.valueOf(position));

            if(kidsMap.get(current.kid_id).equals("1"))
            {
                vholder.cb.setChecked(true);
            }
            else
            {
                vholder.cb.setChecked(false);
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

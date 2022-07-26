package com.ganbook.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.ganbook.communication.ICompletionHandler;
import com.ganbook.communication.datamodel.ResultObj;
import com.ganbook.communication.json.GetUserKids_Response;
import com.ganbook.communication.json.getparentsclass_response;
import com.ganbook.communication.json.getparentstosendnotification_Response;
import com.ganbook.communication.json.transmitter.JsonTransmitter;
import com.ganbook.datamodel.UserKids;
import com.ganbook.models.GetParentAnswer;
import com.ganbook.ui.CircleImageView;
import com.ganbook.user.User;
import com.google.gson.Gson;
import com.project.ganim.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class PickDropNotificationsActivity extends BaseAppCompatActivity {

    private HashMap<String, GetParentAnswer> mParents;
    private HashMap<String, Boolean> mParentsChecked;
    private ArrayList mParentsIds;
    private int syncCount;
    ListView mParentsListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_drop_notifications);

        setActionBar(getString(R.string.select_parents), false);

        mParentsListView = (ListView)findViewById(R.id.parents_list);
        mParentsListView.setSelector(R.drawable.listselector);
        mParentsListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        mParentsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListView lv = (ListView)parent;
                int firstVisible = lv.getFirstVisiblePosition();
                int realIndex = position - firstVisible;
                View listItem = (View)parent.getChildAt(realIndex);
                CheckBox cb = (CheckBox) listItem.findViewById(R.id.checkBox1);
                GetParentAnswer parent_checked = parentByPos(position);

                if(cb.isChecked())
                {
                    cb.setChecked(false);
                    mParentsChecked.put(parent_checked.parent_id, false);
                }
                else
                {
                    cb.setChecked(true);
                    mParentsChecked.put(parent_checked.parent_id, true);
                }
            }
        });

        getList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.save_button_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.save_button:
                saveAndQuit();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    void saveAndQuit()
    {
        String list = "";

        for(Object o : mParentsIds) {
            String parentId = (String)o;
            Boolean checked = mParentsChecked.get(parentId);
            if (checked) {
                list += parentId;
                list += ",";
            }
        }

        if (!list.isEmpty())
            list = list.substring(0, list.length()-1);

        JsonTransmitter.send_savenotificationtoparents(User.getUserId(), list, new ICompletionHandler() {
            @Override
            public void onComplete(ResultObj result) {
                finish();
            }
        });
    }

    void getList()
    {
        mParents = new HashMap<String, GetParentAnswer>();
        mParentsChecked = new HashMap<String, Boolean>();
        mParentsIds = new ArrayList();

        syncCount = UserKids.allKids.size();

        for(GetUserKids_Response kid : UserKids.allKids)
        {
            JsonTransmitter.send_getparents2(kid.class_id, kid.current_year, User.getId(), new ICompletionHandler() {
                @Override
                public void onComplete(ResultObj result) {
                    for (int i=0; i<result.getResponseArray().size(); i++)
                    {
                        GetParentAnswer item = (GetParentAnswer)result.getResponseArray().get(i);
                        if (!mParents.containsKey(item.parent_id) && (!item.parent_id.equals(User.getId()))) {
                            mParents.put(item.parent_id, item);
                            mParentsChecked.put(item.parent_id, false);
                            mParentsIds.add(item.parent_id);
                        }
                    }

                    syncCount--;

                    if (syncCount == 0) {

                        JsonTransmitter.send_getparentstosendnotification(User.getUserId(), new ICompletionHandler() {
                                    @Override
                                    public void onComplete(ResultObj result) {
                                        if (result.succeeded)
                                            processChecked(result.getResponseArray());
                                        mParentsListView.setAdapter(new ParentsAdapter());
                                    }
                                });
                    }
                }
            });
        }
    }

    void processChecked(ArrayList response) {
        try {
            if (response.size() == 0)
                return;

            getparentstosendnotification_Response r = (getparentstosendnotification_Response)response.get(0);

            for(Object o : r.data) {
                String parentId = (String)o;
                mParentsChecked.put(parentId, true);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    GetParentAnswer parentByPos(int position) {
        String parentId = (String)mParentsIds.get(position);
        return  mParents.get(parentId);
    }

    private class ParentsAdapter extends BaseAdapter {

        private LayoutInflater inflater;

        public ParentsAdapter() {
            this.inflater = LayoutInflater.from(PickDropNotificationsActivity.this);
        }

        @Override
        public int getCount() {
            return mParentsIds.size();
        }

        @Override
        public Object getItem(int position) {
            return  parentByPos(position);
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
            PickDropNotificationsActivity.ParentsAdapter.ViewHolder vholder;
            if (view == null) {
                view = inflater.inflate(R.layout.my_list_item, null);
                vholder = new PickDropNotificationsActivity.ParentsAdapter.ViewHolder();
                vholder.cb = (CheckBox) view.findViewById(R.id.checkBox1);
                vholder.tv = (TextView) view.findViewById(R.id.list_content1);
                view.setTag(vholder);
            }
            else {
                vholder = (PickDropNotificationsActivity.ParentsAdapter.ViewHolder) view.getTag();
            }

            GetParentAnswer current = parentByPos(position);

            vholder.cb.setVisibility(View.VISIBLE);

            vholder.tv.setText(current.getName());
            vholder.cb.setTag(Integer.valueOf(position));

            Boolean isChecked = mParentsChecked.get(current.parent_id);
            vholder.cb.setChecked(isChecked);

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

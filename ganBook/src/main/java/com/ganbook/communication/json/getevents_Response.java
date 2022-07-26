package com.ganbook.communication.json;

import com.ganbook.app.MyApp;
import com.project.ganim.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Noa on 29/11/2015.
 */
public class getevents_Response extends BaseResponse {

    public String event_date;
    public Event[] events;

    public class Event
    {
        public String event_id;
        public String kid_pic;
        public String class_id;
        public String all_day;
        public String day_off;
        public String event_title;
        public String event_type;
        public String all_kids;
        public String event_start_date;
        public String event_end_date;
        public String active;
        public String event_comments;
        public Kid[] kids;

        public String [] getEventKidsNames()
        {
            String[] names = null;
            if("1".equals(all_kids))
            {
                names = new String[1];
                names[0] = MyApp.context.getResources().getString(R.string.all_kids);
            }
            else {
                names = new String[kids.length];
                int ind = 0;
                for (Kid kid : kids) {
                    names[ind] = kid.kid_name;
                    ind++;
                }
            }
            return names;
        }

        public String[] getEventKidsIds()
        {
            String[] ids = null;
            if(kids != null) {
                ids = new String[kids.length];
                int ind = 0;
                for (Kid kid : kids) {
                    ids[ind] = kid.kid_id;
                    ind++;
                }
            }

            return ids;
        }


        public void setKidNames(HashMap<String,String> kids_names)
        {
            kids = new Kid[kids_names.keySet().size()];
            int ind = 0;
            for (String id : kids_names.keySet()) {
                kids[ind] = new Kid();
                kids[ind].kid_id = id;
                kids[ind].kid_name = kids_names.get(id);

                ind++;
            }
        }
    }

    public class Kid
    {
        public String kid_id;
        public String kid_name;
        public String kid_pic;
    }



}

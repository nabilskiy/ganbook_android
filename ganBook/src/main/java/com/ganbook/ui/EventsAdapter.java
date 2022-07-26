package com.ganbook.ui;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ganbook.communication.json.getevents_Response;
import com.ganbook.communication.json.transmitter.JsonTransmitter;
import com.ganbook.models.EventModel;
import com.ganbook.models.answers.EventsAnswer;
import com.ganbook.universalloader.UILManager;
import com.ganbook.utils.DateFormatter;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.project.ganim.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.TreeMap;

/**
 * Created by Noa on 29/11/2015.
 */
public class EventsAdapter extends ArrayAdapter<EventsAdapter.Item> {

    private static final String TAG = EventsAdapter.class.getName();
    private final Context context;
    private LayoutInflater mInflater;
//    private static MainActivity activity;
    private static boolean see_more_created = false;
    private static DisplayImageOptions defaultOptions = UILManager.createDefaultDisplayOpyions(R.drawable.birthday);


    public static final String BD = "1";
    public static final String SHABAT = "2";
    public static final String TRIP = "3";
    public static final String HOLIDAY = "4";
    public static final String OTHER = "5";
    public static final String BD_EVENT = "6";
    private ArrayList<Item> events;

    public EventsAdapter(Context context) {
        super(context, 0);

        mInflater = LayoutInflater.from(context);
        this.context = context;
    }

    @Override
    public int getViewTypeCount() {
        return RowType.values().length;

    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).getViewType();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getItem(position).getView(mInflater, convertView, parent);
    }

    public ArrayList<Item> getEvents() {
        return events;
    }

    public Item generateItem(EventModel event) {
        return null;
    }

    public void removeItem(String eventId) {

        Log.d(TAG, "removeItem: deleting event id = " + eventId);
        Log.d(TAG, "removeItem: events size = " + events.size());

        for (int i = 0; i < events.size(); i++) {

            int type = events.get(i).getViewType();

            if(type == EventsAdapter.RowType.EVENT_ITEM.ordinal()) {

                EventsAdapter.EventItem item = (EventsAdapter.EventItem)events.get(i);

                Log.d(TAG, "looking item = " + item);

                if (item.event_id.equals(eventId))
                    events.remove(item);
            }
        }

        Log.d(TAG, "removeItem: events size = " + events.size());

        notifyDataSetChanged();
    }

    public interface Item
    {
        int getViewType();
        View getView(LayoutInflater inflater, View convertView, ViewGroup parent);
    }

    public enum RowType
    {
        EVENT_ITEM, HEADER_ITEM, BD_EVENT_ITEM
    }

    public ArrayList<Item> mapToArrayList(TreeMap<String, ArrayList<getevents_Response.Event>> map) {
        ArrayList<Item> events = new ArrayList<Item>();

        if(map == null || map.isEmpty())
        {
            return events;
        }


        for (String date : map.keySet())
        {
            Item headerItem = new Header(date);

            events.add(headerItem);

            ArrayList<getevents_Response.Event> eventsArr = map.get(date);
            for (getevents_Response.Event event : eventsArr)
            {
                if(event.event_type.equals(BD_EVENT))
                {
                    Locale locale = context.getResources().getConfiguration().locale;
                    String title = "";
                    String [] arr = event.event_title.split("&");

                    if(locale.equals(Locale.ENGLISH) || locale.equals(Locale.US) || locale.equals(Locale.UK))
                    {
                        title = "\u200e" + arr[0] + "'s " + arr[1] + arr[2] + " " + context.getResources().getString(R.string.birthday);
                    }
                    else
                    {
                        title = context.getResources().getString(R.string.birthday) + " " + arr[1] + " " + context.getResources().getString(R.string.to) + arr[0];
                    }
                    EventItemBD eventItemBd = new EventItemBD(title, event.kid_pic);
                    events.add(eventItemBd);
                }
                else
                {
                    String start_date = DateFormatter.formatStringDate(event.event_start_date,"yyyy-MM-dd", "MMMM dd");
                    String end_date = DateFormatter.formatStringDate(event.event_end_date,"yyyy-MM-dd", "MMMM dd");

                    EventItem eventItem = new EventItem(event.event_id,event.all_day,date,event.day_off,
                            event.getEventKidsNames(),event.event_comments,event.event_start_date.substring(11,16),
                            event.event_end_date.substring(11,16), event.event_title, event.event_type,
                            start_date, end_date, event.all_kids,
                            event.getEventKidsIds(),event.event_start_date,event.event_end_date);
                    events.add(eventItem);
                }
            }
        }


        return events;
    }

    public ArrayList<Item> generateItemsList(List<EventsAnswer> list) {

        events = new ArrayList<>();

        if(list == null || list.isEmpty())
            return events;


        for (EventsAnswer eventsAnswer : list) {

            Item headerItem = new Header(eventsAnswer.getEventDate());

            events.add(headerItem);

            for (EventModel eventModel : eventsAnswer.getEventModelList())
            {
                if(eventModel.getEvent_type().equals(BD_EVENT))
                {
                    Locale locale = context.getResources().getConfiguration().locale;
                    String title = "";
                    String [] arr = eventModel.getEvent_title().split("&");

                    if(locale.equals(Locale.ENGLISH) || locale.equals(Locale.US) || locale.equals(Locale.UK))
                    {
                        title = "\u200e" + arr[0] + "'s " + arr[1] + arr[2] + " " + context.getResources().getString(R.string.birthday);
                    }
                    else
                    {
                        title = context.getResources().getString(R.string.birthday) + " " + arr[1] + " " + context.getResources().getString(R.string.to) + arr[0];
                    }
                    EventItemBD eventItemBd = new EventItemBD(title, eventModel.getKid_pic());
                    events.add(eventItemBd);
                }
                else
                {
                    String start_date = DateFormatter.formatStringDate(eventModel.getEvent_start_date(),"yyyy-MM-dd", "MMMM dd");
                    String end_date = DateFormatter.formatStringDate(eventModel.getEvent_end_date(),"yyyy-MM-dd", "MMMM dd");

                    Log.d(TAG, "generateItemsList: event model = " + eventModel);
                    
                    EventItem eventItem = new EventItem(eventModel.event_id,eventModel.all_day,
                            eventsAnswer.getEventDate(),eventModel.day_off,
                            eventModel.getEventKidsNames(),eventModel.event_comments,
                            eventModel.event_start_date.substring(11,16),
                            eventModel.event_end_date.substring(11,16), eventModel.event_title,
                            eventModel.event_type, start_date, end_date,
                            eventModel.all_kids, eventModel.getEventKidsIds(),
                            eventModel.event_start_date, eventModel.event_end_date);
                    events.add(eventItem);
                }
            }
        }

        return events;
    }

    public static class Header implements Item
    {
        private String         event_date;

        public Header(String event_date) {
            this.event_date = event_date;
        }

        @Override
        public int getViewType() {
            return RowType.HEADER_ITEM.ordinal();
        }

        class ViewHolder {
            TextView event_date_tv;
        }

        @Override
        public View getView(LayoutInflater inflater, View convertView, ViewGroup parent)
        {
            final ViewHolder vholder;

            if (convertView == null) {
                convertView = (View) inflater.inflate(R.layout.event_header, parent, false);
                vholder = new ViewHolder();
                vholder.event_date_tv = (TextView) convertView.findViewById(R.id.event_date_tv);

                convertView.setTag(vholder);
            } else {
                vholder = (ViewHolder) convertView.getTag();;
            }

            vholder.event_date_tv.setText(DateFormatter.formatStringDate(event_date, "yyyy-MM-dd", "MMMM dd"));

            return convertView;
        }

    }

    public static class EventItemBD implements Item
    {
        private String         event_title;
        private String         event_pic;

        public EventItemBD(String event_title, String event_pic) {
            this.event_pic = event_pic;
            this.event_title = event_title;
        }

        @Override
        public int getViewType() {
            return RowType.BD_EVENT_ITEM.ordinal();
        }

        class ViewHolder {
            TextView event_title_tv;
            CircleImageView profile_image;
            ImageView event_iv;
        }

        @Override
        public View getView(LayoutInflater inflater, View convertView, ViewGroup parent)
        {
            final ViewHolder vholder;

            if (convertView == null) {
                convertView = (View) inflater.inflate(R.layout.event_birthday_cell, parent, false);
                vholder = new ViewHolder();
                vholder.event_title_tv = (TextView) convertView.findViewById(R.id.event_title_tv);
                vholder.profile_image = (CircleImageView) convertView.findViewById(R.id.profile_image);
                vholder.event_iv = (ImageView) convertView.findViewById(R.id.event_iv);

                convertView.setTag(vholder);
            } else {
                vholder = (ViewHolder) convertView.getTag();;
            }

            vholder.event_title_tv.setText(event_title);

            if(event_pic != null)
            {
                vholder.event_iv.setVisibility(View.GONE);
                vholder.profile_image.setVisibility(View.VISIBLE);
                getKidPicture(event_pic, vholder.profile_image);
            }
            else
            {
                vholder.event_iv.setVisibility(View.VISIBLE);
                vholder.profile_image.setVisibility(View.GONE);
            }

            return convertView;
        }

    }

    private static void getKidPicture(String kid_pic, CircleImageView imgView) {
        String url = kidPicToUrl(kid_pic);
        UILManager.imageLoader.displayImage(url, imgView, defaultOptions);
    }

    public static String kidPicToUrl(String kid_pic) {
        return JsonTransmitter.PICTURE_HOST + JsonTransmitter.USERS_HOST + kid_pic + ".png";
    }

    public class EventItem implements Item
    {
        public String         all_day;
        public String         event_id;
        public String         event_date;
        public String         start_hour;
        public String         end_hour;
        public String         event_title;
        public String         event_type;
        public String         dayOff;
        public String         start_date;
        public String         end_date;
        public String         event_comments;
        public String         event_end_date;
        public String         event_start_date;
        public String         all_kids;
        public String[]       kids;
        public String[]       kids_ids;

        public EventItem(String event_id, String all_day, String event_date, String dayOff, String[] kids, String event_comments, String start_hour, String end_hour, String event_title, String event_type, String start_date, String end_date, String all_kids, String[] kids_ids, String event_start_date, String event_end_date) {
            this.event_id = event_id;
            this.event_date = event_date;
            this.start_hour = start_hour;
            this.end_hour = end_hour;
            this.event_title = event_title;
            this.event_type = event_type;
            this.dayOff = dayOff;
            this.all_day = all_day;
            this.start_date = start_date;
            this.end_date = end_date;
            this.event_comments = event_comments;
            this.kids = kids;
            this.all_kids = all_kids;
            this.kids_ids = kids_ids;
            this.event_start_date = event_start_date;
            this.event_end_date = event_end_date;
        }

        @Override
        public int getViewType() {
            return RowType.EVENT_ITEM.ordinal();
        }

        @Override
        public String toString() {
            return "EventItem{" +
                    "all_day='" + all_day + '\'' +
                    ", event_id='" + event_id + '\'' +
                    ", event_date='" + event_date + '\'' +
                    ", start_hour='" + start_hour + '\'' +
                    ", end_hour='" + end_hour + '\'' +
                    ", event_title='" + event_title + '\'' +
                    ", event_type='" + event_type + '\'' +
                    ", dayOff='" + dayOff + '\'' +
                    ", start_date='" + start_date + '\'' +
                    ", end_date='" + end_date + '\'' +
                    ", event_comments='" + event_comments + '\'' +
                    ", event_end_date='" + event_end_date + '\'' +
                    ", event_start_date='" + event_start_date + '\'' +
                    ", all_kids='" + all_kids + '\'' +
                    ", kids=" + Arrays.toString(kids) +
                    ", kids_ids=" + Arrays.toString(kids_ids) +
                    '}';
        }

        class ViewHolder {
            TextView start_hour_tv,end_hour_tv,event_title_tv;
            ImageView event_iv;
        }

        @Override
        public View getView(LayoutInflater inflater, View convertView, ViewGroup parent) {
//            View view;
            final ViewHolder vholder;

            if (convertView == null) {
                convertView = (View) inflater.inflate(R.layout.event_item, parent, false);
                vholder = new ViewHolder();
                vholder.start_hour_tv = (TextView) convertView.findViewById(R.id.start_hour_tv);
                vholder.end_hour_tv = (TextView) convertView.findViewById(R.id.end_hour_tv);
                vholder.event_title_tv = (TextView) convertView.findViewById(R.id.event_title_tv);
                vholder.event_iv = (ImageView) convertView.findViewById(R.id.event_iv);

                convertView.setTag(vholder);
            } else {
                vholder = (ViewHolder) convertView.getTag();;
            }


            if("1".equals(all_day))
            {
                vholder.start_hour_tv.setText(context.getString(R.string.All));
                vholder.end_hour_tv.setText(context.getString(R.string.Day));
            }
            else {
                vholder.start_hour_tv.setText(start_hour);
                vholder.end_hour_tv.setText(end_hour);
            }

            vholder.event_title_tv.setText(event_title);

            vholder.event_iv.setBackgroundResource(getEventTypeDrawable(event_type));

            return convertView;
        }



    }

    public static int getEventTypeDrawable(String event_type)
    {
        if(event_type.equals(BD))
        {
            return R.drawable.birthday;
        }
        else if(event_type.equals(SHABAT))
        {
            return R.drawable.shabat;
        }
        else if(event_type.equals(HOLIDAY))
        {
            return R.drawable.holiday;
        }
        else if(event_type.equals(TRIP))
        {
            return R.drawable.trip;
        }
        else if(event_type.equals(OTHER))
        {
            return R.drawable.other;
        }
        else if(event_type.equals(BD_EVENT))
        {
            return R.drawable.present;
        }

        return 0;
    }
}

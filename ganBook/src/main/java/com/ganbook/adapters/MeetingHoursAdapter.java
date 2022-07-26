package com.ganbook.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ganbook.models.MeetingHour;
import com.ganbook.models.TimeSlotModel;
import com.project.ganim.R;

import java.text.SimpleDateFormat;
import java.util.List;

public class MeetingHoursAdapter extends ArrayAdapter<TimeSlotModel> {

    private List<TimeSlotModel> timeSlotModelList;
    private Context context;

    public MeetingHoursAdapter(@NonNull Context context, List<TimeSlotModel> timeSlotModelList) {
        super(context, R.layout.meeting_hour_item_inflater, timeSlotModelList);
        this.context = context;
        this.timeSlotModelList = timeSlotModelList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;
        TimeSlotModel timeSlotModel = getItem(position);
        assert timeSlotModel != null;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.meeting_hour_item_inflater, parent, false);
            viewHolder.dateTime = convertView.findViewById(R.id.meetingHourDateTimeText);
            viewHolder.available = convertView.findViewById(R.id.meetingHourAvailableText);
            viewHolder.meetingHourLayout = convertView.findViewById(R.id.meetingHourLayout);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (timeSlotModel.getActive().equals("1")) {
            viewHolder.meetingHourLayout.setClickable(false);
            viewHolder.meetingHourLayout.setEnabled(false);
            viewHolder.available.setText(R.string.meeting_unavailable_text);
            viewHolder.available.setTextColor(Color.parseColor("#FF0000"));
        } else {
            viewHolder.available.setText(R.string.meeting_available_text);
            viewHolder.available.setTextColor(Color.parseColor("#008000"));
        }
        viewHolder.dateTime.setText(timeSlotModel.getMeetingSlot());

        return convertView;
    }




    private class ViewHolder {
        RelativeLayout meetingHourLayout;
        TextView dateTime, available;
    }
}

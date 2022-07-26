package com.ganbook.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ganbook.activities.MeetingEventListActivity;
import com.ganbook.dialogs.Dialogs;
import com.ganbook.models.MeetingAttendeesModel;
import com.ganbook.models.MeetingEventModel;
import com.ganbook.user.User;
import com.project.ganim.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class MeetingsEventListAdapter extends ArrayAdapter<MeetingEventModel> {

    private List<MeetingEventModel> meetingEventModelList;
    private Context context;

    public MeetingsEventListAdapter(@NonNull Context context, List<MeetingEventModel> meetingEventModelList) {
        super(context, R.layout.meeting_event_list, meetingEventModelList);
        this.context = context;
        this.meetingEventModelList = meetingEventModelList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;
        MeetingEventModel meetingEventModel = getItem(position);

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.meeting_event_list, parent, false);
            viewHolder.meetingEventDate = convertView.findViewById(R.id.meetingEventDate);
            viewHolder.meetingDescription = convertView.findViewById(R.id.meetingDescription);
            viewHolder.deleteMeetingIcon = convertView.findViewById(R.id.deleteMeetingIcon);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Date date = null;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).parse(meetingEventModel.getMeetingStartDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String formattedDate = new SimpleDateFormat("EEEE, MMM d", Locale.getDefault()).format(date);

        viewHolder.meetingEventDate.setText(formattedDate);
        viewHolder.meetingDescription.setText(meetingEventModel.getMeetingComment());

        if (User.isTeacher()) {
            viewHolder.deleteMeetingIcon.setVisibility(View.VISIBLE);
            viewHolder.deleteMeetingIcon.setOnClickListener(v -> {
                if(context instanceof MeetingEventListActivity) {
                    Dialogs.successDialogWithButton(context, context.getString(R.string.delete_meeting_text), context.getString(R.string.delete_meeting_message_text), context.getString(R.string.yes), context.getString(R.string.cancel), new Dialogs.ButtonDialogInterface() {
                        @Override
                        public void onButtonOkClicked() {
                            ((MeetingEventListActivity)context).deleteMeeting(meetingEventModel.getMeetingId());
                            notifyDataSetChanged();
                        }
                    });
                }
            });
        } else {
            viewHolder.deleteMeetingIcon.setVisibility(View.GONE);

        }

        return convertView;
    }

    private class ViewHolder {
        TextView meetingEventDate, meetingDescription;
        ImageView deleteMeetingIcon;

    }


}

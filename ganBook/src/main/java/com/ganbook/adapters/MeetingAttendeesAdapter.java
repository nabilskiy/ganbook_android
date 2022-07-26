package com.ganbook.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.ganbook.models.MeetingAttendeesModel;
import com.project.ganim.R;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class MeetingAttendeesAdapter extends ArrayAdapter<MeetingAttendeesModel> {

    private List<MeetingAttendeesModel> meetingAttendeesModelList;
    private Context context;

    public MeetingAttendeesAdapter(@NonNull Context context, List<MeetingAttendeesModel> meetingAttendeesModelList) {
        super(context, R.layout.meeting_attendees_list_inflater, meetingAttendeesModelList);
        this.context = context;
        this.meetingAttendeesModelList = meetingAttendeesModelList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;
        MeetingAttendeesModel meetingAttendeesModel = getItem(position);

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.meeting_attendees_list_inflater, parent, false);
            viewHolder.parentName = convertView.findViewById(R.id.meetingParentName);
            viewHolder.parentImage = convertView.findViewById(R.id.meetingParentImage);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.parentName.setText(meetingAttendeesModel.getParentName());
        return convertView;
    }

    private class ViewHolder {
        TextView parentName;
        ImageView parentImage;

    }
}

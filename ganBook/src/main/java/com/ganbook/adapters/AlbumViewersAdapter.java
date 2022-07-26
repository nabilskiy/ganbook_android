package com.ganbook.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ganbook.models.AlbumViewerModel;
import com.ganbook.ui.CircleImageView;
import com.project.ganim.R;

import java.util.List;

public class AlbumViewersAdapter extends ArrayAdapter<AlbumViewerModel> {
    private Context context;
    private List<AlbumViewerModel> albumViewerModels;

    public AlbumViewersAdapter(List<AlbumViewerModel> data, Context context) {
        super(context,  R.layout.album_viewers_inflater, data);
        this.context = context;
        this.albumViewerModels = data;
    }


    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;
        AlbumViewerModel albumViewerModel = getItem(position);
        if(convertView == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.album_viewers_inflater, parent, false);
            holder.parentName = convertView.findViewById(R.id.albumViewerName);
            holder.img = convertView.findViewById(R.id.albumViewerImage);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        assert albumViewerModel != null;
        holder.parentName.setText(albumViewerModel.getParentFirstName() + " " + albumViewerModel.getParentLastName());
        return convertView;
    }

    public class ViewHolder {
        TextView parentName;
        CircleImageView img;
    }

}



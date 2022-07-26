package com.ganbook.ui;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.project.ganim.R;
import com.ganbook.datamodel.AlbumObject;

public class AlbumAdapter extends BaseAdapter {
	
	private Context context;
	private List<AlbumObject> albumList = null;
	
	public AlbumAdapter(Context ctx, List<AlbumObject> list){
		this.context = ctx;
		this.albumList = list;
	}
	@Override
	public int getCount() {
		return albumList.size();
	}

	@Override
	public Object getItem(int position) {
		return albumList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	class ViewHolder {
		TextView name;
	};

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		ViewHolder vholder;
		if (row == null) {
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			row = inflater.inflate(R.layout.feeds_list_inflator, parent, false);
			vholder = new ViewHolder();
			vholder.name = (TextView) row.findViewById(R.id.name);
			row.setTag(vholder);
		} 
		else {
			vholder = (ViewHolder) row.getTag();
		}
		
		vholder.name.setText(albumList.get(position).getName());
		return row;
	}

}

package com.ganbook.ui;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.ganbook.datamodel.StringObject;
import com.project.ganim.R;

@SuppressLint("ViewHolder")
public class ViewedAdapter extends BaseAdapter {
	
	private Context context;
	private LayoutInflater inflater;
	private ArrayList<StringObject> data;

	public ViewedAdapter(Context c, ArrayList<StringObject> _arraylist) {
		this.context = c;
		this.data = _arraylist;
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		inflater = (LayoutInflater) context.getApplicationContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View itemView = inflater.inflate(R.layout.viewed_inflator, parent,
				false);
		final CheckBox checks = (CheckBox) itemView.findViewById(R.id.b);
		final TextView _setappname = (TextView) itemView.findViewById(R.id.a);
		checks.setChecked(data.get(position).isSelected());

		StringObject obj = data.get(position);
		_setappname.setText(obj.getAppname());
		checks.setTag(position);

		checks.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				int getPosition = (Integer) buttonView.getTag();
				StringObject _obj = data.get(getPosition);
				data.get(position).setSelected(isChecked);
				String ss = _obj.getAppname();
				System.out.println("pos is" + getPosition);

			}
		});

		return itemView;
	}

}

package com.ganbook.ui;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.ganbook.app.MyApp;
import com.ganbook.communication.datamodel.MessageDetails;
import com.ganbook.fragments.MessageListFragment;
import com.ganbook.utils.DateFormatter;
import com.project.ganim.R;

import java.util.ArrayList;

import static android.content.Context.CLIPBOARD_SERVICE;

public class MessageAdapter extends BaseAdapter {

	// messages_row_item
	
	private ArrayList<MessageDetails> messageList;
	private LayoutInflater inflater;
	private Context context;

	public MessageAdapter(Context cnt, ArrayList<MessageDetails> list) {
		this.messageList = list;
		this.context = cnt;
		this.inflater = LayoutInflater.from(cnt);
	} 

	@Override
	public int getCount() {
		return messageList.size();
	}

	@Override
	public Object getItem(int position) {
		return messageList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	class ViewHolder {
		TextView tv_message;
		TextView tv_day_time;
		TextView tv_day_views;
		
	};

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View view, ViewGroup parent) {
		final ViewHolder vholder;
		if (view == null) {
			view = inflater.inflate(R.layout.messages_row_item, null);
			vholder = new ViewHolder();
			vholder.tv_message = (TextView) view.findViewById(R.id.tv_message);
			vholder.tv_day_time = (TextView) view.findViewById(R.id.tv_day_time);
			vholder.tv_day_views = (TextView) view.findViewById(R.id.tv_day_views);
			view.setTag(vholder);
		}
		else {
			vholder = (ViewHolder) view.getTag();
		}
		MessageDetails current = messageList.get(position);
		vholder.tv_message.setText(Html.fromHtml(current.message_text));
		vholder.tv_message.setMovementMethod(LinkMovementMethod.getInstance());

		vholder.tv_day_time.setText(DateFormatter.formatStringDate(current.message_date,"yyyy-MM-dd hh:mm","yyyy-MM-dd, HH:mm"));
		
		int num_viewed = current.numViewed();
		String total_number = MessageListFragment.total_active_parents;
		String ViewedStr = MyApp.context.getResources().getString(
				R.string.viewed_str, num_viewed, total_number); 		
		vholder.tv_day_views.setText(ViewedStr);
		return view;
	}

}
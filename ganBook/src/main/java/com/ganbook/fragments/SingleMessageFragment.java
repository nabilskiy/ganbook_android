package com.ganbook.fragments;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.ganbook.amazinglist.special.CustomToast;
import com.ganbook.app.MyApp;
import com.ganbook.communication.ICompletionHandler;
import com.ganbook.communication.datamodel.MessageDetails;
import com.ganbook.communication.datamodel.ResultObj;
import com.ganbook.communication.json.transmitter.JsonTransmitter;
import com.ganbook.interfaces.GanbookApiInterface;
import com.ganbook.models.MessageModel;
import com.ganbook.models.answers.ReadMessageAnswer;
import com.ganbook.user.User;
import com.ganbook.utils.AlertUtils;
import com.ganbook.utils.Const;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;
import com.project.ganim.R;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//import com.ganbook.fragments.SingleMessageScreenFragment.TwoTextArrayAdapter.RowType;

public class SingleMessageFragment extends Fragment {

	public static final String TAG = SingleMessageFragment.class.getName();
//	private Context context;
	private ListView viewded_list;
	static int pos;

	private static final int HEADER = 0;
	private static final int ITEM = 1;
	SweetAlertDialog progress;
	

	private ArrayList<ReadMessageAnswer> viewerArr;
	private ArrayList<ReadMessageAnswer> didntViewArr;

	MessageModel messageModel;

	public static MessageDetails selMessage;

	@Inject
	@Named("GET")
	GanbookApiInterface ganbookApiInterfaceGET;

	public static SingleMessageFragment newInstance(MessageModel messageModel) {

		Bundle args = new Bundle();

		SingleMessageFragment fragment = new SingleMessageFragment();

		args.putParcelable(Const.ARG_MESSAGE, messageModel);

		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getArguments() != null) {

			this.messageModel = getArguments().getParcelable(Const.ARG_MESSAGE);

			Log.d(TAG, "onCreate: received message = " + messageModel);
		}

		((MyApp) getActivity().getApplication()).getGanbookApiComponent().inject(this);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {		
		View _view = inflater.inflate(R.layout.single_message, container, false);

		viewded_list = (ListView) _view.findViewById(R.id.viewded_list);

		progress = AlertUtils.createProgressDialog(getContext(), getString(R.string.processing_wait));

		return _view;
	}

	@Override
	public void onResume() {
		super.onResume();
		populateMessageViewerList();
	}


	private void populateMessageViewerList() {

		String message_id = messageModel.getMessageId();
		String class_id = User.current.getCurrentClassId();

		progress.show();

		Call<List<ReadMessageAnswer>> call = ganbookApiInterfaceGET.getReadMessage(message_id, class_id);

		call.enqueue(new Callback<List<ReadMessageAnswer>>() {
			@Override
			public void onResponse(Call<List<ReadMessageAnswer>> call, Response<List<ReadMessageAnswer>> response) {

				progress.hide();

				List<ReadMessageAnswer> readMessageAnswers = response.body();

				Log.d(TAG, "onResponse: successfull load " + readMessageAnswers);

				if (readMessageAnswers != null) {

					viewerArr = new ArrayList<>();
					didntViewArr = new ArrayList<>();

					for (ReadMessageAnswer br : readMessageAnswers) {

						if (br.read) {
							viewerArr.add(br);
						} else {
							didntViewArr.add(br);
						}
					}

					ArrayList<ReadMessageAnswer> mergedArr = new ArrayList<>();
					mergedArr.add(ReadMessageAnswer.createHeaderItem(R.string.viewed_by, true));
					mergedArr.addAll(viewerArr);
					mergedArr.add(ReadMessageAnswer.createHeaderItem(R.string.didnt_view, false));
					mergedArr.addAll(didntViewArr);
					ListWithCaptionAdapter adapter = new ListWithCaptionAdapter(getContext(), mergedArr);
					viewded_list.setAdapter(adapter);
				}
			}

			@Override
			public void onFailure(Call<List<ReadMessageAnswer>> call, Throwable t) {
				progress.hide();
				CustomToast.show(getActivity(), R.string.error_read_message);

				Log.e(TAG, "onFailure: errro while load read message = " + Log.getStackTraceString(t));
			}
		});
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	
	class ListWithCaptionAdapter extends ArrayAdapter<ReadMessageAnswer> {
		private LayoutInflater mInflater;

		public ListWithCaptionAdapter(Context context, ArrayList<ReadMessageAnswer> mergedArr) {
			super(context, 0, mergedArr);
			mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getViewTypeCount() {
			return 2; // list + header items
		}

		@Override
		public int getItemViewType(int position) {
			ReadMessageAnswer cur = getItem(position);
			if (cur.isHeader) {
				return HEADER;
			} else {
				return ITEM;
			}
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			pos = position;
			ReadMessageAnswer cur = getItem(position);
			if (cur.isHeader) {
				return getView_header(position, convertView, parent);
			} else {
				return getView_parent(position, convertView, parent);
			}
		}


		private View getView_parent(int position, View convertView, ViewGroup parent) {
			View view = convertView;
			if (view == null) {
				view = (View) mInflater.inflate(R.layout.my_list_item, null);
			}
			TextView parentTxt = (TextView) view.findViewById(R.id.list_content1);
			CheckBox checkBox1 = (CheckBox) view.findViewById(R.id.checkBox1);
			ReadMessageAnswer cur = getItem(position);
			parentTxt.setText(cur.getName());

			return view;
		}


		private View getView_header(int position, View convertView, ViewGroup parent) {
			View view = convertView;
			if (view == null) {
				view = (View) mInflater.inflate(R.layout.viewed_by_header, null);
			} 
			ReadMessageAnswer cur = getItem(position);
			TextView text = (TextView) view.findViewById(R.id.separator);
			Button send_reminder = (Button) view.findViewById(R.id.send_reminder);
			send_reminder.setOnClickListener(new View.OnClickListener() {				
				@Override
				public void onClick(View v) {
					sendReminderToAll();
				}
			});
			String caption = cur.first_name;
			text.setText(caption);
//			send_reminder.setVisibility(cur.isViewedheader ? View.GONE : View.VISIBLE);
			return view;
		}

	};

	private void sendReminderToAll() {
		String message_id = messageModel.getMessageId();
		String class_id = User.current.getCurrentClassId();
		
		ArrayList<String> parentIds = new ArrayList<String>();
		for (ReadMessageAnswer parent: didntViewArr) { // only those that r yet to read the msg
			parentIds.add(parent.user_id);
		}

		progress.show();

		JsonTransmitter.send_senduserspush(parentIds, message_id, class_id, new ICompletionHandler() {			
			@Override
			public void onComplete(ResultObj result) {
		progress.hide();
				if(JsonTransmitter.NO_NETWORK_MODE.equals(result.result))
				{

					return;
				}
				String msg;
				if (!result.succeeded) {
					msg = result.result;
				} else {
					msg = getResources().getString(R.string.operation_succeeded); 
				}

				CustomToast.show(getActivity(), msg);
			}
		});
	}
	
}

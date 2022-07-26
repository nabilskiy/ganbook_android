package com.ganbook.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.ganbook.activities.AddMessageActivity;
import com.ganbook.activities.MainActivity;
import com.ganbook.amazinglist.special.CustomToast;
import com.ganbook.app.MyApp;
import com.ganbook.communication.ICompletionHandler;
import com.ganbook.communication.datamodel.MessageDetails;
import com.ganbook.communication.datamodel.ResultObj;
import com.ganbook.communication.json.getmessage_Response;
import com.ganbook.communication.json.transmitter.JsonTransmitter;
import com.ganbook.ui.MessageAdapter;
import com.ganbook.user.User;
import com.ganbook.utils.CurrentYear;
import com.ganbook.utils.StrUtils;

import com.project.ganim.R;

import java.util.ArrayList;
import java.util.Collections;

//import com.rockerhieu.emojicon.EmojiconEditText;
//import com.rockerhieu.emojicon.EmojiconGridFragment;
//import com.rockerhieu.emojicon.EmojiconTextView;
//import com.rockerhieu.emojicon.EmojiconsFragment;
//import com.rockerhieu.emojicon.emoji.Emojicon;
//import com.tonicartos.superslim.LinearSLM;

@SuppressLint("InflateParams")
public class MessageListFragment extends BaseFragment
	implements OnRefreshListener, OnItemClickListener{//, EmojiconGridFragment.OnEmojiconClickedListener, EmojiconsFragment.OnEmojiconBackspaceClickedListener {
	
	// message_fragment 

	private static ArrayList<MessageDetails> messageArr;
	
	private static MessageListFragment inst;

	public static String total_active_parents = "";
	

	private Context context;
    private LinearLayout send_msg_panel;
	private ListView message_list;
	private Button send_btn;
	private SwipeRefreshLayout mSwipeRefreshLayout;
	private MessageAdapter _adapter;
	View noMessagesView;

	LinearLayout footer_for_emoticons;
	ViewSwitcher switcher;
	ProgressBar pbHeaderProgress;
	private boolean justCreated = false;

	private final Initializer initializer = new Initializer();
	
	class Initializer {

		public void onAttach(Activity activity) {
			context = activity;
		}

		public void onCreate() {
			inst = MessageListFragment.this;
			justCreated = true;
		}

		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

			MyApp.sendAnalytics("messages-gan-ui", "messages-gan"+User.current.getCurrentGanId()+"-class"+User.current.getCurrentClassId()+"-ui"+ User.getId(), "messages-gan-ui", "MessagesGan");

			setActionBarValues();
			
			View _view = inflater.inflate(R.layout.message_fragment, container, false);
			
			base_onCreateView();

			pbHeaderProgress = (ProgressBar)_view.findViewById(R.id.pbHeaderProgress);
			mSwipeRefreshLayout = (SwipeRefreshLayout) _view
					.findViewById(R.id.refresh_layout);
			mSwipeRefreshLayout.setOnRefreshListener(MessageListFragment.this);
			mSwipeRefreshLayout.setColorScheme(android.R.color.holo_blue_bright,
					android.R.color.holo_green_light,
					android.R.color.holo_orange_light,
					android.R.color.holo_red_light);
			
			final boolean _parent = User.isParent();

//			mEditEmojicon = (EmojiconEditText) _view.findViewById(R.id.editEmojicon);
			send_msg_panel = (LinearLayout) _view.findViewById(R.id.send_msg_panel);

			send_msg_panel.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					Intent i = new Intent(MyApp.context, AddMessageActivity.class);

//					i.putExtra("comment_id",commentsArr.get(position).comment_id);

					startActivity(i);
				}
			});

//			footer_for_emoticons = (LinearLayout) _view.findViewById(R.id.footer_for_emoticons);
//			switcher = (ViewSwitcher) _view.findViewById(R.id.switcher);
//
//			switcher.setOnClickListener(new OnClickListener() {
//				@Override
//				public void onClick(View v) {
//
//					ViewSwitcher switcher = (ViewSwitcher) v;
//
//					if(switcher.getDisplayedChild() == 0)
//					{
//						switcher.showNext();
//						KeyboardUtils.close(activity(),mEditEmojicon);
//						footer_for_emoticons.setVisibility(View.VISIBLE);
//					}
//					else
//					{
//						switcher.showPrevious();
//						footer_for_emoticons.setVisibility(View.GONE);
//						InputMethodManager inputMethodManager=(InputMethodManager)activity().getSystemService(Context.INPUT_METHOD_SERVICE);
//						inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
//					}
//				}
//			});
			
//			if (User.isTeacher() || User.current.getCurrentKidPTA()) {
//				send_msg_panel.setVisibility(View.VISIBLE);
//			}
						
//			new_msg_text.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE );
			message_list = (ListView) _view.findViewById(R.id.message_list);
			message_list.setOnItemClickListener(MessageListFragment.this);

//			mEditEmojicon.addTextChangedListener(new TextWatcher() {
//				public void afterTextChanged(Editable s) {
//					int len = mEditEmojicon.getText().toString().length();
//					if (len == 0) {
//						send_btn.setTextColor(Color.GRAY);
////			        	send_btn.setEnabled(false);
//					} else {
//						send_btn.setTextColor(Color.BLACK);
////			        	send_btn.setEnabled(true);
//					}
//				}
//
//				public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//				}
//
//				public void onTextChanged(CharSequence s, int start, int before, int count){}
//			});
			
//			send_btn = (Button) _view.findViewById(R.id.send);
					
//			send_btn.setOnClickListener(new OnClickListener() {
//				@Override
//				public void onClick(View arg0) {
//					String newMsgText = mEditEmojicon.getText().toString();
//					if (newMsgText.length() == 0) {
//						Toast.makeText(activity(), "Please enter some value",
//								Toast.LENGTH_SHORT).show();
//					}
//					else {
//
//						if(User.current.getCurrentKidPTA())
//						{
//							newMsgText = getString(R.string.message_from_pta) + "\n" + newMsgText;
//						}
////						if (newMsgText.length() > 200) {
////							Toast.makeText(activity(),
////									"Message can't be longer than 200 alphabets",
////									Toast.LENGTH_SHORT).show();
////						}
////						else {
//							addNewMessage(newMsgText);
////						}
//
//					}
//				}
//			});
			
//			handleFragmentVisible();
			noMessagesView = inflater.inflate(R.layout.item_composer_text, null);

			((TextView)noMessagesView.findViewById(R.id.tv)).setText(activity().getString(R.string.empty_messages_list));

//			setEmojiconFragment(false);

			return _view;
		}

		public void onStart() {
			Log.i("noanoa","onStart");
			boolean _justCreated = justCreated;
			justCreated = false; //!
			populateMessageList(_justCreated);

		}


		public void onBecomingVisible() {
			handleFragmentVisible();
			populateMessageList(false);
		}
		
	};

	public static void addMessageToList(String newMsgText)
	{
		if (inst == null)
		{
			return;
		}
		messageArr.add(messageArr.size(), MessageDetails.createNewMessage(newMsgText)); //add as first in list
		inst._adapter = new MessageAdapter(inst.context, messageArr);
		inst.message_list.setAdapter(inst._adapter);
	}

	public static boolean closeFooterEmoticonsVisible()
	{
		if(inst == null)
		{
			return false;
		}

		if(inst.footer_for_emoticons.getVisibility() == View.VISIBLE)
		{
			inst.footer_for_emoticons.setVisibility(View.GONE);
			return true;
		}
		else
		{
			return false;
		}
	}

//	private void setEmojiconFragment(boolean useSystemDefault) {
//		activity().getSupportFragmentManager()
//				.beginTransaction()
//				.replace(R.id.emojicons, EmojiconsFragment.newInstance(useSystemDefault))
//				.commit();
//	}
	
	
	public static void clearMessages() {
		messageArr = null; // force refresh
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		initializer.onAttach(activity);
	}

	@SuppressWarnings("deprecation")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		return initializer.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onStart() {
		super.onStart();
		initializer.onStart();
	}

	
	private void setActionBarValues() {
		titleTextId = R.string.messages;
		saveBtnVisibility = View.GONE;
		drawerMenuVisibility = View.VISIBLE; 
	}
	

	@Override
	public void onResume() {

		super.onResume();

		int jj=234;
		jj++;
	}

	



	@Override
	public void onRefresh() {
		mSwipeRefreshLayout.setRefreshing(false); //!
		populateMessageList(true);
	}

				
	@Override
	public void onBecomingVisible() {
		super.onBecomingVisible();
		initializer.onBecomingVisible();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		initializer.onCreate();
	}
	
	@Override
	public void onDestroy() {

		super.onDestroy();
		if (this == inst) {
			inst = null;
		}
	}
	
	public static void forceContentRefresh() {
		if (inst != null) {
			inst.base_forceContentRefresh();
			inst.populateMessageList(true);
		}
	}
	
	public static void setFragmentVisible() {
		if (inst != null) {
			inst.handleFragmentVisible();
		}
	}

		
	private void populateMessageList(boolean _forceRefresh) {

		if(send_msg_panel != null) {
			if (User.isTeacher() || User.current.getCurrentKidPTA()) {
				send_msg_panel.setVisibility(View.VISIBLE);
//				ViewGroup.LayoutParams params = send_msg_panel.getLayoutParams();
//				params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
//				send_msg_panel.setLayoutParams(params);
				//			send_msg_panel.requestLayout();
			} else {
				send_msg_panel.setVisibility(View.GONE);
//				ViewGroup.LayoutParams params = send_msg_panel.getLayoutParams();
//				params.height = 0;
//				send_msg_panel.setLayoutParams(params);
			}
		}

		if(mSwipeRefreshLayout != null) {
			mSwipeRefreshLayout.setRefreshing(false);
		}
		if (!_forceRefresh && (messageArr != null && messageArr.size() > 0)) {
			__setDynamicValues();
			
			if(User.isParent())
			{
				MainScreenFragment.updateMessageTabBadge();
				updateUnreadMessage();
			}
			
			return;
		} 
		
//		startProgress(R.string.operation_proceeding);

		if(pbHeaderProgress != null) {
			pbHeaderProgress.setVisibility(View.VISIBLE);
		}

		if(message_list != null) {
			message_list.setVisibility(View.GONE);
		}

		String class_id = User.current.getCurrentClassId(); 
		String year = CurrentYear.get();		
		
		JsonTransmitter.send_getmessage(class_id, year, new ICompletionHandler() {
			@Override 
			public void onComplete(ResultObj result) {
//				stopProgress();

				if(pbHeaderProgress != null) {
					pbHeaderProgress.setVisibility(View.GONE);
				}

				message_list.setVisibility(View.VISIBLE);

				if(mSwipeRefreshLayout != null) {
					mSwipeRefreshLayout.setRefreshing(false);
				}
				if (!result.succeeded) {
					
					messageArr = new ArrayList<MessageDetails>();
					if(JsonTransmitter.NO_NETWORK_MODE.equals(result.result))
					{
						showNotInternetAlert();

						message_list.removeHeaderView(tryAgainView);
						message_list.removeHeaderView(noMessagesView);

						message_list.addHeaderView(tryAgainView);
						_adapter = new MessageAdapter(context, messageArr);
						message_list.setAdapter(_adapter);
						return;
					}
					String errmsg = result.result;
					CustomToast.show(activity(), errmsg);
				}
				else {
					int numResponses = result.getNumResponses();
					if(numResponses > 0)
					{
						getmessage_Response res = (getmessage_Response) result.getResponse(0);
						total_active_parents = StrUtils.emptyIfNull(res.total_active_parents);
						messageArr = toArrayList(res.messages);
						if (messageArr==null || messageArr.isEmpty()) {
	//						CustomToast.show(activity(), R.string.empty_messages_list);
							message_list.removeHeaderView(tryAgainView);
							message_list.removeHeaderView(noMessagesView);

							message_list.addHeaderView(noMessagesView);
							_adapter = new MessageAdapter(context, messageArr);
							message_list.setAdapter(_adapter);
							return;
						}

						else
						{
							if (User.isParent()) {  // only!
								JsonTransmitter.send_updatereadmessage(User.current.id, User.current.getCurrentKid().class_id, messageArr.get(0).message_id);
								updateUnreadMessage();
								MainScreenFragment.updateMessageTabBadge();
								MainActivity.updateDrawerContent();
							}
						}
					}

				}				
				__setDynamicValues();
			}
			
		});
	}
		
	private void updateUnreadMessage()
	{
		User.current.updateUnreadMessage();
	}

	private static ArrayList<MessageDetails> toArrayList(MessageDetails[] messages) {
		if (messages==null) {
			return new ArrayList<MessageDetails>();
		}
		ArrayList<MessageDetails> m_list = new ArrayList<MessageDetails>();
		for (MessageDetails m: messages) {
			m_list.add(m);
		}

		Collections.reverse(m_list);

		return m_list;
	}
	

	protected void __setDynamicValues() {
		message_list.removeHeaderView(tryAgainView);
		_adapter = new MessageAdapter(context, messageArr);
		message_list.setAdapter(_adapter);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// setOnClick -- viewed single message
		if (messageArr==null) {
			populateMessageList(true);
			return;
		}
 		
		if (User.isTeacher()) {
			SingleMessageFragment.selMessage = messageArr.get(position);
			activity().moveToTab(FragmentType.Single_Message);
		}
	}

	public static void setTitleVisibility(int visible) {
		if (inst != null) {
			if(visible == View.VISIBLE)
			{
				inst.handleFragmentVisible();
			}
			inst.doSetTitleVisibility(visible); 
		}
	}



//	@Override
//	public void onEmojiconClicked(Emojicon emojicon) {
//		EmojiconsFragment.input(mEditEmojicon, emojicon);
//	}
//
//	@Override
//	public void onEmojiconBackspaceClicked(View v) {
//		EmojiconsFragment.backspace(mEditEmojicon);
//	}
}

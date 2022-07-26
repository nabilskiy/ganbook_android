package com.ganbook.fragments.tabs;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ganbook.activities.AddMessageActivity;
import com.ganbook.activities.MainActivity;
import com.ganbook.adapters.MessagesAdapter;
import com.ganbook.amazinglist.special.CustomToast;
import com.ganbook.app.MyApp;
import com.ganbook.dialogs.Dialogs;
import com.ganbook.dividers.SpaceItemDecoration;
import com.ganbook.fragments.MainScreenFragment;
import com.ganbook.interfaces.GanbookApiInterface;
import com.ganbook.interfaces.MessagesFragmentInterface;
import com.ganbook.models.MessageAnswer;
import com.ganbook.models.MessageModel;
import com.ganbook.models.MessageStatus;
import com.ganbook.models.SuccessAnswer;
import com.ganbook.models.events.AttachmentFileEvent;
import com.ganbook.models.events.SelectDrawerItemEvent;
import com.ganbook.user.User;
import com.ganbook.utils.AlertUtils;
import com.ganbook.utils.Const;
import com.ganbook.utils.CurrentYear;
import com.ganbook.utils.NetworkUtils;
import com.ganbook.utils.UrlUtils;

import com.project.ganim.R;
import com.project.ganim.databinding.FragmentMessagesBinding;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;
import java.util.Objects;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessagesFragment extends Fragment implements MessagesFragmentInterface {

    private static final String TAG = MessagesFragment.class.getName();
    private AlertDialog dialog;
    MessagesAdapter messagesAdapter;

    @BindView(R.id.messages_recycler)
    RecyclerView messagesRecycler;

    private int totalActiveParents;

    @BindView(R.id.messages_refresher)
    SwipeRefreshLayout messagesRefresher;

    @BindView(R.id.send_msg_panel)
    RelativeLayout sendMsgPanel;

    @BindView(R.id.sendMessageEditText)
    EditText messageText;

    @BindView(R.id.sendMessageBtn)
    TextView sendMessage;

    @BindView(R.id.attachment)
    ImageView messageAttachment;

    FragmentMessagesBinding binding;

    @Inject
    @Named("POST")
    GanbookApiInterface ganbookApiInterfacePOST;

    @Inject
    @Named("GET")
    GanbookApiInterface ganbookApiInterfaceGET;

    public MessagesFragment() {
        // Required empty public constructor
    }


    public static MessagesFragment newInstance() {
        MessagesFragment fragment = new MessagesFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        messagesAdapter = new MessagesAdapter(getActivity(), this);

        setHasOptionsMenu(true);

        if (getArguments() != null) {

        }

        ((MyApp) getActivity().getApplication()).getGanbookApiComponent().inject(this);

        loadMessages();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        MyApp.sendAnalytics("messages-gan-ui", "messages-gan"+User.current.getCurrentGanId()+"-class"+User.current.getCurrentClassId()+"-ui"+ User.getId(), "messages-gan-ui", "MessagesGan");


        View rootView = inflater.inflate(R.layout.fragment_messages, container, false);


        ButterKnife.bind(this, rootView);
        binding = DataBindingUtil.bind(rootView);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setReverseLayout(true);
        messagesRecycler.setLayoutManager(linearLayoutManager);
        messagesRecycler.addItemDecoration(new SpaceItemDecoration(3, SpaceItemDecoration.VERTICAL));
        messagesRecycler.setAdapter(messagesAdapter);

        messagesRefresher.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                loadMessages();
            }
        });

        sendMsgPanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(MyApp.context, AddMessageActivity.class);

                startActivityForResult(i, Const.ADD_COMMENT_CODE);
            }
        });

        if(User.isTeacher() || User.current.getCurrentKidPTA()) {
            sendMsgPanel.setVisibility(View.VISIBLE);
        } else {
            sendMsgPanel.setVisibility(View.GONE);
        }

        binding.setVisibleHint(true);

        messageText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(messageText.getText().length() == 0) {
                    sendMessage.setEnabled(false);
                } else {
                    sendMessage.setEnabled(true);
                }
            }
        });


        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(messageText.getText().length() > 0) {
                    addNewMessage(messageText.getText().toString());
                }
            }
        });

        messageAttachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder builder =  new AlertDialog.Builder(getActivity());

                LayoutInflater inflater = getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.attachment_dialog_style, null);
                builder.setView(dialogView);

                final EditText pasteUrl = dialogView.findViewById(R.id.pasteUrlText);
                final Button sendUrl = dialogView.findViewById(R.id.uploadFileAttachment);
                Button cancelBtn = dialogView.findViewById(R.id.cancelDialogAttachment);
                dialog = builder.show();
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);


                cancelBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });


                sendUrl.setText(getString(R.string.send_text));
                sendUrl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        messageText.setText(pasteUrl.getText().toString());
                    }
                });

            }
        });
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onAttachmentFileEvent(AttachmentFileEvent attachmentFileEvent){

        Log.d(TAG, "onAttachmentFileEvent: received refresh drawer " + attachmentFileEvent.getFileUrl());

        messageText.setText(attachmentFileEvent.getFileUrl());
        dialog.dismiss();
        EventBus.getDefault().removeStickyEvent(attachmentFileEvent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, "onActivityResult: req code = " + requestCode + " res code = " + resultCode +
        " data = " + data);

        if (resultCode == Activity.RESULT_OK) {

            switch (requestCode) {

                case Const.ADD_COMMENT_CODE:

                    if (data.getExtras() != null) {

                        messagesAdapter.addItem(MessageModel.createMessage(data.getExtras().getString("message"),
                                data.getExtras().getString("message_id")));
                    }
                    break;

                case Const.CODE_EDIT_MESSAGE:

                    if (data.getExtras() != null) {

                        int pos = data.getExtras().getInt(Const.LIST_POSITION);
                        MessageModel message = data.getExtras()
                                .getParcelable(Const.ARG_MESSAGE);

                        Log.d(TAG, "onActivityResult: udapted message = " + message + " pos = " + pos);

                        messagesAdapter.updateItem(pos, message);
                    }

                    break;
            }
        }
    }

    protected void addNewMessage(String newMsgText) {
        String class_id = User.current.getCurrentClassId();
        String year = CurrentYear.get();

        final SweetAlertDialog progress = AlertUtils.createProgressDialog(getActivity(), getString(R.string.operation_proceeding));
        progress.show();

        Call<MessageStatus> call = ganbookApiInterfacePOST.createMessage(newMsgText, class_id);

        call.enqueue(new Callback<MessageStatus>() {
            @Override
            public void onResponse(Call<MessageStatus> call, Response<MessageStatus> response) {

                MessageStatus messageStatus = response.body();

                progress.dismiss();

                Log.d(TAG, "onResponse: success answer = " + messageStatus);

                if (messageStatus != null) {

                    CustomToast.show(getActivity(), R.string.operation_succeeded);
                    InputMethodManager imm = (InputMethodManager)Objects.requireNonNull(getActivity()).getSystemService(
                            Context.INPUT_METHOD_SERVICE);
                    Objects.requireNonNull(imm).hideSoftInputFromWindow(messageText.getWindowToken(), 0);
                    messageText.getText().clear();
                    loadMessages();

                }
            }

            @Override
            public void onFailure(Call<MessageStatus> call, Throwable t) {

                progress.hide();
                Log.e(TAG, "onFailure: error while send message = " + Log.getStackTraceString(t));

                if (NetworkUtils.isConnected()) {
                    CustomToast.show(getActivity(), R.string.error_send_message);
                } else {
                    CustomToast.show(getActivity(), R.string.internet_offline);
                }
            }
        });

    }

    @Subscribe
    public void onSelecteDrawerItem(SelectDrawerItemEvent selectDrawerItemEvent){

        Log.d(TAG, "onMessageEvent: in messages" + selectDrawerItemEvent);

        if (selectDrawerItemEvent.isSet) {

            loadMessages();
        }
    }

    private void loadMessages() {

        Call<MessageAnswer> messagesCall =  ganbookApiInterfaceGET
                .getMessages(User.current.getCurrentClassId(), User.getCurrentYear());

        messagesCall.enqueue(new Callback<MessageAnswer>() {
            @Override
            public void onResponse(Call<MessageAnswer> call, Response<MessageAnswer> response) {

                Log.d(TAG, "onResponse: success load = " + response.body());

                MessageAnswer messageAnswer = response.body();

                if (messageAnswer != null) {

                    messagesAdapter.clearList();
                    messagesRefresher.setRefreshing(false);

                    if (messageAnswer.getMessageModels().size() > 0) {

                        binding.setVisibleHint(false);

                        List<MessageModel> messages = messageAnswer.getMessageModels();

                        Log.d(TAG, "onResponse: loaded messages = " + messages);

                        totalActiveParents = messageAnswer.getTotalActiveParents();

                        messagesAdapter.addItems(messages);

                        messagesRecycler.scrollToPosition(messagesAdapter.getItemCount());
                    } else {

                        binding.setVisibleHint(true);
                        Log.e(TAG, "onResponse: empty messages list ");
                    }
                }
            }

            @Override
            public void onFailure(Call<MessageAnswer> call, Throwable t) {
                Dialogs.errorDialogWithButton(getActivity(), "Error!", getString(R.string.internet_offline), "OK");
                messagesRefresher.setRefreshing(false);
            }
        });
    }

    @Override
    public void setMenuVisibility(final boolean visible) {
        super.setMenuVisibility(visible);

        if (visible) {

            boolean isP = false;

            try {

                isP = User.isParent();
            } catch (NullPointerException npe) {

                Log.e(TAG, "setMenuVisibility: catched npe = " + Log.getStackTraceString(npe));
            }

            if (isP && messagesAdapter != null && messagesAdapter.getItemCount() > 0) {  // only!

                Call<SuccessAnswer> successAnswerCall = ganbookApiInterfacePOST
                        .updateReadMessage(User.current.id,
                                User.current.getCurrentKid().class_id,
                                messagesAdapter.getItem(messagesAdapter.getItemCount()-1).getMessageId());

                successAnswerCall.enqueue(new Callback<SuccessAnswer>() {
                    @Override
                    public void onResponse(Call<SuccessAnswer> call, Response<SuccessAnswer> response) {

                        SuccessAnswer successAnswer = response.body();

                        Log.d(TAG, "onResponse: success = " + successAnswer);

                        if (successAnswer != null && successAnswer.isSuccess()) {

                            User.current.updateUnreadMessage();
                            MainScreenFragment.updateMessageTabBadge();
                            MainActivity.updateDrawerContent();
                        } else {

                            //unsuccess view here
                        }
                    }

                    @Override
                    public void onFailure(Call<SuccessAnswer> call, Throwable t) {

                        Log.e(TAG, "error while update message read = " + Log.getStackTraceString(t));
                    }
                });
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public int getTotalActiveParents() {

        return totalActiveParents;
    }

    @Override
    public void editMessage(MessageModel messageModel, int position) {

        Intent i = new Intent(MyApp.context, AddMessageActivity.class);

        i.putExtra(Const.ARG_MESSAGE, messageModel);
        i.putExtra(Const.LIST_POSITION, position);

        startActivityForResult(i, Const.CODE_EDIT_MESSAGE);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.messages_fragment_menu, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {
//            case R.id.bit_app:
//                UrlUtils.openBitApp(this.getContext());
//                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

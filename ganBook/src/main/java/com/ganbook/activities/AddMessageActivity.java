package com.ganbook.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.ganbook.amazinglist.special.CustomToast;
import com.ganbook.app.MyApp;
import com.ganbook.models.MessageModel;
import com.ganbook.models.MessageStatus;
import com.ganbook.models.SuccessAnswer;
import com.ganbook.models.events.AttachmentFileEvent;
import com.ganbook.models.events.RefreshDrawerEvent;
import com.ganbook.services.UploadDrawingService;
import com.ganbook.services.UploadMessageAttachmentService;
import com.ganbook.user.User;
import com.ganbook.utils.AlertUtils;
import com.ganbook.utils.Const;
import com.ganbook.utils.CurrentYear;
import com.ganbook.utils.KeyboardUtils;
import com.ganbook.utils.NetworkUtils;

import com.obsez.android.lib.filechooser.ChooserDialog;
import com.project.ganim.R;
import com.rockerhieu.emojicon.EmojiconEditText;
import com.rockerhieu.emojicon.EmojiconGridFragment;
import com.rockerhieu.emojicon.EmojiconsFragment;
import com.rockerhieu.emojicon.emoji.Emojicon;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;

import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by Noa on 16/03/2016.
 */
public class AddMessageActivity extends BaseAppCompatActivity implements  EmojiconGridFragment.OnEmojiconClickedListener, EmojiconsFragment.OnEmojiconBackspaceClickedListener {

    private static final String TAG = AddMessageActivity.class.getName();
    private ViewSwitcher switcher;
    private LinearLayout footer_for_emoticons;
    public EmojiconEditText mEditEmojicon;
    private MessageModel messageToEdit;
    private ImageView messageAttachment;
    int pos = -1;
    private boolean editMode = false;
    private static int REQUEST_FILE_CODE = 8;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MyApp.sendAnalytics("add-message-ui", "add-message-ui-"+User.getId(), "add-message-ui", "AddMessage");


        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        setContentView(R.layout.activity_editcomment);

        setActionBar(getString(R.string.messages), false);

        final TextView sendBtn = (TextView) findViewById(R.id.save_button);

        sendBtn.setText(getString(R.string.send_btn));

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newMsgText = mEditEmojicon.getText().toString();
                if (newMsgText.length() == 0)
                {
                    Toast.makeText(AddMessageActivity.this, "Please enter some value",
                            Toast.LENGTH_SHORT).show();
                }
                else
                {
                    if(User.current.getCurrentKidPTA())
                    {
                        newMsgText = getString(R.string.message_from_pta) + "\n" + newMsgText;
                    }

                    addNewMessage(newMsgText);
                }
            }
        });

        mEditEmojicon = findViewById(R.id.et_edit_comment);
/*        messageAttachment = findViewById(R.id.attachment);

        messageAttachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "CLICKED");
                final AlertDialog.Builder builder =  new AlertDialog.Builder(AddMessageActivity.this);

                LayoutInflater inflater = getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.attachment_dialog_style, null);
                builder.setView(dialogView);

                final Button uploadFileBtn = dialogView.findViewById(R.id.uploadFileAttachment);
                final EditText pasteUrl = dialogView.findViewById(R.id.pasteUrlText);

                dialog = builder.show();
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

                uploadFileBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new ChooserDialog().with(AddMessageActivity.this)
                                .withStartFile("*")
                                .withChosenListener(new ChooserDialog.Result() {
                                    @Override
                                    public void onChoosePath(String path, File pathFile) {
                                        Log.d("FILE: ", pathFile.getName());
                                        Intent intent = new Intent(AddMessageActivity.this, UploadMessageAttachmentService.class);

                                        intent.putExtra("attachmentFilePath", path);
                                        intent.putExtra("attachmentFileName", pathFile.getName());
                                        startService(intent);
                                    }
                                })
                                .build()
                                .show();
                    }
                });

                pasteUrl.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        int len = pasteUrl.getText().toString().length();

                        if (len == 0) {
                            uploadFileBtn.setText("Upload file");
                            uploadFileBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    new ChooserDialog().with(AddMessageActivity.this)
                                            .withStartFile("*")
                                            .withChosenListener(new ChooserDialog.Result() {
                                                @Override
                                                public void onChoosePath(String path, File pathFile) {
                                                    Log.d("FILE: ", pathFile.getName());
                                                    Intent intent = new Intent(AddMessageActivity.this, UploadMessageAttachmentService.class);

                                                    intent.putExtra("attachmentFilePath", path);
                                                    intent.putExtra("attachmentFileName", pathFile.getName());
                                                    startService(intent);
                                                }
                                            })
                                            .build()
                                            .show();
                                }
                            });
                        } else {
                            uploadFileBtn.setText("Send");
                            uploadFileBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialog.dismiss();
                                    mEditEmojicon.setText(pasteUrl.getText().toString());
                                }
                            });
                        }
                    }
                });
            }
        });*/

        mEditEmojicon.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                int len = mEditEmojicon.getText().toString().length();
                if (len == 0) {
                    sendBtn.setTextColor(Color.GRAY);
                    sendBtn.setEnabled(false);
                } else {
                    sendBtn.setTextColor(Color.parseColor("#45c8f4"));
                    sendBtn.setEnabled(true);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count){}
        });

        switcher = (ViewSwitcher) findViewById(R.id.switcher);
        footer_for_emoticons = (LinearLayout) findViewById(R.id.footer_for_emoticons);

        switcher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ViewSwitcher switcher = (ViewSwitcher) v;

                if(switcher.getDisplayedChild() == 0)
                {
                    switcher.showNext();
                    KeyboardUtils.close(AddMessageActivity.this, mEditEmojicon);
                    footer_for_emoticons.setVisibility(View.VISIBLE);
                }
                else
                {
                    switcher.showPrevious();
                    footer_for_emoticons.setVisibility(View.GONE);
                    InputMethodManager inputMethodManager=(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
                }
            }
        });

        setEmojiconFragment(false);

        Bundle args = getIntent().getExtras();

        if (args != null) {

            editMode = true;

            messageToEdit = args.getParcelable(Const.ARG_MESSAGE);
            pos = args.getInt(Const.LIST_POSITION);

            Log.d(TAG, "onCreate: received message to edit = " + messageToEdit);

            mEditEmojicon.setText(messageToEdit.getMessageText());
        }
    }




    private void setEmojiconFragment(boolean useSystemDefault) {
            getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.emojicons, EmojiconsFragment.newInstance(useSystemDefault))
            .commit();
	}




    protected void addNewMessage(final String newMsgText) {
        String class_id = User.current.getCurrentClassId();
        String year = CurrentYear.get();

        final SweetAlertDialog progress = AlertUtils.createProgressDialog(this, getString(R.string.operation_proceeding));
        progress.show();

        if (!editMode) {
            Call<MessageStatus> call = ganbookApiInterfacePOST.createMessage(newMsgText, class_id);

            call.enqueue(new Callback<MessageStatus>() {
                @Override
                public void onResponse(Call<MessageStatus> call, Response<MessageStatus> response) {

                    MessageStatus messageStatus = response.body();

                    progress.dismiss();

                    Log.d(TAG, "onResponse: success answer = " + messageStatus);

                    if (messageStatus != null) {

                        CustomToast.show(AddMessageActivity.this, R.string.operation_succeeded);

                        KeyboardUtils.close(AddMessageActivity.this, mEditEmojicon);

                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("message", newMsgText);
                        returnIntent.putExtra("message_id", messageStatus.getMessageId());
                        setResult(Activity.RESULT_OK, returnIntent);
                        finish();
                    }
                }

                @Override
                public void onFailure(Call<MessageStatus> call, Throwable t) {

                    progress.hide();
                    Log.e(TAG, "onFailure: error while send message = " + Log.getStackTraceString(t));

                    if (NetworkUtils.isConnected()) {
                        CustomToast.show(AddMessageActivity.this, R.string.error_send_message);
                    } else {
                        CustomToast.show(AddMessageActivity.this, R.string.internet_offline);
                    }
                }
            });
        } else {

            messageToEdit.setMessageText(newMsgText);

            Call<SuccessAnswer> call = ganbookApiInterfacePOST.updateMessage(messageToEdit.getMessageId(),
                    newMsgText, class_id);

            progress.show();
            call.enqueue(new Callback<SuccessAnswer>() {
                @Override
                public void onResponse(Call<SuccessAnswer> call, Response<SuccessAnswer> response) {

                    progress.hide();
                    SuccessAnswer successAnswer = response.body();

                    if (successAnswer == null) {

                        
                    } else {

                        Log.d(TAG, "onResponse: success update message = " + successAnswer);

                        KeyboardUtils.close(AddMessageActivity.this, mEditEmojicon);

                        Intent returnIntent = new Intent();
                        returnIntent.putExtra(Const.ARG_MESSAGE, messageToEdit);
                        returnIntent.putExtra(Const.LIST_POSITION, pos);
                        setResult(Activity.RESULT_OK, returnIntent);
                        finish();
                    }
                }

                @Override
                public void onFailure(Call<SuccessAnswer> call, Throwable t) {

                    CustomToast.show(AddMessageActivity.this, R.string.error_send_message);
                    progress.hide();
                    Log.e(TAG, "onFailure: error while update message = " + Log.getStackTraceString(t));
                }
            });
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onAttachmentFileEvent(AttachmentFileEvent attachmentFileEvent){

        Log.d(TAG, "onAttachmentFileEvent: received refresh drawer " + attachmentFileEvent.getFileUrl());

        mEditEmojicon.setText(attachmentFileEvent.getFileUrl());
        dialog.dismiss();
        EventBus.getDefault().removeStickyEvent(attachmentFileEvent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onPause() {

        super.onPause();
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
    }

    @Override
    public void onBackPressed() {
        if(footer_for_emoticons.getVisibility() == View.GONE) {
            String newMsgText = mEditEmojicon.getText().toString();
            if (newMsgText.length() > 0)
            {

                SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE);

                sweetAlertDialog.setTitleText(getString(R.string.cancel_changes));
                sweetAlertDialog.setContentText(getString(R.string.cancel_changes_explanation));
                sweetAlertDialog.setConfirmText(getString(R.string.continue_));
                sweetAlertDialog.setCancelText(getString(R.string.popup_btn_cancel));

                sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {

                        sweetAlertDialog.dismiss();
                    }
                });

                sweetAlertDialog.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        finish();
                    }
                });

                sweetAlertDialog.show();
            } else {
                super.onBackPressed();
            }
        } else {
            footer_for_emoticons.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
    }

	@Override
	public void onEmojiconClicked(Emojicon emojicon) {
		EmojiconsFragment.input(mEditEmojicon, emojicon);
	}

	@Override
	public void onEmojiconBackspaceClicked(View v) {
		EmojiconsFragment.backspace(mEditEmojicon);
	}
}

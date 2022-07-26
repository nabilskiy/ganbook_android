package com.ganbook.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.ganbook.amazinglist.special.CustomToast;
import com.ganbook.app.MyApp;
import com.ganbook.models.SuccessAnswer;
import com.ganbook.user.User;
import com.ganbook.utils.Const;
import com.ganbook.utils.KeyboardUtils;

import com.project.ganim.R;
import com.rockerhieu.emojicon.EmojiconEditText;
import com.rockerhieu.emojicon.EmojiconGridFragment;
import com.rockerhieu.emojicon.EmojiconsFragment;
import com.rockerhieu.emojicon.emoji.Emojicon;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Noa on 07/10/2015.
 */
public class EditCommentActivity extends BaseAppCompatActivity implements  EmojiconGridFragment.OnEmojiconClickedListener,
        EmojiconsFragment.OnEmojiconBackspaceClickedListener {

    private static final String TAG = EditCommentActivity.class.getName();

    String album_id;
    String comment_id;
    String album_comment;

    EditText et_edit_comment;
//    TextView tv_save_comment;

    ViewSwitcher switcher;
    LinearLayout footer_for_emoticons;

    public static EmojiconEditText mEditEmojicon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MyApp.sendAnalytics("edit-comment-ui", "edit-comment-ui-"+ User.getId(), "edit-comment-ui", "EditComment");

        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        setContentView(R.layout.activity_editcomment);
        setActionBar(getString(R.string.comments), false);

        final Activity a = this;
        TextView save_btn = (TextView) findViewById(R.id.save_button);
        save_btn.setText(getString(R.string.delete));
        save_btn.setTextColor(getResources().getColor(R.color.red));

        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KeyboardUtils.close(a, et_edit_comment);
                openDeletePopup();
            }
        });

        final TextView back_btn = (TextView) findViewById(R.id.delete);
        back_btn.setVisibility(View.VISIBLE);
        back_btn.setText(getString(R.string.save_btn));

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KeyboardUtils.close(a, et_edit_comment);
                call_updatecomment();
            }
        });

        et_edit_comment = (EditText) findViewById(R.id.et_edit_comment);

        comment_id = getIntent().getStringExtra("comment_id");
        album_comment = getIntent().getStringExtra("album_comment");
        album_id = getIntent().getStringExtra("album_id");

        et_edit_comment.setText(album_comment);

        mEditEmojicon = (EmojiconEditText) findViewById(R.id.et_edit_comment);

        mEditEmojicon.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                int len = mEditEmojicon.getText().toString().length();
                if (len == 0) {
                    back_btn.setTextColor(Color.GRAY);
                    back_btn.setEnabled(false);
                } else {
                    back_btn.setTextColor(Color.parseColor("#45c8f4"));
                    back_btn.setEnabled(true);
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
                    KeyboardUtils.close(EditCommentActivity.this, mEditEmojicon);
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

    }

    private void setEmojiconFragment(boolean useSystemDefault) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.emojicons, EmojiconsFragment.newInstance(useSystemDefault))
                .commit();
    }

    @Override
    public void onBackPressed() {
        if(footer_for_emoticons.getVisibility() == View.GONE)
        {
                super.onBackPressed();
        }
        else
        {
            footer_for_emoticons.setVisibility(View.GONE);
        }
    }

    private void openDeletePopup() {
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        //		adb.setView(alertDialogView);
        adb.setTitle(R.string.delete_comment_pop_text);
        adb.setIcon(R.drawable.ic_launcher);
        adb.setPositiveButton(R.string.popup_btn_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                MyApp.sendAnalytics("delete-comment-ui", "delete-comment"+"-ui"+ User.getId(), "delete-comment-ui", "DeleteComment");

                call_deletecomment();
            } });
        adb.setNegativeButton(R.string.popup_btn_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // no op
            } });

        adb.show();
    }

    @Override
    protected void onPause() {

        super.onPause();
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
    }

    private void call_deletecomment()
    {
        final Activity a = this;

        Call<SuccessAnswer> successAnswerCall = ganbookApiInterfacePOST.deleteAlbumComment(comment_id);

        successAnswerCall.enqueue(new Callback<SuccessAnswer>() {
            @Override
            public void onResponse(Call<SuccessAnswer> call, Response<SuccessAnswer> response) {

                if (response.body().isSuccess()) {
                    Intent intent = new Intent();

                    intent.putExtra(Const.COMMENT_ID, comment_id);

                    setResult(Const.CODE_DELETE_ITEM, intent);

                    finish();
                }
            }

            @Override
            public void onFailure(Call<SuccessAnswer> call, Throwable t) {

                Log.e(TAG, "onFailure: error while delete comment = " + Log.getStackTraceString(t));

                CustomToast.show(a, R.string.error_delete_comment);
            }
        });
    }


    private void call_updatecomment()
    {
        final Activity a = this;
        album_comment = et_edit_comment.getText().toString();

        Call<SuccessAnswer> successAnswerCall = ganbookApiInterfacePOST.updateAlbumComment(comment_id, album_comment);

        successAnswerCall.enqueue(new Callback<SuccessAnswer>() {
            @Override
            public void onResponse(Call<SuccessAnswer> call, Response<SuccessAnswer> response) {

                if (response.body().isSuccess()) {

                    Intent intent = new Intent();

                    intent.putExtra(Const.COMMENT_ID, comment_id);
                    intent.putExtra(Const.COMMENT_TEXT, album_comment);

                    setResult(Const.CODE_EDIT_ITEM, intent);

                    finish();
                }
            }

            @Override
            public void onFailure(Call<SuccessAnswer> call, Throwable t) {

                Log.e(TAG, "onFailure: error while update comment = " + Log.getStackTraceString(t));

                CustomToast.show(a, R.string.error_update_comment);
            }
        });
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

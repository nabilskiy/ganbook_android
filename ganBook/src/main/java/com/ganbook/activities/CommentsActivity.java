package com.ganbook.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.ganbook.amazinglist.special.CustomToast;
import com.ganbook.app.MyApp;
import com.ganbook.communication.json.createalbumcomment_Response;
import com.ganbook.communication.json.getalbumcomments_response;
import com.ganbook.models.events.UpdateAlbumComments;
import com.ganbook.ui.CircleImageView;
import com.ganbook.user.User;
import com.ganbook.utils.Const;
import com.ganbook.utils.DateFormatter;
import com.ganbook.utils.ImageUtils;
import com.ganbook.utils.KeyboardUtils;

import com.project.ganim.R;
import com.rockerhieu.emojicon.EmojiconEditText;
import com.rockerhieu.emojicon.EmojiconGridFragment;
import com.rockerhieu.emojicon.EmojiconsFragment;
import com.rockerhieu.emojicon.emoji.Emojicon;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Noa on 07/10/2015.
 */
public class CommentsActivity extends BaseAppCompatActivity implements SwipeRefreshLayout.OnRefreshListener ,EmojiconGridFragment.OnEmojiconClickedListener, EmojiconsFragment.OnEmojiconBackspaceClickedListener{

    private static final String TAG = CommentsActivity.class.getName();
    private static final int COMMENT_DETAILS_CODE = 0;
    ListView comments_list;
    String album_id;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private Button send_btn;
    private ImageButton up_navigation;
    public EmojiconEditText mEditEmojicon;
    LinearLayout footer_for_emoticons;
    ViewSwitcher switcher;
    CommentsAdapter adapter;
    ArrayList<getalbumcomments_response> commentsArr;
    View empty_header;
    View error_header;
    int pos;
    private int newSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        newSize = 0;


        MyApp.sendAnalytics("Comments-ui", "comments-ui-"+User.getId(), "comments-ui", "Comments");


        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        setContentView(R.layout.activity_comments);

        setActionBar(getString(R.string.comments), true);

        send_btn = (Button) findViewById(R.id.send);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(CommentsActivity.this);
        mSwipeRefreshLayout.setColorScheme(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);


        album_id = getIntent().getStringExtra("album_id");
        pos = getIntent().getIntExtra("pos", -1);

        send_btn = (Button) findViewById(R.id.send);

        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                String newMsgText = mEditEmojicon.getText().toString();
                if (newMsgText.length() == 0) {
//                    Toast.makeText(CommentsActivity.this, "Please enter some value",
//                            Toast.LENGTH_SHORT).show();
                }
                else {
                    addNewComment(newMsgText);
                }
            }
        });

        footer_for_emoticons = (LinearLayout) findViewById(R.id.footer_for_emoticons);
        mEditEmojicon = (EmojiconEditText) findViewById(R.id.editEmojicon);

        mEditEmojicon.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                int len = mEditEmojicon.getText().toString().length();
                if (len == 0) {
                    send_btn.setTextColor(Color.GRAY);
                } else {
                    send_btn.setTextColor(Color.BLACK);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count){}
        });

        switcher = (ViewSwitcher) findViewById(R.id.switcher);

        switcher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ViewSwitcher switcher = (ViewSwitcher) v;

                if(switcher.getDisplayedChild() == 0)
                {
                    switcher.showNext();
                    KeyboardUtils.close(CommentsActivity.this,mEditEmojicon);
                    footer_for_emoticons.setVisibility(View.VISIBLE);
                }
                else
                {
                    switcher.showPrevious();
                    footer_for_emoticons.setVisibility(View.GONE);
                    InputMethodManager inputMethodManager=(InputMethodManager)CommentsActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
                }
            }
        });

        comments_list = (ListView) findViewById(R.id.comments_list);

        LayoutInflater inflater = getLayoutInflater();
        empty_header = inflater.inflate(R.layout.item_composer_text, comments_list, false);
        ((TextView)empty_header.findViewById(R.id.tv)).setText(getString(R.string.empty_comments_list));

        error_header = inflater.inflate(R.layout.header_error_layout, comments_list, false);


        comments_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView tv_edit_comment = (TextView) view.findViewById(R.id.tv_edit_comment);
                if(tv_edit_comment != null && tv_edit_comment.getVisibility() == View.VISIBLE)
                {
                    Intent i = new Intent(MyApp.context, EditCommentActivity.class);

                    i.putExtra("comment_id",commentsArr.get(position).comment_id);
                    i.putExtra("album_comment",commentsArr.get(position).album_comment);
                    i.putExtra("album_id",album_id);

                    startActivityForResult(i, COMMENT_DETAILS_CODE);
                }
            }
        });
        setEmojiconFragment(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {

            finish();
        }
        return super.onOptionsItemSelected(menuItem);
    }

    private void setEmojiconFragment(boolean useSystemDefault) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.emojicons, EmojiconsFragment.newInstance(useSystemDefault))
                .commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, "onActivityResult: album comments " + " req code = " + requestCode +
                " res code = " + resultCode + " data = " + data);

        if (requestCode == COMMENT_DETAILS_CODE) {

            switch (resultCode) {

                case Const.CODE_DELETE_ITEM:

                    deleteComment(data.getStringExtra(Const.COMMENT_ID));
                    break;

                case Const.CODE_EDIT_ITEM:

                    updateComment(data.getStringExtra(Const.COMMENT_ID), data.getStringExtra(Const.COMMENT_TEXT));
                    break;
            }
        }
    }

    public void updateComment(String comment_id, String album_comment) {

            for (getalbumcomments_response comment:commentsArr) {

                if(comment.getComment_id().equals(comment_id)) {

                    comment.album_comment = album_comment;
                    break;
                }
            }

            adapter.notifyDataSetChanged();
    }

    public void deleteComment(String comment_id) {

            int ind;

            for (getalbumcomments_response comment:commentsArr) {

                if(comment.getComment_id().equals(comment_id)) {

                    commentsArr.remove(comment);
                    break;
                }
            }

            newSize--;
            adapter.notifyDataSetChanged();
    }

    private void addNewComment(final String newMsgText) {
//        startProgress(R.string.operation_proceeding);

        Call<createalbumcomment_Response> successCall = ganbookApiInterfacePOST.createAlbumComment(album_id, newMsgText);

        successCall.enqueue(new Callback<createalbumcomment_Response>() {
            @Override
            public void onResponse(Call<createalbumcomment_Response> call, Response<createalbumcomment_Response> response) {

                comments_list.removeHeaderView(empty_header);
                comments_list.removeHeaderView(error_header);

                if(commentsArr == null)
                {
                    commentsArr = new ArrayList<>();
                }

                if(adapter == null)
                {
                    adapter = new CommentsAdapter(MyApp.context);
                }

                createalbumcomment_Response responseComment = response.body();

                commentsArr.add(commentsArr.size(), getalbumcomments_response.createNewComment(newMsgText,responseComment.comment_id)); //add as first in list

                adapter.notifyDataSetChanged();

                mEditEmojicon.setText("");

                KeyboardUtils.close(CommentsActivity.this, mEditEmojicon);
                if (footer_for_emoticons.getVisibility() == View.VISIBLE) {
                    footer_for_emoticons.setVisibility(View.GONE);
                }
                switcher.setDisplayedChild(0);

                newSize++;
            }

            @Override
            public void onFailure(Call<createalbumcomment_Response> call, Throwable t) {

                Log.e(TAG, "onFailure: error while add new comment = " + Log.getStackTraceString(t));

                comments_list.removeHeaderView(error_header);
                comments_list.removeHeaderView(empty_header);

                comments_list.addHeaderView(error_header);

                adapter = new CommentsAdapter(MyApp.context);

                comments_list.setAdapter(adapter);

                CustomToast.show(CommentsActivity.this, R.string.error_while_add_comment);
            }
        });
    }

    @Override
    public void onRefresh() {
        mSwipeRefreshLayout.setRefreshing(false); //!
        call_getcomments();
    }

    @Override
    protected void onResume() {
        super.onResume();
        call_getcomments();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        overridePendingTransition(R.anim.fadein, R.anim.fadeout);

        EventBus.getDefault().postSticky(new UpdateAlbumComments(pos, newSize));
    }

    @Override
    protected void onPause() {

        super.onPause();
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
    }

    private void call_getcomments() {

        final Activity a = this;

        Call<List<getalbumcomments_response>> commentsCall = ganbookApiInterfaceGET.getAlbumComments(album_id);

        commentsCall.enqueue(new Callback<List<getalbumcomments_response>>() {
            @Override
            public void onResponse(Call<List<getalbumcomments_response>> call, Response<List<getalbumcomments_response>> response) {

                comments_list.removeHeaderView(empty_header);
                comments_list.removeHeaderView(error_header);

                if (response.body() != null) {
                    int num = response.body().size();

                    if (num == 0) {

                        comments_list.removeHeaderView(empty_header);
                        comments_list.addHeaderView(empty_header);

                        commentsArr = new ArrayList<>();

                        adapter = new CommentsAdapter(MyApp.context);

                        comments_list.setAdapter(adapter);

                        return;
                    }

                    commentsArr = new ArrayList<>();

                    for (int i = 0; i < num; i++) {
                        commentsArr.add(response.body().get(i));
                    }

                    Collections.reverse(commentsArr);

                    adapter = new CommentsAdapter(MyApp.context);
                    comments_list.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<List<getalbumcomments_response>> call, Throwable t) {

                Log.e(TAG, "onFailure: error while load comments = " + Log.getStackTraceString(t));

                CustomToast.show(a, R.string.error_load_comments);

                comments_list.removeHeaderView(empty_header);
                comments_list.removeHeaderView(error_header);

                comments_list.addHeaderView(error_header);

                commentsArr = new ArrayList<>();

                adapter = new CommentsAdapter(MyApp.context);

                comments_list.setAdapter(adapter);
            }
        });
    }

    class CommentsAdapter extends BaseAdapter {
//        private ArrayList<getalbumcomments_response> commentList;
        private LayoutInflater inflater;

        public CommentsAdapter(Context cnt) {
//            this.commentList = list;
            this.inflater = LayoutInflater.from(cnt);
        }

        @Override
        public int getCount() {
            return commentsArr.size();
        }

        @Override
        public Object getItem(int position) {
            return commentsArr.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        class ViewHolder {
            TextView tv_comment, tv_date, tv_edit_comment,tv_title;
            CircleImageView profile_image;

        }

        ;

        @SuppressLint("InflateParams")
        @Override
        public View getView(int position, View view, ViewGroup parent) {
            ViewHolder vholder;
            if (view == null) {

                Locale locale = getResources().getConfiguration().locale;

                if(locale.equals(Locale.ENGLISH) || locale.equals(Locale.US) || locale.equals(Locale.UK))
                {
                    view = inflater.inflate(R.layout.item_comment, null);
                }
                else
                {
                    view = inflater.inflate(R.layout.item_comment_he, null);
                }

                vholder = new ViewHolder();
                vholder.tv_comment = (TextView) view.findViewById(R.id.tv_comment);
                vholder.tv_date = (TextView) view.findViewById(R.id.tv_date);
                vholder.tv_edit_comment = (TextView) view.findViewById(R.id.tv_edit_comment);
                vholder.tv_title = (TextView) view.findViewById(R.id.tv_title);
                vholder.profile_image = (CircleImageView) view.findViewById(R.id.profile_image);

                view.setTag(vholder);
            } else {
                vholder = (ViewHolder) view.getTag();
            }

            getalbumcomments_response current = commentsArr.get(position);

            Log.d(TAG, "getView: current response = " + current);

            vholder.tv_comment.setText(current.album_comment);

            vholder.tv_date.setText(DateFormatter.formatStringDate(current.comment_date,"yyyy-MM-dd hh:mm","yyyy-MM-dd, HH:mm"));

            if (User.current.id.equals(current.user_id)) {
                vholder.tv_edit_comment.setVisibility(View.VISIBLE);
            } else {
                vholder.tv_edit_comment.setVisibility(View.GONE);
            }

            String title = getCommentTitle(current);
            vholder.tv_title.setText(title);

            if(current.kid_pic == null)
            {
                int defImgRes;
                if(User.Type_Teacher.equals(current.user_type))
                {
                    defImgRes = R.drawable.kindergarten_icon;
                }
                else
                {
                    defImgRes = User.getDeafultKidImg(current.kid_gender);
                }

                vholder.profile_image.setImageResource(defImgRes);
            }
            else
            {
                Log.d(TAG, "getView: kid gender = " + current.kid_gender);
                String url = ImageUtils.kidPicToUrl(current.kid_pic);
                ImageUtils.getKidPicture(url,vholder.profile_image,0);
            }

            return view;
        }

        private String getCommentTitle(getalbumcomments_response current) {
            String title = "";

            title += current.user_first_name;

            if(current.user_type.equals(User.Type_Teacher))
            {
                title += ":";
                return title;
            }

            title += ", ";

            String parent = "";

            if(current.user_type.equals(User.Type_Parent))
            {
                parent = getString(R.string.parent);
            }
            else if(current.user_type.equals(User.Type_Mother))
            {
                parent = getString(R.string.mother);
            }
            else if(current.user_type.equals(User.Type_Father))
            {
                parent = getString(R.string.father);
            }

            String of = getString(R.string.of);

            Locale locale = getResources().getConfiguration().locale;

            if(locale.equals(Locale.ENGLISH) || locale.equals(Locale.US) || locale.equals(Locale.UK))
            {
                title += current.kid_name + of + " " + parent;
            }
            else
            {
                title += parent + " " + of + " " + current.kid_name;
            }

            title += ":";

            return title;
        }
    }

    @Override
    public void onEmojiconClicked(Emojicon emojicon) {
        EmojiconsFragment.input(mEditEmojicon, emojicon);
    }

    @Override
    public void onEmojiconBackspaceClicked(View v) {
        EmojiconsFragment.backspace(mEditEmojicon);
    }

    @Override
    public void onBackPressed() {

        if(footer_for_emoticons.getVisibility() == View.VISIBLE) {
            footer_for_emoticons.setVisibility(View.GONE);
        }
        else {
            super.onBackPressed();
        }
    }
}

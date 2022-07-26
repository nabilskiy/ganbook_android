package com.ganbook.adapters;

import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.media.MediaPlayer;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.ganbook.activities.CommentsActivity;
import com.ganbook.amazinglist.special.CustomToast;
import com.ganbook.app.MyApp;
import com.ganbook.fragments.AlbumDetailsFragment;
import com.ganbook.interfaces.CRUDAdapterInterface;
import com.ganbook.interfaces.GanbookApiInterface;
import com.ganbook.interfaces.KidWithoutGanInterface;
import com.ganbook.interfaces.RecyclerViewItem;
import com.ganbook.models.AlbumsAnswer;
import com.ganbook.models.AlbumsYearModel;
import com.ganbook.models.SuccessAnswer;
import com.ganbook.user.User;
import com.ganbook.utils.CurrentYear;
import com.ganbook.utils.FragmentUtils;
import com.ganbook.utils.KeyboardUtils;
import com.ganbook.utils.StrUtils;
import com.project.ganim.R;
import com.project.ganim.databinding.HeaderAlbumListBinding;
import com.project.ganim.databinding.ItemAlbumListBinding;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by dmytro_vodnik on 8/17/16.
 * working on ganbook1 project
 */
public class KidWithoutGanAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements CRUDAdapterInterface<RecyclerViewItem> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ALBUM = 1;
    private static final int TYPE_YEAR = 2;
    private static final String TAG = KidWithoutGanAdapter.class.getName();
    private final Context context;
    private final LayoutInflater inflater;
    ArrayList<RecyclerViewItem> items;
    KidWithoutGanInterface kidWithoutGanInterface;

    @Inject
    @Named("POST")
    GanbookApiInterface ganbookApiInterfacePOST;

    public KidWithoutGanAdapter(Context context, KidWithoutGanInterface kidWithoutGanInterface) {

        this.context = context;
        this.inflater = LayoutInflater.from(context);
        items = new ArrayList<>();
        this.kidWithoutGanInterface = kidWithoutGanInterface;
        ((MyApp) ((Activity) context).getApplication()).getGanbookApiComponent().inject(this);
    }

    @Override
    public int getItemViewType(int position) {

        if (position == 0)
            return TYPE_HEADER;

        if (getItem(position) instanceof AlbumsYearModel)
            return TYPE_YEAR;

        if (getItem(position) instanceof AlbumsAnswer)
            return TYPE_ALBUM;

        return 0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        switch (viewType) {

            case TYPE_HEADER:

                View headerView;

                if (User.current.kidIsWaitingApproval()) {
                    headerView = inflater.inflate(R.layout.kid_waiting_approval_fragment, parent, false);
                } else
                    headerView = inflater.inflate(R.layout.enter_code_unattached__kid_fragment, parent, false);

                return new HeaderItem(headerView);

            case TYPE_ALBUM:
                ItemAlbumListBinding binding = DataBindingUtil.inflate(inflater, R.layout.item_album_list,
                        parent, false);

                return new ViewHolderAlbum(binding);

            case TYPE_YEAR:

                HeaderAlbumListBinding bindingA =  DataBindingUtil.inflate(inflater, R.layout.header_album_list,
                        parent, false);

                return new ViewHolderYear(bindingA);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        if (holder instanceof HeaderItem) {

            View headerView = ((HeaderItem) holder).root;

            if (!User.current.kidIsWaitingApproval()) {

                final EditText gan_code = (EditText) headerView.findViewById(R.id.gan_code);

                headerView.findViewById(R.id.send_btn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        KeyboardUtils.close((Activity) context, gan_code);
                        String code = gan_code.getText().toString();
                        if (code.length() == 0) {
                            CustomToast.show((Activity) context, R.string.enter_code_msg);
                            return;
                        }

                        kidWithoutGanInterface.call_getKindergarten(code);
                    }
                });

                Button cont_btn = (Button) headerView.findViewById(R.id.cont_btn);
                TextView or = (TextView) headerView.findViewById(R.id.or);

                if (User.current.getCurrentKidHistory() != null && User.current.getCurrentKidHistory().length > 0) {
                    or.setVisibility(View.VISIBLE);
                    cont_btn.setVisibility(View.VISIBLE);
                } else {
                    or.setVisibility(View.GONE);
                    cont_btn.setVisibility(View.GONE);
                }

                cont_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (User.current.getCurrentKidHistory() != null && User.current.getCurrentKidHistory().length > 0) {
                            String code = User.current.getCurrentKidHistory()[0].gan_code;
                            kidWithoutGanInterface.call_getKindergarten(code);
                        }
                    }
                });
            } else {

//                headerView.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        kidWithoutGanInterface.call_getKids();
//                    }
//                });
            }

            TextView blue_header = (TextView) headerView.findViewById(R.id.blue_header);
            String cur_year = CurrentYear.get();
            blue_header.setText(cur_year);
        }

        if (holder instanceof ViewHolderYear) {

            final AlbumsYearModel albumsYearModel = (AlbumsYearModel) getItem(position);

            Log.d(TAG, "onBindViewHolder: year = " + albumsYearModel);

            ((ViewHolderYear) holder).binding.setDown(albumsYearModel.getYear());

            ((ViewHolderYear) holder).binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (!albumsYearModel.isSelected()) {
                        Log.d(TAG, "onClick: load albums for = " + albumsYearModel);

                        kidWithoutGanInterface.loadAlbums(albumsYearModel.getYear(),
                                albumsYearModel.getClassId(), albumsYearModel.getGanId(),
                                items.indexOf(albumsYearModel) + 1, albumsYearModel);
                    }
                }
            });
        }

        if (holder instanceof ViewHolderAlbum) {

            final AlbumsAnswer albumsAnswer = (AlbumsAnswer) getItem(position);

            Log.d(TAG, "onBindViewHolder: current album answer = " + albumsAnswer);

            //bind album to view
            ((ViewHolderAlbum) holder).binding.setAlbum(albumsAnswer);

            final String class_id = albumsAnswer.getClassId();
            final String gan_id = albumsAnswer.getGanId();

            //bind picture preview
            String picUrl = StrUtils.getAlbumFullSizeUrl(albumsAnswer.getPicPath(),
                    albumsAnswer.getAlbumId(), class_id, gan_id);

            Log.d(TAG, "onBindViewHolder: picurl = " + picUrl);
            ((ViewHolderAlbum) holder).binding.setPreview(picUrl);

            //bind boolean to show/hide new ribbon
            ((ViewHolderAlbum) holder).binding.setShowNewLabel(User.isParent() && albumsAnswer.getUnseenPhotos() != 0);

            //process titles for video/photo count
            String photosPref = context.getString(R.string.photos_pref);
            String videosPref = context.getString(R.string.videos_pref);
            String videoPref = context.getString(R.string.video_pref);
            String num_photos = "";
            String num_videos = "";

            if (albumsAnswer.getPicCount() > 0) {

                num_photos = albumsAnswer.getPicCount() + " " + photosPref;
            }

            if (albumsAnswer.getVideosCount() > 0) {

                if(albumsAnswer.getVideosCount() == 1) {

                    num_videos = videoPref;
                }
                else {

                    num_videos = albumsAnswer.getVideosCount() + " " + videosPref;
                }
            }

            //bind titles to view
            ((ViewHolderAlbum) holder).binding.setPhotosTitle(num_photos);
            ((ViewHolderAlbum) holder).binding.setVideosTitle(num_videos);

            //like animation
            if(albumsAnswer.isLiked()) {

                ((ViewHolderAlbum) holder).switcher.setDisplayedChild(1);
            }
            else {

                ((ViewHolderAlbum) holder).switcher.setDisplayedChild(0);
            }

            ((ViewHolderAlbum) holder).switcher.setTag(albumsAnswer.getAlbumId());

            ((ViewHolderAlbum) holder).switcher.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    ViewSwitcher switcher = (ViewSwitcher) v;

                    if (switcher.getDisplayedChild() == R.id.heart_active) {

                        switcher.showNext();
                    } else {

                        switcher.showPrevious();
                    }

                    if(switcher.getDisplayedChild() == 0) {

                        albumsAnswer.setLikesCount(albumsAnswer.getLikesCount() - 1);
                        albumsAnswer.setLiked(false);
                    }
                    else {

                        MediaPlayer mp = MediaPlayer.create(MyApp.context, R.raw.like);
                        mp.start();

                        albumsAnswer.setLikesCount(albumsAnswer.getLikesCount() + 1);
                        albumsAnswer.setLiked(true);

                        ((ViewHolderAlbum) holder).heart.setVisibility(View.VISIBLE);


                        ((ViewHolderAlbum) holder).heart.setAlpha(0.f);
                        ((ViewHolderAlbum) holder).heart.setScaleX(0.f);
                        ((ViewHolderAlbum) holder).heart.setScaleY(0.f);
                        ((ViewHolderAlbum) holder).heart.animate()
                                .alpha(1.f)
                                .scaleX(1.f).scaleY(1.f)
                                .setDuration(500)
                                .setListener(new Animator.AnimatorListener() {
                                    @Override
                                    public void onAnimationStart(Animator animation) {

                                    }

                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        ((ViewHolderAlbum) holder).heart.animate()
                                                .alpha(0.f)
                                                .scaleX(0.f).scaleY(0.f)
                                                .setDuration(500)
                                                .setListener(new Animator.AnimatorListener() {
                                                    @Override
                                                    public void onAnimationEnd(Animator animation) {

                                                    }

                                                    @Override
                                                    public void onAnimationStart(Animator animation) {

                                                    }

                                                    @Override
                                                    public void onAnimationCancel(Animator animation) {

                                                    }

                                                    @Override
                                                    public void onAnimationRepeat(Animator animation) {

                                                    }
                                                })
                                                .start();
                                    }

                                    @Override
                                    public void onAnimationCancel(Animator animation) {

                                    }

                                    @Override
                                    public void onAnimationRepeat(Animator animation) {

                                    }
                                })
                                .start();
                    }

                    //send like to server
//                    JsonTransmitter.send_updatealbumlike((String) v.getTag());

                    Call<SuccessAnswer> albumLike = ganbookApiInterfacePOST.updateAlbumLike(albumsAnswer.getAlbumId());

                    albumLike.enqueue(new Callback<SuccessAnswer>() {
                        @Override
                        public void onResponse(Call<SuccessAnswer> call, Response<SuccessAnswer> response) {

                            SuccessAnswer successAnswer = response.body();

                            Log.d(TAG, "onResponse: success = " + successAnswer);
                        }

                        @Override
                        public void onFailure(Call<SuccessAnswer> call, Throwable t) {

                            Log.e(TAG, "onFailure: error while update album like = " + Log.getStackTraceString(t) );
                        }
                    });

                }
            });

            //open albums details
            ((ViewHolderAlbum) holder).binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    showItem(albumsAnswer, position - 1, gan_id, class_id);
                }
            });

            //open comments window
            ((ViewHolderAlbum) holder).comments.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent i = new Intent(MyApp.context, CommentsActivity.class);
                    i.putExtra("album_id", albumsAnswer.getAlbumId());
                    i.putExtra("pos", position - 1);
                    context.startActivity(i);
                }
            });

            // hide/show like/comment layout
            if(User.isTeacher()) {

                if (User.getCurrentLikeForbidden()) {
                    ((ViewHolderAlbum) holder).layout_likes.setVisibility(View.GONE);
                }
                else {
                    ((ViewHolderAlbum) holder).layout_likes.setVisibility(View.VISIBLE);
                }

                if (User.getCurrentCommentForbidden()) {
                    ((ViewHolderAlbum) holder).layout_comments.setVisibility(View.GONE);
                }
                else
                {
                    ((ViewHolderAlbum) holder).layout_comments.setVisibility(View.VISIBLE);
                }
            } else {

                if (User.current.getCurrentKidLikesForbidden()) {
                    ((ViewHolderAlbum) holder).layout_likes.setVisibility(View.GONE);
                }
                else {
                    ((ViewHolderAlbum) holder).layout_likes.setVisibility(View.VISIBLE);
                }

                if (User.current.getCurrentKidCommentsForbidden()) {
                    ((ViewHolderAlbum) holder).layout_comments.setVisibility(View.GONE);
                }
                else {
                    ((ViewHolderAlbum) holder).layout_comments.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    private void showItem(AlbumsAnswer albumsAnswer, final int i, String gan_id, String class_id) {

        AlbumDetailsFragment albumDetailsFragment = AlbumDetailsFragment.newInstance(class_id, gan_id,
                albumsAnswer, i);
        albumDetailsFragment.setCompletionListener(new AlbumDetailsFragment.CompletionAlbumListener() {
            @Override
            public void onComplete(AlbumsAnswer a) {

                //we need update ui about new album info
                if (items.size() > 0)
                    updateItem(i, a);
            }
        });

        FragmentUtils.openFragment(albumDetailsFragment, R.id.content_frame, "SINGLE", context, true);
    }

    @Override
    public int getItemCount() {
        return items.size() + 1;
    }
    public void addYears(ArrayList<AlbumsYearModel> albumsYearModels) {

        this.items.addAll(albumsYearModels);
        notifyDataSetChanged();
    }

    @Override
    public RecyclerViewItem getItem(int position) {
        return items.get(position - 1);
    }

    @Override
    public void addItem(RecyclerViewItem item) {

    }

    @Override
    public void addItem(RecyclerViewItem item, int index) {

    }

    @Override
    public void removeItem(RecyclerViewItem item) {

    }

    @Override
    public void addItems(List<RecyclerViewItem> items) {

    }

    @Override
    public void clearList() {

    }

    @Override
    public void updateItem(int pos, RecyclerViewItem item) {

        items.set(pos, item);
        notifyItemChanged(pos);
    }

    @Override
    public void showItem(RecyclerViewItem item, int itemPosition) {

    }

    @Override
    public List<RecyclerViewItem> getItems() {
        return null;
    }

    @Override
    public void removeItem(int currentItemPosition) {

    }

    public void addItemsAfter(int insertAfter, List<AlbumsAnswer> albumsAnswers) {

        items.addAll(insertAfter, albumsAnswers);
        notifyDataSetChanged();
    }

    public class HeaderItem extends RecyclerView.ViewHolder{

        View root;

        public HeaderItem(View itemView) {
            super(itemView);

            root = itemView;
        }
    }

    private class ViewHolderYear extends RecyclerView.ViewHolder {

        HeaderAlbumListBinding binding;

        public ViewHolderYear(HeaderAlbumListBinding binding) {
            super(binding.getRoot());

            this.binding = binding;
        }
    }

    private class ViewHolderAlbum extends RecyclerView.ViewHolder {

        ItemAlbumListBinding binding;
        ViewSwitcher switcher;
        ImageView heart, comments;
        LinearLayout layout_comments, layout_likes;

        public ViewHolderAlbum(ItemAlbumListBinding binding) {
            super(binding.getRoot());

            this.binding = binding;
            this.switcher = (ViewSwitcher) binding.getRoot().findViewById(R.id.switcher);
            this.heart = (ImageView) binding.getRoot().findViewById(R.id.heart_active_center);
            this.comments = (ImageView) binding.getRoot().findViewById(R.id.comments_image);
            this.layout_comments = (LinearLayout) binding.getRoot().findViewById(R.id.layout_comments);
            this.layout_likes = (LinearLayout) binding.getRoot().findViewById(R.id.layout_likes);
        }
    }
}

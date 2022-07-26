package com.ganbook.adapters;

import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.media.MediaPlayer;
import androidx.recyclerview.widget.RecyclerView;

import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.ganbook.activities.AlbumViewersActivity;
import com.ganbook.activities.CommentsActivity;
import com.ganbook.activities.DescriptionActivity;
import com.ganbook.app.MyApp;
import com.ganbook.fragments.AlbumDetailsFragment;
import com.ganbook.interfaces.AlbumsInterface;
import com.ganbook.interfaces.CRUDAdapterInterface;
import com.ganbook.interfaces.GanbookApiInterface;
import com.ganbook.interfaces.IGanbookApiCommercial;
import com.ganbook.models.AlbumsAnswer;
import com.ganbook.models.AlbumsYearModel;
import com.ganbook.models.CommercialClickCounter;
import com.ganbook.models.SuccessAnswer;
import com.ganbook.user.User;
import com.ganbook.utils.FragmentUtils;
import com.ganbook.utils.StrUtils;
import com.project.ganim.R;
import com.project.ganim.databinding.HeaderAlbumListBinding;
import com.project.ganim.databinding.ItemAlbumListBinding;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by dmytro_vodnik on 6/6/16.
 * working on ganbook1 project
 */
public class TabAlbumsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements CRUDAdapterInterface<AlbumsAnswer> {

    private static final String TAG = TabAlbumsAdapter.class.getName();
    private final AlbumsInterface albumsInterface;
    Context context;
    LayoutInflater layoutInflater;
    List<AlbumsAnswer> albumsAnswers;
    ArrayList<AlbumsYearModel> albumsYearModels;

    public static final int TYPE_HEADER = 0;
    public static final int TYPE_ITEM = 1;
    private AlbumsYearModel albumsYearModel;
    private int lastPosition = -1;

    @Inject
    @Named("POST")
    GanbookApiInterface ganbookApiInterfacePOST;

    @Inject
    @Named("COMMERCIAL")
    IGanbookApiCommercial ganbookApiCommercial;

    public TabAlbumsAdapter(Context context, AlbumsInterface albumsInterface) {

        this.context = context;
        this.albumsAnswers = new ArrayList<>();
        this.layoutInflater = LayoutInflater.from(this.context);
        ((MyApp) ((Activity) context).getApplication()).getGanbookApiComponent().inject(this);
        this.albumsYearModels = new ArrayList<>();
        this.albumsInterface = albumsInterface;
    }

    public AlbumsYearModel getCurrentYearItem() {

        return albumsYearModels.get(0);
    }

    public int getYearsCount() {
        return albumsYearModels.size();
    }

    public void clearYears() {

        albumsYearModels.clear();
        notifyDataSetChanged();
    }

    public class ViewHolderItem extends RecyclerView.ViewHolder {

        ItemAlbumListBinding binding;
        ViewSwitcher switcher;
        ImageView heart, comments, description, albumViewers, mainImage;
        LinearLayout layout_comments, layout_likes;
        RelativeLayout titleLayout, parentAlbumLayout;
        TextView numViewText;

        public ViewHolderItem(ItemAlbumListBinding binding) {
            super(binding.getRoot());

            this.binding = binding;
            this.switcher = (ViewSwitcher) binding.getRoot().findViewById(R.id.switcher);
            this.heart = (ImageView) binding.getRoot().findViewById(R.id.heart_active_center);
            this.comments = (ImageView) binding.getRoot().findViewById(R.id.comments_image);
            this.description = (ImageView) binding.getRoot().findViewById(R.id.description_image);
            this.layout_comments = (LinearLayout) binding.getRoot().findViewById(R.id.layout_comments);
            this.layout_likes = (LinearLayout) binding.getRoot().findViewById(R.id.layout_likes);
            this.albumViewers = binding.getRoot().findViewById(R.id.views_image);
            this.titleLayout = binding.getRoot().findViewById(R.id.title_layout);
            this.mainImage = binding.getRoot().findViewById(R.id.item_image);
            this.numViewText = binding.getRoot().findViewById(R.id.num_views_text);
            this.parentAlbumLayout = binding.getRoot().findViewById(R.id.parent_album_layout);
        }
    }

    private class ViewHolderHeader extends RecyclerView.ViewHolder {

        HeaderAlbumListBinding binding;

        public ViewHolderHeader(HeaderAlbumListBinding binding) {
            super(binding.getRoot());

            this.binding = binding;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d("VIEW HOLDER", "VIEW HOLDER TAB ALBUMS");

        if (viewType == TYPE_ITEM) {

            ItemAlbumListBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.item_album_list,
                    parent, false);

            return new ViewHolderItem(binding);
        }

        if (viewType == TYPE_HEADER) {

            HeaderAlbumListBinding binding =  DataBindingUtil.inflate(layoutInflater, R.layout.header_album_list,
                    parent, false);

            return new ViewHolderHeader(binding);
        }

        throw new RuntimeException("there is no type that matches the type " + viewType +
                " + make sure your using types correctly");
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

    //    if (User.current.kidIsWaitingApproval()==false) {


            /** processing {@link ViewHolderItem} item */
            if (holder instanceof ViewHolderItem) {

                final AlbumsAnswer[] albumsAnswer = {getItem(position)};

                Log.d(TAG, "onBindViewHolder: current album answer = " + albumsAnswer[0]);

                //bind album to view
                ((ViewHolderItem) holder).binding.setAlbum(albumsAnswer[0]);

                final String class_id = albumsYearModels.get(0).getClassId();
                final String gan_id = albumsYearModels.get(0).getGanId();

                //bind picture preview
                String picUrl = "";

                if (!albumsAnswer[0].getIsCommercial()) {
                    if (albumsAnswer[0].getPicPath() != null && albumsAnswer[0].getPicPath().equals("tmb")) {
                        picUrl = StrUtils.getAlbumFullSizeUrl(albumsAnswer[0].getFirstPicture(),
                                albumsAnswer[0].getAlbumId(), class_id, gan_id);
                    } else {
                        picUrl = StrUtils.getAlbumFullSizeUrl(albumsAnswer[0].getPicPath(),
                                albumsAnswer[0].getAlbumId(), class_id, gan_id);
                    }
                } else {
                    // picUrl = "http://dev.ganbook.co.il.s3.amazonaws.com/promo/" + albumsAnswer[0].getCommercial().getImageName();
                    picUrl = "http://s3.ganbook.co.il/promo/" + albumsAnswer[0].getCommercial().getImageName();
                }

                Log.d(TAG, "onBindViewHolder: picurl = " + picUrl);
                ((ViewHolderItem) holder).binding.setPreview(picUrl);

                //bind boolean to show/hide new ribbon
                ((ViewHolderItem) holder).binding.setShowNewLabel(User.isParent() && albumsAnswer[0].getUnseenPhotos() != 0);

                //process titles for video/photo count
                String photosPref = context.getString(R.string.photos_pref);
                String videosPref = context.getString(R.string.videos_pref);
                String videoPref = context.getString(R.string.video_pref);
                String num_photos = "";
                String num_videos = "";

                if (albumsAnswer[0].getPicCount() > 0) {

                    num_photos = albumsAnswer[0].getPicCount() + " " + photosPref;
                }

                if (albumsAnswer[0].getVideosCount() > 0) {

                    if (albumsAnswer[0].getVideosCount() == 1) {

                        num_videos = videoPref;
                    } else {

                        num_videos = albumsAnswer[0].getVideosCount() + " " + videosPref;
                    }
                }

                //bind titles to view
                ((ViewHolderItem) holder).binding.setPhotosTitle(num_photos);
                ((ViewHolderItem) holder).binding.setVideosTitle(num_videos);

                //like animation
                if (albumsAnswer[0].isLiked()) {

                    ((ViewHolderItem) holder).switcher.setDisplayedChild(1);
                } else {

                    ((ViewHolderItem) holder).switcher.setDisplayedChild(0);
                }

                ((ViewHolderItem) holder).switcher.setTag(albumsAnswer[0].getAlbumId());

                ((ViewHolderItem) holder).switcher.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        ViewSwitcher switcher = (ViewSwitcher) v;

                        if (switcher.getDisplayedChild() == R.id.heart_active) {

                            switcher.showNext();
                        } else {

                            switcher.showPrevious();
                        }

                        if (switcher.getDisplayedChild() == 0) {

                            albumsAnswer[0].setLikesCount(albumsAnswer[0].getLikesCount() - 1);
                            albumsAnswer[0].setLiked(false);
                        } else {

                            MediaPlayer mp = MediaPlayer.create(MyApp.context, R.raw.like);
                            mp.start();

                            albumsAnswer[0].setLikesCount(albumsAnswer[0].getLikesCount() + 1);
                            albumsAnswer[0].setLiked(true);

                            ((ViewHolderItem) holder).heart.setVisibility(View.VISIBLE);


                            ((ViewHolderItem) holder).heart.setAlpha(0.f);
                            ((ViewHolderItem) holder).heart.setScaleX(0.f);
                            ((ViewHolderItem) holder).heart.setScaleY(0.f);
                            ((ViewHolderItem) holder).heart.animate()
                                    .alpha(1.f)
                                    .scaleX(1.f).scaleY(1.f)
                                    .setDuration(500)
                                    .setListener(new Animator.AnimatorListener() {
                                        @Override
                                        public void onAnimationStart(Animator animation) {

                                        }

                                        @Override
                                        public void onAnimationEnd(Animator animation) {
                                            ((ViewHolderItem) holder).heart.animate()
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

                        Call<SuccessAnswer> albumLike = ganbookApiInterfacePOST.updateAlbumLike(albumsAnswer[0].getAlbumId());

                        albumLike.enqueue(new Callback<SuccessAnswer>() {
                            @Override
                            public void onResponse(Call<SuccessAnswer> call, Response<SuccessAnswer> response) {

                                SuccessAnswer successAnswer = response.body();

                                Log.d(TAG, "onResponse: success = " + successAnswer);
                            }

                            @Override
                            public void onFailure(Call<SuccessAnswer> call, Throwable t) {

                                Log.e(TAG, "onFailure: error while update album like = " + Log.getStackTraceString(t));
                            }
                        });

                    }
                });

                //open albums details
                ((ViewHolderItem) holder).binding.getRoot().setOnClickListener(v -> {
                    if (albumsAnswer[0].getIsCommercial()) {
                        boolean hasCommercialUrl = StringUtils.isNotBlank(albumsAnswer[0].getCommercial().getCommercialUrl());

                        if (hasCommercialUrl) {
                            ganbookApiCommercial.updateCommercialClicksCounter(albumsAnswer[0].getCommercial().getId())
                                    .enqueue(new Callback<CommercialClickCounter>() {
                                        @Override
                                        public void onResponse(Call<CommercialClickCounter> call, Response<CommercialClickCounter> response) {
                                            if (response.body() != null) {
                                                String url = albumsAnswer[0].getCommercial().getCommercialUrl();
                                                if (!url.startsWith("http://") && !url.startsWith("https://")) {
                                                    url = "http://" + url;
                                                }
                                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                                context.startActivity(browserIntent);
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<CommercialClickCounter> call, Throwable t) {
                                            Log.e("CLICK RESPONSE", t.getLocalizedMessage());
                                        }
                                    });
                        }
                        return;
                    }
                    showItem(albumsAnswer[0], position - 1, gan_id, class_id);
                });

                //open comments window
                ((ViewHolderItem) holder).comments.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent i = new Intent(MyApp.context, CommentsActivity.class);
                        i.putExtra("album_id", albumsAnswer[0].getAlbumId());
                        i.putExtra("pos", position);
                        context.startActivity(i);
                    }
                });

                ((ViewHolderItem) holder).description.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(MyApp.context, DescriptionActivity.class);
                        i.putExtra("album_id", albumsAnswer[0].getAlbumId());
                        i.putExtra("album_description", albumsAnswer[0].getAlbumDescription());
                        context.startActivity(i);
                    }
                });

                if (User.isTeacher()) {
                    ((ViewHolderItem) holder).albumViewers.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent i = new Intent(MyApp.context, AlbumViewersActivity.class);
                            i.putExtra("album_id", albumsAnswer[0].getAlbumId());
                            i.putExtra("album_name", albumsAnswer[0].getAlbumName());
                            context.startActivity(i);
                        }
                    });
                }

                if (albumsAnswer[0].getIsCommercial()) {
                    ((ViewHolderItem) holder).titleLayout.setVisibility(View.GONE);
                    ((ViewHolderItem) holder).albumViewers.setVisibility(View.GONE);
                    ((ViewHolderItem) holder).numViewText.setVisibility(View.GONE);
                } else {
                    ((ViewHolderItem) holder).titleLayout.setVisibility(View.VISIBLE);
                    ((ViewHolderItem) holder).albumViewers.setVisibility(View.VISIBLE);
                    ((ViewHolderItem) holder).numViewText.setVisibility(View.VISIBLE);
                }

            }

            /** processing {@link ViewHolderHeader} item */


            if (holder instanceof ViewHolderHeader && albumsYearModels.size() > 0) {

                int yearPos = 0;

                if (albumsAnswers.size() == 0)
                    yearPos = position;
                else
                    yearPos = getItemCount() - position;

                if (position == 0) {
                    albumsYearModel = albumsYearModels.get(0);

                    ((ViewHolderHeader) holder).binding.setDown(generateYearTitle(albumsYearModel));
                } else {
                    albumsYearModel = albumsYearModels.get(yearPos);

                    ((ViewHolderHeader) holder).binding.setDown(generateYearTitle(albumsYearModel));

                    final AlbumsYearModel finalAlbumsYearModel = albumsYearModel;
                    final int finalYearPos = yearPos;

                    ((ViewHolderHeader) holder).binding.getRoot().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (albumsYearModels.size() > 1 && position != 0) {
                                AlbumsYearModel temp = albumsYearModels.get(0);

                                albumsYearModels.set(finalYearPos, temp);
                                albumsYearModels.set(0, finalAlbumsYearModel);

                                notifyDataSetChanged();
                                albumsInterface.loadAlbums(finalAlbumsYearModel.getYear(), finalAlbumsYearModel.getClassId());
                            }
                        }
                    });

                }

                Log.w("tag","sharoni 2");

                Log.d(TAG, "onBindViewHolder: curr pos for header = " + position);

            }

            if (holder instanceof ViewHolderItem) {
                Calendar currentYear = new GregorianCalendar(Integer.valueOf(User.getCurrentYear()), Calendar.AUGUST, 20);
                Calendar previousYear = new GregorianCalendar(Integer.valueOf(User.getCurrentYear()) - 1, Calendar.AUGUST, 20);
                for (int i = 0; i < albumsAnswers.size(); i++) {
                    if (albumsAnswers.get(i).getAlbumDate() != null) {
                        if (dateToCalendar(albumsAnswers.get(i).getAlbumDate()).before(currentYear) && dateToCalendar(albumsAnswers.get(i).getAlbumDate()).before(previousYear)) {
                            ((ViewHolderItem) holder).description.setVisibility(View.GONE);
                            ((ViewHolderItem) holder).layout_comments.setVisibility(View.GONE);
                            ((ViewHolderItem) holder).layout_likes.setVisibility(View.GONE);
                        } else {

                            ((ViewHolderItem) holder).description.setVisibility(View.VISIBLE);
                            ((ViewHolderItem) holder).layout_comments.setVisibility(View.VISIBLE);
                            ((ViewHolderItem) holder).layout_likes.setVisibility(View.VISIBLE);

                            if (User.isTeacher()) {
                                if (User.getCurrentCommentForbidden()) {
                                    ((ViewHolderItem) holder).layout_comments.setVisibility(View.GONE);

                                } else {

                                    ((ViewHolderItem) holder).layout_comments.setVisibility(View.VISIBLE);

                                }
                                if (User.getCurrentLikeForbidden()) {
                                    ((ViewHolderItem) holder).layout_likes.setVisibility(View.GONE);

                                } else {
                                    ((ViewHolderItem) holder).layout_likes.setVisibility(View.VISIBLE);

                                }
                            } else {
                                if (User.current.getCurrentKidCommentsForbidden()) {
                                    ((ViewHolderItem) holder).layout_comments.setVisibility(View.GONE);

                                } else {
                                    ((ViewHolderItem) holder).layout_comments.setVisibility(View.VISIBLE);
                                }
                                if (User.current.getCurrentKidLikesForbidden()) {

                                    ((ViewHolderItem) holder).layout_likes.setVisibility(View.GONE);

                                } else {
                                    ((ViewHolderItem) holder).layout_likes.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    }
                }
            }


      //  }
    }

    private Calendar dateToCalendar(Date date) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;

    }

    private String generateYearTitle(AlbumsYearModel albumsYearModel) {

        return albumsYearModel.getYear() + "\n" + albumsYearModel.getGanName() + " - " + albumsYearModel.getClassName();

    }

    private void setYearTitle(HeaderAlbumListBinding binding, AlbumsYearModel albumsYearModel) {

        if (User.isParent()) {

        } else {

            binding.setDown(albumsYearModel.getYear() + "\n" + StrUtils.generateTitle());
        }
    }

    private void showItem(AlbumsAnswer albumsAnswer, final int i, String gan_id, String class_id) {

        AlbumDetailsFragment albumDetailsFragment = AlbumDetailsFragment.newInstance(class_id, gan_id,
                albumsAnswer, i);
        albumDetailsFragment.setCompletionListener(new AlbumDetailsFragment.CompletionAlbumListener() {
            @Override
            public void onComplete(AlbumsAnswer a) {

                //we need update ui about new album info
                if (albumsAnswers.size() > 0)
                    updateItem(i, a);
            }
        });

        FragmentUtils.openFragment(albumDetailsFragment, R.id.content_frame, "SINGLE", context, true);
    }

    @Override
    public int getItemCount() {
        return albumsAnswers.size() + (albumsYearModels.size());
    }

    @Override
    public int getItemViewType(int position) {
        
        if (isPositionHeader(position) || position > albumsAnswers.size())
            return TYPE_HEADER;

        return TYPE_ITEM;
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }

    @Override
    public AlbumsAnswer getItem(int position) {

        return albumsAnswers.get(position-1);
    }

    @Override
    public void addItem(AlbumsAnswer item) {
        albumsAnswers.add(0, item);
        notifyDataSetChanged();
    }

    @Override
    public void addItem(AlbumsAnswer item, int index) {
        albumsAnswers.add(index, item);
        notifyDataSetChanged();
    }
    @Override
    public void removeItem(AlbumsAnswer item) {

        albumsAnswers.remove(item);
        notifyDataSetChanged();
    }

    @Override
    public void removeItem(int pos) {

        albumsAnswers.remove(pos);
        notifyDataSetChanged();
    }

    @Override
    public void addItems(List<AlbumsAnswer> items) {
        Log.w("tag","sharoni 1");


        albumsAnswers.addAll(items);
        notifyDataSetChanged();
    }

    public void addYears(ArrayList<AlbumsYearModel> albumsYearModels) {

        this.albumsYearModels.addAll(albumsYearModels);
        notifyDataSetChanged();
    }

    @Override
    public void clearList() {
        albumsAnswers.clear();
        notifyDataSetChanged();
    }

    @Override
    public void updateItem(int pos, AlbumsAnswer item) {

        albumsAnswers.set(pos, item);
        notifyItemChanged(pos);
    }

    @Override
    public void showItem(AlbumsAnswer item, final int itemPosition) {

        String classId, ganId;

        if (User.isParent()) {

            classId = User.current.getCurrentKid().class_id;
            ganId = User.current.getCurrentKid().gan_id;
        } else {

            classId = User.current.getCurrentClassId();
            ganId = User.current.getCurrentGanId();
        }

        AlbumDetailsFragment albumDetailsFragment = AlbumDetailsFragment.newInstance(classId, ganId,
                item, itemPosition);
        albumDetailsFragment.setCompletionListener(new AlbumDetailsFragment.CompletionAlbumListener() {
            @Override
            public void onComplete(AlbumsAnswer a) {

                //we need update ui about new album info
                if (albumsAnswers.size() > 0)
                updateItem(itemPosition, a);
            }
        });

        FragmentUtils.openFragment(albumDetailsFragment, R.id.content_frame, "SINGLE", context, true);
    }

    @Override
    public List<AlbumsAnswer> getItems() {
        return albumsAnswers;
    }
}

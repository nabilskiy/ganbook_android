package com.ganbook.fragments;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.ViewSwitcher;

import com.ganbook.adapters.ZoomImageAdapter;
import com.ganbook.amazinglist.special.CustomToast;
import com.ganbook.app.MyApp;
import com.ganbook.asynctasks.DownloadTask;
import com.ganbook.communication.ICompletionDownloadHandler;
import com.ganbook.dialogs.PhotoDescriptionDialog;
import com.ganbook.handlers.PictureDetailsHandlers;
import com.ganbook.interfaces.GanbookApiInterface;
import com.ganbook.interfaces.PictureDetailsInterface;
import com.ganbook.models.OnPostS3Answer;
import com.ganbook.models.PictureAnswer;
import com.ganbook.models.SuccessAnswer;
import com.ganbook.models.events.FavoriteEvent;
import com.ganbook.services.UploadService;
import com.ganbook.share.ShareManager;
import com.ganbook.ui.zoomable.ExtendedViewPager;
import com.ganbook.user.User;
import com.ganbook.utils.Const;
import com.ganbook.utils.StrUtils;
import com.project.ganim.R;
import com.project.ganim.databinding.FragmentPictureDetailsBinding;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@RuntimePermissions
public class PictureDetailsFragment extends Fragment implements PictureDetailsInterface {

    private static final String ARG_CURRENT_POSITION = "curPos";
    private static final String ARG_PICTURES = "pictures";
    public static final String TAG = PictureDetailsFragment.class.getName();
    private static final String ARG_GAN_ID = "gan";
    private static final String ARG_CLASS_ID = "class";
    private FragmentPictureDetailsBinding binding;
    private int currentPosition;
    private ArrayList<PictureAnswer> pictureAnswers;

    @BindView(R.id.view_pager)
    ExtendedViewPager mViewPager;

    @BindView(R.id.zoom_footer)
    RelativeLayout zoomFooter;

    @BindView(R.id.zoom_header)
    RelativeLayout zoomHeader;

    @BindView(R.id.switcher)
    ViewSwitcher favoriteSwitcher;

    @BindView(R.id.save_right_btn)
    ImageButton save;

    @BindView(R.id.addPhotoCommentBtn)
    ImageButton addPhotoComment;

    @BindView(R.id.share_pict_btn)
    ImageButton share;

    ZoomImageAdapter adapter;

    String ganId;
    String classId;

    @Inject
    @Named("POST")
    GanbookApiInterface ganbookApiInterfacePOST;

    public PictureDetailsFragment() {
        // Required empty public constructor
    }

    public static PictureDetailsFragment newInstance(int pos, ArrayList<PictureAnswer> pictureAnswers, String ganId, String classId) {
        PictureDetailsFragment fragment = new PictureDetailsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_CURRENT_POSITION, pos);
        args.putParcelableArrayList(ARG_PICTURES, pictureAnswers);
        args.putString(ARG_GAN_ID, ganId);
        args.putString(ARG_CLASS_ID, classId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {

            currentPosition = getArguments().getInt(ARG_CURRENT_POSITION);
            pictureAnswers = getArguments().getParcelableArrayList(ARG_PICTURES);
            ganId = getArguments().getString(ARG_GAN_ID);
            classId = getArguments().getString(ARG_CLASS_ID);

            Log.d(TAG, "onCreate: Received in picture details pos =  " + currentPosition);
            Log.d(TAG, "onCreate: received picture answers = " + pictureAnswers);
        }

        ((MyApp) getActivity().getApplication()).getGanbookApiComponent().inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getActivity().setRequestedOrientation(
                ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);

        try {

            ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        } catch (Exception e) {

            Log.e(TAG, "onCreate: error = " + Log.getStackTraceString(e));
        }

        View rootView = inflater.inflate(R.layout.fragment_picture_details, container, false);

        ButterKnife.bind(this, rootView);
        binding = DataBindingUtil.bind(rootView);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mViewPager.setOffscreenPageLimit(0);

        adapter = new ZoomImageAdapter(getActivity(), pictureAnswers, this, ganId, classId);

        mViewPager.setAdapter(adapter);

        mViewPager.setCurrentItem(currentPosition);

        binding.setTitle((currentPosition + 1) + "/" + pictureAnswers.size());
        binding.setHandlers(new PictureDetailsHandlers(pictureAnswers.get(currentPosition), getContext(), ganId, classId));
        toggleFavorite(pictureAnswers.get(currentPosition));

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PictureDetailsFragmentPermissionsDispatcher.savePicWithCheck(PictureDetailsFragment.this, pictureAnswers.get(currentPosition), ganId, classId);
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PictureDetailsFragmentPermissionsDispatcher.sharePicWithCheck(PictureDetailsFragment.this, pictureAnswers.get(currentPosition), ganId, classId);
            }
        });

        addPhotoComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PhotoDescriptionDialog photoDescriptionDialog = new PhotoDescriptionDialog(getActivity(), pictureAnswers.get(currentPosition).getPictureDescription(), new PhotoDescriptionDialog.OnCloseListener() {
                    @Override
                    public void onClose(String text) {
                      Call<SuccessAnswer> addPhotoDescriptionCall =  ganbookApiInterfacePOST.addPhotoDescription(pictureAnswers.get(currentPosition).getPictureId(), pictureAnswers.get(currentPosition).getAlbumId(), text);

                      addPhotoDescriptionCall.enqueue(new Callback<SuccessAnswer>() {
                          @Override
                          public void onResponse(Call<SuccessAnswer> call, Response<SuccessAnswer> response) {
                              Log.d("RESPONSE", response.toString());
                          }

                          @Override
                          public void onFailure(Call<SuccessAnswer> call, Throwable t) {

                          }
                      });

                    }
                });

                photoDescriptionDialog.show();
            }
        });

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(final int position) {

                binding.setTitle((position + 1) + "/" + pictureAnswers.size());
                binding.setHandlers(new PictureDetailsHandlers(pictureAnswers.get(position), getContext(), ganId, classId));
                toggleFavorite(pictureAnswers.get(position));

                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        PictureDetailsFragmentPermissionsDispatcher.savePicWithCheck(PictureDetailsFragment.this, pictureAnswers.get(position), ganId, classId);
                    }
                });

                addPhotoComment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        PhotoDescriptionDialog photoDescriptionDialog = new PhotoDescriptionDialog(getActivity(), pictureAnswers.get(position).getPictureDescription(), new PhotoDescriptionDialog.OnCloseListener() {
                            @Override
                            public void onClose(String text) {
                                Call<SuccessAnswer> addPhotoDescriptionCall =  ganbookApiInterfacePOST.addPhotoDescription(pictureAnswers.get(position).getPictureId(), pictureAnswers.get(position).getAlbumId(), text);

                                addPhotoDescriptionCall.enqueue(new Callback<SuccessAnswer>() {
                                    @Override
                                    public void onResponse(Call<SuccessAnswer> call, Response<SuccessAnswer> response) {
                                        Log.d("RESPONSE", response.toString());
                                    }

                                    @Override
                                    public void onFailure(Call<SuccessAnswer> call, Throwable t) {

                                    }
                                });

                            }
                        });

                        photoDescriptionDialog.show();
                    }
                });

                share.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        PictureDetailsFragmentPermissionsDispatcher.sharePicWithCheck(PictureDetailsFragment.this, pictureAnswers.get(position), ganId, classId);
                    }
                });
            }
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });

        favoriteSwitcher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final int curInd = mViewPager.getCurrentItem();

                final PictureAnswer pictureAnswer = adapter.getPictureAnswers().get(curInd);

                if(favoriteSwitcher.getDisplayedChild() == 0) //inactive
                {
                    favoriteSwitcher.showNext();
                    pictureAnswer.setFavorite(true);
                    CustomToast.show(getActivity(),getActivity().getString(R.string.saved_to_favorites));
                }
                else
                {
                    favoriteSwitcher.showPrevious();
                    pictureAnswer.setFavorite(false);
                    CustomToast.show(getActivity(),getActivity().getString(R.string.removed_from_favorites));
                }

                adapter.updateItem(curInd, pictureAnswer);

                Call<SuccessAnswer> call = ganbookApiInterfacePOST.updatePicFavorite(pictureAnswer.getPictureId());

                call.enqueue(new Callback<SuccessAnswer>() {
                    @Override
                    public void onResponse(Call<SuccessAnswer> call, Response<SuccessAnswer> response) {

                        SuccessAnswer successAnswer = response.body();

                        Log.d(TAG, "onResponse: success = " + successAnswer);

                        if (successAnswer != null && successAnswer.isSuccess()) {

                            EventBus.getDefault().postSticky(new FavoriteEvent(curInd, pictureAnswer));
                        }
                    }

                    @Override
                    public void onFailure(Call<SuccessAnswer> call, Throwable t) {

                        Log.e(TAG, "onFailure: error while update favorite = " + Log.getStackTraceString(t));
                    }
                });
            }
        });
    }

    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    public void sharePic(PictureAnswer pictureAnswer, String ganId, String classId) {

        final String imgUrl = StrUtils.getAlbumFullSizeUrl(pictureAnswer.getPictureName(),
                pictureAnswer.getAlbumId(), classId, ganId);

        Log.d(TAG, "onShareClick: img url = " + imgUrl);

        if (imgUrl == null) {
            CustomToast.show(getActivity(), R.string.cannot_perform_op);
            return;
        }

        new DownloadTask(imgUrl, new ICompletionDownloadHandler() {
            @Override
            public void onComplete(File f) {

                if (f==null) {
                    CustomToast.show(getActivity(), R.string.cannot_perform_op);
                    return;
                }

                String subject = "";

                String body = "";

                if(imgUrl.contains("VID")) {

                    String a_text = getString(R.string.watch_video);
                    String url = imgUrl.substring(imgUrl.indexOf("/ImageStore/"));
                    String href = "http://d1i2jsi02r7fxj.cloudfront.net" + url;
                    body = getContext().getResources().getString(R.string.share_pict_body) + "<br><a href=\""+href+"\">"+a_text+"</a>";

                    subject = getString(R.string.share_vid_subject);
                }
                else
                    subject = getString(R.string.share_pict_subject);


                ShareManager.openShareMenu(getActivity(), subject + User.current.getCurrentGanName(),
                        R.string.share_pict_body, body, null, f);
            }
        }).execute();
    }

    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    public void savePic(PictureAnswer pictureAnswer, String ganId, String classId) {

        Log.d(TAG, "onSavePictureClick: sharoni 2");

        final String imgUrl = StrUtils.getAlbumFullSizeUrl(pictureAnswer.getPictureName(),
                pictureAnswer.getAlbumId(), classId, ganId);
        if (imgUrl == null) {
            CustomToast.show(getActivity(), R.string.cannot_perform_op);
            return;
        }

        final SweetAlertDialog progress = new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE);
        progress.setTitleText(getContext().getString(R.string.operation_proceeding));
        progress.setContentText(getContext().getString(R.string.processing_wait));
        progress.setCancelable(false);
        progress.show();

        new DownloadTask(imgUrl, new ICompletionDownloadHandler() {
            @Override
            public void onComplete(File f) {

                progress.hide();
                long f_len = (f==null ? 0 : f.length());
                boolean success = (f != null);

                if (success) {
                    CustomToast.show(getActivity(), R.string.operation_succeeded);
                } else {
                    CustomToast.show(getActivity(), R.string.cannot_perform_op);
                }
            }
        }).execute();
    }

    private void toggleFavorite(PictureAnswer pictureAnswer) {

        if(pictureAnswer.getFavorite()) {

            favoriteSwitcher.setDisplayedChild(1);
        }
        else {

            favoriteSwitcher.setDisplayedChild(0);
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    @Override
    public void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver((uploadingReceiver),
                new IntentFilter(Const.UPLOADING_INTENT));
    }

    @Override
    public void onStop() {

        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(uploadingReceiver);
        super.onStop();
    }

    @Override
    public void onDestroyView() {

        super.onDestroyView();
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    public void hideHeaderFooter() {

        if(zoomFooter.getVisibility() == View.GONE) {

            zoomFooter.setVisibility(View.VISIBLE);
            zoomHeader.setVisibility(View.VISIBLE);
        } else {
            zoomFooter.setVisibility(View.GONE);
            zoomHeader.setVisibility(View.GONE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        PictureDetailsFragmentPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    /**
     * receiver for {@link UploadService} class
     * here we need also support sequence of files which we need to upload on success/error uplaod
     * previous file
     */
    BroadcastReceiver uploadingReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Bundle extras = intent.getExtras();

            if(extras!=null) {

                OnPostS3Answer successAnswer = extras.getParcelable(Const.SUCCESS_STATUS);

                int pos = extras.getInt("pos");
                PictureAnswer pictureAnswer = extras.getParcelable(Const.PICTURE_PARCELABLE);

                Log.d(TAG, "onReceive: successful uploaded picture to adapter = " + pictureAnswer);

                if (successAnswer != null && pictureAnswer != null) {

                    Log.d(TAG, "onReceive: Received upload answer " + successAnswer);

                    if (successAnswer.isSuccess()) {

                        adapter.updateItem(pos, pictureAnswer);
                    } else {


                    }
                }
            }
        }
    };
}

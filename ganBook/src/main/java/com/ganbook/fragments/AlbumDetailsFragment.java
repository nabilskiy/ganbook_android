package com.ganbook.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import androidx.databinding.DataBindingUtil;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.StrictMode;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ViewSwitcher;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferType;
import com.ganbook.activities.GridGalleryActivity;
import com.ganbook.activities.MainActivity;
import com.ganbook.adapters.AlbumDetailsImagesAdapter;
import com.ganbook.amazinglist.special.CustomToast;
import com.ganbook.app.MyApp;
import com.ganbook.dao.PictureAnswerDAO;
import com.ganbook.dialogs.Dialogs;
import com.ganbook.dividers.GridSpacingItemDecoration;
import com.ganbook.handlers.AlbumDetailsHandlers;
import com.ganbook.interfaces.AlbumDetailsInterface;
import com.ganbook.interfaces.GanbookApiInterface;
import com.ganbook.interfaces.SelectMediaInterface;
import com.ganbook.interfaces.TitleIteractionListener;
import com.ganbook.interfaces.VideoProcessListener;
import com.ganbook.models.AlbumsAnswer;
import com.ganbook.models.MediaFile;
import com.ganbook.models.OnPostS3Answer;
import com.ganbook.models.PictureAnswer;
import com.ganbook.models.SuccessAnswer;
import com.ganbook.models.events.DeleteAlbumEvent;
import com.ganbook.models.events.DeletePicturesEvent;
import com.ganbook.models.events.FavoriteEvent;
import com.ganbook.models.events.MediaFileEvent;
import com.ganbook.models.events.RenameAlbumEvent;
import com.ganbook.models.events.UpdateAlbumViewEvent;
import com.ganbook.models.factories.PictureAnswerFactory;
import com.ganbook.s3transferutility.Util;
import com.ganbook.services.SupportUploadService;
import com.ganbook.services.UploadService;
import com.ganbook.universalloader.UILManager;
import com.ganbook.user.User;
import com.ganbook.utils.Const;
import com.ganbook.utils.DBUtils.HelperFactory;
import com.ganbook.utils.FfmpegUtils;
import com.ganbook.utils.FragmentUtils;
import com.ganbook.utils.ImageUtils;
import com.ganbook.utils.ServiceUtils;
import com.ganbook.utils.UrlUtils;
import com.luminous.pick.CustomGalleryActivity;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.project.ganim.R;
import com.project.ganim.databinding.FragmentAlbumDetailsBinding;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.ResponseBody;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.ganbook.activities.TrimActivity.PATH_ARG;
import static com.ganbook.utils.FfmpegUtils.getOutputMediaFilePath;

@RuntimePermissions
public class AlbumDetailsFragment extends Fragment implements AlbumDetailsInterface, SelectMediaInterface {

    public static final String TAG = AlbumDetailsFragment.class.getName();
    private static final String ARG_CLASS_ID = "classid";
    private static final String ARG_GAN_ID = "ganid";
    private static final String ARG_ALBUM = "album";
    private static final String ARG_POS = "pos";

    private AlbumsAnswer albumsAnswer;
    private TitleIteractionListener mListener;
    private String classId;
    private String ganId;
    private int pos;
    private FragmentAlbumDetailsBinding binding;
    private static Uri selectedMediaUri;
    private boolean picturesForUpload = false;
    public static int albumDetailsFragmentId;
    CompletionAlbumListener completionListener;
    private boolean finishedUpload = false;
    private EditText input;
    private String cameraPerm;


    @BindView(R.id.switcher)
    ViewSwitcher like;

    @BindView(R.id.images_gallery)
    RecyclerView imagesGallery;

    @BindView(R.id.try_again_btn)
    ImageView tryAgain;

    @BindView(R.id.set_thumb_button)
    ImageView addThumbBtn;

    @BindView(R.id.add_thumb)
    ImageView selectThumbnailBtn;

    AlbumDetailsImagesAdapter adapter;

    boolean isInSelectingMode = false;
    private int spanCount = 4;

    SweetAlertDialog progressDialog;

    @BindView(R.id.album_detail_progressbar)
    ProgressBar progressBar;

    @BindView(R.id.likes_layout)
    LinearLayout likesLayout;

    @BindView(R.id.comments_layout)
    LinearLayout commentsLayout;

    @BindView(R.id.user_action_button)
    ImageView userActionButton;

    private boolean deleted = false;

    @Inject
    @Named("POST")
    GanbookApiInterface ganbookApiInterfacePOST;

    @Inject
    @Named("GET")
    GanbookApiInterface ganbookApiInterfaceGET;
    private boolean progressStarted = false;
    private Calendar currentYear, previousYear;
    private ImageView imageView;
    private List<PictureAnswer> thumbPictureAnswer = new ArrayList<>();
    private String blockCharacterSet;

    public AlbumDetailsFragment() {
        // Required empty public constructor
    }

    public static AlbumDetailsFragment newInstance(String class_id, String gan_id, AlbumsAnswer album, int pos) {
        AlbumDetailsFragment fragment = new AlbumDetailsFragment();

        Bundle args = new Bundle();

        args.putParcelable(ARG_ALBUM, album);
        args.putString(ARG_CLASS_ID, class_id);
        args.putString(ARG_GAN_ID, gan_id);
        args.putInt(ARG_POS, pos);

        fragment.setArguments(args);
        return fragment;
    }

    public static AlbumDetailsFragment newInstance() {
        AlbumDetailsFragment albumDetailsFragment = new AlbumDetailsFragment();
        return albumDetailsFragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {

            albumsAnswer = getArguments().getParcelable(ARG_ALBUM);
            classId = getArguments().getString(ARG_CLASS_ID);
            ganId = getArguments().getString(ARG_GAN_ID);
            pos = getArguments().getInt(ARG_POS);

            Log.d(TAG, "onCreate: received album === " + albumsAnswer + "" +
                    "class id = " + classId + " gan id = " + ganId
                    + " pos = " + pos);
        }

        ((MyApp) getActivity().getApplication()).getGanbookApiComponent().inject(this);
        adapter = new AlbumDetailsImagesAdapter(getActivity(), this, ganId, classId);

        progressDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
        input = new EditText(getActivity());
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        blockCharacterSet = "/\\";

        final InputFilter filter  = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                if(source != null && blockCharacterSet.contains("" + source)) {
                    return "";
                }
                return null;
            }
        };
        input.setFilters(new InputFilter[]{filter});
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootView = inflater.inflate(R.layout.fragment_album_details, container, false);
        albumDetailsFragmentId = rootView.getId();
        setHasOptionsMenu(true);

        //binding
        ButterKnife.bind(this, rootView);
        binding = DataBindingUtil.bind(rootView);



        if(User.isTeacher()) {

            if (User.getCurrentLikeForbidden()) {
                likesLayout.setVisibility(View.INVISIBLE);
            }
            else {
                likesLayout.setVisibility(View.VISIBLE);
            }

            if (User.getCurrentCommentForbidden()) {
                commentsLayout.setVisibility(View.INVISIBLE);
            }
            else
            {
                commentsLayout.setVisibility(View.VISIBLE);
            }
        } else {

            if (User.current.getCurrentKidLikesForbidden()) {
                likesLayout.setVisibility(View.INVISIBLE);
            }
            else {
                likesLayout.setVisibility(View.VISIBLE);
            }

            if (User.current.getCurrentKidCommentsForbidden()) {
                commentsLayout.setVisibility(View.INVISIBLE);
            }
            else {
                commentsLayout.setVisibility(View.VISIBLE);
            }
        }

        currentYear = new GregorianCalendar(Integer.valueOf(User.getCurrentYear()), Calendar.AUGUST, 20);
        previousYear = new GregorianCalendar(Integer.valueOf(User.getCurrentYear()) - 1, Calendar.AUGUST, 20);

        imageView = getActivity().findViewById(R.id.item_image);

        addThumbBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final PictureAnswer answer = adapter.getSelectedItem();
                Log.d("ADAPTER ITEM", String.valueOf(adapter.getItemCount()));
                if(answer != null) {
                    Call<SuccessAnswer> uploadAlbumThumbnail = ganbookApiInterfacePOST
                            .uploadAlbumThumbnail(albumsAnswer.getAlbumId(), answer.getPictureName());

                    uploadAlbumThumbnail.enqueue(new Callback<SuccessAnswer>() {
                        @Override
                        public void onResponse(Call<SuccessAnswer> call, Response<SuccessAnswer> response) {
                            if (response.body() != null) {
                                getActivity().finish();
                                startActivity(new Intent(getActivity(), MainActivity.class));
                                CustomToast.show(getActivity(), getString(R.string.thumb_updated));
                            }
                        }

                        @Override
                        public void onFailure(Call<SuccessAnswer> call, Throwable t) {

                        }
                    });
                } else {
                    CustomToast.show(getActivity(), R.string.choose_picture_alert);
                }
            }
        });

        if(User.isTeacher()) {
            selectThumbnailBtn.setVisibility(View.VISIBLE);
            selectThumbnailBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(finishedUpload) {
                        selectThumbnail();
                    } else {
                        CustomToast.show(getActivity(), R.string.add_thumb_alert);
                    }
                }
            });

        } else {
            selectThumbnailBtn.setVisibility(View.GONE);
        }


        if (albumsAnswer.getAlbumDate() != null) {
            if (dateToCalendar(albumsAnswer.getAlbumDate()).before(currentYear) && dateToCalendar(albumsAnswer.getAlbumDate()).before(previousYear)) {
                selectThumbnailBtn.setVisibility(View.GONE);
                likesLayout.setVisibility(View.GONE);
                commentsLayout.setVisibility(View.GONE);
                addThumbBtn.setVisibility(View.GONE);
                userActionButton.setVisibility(View.GONE);
            } else {
                if (User.isStaff() || User.isParent()) {
                    addThumbBtn.setVisibility(View.GONE);
                    selectThumbnailBtn.setVisibility(View.GONE);
                } else {
                    selectThumbnailBtn.setVisibility(View.VISIBLE);
                }
                likesLayout.setVisibility(View.VISIBLE);
                commentsLayout.setVisibility(View.VISIBLE);
                userActionButton.setVisibility(View.VISIBLE);
            }
        }

        return rootView;
    }


    private Calendar dateToCalendar(Date date) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.setTime(date);
        return calendar;

    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.single_album_menu, menu);

        final MenuItem menuItem = menu.findItem(R.id.edit_menu);

        if (User.isTeacher()) {

            menuItem.getActionView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Log.d(TAG, "onClick: edit");

                    final boolean isEmpty = adapter.getItems() == null && adapter.getItems().size() == 0;
                    String[] items;
                    if (isEmpty) {
                        items = new String[2];
                        items[0] = getActivity().getResources().getString(R.string.rename_opt);
                        items[1] = getActivity().getResources().getString(R.string.delete_opt);
                    } else {
                        items = new String[3];
                        items[0] = getActivity().getResources().getString(R.string.select_opt);
                        items[1] = getActivity().getResources().getString(R.string.delete_opt);
                        items[2] = getActivity().getResources().getString(R.string.rename_opt);
                    }

                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("");
                    builder.setItems(items, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int index) {
                            if (isEmpty) {
                                //empty list
                                switch (index) {
                                    case 0:
                                        doRename();
                                        break;
                                    case 1:
                                        doDelete();
                                        break;
                                }
                            } else {
                                //list not empty
                                switch (index) {
                                    case 0:
                                        doSelect();
                                        break;
                                    case 1:
                                        doDelete();
                                        break;
                                    case 2:
                                        doRename();
                                        break;
                                }

                            }
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            });
        } else {

            menuItem.setVisible(false);
        }

        if(dateToCalendar(albumsAnswer.getAlbumDate()).before(currentYear) && dateToCalendar(albumsAnswer.getAlbumDate()).before(previousYear)) {
            menuItem.setVisible(false);
        }

        super.onCreateOptionsMenu(menu, inflater);
    }


   private void selectThumbnail() {

        adapter.setSelectionState();
        adapter.setThumbnailSelection(true);
        addThumbBtn.setVisibility(View.VISIBLE);
    }

    private void doSelect() {

        toggleSelection();

        Log.d(TAG, "doSelect: started");
    }

    private void toggleSelection() {
        adapter.setSelectionState();
        isInSelectingMode = !isInSelectingMode;
        binding.setShowDelete(isInSelectingMode);
    }

    private void doDelete() {

        AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
        adb.setTitle(R.string.delete_album_text);
        adb.setIcon(R.drawable.ic_launcher);
        adb.setPositiveButton(R.string.popup_btn_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                Call<SuccessAnswer> call = ganbookApiInterfacePOST.deleteAlbum(albumsAnswer.getAlbumId());

                call.enqueue(new Callback<SuccessAnswer>() {
                    @Override
                    public void onResponse(Call<SuccessAnswer> call, Response<SuccessAnswer> response) {

                        SuccessAnswer answer = response.body();

                        if(answer != null && answer.isSuccess())
                        {
                            EventBus.getDefault().postSticky(new DeleteAlbumEvent(pos));

                            //we need to delete picture from DB too
                            try {
                                PictureAnswerDAO pictureAnswerDAO = HelperFactory.getHelper()
                                        .getPictureAnswerDAO();

                                List<PictureAnswer> pendingItems = pictureAnswerDAO
                                        .getPicturesByAlbumId(albumsAnswer.getAlbumId());

                                pictureAnswerDAO.delete(pendingItems);

                                deleted = true;

                                FragmentUtils.popBackStack(getContext());
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<SuccessAnswer> call, Throwable t) {

                        CustomToast.show(getActivity(), R.string.error_delete_album);

                        Log.e(TAG, "onFailure: while edit album name === " + Log.getStackTraceString(t));
                    }
                });
            }
        });
        adb.setNegativeButton(R.string.popup_btn_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // no op
            }
        });

        adb.show();

        Log.d(TAG, "doDelete: started");
    }

    private void doRename() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.rename_opt);
        builder.setMessage(R.string.rename_album_txt);
        input.setInputType(InputType.TYPE_TEXT_FLAG_AUTO_CORRECT);
        input.setText(albumsAnswer.getAlbumName());
        builder.setView(input);



        builder.setPositiveButton(R.string.send_btn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                final String album_name = input.getText().toString().trim();

                Call<SuccessAnswer> call = ganbookApiInterfacePOST.editAlbum(albumsAnswer.getAlbumId(), album_name);

                call.enqueue(new Callback<SuccessAnswer>() {
                    @Override
                    public void onResponse(Call<SuccessAnswer> call, Response<SuccessAnswer> response) {

                        SuccessAnswer answer = response.body();

                        if(answer != null && answer.isSuccess()) {

                            albumsAnswer.setAlbumName(album_name);
                            mListener.setTitle(album_name);
                            EventBus.getDefault().postSticky(new RenameAlbumEvent(albumsAnswer,pos));
                        }
                    }

                    @Override
                    public void onFailure(Call<SuccessAnswer> call, Throwable t) {

                        CustomToast.show(getActivity(), R.string.error_rename_album);

                        Log.e(TAG, "onFailure: while edit album name === " + Log.getStackTraceString(t));
                    }
                });

                return;
            }
        });
        builder.setNegativeButton(R.string.cancel_btn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // no op
            }
        });

        builder.show();



        Log.d(TAG, "doRename: started");
    }

    private void getCamerraPermission() {
        Call<ResponseBody> cameraPermission = ganbookApiInterfaceGET.getStaffPermissions(String.valueOf(Integer.valueOf(User.getId()) - Integer.valueOf(User.USER_ID_KEY)), User.current.getCurrentClassId(), User.current.getCurrentGanId());

        cameraPermission.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.body() != null) {
                        JSONObject cameraPermissionObject = new JSONObject(response.body().string());
                        cameraPerm = cameraPermissionObject.getString("camera_permission");

                        if (cameraPerm.equals("1")) {
                            userActionButton.setImageResource(R.drawable.lower_panel_camara);
                        } else {
                            userActionButton.setImageResource(R.drawable.download);
                        }

                }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {


                Log.e(TAG, "onFailure: error while load parents = " + Log.getStackTraceString(t));
            }
        });
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(User.isStaff()) {
            getCamerraPermission();
        } else if(User.isParent()) {
            userActionButton.setImageResource(R.drawable.download);
        } else {
            userActionButton.setImageResource(R.drawable.lower_panel_camara);

        }
        userActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(User.isStaff()) {
                    if(cameraPerm.equals("1")) {
                        binding.getHandlers().onCameraClick(userActionButton);
                    } else {
                        binding.getHandlers().onDownloadImagesClick(userActionButton);
                    }
                } else if(User.isParent()) {
                    binding.getHandlers().onDownloadImagesClick(userActionButton);
                } else {
                    binding.getHandlers().onCameraClick(userActionButton);
                }
            }
        });
//        if (ServiceUtils.isMyServiceRunning(getActivity(), SupportUploadService.class))
//            getActivity().stopService(new Intent(getActivity(), SupportUploadService.class));

        binding.setAlbum(albumsAnswer);
        binding.setIsParent(User.isParent());
        binding.setIsStaff(User.isStaff());

        binding.setHandlers(new AlbumDetailsHandlers(getContext(), albumsAnswer, this, this));
        tryAgain.setVisibility(View.GONE);

        if(albumsAnswer.isLiked()) {

            like.setDisplayedChild(1);
        }
        else {

            like.setDisplayedChild(0);
        }

        imagesGallery.setAdapter(adapter);
        imagesGallery.setHasFixedSize(true);
        imagesGallery.setItemViewCacheSize(20);
        imagesGallery.setDrawingCacheEnabled(true);
        imagesGallery.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        imagesGallery.setLayoutManager(new GridLayoutManager(getActivity(), spanCount));
        imagesGallery.addItemDecoration(new GridSpacingItemDecoration(spanCount, 10, false));

        loadPictures();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, "onActivityResult: requestCode = " + requestCode + " result code = " + resultCode
                + " data = " + data);

        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {

                case Const.CAMERA_APP:
                    openHandleMediaTaken(true);
                    break;

                case Const.CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE:
                    openHandleMediaTaken(false);
                    break;

                case Const.GALLERY_APP:
                    openHandleFilesArray(data.<MediaFile>getParcelableArrayListExtra(Const.FILE_ARRAY));
                    break;
            }
        }
    }

    private void openHandleFilesArray(@NonNull ArrayList<MediaFile> mediaFiles) {

        Log.d(TAG, "openHandleFilesArray: received array = " + mediaFiles);

        ArrayList<PictureAnswer> pictureAnswers = new ArrayList<>();


        //moving selected pictures from gallery to adapter
        for (MediaFile mediaFile : mediaFiles) {

            PictureAnswer pictureToUpload = PictureAnswerFactory
                    .getPictureAnswer(albumsAnswer.getAlbumId(), "", mediaFile.getFilePath());

            pictureAnswers.add(pictureToUpload);

            adapter.addItem(pictureToUpload);
        }

        startUploadingToServer(pictureAnswers);
    }

    private void startUploadingToServer(ArrayList<PictureAnswer> picturesToUpload) {

        if (isNetworkAvailable()) {

            if (ServiceUtils.isMyServiceRunning(getActivity(), SupportUploadService.class)) {

                Intent intent = new Intent(Const.APPEND_UPLOAD_INTENT);

                intent.putParcelableArrayListExtra(Const.PICTURES_PARCELABLE_LIST, picturesToUpload);

                LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
            } else {

                Intent intent = new Intent(getActivity(), SupportUploadService.class);

                intent.putParcelableArrayListExtra(Const.PICTURES_PARCELABLE_LIST, picturesToUpload);
                intent.putExtra(Const.GAN_ID, ganId);
                intent.putExtra(Const.CLASS_ID, classId);

                getActivity().startService(intent);
            }
        } else
            Dialogs.errorDialogWithButton(getActivity(), "Error!", getString(R.string.internet_offline), "OK");

    }

    private void loadPictures() {

        Call<List<PictureAnswer>> pictures = ganbookApiInterfaceGET.getPictures(albumsAnswer.getAlbumId(), "1");

        pictures.enqueue(new Callback<List<PictureAnswer>>() {
            @Override
            public void onResponse(Call<List<PictureAnswer>> call, Response<List<PictureAnswer>> response) {

                List<PictureAnswer> pictureAnswers = response.body();


                if (pictureAnswers != null && pictureAnswers.size() != 0 && response.body() != null) {
                    thumbPictureAnswer = pictureAnswers;
                    finishedUpload = true;

                    Collections.reverse(pictureAnswers);

                    Log.d(TAG, "loaded pictures === " + pictureAnswers);

                    adapter.addItems(pictureAnswers);

                    if(albumsAnswer.getUnseenPhotos() > 0 && User.isParent()) {

                        Call<SuccessAnswer> successAnswerCall = ganbookApiInterfacePOST
                                .updateAlbumView(albumsAnswer.getAlbumId(), User.getCurrentYear(),
                                        pictureAnswers.size());

                        successAnswerCall.enqueue(new Callback<SuccessAnswer>() {
                            @Override
                            public void onResponse(Call<SuccessAnswer> call, Response<SuccessAnswer> response) {

                                Log.d(TAG, "onResponse: response mark seen = " + response.body());

                                SuccessAnswer successAnswer = response.body();

                                if (successAnswer != null && successAnswer.isSuccess()) {

                                    EventBus.getDefault().postSticky(new UpdateAlbumViewEvent(albumsAnswer.getUnseenPhotos()));

                                    albumsAnswer.setUnseenPhotos(0);
                                }

                            }

                            @Override
                            public void onFailure(Call<SuccessAnswer> call, Throwable t) {

                                Log.e(TAG, "onFailure: error while update album view " + Log.getStackTraceString(t));
                            }
                        });
                    }
                } else {

                    Log.e(TAG, "onResponse: " + "empty data");
                    progressDialog.hide();

                    CustomToast.show(getActivity(), R.string.empty_album);

                    progressBar.setVisibility(View.GONE);
                }

                if (User.isTeacher()) {
                    //looking for added pictures to album, bit not uploaded IF TEACHER!!!
                    try {
                        Log.d(TAG, "onResponse: get cached pics from DB");
                        List<PictureAnswer> notLoadedPictures = HelperFactory.getHelper()
                                .getPictureAnswerDAO()
                                .getPicturesByAlbumId(albumsAnswer.getAlbumId());

                        if (notLoadedPictures != null) {

                            for (PictureAnswer p : notLoadedPictures) {

                                if (ServiceUtils.isMyServiceRunning(getActivity(), SupportUploadService.class))
                                    p.setStatus(0);
                                else
                                    p.setStatus(2);
                            }

                            adapter.addItems(notLoadedPictures);

                            if (notLoadedPictures.size() != 0)
                                tryAgain.setVisibility(View.VISIBLE);
                        }
                    } catch (SQLException e) {

                        Log.e(TAG, "onResponse: error while load not uploaded pics = " + Log.getStackTraceString(e));
                    }
                }
            }

            @Override
            public void onFailure(Call<List<PictureAnswer>> call, Throwable t) {

                Log.e(TAG, "onFailure: while load pictures === " + Log.getStackTraceString(t));

                if (isAdded()) {

                    progressDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                    progressDialog.setContentText(getString(R.string.error_loading_pictures));
                    progressDialog.setConfirmText(getString(R.string.try_again));
                    progressDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            loadPictures();
                        }
                    });
                    progressDialog.setCancelText(getString(R.string.cancel));
                    progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            dialog.dismiss();
                        }
                    });
                }
            }
        });
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof TitleIteractionListener) {
            mListener = (TitleIteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement TitleIteractionListener");
        }

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver((uploadingReceiver),
                new IntentFilter(Const.UPLOADING_INTENT));
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver((inetReceiver),
                new IntentFilter(Const.NETWORK_INTENT));
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver((progressReceiver),
                new IntentFilter(Const.PIC_PROGRESS_INTENT));
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver((uploadDoneReceiver),
                new IntentFilter(Const.UPLOADING_DONE_INTENT));
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver((removePictureReceiver),
                new IntentFilter(Const.REMOVE_PICTURE));
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDetach() {

        super.onDetach();
        mListener = null;

        if (completionListener != null && !deleted)
            completionListener.onComplete(albumsAnswer);

        progressDialog.dismiss();

        Log.d(TAG, "onDetach: detached");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

//        if (ServiceUtils.isMyServiceRunning(getActivity(), SupportUploadService.class))
//            getActivity().stopService(new Intent(getActivity(), SupportUploadService.class));

        //before exit from fragment we need to mark all pictures as not uploaded with status 2

        EventBus.getDefault().unregister(this);
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(uploadingReceiver);
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(inetReceiver);
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(progressReceiver);
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(uploadDoneReceiver);
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(removePictureReceiver);

        progressDialog.dismiss();

        Log.d(TAG, "onDestroy: albums details destroyed");
    }


    private void stopUploading() {

        try {
            List<PictureAnswer> markToReloadPictures = HelperFactory.getHelper()
                    .getPictureAnswerDAO()
                    .getPicturesByAlbumId(albumsAnswer.getAlbumId());

            if (markToReloadPictures != null) {
                this.picturesForUpload = true;
                for (PictureAnswer p : markToReloadPictures) {

                    markPictureNotUploaded(p);

                    Log.d(TAG, "onDestroy: picture added to DB = " + p);
                }
            } else {
                this.picturesForUpload = false;
                Log.d("NO PICUTRES FOR UPLOAD", "NO PICTURES");
            }

        } catch (SQLException e) {
            Log.e(TAG, "error while mark failed " + Log.getStackTraceString(e));
        } catch (NullPointerException npe) {
            Log.e(TAG, "no data available " + Log.getStackTraceString(npe));
        }

        adapter.stopUpload();
        Util.getTransferUtility(getActivity()).cancelAllWithType(TransferType.ANY);
        if (ServiceUtils.isMyServiceRunning(getActivity(), SupportUploadService.class))
            getActivity().stopService(new Intent(getActivity(), SupportUploadService.class));
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onReceiveRenameAlbumEvent(DeletePicturesEvent deletePicturesEvent) {

        Log.d(TAG, "onReceiveRenameAlbumEvent: " + deletePicturesEvent);

        if (deletePicturesEvent.deleted) {
            adapter.removeSelected();
            toggleSelection();

            Log.d(TAG, "onReceiveRenameAlbumEvent: size = " + adapter.getPendingItems().size());

            if (adapter.getPendingItems().size() > 0)
                tryAgain.setVisibility(View.VISIBLE);
            else
                tryAgain.setVisibility(View.GONE);
        }

        //do not forget remove not needed events
        EventBus.getDefault().removeStickyEvent(deletePicturesEvent);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onReceiveVideoEvent(MediaFileEvent mediaFileEvent) {

        Log.d(TAG, "onReceiveVideoEvent: media file = " + mediaFileEvent);

        PictureAnswer videoPic = PictureAnswerFactory.getPictureAnswer(albumsAnswer.getAlbumId(),
                mediaFileEvent.time, mediaFileEvent.filePath, mediaFileEvent.id);

        Log.d(TAG, "onReceiveVideoEvent: video to upload = " + videoPic);

        adapter.addItem(videoPic);

//        uploadFileToServer(adapter.getItemPosition(videoPic), videoPic);

        ArrayList<PictureAnswer> pictureAnswers = new ArrayList<>();

        pictureAnswers.add(videoPic);

        startUploadingToServer(pictureAnswers);

        //do not forget remove not needed events
        EventBus.getDefault().removeStickyEvent(mediaFileEvent);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onReceiveFavoriteEvent(FavoriteEvent favoriteEvent) {

        Log.d(TAG, "onReceiveFavoriteEvent: " + favoriteEvent);

        adapter.updateItem(favoriteEvent.curPosition, favoriteEvent.fav);

        //do not forget remove not needed events
        EventBus.getDefault().removeStickyEvent(favoriteEvent);
    }

    private void markPictureNotUploaded(PictureAnswer p) {

        try {
            p.setStatus(2);
            HelperFactory.getHelper()
                    .getPictureAnswerDAO()
                    .update(p);

            Log.d(TAG, "onDestroy: updated " + p);
        } catch (SQLException e) {
            Log.e(TAG, "error while mark picture = " + Log.getStackTraceString(e));
        }
    }

    private void markPictureUploaded(PictureAnswer pictureAnswer) {

        //if picture successful uploaded - delete from DB
        deletePictureFromDB(pictureAnswer);
    }

    private void deletePictureFromDB(PictureAnswer pictureAnswer) {

        try {
            Log.d(TAG, "deleting picture " + pictureAnswer);

            HelperFactory.getHelper().getPictureAnswerDAO().delete(pictureAnswer);
        } catch (SQLException e) {
            Log.e(TAG, "error while delete picture " + Log.getStackTraceString(e));
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        mListener.setTitle(albumsAnswer.getAlbumName());

        if (videoProcess != null && !videoProcess.isShowing() && progressStarted)
            videoProcess.show();
    }


    BroadcastReceiver progressReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Bundle extras = intent.getExtras();

            if (extras!=null) {

                PictureAnswer pictureAnswer = extras.getParcelable(Const.PICTURE_PARCELABLE);
                int pos = adapter.getItemPosition(pictureAnswer);

                Log.d(TAG, "onReceive: pos = " + pos + " picture = " + pictureAnswer);
                if (pos != -1)
                    adapter.updateItem(pos, pictureAnswer);
            }
        }
    };

    BroadcastReceiver inetReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Bundle extras = intent.getExtras();

            if(extras!=null) {

                boolean inetAvailable = extras.getBoolean(Const.INTERNET_AVAIL);

                Log.d(TAG, "onReceive: inet available = " + inetAvailable);

                if (!inetAvailable) {
                    stopUploading();
                    tryAgain.setVisibility(View.VISIBLE);
                } else {
                    if(picturesForUpload) {
                        tryAgain.setVisibility(View.VISIBLE);
                    } else {
                        tryAgain.setVisibility(View.GONE);
                    }
                }
            }
        }
    };

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

                PictureAnswer pictureAnswer = extras.getParcelable(Const.PICTURE_PARCELABLE);
                int pos = adapter.getItemPosition(pictureAnswer);

                Log.d(TAG, "onReceive: successful uploaded picture to adapter = " + pictureAnswer);

                if (successAnswer != null && pictureAnswer != null && pos != -1) {

                    adapter.updateItem(pos, pictureAnswer);

                    Log.d(TAG, "onReceive: Received upload answer " + successAnswer);

                    if (successAnswer.isSuccess()) {
                        finishedUpload = true;
                        markPictureUploaded(pictureAnswer);

                    } else {
                        finishedUpload = false;
                        markPictureNotUploaded(pictureAnswer);
                    }

                    //we need to update album preview
                    if (albumsAnswer.getPicPath() == null
                            || albumsAnswer.getPicPath().equals("tmb")
                            || albumsAnswer.getPicPath().equals(""))
                        albumsAnswer.setPicPath(pictureAnswer.getPictureName().replace("jpeg", "jpg"));

                }
            }
        }
    };

    private BroadcastReceiver removePictureReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Bundle extras = intent.getExtras();

            if (extras!=null) {

                PictureAnswer pictureAnswer = extras.getParcelable(Const.PICTURE_PARCELABLE);

                deletePictureFromDB(pictureAnswer);

                adapter.removeItem(pictureAnswer);

                if (ServiceUtils.isMyServiceRunning(getActivity(), SupportUploadService.class))
                    getActivity().stopService(new Intent(getActivity(), SupportUploadService.class));
            }
        }
    };

    private BroadcastReceiver uploadDoneReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            tryAgain.setVisibility(View.GONE);

        }
    };

    public void setCompletionListener(CompletionAlbumListener completionListener) {
        this.completionListener = completionListener;
    }

    /**
     * Starting upload file to server with {@link UploadService} class
     * @param pos - position of item in adapter
     * @param pictureAnswer - picture which need to upload
     */
    @Override
    public void uploadFileToServer(int pos, PictureAnswer pictureAnswer) {

        pictureAnswer.setStatus(0);

        adapter.updateItem(pos, pictureAnswer);

        Intent intent = new Intent(getActivity(), UploadService.class);
        intent.putExtra(Const.PICTURE_PARCELABLE, pictureAnswer);
        intent.putExtra(Const.GAN_ID, ganId);
        intent.putExtra(Const.CLASS_ID, classId);
        getActivity().startService(intent);
    }

    @Override
    public void retryUploadFile(int pos, PictureAnswer file) {

        ArrayList<PictureAnswer> pictureAnswers = new ArrayList<>();

        pictureAnswers.add(file);

        startUploadingToServer(pictureAnswers);
    }

    @Override
    public void retryAll() {

        stopUploading();

        startUploadingToServer(new ArrayList<>(adapter.getPendingItems()));
    }

    @Override
    public void hideProgress() {

        if (progressBar.isShown())
            progressBar.setVisibility(View.GONE);
    }

    @Override
    public void downloadPictures(final List<String> pics) {

        SweetAlertDialog downloadDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE);

        downloadDialog.setTitleText(getString(R.string.album));
        downloadDialog.setContentText(getString(R.string.download_all_album));

        downloadDialog.setCancelText(getString(R.string.no));
        downloadDialog.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {

                sweetAlertDialog.dismiss();
            }
        });
        downloadDialog.setConfirmText(getString(R.string.yes));
        downloadDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                Log.d("DOWNLOAD ALBUM", "DOWNLOAD");
                sweetAlertDialog.dismiss();

                AlbumDetailsFragmentPermissionsDispatcher
                        .performDownloadPicturesWithCheck(AlbumDetailsFragment.this, pics);
            }
        });

        downloadDialog.show();
    }

    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    public void performDownloadPictures(List<String> pics) {

        DownloadManager dm = (DownloadManager) getContext().getSystemService(Context.DOWNLOAD_SERVICE);

        for (String url: pics) {
            DownloadManager.Request request = new DownloadManager.Request(
                    Uri.parse(url));

            String fileName = UrlUtils.urlToName(url);
            if (fileName==null) {
                fileName = System.currentTimeMillis() + ".jpeg";
            }

            String dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath()
                    + File.separator + Const.PICS_DIR + File.separator;

            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES, File.separator + Const.PICS_DIR + File.separator + fileName);
            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
            request.setVisibleInDownloadsUi(true);
            request.setTitle(getContext().getString(R.string.app_name));

            dm.enqueue(request);
        }

    }

    @Override
    public boolean getSelectionState() {
        return isInSelectingMode;
    }

    @Override
    public List<PictureAnswer> getPicturesToDelete() {

        return adapter.getSelectedItems();
    }

    @Override
    public List<String> getAllPicturesUrls() {

        List<String> urls = new ArrayList<String>();

        List<PictureAnswer> pictureAnswers = adapter.getItems();
        for (PictureAnswer pictureAnswer:pictureAnswers) {
            String url = ImageUtils.getFullSizeImage(ganId, classId, albumsAnswer.getAlbumId(), pictureAnswer.getPictureName());
            urls.add(url);
        }

        return urls;
    }

    @Override
    public void initMaxUploadCount() {

//        maxItems = uploadStack.size();
    }

    @Override
    public void openGalleryApp(int typeOfMedia) {

        Log.d(TAG, "openGalleryApp: with media type = " + typeOfMedia);

        AlbumDetailsFragmentPermissionsDispatcher.showGalleryWithCheck(this, typeOfMedia);
    }

    @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    void showGallery(int typeOfMedia) {

        Intent i = new Intent(getActivity(), CustomGalleryActivity.class);

        if (typeOfMedia == Const.OPEN_LIB_IND)
            i = new Intent(getContext(), GridGalleryActivity.class);

        i.putExtra("type", typeOfMedia);
        i.putExtra("albumName", albumsAnswer.getAlbumName());

        startActivityForResult(i, Const.GALLERY_APP);
    }

    @NeedsPermission({Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE})
    void showCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File _file = new File(getOutputMediaFilePath(Const.MEDIA_TYPE_IMAGE));

       if (_file==null) {
            CustomToast.show(getActivity(), R.string.operation_cannot_be_performed);
            return;
        }

        selectedMediaUri = Uri.fromFile(_file);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, selectedMediaUri);

        startActivityForResult(intent, Const.CAMERA_APP);
    }

    @Override
    public void openCameraApp() {

        AlbumDetailsFragmentPermissionsDispatcher.showCameraWithCheck(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // NOTE: delegate the permission handling to generated method
        AlbumDetailsFragmentPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);

    }

    @NeedsPermission({Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE})
    void showVideoCamera() {

        //create new Intent
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

        selectedMediaUri = FfmpegUtils.getOutputMediaFileUri(Const.MEDIA_TYPE_VIDEO);  // create a file to save the video
        intent.putExtra(MediaStore.EXTRA_OUTPUT, selectedMediaUri);  // set the image file name

        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1); // set the video image quality to high

        //intent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, 30 * 1048576L);//X mb *1024*1024

        // start the Video Capture Intent
        startActivityForResult(intent, Const.CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE);
    }

    @Override
    public void openVideoApp() {

        AlbumDetailsFragmentPermissionsDispatcher.showVideoCameraWithCheck(this);
    }

    SweetAlertDialog videoProcess;
    private void openHandleMediaTaken(boolean isPicture) {

        Log.d(TAG, "openHandleMediaTaken: is picture = " + isPicture);

        boolean ok = false;

        if (selectedMediaUri != null && selectedMediaUri.getPath() != null) {

            File taken_pict = new File(selectedMediaUri.getPath());

            if (taken_pict.exists() && taken_pict.length() > 100) {

                if(!isPicture) {

                    videoProcess = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
                    videoProcess.setContentText(getString(R.string.prepare_video_upload));
                    videoProcess.setTitleText(getString(R.string.processing_wait));
                    videoProcess.setCancelable(false);
                    videoProcess.show();
                    processVideo();
                    return;
                }
                else {

                    handlePictureTaken(taken_pict, isPicture);
                    ok = true;
                }
            }
        }
        if (!ok) {
            CustomToast.show(getActivity(), R.string.operation_cannot_be_performed);
        }
    }


    private void processVideo() {

        progressStarted = true;

        new Thread(new Runnable() {
            @Override
            public void run() {


               final File file = FfmpegUtils.getCompressedVideo(getActivity().getApplicationContext(), new VideoProcessListener() {
                    @Override
                    public void onSuccess(File file) {
                        postToUI(file);
                    }

                    @Override
                    public void onError(String error) {

                        Log.e(TAG, "onError: error while process video = " + error);
                    }
                }, selectedMediaUri.getPath(), "-1", "-1");

                Log.d(TAG, "onPrepared: video process end with file = " + file);
            }

            private void postToUI(File file) {

                Message message = handler.obtainMessage();

                Bundle args = new Bundle();

                args.putString(PATH_ARG, file.getAbsolutePath());

                message.setData(args);

                handler.handleMessage(message);
            }

            // Define the Handler that receives messages from the thread and update the progress
            private final Handler handler = new Handler(Looper.getMainLooper()) {

                public void handleMessage(Message msg) {

                    progressStarted = false;
                    videoProcess.dismiss();

                    try {
                        final File file = new File(msg.getData().getString(PATH_ARG));

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                handlePictureTaken(file, false);
                            }
                        });
                    } catch (Exception e) {

                        Log.e(TAG, "handleMessage: error while handle pic = " + Log.getStackTraceString(e));
                    }
                }
            };

        }).start();
    }

    private void handlePictureTaken(final File photo, final boolean isPicture) {

        String alertTitle;

        if(isPicture) {
            alertTitle = getString(R.string.upload_photo_text);
        }
        else {
            alertTitle = getString(R.string.upload_video_text);
        }

        Dialogs.successDialogWithButton(getActivity(), getString(R.string.image_upload_alert_title), alertTitle, getString(R.string.ok), getString(R.string.cancel), new Dialogs.ButtonDialogInterface() {
            @Override
            public void onButtonOkClicked() {
                PictureAnswer newPicture = null;

                if (isPicture)
                    newPicture = PictureAnswerFactory.getPictureAnswer(albumsAnswer.getAlbumId(), "",
                            photo.getAbsolutePath());
                else
                    newPicture = PictureAnswerFactory.getPictureAnswer(albumsAnswer.getAlbumId(),
                            getVideoLen(Uri.fromFile(photo)), photo.getAbsolutePath());

                adapter.addItem(newPicture);

                ArrayList<PictureAnswer> pictureAnswers = new ArrayList<PictureAnswer>();

                pictureAnswers.add(newPicture);

                startUploadingToServer(pictureAnswers);
            }
        });
    }

    @SuppressLint("DefaultLocale")
    private String getVideoLen(Uri uri) {

        MediaPlayer mp = MediaPlayer.create(getActivity(), uri);
        int duration = mp.getDuration();
        mp.release();
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(duration),
                TimeUnit.MILLISECONDS.toSeconds(duration) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration))
        );
    }

    public interface CompletionAlbumListener {

        void onComplete(AlbumsAnswer albumsAnswer);
    }
}
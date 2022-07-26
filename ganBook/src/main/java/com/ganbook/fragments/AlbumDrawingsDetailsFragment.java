package com.ganbook.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.ganbook.activities.DrawingInformationAdding;
import com.ganbook.adapters.AlbumDetailsDrawingsAdapter;
import com.ganbook.app.MyApp;
import com.ganbook.dialogs.Dialogs;
import com.ganbook.dividers.GridSpacingItemDecoration;
import com.ganbook.interfaces.AlbumDetailsInterface;
import com.ganbook.interfaces.GanbookApiInterface;
import com.ganbook.interfaces.SelectMediaInterface;
import com.ganbook.interfaces.TitleIteractionListener;
import com.ganbook.models.DrawingAnswer;
import com.ganbook.models.OKAnswer;
import com.ganbook.models.PictureAnswer;
import com.ganbook.models.SuccessAnswer;
import com.ganbook.models.events.DeletePicturesEvent;
import com.ganbook.models.events.UploadDrawingEvent;
import com.ganbook.user.User;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;
import com.project.ganim.R;
import com.project.ganim.databinding.FragmentAlbumDrawingsDetailsBinding;

import org.apache.commons.io.FilenameUtils;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AlbumDrawingsDetailsFragment extends Fragment implements SelectMediaInterface {

    @BindView(R.id.user_action_button)
    ImageView userActionButton;

    @BindView(R.id.drawing_gallery)
    RecyclerView drawingGallery;

    @BindView(R.id.album_detail_progressbar)
    ProgressBar progressBar;

    @BindView(R.id.delete_btn)
    ImageView deleteBtn;


    @Inject
    @Named("GET")
    GanbookApiInterface ganbookApiInterfaceGET;

    @Inject
    @Named("POST")
    GanbookApiInterface ganbookApiInterfacePOST;

    private AlbumDetailsDrawingsAdapter adapter;
    private List<DrawingAnswer> drawingAnswers;
    private String drawingAlbumId, drawingAlbumName;
    private TitleIteractionListener mListener;
    private boolean isInSelectingMode = false;
    private FragmentAlbumDrawingsDetailsBinding binding;
    private SweetAlertDialog progress;

    public static AlbumDrawingsDetailsFragment newInstance(String drawingAlbumId, String drawingAlbumName) {

        AlbumDrawingsDetailsFragment drawingsDetailsFragment = new AlbumDrawingsDetailsFragment();
        Bundle args = new Bundle();

        args.putString("drawingAlbumId", drawingAlbumId);
        args.putString("drawingAlbumName", drawingAlbumName);
        drawingsDetailsFragment.setArguments(args);
        return drawingsDetailsFragment;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_album_drawings_details, container, false);
        setHasOptionsMenu(true);
        ButterKnife.bind(this, rootView);

        binding = DataBindingUtil.bind(rootView);

        deleteBtn.setOnClickListener(v -> {
            onDeleteImagesClick(v);
        });

        return rootView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            drawingAlbumId = getArguments().getString("drawingAlbumId");
            drawingAlbumName = getArguments().getString("drawingAlbumName");
            User.current.setCurrentDrawingAlbumId(drawingAlbumId);
        }
        ((MyApp) getActivity().getApplication()).getGanbookApiComponent().inject(this);
        adapter = new AlbumDetailsDrawingsAdapter(getActivity());

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
            userActionButton.setImageResource(R.drawable.lower_panel_camara);
            progressBar.setVisibility(View.GONE);
            userActionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), DrawingInformationAdding.class);

                    startActivity(intent);
                }
            });
        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        mListener.setTitle(drawingAlbumName);

        binding.setIsParent(User.isParent());
        binding.setIsStaff(User.isStaff());

        drawingGallery.setAdapter(adapter);

        drawingGallery.setHasFixedSize(true);
        drawingGallery.setItemViewCacheSize(20);
        drawingGallery.setDrawingCacheEnabled(true);
        drawingGallery.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        drawingGallery.setLayoutManager(new GridLayoutManager(getActivity(), 4));
        drawingGallery.addItemDecoration(new GridSpacingItemDecoration(4, 10, false));
        drawingGallery.setItemAnimator(new DefaultItemAnimator());

        loadDrawings();
    }

    private void loadDrawings() {
        Call<List<DrawingAnswer>> pictures = ganbookApiInterfaceGET.getDrawings(drawingAlbumId);

        pictures.enqueue(new Callback<List<DrawingAnswer>>() {
            @Override
            public void onResponse(Call<List<DrawingAnswer>> call, Response<List<DrawingAnswer>> response) {

                drawingAnswers = response.body();

                if (drawingAnswers != null && drawingAnswers.size() != 0 && response.body() != null) {
                    Collections.reverse(drawingAnswers);

                    adapter.addItems(drawingAnswers);
                }
            }

            @Override
            public void onFailure(Call<List<DrawingAnswer>> call, Throwable t) {

            }
        });

    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onReceiveDrawingEvent(UploadDrawingEvent uploadDrawingEvent) {

        DrawingAnswer drawingAnswer = uploadDrawingEvent.drawingAnswer;
        if(!FilenameUtils.getExtension(drawingAnswer.getLocaFilePath()).equals("wav")) {
            adapter.clearList();
            this.loadDrawings();
        }

        EventBus.getDefault().removeStickyEvent(uploadDrawingEvent);
        Dialogs.sneakerSuccess(getActivity(), getString(R.string.drawing_album_succeed), getString(R.string.drawing_upload_succeed));
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
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
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.single_album_menu, menu);

        final MenuItem menuItem = menu.findItem(R.id.edit_menu);

        menuItem.getActionView().setOnClickListener(v -> {

            String[] items;

            items = new String[1];
            items[0] = getActivity().getResources().getString(R.string.select_opt);



            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("");
            builder.setItems(items, (dialog, index) -> {

                switch (index) {
                    case 0:
                        doSelect();
                        break;
                }

            });
            AlertDialog alert = builder.create();
            alert.show();
        });

        super.onCreateOptionsMenu(menu, inflater);
    }

    public void onDeleteImagesClick(View view) {

        final List<DrawingAnswer> toDeleteList = getSelectedDrawings();

        Log.d("TAG", "onDeleteImagesClick: deleting " + toDeleteList);

        progress = new SweetAlertDialog(view.getContext(), SweetAlertDialog.NORMAL_TYPE);
        progress.setCancelable(false);
        progress.setTitleText(view.getContext().getString(R.string.are_you_sure_delete));
        progress.setContentText(view.getContext().getString(R.string.deleting_pictures));
        progress.setConfirmClickListener(sweetAlertDialog -> deletePictures(toDeleteList));
        progress.setCancelText(getActivity().getString(R.string.cancel));
        progress.setCancelClickListener(sweetAlertDialog -> progress.dismiss());
        progress.show();
    }

    private String appendDrawingIds(List<DrawingAnswer> toDeleteList) {

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < toDeleteList.size(); i++) {

            sb.append(toDeleteList.get(i).getDrawingId());
            if (i <  toDeleteList.size() - 1) {
                sb.append(",");
            }
        }

        Log.d("STRING: ", sb.toString());

        return sb.toString();
    }

    private void deletePictures(final List<DrawingAnswer> toDeleteList) {

        progress.changeAlertType(SweetAlertDialog.PROGRESS_TYPE);

        Call<SuccessAnswer> deleteDrawings = ganbookApiInterfacePOST.deleteDrawings(appendDrawingIds(toDeleteList));
        deleteDrawings.enqueue(new Callback<SuccessAnswer>() {
            @Override
            public void onResponse(Call<SuccessAnswer> call, Response<SuccessAnswer> response) {
                if (response.body() != null) {
                    if (response.isSuccessful()) {
                        adapter.removeSelected();
                        toggleSelection();

                        progress.dismiss();
                    } else {
                        progress.dismiss();
                    }
                }
            }

            @Override
            public void onFailure(Call<SuccessAnswer> call, Throwable t) {

                progress.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                progress.setCancelable(true);
                progress.setTitleText(getActivity().getString(R.string.connection_error));
                progress.setConfirmText(getActivity().getString(R.string.try_again));
                progress.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {

                        deletePictures(toDeleteList);
                    }
                });
                progress.setCancelText(getActivity().getString(R.string.cancel));
                progress.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        progress.dismiss();
                    }
                });
            }
        });

    }

    private void toggleSelection() {
        adapter.setSelectionState();
        isInSelectingMode = !isInSelectingMode;
        binding.setShowDelete(isInSelectingMode);
    }

    private void doSelect() {

        adapter.setSelectionState();
        isInSelectingMode = !isInSelectingMode;
        binding.setShowDelete(isInSelectingMode);

    }

   private List<DrawingAnswer> getSelectedDrawings() {
        return adapter.getSelectedItems();
   }

   private DrawingAnswer getSelectedItem() {
        return adapter.getSelectedItem();
   }

    @Override
    public void openGalleryApp(int typeOfMedia) {

    }

    @Override
    public void openCameraApp() {

    }

    @Override
    public void openVideoApp() {

    }
}

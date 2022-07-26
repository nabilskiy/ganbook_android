package com.ganbook.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ganbook.adapters.ZoomDrawingAdapter;
import com.ganbook.amazinglist.special.CustomToast;
import com.ganbook.app.MyApp;
import com.ganbook.asynctasks.DownloadTask;
import com.ganbook.communication.ICompletionDownloadHandler;
import com.ganbook.models.DrawingAnswer;
import com.ganbook.ui.zoomable.ExtendedViewPager;
import com.ganbook.user.User;
import com.ganbook.utils.StrUtils;
import com.project.ganim.R;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class DrawingDetailsFragment extends Fragment {

    public static final String TAG = DrawingDetailsFragment.class.getName();
    private int currentPosition;
    private ArrayList<DrawingAnswer> drawingAnswers;
    private MediaPlayer mediaPlayer;

    public static DrawingDetailsFragment newInstance(int pos, ArrayList<DrawingAnswer> drawingAnswers) {
        DrawingDetailsFragment fragment = new DrawingDetailsFragment();
        Bundle args = new Bundle();
        args.putInt("drawingCurrentPosition", pos);
        args.putParcelableArrayList("drawings", drawingAnswers);
        fragment.setArguments(args);
        return fragment;
    }

    @BindView(R.id.drawing_view_pager)
    ExtendedViewPager drawingViewPager;

    @BindView(R.id.drawingDesicrpitonText)
    TextView drawingDescriptionText;

    @BindView(R.id.drawing_details_title)
    TextView drawingDetailsTitle;

    @BindView(R.id.save_right_btn)
    TextView saveDrawing;

    @BindView(R.id.drawingBackButton)
    ImageView drawingBackButotn;

    @BindView(R.id.playBtn)
    Button playBtn;

    @BindView(R.id.stopBtn)
    Button stopBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {

            currentPosition = getArguments().getInt("drawingCurrentPosition");
            drawingAnswers = getArguments().getParcelableArrayList("drawings");

        }

        ((MyApp) getActivity().getApplication()).getGanbookApiComponent().inject(this);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setRequestedOrientation(
                ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);

        try {
            ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        } catch (Exception e) {
            Log.e(TAG, "onCreate: error = " + Log.getStackTraceString(e));
        }

        View rootView = inflater.inflate(R.layout.fragment_drawing_details, container, false);


        ButterKnife.bind(this, rootView);
        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


            }
        });


        return rootView;
    }



    @SuppressLint("SetTextI18n")
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        drawingViewPager.setOffscreenPageLimit(0);

        ZoomDrawingAdapter adapter = new ZoomDrawingAdapter(getActivity(), drawingAnswers);

        drawingViewPager.setAdapter(adapter);

        drawingViewPager.setCurrentItem(currentPosition);
        drawingDetailsTitle.setText((currentPosition + 1) + "/" + drawingAnswers.size());
        drawingDescriptionText.setText(drawingAnswers.get(currentPosition).getDrawingDescription());

        saveDrawing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DrawingDetailsFragmentPermissionsDispatcher.saveDrawingWithCheck(DrawingDetailsFragment.this, drawingAnswers.get(currentPosition), User.current.getCurrentKidId(), User.current.getCurrentClassId(), User.current.getCurrentGanId());
            }
        });

        drawingBackButotn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(drawingAnswers.get(currentPosition).getDrawingAudio() != null) {
                    mediaPlayer = MediaPlayer.create(getActivity(), Uri.parse("http://s3.ganbook.co.il/Drawings/" + User.current.getCurrentGanId() + "/" + User.current.getCurrentClassId() + "/" + User.current.getCurrentKidId() + "/" + drawingAnswers.get(currentPosition).getDrawingAudio()));
                    mediaPlayer.setVolume(1.0f, 1.0f);
                    mediaPlayer.start();
                } else {
                    Toast.makeText(getActivity(), getString(R.string.drawing_audio_empty), Toast.LENGTH_LONG).show();
                }
            }
        });

        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(drawingAnswers.get(currentPosition).getDrawingAudio() != null) {
                    mediaPlayer.stop();
                } else {
                    Toast.makeText(getActivity(), getString(R.string.drawing_audio_empty), Toast.LENGTH_LONG).show();
                }

            }
        });




        drawingViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onPageSelected(final int position) {

                drawingDetailsTitle.setText((position + 1) + "/" + drawingAnswers.size());
                drawingDescriptionText.setText(drawingAnswers.get(position).getDrawingDescription());

                saveDrawing.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DrawingDetailsFragmentPermissionsDispatcher.saveDrawingWithCheck(DrawingDetailsFragment.this, drawingAnswers.get(position), User.current.getCurrentKidId(), User.current.getCurrentClassId(), User.current.getCurrentGanId());
                    }
                });

                playBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(drawingAnswers.get(position).getDrawingAudio() != null) {
                            mediaPlayer = MediaPlayer.create(getActivity(), Uri.parse("http://s3.ganbook.co.il/Drawings/" + User.current.getCurrentGanId() + "/" + User.current.getCurrentClassId() + "/" + User.current.getCurrentKidId() + "/" + drawingAnswers.get(position).getDrawingAudio()));
                            mediaPlayer.setVolume(1.0f, 1.0f);
                            mediaPlayer.start();
                        } else {
                            Toast.makeText(getActivity(), "No audio for this drawing!", Toast.LENGTH_LONG).show();
                        }
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

    }

    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    public void saveDrawing(DrawingAnswer drawingAnswer, String kidId, String classId, String ganId) {

        String imgUrl = StrUtils.getDrawingAlbumFullSizeUrl(drawingAnswer.getDrawingName(), kidId, classId, ganId);

        if (imgUrl == null) {
            CustomToast.show(getActivity(), R.string.cannot_perform_op);
            return;
        }

        final SweetAlertDialog progress = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
        progress.setTitleText(getActivity().getString(R.string.operation_proceeding));
        progress.setContentText(getActivity().getString(R.string.processing_wait));
        progress.setCancelable(false);
        progress.show();

        new DownloadTask(imgUrl, new ICompletionDownloadHandler() {
            @Override
            public void onComplete(File f) {
                boolean success = (f != null);
                if (success) {
                    progress.dismiss();
                    CustomToast.show(getActivity(), R.string.operation_succeeded);
                } else {
                    CustomToast.show(getActivity(), R.string.cannot_perform_op);
                }
            }
        }).execute();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        DrawingDetailsFragmentPermissionsDispatcher.onRequestPermissionsResult(DrawingDetailsFragment.this, requestCode, grantResults);
    }
}

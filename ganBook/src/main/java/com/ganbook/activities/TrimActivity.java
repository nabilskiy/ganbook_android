package com.ganbook.activities;

/**
 * Created by Noa on 27/01/2016.
 */

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.ganbook.commonvideolibrary.VideoSliceSeekBar;
import com.ganbook.interfaces.VideoProcessListener;
import com.ganbook.models.events.MediaFileEvent;
import com.ganbook.utils.AlertUtils;
import com.ganbook.utils.Const;
import com.ganbook.utils.FfmpegUtils;
import com.project.ganim.R;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

//import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
//import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
//import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
//import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
//import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;

public class TrimActivity extends BaseAppCompatActivity {

    private static final String TAG = TrimActivity.class.getSimpleName();

    private static final int MAX = 60000 * 2; // 2 min
    private static final int MIN = 2000; // 2 sec
    public static final String TIME_ARG = "time";
    public static final String PATH_ARG = "path";

    TextView textViewLeft, textViewRight;
    VideoSliceSeekBar videoSliceSeekBar;
    VideoView videoView;
    View videoControlBtn;
    View videoSabeBtn,cancelButton;
    ProgressDialog progress;

//    FFmpeg ffmpeg;

    Uri uri;
    String id;

    private ProgressDialog progressDialog;

    MediaMetadataRetriever retriever = new MediaMetadataRetriever();
    private boolean progressStarted = false;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trim);
        textViewLeft = (TextView) findViewById(R.id.left_pointer);
        textViewRight = (TextView) findViewById(R.id.right_pointer);

        videoSliceSeekBar = (VideoSliceSeekBar) findViewById(R.id.seek_bar);
        videoView = (VideoView) findViewById(R.id.video);
        videoControlBtn = findViewById(R.id.video_control_btn);
        videoSabeBtn = findViewById(R.id.saveButton);
        cancelButton = findViewById(R.id.cancelButton);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(null);

        uri = getIntent().getData();
        id = getIntent().getStringExtra(Const.VIDEO_ID);

        initVideoView();
//        loadFFMpegBinary();
    }



    private void initVideoView() {
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(final MediaPlayer mp) {
                videoSliceSeekBar.setSeekBarChangeListener(new VideoSliceSeekBar.SeekBarChangeListener() {
                    @Override
                    public void SeekBarValueChanged(int leftThumb, int rightThumb) {
                        textViewLeft.setText(getTimeForTrackFormat(leftThumb, true));
                        textViewRight.setText(getTimeForTrackFormat(rightThumb, true));

                        videoView.seekTo(leftThumb);
                    }
                });

                videoSliceSeekBar.setMaxValue(mp.getDuration());
                videoSliceSeekBar.setLeftProgress(0);
                //videoSliceSeekBar.setRightProgress(mp.getDuration());
                videoSliceSeekBar.setRightProgress(MAX); //10 segundos como m�ximo de entrada
                videoSliceSeekBar.setProgressMinDiff((MIN * 100)/mp.getDuration()); //Diferencia m�nima de 5 segundos
                videoSliceSeekBar.setProgressMaxDiff((MAX * 100)/mp.getDuration());//Diferencia m�xima de 10 segundos

                videoControlBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        performVideoViewClick();
                    }
                });

                videoSabeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG, "Left progress : " + videoSliceSeekBar.getLeftProgress()/1000);
                        Log.d(TAG, "Right progress : " + videoSliceSeekBar.getRightProgress()/1000);

                        Log.d(TAG, "Total Duration : " + mp.getDuration()/1000);

                        AlertDialog.Builder adb = new AlertDialog.Builder(TrimActivity.this);
                        adb.setTitle(R.string.upload_video_text);
                        adb.setIcon(R.drawable.ic_launcher);
                        adb.setPositiveButton(R.string.popup_btn_ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                try {

                                    processVideo(getTimeString(videoSliceSeekBar.getLeftProgress() / 1000), getTimeString(videoSliceSeekBar.getRightProgress() / 1000));
                                } catch (Exception e) {

                                    Log.e(TAG, "onClick: error while process video = " + Log.getStackTraceString(e) );
                                }
                            }
                        });

                        adb.setNegativeButton(R.string.popup_btn_cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // no op
                            } });

                        adb.show();
                    }

                });

                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TrimActivity.this.finish();
                    }
                });

                videoView.start();
                videoView.pause();

            }
        });

        videoView.setVideoURI(uri);
    }

    private void processVideo(final String timeFrom, final String timeTo) {

        progress = AlertUtils.buildGanbookProgressDialod(this, getString(R.string.prepare_video_upload));
        progressStarted = true;

        new Thread(new Runnable() {
            @Override
            public void run() {

                try {

                    String orig_file_path = uri.getPath();

                    try {

                        FfmpegUtils.getCompressedVideo(getApplicationContext(), new VideoProcessListener() {
                            @Override
                            public void onSuccess(final File file) {

                                try {

                                    ArrayList<String> filePathArr = new ArrayList<>();
                                    filePathArr.add(file.getPath());

                                    MediaPlayer mp = new MediaPlayer();
                                    mp.setDataSource(TrimActivity.this, Uri.fromFile(file));
                                    mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                        @Override
                                        public void onPrepared(MediaPlayer mp) {

                                            int duration = mp.getDuration();
                                            mp.release();

                                            String time = String.format("%02d:%02d",
                                                    TimeUnit.MILLISECONDS.toMinutes(duration),
                                                    TimeUnit.MILLISECONDS.toSeconds(duration) -
                                                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration))
                                            );

                                            Log.d(TAG, "onPrepared: video process end with time = " + time);

                                            postToUI(time, file.getAbsolutePath());
                                        }
                                    });

                                    mp.prepareAsync();
                                } catch (IOException e) {

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(TrimActivity.this, R.string.check_video, Toast.LENGTH_LONG).show();
                                        }
                                    });

                                    Log.e(TAG, "onPostExecute: error while get video length = " + Log.getStackTraceString(e));
                                }
                            }

                            @Override
                            public void onError(String error) {

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(TrimActivity.this, R.string.check_video, Toast.LENGTH_LONG).show();
                                    }
                                });

                                Log.e(TAG, "onPostExecute: error while get video length = " + error);
                            }
                        }, orig_file_path, timeFrom, timeTo);

                        Log.i("test", "ffmpeg4android finished successfully");
                    } catch (Throwable e) {
                        Log.e("test", "vk run exception.", e);
                    }

                } catch (NullPointerException npe) {

//                    MailSender.writeStringAsFile(TrimActivity.this, Log.getStackTraceString(npe), "videoprocess.txt", "VideoProcess");

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(TrimActivity.this, R.string.check_video, Toast.LENGTH_LONG).show();
                        }
                    });

                    Log.e(TAG, "process video error = " + Log.getStackTraceString(npe));
                } catch (Exception e) {

//                    MailSender.writeStringAsFile(TrimActivity.this, Log.getStackTraceString(e), "videoprocess.txt", "VideoProcess");
                }
            }

            private void postToUI(String time, String absolutePath) {

                Message message = handler.obtainMessage();

                Bundle args = new Bundle();

                args.putString(TIME_ARG, time);
                args.putString(PATH_ARG, absolutePath);

                message.setData(args);

                handler.handleMessage(message);
            }

            // Define the Handler that receives messages from the thread and update the progress
            private final Handler handler = new Handler() {

                public void handleMessage(Message msg) {

                    progressStarted = false;
                    progress.dismiss();

                    TrimActivity.this.finish();

                    EventBus.getDefault().postSticky(new MediaFileEvent(msg.getData().getString(PATH_ARG),
                            msg.getData().getString(TIME_ARG), id));
                }
            };

        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (progressStarted && progress != null && !progress.isShowing())
            progress.show();
    }

    private String getTimeString(int totalSecs) {
        int hours = totalSecs / 3600;
        int  minutes = (totalSecs % 3600) / 60;
        int seconds = totalSecs % 60;

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

//    private void loadFFMpegBinary() {
//        try {
//            if(ffmpeg == null) {
//                Log.d(TAG, "ffmpeg : era nulo");
//                ffmpeg = FFmpeg.getInstance(this);
//            }
//            ffmpeg.loadBinary(new LoadBinaryResponseHandler() {
//                @Override
//                public void onFailure() {
//                    showUnsupportedExceptionDialog();
//                }
//                @Override
//                public void onSuccess() {
//                    Log.d(TAG, "ffmpeg : correct Loaded");
//                }
//            });
//        } catch (FFmpegNotSupportedException e) {
//            showUnsupportedExceptionDialog();
//        } catch (Exception e) {
//            Log.d(TAG, "EXception no controlada : "  + e);
//        }
//    }

//    private void execFFmpegBinary(final String command) {
//        try {
//            ffmpeg.execute(command, new ExecuteBinaryResponseHandler() {
//                @Override
//                public void onFailure(String s) {
//                    Log.d(TAG, "FAILED with output : "+s);
//                }
//
//                @Override
//                public void onSuccess(String s) {
//                    Log.d(TAG, "SUCCESS with output : "+s);
//                }
//
//                @Override
//                public void onProgress(String s) {
//                    Log.d(TAG, "Started command : ffmpeg "+command);
//                    Log.d(TAG, "progress : " + s);
//                }
//
//                @Override
//                public void onStart() {
//                    Log.d(TAG, "Started command : ffmpeg " + command);
//                    progressDialog.setMessage("Processing...");
//                    progressDialog.show();
//                }
//
//                @Override
//                public void onFinish() {
//                    Log.d(TAG, "Finished command : ffmpeg " + command);
//                    progressDialog.dismiss();
//                }
//            });
//        } catch (FFmpegCommandAlreadyRunningException e) {
//            // do nothing for now
//        }
//    }

    private void performVideoViewClick() {
        if (videoView.isPlaying()) {
            videoView.pause();
            videoSliceSeekBar.setSliceBlocked(false);
            videoSliceSeekBar.removeVideoStatusThumb();
            videoControlBtn.setVisibility(View.VISIBLE);
        } else {
            videoControlBtn.setVisibility(View.GONE);
            videoView.seekTo(videoSliceSeekBar.getLeftProgress());
            videoView.start();
            videoSliceSeekBar.setSliceBlocked(true);
            videoSliceSeekBar.videoPlayingProgress(videoSliceSeekBar.getLeftProgress());
            videoStateObserver.startVideoProgressObserving();
        }
    }

    public static String getTimeForTrackFormat(int timeInMills, boolean display2DigitsInMinsSection) {
        int minutes = (timeInMills / (60 * 1000));
        int seconds = (timeInMills - minutes * 60 * 1000) / 1000;
        String result = display2DigitsInMinsSection && minutes < 10 ? "0" : "";
        result += minutes + ":";
        if (seconds < 10) {
            result += "0" + seconds;
        } else {
            result += seconds;
        }
        return result;
    }

    private void showUnsupportedExceptionDialog() {
        new AlertDialog.Builder(TrimActivity.this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("device_not_supported")
                .setMessage("device_not_supported_message")
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        TrimActivity.this.finish();
                    }
                })
                .create()
                .show();

    }


    private StateObserver videoStateObserver = new StateObserver();

    private class StateObserver extends Handler {

        private boolean alreadyStarted = false;

        private void startVideoProgressObserving() {
            if (!alreadyStarted) {
                alreadyStarted = true;
                sendEmptyMessage(0);
            }
        }

        private Runnable observerWork = new Runnable() {
            @Override
            public void run() {
                startVideoProgressObserving();
            }
        };

        @Override
        public void handleMessage(Message msg) {
            alreadyStarted = false;
            videoSliceSeekBar.videoPlayingProgress(videoView.getCurrentPosition());
            if (videoView.isPlaying() && videoView.getCurrentPosition() < videoSliceSeekBar.getRightProgress()) {
                postDelayed(observerWork, 50);
            } else {

                if (videoView.isPlaying()) videoView.pause();
                videoControlBtn.setVisibility(View.VISIBLE);
                videoSliceSeekBar.setSliceBlocked(false);
                videoSliceSeekBar.removeVideoStatusThumb();
            }
        }
    }

    private void storeFile(InputStream input, File file) {
        try {
            final OutputStream output = new FileOutputStream(file);
            try {
                try {
                    final byte[] buffer = new byte[1024];
                    int read;

                    while ((read = input.read(buffer)) != -1)
                        output.write(buffer, 0, read);

                    output.flush();
                } finally {
                    output.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                input.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
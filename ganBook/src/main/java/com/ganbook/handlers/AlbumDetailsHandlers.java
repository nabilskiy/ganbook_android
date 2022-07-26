package com.ganbook.handlers;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.View;
import android.widget.ViewSwitcher;

import com.ganbook.activities.CommentsActivity;
import com.ganbook.app.MyApp;
import com.ganbook.communication.json.transmitter.JsonTransmitter;
import com.ganbook.interfaces.AlbumDetailsInterface;
import com.ganbook.interfaces.GanbookApiInterface;
import com.ganbook.interfaces.SelectMediaInterface;
import com.ganbook.models.AlbumsAnswer;
import com.ganbook.models.OKAnswer;
import com.ganbook.models.PictureAnswer;
import com.ganbook.models.events.DeletePicturesEvent;
import com.ganbook.utils.Const;
import com.project.ganim.R;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by dmytro_vodnik on 6/10/16.
 * working on ganbook1 project
 */
public class AlbumDetailsHandlers {

    private static final String TAG = AlbumDetailsHandlers.class.getName();
    private AlbumsAnswer albumsAnswer;
    SelectMediaInterface selectMediaInterface;
    AlbumDetailsInterface albumDetailsInterface;
    SweetAlertDialog progress;
    Context context;

    @Inject
    @Named("POST")
    GanbookApiInterface ganbookApiInterfacePOST;

    public AlbumDetailsHandlers(Context context, AlbumsAnswer albumsAnswer,
                                SelectMediaInterface selectMediaInterface,
                                AlbumDetailsInterface albumDetailsInterface) {
        this.albumsAnswer = albumsAnswer;
        this.selectMediaInterface = selectMediaInterface;
        this.albumDetailsInterface = albumDetailsInterface;
        this.context = context;

        ((MyApp) ((Activity) context).getApplication()).getGanbookApiComponent().inject(this);
    }

    public void openCommentsClick(View view) {

        Log.d(TAG, "openCommentsClick: ");

        Intent i = new Intent(MyApp.context, CommentsActivity.class);
        i.putExtra("album_id", albumsAnswer.getAlbumId());
        view.getContext().startActivity(i);
    }

    public void likeSwitcherClick(View view) {

        Log.d(TAG, "likeSwitcherClick: ");

        ViewSwitcher switcher = (ViewSwitcher) view;

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
        }

        //send like to server
        JsonTransmitter.send_updatealbumlike(albumsAnswer.getAlbumId());
    }

    public void onCameraClick(View view) {

        Log.d(TAG, "onCameraClick: ");

        final Context context = view.getContext();

//        if (ServiceUtils.isMyServiceRunning(context, SupportUploadService.class)) {
//
//           SweetAlertDialog alertDialog =  AlertUtils.showAlertMessage(context,
//                   context.getString(R.string.batch_uploading),
//                   context.getString(R.string.album));
//
//            if (!alertDialog.isShowing())
//                alertDialog.show();
//
//        } else {

            final String[] items = new String[5];
            items[Const.OPEN_LIB_IND] = context.getResources().getString(R.string.existing_library);
            items[Const.OPEN_CAM_IND] = context.getResources().getString(R.string.take_new_pict);
            items[Const.OPEN_VIDEO_GALLERY_IND] = context.getResources().getString(R.string.existing_video_library);
            items[Const.OPEN_VIDEO_IND] = context.getResources().getString(R.string.take_new_vid);
            items[Const.CLOSE_MENU_IND] = context.getResources().getString(R.string.cancel);


            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("");
            builder.setItems(items, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int index) {
                    switch (index) {
                        case Const.OPEN_LIB_IND:
                            selectMediaInterface.openGalleryApp(Const.OPEN_LIB_IND);
                            break;
                        case Const.OPEN_CAM_IND:
                            selectMediaInterface.openCameraApp();
                            break;
                        case Const.OPEN_VIDEO_GALLERY_IND:
                            selectMediaInterface.openGalleryApp(Const.OPEN_VIDEO_GALLERY_IND);
                            break;
                        case Const.OPEN_VIDEO_IND:
                            selectMediaInterface.openVideoApp();
                            break;
                        case Const.CLOSE_MENU_IND:
                            break;
                        default:
                            // ??
                    }
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
//        }
    }

    public void onDownloadImagesClick(View view) {

        Log.d(TAG, "onDownloadImagesClick: ");

        List<String> urlsToDownload = albumDetailsInterface.getAllPicturesUrls();

        albumDetailsInterface.downloadPictures(urlsToDownload);

//        DownloadManager dm = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
//
//        for (String url:urlsToDownload) {
//            DownloadManager.Request request = new DownloadManager.Request(
//                    Uri.parse(url));
//
//            String fileName = UrlUtils.urlToName(url);
//            if (fileName==null) {
//                fileName = System.currentTimeMillis() + ".jpeg";
//            }
//
//            String dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString()
//                    + File.separator + "GanBook Images" + File.separator;
//
//            request.setDestinationInExternalPublicDir( dir, fileName);
//            request.allowScanningByMediaScanner();
//            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
//            request.setVisibleInDownloadsUi(true);
//            request.setTitle(context.getString(R.string.app_name));
//
//            long enqueue = dm.enqueue(request);
//        }
    }

    public void onDeleteImagesClick(View view) {

        final List<PictureAnswer> toDeleteList = albumDetailsInterface.getPicturesToDelete();

        Log.d(TAG, "onDeleteImagesClick: deleting " + toDeleteList);

        progress = new SweetAlertDialog(view.getContext(), SweetAlertDialog.NORMAL_TYPE);
        progress.setCancelable(false);
        progress.setTitleText(view.getContext().getString(R.string.processing_wait));
        progress.setContentText(view.getContext().getString(R.string.deleting_pictures));
        progress.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {

                deletePictures(toDeleteList);
            }
        });
        progress.setCancelText(context.getString(R.string.cancel));
        progress.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {

                progress.dismiss();
            }
        });
        progress.show();
    }

    public void onTryAgainClick(View view) {

        Log.d(TAG, "onTryAgainClick: ");

        albumDetailsInterface.retryAll();
    }

    private void deletePictures(final List<PictureAnswer> toDeleteList) {

        progress.changeAlertType(SweetAlertDialog.PROGRESS_TYPE);

        Call<OKAnswer> deletePics = ganbookApiInterfacePOST.deletePictures(appendPictureIds(toDeleteList));
        deletePics.enqueue(new Callback<OKAnswer>() {
            @Override
            public void onResponse(Call<OKAnswer> call, Response<OKAnswer> response) {

                OKAnswer okAnswer = response.body();

                Log.d(TAG, "onResponse: received answer = " + okAnswer);

                if (okAnswer != null && !okAnswer.getOk().contains("Error")) {

                    EventBus.getDefault().postSticky(new DeletePicturesEvent(true));

                    progress.dismiss();
                }
            }

            @Override
            public void onFailure(Call<OKAnswer> call, Throwable t) {

                Log.e(TAG, "onFailure: error while delete pictures" + Log.getStackTraceString(t));

                progress.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                progress.setCancelable(true);
                progress.setTitleText(context.getString(R.string.connection_error));
                progress.setConfirmText(context.getString(R.string.try_again));
                progress.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {

                        deletePictures(toDeleteList);
                    }
                });
                progress.setCancelText(context.getString(R.string.cancel));
                progress.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        progress.dismiss();
                    }
                });
            }
        });
    }

    private String appendPictureIds(List<PictureAnswer> toDeleteList) {

        StringBuilder sb = new StringBuilder();

        for (PictureAnswer p : toDeleteList) {

            sb.append(p.getPictureName());
            sb.append(",");
        }

        Log.d(TAG, "appendPictureIds: pictures ids = " + sb.toString());

        return sb.toString();
    }
}

package com.ganbook.activities;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import androidx.core.app.ActivityCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.ganbook.amazinglist.special.CustomToast;
import com.ganbook.app.MyApp;
import com.ganbook.communication.ICompletionHandler;
import com.ganbook.communication.datamodel.ResultObj;
import com.ganbook.communication.json.BaseResponse;
import com.ganbook.communication.json.getfavorite_Response;
import com.ganbook.communication.json.getpicture_Response;
import com.ganbook.communication.json.transmitter.JsonTransmitter;
import com.ganbook.communication.upload.state.UploadStatus;
import com.ganbook.datamodel.SingleImageObject;
import com.ganbook.fragments.FragmentType;
import com.ganbook.ui.FavoritesAdapter;
import com.ganbook.user.User;
import com.ganbook.utils.Const;
import com.ganbook.utils.FileDescriptor;
import com.ganbook.utils.ImageUtils;
import com.ganbook.utils.UrlUtils;

import com.project.ganim.R;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.PermissionUtils;

/**
 * Created by Noa on 29/10/2015.
 */
public class FavoriteActivity extends BaseAppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout mSwipeRefreshLayout;
    RecyclerView photos;
    FavoritesAdapter adapter;
    HashMap<String, ArrayList<getfavorite_Response>> map;
    ArrayList<getfavorite_Response> allFavorites;
    FrameLayout content_frame;
    GridLayoutManager layoutManager;
    boolean fragmentOpen = false;
    public static boolean wasShown;
    private ImageView downloadFavorites;
    private List<String> selectImagesTest = new ArrayList<>();
    private static final String[] PERMISSION_PERFORMDOWNLOADPICTURES = new String[] {"android.permission.WRITE_EXTERNAL_STORAGE"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MyApp.sendAnalytics("favorite-ui", "favorite"+"-ui"+ User.getId(), "favorite-ui", "Favorite");


        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        setContentView(R.layout.activity_favorite);

        setActionBar(getString(R.string.favorites), true);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_layout);

        mSwipeRefreshLayout.setOnRefreshListener(FavoriteActivity.this);
        mSwipeRefreshLayout.setColorScheme(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        content_frame = (FrameLayout)findViewById(R.id.content_frame);

        photos = (RecyclerView) findViewById(R.id.photos);

        layoutManager = new GridLayoutManager(FavoriteActivity.this,4);
//        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        photos.setLayoutManager(layoutManager);

        downloadFavorites = findViewById(R.id.downloadFavorites);

        downloadFavorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> urlsToDownload = getAllPictureUrls();
                downloadPictures(urlsToDownload);
            }
        });


        map = User.current.getFavorites();
        if(map == null || !wasShown) {
            call_getfavorite();
        }
        else
        {
            updateUI();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.save_button_menu, menu);
        menu.findItem(R.id.save_button).setTitle(getString(R.string.edit_btn));

        menu.findItem(R.id.save_button).setVisible(false);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:

                onBackPressed();

                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void refresh()
    {
        call_getfavorite();
    }

    @Override
    public void onRefresh() {
        mSwipeRefreshLayout.setRefreshing(false);
        refresh();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (fragmentOpen) {

            content_frame.setVisibility(View.GONE);
            fragmentOpen = false;
            updateUI();
        }
    }

    public void onItemClick(View v) { //setOnClickListener

        int position = photos.getChildPosition(v);

        ArrayList<FavoritesAdapter.Item> items = FavoritesAdapter.mapToArrayList(User.current.getFavorites());
        int type = items.get(position).getViewType();

        if (type == FavoritesAdapter.FAVORITE_ITEM) {

            ArrayList<FileDescriptor> all_images = getFullSizeImageArr();

            ArrayList<getpicture_Response> pictures = new ArrayList<getpicture_Response>();
            for(getfavorite_Response response:allFavorites)
            {
                response.favorite = true;
                pictures.add(response);
            }

            position = position - ((FavoritesAdapter.FavoriteItem)items.get(position)).section - 1;
            Log.d("PICTURE NAME ",String.valueOf(position));
            getfavorite_Response elem = allFavorites.get(position);

            String url = ImageUtils.getFullSizeImage(elem.gan_id, elem.class_id, elem.album_id, elem.picture_name);

            //selectImagesTest.add(url);
            //v.setBackgroundColor(Color.RED);
            MyApp.singleImageObject = new SingleImageObject(position, true, all_images, pictures, allFavorites.get(position).year, allFavorites.get(position).class_id, allFavorites.get(position).gan_id);
            MyApp.async_writeImageToLocaCache();

            content_frame.setVisibility(View.VISIBLE);

            fragmentToMoveTo = FragmentType.Single_Image;
            moveToTab(FragmentType.Single_Image);

            fragmentOpen = true;
        }
    }


    private ArrayList<FileDescriptor> getFullSizeImageArr() {
        allFavorites = new ArrayList<getfavorite_Response>();
        for(ArrayList<getfavorite_Response> arrayList: map.values())
        {
            allFavorites.addAll(arrayList);
        }

        int len = allFavorites.size();

        ArrayList<FileDescriptor> fullSizeImageArr = new ArrayList<FileDescriptor>();
        for (int i = 0; i < len; i++) {
            getfavorite_Response elem = allFavorites.get(i);

            String url = ImageUtils.getFullSizeImage(elem.gan_id, elem.class_id, elem.album_id, elem.picture_name);

            fullSizeImageArr.add(i, new FileDescriptor(url, null, UploadStatus.SUCCESS));
        }
//		}
        return fullSizeImageArr;
    }

    private List<String> getAllPictureUrls() {
        allFavorites = new ArrayList<getfavorite_Response>();
        for(ArrayList<getfavorite_Response> arrayList: map.values())
        {
            allFavorites.addAll(arrayList);
        }

        int len = allFavorites.size();

        List<String> fullSizeImageUrl = new ArrayList<String>();
        for (int i = 0; i < len; i++) {
            getfavorite_Response elem = allFavorites.get(i);

            String url = ImageUtils.getFullSizeImage(elem.gan_id, elem.class_id, elem.album_id, elem.picture_name);

            fullSizeImageUrl.add(url);
        }
        return fullSizeImageUrl;
    }

    public void downloadPictures(final List<String> pics) {

        SweetAlertDialog downloadDialog = new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE);

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

                if (PermissionUtils.hasSelfPermissions(FavoriteActivity.this, PERMISSION_PERFORMDOWNLOADPICTURES)) {
                    performDownloadPictures(pics);
                } else {
                    ActivityCompat.requestPermissions(FavoriteActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                }
            }
        });

        downloadDialog.show();
    }

    private void call_getfavorite()
    {
        final Activity a = this;

        JsonTransmitter.send_getfavorite(new ICompletionHandler() {
            @Override
            public void onComplete(ResultObj result) {
//				stopProgress();
                if (!result.succeeded) {
                    if (JsonTransmitter.NO_NETWORK_MODE.equals(result.result)) {


                        return;
                    }
                    stopProgress();
                    CustomToast.show(a, result.result);
                } else {
                    ArrayList<getfavorite_Response> _favoriteList = new ArrayList<getfavorite_Response>();
                    map = new HashMap<String, ArrayList<getfavorite_Response>>();

                    ArrayList<BaseResponse> arr = result.getResponseArray();
                    for (BaseResponse r: arr) {
                        getfavorite_Response response = (getfavorite_Response)r;

                        ArrayList<getfavorite_Response> favorites = map.get(response.year);

                        if(favorites == null)
                        {
                            favorites = new ArrayList<getfavorite_Response>();
                        }

                        favorites.add(response);

                        map.put(response.year,favorites);
                    }

                    wasShown = true;
                    User.current.updateFavorite(map);
                    updateUI();
                }
            }
        });
    }

    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    public void performDownloadPictures(List<String> pics) {

        DownloadManager dm = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);

        for (String url: pics) {
            DownloadManager.Request request = new DownloadManager.Request(
                    Uri.parse(url));

            String fileName = UrlUtils.urlToName(url);
            if (fileName==null) {
                fileName = System.currentTimeMillis() + ".jpeg";
            }

            String dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath()
                    + File.separator + Const.FAVORITE_PICS_DIR + File.separator;

            request.setDestinationInExternalPublicDir( dir, fileName);
            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
            request.setVisibleInDownloadsUi(true);
            request.setTitle(getString(R.string.app_name));

            long enqueue = dm.enqueue(request);
        }

    }

    private void updateUI()
    {
        adapter = new FavoritesAdapter(MyApp.context, map, FavoriteActivity.this);

        photos.setAdapter(adapter);

        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return adapter.isHeader(position) ? layoutManager.getSpanCount() : 1;
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
    }

    @Override
    protected void onPause() {

        super.onPause();
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
    }
}

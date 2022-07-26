package com.ganbook.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.ganbook.adapters.GalleryPicsGridAdapter;
import com.ganbook.fragments.AlbumDetailsFragment;
import com.ganbook.fragments.PreviewSelectedPicturesFragment;
import com.ganbook.interfaces.GridGalleryInterface;
import com.ganbook.interfaces.TitleIteractionListener;
import com.ganbook.models.GridViewPicture;
import com.ganbook.models.MediaFile;
import com.ganbook.user.User;
import com.ganbook.utils.FragmentUtils;
import com.ganbook.utils.debugUtils.MailSender;
import com.project.ganim.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class GridGalleryActivity extends BaseAppCompatActivity implements
        AdapterView.OnItemClickListener, GridGalleryInterface, FragmentManager.OnBackStackChangedListener, TitleIteractionListener {

    private static final String TAG = GridGalleryActivity.class.getName();
    private static final int DEFAULT_MAX = 50;

    //    List<GridViewPicture> gridItems;
    Stack<GalleryPicsGridAdapter> adapterStack;
    GridView gridView;
    private String albumName;
    TextView selectedPics;

    private String strMax;
    private int max;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid_gallery);

        setActionBar(getString(R.string.select_picture), false);

        albumName = getIntent().getStringExtra("albumName");

        adapterStack = new Stack<>();

        gridView = (GridView) findViewById(R.id.gridView);
        selectedPics = (TextView) findViewById(R.id.selected_pics);

        // Set the onClickListener
        gridView.setOnItemClickListener(this);

        createGridItems(null);

        strMax = User.current.max_images_batch_upload_android;
        
        if  ((strMax == null) || strMax.isEmpty()) {
            strMax = "" + DEFAULT_MAX;
            max = DEFAULT_MAX;
        }
        else {
            max = Integer.parseInt(strMax);
            if (max < 1) {
                max = DEFAULT_MAX;
            }

            strMax = "" + max;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.select_pictures_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_ok:

                Log.d(TAG, "onOptionsItemSelected: ok clicked");

                if (gridView.getAdapter() != null) {
                    List<GridViewPicture> pictures = ((GalleryPicsGridAdapter) gridView.getAdapter()).getSelectedItems();

                    Log.d(TAG, "onOptionsItemSelected: pics selected = " + pictures);


                    if (pictures.size() > 0) {
                        ArrayList<MediaFile> mediaFiles = new ArrayList<MediaFile>();

                        for (int i = 0; i < pictures.size(); i++) {

                            mediaFiles.add(new MediaFile(pictures.get(i).getPath()));
                        }

                        StringBuilder filePathArr = new StringBuilder();

                        for (MediaFile mediaFile : mediaFiles) {

                            filePathArr.append(mediaFile.getFilePath()).append("\n");
                        }

//                    GridGalleryActivityPermissionsDispatcher.sendMailWithCheck(this, filePathArr);

                        PreviewSelectedPicturesFragment previewSelectedPicturesFragment =
                                PreviewSelectedPicturesFragment.newInstance(albumName, mediaFiles);

                        FragmentUtils.openFragment(previewSelectedPicturesFragment, R.id.layout_root,
                                PreviewSelectedPicturesFragment.TAG, GridGalleryActivity.this, true);

                        selectedPics.setVisibility(View.GONE);
                    } else
                        Toast.makeText(this, R.string.not_selected_pictures, Toast.LENGTH_SHORT).show();
                }
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    public void sendMail(StringBuilder filePathArr) {

        MailSender.writeStringAsFile(GridGalleryActivity.this, filePathArr.toString(), "temp.txt");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "ACTIVITY DESTROYED");
        FragmentUtils.openFragment(AlbumDetailsFragment.newInstance(), R.id.content_frame, "SINGLE", this, true);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.d(TAG, "onResume: called onresume");

        if (gridView.getAdapter() != null && ((GalleryPicsGridAdapter) gridView.getAdapter()).getSelectedItems().size() > 0)
            selectedPics.setVisibility(View.VISIBLE);
        else
            selectedPics.setVisibility(View.GONE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        GridGalleryActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    /**
     * This will create our GridViewItems and set the adapter
     *
     * @param selection param for query selection
     */
    private void setGridAdapter(String selection) {
        // Create a new grid adapter
        createGridItems(selection);
    }


    /**
     * Go through the specified directory, and create items to display in our
     * GridView
     */
    private void createGridItems(final String selection) {

        final int Max = this.max;

        Thread background = new Thread(new Runnable() {

            // After call for background.start this run method call
            @RequiresApi(api = Build.VERSION_CODES.Q)
            public void run() {

                ArrayList<GridViewPicture> items = new ArrayList<>();

                String[] projection = new String[] {
                        MediaStore.Images.Media._ID,
                        MediaStore.Images.Media.DATA,
                        MediaStore.Images.Media.DISPLAY_NAME,
                        MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                        MediaStore.Images.Media.DATE_ADDED,
                        MediaStore.Images.Media.DATE_TAKEN
                };

                // content:// style URI for the "primary" external storage volume
                Uri images = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

                // Make the query.
                Cursor cur = getContentResolver().query(
                        images,
                        projection,
                        selection,
                        null,
                        MediaStore.Images.Media.DATE_ADDED + " DESC");

                Log.i("ListingImages"," query count=" + cur.getCount());

                if (cur.moveToFirst()) {
                    String bucket;
                    String date;
                    String name;
                    String filePath;

                    int bucketColumn = cur.getColumnIndex(
                            MediaStore.Images.Media.BUCKET_DISPLAY_NAME);

                    int dateColumn = cur.getColumnIndex(
                            MediaStore.Images.Media.DATE_TAKEN);

                    int nameColumn = cur.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME);

                    int pathColumn = cur.getColumnIndex(MediaStore.Images.Media.DATA);

                    do {
                        // Get the field values
                        bucket = cur.getString(bucketColumn);
                        date = cur.getString(dateColumn);
                        name = cur.getString(nameColumn);
                        filePath = cur.getString(pathColumn);
                        // Do something with the values.
                        Log.i(TAG, "rana bucket=" + bucket
                                + " date_taken=" + date
                                + " name= " + name
                                + " path = " + filePath);

                        //adding folders first
                        String picName = selection == null ? bucket : name;

                        GridViewPicture gridViewPicture = new GridViewPicture(false, picName,
                                selection == null,
                                new File(filePath).getPath());

                        if (selection == null) {
                            if (!items.contains(gridViewPicture))
                                items.add(gridViewPicture);
                        }
                        else
                            items.add(gridViewPicture);

                    } while (cur.moveToNext());

                }

                threadMsg(items);
                cur.close();
            }

            private void threadMsg(ArrayList<GridViewPicture> items) {

                if (items!=null && items.size() > 0) {
                    Message msgObj = handler.obtainMessage();
                    Bundle b = new Bundle();
                    b.putParcelableArrayList("pics", items);
                    msgObj.setData(b);
                    handler.sendMessage(msgObj);
                }
            }

            // Define the Handler that receives messages from the thread and update the progress
            @SuppressLint("HandlerLeak")
            private final Handler handler = new Handler() {

                public void handleMessage(Message msg) {

                    ArrayList<GridViewPicture> pics = msg.getData().getParcelableArrayList("pics");

                    if (pics != null && pics.size() > 0) {

                        //add processed pics/folders to grid

                        GalleryPicsGridAdapter adapter = new GalleryPicsGridAdapter(getBaseContext(), pics,
                                GridGalleryActivity.this);

                        //we push prev adapter to stack to pop it in backpress
                        adapterStack.push((GalleryPicsGridAdapter) gridView.getAdapter());

                        adapter.setMax(Max);
                        //set adapter
                        gridView.setAdapter(adapter);
                    } else {

                       //error

                    }

                }
            };

        });

        // Start Thread
        background.start();  //After call start method thread called run Method
    }

    @Override
    public void
    onItemClick(AdapterView<?> parent, View view, int position, long id) {

        GridViewPicture pic = (GridViewPicture) gridView.getAdapter().getItem(position);

        if (pic.isDirectory()) {

            Log.d(TAG, "onItemClick: file name " + pic.getFileName());
            Log.d(TAG, "onItemClick: after = " + DatabaseUtils.sqlEscapeString(pic.getFileName()));

            setGridAdapter(MediaStore.Images.Media.BUCKET_DISPLAY_NAME + "="
                    + DatabaseUtils.sqlEscapeString(pic.getFileName()));
            selectedPics.setVisibility(View.VISIBLE);
            selectedPics.setText(0 + " / " + strMax);
        }
        else {
            // Display the image

        }

    }

    @Override
    public void onBackPressed() {

        if (getSupportFragmentManager().findFragmentByTag(PreviewSelectedPicturesFragment.TAG) != null) {
            FragmentUtils.popBackStack(this);
            selectedPics.setVisibility(View.VISIBLE);
        }
        else {
            if (!adapterStack.empty() && adapterStack.size() != 1) {
                gridView.setAdapter(adapterStack.pop());
                selectedPics.setVisibility(View.GONE);
            } else
                super.onBackPressed();
        }
    }

    @Override
    public void showSelectedCount(int cnt) {

        selectedPics.setText(cnt + " / " + strMax);
    }

    @Override
    public void onBackStackChanged() {
        Log.d(TAG, "onBackStackChanged: stack count === " + fragmentManager.getBackStackEntryCount());
    }

    @Override
    public void setTitle(String text) {

    }
}
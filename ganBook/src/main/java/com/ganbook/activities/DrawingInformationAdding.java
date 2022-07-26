package com.ganbook.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.deskode.recorddialog.RecordDialog;
import com.fxn.pix.Pix;
import com.ganbook.app.MyApp;
import com.ganbook.dialogs.Dialogs;
import com.ganbook.models.DrawingAnswer;
import com.ganbook.models.factories.DrawingAnswerFactory;
import com.ganbook.services.UploadDrawingService;
import com.ganbook.ui.CircleImageView;
import com.ganbook.ui.ContactListAdapter;
import com.ganbook.user.User;
import com.ganbook.utils.Const;

import com.squareup.picasso.Picasso;
import java.io.File;
import java.util.ArrayList;
import java.util.Objects;
import com.project.ganim.R;
import static com.ganbook.utils.Const.CAMERA_APP;
import static com.ganbook.utils.Const.GALLERY_APP;
import static com.ganbook.utils.GenderUtils.BOY;


public class DrawingInformationAdding extends BaseAppCompatActivity {

    private ImageView kidDrawing;
    private Uri currentPhotoUri;
    private DrawingAnswer pictureToUpload;
    private String audioPath, audioName;
    private CircleImageView kidImageDrawingPage;
    private EditText drawingDescription;
    private Button closeKeyboardBtn;
    private TextView kidNameText;
    private RecordDialog recordDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_drawing);
        setActionBar(getString(R.string.drawing_add_story_text), true);

        kidDrawing = findViewById(R.id.selectedDrawing);
        drawingDescription = findViewById(R.id.add_drawing_description);
        closeKeyboardBtn = findViewById(R.id.closeKeyboardBtn);
        kidImageDrawingPage = findViewById(R.id.kidImageDrawingPage);
        kidNameText = findViewById(R.id.kidNameText);

        kidNameText.setText(User.current.getCurrentKidName());

        MyApp.sendAnalytics("drawing-adding-ui", "darwing-addiing-"+User.getId(), "drawing-adding-ui", "DrawingAdd");


        InputFilter filter = new InputFilter() {
            public CharSequence filter(CharSequence source, int start,
                                       int end, Spanned dest, int dstart, int dend) {

                for (int i = start;i < end;i++) {
                    if (!Character.isLetterOrDigit(source.charAt(i)) &&
                            !Character.toString(source.charAt(i)).equals(",") &&
                            !Character.toString(source.charAt(i)).equals("."))
                    {
                        return "";
                    }
                }
                return null;
            }
        };

        drawingDescription.setFilters(new InputFilter[] { filter });

        drawingDescription.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b) {
                    closeKeyboardBtn.setVisibility(View.VISIBLE);
                    closeKeyboardBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            hideSoftKeyboard(DrawingInformationAdding.this);
                            closeKeyboardBtn.setVisibility(View.GONE);
                            drawingDescription.clearFocus();
                        }
                    });
                } else {
                    closeKeyboardBtn.setVisibility(View.GONE);
                }
            }
        });


        RequestOptions myOptions = new RequestOptions()
                .centerCrop()
                .dontAnimate()
                .dontTransform()
                .placeholder(R.drawable.add_pic_default)
                .diskCacheStrategy(DiskCacheStrategy.ALL);

        if(User.current.getCurrentKidPic() == null) {
            int gender = User.current.getCurrentKidGender().equals(BOY) ? R.drawable.boydefault : R.drawable.girldefault;
            Glide.with(kidImageDrawingPage.getContext())
                    .load(gender)
                    .apply(myOptions)
                    .into(kidImageDrawingPage);

        } else {
            String kidPicUrl = ContactListAdapter.kidPicToUrl(User.current.getCurrentKidPic());
            Glide.with(kidImageDrawingPage.getContext())
                    .load(kidPicUrl)
                    .apply(myOptions)
                    .into(kidImageDrawingPage);
        }


        kidDrawing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Pix.start(DrawingInformationAdding.this, GALLERY_APP);
            }
        });

        findViewById(R.id.show).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recordDialog = RecordDialog.newInstance(getString(R.string.record_audio_string));
                recordDialog.setMessage(getString(R.string.press_for_record_string));
                recordDialog.show(DrawingInformationAdding.this.getFragmentManager(),"TAG");
                recordDialog.setPositiveButton(getString(R.string.save_btn), new RecordDialog.ClickListener() {
                    @Override
                    public void OnClickListener(String path) {
                        audioPath = path;
                        audioName = System.currentTimeMillis() + "_drawing_audio.wav";
                    }
                });
                recordDialog.setCancelable(true);
            }
        });
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        Objects.requireNonNull(inputMethodManager).hideSoftInputFromWindow(
                Objects.requireNonNull(activity.getCurrentFocus()).getWindowToken(), 0);
    }

    @Override
    protected void onResume() {
        super.onResume();

        View rootView =((ViewGroup)findViewById(android.R.id.content)).
                getChildAt(0);
        rootView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    hideSoftKeyboard(DrawingInformationAdding.this);
                }
            }
        });
    }

    private void startUploadingToServer(DrawingAnswer picturesToUpload) {

        if (isNetworkAvailable()) {
            Intent intent = new Intent(this, UploadDrawingService.class);

            intent.putExtra(Const.PICTURES_PARCELABLE_LIST, picturesToUpload);
            intent.putExtra(Const.GAN_ID, User.current.getCurrentGanId());
            intent.putExtra(Const.KID_ID, User.current.getCurrentKidId());
            intent.putExtra(Const.CLASS_ID, User.current.getCurrentClassId());
            intent.putExtra(Const.DRAWING_DESCRIPTION, drawingDescription.getText().toString());
            intent.putExtra(Const.DRAWING_ALBUM_ID, User.current.getCurrentDrawingAlbumId());
            intent.putExtra("drawingAudioPath", audioPath);
            intent.putExtra("drawingAudioName", audioName);
            startService(intent);
        } else
            Dialogs.errorDialogWithButton(DrawingInformationAdding.this, "Error!", getString(R.string.internet_offline), getString(R.string.ok));
    }


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case GALLERY_APP:
                if(data != null) {
                    ArrayList<String> returnValue = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);
                    Uri imageUri = Uri.fromFile(new File(returnValue.get(0)));
                    pictureToUpload = DrawingAnswerFactory.getDrawingAnswer(User.current.getCurrentDrawingAlbumId(), returnValue.get(0));
                    Picasso.with(DrawingInformationAdding.this)
                            .load(imageUri)
                            .fit()
                            .centerCrop()
                            .into(kidDrawing);
                }
                break;

            case CAMERA_APP:
                kidDrawing.setImageURI(currentPhotoUri);
                 pictureToUpload = DrawingAnswerFactory
                        .getDrawingAnswer(User.current.getCurrentDrawingAlbumId(), currentPhotoUri.getPath());
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.single_drawing_album_menu, menu);

        MenuItem menuItem = menu.findItem(R.id.save_menu);


        menuItem.getActionView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File outputFile = null;
                if (audioPath != null) {
                    outputFile = new File(audioPath);
                }

                if(pictureToUpload == null) {
                    Dialogs.errorDialogWithButton(DrawingInformationAdding.this, getString(R.string.warning), getString(R.string.drawing_choose_take_warning), getString(R.string.ok));
                } else if(drawingDescription.getText().toString().isEmpty()) {
                    Dialogs.errorDialogWithButton(DrawingInformationAdding.this, getString(R.string.warning), getString(R.string.drawing_upload_title_description), getString(R.string.ok));
                } else if (audioPath == null || !outputFile.exists()) {
                    Dialogs.errorDialogWithButton(DrawingInformationAdding.this, getString(R.string.warning), "Incorrect audio file or audio is empty!", getString(R.string.ok));
                } else {
                    startUploadingToServer(pictureToUpload);
                    finish();
                }
            }
        });

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

}




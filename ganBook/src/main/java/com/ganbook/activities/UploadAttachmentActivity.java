package com.ganbook.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.fxn.cue.Cue;
import com.fxn.cue.enums.Type;
import com.ganbook.dialogs.Dialogs;
import com.ganbook.models.SuccessAnswer;
import com.ganbook.models.events.AttachmentFileEvent;
import com.ganbook.models.events.ProgressEvent;
import com.ganbook.services.UploadMessageAttachmentService;
import com.ganbook.user.User;
import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;
import com.project.ganim.R;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import java.io.File;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UploadAttachmentActivity extends BaseAppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private static final int DOCUMENT_UPLOAD_CODE = 123;
    private TextView selectedFileName;
    private ImageView selectedFileImage;
    private ProgressBar fileProgressBar;
    private EditText documentTitle, documentDescription;
    private String documentFileName;
    private static final long MAX_ATTACHMENT_SIZE = 20_000_000;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_attachments_layout);
        setActionBar(getString(R.string.upload_documents_string), true);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        selectedFileName = findViewById(R.id.fileNameSelected);
        selectedFileImage = findViewById(R.id.fileImageSelected);
        fileProgressBar = findViewById(R.id.fileProgressBar);
        documentTitle = findViewById(R.id.attachmentTitle);
        documentDescription = findViewById(R.id.attachmentDescription);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                if(menuItem.getItemId() == R.id.bottomNavigationUploadDocument) {
                    new MaterialFilePicker()
                            .withActivity(UploadAttachmentActivity.this)
                            .withRequestCode(DOCUMENT_UPLOAD_CODE)
                            .withHiddenFiles(false)
                            .start();

                    return true;
                }
                return false;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == DOCUMENT_UPLOAD_CODE && resultCode == RESULT_OK) {
            String realPath = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);
            File attachmentFile = new File(realPath);
            if (attachmentFile.length() <= MAX_ATTACHMENT_SIZE) {
                selectedFileName.setVisibility(View.VISIBLE);
                selectedFileImage.setVisibility(View.VISIBLE);
                fileProgressBar.setVisibility(View.VISIBLE);

                String splitFile[] = realPath.split("/");
                String fileName = splitFile[splitFile.length - 1];
                documentFileName = fileName;
                selectedFileName.setText(fileName);

                Intent intent = new Intent(UploadAttachmentActivity.this, UploadMessageAttachmentService.class);

                intent.putExtra("attachmentFilePath", realPath);
                intent.putExtra("attachmentFileName", fileName);
                startService(intent);
            } else {
                Cue.init()
                        .with(UploadAttachmentActivity.this)
                        .setMessage(getString(R.string.max_file_size_20mb))
                        .setType(Type.DANGER)
                        .setTextSize(20)
                        .show();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }


    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);

    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onProgressEvent(ProgressEvent progressEvent){

        fileProgressBar.setMax(progressEvent.getTotalBytes());
        fileProgressBar.setProgress(progressEvent.getCurrentByte());
        EventBus.getDefault().removeStickyEvent(progressEvent);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onAttachmentFileEvent(AttachmentFileEvent attachmentFileEvent){

        Cue.init()
                .with(UploadAttachmentActivity.this)
                .setMessage(getString(R.string.document_attached_string))
                .setType(Type.SUCCESS)
                .setTextSize(20)
                .show();
        EventBus.getDefault().removeStickyEvent(attachmentFileEvent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.single_drawing_album_menu, menu);

        MenuItem menuItem = menu.findItem(R.id.save_menu);

        menuItem.getActionView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(documentTitle.getText().toString().isEmpty() || documentDescription.getText().toString().isEmpty()) {
                    Dialogs.errorDialogWithButton(UploadAttachmentActivity.this, getString(R.string.required_string), getString(R.string.all_fields_required), getString(R.string.ok));

                } else if(documentFileName == null || documentFileName.isEmpty()) {
                    Dialogs.errorDialogWithButton(UploadAttachmentActivity.this, getString(R.string.required_string), getString(R.string.please_attach_document_string), getString(R.string.ok));
                } else {
                    Call<SuccessAnswer> answerCall = ganbookApiInterfacePOST.uploadDocument(User.current.getCurrentClassId(), documentTitle.getText().toString(), documentDescription.getText().toString(), documentFileName);

                    answerCall.enqueue(new Callback<SuccessAnswer>() {
                        @Override
                        public void onResponse(Call<SuccessAnswer> call, Response<SuccessAnswer> response) {

                            if (response.body() != null) {
                                SuccessAnswer answer = response.body();

                                if (answer.isSuccess()) {
                                    Intent intent = new Intent(UploadAttachmentActivity.this, UserAttachmentsActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<SuccessAnswer> call, Throwable t) {

                        }
                    });
                }
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }



}

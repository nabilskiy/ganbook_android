package com.ganbook.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.ganbook.adapters.UserAttachmentsListAdapter;
import com.ganbook.models.SuccessAnswer;
import com.ganbook.models.UserAttachmentModel;
import com.ganbook.s3transferutility.Constants;
import com.ganbook.user.User;
import com.project.ganim.R;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserAttachmentsActivity extends BaseAppCompatActivity {

    private ListView userAttachmentListView;
    private UserAttachmentsListAdapter userAttachmentListAdapter;
    private ProgressBar progressBar;
    private BottomNavigationView bottomNavigationView;
    private List<UserAttachmentModel> userAttachmentModelList;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_attachments_layout);
        setActionBar(getString(R.string.document_title_string), true);

        progressBar = findViewById(R.id.attachmentsListProgress);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        userAttachmentListView = findViewById(R.id.attachmentsList);

        if(User.isParent() || User.isStaff()) {
            bottomNavigationView.setVisibility(View.GONE);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT
            );
            params.bottomMargin = 0;
            userAttachmentListView.setLayoutParams(params);
        }

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                if(menuItem.getItemId() == R.id.bottomNavigationAttachmentUpload) {
                    Intent intent = new Intent(UserAttachmentsActivity.this, UploadAttachmentActivity.class);
                    startActivity(intent);
                    return true;
                }
                return false;
            }
        });


        loadDocuments();


        userAttachmentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                UserAttachmentModel model = (UserAttachmentModel) adapterView.getItemAtPosition(i);
                String url = "http://" + Constants.BUCKET_NAME + "/Documents/" + User.current.getCurrentGanId() + "/" + User.current.getCurrentClassId() + "/" + model.getDocumentName();
                AttachmentWebView.start(UserAttachmentsActivity.this, url);
            }
        });

    }

    private void loadDocuments() {
        userAttachmentModelList = new ArrayList<>();

        Call<List<UserAttachmentModel>> documents = ganbookApiInterfaceGET.getDocuments(User.current.getCurrentClassId());

        documents.enqueue(new Callback<List<UserAttachmentModel>>() {
            @Override
            public void onResponse(Call<List<UserAttachmentModel>> call, Response<List<UserAttachmentModel>> response) {

                userAttachmentModelList = response.body();

                if (userAttachmentModelList != null && userAttachmentModelList.size() != 0 && response.body() != null) {
                    userAttachmentListAdapter = new UserAttachmentsListAdapter(UserAttachmentsActivity.this, userAttachmentModelList);
                    userAttachmentListView.setAdapter(userAttachmentListAdapter);
                    progressBar.setVisibility(View.GONE);
                } else {
                    progressBar.setVisibility(View.GONE);
                    findViewById(R.id.emptyListView).setVisibility(View.VISIBLE);
                    userAttachmentListView.setEmptyView(findViewById(R.id.emptyListView));
                }
            }

            @Override
            public void onFailure(Call<List<UserAttachmentModel>> call, Throwable t) {

            }
        });

    }

    public void deleteDocument(String documentId) {
        Call<SuccessAnswer> answerCall = ganbookApiInterfacePOST.deleteDocument(User.current.getCurrentClassId(), documentId);

        answerCall.enqueue(new Callback<SuccessAnswer>() {
            @Override
            public void onResponse(Call<SuccessAnswer> call, Response<SuccessAnswer> response) {

                if(response.body() != null) {
                    SuccessAnswer answer = response.body();

                    if(answer.isSuccess()) {
                        userAttachmentListAdapter.clear();
                        loadDocuments();
                    }
                }
            }

            @Override
            public void onFailure(Call<SuccessAnswer> call, Throwable t) {

            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}

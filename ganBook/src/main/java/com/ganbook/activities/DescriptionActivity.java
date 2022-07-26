package com.ganbook.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ganbook.amazinglist.special.CustomToast;
import com.ganbook.models.SuccessAnswer;
import com.ganbook.user.User;
import com.project.ganim.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DescriptionActivity extends BaseAppCompatActivity {

    private EditText albumDescription;
    private String albumId;
    private Button updateButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.description_layout);
        setActionBar(getString(R.string.album_description_hint), true);

        albumDescription = findViewById(R.id.editAlbumDescription);
        updateButton = findViewById(R.id.updateDescriptionBtn);

        Intent intent = getIntent();
        albumId = intent.getStringExtra("album_id");
        String albumDescString = intent.getStringExtra("album_description");

        if(User.isTeacher()) {
            updateButton.setVisibility(View.VISIBLE);
            albumDescription.setText(albumDescString, TextView.BufferType.EDITABLE);
            albumDescription.setVisibility(View.VISIBLE);
            updateButton.setOnClickListener(
                    new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateAlbum(albumId, albumDescription.getText().toString().trim());

                }
            });
        } else {
            albumDescription.setEnabled(false);
            albumDescription.setText(albumDescString);
            updateButton.setVisibility(View.GONE);
        }

    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
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
                    hideSoftKeyboard(DescriptionActivity.this);
                }
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

    private void updateAlbum(String albumId, String newDescription) {

        Call<SuccessAnswer> call = ganbookApiInterfacePOST.editAlbumDescription(albumId, newDescription);

        call.enqueue(new Callback<SuccessAnswer>() {
            @Override
            public void onResponse(Call<SuccessAnswer> call, Response<SuccessAnswer> response) {
                SuccessAnswer answer = response.body();

                if(answer != null && answer.isSuccess()) {
                    CustomToast.show(DescriptionActivity.this, getString(R.string.description_updated_successfully));
                    Intent intent = new Intent(DescriptionActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(0, 0);
                }
            }

            @Override
            public void onFailure(Call<SuccessAnswer> call, Throwable t) {
                CustomToast.show(DescriptionActivity.this, R.string.error_rename_album);
            }
        });
    }

}

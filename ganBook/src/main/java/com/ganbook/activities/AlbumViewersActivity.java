package com.ganbook.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.ganbook.adapters.AlbumViewersAdapter;
import com.ganbook.amazinglist.special.CustomToast;
import com.ganbook.models.AlbumViewerModel;
import com.project.ganim.R;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AlbumViewersActivity extends BaseAppCompatActivity {

    private AlbumViewersAdapter albumViewersAdapter;
    private ListView albumViewersList;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.album_viewers_layout);
        Intent intent = getIntent();
        setActionBar(getString(R.string.who_viewed_album) + intent.getStringExtra("album_name"), true);

        albumViewersList = findViewById(R.id.albumViewersList);
        progressBar = findViewById(R.id.albumViewersProgress);
        progressBar.setVisibility(View.VISIBLE);

        Call<List<AlbumViewerModel>> albumViewersCall = ganbookApiInterfaceGET.getAlbumViewers(intent.getStringExtra("album_id"));

        albumViewersCall.enqueue(new Callback<List<AlbumViewerModel>>() {
            @Override
            public void onResponse(Call<List<AlbumViewerModel>> call, Response<List<AlbumViewerModel>> response) {
                if(response.body() != null) {
                    List<AlbumViewerModel> albumViewerModelList = response.body();
                    albumViewersAdapter = new AlbumViewersAdapter(albumViewerModelList, AlbumViewersActivity.this);
                    albumViewersList.setAdapter(albumViewersAdapter);
                    progressBar.setVisibility(View.GONE);
                } else {
                    CustomToast.show(AlbumViewersActivity.this, getString(R.string.album_no_viewers));
                    progressBar.setVisibility(View.GONE);
                }

            }

            @Override
            public void onFailure(Call<List<AlbumViewerModel>> call, Throwable t) {
                CustomToast.show(AlbumViewersActivity.this, t.getMessage());
                progressBar.setVisibility(View.GONE);
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

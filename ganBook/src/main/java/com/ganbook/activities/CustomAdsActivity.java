package com.ganbook.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.ganbook.app.MyApp;
import com.ganbook.interfaces.IGanbookApiCommercial;
import com.ganbook.models.Commercial;
import com.ganbook.models.CommercialClickCounter;
import com.project.ganim.R;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;
import javax.inject.Named;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomAdsActivity extends AppCompatActivity {

    private ImageView mainAdImage;
    private Button openPromoBtn, closePromoBtn;

    @Inject
    @Named("COMMERCIAL")
    IGanbookApiCommercial ganbookApiCommercial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_ads);
        init();

        Intent intent = getIntent();
        Commercial commercial = (Commercial) intent.getSerializableExtra("commercial");

        if (commercial.getImageName() != null) {
            Picasso.with(this)
                    .load(IGanbookApiCommercial.PROMO_S3_BUCKET_URL + commercial.getImageName())
                    .into(mainAdImage);
        }

        if (StringUtils.isBlank(commercial.getCommercialUrl())) {
            openPromoBtn.setVisibility(View.GONE);
        }

        closePromoBtn.setOnClickListener(view -> {
            ganbookApiCommercial.updateCommercialClicksCounter(commercial.getId())
                    .enqueue(new Callback<CommercialClickCounter>() {
                        @Override
                        public void onResponse(Call<CommercialClickCounter> call, Response<CommercialClickCounter> response) {
                            if (response.body() == null) {
                                return;
                            }
                            startActivity(new Intent(CustomAdsActivity.this, MainActivity.class));
                            finish();
                        }

                        @Override
                        public void onFailure(Call<CommercialClickCounter> call, Throwable t) {

                        }
                    });
        });


        openPromoBtn.setOnClickListener(view -> openPromoLink(commercial));
        mainAdImage.setOnClickListener(view -> openPromoLink(commercial));
    }

    private void init() {
        ((MyApp) getApplication()).getGanbookApiComponent().inject(this);

        openPromoBtn = findViewById(R.id.openPromoBtn);
        closePromoBtn = findViewById(R.id.closePromoBtn);
        mainAdImage = findViewById(R.id.mainAdImage);
    }

    private void openPromoLink(Commercial commercial) {
        if (StringUtils.isBlank(commercial.getCommercialUrl())) {
            return;
        }

        String url = commercial.getCommercialUrl();
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "http://" + url;
        }

        String finalUrl = url;
        ganbookApiCommercial.updateCommercialClicksCounter(commercial.getId())
                .enqueue(new Callback<CommercialClickCounter>() {
                    @Override
                    public void onResponse(Call<CommercialClickCounter> call, Response<CommercialClickCounter> response) {
                        if (response.body() == null) {
                            return;
                        }
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(finalUrl));
                        startActivity(browserIntent);
                        finish();
                    }

                    @Override
                    public void onFailure(Call<CommercialClickCounter> call, Throwable t) {

                    }
                });
    }
}
package com.ganbook.services;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.ganbook.activities.CustomAdsActivity;
import com.ganbook.app.MyApp;
import com.ganbook.interfaces.IGanbookApiCommercial;
import com.ganbook.models.Commercial;
import com.ganbook.models.events.SingleCommercialEvent;
import com.ganbook.user.User;

import org.apache.commons.lang3.StringUtils;
import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;
import javax.inject.Named;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SingleCommercialService extends IntentService {

    @Inject
    @Named("COMMERCIAL")
    IGanbookApiCommercial ganbookApiCommercial;

    public SingleCommercialService(String name) {
        super(name);
    }

    public SingleCommercialService() {
        super("SingleCommercialService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ((MyApp) getApplication()).getGanbookApiComponent().inject(this);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent == null) {
            return;
        }
        String userType = intent.getStringExtra("userType");
        Call<Commercial> call = ganbookApiCommercial.getSingleCommercial(userType);
        call.enqueue(new Callback<Commercial>() {
            @Override
            public void onResponse(Call<Commercial> call, Response<Commercial> response) {
                if (response.body() != null) {
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                    boolean isPromoVisited = prefs.getBoolean("isPromoVisited", false);
                    long visitTime = prefs.getLong("visitTime", 0);
                    String commercialId = prefs.getString("commercialId", null);

                    SharedPreferences.Editor edit = prefs.edit();

                    if (visitTime == 0) {
                        edit.putLong("visitTime", System.currentTimeMillis());
                    }
                    if (StringUtils.isBlank(commercialId)) {
                        edit.putString("commercialId", response.body().getId());
                    } else {
                        if (!commercialId.equals(response.body().getId())) {
                            visitTime = System.currentTimeMillis();
                            isPromoVisited = false;
                            edit.putLong("visitTime", System.currentTimeMillis());
                            edit.putString("commercialId", response.body().getId());
                            edit.putBoolean("isPromoVisited", false);
                        }
                    }


                    long diff = System.currentTimeMillis() - visitTime;
                    long diffDays = diff / (24 * 60 * 60 * 1000);

                    if (diffDays >= 1) {
                        edit.putBoolean("isPromoVisited", false);
                        edit.putLong("visitTime", System.currentTimeMillis());
                        isPromoVisited = false;
                    }

                    if (!isPromoVisited) {
                        edit.putBoolean("isPromoVisited", true);
                        EventBus.getDefault().postSticky(new SingleCommercialEvent(response.body()));
                    }
                    edit.apply();
                }
            }

            @Override
            public void onFailure(Call<Commercial> call, Throwable t) {
            }
        });
    }
}

package com.ganbook.interfaces;

import androidx.annotation.Keep;

import com.ganbook.models.Commercial;
import com.ganbook.models.CommercialClickCounter;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

@Keep
public interface IGanbookApiCommercial {
    String COMMERCIAL_BASE_URL = "https://promo.ganbook.co.il/api/";
    //String COMMERCIAL_BASE_URL = "http://192.168.0.118:3000/api/";
    String PROMO_S3_BUCKET_URL = "http://s3.ganbook.co.il/promo/";

    @GET("commercial")
    Call<List<Commercial>> getCommercials(@Query("status") String status);

    @GET("commercial/single")
    Call<Commercial> getSingleCommercial(@Query("userType") String userType);

    @POST("commercial/clicks")
    Call<CommercialClickCounter> updateCommercialClicksCounter(@Query("commercialId") String commercialId);
}

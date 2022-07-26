package com.ganbook.dagger.modules;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.ganbook.communication.json.transmitter.JsonTransmitter;
import com.ganbook.interfaces.GanbookApiInterface;
import com.ganbook.user.User;
import com.ganbook.utils.CompositeX509TrustManager;
import com.ganbook.utils.StrUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import javax.inject.Named;
import javax.inject.Singleton;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import dagger.Module;
import dagger.Provides;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by dmytro_vodnik on 7/27/16.
 * working on ganbook1 project
 */
@Module
public class NetModule {

    String mBaseUrl;
    private String commercialBaseUrl;

    // Constructor needs one parameter to instantiate.
    public NetModule(String baseUrl, String commercialBaseUrl) {
        this.mBaseUrl = baseUrl;
        this.commercialBaseUrl = commercialBaseUrl;
    }

    // Dagger will only look for methods annotated with @Provides
    @Provides
    @Singleton
    // Application reference must come from AppModule.class
    SharedPreferences providesSharedPreferences(Application application) {
        return PreferenceManager.getDefaultSharedPreferences(application);
    }

    @Provides
    @Singleton
    Gson provideGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setDateFormat("yyyy-MM-dd HH:mm:ss");
        return gsonBuilder.create();
    }


    private OkHttpClient provideOkHttpClient(boolean isPost) {
        //for logging
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        //append singing parameters for methods
        OkHttpClient.Builder httpClient =
                new OkHttpClient.Builder();

        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);

//            X509TrustManager tm = new X509TrustManager() {
//                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
//                    return new java.security.cert.X509Certificate[] {};
//                }
//
//                public void checkClientTrusted(X509Certificate[] chain,
//                                               String authType) throws CertificateException {
//                }
//
//                public void checkServerTrusted(X509Certificate[] chain,
//                                               String authType) throws CertificateException {
//                    try {
//                        if(chain != null && chain.length > 0) {
//                            chain[0].checkValidity();
//                        }
//                    } catch(Exception e) {
//                        e.printStackTrace();
//                        throw new CertificateException("Certificate not valid!!");
//                    }
//                }
//            };

            CompositeX509TrustManager compositeX509TrustManager = new CompositeX509TrustManager(trustStore);
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, new TrustManager[]{ compositeX509TrustManager }, null);

            httpClient.sslSocketFactory(sslContext.getSocketFactory(), compositeX509TrustManager);

        } catch (Exception e) {
            e.printStackTrace();
        }

        httpClient.addInterceptor(interceptor);

        if (isPost) {

            httpClient.addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request request = chain.request();
                    Request.Builder requestBuilder = request.newBuilder();

                    if(User.getAuthToken() != null) {
                        requestBuilder.addHeader("Token", User.getAuthToken());
                    }

                    String ts = "" + System.currentTimeMillis();
                    String apiKey = User.getApiKey(); //see CREATE_USER_OBJ_FROM_RESPONSE;
                    String userId = User.getUserId();

                    FormBody.Builder formBuilder = null;
                    if (apiKey != null && userId != null)
                        formBuilder = new FormBody.Builder()
                                .add("sig", JsonTransmitter.sig_request(apiKey,
                                        request.url().toString().replace(GanbookApiInterface.BASE_URL, ""),
                                        true, ts, userId))
                                .add("ts", ts)
                                .add("user_id", userId);


                    RequestBody formBody = formBuilder.build();
                    String postBodyString = StrUtils.bodyToString(request.body());
                    postBodyString += ((postBodyString.length() > 0) ? "&" : "") + StrUtils.bodyToString(formBody);
                    request = requestBuilder
                            .post(RequestBody.create(MediaType.parse("application/x-www-form-urlencoded;charset=UTF-8"), postBodyString))
                            .build();
                    return chain.proceed(request);
                }
            });

        } else {
            httpClient.addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {

                    Request original = chain.request();
                    HttpUrl originalHttpUrl = original.url();

                    String ts = "" + System.currentTimeMillis();
                    String apiKey = User.getApiKey(); //see CREATE_USER_OBJ_FROM_RESPONSE;
                    String userId = User.getUserId();

                    HttpUrl url = null;

                    if (apiKey != null && userId != null)
                        url = originalHttpUrl.newBuilder()
                                .addQueryParameter("sig", JsonTransmitter.sig_request(apiKey,
                                        original.url().toString().replace(GanbookApiInterface.BASE_URL, ""),
                                        false, ts, userId))
                                .addQueryParameter("ts", ts)
                                .addQueryParameter("user_id", userId)
                                .build();
                    else
                        url = originalHttpUrl.newBuilder().build();


                    // Request customization: add request headers
                    Request.Builder requestBuilder = original.newBuilder()
                            .url(url);

                    if(User.getAuthToken() != null) {
                        requestBuilder.addHeader("Token", User.getAuthToken());
                    }

                    Request request = requestBuilder.build();

                    return chain.proceed(request);
                }
            });
        }

        httpClient.connectTimeout(30, TimeUnit.SECONDS);
        httpClient.readTimeout(30, TimeUnit.SECONDS);
        httpClient.writeTimeout(30, TimeUnit.SECONDS);

        return httpClient.build();
    }

    @Provides
    @Singleton
    @Named("POST")
    Retrofit provideRetrofitPOST(Gson gson) {
        return new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(mBaseUrl)
                .client(provideOkHttpClient(true))
                .build();
    }

    @Provides
    @Singleton
    @Named("GET")
    Retrofit provideRetrofitGET(Gson gson) {
        return new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(mBaseUrl)
                .client(provideOkHttpClient(false))
                .build();
    }

    @Provides
    @Singleton
    @Named("COMMERCIAL")
    Retrofit provideRetrofitCommercial(Gson gson) {
        return new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(commercialBaseUrl)
                .client(new OkHttpClient())
                .build();
    }
}

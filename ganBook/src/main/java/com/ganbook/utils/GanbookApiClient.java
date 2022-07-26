package com.ganbook.utils;

import android.util.Log;

import com.ganbook.communication.json.GetUserKids_Response;
import com.ganbook.communication.json.transmitter.JsonTransmitter;
import com.ganbook.interfaces.GanbookApiInterface;
import com.ganbook.models.AlbumsAnswer;
import com.ganbook.models.CreateAlbumAnswer;
import com.ganbook.models.GetParentAnswer;
import com.ganbook.models.MessageAnswer;
import com.ganbook.models.MessageStatus;
import com.ganbook.models.OKAnswer;
import com.ganbook.models.OnPostS3Answer;
import com.ganbook.models.PictureAnswer;
import com.ganbook.models.SuccessAnswer;
import com.ganbook.models.answers.EventsAnswer;
import com.ganbook.models.answers.ReadMessageAnswer;
import com.ganbook.user.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * Created by dmytro_vodnik on 5/14/16.
 * working on GanbookApiClient project
 */
public class GanbookApiClient {

    private static final String TAG = "GANBOOK API CLIENT";
    private static GanbookApiClient ganbookApiClientPOST, ganbookApiClientGET;
    private GanbookApiInterface ganbookApiInterface;

    //Singleton for api instance
    public static GanbookApiClient init(boolean isPost) {

        if (isPost) {

            if (ganbookApiClientPOST == null)
                ganbookApiClientPOST = new GanbookApiClient(true);

            return ganbookApiClientPOST;
        } else {

            if (ganbookApiClientGET == null)
                ganbookApiClientGET = new GanbookApiClient(false);

            return ganbookApiClientGET;
        }
    }

    private GanbookApiClient(final boolean isPost) {

        //date formatter
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .create();

        //for logging
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        //append singing parameters for methods
        OkHttpClient.Builder httpClient =
                new OkHttpClient.Builder();

        httpClient.addInterceptor(interceptor);

        if (isPost) {

            httpClient.addInterceptor(new Interceptor() {
                        @Override
                        public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                Request.Builder requestBuilder = request.newBuilder();

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
                postBodyString += ((postBodyString.length() > 0) ? "&" : "") +  StrUtils.bodyToString(formBody);
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

                    Request request = requestBuilder.build();

                    return chain.proceed(request);
                }
            });
        }

        //build retrofit client
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(GanbookApiInterface.BASE_URL)
                .client(httpClient.build())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        ganbookApiInterface = retrofit.create(GanbookApiInterface.class);
    }

    public Call<List<AlbumsAnswer>> getAlbums(String classId, String year) {

        Log.d(TAG, "getAlbums: ");

        return ganbookApiInterface.getAlbums(classId, year);
    }

    public Call<CreateAlbumAnswer> createAlbum(String albumName, String albumDescription, String classId) {

        Log.d(TAG, "createAlbum: ");

        return ganbookApiInterface.createAlbum(albumName,albumDescription, classId);
    }

    public Call<SuccessAnswer> updateAlbumLike(String albumId) {

        Log.d(TAG, "updateAlbumLike: ");

        return ganbookApiInterface.updateAlbumLike(albumId);
    }

    public Call<List<PictureAnswer>> getPictures(String albumId, String active) {

        Log.d(TAG, "getPictures: ");

        return ganbookApiInterface.getPictures(albumId, active);
    }

    public Call<SuccessAnswer> deleteAlbum(String albumId) {

        Log.d(TAG, "deleteAlbum: ");

        return ganbookApiInterface.deleteAlbum(albumId);
    }

    public Call<SuccessAnswer> editAlbum(String albumId, String newAlbumName) {

        Log.d(TAG, "editAlbum: ");

        return ganbookApiInterface.editAlbum(albumId, newAlbumName);
    }

    public Call<OnPostS3Answer> sendS3Results(String albumId, String ganId, String name, String duration,
                                              String createDate) {

        Log.d(TAG, "sendS3Results: ");

        return ganbookApiInterface.sendS3Results(albumId, ganId, name, duration, true, createDate);
    }

    public Call<SuccessAnswer> pushAfterUpload(String albumId, String classId, int numSuccess, String isVideo) {

        Log.d(TAG, "pushAfterUpload: ");

        return ganbookApiInterface.pushAfterUpload(albumId, classId, numSuccess, UUID.randomUUID().toString(),
                isVideo);
    }

    public Call<OKAnswer> deletePictures(String pictureIds) {

        Log.d(TAG, "deletePictures: ");

        return ganbookApiInterface.deletePictures(pictureIds);
    }

    public Call<SuccessAnswer> updatePicFavorite(String picId) {

        Log.d(TAG, "updatePicFavorite: ");

        return ganbookApiInterface.updatePicFavorite(picId);
    }

    public Call<SuccessAnswer> updateAlbumView(String albumId, String userId, int numUnseenPhotos) {

        Log.d(TAG, "updateAlbumView: album id = " + albumId + " unseen photos = " + numUnseenPhotos);

        return ganbookApiInterface.updateAlbumView(albumId, userId, numUnseenPhotos);
    }

    public Call<MessageAnswer> getMessages(String classId, String year) {

        Log.d(TAG, "getMessages: ");

        return ganbookApiInterface.getMessages(classId, year);
    }

    public Call<SuccessAnswer> updateReadMessage(String parentId, String classId, String lastMessageId) {

        Log.d(TAG, "updateReadMessage: ");

        return ganbookApiInterface.updateReadMessage(parentId, classId, lastMessageId);
    }

    public Call<List<ReadMessageAnswer>> getReadMessage(String messageId, String classId) {

        Log.d(TAG, "getReadMessage: ");

        return ganbookApiInterface.getReadMessage(messageId, classId);
    }

    public Call<MessageStatus> createMessage(String text, String classId) {

        Log.d(TAG, "createMessage: ");

        return ganbookApiInterface.createMessage(text, classId);
    }

    public Call<List<GetParentAnswer>> getParents(String classId, String year) {

        Log.d(TAG, "getParents: ");

        return ganbookApiInterface.getParents(classId, year);
    }

    public Call<SuccessAnswer> activeKid(String kidId) {

        Log.d(TAG, "activeKid: ");

        return ganbookApiInterface.activeKid(kidId);
    }

    public Call<SuccessAnswer> updateClass(String kidId, String classId) {

        Log.d(TAG, "updateClass: ");

        return ganbookApiInterface.updateClass(kidId, classId);
    }

    public Call<SuccessAnswer> setClass(String kidId, String classId) {

        Log.d(TAG, "setClass: ");

        return ganbookApiInterface.setClass(kidId, classId);
    }

    public Call<List<GetUserKids_Response>> getUserKids(String userId) {

        Log.d(TAG, "getUserKids: ");

        return ganbookApiInterface.getUserKids(userId);
    }

    public Call<List<EventsAnswer>> getEvents(String classId) {

        Log.d(TAG, "getEvents: ");

        return ganbookApiInterface.getEvents(classId, "1");
    }

    public Call<List<EventsAnswer>> createEvent(RequestBody requestBody) {

        Log.d(TAG, "createEvent: ");

        return ganbookApiInterface.createEvent(requestBody);
    }

    public Call<List<EventsAnswer>> updateEvent(RequestBody requestBody) {

        Log.d(TAG, "updateEvent: ");

        return ganbookApiInterface.updateEvent(requestBody);
    }
    
    public Call<SuccessAnswer> deleteEvent(String eventId) {

        Log.d(TAG, "deleteEvent: ");

        return ganbookApiInterface.deleteEvent(eventId);
    }

    public Call<SuccessAnswer> updateGanPermission(String ganId, String type, String active) {

        Log.d(TAG, "updateGanPermission: ");

        return ganbookApiInterface.updateGanPermission(ganId, type, active);
    }

    public Call<SuccessAnswer> updateMessage(String messageId, String text, String classId) {

        Log.d(TAG, "updateMessage: ");

        return ganbookApiInterface.updateMessage(messageId, text, classId);
    }
}

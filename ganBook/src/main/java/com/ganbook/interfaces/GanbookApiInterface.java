package com.ganbook.interfaces;


import androidx.annotation.Keep;

import com.ganbook.communication.json.GetUserKids_Response;
import com.ganbook.communication.json.InstitutionLogoResponse;
import com.ganbook.communication.json.TeacherPhotoResponse;
import com.ganbook.communication.json.createalbumcomment_Response;
import com.ganbook.communication.json.getalbumcomments_response;
import com.ganbook.communication.json.transmitter.JsonTransmitter;
import com.ganbook.models.AlbumViewerModel;
import com.ganbook.models.AlbumsAnswer;
import com.ganbook.models.CreateAlbumAnswer;
import com.ganbook.models.CreateDrawingAnswer;
import com.ganbook.models.DrawingAnswer;
import com.ganbook.models.EventModel;
import com.ganbook.models.GetParentAnswer;
import com.ganbook.models.MeetingAttendeesModel;
import com.ganbook.models.MeetingEventModel;
import com.ganbook.models.MessageAnswer;
import com.ganbook.models.MessageStatus;
import com.ganbook.models.OKAnswer;
import com.ganbook.models.OnPostS3Answer;
import com.ganbook.models.PictureAnswer;
import com.ganbook.models.StudentModel;
import com.ganbook.models.SuccessAnswer;
import com.ganbook.models.TimeSlotModel;
import com.ganbook.models.UserAttachmentModel;
import com.ganbook.models.answers.EventsAnswer;
import com.ganbook.models.answers.ReadMessageAnswer;

import java.util.List;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by dmytro_vodnik on 6/3/16.
 * working on ganbook1 project
 */
@Keep
public interface GanbookApiInterface {

    String BASE_URL = JsonTransmitter.BASE_URL;
//            "https://api.ganbook.co.il/index.php/api/ganbook/";
    //public static final String BASE_URL = "http://ganbook.develop-soft.com/index.php/api/ganbook/";


    String PICTURE_HOST = JsonTransmitter.PICTURE_HOST; //"http://s3.ganbook.co.il/ImageStore/";
    String DRAWING_HOST = "http://s3.ganbook.co.il/Drawings/";
    /**
     * Send to server uploaded to S3 photo
     * @param album_id - album id of photo
     * @param gan_id - gan id
     * @param name - name of FULL SIZE!!! photo
     * @param duration - duration (if video)
     * @param create_date - create date of photo
     * @return {@link SuccessAnswer}
     */
    @FormUrlEncoded
    @POST("onposts3.json")
    Call<OnPostS3Answer> sendS3Results(@Field("album_id") String album_id,
                                       @Field("gan_id") String gan_id,
                                       @Field("name") String name,
                                       @Field("duration") String duration,
                                       @Field("android") boolean isAndroid,
                                       @Field("create_date") String create_date);

    /**
     * Method which retrieve form server all albums
     * @param classId - id of class where located album
     * @param year - year for retreive
     * @return list of {@link AlbumsAnswer} class models
     */
    @GET("getalbum.json")
    Call<List<AlbumsAnswer>> getAlbums(@Query("class_id") String classId, @Query("year") String year);

    /**
     * creates new album at server
     * @param albumName - created album name
     * @param classId - created album class id
     * @return create answer
     */
    @FormUrlEncoded
    @POST("createalbum.json")
    Call<CreateAlbumAnswer> createAlbum(@Field("album_name") String albumName, @Field("album_description") String albumDescription, @Field("class_id") String classId);

    /**
     * Updates album like at server
     * @param albumId - album id
     * @return {@link SuccessAnswer} response
     */
    @FormUrlEncoded
    @POST("updatealbumlike.json")
    Call<SuccessAnswer>  updateAlbumLike(@Field("album_id") String albumId);

    /**
     * retreive all pictures by album id
     * @param albumId - album id which contains pictures
     * @param active - picture type
     * @return list of pictures/videos
     */
    @GET("getpicture.json")
    Call<List<PictureAnswer>> getPictures(@Query("album_id") String albumId, @Query("active") String active);

    /**
     * deletes album from server
     * @param albumId - album id which need to delete
     * @return boolean {@link SuccessAnswer} response
     */
    @FormUrlEncoded
    @POST("deletealbumj.json")
    Call<SuccessAnswer> deleteAlbum(@Field("album_id") String albumId);

    /**
     * changes album name on server
     * @param albumId - album id which name will be changed
     * @param albumName - new album name
     * @return boolean {@link SuccessAnswer} response
     */
    @FormUrlEncoded
    @POST("editalbum.json")
    Call<SuccessAnswer> editAlbum(@Field("album_id") String albumId, @Field("album_name") String albumName);

    /**
     * changes album description on server
     * @param albumId - album id which description will be changed
     * @param albumDescription - new album description
     * @return boolean {@link SuccessAnswer} response
     */
    @FormUrlEncoded
    @POST("editdescription.json")
    Call<SuccessAnswer> editAlbumDescription(@Field("album_id") String albumId, @Field("album_description") String albumDescription);

    /**
     * Sends message to server thats need to send push about this upload
     * @param albumId - album id which was updated
     * @param classId - class id which contant album
     * @param numSuccess - successful num of uploaded files
     * @param uploadId - id of upload
     * @param isVideo - isvideo
     * @return {@link SuccessAnswer}
     */
    @FormUrlEncoded
    @POST("pushafterupload.json")
    Call<SuccessAnswer> pushAfterUpload(@Field("album_id") String albumId, @Field("class_id") String classId,
                                        @Field("num_success") int numSuccess, @Field("upload_id") String uploadId,
                                        @Field("isVideo") String isVideo);

    /**
     * Deltetes pictures from album
     * @param pictureNames - concat ids to string which need to delete
     * @return {@link OKAnswer}
     */
    @FormUrlEncoded
    @POST("deletepicturesj.json")
    Call<OKAnswer> deletePictures(@Field("picture_names") String pictureNames);

    /**
     * updates current picture favorite state
     * @param pictureId - id of picture which need to update
     * @return {@link SuccessAnswer}
     */
    @FormUrlEncoded
    @POST("updatepicfavorite.json")
    Call<SuccessAnswer> updatePicFavorite(@Field("picture_id") String pictureId);

    /**
     * Updates user unseens photos
     * @param albumId - album id with unseen photos
     * @param userId - user which look album
     * @param numSendPhotos - num photos which user seen
     * @return {@link SuccessAnswer}
     */
    @FormUrlEncoded
    @POST("updateAlbumView.json")
    Call<SuccessAnswer> updateAlbumView(@Field("album_id") String albumId, @Field("year") String userId,
                                        @Field("num_seen_photos") int numSendPhotos);

    /**
     * Return list of messages for current class
     * @param classId - id of class
     * @param year - year to find
     * @return list of {@link com.ganbook.models.MessageModel}
     */
    @GET("getmessage.json")
    Call<MessageAnswer> getMessages(@Query("class_id") String classId, @Query("year") String year);

    /**
     * Updates views of current message
     * @param parentId - id of parent who viewd message
     * @param classId - class which contain message
     * @param lastMsgId - last!!! loaded message id
     * @return {@link SuccessAnswer}
     */
    @FormUrlEncoded
    @POST("updatereadmessage.json")
    Call<SuccessAnswer> updateReadMessage(@Field("parent_id") String parentId,
                                          @Field("class_id")String classId,
                                          @Field("last_msg_id")String lastMsgId);

    /**
     * Get parents who viewed current message
     * @param messageId - id of message to check
     * @param classId - class which contain message
     * @return List of {@link ReadMessageAnswer}
     */
    @GET("getreadmessage.json")
    Call<List<ReadMessageAnswer>> getReadMessage(@Query("message_id") String messageId,
                                                 @Query("class_id") String classId);

    /**
     * creates message
     * @param text - text of message
     * @param classId - class which contain message
     * @return {@link SuccessAnswer}
     */
    @FormUrlEncoded
    @POST("createmessage.json")
    Call<MessageStatus> createMessage(@Field("text") String text, @Field("class_id") String classId);

    /**
     * get parents list
     * @param classId - class which contain kids of parent
     * @param year - year to return
     * @return list of {@link GetParentAnswer}
     */
    @GET("getparents.json")
    Call<List<GetParentAnswer>> getParents(@Query("class_id") String classId, @Query("year") String year);

    /**
     * Activate kid of parent
     * @param kidId - id of kid
     * @return {@link SuccessAnswer}
     */
    @FormUrlEncoded
    @POST("activekid.json")
    Call<SuccessAnswer> activeKid(@Field("kid_id") String kidId);

    /**
     * updates kid class
     * @param kidId - id of kid
     * @param classId - class id which will be new, "" - for disconnect class
     * @return {@link SuccessAnswer}
     */
    @FormUrlEncoded
    @POST("updateclass.json")
    Call<SuccessAnswer> updateClass(@Field("kid_id") String kidId, @Field("class_id") String classId);

    /**
     * Set class to kid
     * @param kidId - id of kid
     * @param classId - id of class for kid
     * @return {@link SuccessAnswer}
     */
    @FormUrlEncoded
    @POST("setclass.json")
    Call<SuccessAnswer> setClass(@Field("kid_id") String kidId, @Field("class_id") String classId);

    /**
     * Get kids from user
     * @param parentId - id of user
     * @return list of {@link GetUserKids_Response}
     */
    @GET("getuserkids.json")
    Call<List<GetUserKids_Response>> getUserKids(@Query("parent_id") String parentId);

    /**
     * get events for current class
     * @param classId - id of class with events
     * @return list of {@link EventModel}
     */
    @GET("getEvents.json")
    Call<List<EventsAnswer>> getEvents(@Query("class_id") String classId, @Query("android") String a);


    /**
     * create new event
     * @param isAndroid - mark for android api
     * @param class_id - id of class with event
     * @param title - title of event
     * @param type - type of event
     * @param event_start_date - start date of event
     * @param event_end_date - end date of event
     * @param comments - comment for event
     * @param all_day - is all day
     * @param day_off - day off of event
     * @param all_kids - if for all kids
     * @param kids - array of kid ids
     * @return
     */
    @Headers("Content-Type: application/json")
    @POST("createEvent.json")
    Call<List<EventsAnswer>> createEvent(@Body RequestBody requestBody);

    /**
     * update exist event
     * @param isAndroid - mark for android api
     * @param class_id - id of class with event
     * @param title - title of event
     * @param type - type of event
     * @param event_start_date - start date of event
     * @param event_end_date - end date of event
     * @param comments - comment for event
     * @param all_day - is all day
     * @param day_off - day off of event
     * @param all_kids - if for all kids
     * @param kids - array of kid ids
     * @return
     */
    @Headers("Content-Type: application/json")
    @POST("updateEvent.json")
    Call<List<EventsAnswer>> updateEvent(@Body RequestBody requestBody);

    /**
     * deletes event from server
     * @param eventId - id of event to delete
     * @return {@link SuccessAnswer}
     */
    @FormUrlEncoded
    @POST("deleteEvent.json")
    Call<SuccessAnswer> deleteEvent(@Field("event_id") String eventId);

    /**
     *
     * @param ganId - id of gan
     * @param type - type of permission
     * @param active - is active
     * @return {@link SuccessAnswer}
     */
    @FormUrlEncoded
    @POST("updateganspermission.json")
    Call<SuccessAnswer> updateGanPermission(@Field("gan_id") String ganId, @Field("permission_type") String type,
                                            @Field("active") String active);

    @FormUrlEncoded
    @POST("updatemessage.json")
    Call<SuccessAnswer> updateMessage(@Field("message_id") String messageId, @Field("text") String text,
                                      @Field("class_id") String classId);

    @GET("getalbumcomments.json")
    Call<List<getalbumcomments_response>> getAlbumComments(@Query("album_id") String albumId);

    @FormUrlEncoded
    @POST("createalbumcomment.json")
    Call<createalbumcomment_Response> createAlbumComment(@Field("album_id") String albumId,
                                                         @Field("album_comment")String albumComment);

    @FormUrlEncoded
    @POST("deletealbumcomment.json")
    Call<SuccessAnswer> deleteAlbumComment(@Field("comment_id") String commentId);

    @FormUrlEncoded
    @POST("updatealbumcomment.json")
    Call<SuccessAnswer> updateAlbumComment(@Field("comment_id") String commentId,
                                           @Field("album_comment") String albumComment);

    @FormUrlEncoded
    @POST("updatepassword.json")
    Call<SuccessAnswer> updatePassword(@Field("old_password") String oldPassword,
                                       @Field("new_password") String new_password,
                                       @Field("lang_region") String lang_region,
                                       @Field("user_id") String user_id);

    @FormUrlEncoded
    @POST("createkid.json")
    Call<SuccessAnswer> createKid(@Field("gan_code") String gan_code, @Field("parent_name") String parent_name,
                                  @Field("parent_id") String parent_id, @Field("parent_address") String parent_address,
                                  @Field("parent_phone") String parent_phone, @Field("parent_mobile") String parent_mobile,
                                  @Field("parent_city") String parent_city, @Field("kid_name") String kid_name,
                                  @Field("kid_gender") String kid_gender, @Field("kid_bd") String kid_bd,
                                  @Field("kid_pic") String kidPic, @Field("android_pic_path") String remotePath);

    @FormUrlEncoded
    @POST("updatekidpic.json")
    Call<SuccessAnswer> updateKidPic(@Field("kid_id") String kidId,
                                     @Field("picture_name") String pictureName);

    @FormUrlEncoded
    @POST("kindergartenlogo.json")
    Call<SuccessAnswer> uploadInstitutionLogo(@Field("gans_id") String ganId,
                                     @Field("kindergarten_logo") String pictureName);

    @FormUrlEncoded
    @POST("teacherphoto.json")
    Call<SuccessAnswer> uploadTeacherPhoto(@Field("gan_id") String ganId,
                                              @Field("teacher_photo") String teacherPhoto);

    @GET("getkindergartenlogo.json")
    Call<List<InstitutionLogoResponse>> getKindergartenLogo(@Query("gans_id") String ganId);

    @GET("teacherphotolink.json")
    Call<List<TeacherPhotoResponse>> getTeacherPhoto(@Query("gan_id") String ganId);

    @FormUrlEncoded
    @POST("albumthumbnail.json")
    Call<SuccessAnswer> uploadAlbumThumbnail(@Field("album_id") String albumId,
                                              @Field("pic_name") String pictureName);

    @GET("albumviewers.json")
    Call<List<AlbumViewerModel>> getAlbumViewers(@Query("album_id") String albumId);

    @FormUrlEncoded
    @POST("kidsattendance.json")
    Call<SuccessAnswer> updateKidAttendance(@Field("kid_id") String kidId, @Field("gan_id") String ganId, @Field("attendance") String attendance);

    @GET("getstaffpermission.json")
    Call<ResponseBody> getStaffPermissions(@Query("staff_id") String staffId, @Query("class_id") String classId, @Query("kindergarten_id") String ganId);

    @FormUrlEncoded
    @POST("staffpermissions.json")
    Call<SuccessAnswer> updateStaffPermission(@Field("staff_id") String staffId, @Field("camera_permission") String attendance, @Field("class_id") String classId, @Field("kindergarten_id") String ganId);

    @FormUrlEncoded
    @POST("createdrawingalbum.json")
    Call<CreateDrawingAnswer> createDrawingAlbum(@Field("album_name") String albumName, @Field("kid_id") String kidId);

    @FormUrlEncoded
    @POST("adddrawingpicture.json")
    Call<SuccessAnswer> uploadDrawings(@Field("kid_album_id") String drawingAlbumId, @Field("drawing_name") String drawingName, @Field("drawing_description") String drawingDescription, @Field("audio_name") String drawingAudio);

    @GET("drawingsfromalbum.json")
    Call<List<DrawingAnswer>> getDrawings(@Query("drawings_album_id") String drawingsAlbumId);

    @GET("studentlist.json")
    Call<List<StudentModel>> getStudentList(@Query("student_class") String studentClassId);

    @GET("studentinfo.json")
    Call<StudentModel> getStudentInfo(@Query("student_id") String studentId);

    @FormUrlEncoded
    @POST("adddocument.json")
    Call<SuccessAnswer> uploadDocument(@Field("class_id") String classId, @Field("document_title") String documentTitle, @Field("document_description") String documentDescription, @Field("document_name") String documentName);

    @GET("documentdata.json")
    Call<List<UserAttachmentModel>> getDocuments(@Query("class_id") String classId);

    @FormUrlEncoded
    @POST("documentdatadelete.json")
    Call<SuccessAnswer> deleteDocument(@Field("class_id") String classId, @Field("document_id") String documentId);

    @FormUrlEncoded
    @POST("picdescription.json")
    Call<SuccessAnswer> addPhotoDescription(@Field("pic_id") String picId, @Field("album_id") String albumId, @Field("pic_desc") String pictureDescription);

    @FormUrlEncoded
    @POST("recaptchacheck.json")
    Call<SuccessAnswer> reCaptchaCheck(@Field("recaptcha_secret") String recaptchaSecret, @Field("recaptcha_response") String recaptchaToken);

    @FormUrlEncoded
    @POST("meetingevent.json")
    Call<SuccessAnswer> addMeetingEvent(@Field("meeting_title") String meetingTitle, @Field("meeting_start") String meetingStart, @Field("meeting_end") String meetingEnd, @Field("meeting_class") String meetingClass, @Field("meeting_comments") String meetingComments, @Field("meeting_duration") String meetingDuration);

    @FormUrlEncoded
    @POST("meetingsattendees.json")
    Call<SuccessAnswer> reserveMeeting(@Field("id") String timeSlotId, @Field("parent_id") String parentId, @Field("parent_name") String parentName);

    @GET("meetingeventget.json")
    Call<List<MeetingEventModel>> getMeetingEvent(@Query("class_id") String classId, @Query("meeting_id") String meetingId);

    @GET("meetingsattendeesget.json")
    Call<List<MeetingAttendeesModel>> getMeetingAttendees(@Query("slot_id") String slotId);

    @GET("meetings.json")
    Call<List<MeetingEventModel>> getMeetings(@Query("class_id") String classId);

    @FormUrlEncoded
    @POST("meetingdelete.json")
    Call<SuccessAnswer> deleteMeeting(@Field("meeting_id") String meetingId);

    @FormUrlEncoded
    @POST("sendpushameeting.json")
    Call<SuccessAnswer> createMeetingPush(@Field("user_id") String userId, @Field("class_id") String classId, @Field("app_name") String appName, @Field("push_text") String pushText);

    @FormUrlEncoded
    @POST("deletedrawingpics.json")
    Call<SuccessAnswer> deleteDrawings(@Field("pictures_ids") String pictureIds);
}

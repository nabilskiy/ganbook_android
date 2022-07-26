package com.ganbook.communication.json;

import com.ganbook.user.User;
import com.ganbook.utils.DateFormatter;
import com.google.gson.annotations.SerializedName;

public class getalbumcomments_response extends BaseResponse {

    @SerializedName("comment_id")
    public String comment_id; //": "15689",
    @SerializedName("album_comment")
    public String album_comment; //": "watermark",
    @SerializedName("user_id")
    public String user_id; //": "1589277815689-1826969173.jpeg.jpeg",
    @SerializedName("comment_date")
    public String comment_date; //": "2015-01-26 17:11:18",
    @SerializedName("kid_pic")
    public String kid_pic; //": "2",
    @SerializedName("user_first_name")
    public String user_first_name; //": "0",
    @SerializedName("user_type")
    public String user_type; //": "5"
    @SerializedName("kid_name")
    public String kid_name;
    @SerializedName("kid_gender")
    public String kid_gender;


    public static getalbumcomments_response createNewComment(String newMsgText, String comment_id) {
        getalbumcomments_response comment = new getalbumcomments_response();

        comment.comment_id = comment_id;
        comment.album_comment = newMsgText;
        comment.user_id = User.getUserId();
        comment.comment_date = DateFormatter.getCurrent_In_Message_Format();

        comment.user_type = User.current.type;

        if(User.isParent()) {
            comment.kid_pic = User.current.getCurrentKidPic();
            comment.kid_name = User.current.getCurrentKidName();
            comment.kid_gender = User.current.getCurrentKidGender();
        }
        comment.user_first_name = User.current.first_name;

        return comment;
    }

    @Override
    public String toString() {
        return "getalbumcomments_response{" +
                "comment_id='" + comment_id + '\'' +
                ", album_comment='" + album_comment + '\'' +
                ", user_id='" + user_id + '\'' +
                ", comment_date='" + comment_date + '\'' +
                ", kid_pic='" + kid_pic + '\'' +
                ", user_first_name='" + user_first_name + '\'' +
                ", user_type='" + user_type + '\'' +
                ", kid_name='" + kid_name + '\'' +
                ", kid_gender='" + kid_gender + '\'' +
                '}';
    }

    public String getComment_id() {
        return comment_id;
    }

    public void setComment_id(String comment_id) {
        this.comment_id = comment_id;
    }

    public String getAlbum_comment() {
        return album_comment;
    }

    public void setAlbum_comment(String album_comment) {
        this.album_comment = album_comment;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getComment_date() {
        return comment_date;
    }

    public void setComment_date(String comment_date) {
        this.comment_date = comment_date;
    }

    public String getKid_pic() {
        return kid_pic;
    }

    public void setKid_pic(String kid_pic) {
        this.kid_pic = kid_pic;
    }

    public String getUser_first_name() {
        return user_first_name;
    }

    public void setUser_first_name(String user_first_name) {
        this.user_first_name = user_first_name;
    }

    public String getUser_type() {
        return user_type;
    }

    public void setUser_type(String user_type) {
        this.user_type = user_type;
    }

    public String getKid_name() {
        return kid_name;
    }

    public void setKid_name(String kid_name) {
        this.kid_name = kid_name;
    }

    public String getKid_gender() {
        return kid_gender;
    }

    public void setKid_gender(String kid_gender) {
        this.kid_gender = kid_gender;
    }
}

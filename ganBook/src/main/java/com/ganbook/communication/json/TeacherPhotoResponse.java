package com.ganbook.communication.json;

import com.google.gson.annotations.SerializedName;

public class TeacherPhotoResponse {


    @SerializedName("teacher_photo")
    String teacherPhoto;

    public String getTeacherPhoto() {
        return teacherPhoto;
    }

    public void setTeacherPhoto(String teacherPhoto) {
        this.teacherPhoto = teacherPhoto;
    }

    @Override
    public String toString() {
        return "GET TEACHER PHOTO RESPONSE{" +
                "kindergarten logo='" + teacherPhoto + '\'' +
                '}';
    }
}

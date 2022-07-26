package com.ganbook.models;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;


public class StudentModel implements Parcelable {

    @SerializedName("student_id")
    private String studentId;

    @SerializedName("first_name")
    private String studentFirstName;

    @SerializedName("last_name")
    private String studentLastName;

    @SerializedName("mail")
    private String studentMail;

    @SerializedName("mobile_phone")
    private String studentMobile;

    @SerializedName("home_phone")
    private String studentHomeNumber;

    @SerializedName("city")
    private String studentCity;

    @SerializedName("address")
    private String studentAddress;

    @SerializedName("gender")
    private String studentGender;

    @SerializedName("birth_date")
    private String studentBirthDate;

    @SerializedName("pic")
    private String studentImage;

    @SerializedName("class_id")
    private String studentClassId;



    public StudentModel() {}

    public StudentModel(Parcel in) {
        studentId = in.readString();
        studentFirstName = in.readString();
        studentLastName = in.readString();
        studentMobile = in.readString();
        studentMail = in.readString();
        studentAddress = in.readString();
        studentGender = in.readString();
        studentBirthDate = in.readString();
        studentClassId = in.readString();
        studentImage = in.readString();
        studentCity = in.readString();
        studentHomeNumber = in.readString();
    }

    public static final Creator<StudentModel> CREATOR = new Creator<StudentModel>() {
        @Override
        public StudentModel createFromParcel(Parcel in) {
            return new StudentModel(in);
        }

        @Override
        public StudentModel[] newArray(int size) {
            return new StudentModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(studentId);
        parcel.writeString(studentFirstName);
        parcel.writeString(studentLastName);
        parcel.writeString(studentMobile);
        parcel.writeString(studentMail);
        parcel.writeString(studentAddress);
        parcel.writeString(studentGender);
        parcel.writeString(studentBirthDate);
        parcel.writeString(studentClassId);
        parcel.writeString(studentImage);
        parcel.writeString(studentCity);
        parcel.writeString(studentHomeNumber);
    }

    public String getStudentFirstName() {
        return studentFirstName;
    }

    public void setStudentFirstName(String studentFirstName) {
        this.studentFirstName = studentFirstName;
    }

    public String getStudentImage() {
        return studentImage;
    }

    public void setStudentImage(String studentImage) {
        this.studentImage = studentImage;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getStudentLastName() {
        return studentLastName;
    }

    public void setStudentLastName(String studentLastName) {
        this.studentLastName = studentLastName;
    }

    public String getStudentMail() {
        return studentMail;
    }

    public void setStudentMail(String studentMail) {
        this.studentMail = studentMail;
    }

    public String getStudentMobile() {
        return studentMobile;
    }

    public void setStudentMobile(String studentMobile) {
        this.studentMobile = studentMobile;
    }

    public String getStudentCity() {
        return studentCity;
    }

    public void setStudentCity(String studentCity) {
        this.studentCity = studentCity;
    }

    public String getStudentAddress() {
        return studentAddress;
    }

    public void setStudentAddress(String studentAddress) {
        this.studentAddress = studentAddress;
    }

    public String getStudentGender() {
        return studentGender;
    }

    public void setStudentGender(String studentGender) {
        this.studentGender = studentGender;
    }

    public String getStudentBirthDate() {
        return studentBirthDate;
    }

    public void setStudentBirthDate(String studentBirthDate) {
        this.studentBirthDate = studentBirthDate;
    }

    public String getStudentClassId() {
        return studentClassId;
    }

    public void setStudentClassId(String studentClassId) {
        this.studentClassId = studentClassId;
    }

    public String getStudentHomeNumber() {
        return studentHomeNumber;
    }

    public void setStudentHomeNumber(String studentHomeNumber) {
        this.studentHomeNumber = studentHomeNumber;
    }

    @Override
    public String toString() {
        return "Student Model{" +
                "id=" + studentId +
                ", studentName='" + studentFirstName + studentLastName + '\'' +
                ", studentMail='" + studentMail + '\'' +
                ", studentMobile=" + studentMobile +
                ", studentHomeNumber=" + studentHomeNumber +
                ", studentAddress='" + studentAddress + '\'' +
                ", studentImage=" + studentImage +
                ", studentBirthDate=" + studentBirthDate +
                ", studentGender='" + studentGender + '\'' +
                '}';
    }
}

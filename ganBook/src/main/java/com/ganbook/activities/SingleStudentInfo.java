package com.ganbook.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;

import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.ganbook.models.StudentModel;
import com.ganbook.share.ShareManager;
import com.ganbook.ui.CircleImageView;
import com.ganbook.validate.Validator;
import com.project.ganim.R;
import com.squareup.picasso.Picasso;

public class SingleStudentInfo extends BaseAppCompatActivity implements View.OnClickListener {

    private CircleImageView studentImage;
    private TextView studentMobile, studentHomeNumber, studentEmail, studentAddress, studentCity, studentName;
    private ImageButton sendSms, makeCall, makeKindergartenCall, sendEmail, showAddress;
    private StudentModel studentModel;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_student_info_layout);
        setActionBar(getString(R.string.student_info_text), true);

        studentName = findViewById(R.id.student_name);
        studentImage = findViewById(R.id.student_profile_image);
        studentMobile = findViewById(R.id.student_mobile_number);
        studentHomeNumber = findViewById(R.id.student_home_number);
        studentEmail = findViewById(R.id.student_email);
        studentAddress = findViewById(R.id.student_address);
        studentCity = findViewById(R.id.student_city);
        sendSms = findViewById(R.id.send_sms);
        makeCall = findViewById(R.id.make_call);
        makeKindergartenCall = findViewById(R.id.kindergarden_phone);
        sendEmail = findViewById(R.id.send_email);
        showAddress = findViewById(R.id.show_address);

        sendSms.setOnClickListener(this);
        makeCall.setOnClickListener(this);
        makeKindergartenCall.setOnClickListener(this);
        sendEmail.setOnClickListener(this);
        showAddress.setOnClickListener(this);

        if(getIntent().getParcelableExtra("studentInfo") != null) {
            studentModel = getIntent().getParcelableExtra("studentInfo");

            studentName.setText(replaceNullString(studentModel.getStudentFirstName()) + " " + replaceNullString(studentModel.getStudentLastName()));
            studentMobile.setText(studentModel.getStudentMobile());
            studentHomeNumber.setText(studentModel.getStudentHomeNumber());
            studentEmail.setText(studentModel.getStudentMail());
            studentAddress.setText(studentModel.getStudentAddress());
            studentCity.setText(studentModel.getStudentCity());

            if(!studentModel.getStudentImage().isEmpty() && studentModel.getStudentImage() != null) {
                Picasso.with(this)
                        .load("http://s3.ganbook.co.il/ImageStore/users/" + studentModel.getStudentImage() + ".png")
                        .into(studentImage);
            } else {
                Picasso.with(this)
                        .load(R.drawable.student_icon)
                        .into(studentImage);
            }
        }

    }


    @Override
    public void onClick(View v) {
        String mobile = studentModel.getStudentMobile();
        String phone = studentModel.getStudentHomeNumber();
        String email = studentModel.getStudentMail();
        switch (v.getId()) {
            case R.id.send_sms:
                sendSms(mobile);
                break;
            case R.id.send_whatsapp:
                sendWhatsapp(mobile);
                break;
            case R.id.make_call:
                makeCall(mobile);
                break;
            case R.id.kindergarden_phone:
                makeCall(phone);
                break;
            case R.id.send_email:
                sendEmail(email);
                break;
            case R.id.show_address:
                // no op
                break;
            default:
        }
    }

    private void sendEmail(String emailAddress) {
        ShareManager.openShareMenu(SingleStudentInfo.this, -1, -1, "", emailAddress, null);
    }

    private void makeCall(String phoneNo) {
        if (!Validator.validPhoneNo(phoneNo)) {
            return;
        }
        phoneNo = phoneNo.trim();
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNo));
        startActivity(intent);
    }

    private void sendSms(String phoneNo) {
        if (!Validator.validPhoneNo(phoneNo)) {
            return;
        }
        phoneNo = phoneNo.trim();
        String smsText = "";
        String uri= "smsto:" + phoneNo;
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse(uri));
        intent.putExtra("sms_body", smsText);
        intent.putExtra("compose_mode", true);
        startActivity(intent);
    }

    private void sendWhatsapp(String phoneNo) {
        if (!Validator.validPhoneNo(phoneNo)) {
            return;
        }
        phoneNo = phoneNo.trim();

        Intent i = new Intent(Intent.ACTION_SENDTO);
        i.setDataAndType(Uri.parse("smsto:" + phoneNo), "text/plain");
        i.setPackage("com.whatsapp");
        startActivity(i);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private String replaceNullString(String input) {
        return input == null ? "" : input;
    }
}

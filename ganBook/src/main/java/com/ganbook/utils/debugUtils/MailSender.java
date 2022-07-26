package com.ganbook.utils.debugUtils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by dmytro_vodnik on 9/6/16.
 * working on ganbook1 project
 */
public class MailSender {

    private static final String TAG = MailSender.class.getName();

    public static void writeStringAsFile(Context context, final String fileContents, String fileName) {

        String targetFilePath = Environment.getExternalStorageDirectory().getPath() + File.separator +
                "tmp" + File.separator;
        File dir = new File (targetFilePath);
        dir.mkdirs();
        File file = new File(dir, fileName);

        try {
            FileWriter out = new FileWriter(file);
            out.write(fileContents);
            out.close();
        } catch (IOException e) {
            Log.e(TAG, "error while write file " + Log.getStackTraceString(e));
        }

        Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);

        emailIntent.setType("message/rfc822")
                .putExtra(Intent.EXTRA_EMAIL, new String[]{"tised.dev@gmail.com"})
                .putExtra(android.content.Intent.EXTRA_SUBJECT, "File path array")
                .putExtra(android.content.Intent.EXTRA_TEXT, "Hope it will help");

        Uri attachmentUri = Uri.parse(targetFilePath + fileName);

        emailIntent.putExtra(android.content.Intent.EXTRA_STREAM, Uri.parse("file://" + attachmentUri));
        context.startActivity(Intent.createChooser(emailIntent , "Send email..."));
    }

    public static void writeStringAsFile(Context context, final String fileContents, String fileName,
                                         String subject) {

        String targetFilePath = Environment.getExternalStorageDirectory().getPath() + File.separator +
                "tmp" + File.separator;
        File dir = new File (targetFilePath);
        dir.mkdirs();
        File file = new File(dir, fileName);

        try {
            FileWriter out = new FileWriter(file);
            out.write(fileContents);
            out.close();
        } catch (IOException e) {
            Log.e(TAG, "error while write file " + Log.getStackTraceString(e));
        }

        Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);

        emailIntent.setType("message/rfc822")
                .putExtra(Intent.EXTRA_EMAIL, new String[]{"tised.dev@gmail.com"})
                .putExtra(android.content.Intent.EXTRA_SUBJECT, subject)
                .putExtra(android.content.Intent.EXTRA_TEXT, "Hope it will help");

        Uri attachmentUri = Uri.parse(targetFilePath + fileName);

        emailIntent.putExtra(android.content.Intent.EXTRA_STREAM, Uri.parse("file://" + attachmentUri));
        context.startActivity(Intent.createChooser(emailIntent , "Send email..."));
    }

}

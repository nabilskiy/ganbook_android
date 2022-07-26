package com.ganbook.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import androidx.annotation.NonNull;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import com.ganbook.amazinglist.special.CustomToast;
import com.ganbook.communication.ICompletionHandler;
import com.ganbook.communication.datamodel.ResultObj;
import com.ganbook.communication.json.transmitter.JsonTransmitter;
import com.ganbook.user.User;
import com.ganbook.validate.Validator;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.safetynet.SafetyNet;
import com.google.android.gms.safetynet.SafetyNetApi;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.project.ganim.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by dmytro_vodnik on 7/13/16.
 * working on ganbook1 project
 */
public class AlertUtils {

    SweetAlertDialog alert;
    private static final String SAFETY_NET_API_SITE_KEY = "6LdbIpAUAAAAABEBGPDMiXwjYbuNRS2ez1Ktt7Ye";


    public static SweetAlertDialog createProgressDialog(Context context, String titleText) {

        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);

        sweetAlertDialog.setTitleText(titleText);

        return sweetAlertDialog;
    }

    public static SweetAlertDialog showAlertMessage(Context context, String message, String title) {

        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE);

        sweetAlertDialog.setTitleText(title);
        sweetAlertDialog.setContentText(message);
        sweetAlertDialog.showCancelButton(false);
        sweetAlertDialog.setConfirmText(context.getString(R.string.btn_ok));

        sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {

                sweetAlertDialog.dismiss();
            }
        });

        return sweetAlertDialog;
    }

    public static void showMessage(final Activity activity, final int stringId)
    {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
                dialogBuilder.setCancelable(false);
                String msg = activity.getString(stringId);

                dialogBuilder.setTitle(msg);
                dialogBuilder.setIcon(R.drawable.ic_launcher);

                dialogBuilder.setPositiveButton(R.string.popup_btn_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                dialogBuilder.show();
            }
        });
    }

    public static void onForgotPassword(final Activity a) {

        AlertDialog.Builder builder = new AlertDialog.Builder(a);
        builder.setTitle(R.string.lost_pass_title);
        builder.setMessage(R.string.on_lost_pass);
        final EditText input = new EditText(a);
        input.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        String email = User.getEmail();
        input.setText(email);
        builder.setView(input);
        builder.setPositiveButton(R.string.send_btn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                String email_addr = input.getText().toString().trim();
                if (Validator.validEmail(email_addr)) {
                    final SweetAlertDialog progress = createProgressDialog(a, a.getString(R.string.operation_proceeding));
                    //progress.show();
                    reCaptcha(a, email_addr);
                    //progress.hide();

                } else {
                    CustomToast.show(a, R.string.bad_email_msg);
                }
            }
        });
        builder.setNegativeButton(R.string.cancel_btn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // no op
            }
        });

        builder.show();
    }


    private static void reCaptcha(final Activity activity, final String email) {

        SafetyNet.getClient(activity).verifyWithRecaptcha(SAFETY_NET_API_SITE_KEY)
                .addOnSuccessListener(activity, new OnSuccessListener<SafetyNetApi.RecaptchaTokenResponse>() {
                    @Override
                    public void onSuccess(SafetyNetApi.RecaptchaTokenResponse recaptchaTokenResponse) {
                        if (!recaptchaTokenResponse.getTokenResult().isEmpty()) {

                            post("https://www.google.com/recaptcha/api/siteverify", recaptchaTokenResponse.getTokenResult(), "6LdbIpAUAAAAAJ9WdZyaPQJw1YZzspA5NtoBVwI5", new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {

                                }

                                @Override
                                public void onResponse(Call call, Response response) throws IOException {

                                    try {
                                        JSONObject object = new JSONObject(response.body().string());
                                        boolean success = object.getBoolean("success");

                                        if (success) {

                                            JsonTransmitter.send_resetpassword(email,
                                                    new ICompletionHandler() {
                                                        @Override
                                                        public void onComplete(ResultObj result) {
                                                            Log.e("RESET PASSWORD", result.result);
                                                            if (!result.succeeded) {
                                                                CustomToast.show(activity, result.result);
                                                            } else {
                                                                CustomToast.show(activity, R.string.lost_pass_notif);
                                                            }
                                                        }
                                                    });
                                        }

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });


                        }
                    }
                })
                .addOnFailureListener(activity, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (e instanceof ApiException) {
                            ApiException apiException = (ApiException) e;
                            Log.d("LoginActivity", CommonStatusCodes.getStatusCodeString(apiException.getStatusCode()));
                        } else {
                            Log.d("LoginActivity", "Unknown type of error: " + e.getMessage());
                        }
                    }
                });
    }

    private static Call post(String url, String response, String secret, Callback callback) {
        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder().add("response", response).add("secret", secret).build();
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }

    public static ProgressDialog buildGanbookProgressDialod(Context context, String msg) {

        ProgressDialog dialog = new ProgressDialog(context, R.style.MyTheme1);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.custom_progress_layout, null);
        TextView progress_msg = (TextView) v.findViewById(R.id.progress_msg);
        progress_msg.setText(msg);

        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.setCancelable(false);
        dialog.setMessage(msg);
        dialog.setIndeterminate(true);
        dialog.show();
        dialog.setContentView(v);

        return dialog;
    }
}

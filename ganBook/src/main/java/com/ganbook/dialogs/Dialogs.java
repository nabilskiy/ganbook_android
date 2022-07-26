package com.ganbook.dialogs;

import android.app.Activity;
import android.content.Context;
import android.view.ViewGroup;

import com.irozon.sneaker.Sneaker;
import com.project.ganim.R;

import libs.mjn.prettydialog.PrettyDialog;
import libs.mjn.prettydialog.PrettyDialogCallback;

public class Dialogs {

    public static void errorDialogWithButton(Context context, String title, String message, String buttonText) {
        final PrettyDialog pDialog = new PrettyDialog(context);
        pDialog.setTitle(title)
                .setIcon(R.drawable.pdlg_icon_info)
                .setIconTint(R.color.pdlg_color_red)
                .setMessage(message)
                .addButton(
                        buttonText,
                        R.color.pdlg_color_white,
                        R.color.pdlg_color_red,
                        new PrettyDialogCallback() {
                            @Override
                            public void onClick() {
                                pDialog.dismiss();
                            }
                        }
                )
                .show();
    }

    public static void successDialogWithButton(Context context, String title, String message, String okBtnText, String cancelBtnText, final ButtonDialogInterface dialogInterface) {
        final PrettyDialog pDialog = new PrettyDialog(context);
        pDialog.setTitle(title)
                .setIcon(R.drawable.ic_launcher)
                .setMessage(message)
                .addButton(
                        okBtnText,
                        R.color.pdlg_color_white,
                        R.color.pdlg_color_blue,
                        new PrettyDialogCallback() {
                            @Override
                            public void onClick() {
                                dialogInterface.onButtonOkClicked();
                                pDialog.dismiss();
                            }
                        }
                )
                .addButton(cancelBtnText,
                        R.color.pdlg_color_white,
                        R.color.pdlg_color_gray,
                        new PrettyDialogCallback() {
                            @Override
                            public void onClick() {
                                pDialog.dismiss();
                            }
                        })
                .show();
    }

    public static void sneakerSuccess(Activity activity, String title, String message) {
        Sneaker.with(activity)
                .setTitle(title)
                .setDuration(4000)
                .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                .autoHide(true)
                .setMessage(message)
                .sneakSuccess();
    }

    public static void sneakerLogout(Activity activity, String title, String message) {
        Sneaker.with(activity)
                .setTitle(title)
                .setDuration(4000)
                .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                .autoHide(true)
                .setMessage(message)
                .setIcon(R.drawable.ic_launcher)
                .sneak(R.color.blue_light);
    }

    public static void sneakersNoInternetConnection(Activity activity) {
        Sneaker.with(activity)
                .setTitle("No internet!", R.color.white)
                .setMessage("There is no internet connection! Turn on WiFi or Mobile data!", R.color.white)
                .setDuration(5000)
                .autoHide(true)
                .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                .setIcon(R.drawable.wifioff, R.color.white, false)
                .sneak(R.color.mdtp_dark_gray);

    }


    public interface ButtonDialogInterface {

        void onButtonOkClicked();
    }

}

package com.ganbook.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.ganbook.user.User;
import com.project.ganim.R;


public class PhotoDescriptionDialog extends Dialog {
    OnCloseListener listener;
    String text;

    public interface OnCloseListener { void onClose(String text); }

    public PhotoDescriptionDialog(Context context, String text, OnCloseListener listener) {
        super(context, R.style.dialogPin);
        this.listener = listener;
        this.text = text;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.photo_description_dialog);
        ((EditText) findViewById(R.id.text)).setText(text);

        if (User.isTeacher() || User.isStaff()) {
            findViewById(R.id.left_button).setVisibility(View.VISIBLE);
            ((EditText) findViewById(R.id.text)).setEnabled(true);

            findViewById(R.id.left_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideKbd();
                    dismiss();
                    if (listener != null)
                        listener.onClose(((EditText) findViewById(R.id.text)).getText().toString());
                }
            });
        } else {
            ((EditText) findViewById(R.id.text)).setEnabled(false);
            ((EditText) findViewById(R.id.text)).clearFocus();
            ((EditText) findViewById(R.id.text)).setFocusable(false);
            ((EditText) findViewById(R.id.text)).setFocusableInTouchMode(false);
            findViewById(R.id.left_button).setVisibility(View.GONE);
        }

        findViewById(R.id.right_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKbd();
                dismiss();
            }
        });
    }

    private void hideKbd() {
        EditText editView = (EditText) findViewById(R.id.text);
        InputMethodManager imm = (InputMethodManager) getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editView.getWindowToken(), 0);
    }

    @Override
    public void show() {
        super.show();
    }
}


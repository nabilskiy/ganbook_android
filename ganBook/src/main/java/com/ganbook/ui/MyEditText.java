package com.ganbook.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import com.project.ganim.R;

@SuppressLint("ClickableViewAccessibility") 
public class MyEditText extends EditText {

	private Drawable imgCloseButton = getResources().getDrawable(
			R.drawable.edit_cross);

	public MyEditText(Context context) {
		super(context);
		if (!isInEditMode()) {
			init();
		}
	}

	public MyEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		if(!isInEditMode()) {
			init();
		}
	}

	public MyEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		if(!isInEditMode()) {
			init();
		}
	}

	void init() {
		imgCloseButton.setBounds(0, 0, imgCloseButton.getIntrinsicWidth(),
				imgCloseButton.getIntrinsicHeight());
		handleClearButton();
		this.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				MyEditText et = MyEditText.this;
				if (et.getCompoundDrawables()[2] == null) {
					return false;
				}

				if (event.getAction() != MotionEvent.ACTION_UP) {
					return false;
				}

				if (event.getX() > et.getWidth() - et.getPaddingRight() - imgCloseButton.getIntrinsicWidth()) {
					et.setText("");
					MyEditText.this.handleClearButton();
				}
				return false;
			}
		});

		this.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				MyEditText.this.handleClearButton();
			}

			@Override
			public void afterTextChanged(Editable arg0) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}
		});
	}

	void handleClearButton() {
		if (this.getText().toString().equals("")) {
			this.setCompoundDrawables(this.getCompoundDrawables()[0],
					this.getCompoundDrawables()[1], null,
					this.getCompoundDrawables()[3]);
		} 
		else {
			this.setCompoundDrawables(this.getCompoundDrawables()[0],
					this.getCompoundDrawables()[1], imgCloseButton,
					this.getCompoundDrawables()[3]);
		}
	}
}

package com.ganbook.activities;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.ganbook.amazinglist.special.CustomToast;
import com.ganbook.app.MyApp;
import com.ganbook.models.AlbumsAnswer;
import com.ganbook.models.CreateAlbumAnswer;
import com.ganbook.models.events.CreateAlbumEvent;
import com.ganbook.user.User;

import com.project.ganim.R;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddAlbumActivity extends BaseAppCompatActivity {

	private static final String TAG = AddAlbumActivity.class.getName();

	// activity_create_album

	@BindView(R.id.add_album_name)
    EditText add_album_name;

	@BindView(R.id.add_album_btn)
    Button add_album_btn;

	@BindView(R.id.add_album_description)
	EditText add_album_description;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_create_album);

		setActionBar(getString(R.string.new_album_caption), true);

		ButterKnife.bind(this);

		MyApp.sendAnalytics("add-album-ui", "add-album-ui" + User.getId(), "add-album-ui", "AddAlbum");


		add_album_btn.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {

				uploadAlbumToServer();
		}
		});

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if(item.getItemId() == android.R.id.home) {
			onBackPressed();
		}
		return super.onOptionsItemSelected(item);
	}

	public static void hideSoftKeyboard(Activity activity) {
		InputMethodManager inputMethodManager =
				(InputMethodManager) activity.getSystemService(
						Activity.INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(
				activity.getCurrentFocus().getWindowToken(), 0);
	}

	@Override
	protected void onResume() {
		super.onResume();

		View rootView =((ViewGroup)findViewById(android.R.id.content)).
				getChildAt(0);
		rootView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					hideSoftKeyboard(AddAlbumActivity.this);
				}
			}
		});
	}

	private void uploadAlbumToServer() {

		final String album_name = add_album_name.getText().toString().trim();
		final String album_description = add_album_description.getText().toString().trim();
		if (album_name.isEmpty()) {
			CustomToast.show(this, R.string.no_album_name);
			return;
		}
		String class_id = User.current.getCurrentClassId();
		startProgress(R.string.operation_proceeding);

		Call<CreateAlbumAnswer> call = ganbookApiInterfacePOST.createAlbum(album_name,album_description,class_id);

		call.enqueue(new Callback<CreateAlbumAnswer>() {
			@Override
			public void onResponse(Call<CreateAlbumAnswer> call, Response<CreateAlbumAnswer> response) {

				CreateAlbumAnswer answer = response.body();

				Log.d(TAG, "onR esponse: answer = " + answer);

				if (answer != null && answer.getAlbumId() != null) {

					AlbumsAnswer albumsAnswer = new AlbumsAnswer(answer.getAlbumId(),
							album_name,album_description, "tmb", new Date(), 0, 0, 0, 0, 0, 0, 0);

					EventBus.getDefault().postSticky(new CreateAlbumEvent(albumsAnswer));
					stopProgress();

					onBackPressed();
					hideSoftKeyboard(AddAlbumActivity.this);

				} else {

					try {
						JSONObject responseObject = new JSONObject(response.errorBody().string());
						JSONObject errorObject = responseObject.getJSONObject("errors");
						String message  = errorObject.getString("msg");
						String status = errorObject.getString("code");
						CustomToast.show(AddAlbumActivity.this, message + "\n" + "Status: " + status);

					} catch (IOException e) {
						e.printStackTrace();
					} catch (JSONException e) {
						e.printStackTrace();
					}

					stopProgress();
				}
			}

			@Override
			public void onFailure(Call<CreateAlbumAnswer> call, Throwable t) {
				stopProgress();
				CustomToast.show(AddAlbumActivity.this, "Server error!");

				Log.e(TAG, "onFailure: create album = " + Log.getStackTraceString(t));
			}
		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
	}
}
package com.ganbook.activities;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import androidx.cardview.widget.CardView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;
import com.ganbook.app.MyApp;
import com.ganbook.communication.ICompletionHandler;
import com.ganbook.communication.datamodel.ResultObj;
import com.ganbook.communication.json.transmitter.JsonTransmitter;
import com.ganbook.dialogs.Dialogs;
import com.ganbook.models.SuccessAnswer;
import com.ganbook.models.events.SetPermissionEvent;
import com.ganbook.share.ShareManager;
import com.ganbook.user.User;
import com.ganbook.utils.FeedbackUtils;

import com.parse.ParseQuery;
import com.project.ganim.R;

import org.greenrobot.eventbus.EventBus;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingsActivity extends BaseAppCompatActivity implements OnClickListener, CompoundButton.OnCheckedChangeListener {

	// activity_settings

	private static final String LIKES = "1";
	private static final String COMMENTS = "2";
	private static final String CONTACTS = "3";

	private static final String GANBOOK_EMAIL = "info@ganbook.co.il";
	private static final String TAG = SettingsActivity.class.getName();

	ToggleButton likes, comments, contacts;
	CardView likes_layout, comments_layout, contactsLayout;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);

		setActionBar(getString(R.string.settings), true);

		if(User.current == null)
		{
			User.blocking_loadFromLocalCache(true);
		}

		MyApp.sendAnalytics("settings-ui", "settings-ui-" + User.getId(), "settings-ui", "Settings");


		overridePendingTransition(R.anim.fadein, R.anim.fadeout);

		TextView updateTxt = (TextView) findViewById(R.id.update_details);
		if (User.isStaff()) {
			updateTxt.setText(R.string.edit_staff_details);
		} else if (User.isStudent()) {
			updateTxt.setText(getString(R.string.student_details_text));
		} else if(User.isParent()) {
			updateTxt.setText(R.string.edit_parent_details);
		}  else {
			updateTxt.setText(R.string.edit_teacher_details);
		}
		
		String versionName = "";
		try {
			versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {

			e.printStackTrace();
		}		
		int versionCode = 0;
		try {
			versionCode = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
		} catch (NameNotFoundException e) {

			e.printStackTrace();
		}
		
		TextView app_version = (TextView) findViewById(R.id.app_version);
		app_version.setText(getString(R.string.app_name) + " - " + versionName + "(" + versionCode + ")");

		likes = (ToggleButton) findViewById(R.id.likes);

		comments = (ToggleButton) findViewById(R.id.comments);

		contacts = findViewById(R.id.contacts);

		if(!User.isTeacher())
		{


			likes_layout = findViewById(R.id.likes_layout);
			comments_layout = findViewById(R.id.comments_layout);
			contactsLayout = findViewById(R.id.contacts_layout);

			comments.setVisibility(View.GONE);
			likes.setVisibility(View.GONE);
			contacts.setVisibility(View.GONE);


			likes_layout.setVisibility(View.GONE);
			comments_layout.setVisibility(View.GONE);
			contactsLayout.setVisibility(View.GONE);
		}
		else
		{
			if (User.getCurrentLikeForbidden()) {
				likes.setChecked(false);

			}
			else
			{
				likes.setChecked(true);

			}

			if (User.getCurrentCommentForbidden()) {
				comments.setChecked(false);
			}
			else
			{
				comments.setChecked(true);
			}

			if (User.getCurrentContactsForbidden()) {
				contacts.setChecked(false);
			}
			else
			{
				contacts.setChecked(true);
			}
		}


		likes.setOnCheckedChangeListener(this);
		comments.setOnCheckedChangeListener(this);
		contacts.setOnCheckedChangeListener(this);

		__listen(findViewById(R.id.edit_teacher));
		__listen(findViewById(R.id.rate_us));
		__listen(findViewById(R.id.feedback));
		__listen(findViewById(R.id.logout));
		__listen(findViewById(R.id.faq));		
		__listen(findViewById(R.id.change_password));
		__listen(findViewById(R.id.invite_friends));

	}

	@Override
	public View onCreateView(String name, Context context, AttributeSet attrs) {
		return super.onCreateView(name, context, attrs);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem) {
		if (menuItem.getItemId() == android.R.id.home) {

			finish();
		}
		return super.onOptionsItemSelected(menuItem);
	}

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopProgress();
    }

    @Override
	protected void onPause() {
		super.onPause();
		overridePendingTransition(R.anim.fadein, R.anim.fadeout);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.edit_teacher:
			
			String screen_name = "edit-parents";
			
			if(User.isTeacher())
			{
				screen_name = "edit-teacher";
			}

			MyApp.sendAnalytics("settings-ui", "settings-"+screen_name+"-ui"+ User.getId(), "settings-ui", "Settings");


			start(UpdateTeacherDetailsActivity.class);			 
			break;
			
		case R.id.change_password:

			MyApp.sendAnalytics("settings-change-password-ui", "settings-change-password"+"-ui"+ User.getId(), "settings-change-password-ui", "SettingsChangePassword");

			start(ChangePasswordActivity.class);
			break;
			
		case R.id.rate_us:
			openAppPageInGooglePlay();
			break;
			 
		case R.id.faq:
			WebViewActivity.start(this, WebViewActivity.FAQ);
			break;
			 
		case R.id.feedback:
			Intent intent = new Intent(Intent.ACTION_SENDTO);
			intent.setType("message/rfc822");
			intent.putExtra(Intent.EXTRA_EMAIL, GANBOOK_EMAIL);
			intent.setData(Uri.parse("mailto:"+GANBOOK_EMAIL));
			intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.feedback_subject));
			intent.putExtra(Intent.EXTRA_TEXT, FeedbackUtils.getEmailBody());

			try {
				startActivity(Intent.createChooser(intent, "Send email..."));
				finish();

			} catch (ActivityNotFoundException e) {
				Log.d("ACTIVITY NOT FOUND", e.getMessage());
			}

			break;
			 
		case R.id.invite_friends:
			ShareManager.openShareMenu(this, R.string.invite_friend_subject, R.string.invite_friend_body);
			break;
			 
		case R.id.logout:
			MyApp.loggingOut = true; 
			openLogoutPopup();
			break;

		default:
			throw new RuntimeException();
		}
	}

	private void openLogoutPopup() {

		Dialogs.successDialogWithButton(this, getString(R.string.logount_pop_title), getString(R.string.logount_pop_text), getString(R.string.ok), getString(R.string.cancel), new Dialogs.ButtonDialogInterface() {
			@Override
			public void onButtonOkClicked() {
				MyApp.sendAnalytics("settings-logout-ui", "settings-logout"+"-ui"+ User.getId(), "settings-logout-ui", "SettingsLogout");

				performLogout();
			}
		});
	}

	private void performLogout() {
		startProgress(R.string.operation_proceeding);
		ParseQuery.clearAllCachedResults();

		final Activity a = this;
		JsonTransmitter.send_userLogout(new ICompletionHandler() {			
			@Override
			public void onComplete(ResultObj result) {
				stopProgress();
				a.finish();
				MainActivity.stop();
				User.clear();
				Dialogs.sneakerLogout(a, "Logged out", getString(R.string.logged_out));
				start(EntryScreenActivity.class);
			}
		}); 
			
	}


	private void openAppPageInGooglePlay() {
		final String myPackageName = getPackageName();
		Uri uri = Uri.parse("market://details?id=" + myPackageName);
		Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
		try {
			startActivity(goToMarket);
		} 
		catch (Exception e) {
			try { 
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + myPackageName)));
			}
			catch (Exception ee) {
				int jj=24;
				jj++;
			}
		}	
	}

	private View __listen(View view) {
		if (view != null) {
			view.setOnClickListener(this);
		}
		return view;
	}


	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

		String active = "0";

		switch (buttonView.getId()) {
			case R.id.likes:


				boolean like_forbidden = false;

				if(!isChecked)
				{
					active = "1";
					like_forbidden = true;
				}

				Call<SuccessAnswer> ganLikes = ganbookApiInterfacePOST
						.updateGanPermission(User.current.getCurrentGanId(),LIKES,active);

				ganLikes.enqueue(new Callback<SuccessAnswer>() {
					@Override
					public void onResponse(Call<SuccessAnswer> call, Response<SuccessAnswer> response) {

						SuccessAnswer successAnswer = response.body();

						if (successAnswer == null) {

						} else {

							Log.d(TAG, "onResponse: success answer likes = " + successAnswer);

							if (successAnswer.isSuccess()) {

								//send event to activity
								EventBus.getDefault().postSticky(new SetPermissionEvent(1));
							} else {

								//error in method
							}
						}
					}

					@Override
					public void onFailure(Call<SuccessAnswer> call, Throwable t) {

						Log.e(TAG, "onFailure: error while set likes perm = " + Log.getStackTraceString(t));
					}
				});

				User.setCurrentLikeForbidden(like_forbidden);

				break;
			case R.id.comments:
				boolean comment_forbidden = false;

				if(!isChecked)
				{
					active = "1";
					comment_forbidden = true;
				}

				Call<SuccessAnswer> ganComments = ganbookApiInterfacePOST
						.updateGanPermission(User.current.getCurrentGanId(),COMMENTS,active);

				ganComments.enqueue(new Callback<SuccessAnswer>() {
					@Override
					public void onResponse(Call<SuccessAnswer> call, Response<SuccessAnswer> response) {

						SuccessAnswer successAnswer = response.body();

						if (successAnswer == null) {

						} else {

							Log.d(TAG, "onResponse: success answer comments = " + successAnswer);

							if (successAnswer.isSuccess()) {

								//send event to activity
								EventBus.getDefault().postSticky(new SetPermissionEvent(0));
							} else {

								//error in method
							}
						}
					}

					@Override
					public void onFailure(Call<SuccessAnswer> call, Throwable t) {

						Log.e(TAG, "onFailure: error while set comments perm = " + Log.getStackTraceString(t));
					}
				});

				User.setCurrentCommentForbidden(comment_forbidden);

				break;

			case R.id.contacts:
				boolean contacts_forbidden = false;

				if(!isChecked)
				{
					active = "1";
                    contacts_forbidden = true;
				}

				Call<SuccessAnswer> getContactsForbidden = ganbookApiInterfacePOST
						.updateGanPermission(User.current.getCurrentGanId(),CONTACTS,active);

				getContactsForbidden.enqueue(new Callback<SuccessAnswer>() {
					@Override
					public void onResponse(Call<SuccessAnswer> call, Response<SuccessAnswer> response) {

						SuccessAnswer successAnswer = response.body();

						if (successAnswer == null) {

						} else {

							Log.d(TAG, "onResponse: success answer comments = " + successAnswer);

							if (successAnswer.isSuccess()) {
								//send event to activity
								EventBus.getDefault().postSticky(new SetPermissionEvent(3));
							} else {

								//error in method
							}
						}
					}

					@Override
					public void onFailure(Call<SuccessAnswer> call, Throwable t) {

						Log.e(TAG, "onFailure: error while set contacts perm = " + Log.getStackTraceString(t));
					}
				});

				User.setCurrentContactsForbidden(contacts_forbidden);

				break;


		}
	}
}

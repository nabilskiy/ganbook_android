package com.ganbook.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;

import com.ganbook.amazinglist.special.CustomToast;
import com.ganbook.app.MyApp;
import com.ganbook.communication.datamodel.ResultObj;
import com.ganbook.communication.json.BaseResponse;
import com.ganbook.communication.json.GetUserKids_Response;
import com.ganbook.communication.json.createsession_response;
import com.ganbook.communication.json.getclass_Response;
import com.ganbook.communication.json.loginnew_Response_Parent;
import com.ganbook.communication.json.loginnew_Response_Teacher;
import com.ganbook.communication.json.transmitter.JsonTransmitter;
import com.ganbook.gcm.PushMsgPayload;
import com.ganbook.gcm.PushRepository;
import com.ganbook.gcm.RegistrationIntentService;
import com.ganbook.interfaces.IGanbookApiCommercial;
import com.ganbook.sharedprefs.SPReader;
import com.ganbook.sharedprefs.SPWriter;
import com.ganbook.user.User;
import com.ganbook.utils.NetworkUtils;
import com.ganbook.utils.UpdateUtils;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.security.ProviderInstaller;
import com.google.gson.Gson;
import com.parse.ParseInstallation;
import com.parse.ParseQuery;
import com.project.ganim.R;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.inject.Named;
import javax.net.ssl.SSLContext;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashActivity extends Activity { 
	
	private static final long SPLASH_INTERVAL = 2000;
	
	public static boolean wasShown;
	
	private final Initializer initializer = new Initializer();
	
	private static SplashActivity inst;
    private SharedPreferences prefs;
    private Button register_btn;
    private String pushLocKey;

    @Inject
    @Named("COMMERCIAL")
    IGanbookApiCommercial ganbookApiCommercial;

	class Initializer {
		void onCreate() {
			inst = SplashActivity.this;
            prefs = getSharedPreferences("com.ganbook.activities", MODE_PRIVATE);

			setContentView(R.layout.splash_layout);
			new Handler().postDelayed(() -> init(), SPLASH_INTERVAL);
		}
	}

    private void init()
    {

        if (!NetworkUtils.isConnected()) {
            set_no_internet();
        }

        Intent intent = new Intent(MyApp.context, RegistrationIntentService.class);
        startService(intent);


        User _user = User.blocking_loadFromLocalCache(true);
        if (_user != null) {

            if(wasShown) {
                createSession_And_goto_MainScreen();
            }
            else {
                if(getRegisterProcess())
                {
                    gotoRegistration();
                    SplashActivity.callFinish();
                }
                else
                {
                    refreshFromServer(_user.id, _user.type);
                }
            }
            wasShown = true;
            return;
        }

        gotoNextScreen();

        String user_id = new SPReader("GANIM").getString("user_id_new", "0");
        if(!"0".equals(user_id)) //old
        {
            return;
        }
}


    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        ((MyApp) getApplication()).getGanbookApiComponent().inject(this);

        try {
            ProviderInstaller.installIfNeeded(getApplicationContext());
            SSLContext sslContext;
            sslContext = SSLContext.getInstance("TLSv1.2");
            sslContext.init(null, null, null);
            sslContext.createSSLEngine();
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException
                | NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
        }

        String push_class_id = getIntent().getStringExtra(PushRepository.PUSH_CLASS_ID);
        pushLocKey = getIntent().getStringExtra(PushRepository.PUSH_LOC_KEY);
        String pushMessage = getIntent().getStringExtra(PushRepository.PUSH_JSON_MSG);

        if (pushMessage != null) {
            Optional<String> url = extractUrls(pushMessage).stream().findFirst();
            if (url.isPresent()) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url.get()));
                startActivity(i);
                finish();
                return;
            }
        }

        if (push_class_id != null || pushLocKey != null) {

            PushRepository.setActivePushNotif(push_class_id, pushLocKey);
        }
        else if(getIntent().getData()!=null)
        {
            Uri data = getIntent().getData();//set a variable for the Intent

            String fullPath = data.getEncodedSchemeSpecificPart();//get the full path -scheme - fragments

            if(fullPath.contains("web.ganbook.co.il"))
            {
                String scheme = data.getScheme();

                Uri uri=Uri.parse(scheme+"://"+fullPath);

                String url = uri.getQueryParameter("url");

                Log.d("ganbook",url);

                String []  arr = url.split("_");

                String tab = arr[0];

                String class_id = arr[1];
                String loc_key = PushMsgPayload.NEW_PICS_UPLD;


                switch (tab)
                {
                    case "a":
                    {
                        loc_key = PushMsgPayload.NEW_PICS_UPLD;
                        break;
                    }

                    case "m":
                    {
                        loc_key = PushMsgPayload.MSG;
                        break;
                    }

                    case "e":
                    {
                        loc_key = PushMsgPayload.EVNT;
                        break;
                    }

                }
                PushRepository.setActivePushNotif(class_id, loc_key);
            }
        }

        //     Fabric.with(this, new Crashlytics());
		initializer.onCreate();
	}

    public static List<String> extractUrls(String text) {
        List<String> containedUrls = new ArrayList<String>();
        String urlRegex = "((https?|ftp|gopher|telnet|file):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
        Pattern pattern = Pattern.compile(urlRegex, Pattern.CASE_INSENSITIVE);
        Matcher urlMatcher = pattern.matcher(text);

        while (urlMatcher.find())
        {
            containedUrls.add(text.substring(urlMatcher.start(0),
                    urlMatcher.end(0)));
        }

        return containedUrls;
    }


    private void createSession_And_goto_MainScreen() {

        final Activity a = this;
        final String userId = User.getId();

        ParseQuery.clearAllCachedResults();
        ParseInstallation parseInstallation = ParseInstallation.getCurrentInstallation();

    	parseInstallation.put("user_id", String.valueOf(Integer.valueOf(userId) - Integer.valueOf(User.USER_ID_KEY)));

        parseInstallation.saveInBackground();

        JsonTransmitter.send_createsession(null, result -> {

            if (!result.succeeded) {
                if (JsonTransmitter.NO_NETWORK_MODE.equals(result.result)) {
                    set_no_internet();
                    return;
                }

                String errmsg = result.result;
                CustomToast.show(SplashActivity.this, errmsg);
            } else {
                createsession_response response = (createsession_response) result.getResponse(0);

                User.updateWithCreateSession(response);

                if (!UpdateUtils.NO_UPDATE.equals(response.update)) {
                    setContentView(R.layout.splash_layout);
                    showRecommendUpdateErrorDialog(response.update);
                    return;
                }

                gotoMain();
            }

        });

    }

    private void set_no_internet()
    {
        setContentView(R.layout.no_internet_layout);
        register_btn = (Button) findViewById(R.id.register_btn);

        register_btn.setOnClickListener(v -> {

            finish();
            startActivity(getIntent());
        });
    }

    private void showRecommendUpdateErrorDialog(String update) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);


        dialogBuilder.setNeutralButton(R.string.update, (dialog, whichButton) -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("market://details?id=com.project.ganim"));
            startActivity(intent);
        });


        if(UpdateUtils.RECOMMEND_UPDATE.equals(update)) {
            dialogBuilder.setMessage(R.string.new_version_avail);
            dialogBuilder.setNegativeButton(R.string.notnow, (dialog, whichButton) -> {
                dialog.dismiss();
                gotoMain();
            });
        }
        else {
            dialogBuilder.setMessage(getString(R.string.new_version_avail) + "\n" + getString(R.string.new_version_critical));
        }

        dialogBuilder.setTitle(R.string.app_name);

        dialogBuilder.setCancelable(false);

        dialogBuilder.show();
    }

    private boolean getRegisterProcess() {
        return new SPReader("registerProcessFile").getBool("in_process",false);
    }

    private void logout() {
        JsonTransmitter.send_userLogout(result -> {
            finish();
            MainActivity.stop();
            User.clear();
            startActivity(new Intent(SplashActivity.this, EntryScreenActivity.class));
        });
    }

    private void gotoRegistration() {
        startActivity(new Intent(SplashActivity.this, RegistrationSucceededActivity.class));
        this.finish();
    }

    private void gotoMain() {
        if(getRegisterProcess())
        {
            gotoRegistration();
            SplashActivity.callFinish();
        }
        else
        {
            boolean was_shown = new SPReader("NEW_YEAR_NEW").getBool("was_shown", false);

            if(!was_shown && User.current.new_year)
            {
                startActivity(new Intent(SplashActivity.this, NewYearActivity.class));
            }
            else {
                if (pushLocKey != null) {
                    if (pushLocKey.equals(PushMsgPayload.MEETING_MSG)) {
                        startActivity(new Intent(SplashActivity.this, MeetingEventListActivity.class));
                    } else {
                        startActivity(new Intent(SplashActivity.this, MainActivity.class));

                    }
                } else {
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                }

            }

            overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        }
    }
	
	private void gotoNextScreen() {
		this.finish();
		startActivity(new Intent(this, EntryScreenActivity.class));
	}

	public static void callFinish() {
		if (inst != null) {
			inst.finish();
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (this == inst) {
			inst = null;
		}
	}

    private void issueNextRequest(ResultObj result) {
        String rawJson = result.result;
        Class<?> responseType = getResponseType(rawJson);
        if (responseType == null) {
            return;
        }

        BaseResponse response;
        try {
            response = (BaseResponse) new Gson().fromJson(rawJson, responseType);
            if (response==null) {
                CustomToast.show(this, R.string.operation_failed);
                return;
            }
            JsonTransmitter.handleResponseWithUser(response);

            new SPWriter("GANIM").putString("user_id_new", "0").commit(); // no need old anymore

            String userid = User.getId();

            if (isParentResponse) {
                JsonTransmitter.send_getuserkids(userid, result1 -> {
                    if (!result1.succeeded) {
                        if(JsonTransmitter.NO_NETWORK_MODE.equals(result1.result))
                        {
                            set_no_internet();
                            return;
                        }
                        String errmsg = result1.result;
                        CustomToast.show(inst, errmsg);
                        logout();
                        return;
                    }
                    int num = result1.getNumResponses();
                    GetUserKids_Response[] responses = new GetUserKids_Response[num];
                    for (int i = 0; i < num; i++) {
                        responses[i] = (GetUserKids_Response) result1.getResponse(i);
                    }
                    User.updateWithUserkids(responses);
                    gotoMain();
                });
            } else {
                int jj=234;
                jj++;
                JsonTransmitter.send_getclass(userid, result12 -> {
                    int jj1 =234;
                    jj1++;
                    if (!result12.succeeded) {
                        if(JsonTransmitter.NO_NETWORK_MODE.equals(result12.result))
                        {
                            set_no_internet();
                            return;
                        }
                        String errmsg = result12.result;
                        CustomToast.show(inst, errmsg);
                        logout();
                        return;
                    }
                    getclass_Response response1 = (getclass_Response) result12.getResponse(0);
                    User.updateWithClasses(response1);
                    gotoMain();
                });
            }
            int jj=234;
            jj++;
        }
        catch (Exception e) {
            int jj=234;
            jj++;
            return;
        }
    }

    private void refreshFromServer(String userid, String usertype)
    {

             if (!usertype.equals(User.Type_Teacher)) {
                JsonTransmitter.send_getuserkids(userid, result -> {
                    if (!result.succeeded) {
                        if (JsonTransmitter.NO_NETWORK_MODE.equals(result.result)) {
                            set_no_internet();
                            return;
                        }
                        String errmsg = result.result;
                        CustomToast.show(inst, errmsg);
                        logout();
                        return;
                    }
                    int num = result.getNumResponses();
                    GetUserKids_Response[] responses = new GetUserKids_Response[num];
                    for (int i = 0; i < num; i++) {
                        responses[i] = (GetUserKids_Response) result.getResponse(i);
                    }

                    User.updateWithUserkids(responses);

                    new SPWriter("DRAWER_REFRESH").putLong("DRAWER_REFRESH", System.currentTimeMillis()).commit();

                    createSession_And_goto_MainScreen();
                });
            } else {
                int jj = 234;
                jj++;
                JsonTransmitter.send_getclass(userid, result -> {
                    int jj1 = 234;
                    jj1++;
                    if (!result.succeeded) {
                        if (JsonTransmitter.NO_NETWORK_MODE.equals(result.result)) {
                            set_no_internet();
                            return;
                        }
                        String errmsg = result.result;
                        CustomToast.show(inst, errmsg);
                        logout();
                        return;
                    }
                    getclass_Response response = (getclass_Response) result.getResponse(0);
                    User.updateWithClasses(response);

                    new SPWriter("DRAWER_REFRESH").putLong("DRAWER_REFRESH", System.currentTimeMillis()).commit();

                    createSession_And_goto_MainScreen();
                });
            }

    }

    private Class<?> getResponseType(String rawJson) {
        String typeVal;
        try {
            typeVal = new JSONObject(rawJson).getString("type");
        } catch (JSONException e1) {
            e1.printStackTrace();
            int jj=234;
            jj++;
            return null;
        }
        isParentResponse = User.isParentType(typeVal);
        Class<?> responseType;
        if (isParentResponse) {
            responseType = loginnew_Response_Parent.class;
        } else {
            responseType = loginnew_Response_Teacher.class;
        }
        return responseType;
    }

    private void call_loginmigrateandroid(String user_id)
    {
        String new_user_id = String.valueOf((Integer.valueOf(user_id) - Integer.valueOf(User.USER_ID_KEY)));
        JsonTransmitter.send_loginmigrateandroid(new_user_id, result -> {
            if (result.succeeded) {
                issueNextRequest(result);
            }
        });
    }

    public static void loginmigrateandroid(String user_id)
    {
        if(inst == null)
        {
            return;
        }
        inst.call_loginmigrateandroid(user_id);
    }

    private boolean isParentResponse;


}

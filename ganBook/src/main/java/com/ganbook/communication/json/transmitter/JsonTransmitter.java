package com.ganbook.communication.json.transmitter;     

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.util.Log;

import com.ganbook.app.MyApp;
import com.ganbook.communication.CommConsts;
import com.ganbook.communication.ICompletionHandler;
import com.ganbook.communication.SignatureUtils;
import com.ganbook.communication.datamodel.ErrorResult;
import com.ganbook.communication.datamodel.ResultObj;
import com.ganbook.communication.json.BaseResponse;
import com.ganbook.communication.json.Debug_BaseResponse;
import com.ganbook.communication.json.GetUserKids_Response;
import com.ganbook.communication.json.IJsonHenhancer;
import com.ganbook.communication.json.Response_With_User;
import com.ganbook.communication.json.SimpleSuccessResponse;
import com.ganbook.communication.json.addclass_response;
import com.ganbook.communication.json.createalbum_Response;
import com.ganbook.communication.json.createalbumcomment_Response;
import com.ganbook.communication.json.createkid_Response;
import com.ganbook.communication.json.createkindergarten_response;
import com.ganbook.communication.json.createmessage_Response;
import com.ganbook.communication.json.createsession_response;
import com.ganbook.communication.json.createtoken_Response;
import com.ganbook.communication.json.createuser_Response;
import com.ganbook.communication.json.deletepicturesj_Response;
import com.ganbook.communication.json.getactivekids_Response;
import com.ganbook.communication.json.getalbum_response;
import com.ganbook.communication.json.getalbumcomments_response;
import com.ganbook.communication.json.getclass_Response;
import com.ganbook.communication.json.getevents_Response;
import com.ganbook.communication.json.getfavorite_Response;
import com.ganbook.communication.json.getgankids_response;
import com.ganbook.communication.json.getkindergarten_response;
import com.ganbook.communication.json.getmessage_Response;
import com.ganbook.communication.json.getparentsclass_response;
import com.ganbook.communication.json.getparentstosendnotification_Response;
import com.ganbook.communication.json.getpickupdropoffhours_response;
import com.ganbook.communication.json.getpicture_Response;
import com.ganbook.communication.json.getreadmessage_Response;
import com.ganbook.communication.json.getvaadclasses_response;
import com.ganbook.communication.json.loginnew_Response_Parent;
import com.ganbook.communication.json.loginnew_Response_Teacher;
import com.ganbook.communication.json.retention_Response;
import com.ganbook.communication.json.updatekindergarten_response;
import com.ganbook.communication.json.updatepassword_Response;
import com.ganbook.datamodel.UserKids;
import com.ganbook.debug.InDebug;
import com.ganbook.exception.EmptyResultException;
import com.ganbook.googleservices.GoogleServices;
import com.ganbook.models.GetParentAnswer;
import com.ganbook.parseservices.ParseServices;
import com.ganbook.user.User;
import com.ganbook.utils.DeviceNameGetter;
import com.ganbook.utils.ImageUtils;
import com.ganbook.utils.LocaleGetter;
import com.ganbook.utils.MySSLSocketFactory;
import com.ganbook.utils.NetworkUtils;
import com.ganbook.utils.StrUtils;
import com.google.gson.Gson;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

//ggggFix: Startup logic: from Entry screen
//=======================================
//    perform general_init_logic
//           check if GooglePlay services accessable
//           create GCM instance
//           get CGM registration id from local cahche or, if none --
//               perform GCM registration
//               call createtoken() -- ONLE PER APP LIFE
//               updateToken -- EVERY TIME AFTER RESTART
//               and store the result.token_id as registyration id
//   if logged in --
//      call create_session()
//      go directly to main screen
//
//    signup flow:
//         TEACHER 
//         	    (1) createUser (2) create createkindergarten  (3) silent login
//         PAERNT:
//	  			(1) createUser (2) create createkid  (3) silent login

public class JsonTransmitter {
	private static final String TAG = "JSON MESS";

	private JsonTransmitter() {}

	//	private static final boolean MODE_TEACHER = MyApp.IS_TEACHER_APP;	
	//	private static final String APP_TYPE =  "1"  >> ganbook  : "2" >> "parent"; 

	public static final HashMap<String, BaseResponse> debug____map = new HashMap<String, BaseResponse>();

	private static final String _TAG = "ganapi";

	public static final String API_HOST = "https://api.ganbook.co.il";
	//public static final String API_HOST = "http://test-api.ganbook.co.il";

	public static final String API_URL = "/index.php/api/ganbook/";

	public static final String BASE_URL = API_HOST + API_URL;

	public static final String USERS_HOST = "users/";

	public static final String TMB = "tmb";

	public static final String PICTURE_HOST = "http://s3.ganbook.co.il/ImageStore/";
	public static final String PIC_HOST = "http://s3.ganbook.co.il/";
	
	public static final String NO_NETWORK_MODE = "NO_NETWORK_MODE";

	private static final int CONNECT_TIMEOUT_MSECS = 20000; 
	private static final int SOCKET_TIMEOUT_MSECS = 20000;  

	private static final int GENDER_BOY = 0;
	private static final int GENDER_GIRL = 1;

	private static final int ACTIVEPICT_DELETED = 0;
	private static final int ACTIVEPICT_ACTIVE = 1;
	private static final int ACTIVEPICT_ISVIDEO = 2;

	private static final String device_vendor;
	private static final String device_model;
	private static final String os_name;
	private static final String os_version;
	private static float app_version = 0;

	public static final String ACTIVE_PICTURE = "1"; 
	public static final String ACTIVE_VIDEO = "2";


	static {
		final Context context = MyApp.context;
		String versionName = "";
		try {
			versionName = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {

			e.printStackTrace();
		}		
		int versionCode = 0;
		try {
			versionCode = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
		} catch (NameNotFoundException e) {

			e.printStackTrace();
		}
		app_version = versionCode;

		device_vendor = DeviceNameGetter.getManufacturer();
		device_model = DeviceNameGetter.getDeviceModel();

		os_name = "android";
		os_version = "" + android.os.Build.VERSION.RELEASE;		
	}


	private static String debug_testResults;
	private static volatile String req_func_name; 
	private static HashMap<String, Method> debug_method_map;
	private static volatile int debug__ctr = 0;



	public static void execute_via_reflection(String requestName) {
		debug_testResults = "";
		debug__shortDesc = "";
		req_func_name = "send_" + requestName; 
		final String param_arr_name = req_func_name + "_params";
		Method req_method; 
		Field params_field;

		if (debug_method_map == null) {
			debug_method_map = new HashMap<String, Method>(); 
			Method[] allMethods = JsonTransmitter.class.getDeclaredMethods();
			for (Method m: allMethods) {
				debug_method_map.put(m.getName(), m);
			}
		}

		try {
			req_method = debug_method_map.get(req_func_name); 
			if (req_method == null) {
				__throw("Methodd is null");
			}				
			if (!req_method.getReturnType().equals(Void.TYPE)) {
				__throw("Not void method");
			}
			if (!Modifier.isStatic(req_method.getModifiers())) { 
				__throw("Not static method");
			}
			if (!Modifier.isPublic(req_method.getModifiers())) { 
				__throw("Not public method");
			} 

			//			params_field = JsonTransmitter.class.getField(param_arr_name);
			params_field = JsonTransmitter.class.getDeclaredField(param_arr_name);
			if (params_field == null) {
				__throw("Fieldd not found: " + param_arr_name);
			}			 	
			if (!Modifier.isStatic(params_field.getModifiers())) { 
				__throw("Field " + param_arr_name + " is not static");
			}

			String[] params = (String[]) params_field.get(null);
			if (params==null || params.length==0) {
				__throw("Empty param list!");
			}


			// and activate:

			try { 
				int jj=234;
				jj++;
				req_method.invoke(null, params);
			}
			catch (Exception e) {
				__throw("invoke error: " + e);   
			}

			int jj=234;
			jj++;

		} catch (Exception e) {

			e.printStackTrace();
			String err = "Error while " + req_func_name + ": " + e + ";  debug__ctr = " + debug__ctr;
			debug__shortDesc += ("\n" + err); 
			throw new RuntimeException(err); 
		}  
	}


	private static void __throw(String msg) { 
		int jj = 345; 
		jj++;
		String ff = req_func_name;
		debug__shortDesc += ("\n" + "EXCEPTION: " + msg + "\n");
		throw new RuntimeException(msg);		
	}

	public static class ApiTypes {

		public static ArrayList<String> getAll() {
			ArrayList<String> result = new ArrayList<String>();			
			Field[] allFields = ApiTypes.class.getDeclaredFields();
			for (Field f: allFields) {
				if (Modifier.isStatic(f.getModifiers())) {
					if (f.getType().equals(String.class)) { 
						String f_name = f.getName();
						result.add(f_name);
					}
				}
			}
			return result;
		}

		// Total:  45 API calls

		// USER
		public static final String retention = "retention"; // onClick of Enter mail screen (one before registration)
		public static final String createuser = "createuser"; //Signup page onClick .  
		public static final String loginnew = "loginnew"; // login page onClick
		public static final String loginmigrateandroid = "loginmigrateandroid"; // login page onClick
		public static final String userLogout = "userLogout"; // user requests to logout; Will jump to "login" screen
		public static final String resetpassword = "resetpassword"; // onClick forget password page
		public static final String updatepassword = "updatepassword"; // settings > update password
		public static final String createsession = "createsession"; // part of init logic 
		public static final String reportsavedpicture = "reportsavedpicture"; // After user SAVES (not uploads) image (should also write it to 
		// a "GanBook Images" Album)

		// PARENT
		public static final String createkid = "createkid"; // PARENT > SIDE MENU > ADD KID
		public static final String setclass = "setclass"; // PARENT at end of SIGNUP procedure
		public static final String updateclass = "updateclass"; // update kid's class from contact list
		public static final String getuserkids = "getuserkids"; // TEACHER: automatically called after teacher login +
		//also called at sideBar pullToRefresh
		public static final String getparents = "getparents"; // every time upon entering contact List
		public static final String updateparent = "updateparent"; // PARENT > SETTINGS > UPDATE DETAILS
		public static final String getpickupdropoffhours = "getpickupdropoffhours";
		public static final String savepickupdropoffhours = "savepickupdropoffhours";
		public static final String savecoordsperclass = "savecoordsperclass";
		public static final String saveparentnotification = "saveparentnotification";
		public static final String getparentstosendnotification = "getparentstosendnotification";
		public static final String savenotificationtoparents = "savenotificationtoparents";

		// Kindergarten API
		public static final String getkindergarten = "getkindergarten"; // PARENT only: after user inserts CODE
		public static final String createkindergarten = "createkindergarten"; // Silently called after TEACHER signup
		public static final String updatekindergarten = "updatekindergarten"; // settings > change kindergarden settings
		public static final String getgankids = "getgankids"; //
		public static final String updateganspermission = "updateganspermission"; //
		
		// Teacher API
		public static final String getclass = "getclass"; // for TEACHER: automatically called after teacher login +
		// also called at sideBar pullToRefresh
		public static final String activekid  = "activekid"; // TEACHER after confirmation of child 
		public static final String setparentpermission = "setparentpermission"; // TEACHER > SIDEBAR > PARENT PERMISSION > ASSIGN
		public static final String getparentsclass = "getparentsclass"; // TEACHER ONLY > SINGN VAAD
		public static final String getvaadclassesnew = "getvaadclassesnew"; // TEACHER ONLY > SINGN VAAD
		public static final String addclass = "addclass"; // TEACHER only SideBar > Add Class

		// Album API
		public static final String getalbum = "getalbum";  // called FIRST TIME on entering (multiple) albums tab
		public static final String updateAlbumView = "updateAlbumView"; // called after getpicture return success        													// and also after pullToRefresh
		public static final String createalbum = "createalbum"; // TEACHER upon creation of new Album
		public static final String deletealbumj = "deletealbumj"; // TEACHER  upon deletion of new Album
		public static final String editalbum = "editalbum"; // TEACHER upon change Album title
		public static final String onpostftp = "onpostftp";
		public static final String onposts3 = "onposts3";
		public static final String updatealbumlike = "updatealbumlike";
		public static final String getalbumcomments = "getalbumcomments";
		public static final String createalbumcomment = "createalbumcomment";
		public static final String updatealbumcomment = "updatealbumcomment";
		public static final String deletealbumcomment = "deletealbumcomment";


		// Picture API
		public static final String getpicture = "getpicture"; // called EACH TIME entering the (single) album View
		public static final String deletepicturesj = "deletepicturesj"; // TEACHER : Album View > Picture delete
		//		public static final String insertpictures = "insertpictures"; // not used 
		public static final String uploadpic = "uploadpic"; // PARENT SIGNUP ; ONLY FOR CHILD PICTURE
		public static final String pushafterupload = "pushafterupload"; // TEACHER ONLY after entire
		//upload procedure is completed; send summary to server
		public static final String createpicture = "createpicture"; // upload file API
		public static final String updatepicfavorite = "updatepicfavorite"; // upload file API
		public static final String getfavorite = "getfavorite"; // upload file API


		// Message API
		public static final String getmessage = "getmessage"; // called FIRST TIME entering the message-List tab + PULL-TO-REFRESH
		public static final String createmessage = "createmessage"; // upon creation of new message
		public static final String updatereadmessage = "updatereadmessage"; // call after getmessage succeeds with param == the last msg id BEFORE getmessage was called
		public static final String getreadmessage = "getreadmessage"; // TEACHER: list of who has read/not a specific message
		public static final String senduserspush = "senduserspush"; // from read/NOt-read screen -- reminder resend command will set this one
		public static final String deletemessage = "deletemessage"; // TEACHER: delete message

		// Token API
		public static final String createtoken = "createtoken"; // part of the init procedure
		public static final String updatetoken = "updatetoken"; // part of the init procedure
		
		//Upload API
		public static final String createuploadtask = "createuploadtask"; // part of the init procedure
		public static final String updateuploadtask = "updateuploadtask"; // part of the init procedure
		
		public static final String createuserpush = "createuserpush"; // part of the init procedure

		public static final String sendkindergartenmail‎ = "sendkindergartenmail"; // part of the init procedure


		//Event API
		public static final String getevents = "getEvents";
		public static final String createEvent = "createEvent";
		public static final String deleteevent = "deleteEvent";
		public static final String updateEvent = "updateevent";
		public static final String getactivekids = "getactivekids";


	};


	//  Event API
	//====================

	public static void send_getevents(
			String class_id,
			ICompletionHandler handler) {

		sendGeneric(ApiTypes.getevents, getevents_Response[].class, handler, null,
				"android", "1",
				"class_id", class_id
				);
	}

	public static void send_deleteEvent(
			String event_id,
			ICompletionHandler handler) {

		sendGeneric(ApiTypes.deleteevent, SimpleSuccessResponse.class, handler, null,
				"event_id", event_id
		);
	}

	public static void send_createEvent(
			String class_id,
			String title,
			String type,
			String event_start_date,
			String event_end_date,
			String comments,
			String all_day,
			String day_off,
			String all_kids,
			JSONArray kids,
			ICompletionHandler handler) {

		final JSONArray _kids = kids;

		IJsonHenhancer kidsIds = new IJsonHenhancer() {
			@Override
			public void enhance(JSONObject jobj) {
				try {
					jobj.put("kids", _kids);

				} catch (JSONException e) {

					e.printStackTrace();
					throw new RuntimeException();
				}
			}
		};

		title = UtfEncode(title);
		comments = UtfEncode(comments);

		Log.d(TAG, "send_createEvent: kids ids = " + kidsIds.toString());

		sendGeneric(ApiTypes.createEvent, getevents_Response[].class, handler, kidsIds,
				"android","1",
				"class_id", class_id,
				"title", title,
				"type", type,
				"event_start_date", event_start_date,
				"event_end_date", event_end_date,
				"comments", comments,
				"all_day", all_day,
				"day_off", day_off,
				"all_kids", all_kids
		);
	}

	public static void send_updateEvent(
			String event_id,
			String class_id,
			String title,
			String type,
			String event_start_date,
			String event_end_date,
			String comments,
			String all_day,
			String day_off,
			String all_kids,
			JSONArray kids,
			ICompletionHandler handler) {

		final JSONArray _kids = kids;

		IJsonHenhancer kidsIds = new IJsonHenhancer() {
			@Override
			public void enhance(JSONObject jobj) {
				try {
					jobj.put("kids", _kids);

				} catch (JSONException e) {

					e.printStackTrace();
					throw new RuntimeException();
				}
			}
		};

		title = UtfEncode(title);
		comments = UtfEncode(comments);

		sendGeneric(ApiTypes.updateEvent, getevents_Response[].class, handler, kidsIds,
				"android","1",
				"event_id", event_id,
				"class_id", class_id,
				"title", title,
				"type", type,
				"event_start_date", event_start_date,
				"event_end_date", event_end_date,
				"comments", comments,
				"all_day", all_day,
				"day_off", day_off,
				"all_kids", all_kids
		);
	}

	public static void send_getactivekids(
			String class_id,
			ICompletionHandler handler) {

		sendGeneric(ApiTypes.getactivekids, getactivekids_Response[].class, handler, null,
				"class_id", class_id
		);
	}





	//  User API
	//====================


	private static final String[] send_retention_params = {"sdfsd@gmail.com", "1111"};

	public static void send_retention(
			String mail,
			ICompletionHandler handler) {
		String token_id = GoogleServices.getRegistrationId(); 
		sendGeneric(ApiTypes.retention, retention_Response.class, handler, null, 
				"mail", mail, 
				"token_id", token_id); 
	}


	private static final String[] send_createuser_params = 
		{"Sam Spade", "1", "sdfsd@gmail.com", "1111" , "2342", "il", "Ganbook"};

	private static createuser_Response createuser_UserSetParams;

	public static void send_sendkindergartenmail‎() {

		sendGeneric(ApiTypes.sendkindergartenmail‎, SimpleSuccessResponse.class, null, null);
	}


	public static void send_createuser(
			String _name, 
			String type, 
			String mail, 
			String password,
			ICompletionHandler handler) { 		
		String token_id = GoogleServices.getRegistrationId();
		String lang_region = LocaleGetter.get();
		String app_name = MyApp.APP_NAME;
		String name = UtfEncode(_name);

		createuser_UserSetParams = new createuser_Response();
		createuser_UserSetParams.name = name; 
		createuser_UserSetParams.type = type; 
		createuser_UserSetParams.mail = mail; 
		createuser_UserSetParams.password = password; 
		createuser_UserSetParams.lang_region = lang_region; 
		createuser_UserSetParams.app_name = app_name;

		sendGeneric(ApiTypes.createuser, createuser_Response.class, handler, null,
				"name", name, 
				"type", type, 
				"mail", mail,   
				"password", password, 
				"token_id", token_id,
				"lang_region", lang_region, 
				"app_name", app_name);
	}



	private static final String[] send_loginnew_params = 
		{ "sdfs@gamila.com" , "sdfsdfsd", "2", "Samsung", "3423", "Android", 
		"4.4", "1", "33", "324", "12312", "il"};

	public static void send_loginnew(
			String mail, 
			String password,
			ICompletionHandler handler) {
		Class<?> responseClass = null;
		//		if (isTeacher) { 
		//			responseClass = loginnew_Response_Teacher.class;
		//		} else {
		//			responseClass = loginnew_Response_Parent.class;
		//		} 
		String gmt = LocaleGetter.getGmt();
		String scale = "1"; //gggg 
		String app_name =  MyApp.APP_NAME;
		String token_id = GoogleServices.getRegistrationId();
		String lang_region = LocaleGetter.get();
		String app_version = getAppVersionName();
		String object_id = ParseServices.getObjectId();

		sendGeneric(ApiTypes.loginnew, responseClass, handler, null,
				"mail" , mail, 
				"password" , password,
				"parse_id" , object_id,
				"token_id" , token_id,
				"gmt" , gmt, 
				"device_vendor" , device_vendor, 
				"device_model" , device_model, 
				"os_name" , os_name,
				"os_version" , os_version, 
				"scale" , scale,
				"app_version" , app_version, 
				"app_name" , app_name,
				"lang_region", lang_region); 
	}

	public static void send_loginmigrateandroid(
			String user_id, 
			ICompletionHandler handler) {
		Class<?> responseClass = null;
		//		if (isTeacher) { 
		//			responseClass = loginnew_Response_Teacher.class;
		//		} else {
		//			responseClass = loginnew_Response_Parent.class;
		//		} 
		String gmt = LocaleGetter.getGmt();
		String scale = "1"; //gggg 
		String app_name =  MyApp.APP_NAME;
		String token_id = GoogleServices.getRegistrationId();
		String lang_region = LocaleGetter.get();
		String app_version = getAppVersionName();

		sendGeneric(ApiTypes.loginmigrateandroid, responseClass, handler, null,
				"user_id" , user_id, 
				"token_id" , token_id,
				"gmt" , gmt, 
				"device_vendor" , device_vendor, 
				"device_model" , device_model, 
				"os_name" , os_name,
				"os_version" , os_version, 
				"scale" , scale,
				"app_version" , app_version, 
				"app_name" , app_name,
				"lang_region", lang_region); 
	}


	private static final String[] send_userLogout_params = { "234234"};

	public static void send_userLogout(ICompletionHandler handler) {
		String objectId = ParseServices.getObjectId();
		String token_id = GoogleServices.getRegistrationId();
		sendGeneric(ApiTypes.userLogout, SimpleSuccessResponse.class, handler, null, 
				"token_id" , token_id, "parse_id", objectId);
	}


	private static final String[] send_resetpassword_params = { "fsdfs@gmail.com", "Android"};

	public static void send_resetpassword(
			String mail,
			ICompletionHandler handler) {
		sendGeneric(ApiTypes.resetpassword, SimpleSuccessResponse.class, handler, null,
				"mail" , mail,
				"os", os_name); 
	}


	private static final String[] send_updateAlbumView_params = 
		{ "2342423", "2014", "3423"};

	public static void send_updateAlbumView(
			String album_id, 
			String year,
			String num_seen_photos) {
		sendGeneric(ApiTypes.updateAlbumView, SimpleSuccessResponse.class, null, null,
				"album_id", album_id,
				"year", year,
				"num_seen_photos", num_seen_photos);

	}


	private static final String[] send_updatepassword_params = 
		{ "2342423", "sfsdfsd", "he_IL" };

	public static void send_updatepassword(
			String old_password, 
			String new_password,
			ICompletionHandler handler) {		
		String user_id = User.getId();
		String lang_region = LocaleGetter.get(); // he_IL
		sendGeneric(ApiTypes.updatepassword, updatepassword_Response.class, handler, null,
				"old_password", old_password,
				"new_password", new_password,
				"lang_region", lang_region,
				"user_id", user_id);
	}



	private static final String[] send_createsession_params = 
		{ "2342423", "33", "42", "32", "Android", "il"};


	public static void send_createsession(
			String token_id, 
			ICompletionHandler handler) {
		if (token_id == null) {
			token_id = GoogleServices.getRegistrationId(); 			
		}
		String os_version = android.os.Build.VERSION.RELEASE;
		String app_version = getAppVersionName();
		String lang_region = LocaleGetter.get();
		String gmt = getGMT();
		String objectId = ParseServices.getObjectId();

		sendGeneric(ApiTypes.createsession, createsession_response.class, handler, null,
				"token_id", token_id,
				"object_id", objectId,
				"gmt", gmt,
				"os_version", os_version,
				"app_version", app_version,
				"os_name", os_name,
				"lang_region", lang_region);
		//
		//		update
		//			0	No updates
		//			1	Recommended update
		//			2	Mandatory update

	}
	//===========================


	private static final String[] send_reportsavedpicture_params = 
		{ "2342423", };

	public static void send_reportsavedpicture(
			String pic_id) {
		sendGeneric(ApiTypes.reportsavedpicture, SimpleSuccessResponse.class, null, null,
				"pic_id", pic_id);
	}


	private static final String[] send_createkid_params = 
		{ "2342423",  "papa bear", "23423", "sdfsd fsd fsd fa", 
		"054223423", "054566464", "Haifa", "Joe", "1", "sdf", "sdfsdfsdsdf"};

	// Parent API
	//===========================

	public static void send_createkid(
			final String gan_code,
			final String _parent_name,
			final String parent_id,
			final String _parent_address,
			final String parent_phone,
			final String parent_mobile,
			final String _parent_city,
			final String _kid_name,
			final String kid_gender,
			final String kid_bd,
			final String selected_picPath,      
			final ICompletionHandler handler) {

		new Thread() {
			public void run() {
				String remotePath = "";
				if (StrUtils.notEmpty(selected_picPath)) {
					try { 
						ArrayList<String> pathArr = new ArrayList<String>();
						pathArr.add(selected_picPath);
						/*ArrayList<String> remotePathArr = FtpManager.uploadMultipleFiles(pathArr);
						if (remotePathArr.isEmpty() || StrUtils.isEmpty(remotePathArr.get(0))) {
							throw new RuntimeException("Upload failed");
						}
						remotePath = remotePathArr.get(0);*/
					}
					catch (Exception e) {
						if (handler != null) {
							ResultObj err = ResultObj.failure(false, "Failed to upload file " + selected_picPath);
							handler.onComplete(err);
						}
						return;
					}
				}
				String parent_name = UtfEncode(_parent_name);
				String parent_address = UtfEncode(_parent_address);
				String parent_city = UtfEncode(_parent_city);
				String kid_name = UtfEncode(_kid_name);

				//				Class<?> responseClass = StrUtils.notEmpty(gan_code) ?
				//						createkid_Response_With_Code.class : createkid_Response_No_Code.class;2122
				sendGeneric(ApiTypes.createkid, createkid_Response.class, handler, null,
						"gan_code", gan_code,
						"parent_name", parent_name,
						"parent_id", parent_id,
						"parent_address", parent_address,
						"parent_phone", parent_phone,
						"parent_mobile", parent_mobile,
						"parent_city", parent_city,
						"kid_name", kid_name,
						"kid_gender", kid_gender,
						"kid_bd", kid_bd,
						"kid_pic", "",
						"android_pic_path", remotePath);
			}
		}.start();		

	}

	private static final String[] send_setclass_params = 
		{ "42423",  "42342"}; 


	public static void send_setclass(
			String kid_id, 
			String class_id,
			final ICompletionHandler handler) {
		sendGeneric(ApiTypes.setclass, SimpleSuccessResponse.class, handler, null,
				"kid_id", kid_id,
				"class_id", class_id);
	}


	private static final String[] send_updateclass_params = 
		{ "42423",  "42342"}; 

	public static void send_updateclass(
			String kid_id, 
			String class_id,
			ICompletionHandler handler) {
		sendGeneric(ApiTypes.updateclass, SimpleSuccessResponse.class, handler, null,
				"kid_id", kid_id,
				"class_id", class_id);				
	}


	private static final String[] send_getuserkids_params = 
		{ "42423", };  


	public static void send_getuserkids( 
			String parent_id,
			ICompletionHandler handler) {
		sendGeneric(ApiTypes.getuserkids, GetUserKids_Response[].class, handler, null,
				"parent_id",parent_id);
	}

	public static void send_getpickupdropoffhours(
			String user_id,
			ICompletionHandler handler) {
		sendGenericEx(ApiTypes.getpickupdropoffhours, false, getpickupdropoffhours_response.class, handler, null,
				"user_id", user_id);
	}

	public static void send_setpickupdropoffhours(
			String user_id,
			String dropoff_from ,
			String dropoff_to,
			String pickup_from,
			String pickup_to,
			ICompletionHandler handler) {
		sendGenericEx(ApiTypes.savepickupdropoffhours, true, null, handler, null,
				"user_id", user_id,
				"dropoff_from", dropoff_from,
				"dropoff_to", dropoff_to,
				"pickup_from", pickup_from,
				"pickup_to", pickup_to);
	}

	public static void send_savecoordsperclass(
			String class_id,
			double latitude ,
			double longitude,
			ICompletionHandler handler) {
		sendGenericEx(ApiTypes.savecoordsperclass, true, null, handler, null,
				"class_id", class_id,
				"latitude", Double.toString(latitude),
				"longitude", Double.toString(longitude));
	}

	public static void send_saveparentnotification(
			String sender_id,
			String type,
			int status,
			ICompletionHandler handler) {
		sendGenericEx(ApiTypes.saveparentnotification, true, null, handler, null,
				"sender_id", sender_id,
				"type", type,
				"status", ""+status);
	}

	public static void send_getparentstosendnotification(
			String user_id,
			ICompletionHandler handler) {
		sendGenericEx(ApiTypes.getparentstosendnotification, false, getparentstosendnotification_Response.class, handler, null,
				"user_id", user_id);
	}
	public static void send_savenotificationtoparents(
			String user_id,
			String parent_ids,
			ICompletionHandler handler) {
		sendGenericEx(ApiTypes.savenotificationtoparents, true, null, handler, null,
				"user_id", user_id, "parent_ids", parent_ids);
	}

	private static final String[] send_getparents_params = 
		{ "2074", "2015",}; 

	public static void send_getparents(
			String class_id,
			String year,
			ICompletionHandler handler) {
		sendGeneric(ApiTypes.getparents, GetParentAnswer[].class, handler, null,
				"class_id", class_id,
				"year", year);
	}

	public static void send_getparents2(
			String class_id,
			String year,
			String exclude_parent_id,
			ICompletionHandler handler) {
		sendGeneric(ApiTypes.getparents, GetParentAnswer[].class, handler, null,
				"class_id", class_id,
				"year", year,
				"exclude_parent_id", exclude_parent_id);
	}


	private static final String[] send_updateparent_params = 
		{ "42423", "Shani Levi", "0345345", "0323423", "sdfsdfsdfsd sdfs",
		"Tel Aviv" , null}; 

	public static void send_updateparent(
			final String parent_id,
			final String _parent_name,
			final String parent_mobile,
			final String parent_phone,
			final String _parent_address,
			final String _parent_city,
			final JSONArray kids,  //ggggggggggggggggggggFix hhhhhh inside; 
			final ICompletionHandler handler) {		
		//		String _kids = "";
		//		if (kidsArray!= null && kidsArray.length > 0) {
		//			_kids = new Gson().toJson(kidsArray);
		//		}

				final JSONArray _kids = kids;
				
				String parent_name = UtfEncode(_parent_name);
				String parent_address = UtfEncode(_parent_address);
				String parent_city = UtfEncode(_parent_city);
				//		String kids = UtfEncode(_kids);

				

				IJsonHenhancer kidsMap = new IJsonHenhancer() {			 
					@Override
					public void enhance(JSONObject jobj) {
						try {
							jobj.put("kids", _kids);

						} catch (JSONException e) {

							e.printStackTrace();
							throw new RuntimeException();
						}
					}
				};

				// response strucvt is same as that of getuserkids 
				sendGeneric(ApiTypes.updateparent, GetUserKids_Response[].class, handler, kidsMap,
						"parent_id", parent_id,
						"parent_name", parent_name,
						"parent_mobile", parent_mobile,
						"parent_phone", parent_phone,
						"parent_address", parent_address,
						"parent_city", parent_city) ;
		
	}

	// ======================




	// Teacher API
	// ======================



	private static final String[] send_getclass_params = 
		{ "42423", };


	public static void send_getclass(
			String teacher_id,
			ICompletionHandler handler) {
		sendGeneric(ApiTypes.getclass, getclass_Response.class, handler, null, 
				"teacher_id", teacher_id);
	}



	private static final String[] send_activekid_params = 
		{ "42423",};

	public static void send_activekid(
			String kid_id,ICompletionHandler handler) {
		//			String active) {
		sendGeneric(ApiTypes.activekid, SimpleSuccessResponse.class, handler, null,
				"kid_id", kid_id);
	}


	public static void send_setparentpermission( 
			final JSONObject parent_ids, 
			String class_id,ICompletionHandler handler) {
		IJsonHenhancer addParentsMap = new IJsonHenhancer() {			 
			@Override
			public void enhance(JSONObject jobj) {
				try {
					jobj.put("parent_ids", parent_ids);
				} catch (JSONException e) {

					e.printStackTrace();
					throw new RuntimeException();
				}
			}
		};

		sendGeneric(ApiTypes.setparentpermission, SimpleSuccessResponse.class, handler, addParentsMap,
				"class_id", class_id);
	}


	private static final String[] send_getparentsclass_params = 
		{ "42423",};

	public static void send_getparentsclass( 
			String class_id,ICompletionHandler handler) {
		sendGeneric(ApiTypes.getparentsclass, getparentsclass_response[].class, handler, null,
				"class_id", class_id);
	}

	public static void send_getvaadclasses(
			String gan_id,ICompletionHandler handler) {
		sendGeneric(ApiTypes.getvaadclassesnew, getvaadclasses_response[].class, handler, null,
				"gan_id", gan_id);
	}


	public static void send_addclass( 
			String gan_id,
			String _classes, // (names separated with commas)
			ICompletionHandler handler) {
		String classes = UtfEncode(_classes);
		sendGeneric(ApiTypes.addclass, addclass_response.class, handler, null,
				"gan_id", gan_id,
				"classes", classes );
	}


	// ===========================



	// Kindergarten API
	// =====================


	public static void send_getkindergarten(
			String gan_code,
			ICompletionHandler handler) {
		sendGeneric(ApiTypes.getkindergarten,  getkindergarten_response.class, handler, null,
				"gan_code", gan_code);
	}


	public static void send_createkindergarten(
			String _teacher_name,
			String _address,
			String teacher_mobile,
			String _gan_name,
			String gan_phone,
			String _gan_address,
			String _gan_city,
			String _classes,   // (names separated with commas))
			ICompletionHandler handler) {

		String teacher_name = UtfEncode(_teacher_name);
		String address = UtfEncode(_address);
		String gan_name = UtfEncode(_gan_name);
		String gan_address = UtfEncode(_gan_address);
		String gan_city = UtfEncode(_gan_city);
		String classes = UtfEncode(_classes);

		String teacher_id = User.getUserId();
		sendGeneric(ApiTypes.createkindergarten, createkindergarten_response.class, handler, null,
				"teacher_name", teacher_name, 
				"address", address,
				"teacher_mobile", teacher_mobile,
				"teacher_id", teacher_id,  
				"gan_name", gan_name,
				"gan_phone", gan_phone,
				"gan_address", gan_address,
				"gan_city", gan_city,
				"classes", classes); 
	}


	public static void send_updatekindergarten(
			String gan_id,
			String _gan_name,
			String gan_phone,
			String _gan_address,
			String _gan_city,
			JSONArray _classes, // comma separated list of class names
			String _teacher_name,
			String teacher_mobile,
			ICompletionHandler handler) {


		String gan_name = UtfEncode(_gan_name);
		String gan_address = UtfEncode(_gan_address);
		String gan_city = UtfEncode(_gan_city);
		//		String classes = UtfEncode(_classes);
		String teacher_name = UtfEncode(_teacher_name);

		final JSONArray __classes = _classes;

		IJsonHenhancer classesMap = new IJsonHenhancer() {			 
			@Override
			public void enhance(JSONObject jobj) {
				try {
					jobj.put("classes", __classes);

				} catch (JSONException e) {

					e.printStackTrace();
					throw new RuntimeException();
				}
			}
		};

		sendGeneric(ApiTypes.updatekindergarten, updatekindergarten_response.class, handler, classesMap,
				"gan_id", gan_id, 
				"gan_name", gan_name,
				"gan_phone", gan_phone,
				"gan_address", gan_address,
				"gan_city", gan_city,
				"teacher_name", teacher_name,
				"teacher_mobile", teacher_mobile);
	}
	
	public static void send_getgankids(ICompletionHandler handler)
	{
		final String gan_id = User.current.getCurrentGanId();
		
		sendGeneric(ApiTypes.getgankids, getgankids_response[].class, handler, null,
				"gan_id", gan_id);
	}


	public static void send_updateganspermission(String gan_id, String permission_type, String active) {
		sendGeneric(ApiTypes.updateganspermission,  SimpleSuccessResponse.class, null, null,
				"gan_id", gan_id, "permission_type", permission_type, "active", active);
	}



	//===================================



	// Album API
	// ==============


	private static final String[] send_getalbum_params = 
		{ "43323", "2014", }; 

	public static void send_getalbum(
			String class_id, 
			String year,
			ICompletionHandler handler) {
		sendGeneric(ApiTypes.getalbum, getalbum_response[].class, handler, null,
				"class_id", class_id, 
				"year", year);
	}


	public static void send_getalbumcomments(
			String album_id,
			ICompletionHandler handler) {
		sendGeneric(ApiTypes.getalbumcomments, getalbumcomments_response[].class, handler, null, new String[]{"album_id", album_id});
	}

	public static void send_createalbumcomment(
			String album_id,
			String album_comment,
			ICompletionHandler handler) {

		String text = UtfEncode(album_comment);
		sendGeneric(ApiTypes.createalbumcomment, createalbumcomment_Response.class, handler, null, new String[]{"album_id", album_id,"album_comment",text});
	}

	public static void send_updatealbumcomment(
			String comment_id,
			String album_comment,
			ICompletionHandler handler) {

		String text = UtfEncode(album_comment);
		sendGeneric(ApiTypes.updatealbumcomment, SimpleSuccessResponse.class, handler, null, new String[]{"comment_id", comment_id,"album_comment",text});
	}

	public static void send_deletealbumcomment(
			String comment_id,
			ICompletionHandler handler) {
		sendGeneric(ApiTypes.deletealbumcomment, SimpleSuccessResponse.class, handler, null, new String[]{"comment_id", comment_id});
	}


	private static final String[] send_createalbum_params = 
		{ "My Album", "43323",}; 

	public static void send_createalbum(
			String _album_name, 
			String class_id,
			ICompletionHandler handler) {

		String album_name = UtfEncode(_album_name);
		sendGeneric(ApiTypes.createalbum, createalbum_Response.class, handler, null,
				"album_name", album_name,
				"class_id", class_id);				
	}

	public static void send_onpostftp(
			String album_id,
			String gan_id,
			String class_id,
			String ftp_folder,
			String name,
			int duration,
			ICompletionHandler handler) {
		sendGeneric(ApiTypes.onpostftp, createalbum_Response.class, handler, null,
				"album_id", album_id,
				"ftp_folder", ftp_folder,
				"gan_id", gan_id,
				"class_id", class_id,
				"duration", duration+"",
				"name", name);
	}

	public static void send_onposts3(
			String album_id,
			String gan_id,
			String name,
			int duration,
			String create_date,
			ICompletionHandler handler) {
		sendGeneric(ApiTypes.onposts3, createalbum_Response.class, handler, null,
				"album_id", album_id,
				"gan_id", gan_id,
				"duration", duration+"",
				"create_date",create_date,
				"name", name);
	}

	public static void send_updatealbumlike(
			String album_id) {
		sendGeneric(ApiTypes.updatealbumlike, SimpleSuccessResponse.class, null, null,
				"album_id", album_id);
	}



	private static final String[] send_deletealbumj_params = 
		{ "32"}; 


	public static void send_deletealbumj(
			String album_id, ICompletionHandler handler) {
		sendGeneric(ApiTypes.deletealbumj, SimpleSuccessResponse.class, handler, null,
				"album_id", album_id);
	}


	private static final String[] send_editalbum_params = 
		{ "32" , "My Album"}; 

	public static void send_editalbum(
			String album_id, 
			String _album_name) {
		String album_name = UtfEncode(_album_name);
		sendGeneric(ApiTypes.editalbum, SimpleSuccessResponse.class, null, null,
				"album_id", album_id,
				"album_name", album_name);
	}

	//=====================


	// Picture API
	// ==============


	private static final String[] send_getpicture_params = 
		{ "32" , "3"};


	public static void send_getpicture(
			String album_id, 
			String active,
			ICompletionHandler handler) {
		//		Active � 
		//		  By default 1+2 (pictures and video)
		//
		//		picture_active � 
		//		  0 deleted
		//		  1 active
		//		  2 is video

		sendGeneric(ApiTypes.getpicture, getpicture_Response[].class, handler, null,
				"album_id", album_id, 
				"active", active);
	}

	public static void send_updatepicfavorite(
			String pic_id) {

		sendGeneric(ApiTypes.updatepicfavorite, SimpleSuccessResponse.class, null, null,
				"picture_id", pic_id);
	}

	public static void send_getfavorite(ICompletionHandler handler) {

		sendGeneric(ApiTypes.getfavorite, getfavorite_Response[].class, handler, null,"x","1");
	}

	private static final String[] send_deletepicturesj_params = 
		{ "PicName1,PicName2,PicName3"};


	public static void send_deletepicturesj(
			String picture_names, 
			ICompletionHandler handler) { // comman separated image-nales (== last portion of the url)
		// Picture_names: comma separated
		sendGeneric(ApiTypes.deletepicturesj, deletepicturesj_Response.class, handler, null,
				"picture_names", picture_names); 
	}

	private static String getTmpFilePath(File orig_file) {
		String file_name = orig_file.getName();

		File outputDir = MyApp.context.getCacheDir(); // context being the Activity pointer

		File outputFile = new File(outputDir, file_name);
		return outputFile.getAbsolutePath();
	}

	public static void send_uploadpic( //gggggggFix test -- upload child while (Parent) createKid
			final String filePath,
			final String kidId,		
			final String picName,
			final ICompletionHandler handler) {
		
		MyApp.runOnUIThread(new Runnable() { 			
			@Override
			public void run() {
				MultipartEntity reqEntity = new MultipartEntity();
		//		final String picName = kidId + System.currentTimeMillis();
				File file = new File(filePath);
				
				String resizedFilePath = getTmpFilePath(file);
				File local_file = ImageUtils.resizeToFile(filePath, resizedFilePath); 
				FileBody bin = new FileBody(local_file);				
				reqEntity.addPart("file", bin);
				try { 
					reqEntity.addPart("id", new StringBody(kidId));
					reqEntity.addPart("name", new StringBody(picName));
				}
				catch (Exception e) {
					// log error
					if (handler != null) {
						ResultObj err = ResultObj.failure(false, "Failed to create request");
						handler.onComplete(err);
					}
					return;
				}
		
				sendGeneric(ApiTypes.uploadpic, SimpleSuccessResponse.class, 
						handler, null, reqEntity);		
			}
		});
	}


	private static final String[] send_pushafterupload_params = 
		{ "2" , "3" , "23", "3", "0"}; 

	public static void send_pushafterupload(
			String album_id, 
			String class_id,
			int num_success,
			String upload_id, 
			boolean isVideo,
			ICompletionHandler handler) {
		
		sendGeneric(ApiTypes.pushafterupload, SimpleSuccessResponse.class, handler, null,
				"album_id", album_id,
				"class_id", class_id,
				"app_version",getAppVersionName(),
				"upload_task_id", upload_id,
				"num_success", "" + num_success,
				"isVideo", isVideo ? "1" : "0");
	}

	public static void send_createpicture() {
		// call UploadManager instead!!
	}

	//	// ===================================



	// Message API
	// ====================


	private static final String[] send_getmessage_params = 
		{ "2" , "2014" }; 

	public static void send_getmessage(
			String class_id, 
			String year,
			ICompletionHandler handler) {
		sendGeneric(ApiTypes.getmessage, getmessage_Response.class, handler, null,
				"class_id", class_id, 
				"year", year);
	}


	private static final String[] send_createmessage_params = 
		{ "BlaBla" , "4" }; 

	public static void send_createmessage(
			String _text, 
			String class_id,
			ICompletionHandler handler) {
		String text = UtfEncode(_text);
		sendGeneric(ApiTypes.createmessage, createmessage_Response.class, handler, null,
				"text", text, 
				"class_id", class_id);
	}


	private static final String[] send_updatereadmessage_params = 
		{ "22" , "4" , "23" }; 

	public static void send_updatereadmessage(
			String parent_id,
			String class_id,
			String last_msg_id) {
		sendGeneric(ApiTypes.updatereadmessage, SimpleSuccessResponse.class, null, null,
				"parent_id", parent_id,
				"class_id", class_id,
				"last_msg_id", last_msg_id);
	}


	private static final String[] send_getreadmessage_params = 
		{ "22" , "4" ,}; 

	public static void send_getreadmessage(
			String message_id, 
			String class_id,
			ICompletionHandler handler) {
		sendGeneric(ApiTypes.getreadmessage, getreadmessage_Response[].class, handler, null,
				"message_id", message_id, 
				"class_id", class_id);
	}



	private static final String[] send_senduserspush_params = 
		{ "22,23,2" , "4" , "3"}; 


	public static void send_senduserspush(
			final ArrayList<String> userIdArr,
			String message_id, 
			String class_id,
			ICompletionHandler handler) {		

		final JSONArray users_ids = new JSONArray(userIdArr);		
		IJsonHenhancer addUserArr = new IJsonHenhancer() {			 
			@Override
			public void enhance(JSONObject jobj) {
				try {
					jobj.put("users_ids", users_ids);
				} catch (JSONException e) {
					e.printStackTrace();
					throw new RuntimeException();
				}
			}
		};

		sendGeneric(ApiTypes.senduserspush, SimpleSuccessResponse.class, handler, addUserArr,
				//				"users_ids", users_ids, 
				"message_id", message_id, 
				"class_id", class_id); 
	}



	private static final String[] send_deletemessage_params = 
		{ "2"}; 

	public static void send_deletemessage(
			String message_id) { 
		sendGeneric(ApiTypes.deletemessage, SimpleSuccessResponse.class, null, null,
				"message_id", message_id); 
	}
	

	// ========================




	// Token API
	// ==================


	//	private static final String[] send_deletetoken_params = 
	//		{ "MyToken" }; 
	//
	//	public static void send_deletetoken(
	//			String token) {
	//		sendGeneric(ApiTypes.deletetoken, SimpleSuccessResponse.class, null, null,
	//				"token", token);
	//	}


	private static final String[] send_createtoken_params = 
		{ "MyToken" , "android", }; 

	public static void send_createtoken( 
			String token,
			ICompletionHandler handler) {
		Log.i("take22" , "entering create Token: " + token);
		token = null;
		sendGeneric(ApiTypes.createtoken, createtoken_Response.class, handler, null,
				"token", token,
				"type", os_name);
	}




	private static final String[] send_updatetoken_params = 
		{ "2", "MyToken" , }; 

	public static void send_updatetoken(
			String token_id,
			String token) {
		token = null;
		sendGeneric(ApiTypes.updatetoken, SimpleSuccessResponse.class, null, null,
				"token_id", token_id,
				"token", token);
	}

	// ==============================

	
	
	public static void send_createuploadtask( 
			String upload_task_id,
			String album_id,
			String total_num) {
		sendGeneric(ApiTypes.createuploadtask, SimpleSuccessResponse.class, null, null,
				"upload_task_id",upload_task_id,
				"album_id", album_id,
				"total_num", total_num);
	}

	public static void send_updateuploadtask(
			String upload_task_id,
			String uploaded_num,
			String push_sent) {
		sendGeneric(ApiTypes.updateuploadtask, SimpleSuccessResponse.class, null, null,
				"upload_task_id",upload_task_id,
				"uploaded_num", uploaded_num,
				"push_sent", push_sent);
	}

	//=======================================
	public static void send_createuserpush(String push_id) {
		sendGeneric(ApiTypes.createuserpush, SimpleSuccessResponse.class, null, null,
				"token_id",GoogleServices.getRegistrationId(),"push_id",push_id);
	}
	

	@SuppressLint("DefaultLocale")
	private static boolean isTestServer(String url) {
		return url.toLowerCase().startsWith("http:"); // production mode can only be https!
	}

	//{"errors":{"code":"9002","msg":"email retention failed"}}
	private static ResultObj post_request(
			JSONObject jsonObject,
			String request,
			final String orig_requestType,
			final HttpEntity customHttpEntity) {
		if (InDebug.NO_NETWORK) {
			return null;
		}


		HttpParams params = new BasicHttpParams();  
		HttpConnectionParams.setConnectionTimeout(params, CONNECT_TIMEOUT_MSECS);
		HttpConnectionParams.setSoTimeout(params, SOCKET_TIMEOUT_MSECS);		 
		HttpClient client = getNewHttpClient(params);

		HttpPost post;
		ResultObj errorRes;  
		HttpResponse response;
		HttpEntity postEntity;
		try {			
			post = new HttpPost(request);
			if (customHttpEntity == null) { 
				post.setHeader("Accept", "application/json");
				post.setHeader("Content-type", "application/json;charset=UTF-8");
				String s = jsonObject.toString();
//								LogE("" + orig_requestType + " POST params: " + s);

				Log.d(TAG, "post_request: SENDING REQ = " + orig_requestType + " PARAMS + " + s);

				StringEntity se = new StringEntity(s);  
				se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json;charset=UTF-8"));
				postEntity = se;
			} else {
				postEntity = customHttpEntity;
			}
			post.setEntity(postEntity);

			if(User.getAuthToken() != null) {
				post.setHeader("Token", User.getAuthToken());
			}

			response = client.execute(post); // SLOW_ACTION
			if (response == null) {
				throw new ConnectException("Null response!!"); // server/connection error
			}

			HttpEntity resEntity = response.getEntity();
			String s_msg = EntityUtils.toString(resEntity);				
			if (StrUtils.isEmpty(s_msg)) {
				throw new EmptyResultException(CommConsts.EMPTY_RESULT);
			}

			debug__shortDesc += ("\n" + "Raw msg: " + s_msg);

			if ((errorRes = checkIfError(s_msg, orig_requestType)) != null) { 
				debug__shortDesc += ("\n" + "ERROR: " + errorRes.result);
				// error
				//				LogE("" + debug___requestType + " POST returned error result: " + errorRes);
				return errorRes;
			}
			else {
				// success
				debug__shortDesc += ("\n" + "IS SUCCESS");
				LogI("" + orig_requestType + " POST returned success result: " + s_msg);
				return ResultObj.success(s_msg);
			}
		}     
		catch (ConnectException e) 
		{
			debug__shortDesc += ("\n" + "no connection!" + "\n");
			return ResultObj.failure(false, NO_NETWORK_MODE); 
		}
		catch (SocketTimeoutException e) 
		{
			debug__shortDesc += ("\n" + "no connection!" + "\n");
			return ResultObj.failure(false, NO_NETWORK_MODE); 
		}
		catch (UnknownHostException e) 
		{
			debug__shortDesc += ("\n" + "no connection!" + "\n");
			return ResultObj.failure(false, NO_NETWORK_MODE); 
		}
		catch (Exception e) {    
			String e_msg = exceptionToMessage(e);
			debug__shortDesc += ("\n" + "Exception: " + e + "  (Parsed: " + e_msg +")" + "\n\n");
			LogE("" + orig_requestType + " POST failed: " + e + " error msg: " + e_msg);
			return ResultObj.failure(false, e_msg, e, -1);    
		}   
	}  


	private static ResultObj checkIfError(String rawJson, final String orig_requestType) { 
		if (rawJson == null || rawJson.toLowerCase().equals("false")) { // can happen
			return ResultObj.failure(true, "", null, -1);
		}

		JSONObject errorMsg; // keep JSONObject !
		try { 
			errorMsg = new JSONObject(rawJson);
		}
		catch (Exception e) {
			// not an error!
			int jj=24;
			jj++;
			return null; // not an error
		}

		try { 
			boolean has_errors = errorMsg.has("errors");
			if (has_errors) {
				ErrorResult errRes = new Gson().fromJson(rawJson, ErrorResult.class);
				if (errRes != null) {
					if (errRes.errors != null) {
						int code = errRes.errors.code;
						String msg = errRes.errors.msg;
						String e_msg = msg;//"Message: " + msg + ", Code: " + code;
						LogE("" + orig_requestType + " returned error response: " + e_msg);
						return ResultObj.failure(true, e_msg, null, code);
					}
					else {
						LogE("" + orig_requestType + " returned UNKNOWN error response");
						return ResultObj.failure(true, "Unknown error", null, -1);
					}
				}
			}
			else {
				int jj=234;
				jj++;
			}
		} 
		catch (Exception e) {

			int jj=24;
			jj++;
			LogE("" + orig_requestType + " checkIfError failed: " + e);
			e.printStackTrace();
		} 
		return null; // not an error
	}



	private static Exception debug____e; 

	private static ResultObj getXmlFromUrl(String urlStr, final String orig_requestType) { // GET
		if (InDebug.NO_NETWORK) {
			return null;
		}
		LogI("" + orig_requestType + " GET url: " + urlStr);
		HttpURLConnection con = null;
		URL url;
		InputStream is = null;
		int http_status; 
		try {
			URLEncoder.encode(urlStr, "UTF-8");
			url = new URL(urlStr);			
			if(url.getProtocol().toLowerCase().equals("https")) {
				System.out.println("Working in https mode");
				//trustAllHosts();
				HttpsURLConnection httpsCon = (HttpsURLConnection) url.openConnection();
				//httpsCon.setHostnameVerifier(DO_NOT_VERIFY);
				con = httpsCon;
			} else {
				con = (HttpURLConnection) url.openConnection();
			}
			con.setReadTimeout(SOCKET_TIMEOUT_MSECS);
			con.setConnectTimeout(CONNECT_TIMEOUT_MSECS);
			con.setDoInput(true);
			con.setRequestMethod("GET");
			con.setRequestProperty("Accept-Charset", "UTF-8");
			con.setRequestProperty("Content-Type", "text/plain; charset=utf-8");

			if(User.getAuthToken() != null) {
				con.setRequestProperty("Token", User.getAuthToken());
			}

			/*String basic = "Basic " + Base64.encodeToString((CommConsts.USER_PASS).getBytes(), Base64.NO_WRAP);
			con.setRequestProperty("Authorization", basic);*/
			con.connect(); // SLOW_ACTION
			http_status = con.getResponseCode(); 

			boolean success = http_status < HttpStatus.SC_BAD_REQUEST;
			if (success) { 
				is = con.getInputStream();
			} else { 
				is = con.getErrorStream(); 
			}
			final String msg = try_reading(is, orig_requestType); 
			debug__shortDesc += ("\n" + (success ? "Success " : "Error ") + " response: " + msg);
			String mode = success ? "success" : "ERROR";
			LogI("" + orig_requestType + " GET " + mode + " response received: " + msg);
			if (success) {
				return ResultObj.success(msg);
			}
			else { 
				try { 
					ResultObj err_response = checkIfError(msg, orig_requestType);
					if (err_response != null) {
						return err_response;
					}
				}
				catch (Exception e){
					int jj=234;
					jj++;
				}
				return ResultObj.failure(true, msg, null, -1); 
			}
		} 
		catch (ConnectException e) 
		{
			debug__shortDesc += ("\n" + "no connection!" + "\n");
			return ResultObj.failure(false, NO_NETWORK_MODE); 
		}
		catch (SocketTimeoutException e) 
		{
			debug__shortDesc += ("\n" + "no connection!" + "\n");
			return ResultObj.failure(false, NO_NETWORK_MODE); 
		}
		catch (UnknownHostException e) 
		{
			debug__shortDesc += ("\n" + "no connection!" + "\n");
			return ResultObj.failure(false, NO_NETWORK_MODE); 
		}
		catch (Exception e) {
			LogE("" + orig_requestType + " GET failed: " + e);
			int jj=5435;
			jj++;    
			debug____e = e;
			String e_msg = exceptionToMessage(e);
			debug__shortDesc += ("\n" + "Exception: " + e + "  (Parsed: " + e_msg +")" + "\n\n");
			return ResultObj.failure(false, e_msg, e, -1);
		}
		finally {
			if (con != null) {
				try { con.disconnect(); } catch (Exception e) {}
			}
		} 
	}

	private static void sendGeneric( 
			final String requestType,  
			Class<?> responseClass,
			ICompletionHandler handler, 
			final IJsonHenhancer jsonHenhancer,
			String... paramMap) {
		sendGeneric( 
				requestType, 
				responseClass,
				handler, 
				jsonHenhancer,
				null, //final HttpEntity customHttpEntity,
				paramMap);				
	}  


	private static void sendGeneric(
			final String requestType, 
			Class<?> responseClass,  
			ICompletionHandler handler,   
			final IJsonHenhancer jsonHenhancer,
			final HttpEntity customHttpEntity,
			String... paramMap) {		   
		int jj=345;      
		jj++;  		  
//		if (InDebug.NO_NETWORK_trans) { 
//			if (handler != null) {
//				handler.onComplete(ResultObj.failure(false, "NO_NETWORK Mode")); 
//			}
//			return;
//		}		

		if (disabledDueToLogout(requestType)) {
			return; 
		}
		debug__shortDesc = "Running " + requestType + "\n";

		final boolean post = 
				requestType.equals(ApiTypes.getuserkids) || !requestType.toLowerCase().startsWith("get");

		if (!NetworkUtils.isConnected()) {
			// show msg to user?
			if(handler != null)
			{
				handler.onComplete(ResultObj.failure(false, NO_NETWORK_MODE));
			}
			debug__shortDesc += ("\n" + "no connection!" + "\n");
			return;
		}

		debug__shortDesc += ("\n" + "Mod = " + (post ? "post" : "get"));

		JSONObject req = new JSONObject();
		String key = null;
		String requestWithParams = requestType + ".json";
		boolean first = true;
		for (String s: paramMap) { 
			if (key == null) {
				key = s;
				if (key == null) {
					jj=234;
					jj++;
					__throw("Key cannot be null! in " + req_func_name);
				}
			}  
			else {
				String val = s;
				if (post) {
					try {
						req.put(key, val);
					} catch (JSONException e) {

						e.printStackTrace();
						__throw("Bad key/val found! in " + req_func_name + ": " + key + "," + val);
					}
					debug__shortDesc += ("\n" + "     " + key + ": " + val);
				} 
				else { // GET
					if (first) {
						first = false;
						requestWithParams += "?" + key + "=" + val;
					} else {
						requestWithParams += "&" + key + "=" + val;
					}
				}
				key = null;
			}
		}
		if (key != null) {
			jj=234;
			jj++;
			__throw("A key with no values was found! in " + req_func_name);
		}

		if (post) {
			debug__shortDesc += ("\n\n");
		}
		String url = BASE_URL + requestWithParams;

		Log.d(TAG, "sendGeneric: req = " + requestWithParams);

		inner_sendRequest(req, null, url, responseClass, handler, post, 
				requestWithParams, requestType, jsonHenhancer, customHttpEntity);
	}

	private static void sendGenericEx(
			final String requestType,
			boolean post,
			Class<?> responseClass,
			ICompletionHandler handler,
			final IJsonHenhancer jsonHenhancer,
			String... paramMap) {

		if (disabledDueToLogout(requestType)) {
			return;
		}
		debug__shortDesc = "Running " + requestType + "\n";

		if (!NetworkUtils.isConnected()) {
			// show msg to user?
			if(handler != null)
			{
				handler.onComplete(ResultObj.failure(false, NO_NETWORK_MODE));
			}
			debug__shortDesc += ("\n" + "no connection!" + "\n");
			return;
		}

		debug__shortDesc += ("\n" + "Mod = " + (post ? "post" : "get"));

		JSONObject req = new JSONObject();
		String key = null;
		String requestWithParams = requestType + ".json";
		boolean first = true;
		for (String s: paramMap) {
			if (key == null) {
				key = s;
				if (key == null) {
					__throw("Key cannot be null! in " + req_func_name);
				}
			}
			else {
				String val = s;
				if (post) {
					try {
						req.put(key, val);
					} catch (JSONException e) {

						e.printStackTrace();
						__throw("Bad key/val found! in " + req_func_name + ": " + key + "," + val);
					}
					debug__shortDesc += ("\n" + "     " + key + ": " + val);
				}
				else { // GET
					if (first) {
						first = false;
						requestWithParams += "?" + key + "=" + val;
					} else {
						requestWithParams += "&" + key + "=" + val;
					}
				}
				key = null;
			}
		}
		if (key != null) {
			__throw("A key with no values was found! in " + req_func_name);
		}

		if (post) {
			debug__shortDesc += ("\n\n");
		}
		String url = BASE_URL + requestWithParams;

		Log.d(TAG, "sendGeneric: req = " + requestWithParams);

		inner_sendRequest(req, null, url, responseClass, handler, post,
				requestWithParams, requestType, jsonHenhancer, null);
	}

	private static boolean disabledDueToLogout(String requestType) {
		if (MyApp.loggingOut) {
			if (!ApiTypes.userLogout.equals(requestType)) {
				return true;
			}
		}
		return false;
	}

	private static JSONObject debug_obj;
	private static String debug_url;

	public static final String[] UNSIGNED_APIS = {
		ApiTypes.createuser, 
		ApiTypes.getkindergarten,
		ApiTypes.loginnew,
		ApiTypes.loginmigrateandroid,
		ApiTypes.resetpassword,
		ApiTypes.createtoken,
		ApiTypes.updatetoken,
		ApiTypes.retention,
		ApiTypes.getpickupdropoffhours,
		ApiTypes.savepickupdropoffhours,
		ApiTypes.getparentstosendnotification,
		ApiTypes.savenotificationtoparents
	};
	//		ApiTypes.insertpictures,
	//		ApiTypes.createsession };



	@SuppressLint("DefaultLocale")
	@SuppressWarnings("rawtypes") 
	private static void inner_sendRequest(
			final JSONObject jsonObject,  
			final String request, 
			final String url,
			final Class responseClass,
			final ICompletionHandler handler,
			final boolean post,
			final String requestWithParams,
			final String orig_requestType,
			final IJsonHenhancer jsonHenhancer,
			final HttpEntity customHttpEntity) {
		// before JsonSenderTask.doInBackground

		final Class _responseClass = responseClass;

		MyApp.runOnUIThread(new Runnable() { 			
			@SuppressLint("StaticFieldLeak")
            @Override
			public void run() {
				new AsyncTask<Void, Void, ResultObj>() { //gggFix use threadPool ??	 	
					@Override 
					protected ResultObj doInBackground(Void... params) {
						try { 
							return inner_doInBackground(params);
						}
						catch(Exception e) {
							int jj=234;
							jj++;
						}
						return null;
					}

					private ResultObj inner_doInBackground(Void... params) {
						// starting log segment
						if (disabledDueToLogout(orig_requestType)) {
							return null;
						}
						LogI("");
						LogI(""); 
						LogI("");
						LogI(""); 
						LogI("Starting for " + orig_requestType);
						String ts = "" + System.currentTimeMillis(); 
						String apiKey = User.getApiKey(); //see CREATE_USER_OBJ_FROM_RESPONSE;
						String userId = User.getUserId();  
						Integer signed_user_id = 0;
						String sig = "";

						int jj=234; 
						jj++;

						boolean should_sign = 
								!Arrays.asList(UNSIGNED_APIS).contains(orig_requestType);

						if (should_sign && StrUtils.notEmpty(apiKey)) { 

							if (StrUtils.notEmpty(apiKey) && StrUtils.notEmpty(userId)) { 
								//						signed_user_id = Integer.valueOf(userId) + User.USER_ID_KEY;
								//						userId = signed_user_id.toString();

								ts = "" + System.currentTimeMillis();
								sig = sig_request(apiKey, requestWithParams, post, ts, userId); 
							}
							else {
								jj=234;
								jj++;
							}
						}


						ResultObj result = null;
						if (!post) { // GET
							jj=234; 
							jj++;
							String f_url = url;
							if (should_sign && StrUtils.notEmpty(apiKey)) { 

								f_url = url + "&sig=" + sig + "&ts=" + ts + "&user_id=" + userId;
								debug__shortDesc += ("\n" + "Url = " + url + "\n\n");
							} 
							debug_url = f_url;
							result = getXmlFromUrl(f_url, orig_requestType);
						}
						else { // POST
							if (should_sign && StrUtils.notEmpty(apiKey)) {  
								try {  
									jsonObject.put("ts", ts);  
									jsonObject.put("user_id", userId);
									jsonObject.put("sig", sig);
								}
								catch (Exception e) {
									__throw("Bad key/val sig params in " + req_func_name );
								}
							}
							if (jsonHenhancer != null) {  
								jsonHenhancer.enhance(jsonObject); 
							} 
							debug_obj = jsonObject;
							debug_url = url;
							result = post_request(jsonObject, url, orig_requestType, customHttpEntity); 
						}				 
						parseResult(result, orig_requestType);   
						debug__ctr++;
						String xx = orig_requestType;
						return result; 
					}	 


					private void parseResult(final ResultObj resultObj, final String requestType) {				
						int jjj=234; 
						jjj++;				
						if (disabledDueToLogout(orig_requestType)) {
							return;
						} 

						if (resultObj == null) {
							debug____map.put(orig_requestType, new Debug_BaseResponse("No Result!!"));
							debug__shortDesc += ("\n" + "EMPTY RESULT!");
							// handle error
							return;
						}

						if (resultObj.internalErrorOccurred()) {
							String errmsg = "INTERNAL ERROR: " + resultObj.result + ", E = " + resultObj.exception;
							debug__shortDesc += ("\n" + "Internal error occured: " + errmsg);
							debug____map.put(orig_requestType, new Debug_BaseResponse(errmsg));
							// handle error
							return;
						}  

						try {     
							final boolean succeeded = resultObj.succeeded;
							final String json = resultObj.result; 
							//Type t_token = new TypeToken<GetUserKids_Response[]>(){}.getType();
							if (!succeeded) {
								// should not attepts to parse!
								String xx = orig_requestType;
								debug__shortDesc += ("\n" + "Failure result successfully parsed. Msg =" + resultObj.result);
								return; 
							}

							Class resposeClassToUse = responseClass;
							if (responseClass==null && ApiTypes.loginnew.equals(orig_requestType)) {
								// special case:
								resposeClassToUse = getLoginResponseType(json);
							}

							if (ApiTypes.createsession.equals(orig_requestType)) {
								MyApp.sessionWasCreated = true;
							}

							if (resposeClassToUse != null) {												
								@SuppressWarnings("unchecked")
								Object resultArr = new Gson().fromJson(json, resposeClassToUse);
								
								if (resultArr == null) {
									LogE("" + orig_requestType + " cannot parse response json!");
									debug__shortDesc += ("\n" + "Cannot Parse response Json!");
									throw new RuntimeException();
								}

								if (!resultArr.getClass().isArray()) {
									resultArr = new Object[] {resultArr};
								} 

								if (ApiTypes.getuserkids.equals(requestType)) {
									UserKids.set(resultArr);
								}

								// iterate result array
								int length = Array.getLength(resultArr);
								for (int i = 0; i < length; i++) { 
									Object singleResult = Array.get(resultArr, i);
									if (!(singleResult instanceof BaseResponse)) {
										debug__shortDesc += ("\n" + "Badd Result!");
										throw new RuntimeException();
									}  
									BaseResponse response = (BaseResponse)singleResult;
									response.postLoad(); //!

									resultObj.addResponse(response);

									if (response instanceof Response_With_User) {
										handleResponseWithUser(response);
									}

									//						debug__elem = response;
									LogI("" + orig_requestType + " response " + (i+1) + " : " + response.toString());

									Log.i("take22" , "success response = " + response.toString());

									debug____map.put(orig_requestType, response);
									debug__shortDesc += ("\n" + "Valid result of type " + response.getClass().getSimpleName());
								}
								int jj=234;
								jj++;
							}							
						} 
						catch (Exception e) {
							LogE("" + orig_requestType + " response parsing failed: " + e);
							debug__shortDesc += ("\n" + "Parse result failed: " + e);
							int jj=42;
							jj++;
						}

					}		 			

					@Override 
					protected void onPostExecute(ResultObj result) {
						if (disabledDueToLogout(orig_requestType)) {
							return;
						}
						if (handler != null) { 
							handler.onComplete(result); 
						}				

//						if ("send_updatetoken".equals(req_func_name)) {
//							int jj=234;
//							jj++;
//							int jjj = debug__ctr;
//							send_results_via_mail();
//						}
					}
					//			}.execute();
				}.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR); // up to numCpus + 1 parallel threads
			}
		});
	}



	private static Class<?> getLoginResponseType(String rawJson) {
		String typeVal;
		try {
			typeVal = new JSONObject(rawJson).getString("type");
		} catch (JSONException e1) {
			int jj=234;
			jj++; 
			return null;
		}
		boolean isParentResponse = User.isParentType(typeVal);
		Class<?> responseType;
		if (isParentResponse) {
			responseType = loginnew_Response_Parent.class;
		} else {
			responseType = loginnew_Response_Teacher.class;
		}
		return responseType;
	}


	public static void handleResponseWithUser(BaseResponse response) {
		int jj=234;
		jj++;
		if (response instanceof createuser_Response && createuser_UserSetParams != null) {
			createuser_Response fromServer = (createuser_Response)response;
			createuser_UserSetParams.id = fromServer.id;
			createuser_UserSetParams.current_year = fromServer.current_year;
			createuser_UserSetParams.token_id = fromServer.token_id;
			response = createuser_UserSetParams;
			createuser_UserSetParams = null;
		} 
		User.setFromResponse((Response_With_User)response);
		jj=234; 
		jj++;
	}


	public static String getAppVersionName() {
		final Context context = MyApp.context;
		try {
			PackageInfo packageInfo = context.getPackageManager()
					.getPackageInfo(context.getPackageName(), 0);
			return packageInfo.versionName;
		} catch (NameNotFoundException e) {
			// should never happen
			throw new RuntimeException("Could not get package name: " + e);
		}
	}


	public static String getGMT() {
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"), Locale.getDefault());
		java.util.Date currentLocalTime = calendar.getTime();
		SimpleDateFormat date = new SimpleDateFormat("Z");
		String localTime = date.format(currentLocalTime);
		String bagBegin = localTime.substring(0,3);
		String bagEnd = localTime.substring(3);
		localTime = bagBegin + ":" + bagEnd;
		return localTime;
	}

	public static String try_reading(InputStream in, final String orig_requestType) {
		String queryResult = ""; //gggggggggggggFix test
		try {
			queryResult = IOUtils.toString(in, "UTF-8");
			//			queryResult = insertText(queryResult);  
		} catch (IOException e) { 
			int jj=234;
			jj++;
			e.printStackTrace();
		}
		return queryResult; 
	}

	private static String insertText(String s)	{
		String result = null;
		try {
			result = URLEncoder.encode(s, "UTF-8")
					.replaceAll("\\+", "%20")
					.replaceAll("\\%21", "!")
					.replaceAll("\\%27", "'")
					.replaceAll("\\%28", "(")
					.replaceAll("\\%29", ")")
					.replaceAll("\\%7E", "~");
		}
		catch (Exception e) {
			int jj=234;
			jj++;
			result = s;
		}
		return result;
	}  


	public static String sig_request(
			String apiKey,
			String requestWithParams, 
			boolean post,  
			String ts,  
			String userId) {
		requestWithParams = requestWithParams.replace(CommConsts.HOST + CommConsts.REQUEST_HOST, "");
		requestWithParams = requestWithParams.replace(".json", "");

		if (!post) {
			if (requestWithParams.endsWith("?")) {
				requestWithParams += "user_id=" + userId;
			}
			else {
				requestWithParams += "&user_id=" + userId;
			}
		}

		Log.d(TAG, "sig_request: " + requestWithParams);

		String str = requestWithParams + apiKey + ts;
		String sig = "";

		Log.d(TAG, "sig_request: str = " + str);
		try {
			sig = SignatureUtils.calculateRFC2104HMAC(str, apiKey);
		} catch (Exception e) {
			e.printStackTrace();
		}

		Log.d(TAG, "sig_request: SIG === " + sig);
		return sig;
	}

	private static String exceptionToMessage(Exception e) {
		if (e == null) {
			return CommConsts.ERROR;
		}
		if (e instanceof ConnectException) {
			return CommConsts.LOST_CONNECTION;
		}
		if (e instanceof SocketTimeoutException) {
			return CommConsts.LOST_CONNECTION;
		}
		if (e instanceof ParseException) {
			return CommConsts.ERROR;
		}
		if (e instanceof UnknownHostException) {
			return CommConsts.LOST_CONNECTION;
		}
		if (e instanceof IOException) {		
			return CommConsts.ERROR; 
		}
		if (e instanceof EmptyResultException) {
			return CommConsts.EMPTY_RESULT;
		}

		return CommConsts.ERROR;
	}

	public static void LogI(String msg) {
		Log.i(_TAG, msg);
		debug_testResults += (msg + "\n");
	}

	public static void LogE(String msg) {
		Log.e(_TAG, msg);
		debug_testResults += (msg + "\n");
	}


	public static String UtfEncode(String s) {
		if (s==null) {
			return s;
		}
		try {
//			s = URLEncoder.encode(s,"UTF-8");
//			//			s = insertText(s);
//			s = s.replaceAll(Pattern.quote("+"), " "); // e.g. "\\$"

			s = URLEncoder.encode(s, "UTF-8")
					.replaceAll("\\+", "%20")
					.replaceAll("\\%21", "!")
					.replaceAll("\\%27", "'")
					.replaceAll("\\%28", "(")
					.replaceAll("\\%29", ")")
					.replaceAll("\\%7E", "~");
		}  
		catch (UnsupportedEncodingException e) {
			int jj=234;
			jj++;
			e.printStackTrace();
		}
		return s;
	}


	public static final String[] debug_allRequestNames = {
		ApiTypes.retention,
		ApiTypes.createuser,
		ApiTypes.loginnew,
		ApiTypes.userLogout ,
		ApiTypes.resetpassword ,
		//		ApiTypes.changepassword ,
		ApiTypes.updateAlbumView ,
		ApiTypes.updatepassword ,
		ApiTypes.createsession , 
		ApiTypes.reportsavedpicture ,				
		// PARENT
		ApiTypes.createkid  ,
		ApiTypes.setclass  ,
		ApiTypes.updateclass  ,
		ApiTypes.getuserkids  ,
		ApiTypes.getparents,
		ApiTypes.updateparent ,
		//		ApiTypes.getactivekids ,  								
		// Teacher API
		ApiTypes.getclass ,
		ApiTypes.activekid , 
		ApiTypes.setparentpermission ,
		ApiTypes.getparentsclass ,				
		// Album API
		ApiTypes.getalbum ,
		ApiTypes.createalbum ,
		ApiTypes.deletealbumj ,
		ApiTypes.editalbum ,				
		// Picture API
		ApiTypes.getpicture ,
		ApiTypes.deletepicturesj ,
		//		ApiTypes.insertpictures ,
		ApiTypes.uploadpic ,
		ApiTypes.pushafterupload ,

		// Message API  
		ApiTypes.getmessage ,
		ApiTypes.createmessage , 
		ApiTypes.updatereadmessage ,
		ApiTypes.getreadmessage ,
		ApiTypes.senduserspush ,
		ApiTypes.deletemessage ,			
		// Token API
		//		ApiTypes.deletetoken ,
		ApiTypes.createtoken ,
		ApiTypes.updatetoken ,			
	};





	public static volatile String debug__shortDesc;



	public static String ApiToUrl(String apiName) {
		String requestWithParams = apiName + ".json";
		String fullUrl = JsonTransmitter.BASE_URL + requestWithParams;
		return fullUrl;
	}

	private static HttpClient getNewHttpClient(HttpParams params) {
		try {
			//trustAllHosts();

			KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
			trustStore.load(null, null);

			MySSLSocketFactory sf = new MySSLSocketFactory(trustStore);
			//sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

			//params.setVersion(params, HttpVersion.HTTP_1_1);
			//params.setContentCharset(params, HTTP.UTF_8);

			SchemeRegistry registry = new SchemeRegistry();
			registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
			registry.register(new Scheme("https", sf, 443));

			ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

			return new DefaultHttpClient(ccm, params);
		} catch (Exception e) {
			return new DefaultHttpClient(params);
		}
	}
}

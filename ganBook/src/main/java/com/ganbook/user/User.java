package com.ganbook.user;

import android.os.AsyncTask;
import android.util.Log;

import com.ganbook.activities.MainActivity;
import com.ganbook.app.MyApp;
import com.ganbook.communication.datamodel.ClassDetails_2;
import com.ganbook.communication.datamodel.ClassDetails_2.ClassHistory;
import com.ganbook.communication.datamodel.GanDetails;
import com.ganbook.communication.datamodel.KidDetails;
import com.ganbook.communication.json.BaseKindegartenResponse;
import com.ganbook.communication.json.GetUserKids_Response;
import com.ganbook.communication.json.HistoryDetails;
import com.ganbook.communication.json.Response_With_User;
import com.ganbook.communication.json.createsession_response;
import com.ganbook.communication.json.createuser_Response;
import com.ganbook.communication.json.getalbum_response;
import com.ganbook.communication.json.getclass_Response;
import com.ganbook.communication.json.getevents_Response;
import com.ganbook.communication.json.getfavorite_Response;
import com.ganbook.communication.json.getpicture_Response;
import com.ganbook.communication.json.loginnew_Response_Parent;
import com.ganbook.communication.json.loginnew_Response_Teacher;
import com.ganbook.fragments.ContactListFragment;
import com.ganbook.fragments.MessageListFragment;
import com.ganbook.name.NameUtils;
import com.ganbook.sharedprefs.SPReader;
import com.ganbook.sharedprefs.SPWriter;
import com.ganbook.utils.ActiveUtils;
import com.ganbook.utils.GenderUtils;
import com.ganbook.utils.StrUtils;
import com.google.gson.Gson;
import com.project.ganim.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;

public class User {
	
	//ggggg -- on app going up call loadFromLocalCache()
	
	public static final Integer USER_ID_KEY = 1000365478;


	public static final String Type_Parent = "1";
	public static final String Type_Teacher = "2";
	public static final String Type_Father = "3";
	public static final String Type_Mother  = "4";
	public static final String Type_Staff = "5";
	public static final String Type_Student = "6";

	private static final String SENT_CODE_JSON = "SENT_CODE_JSON";
	private static final String SENT_CODES = "SENT_CODES__1";
	
	
	private static String THIS_USER_FILE = "THIS_USER_FILE"; 
	private static String THIS_USER_COLUMN = "THIS_USER_COLUMN";

	
	public static volatile User current; 
	
	public String id; // = "0";
	public String type;
	public String active;
	public String activation;
	public String mail;
	public String password;
	public volatile String first_name;
	public volatile String last_name;
	public String phone;
	public String mobile;
	public String address;
	public String city;
	public String current_year;
	public String max_images_batch_upload_android;
	public String video_size;
	public String max_image_size_android;
	public boolean new_year;
	public boolean like_forbidden;
	public boolean comment_forbidden;
	public boolean contactsForbidden;
	public String currentDrawingAlbumId;
	public String update = "0";
	private String api_key;
	public String auth_token;
	public String atoken; // Amazon federation token

	public Date dropOff_from;
	public Date dropOff_to;
	public Date pickUp_from;
	public Date pickUp_to;

	public long dropOff_from_minutes = -1;
	public long dropOff_to_minutes = -1;
	public long pickUp_from_minutes = -1;
	public long pickUp_to_minutes = -1;

	private GanDetails ganDetails;
	
	public GetUserKids_Response[] userkids; // for parent
	public volatile getclass_Response classList; // for teacher

	HashMap<String, ArrayList<getfavorite_Response>> favorites;

	//private ArrayList<getalbum_response> albumList;
	private HashMap<String, ArrayList<getalbum_response>> albumMap;
	private TreeMap<String, ArrayList<getevents_Response.Event>> eventMap;
	
	private static volatile int current_KidInd = 0; 
	private static volatile int current_ClassInd = 0;

	public void setCurrentKidInd(int position) {
		if (position < 0 || position >= userkids.length) {
			throw new RuntimeException();
		}
		current_KidInd = position;
	}

	public void setCurrentClassInd(int position) {
		int jj=234;
		jj++;
		if (position < 0 || position >= classList.classes.length) {
			throw new RuntimeException();
		}
		current_ClassInd = position;
	}
	
	public GetUserKids_Response getCurrentKid() {
		if (userkids != null) {
			return userkids[current_KidInd];
		}
		return null;
	}
	
	public String getCurrentKidId()
	{
		return getCurrentKid().kid_id;
	}

	public String getCurrentKidPic()
	{
		return getCurrentKid().kid_pic;
	}

	public String getCurrentKidName()
	{
		return getCurrentKid().kid_name;
	}

	public String getCurrentKidDrawingAlbumName()
	{
		return getCurrentKid().drawingName;
	}

	public void setCurrentKidDrawingAlbumName(String drawingName)
	{
		getCurrentKid().drawingName = drawingName;
	}

	public String getCurrentDrawingAlbumId()
	{
		return getCurrentKid().drawingAlbumId;
	}

	public void setCurrentDrawingAlbumId(String drawingAlbumId)
	{
		getCurrentKid().drawingAlbumId = drawingAlbumId;
	}

	public String getCurrentKidGender()
	{
		return getCurrentKid().kid_gender;
	}

	public boolean getCurrentKidLikesForbidden()
	{
		return getCurrentKid().like_forbidden;
	}

	public boolean getCurrentKidCommentsForbidden()
	{
		return getCurrentKid().comment_forbidden;
	}

	public boolean getCurrentKidContactsForbidden() {
		return getCurrentKid().contactsForbidden;
	}

	public HistoryDetails[] getCurrentKidHistory()
	{
		return getCurrentKid().history;
	}	
	
	public ClassHistory[] getCurrentClassHistory()
	{
		return getCurrentClass().history;
	}	
	
	public HistoryDetails getCurrentKidHistoryByYear(String year)
	{
		if(getCurrentKid().history == null) return null;
		for (HistoryDetails historyDetails : getCurrentKid().history) {
			if(year.equals(historyDetails.class_year))
			{
				return historyDetails;
			}
		}
		return null;
	}
	
	public static void updateKindegartenResponse(BaseKindegartenResponse response) {
		if (current != null && response != null) {
//			current.ganDetails = response.gan;
			current.classList = getclass_Response.fromKindegartenResponse(response);
			getclass_Response tmp = current.classList;
			ClassDetails_2[] t_classes = tmp.classes; 
			int jj=234;
			jj++;
		}
	}

	public void updatePendingParents(String __pending_parents)
	{
		if(__pending_parents != null)
		{
			getCurrentClass().pending_parents = __pending_parents;
			return;
		}
		String pending_parents = getCurrentClass().pending_parents;
		int pending_parents_int = 0;
		
		if(pending_parents != null)
		{
			pending_parents_int = Integer.valueOf(pending_parents);
		}
		
		getCurrentClass().pending_parents = String.valueOf(pending_parents_int-1);
	}

	public void updateUnreadMessage()
	{
		getCurrentKid().unread_messages = null;
	}
	
	public void updateUnseenPhotos(String unseen_photos)
	{
		getCurrentKid().unseen_photos = unseen_photos;
	}
	
	public static void loadFromLocalCache() {
  		new AsyncTask<Void, Void, Void>() {
			@Override 
			protected Void doInBackground(Void... params) {
				blocking_loadFromLocalCache(true);
				return null;  
			}
		}.execute();		
		
	}
	
	public static User blocking_loadFromLocalCache(boolean forceRefresh) {
		if (!forceRefresh && current != null) { 
			return current;
		}		
		try {  
			String json = new SPReader(THIS_USER_FILE).getString(THIS_USER_COLUMN, null);
			if (StrUtils.notEmpty(json)) { 
				int jj=234;
				jj++;
				current = new Gson().fromJson(json, User.class);
				//current.setDropPickMinutes();
				return current;
			}
		}
		catch (Exception e) {
			int jj=234;
			jj++;
		}
		return null;
				
	}
	
	
	public static void setFromResponse(Response_With_User response) {
		if (current != null) {
			int jj=234;
			jj++; 
		}
		if (response instanceof getclass_Response) {
			getclass_Response res = (getclass_Response) response;
			if (res.teacher_id == null) {
				return;
			}
		}

		current = new User(response, current); 
		int jj=234;
		jj++;
		//if(!StrUtils.isEmpty(current.id)) {
			async_writeUserToLocaCache();
		//}
	}	
	
	
	private static void async_writeUserToLocaCache() {
  		new AsyncTask<Void, Void, Void>() {
			@Override 
			protected Void doInBackground(Void... params) {
				String json = new Gson().toJson(current);
				if (StrUtils.notEmpty(json)) {
					new SPWriter(THIS_USER_FILE).putString(THIS_USER_COLUMN, json).commit();
				}
				return null; 
			}
		}.execute();
		

	}

	public User(Response_With_User response, User priorInst) {
		// reset indexes
//		current_KidInd = 0; 
//		current_ClassInd = 0; 

		//setDefaultDropPick();

		if (priorInst != null) {
			ganDetails = priorInst.ganDetails;			
			userkids = priorInst.userkids; // for parent
			classList = priorInst.classList; // for teacher
//			albumList = priorInst.albumList;
			albumMap = priorInst.albumMap;
			type = priorInst.type;
			api_key = priorInst.api_key;
			auth_token = priorInst.auth_token;
			atoken = priorInst.atoken;
			max_image_size_android = priorInst.max_image_size_android;
			max_images_batch_upload_android = priorInst.max_images_batch_upload_android;
			video_size = priorInst.video_size;
			new_year = priorInst.new_year;
			like_forbidden = priorInst.like_forbidden;
			comment_forbidden = priorInst.comment_forbidden;
			contactsForbidden = priorInst.contactsForbidden;
		}
		
		if (response instanceof createuser_Response) { 
			createuser_Response res = (createuser_Response) response;
			id = calcId(res.id);
			type = res.type;
			mail = res.mail;
			password = res.password;
			first_name = res.name;
			current_year = res.current_year;
		}
		else if (response instanceof loginnew_Response_Parent) {
			loginnew_Response_Parent res = (loginnew_Response_Parent) response;
			id = calcId("" + res.parent_id); 
			type = res.type;
			mail = res.parent_mail;
			first_name = res.parent_first_name;
			last_name = res.parent_last_name;
			phone = res.parent_phone;
			mobile = res.parent_mobile;
			address = res.parent_address;
			city = res.parent_city;
			current_year = res.current_year;
			update = res.update;
			api_key = res.api_key;
			auth_token = res.auth_token;
			atoken = res.atoken;
			new_year = res.new_year;
		}
		else if (response instanceof loginnew_Response_Teacher) {
			loginnew_Response_Teacher res = (loginnew_Response_Teacher) response;

			id = calcId("" + res.teacher_id);
			type = res.type;
			mail = res.teacher_mail;
			first_name = res.teacher_first_name;
			last_name = res.teacher_last_name;
			phone = res.teacher_phone;
			mobile = res.teacher_mobile;
			address = res.teacher_address;
			city = res.teacher_city;
			current_year = res.current_year;
			update = res.update;
			api_key = res.api_key;
			auth_token = res.auth_token;
			atoken = res.atoken;
			max_image_size_android = res.max_image_size_android;
			max_images_batch_upload_android = res.max_images_batch_upload_android;
			video_size = res.video_size;
			new_year = res.new_year;
			comment_forbidden = res.comment_forbidden;
			like_forbidden = res.like_forbidden;
			contactsForbidden = res.contact_forbidden;
		} 
		else if (response instanceof getclass_Response) {
			getclass_Response res = (getclass_Response) response;
			if(res.teacher_id != null) {
				id = calcId("" + res.teacher_id);
			}
			if(res.teacher_mail != null) {
				mail = res.teacher_mail;
			}
			if(res.teacher_first_name != null) {
				first_name = res.teacher_first_name;
			}
			if(res.teacher_last_name != null) {
				last_name = res.teacher_last_name;
			}
			if(res.teacher_phone != null) {
				phone = res.teacher_phone;
			}
			if(res.teacher_mobile != null) {
				mobile = res.teacher_mobile;
			}
			if(res.teacher_address != null) {
				address = res.teacher_address;
			}
			if(res.teacher_city != null) {
				city = res.teacher_city;
			}
			if(res.current_year != null) {
				current_year = res.current_year;
			}
		} 
		else {
			int jj=234;
			jj++;
			throw new RuntimeException();
		}
		
		Log.i("test2712", "SET id = " + id);
		
	}


	private static String calcId(String idStr) {
		return idStr;
	}

 
	public static String getApiKey() {
		String apiKey = ""; 
		if (current != null) {
			apiKey = current.api_key;
		}
		int jj=234;
		jj++;
		return apiKey;
	}


	public static String getUserId() {
		if (current != null) {  
			Log.i("test2712", "GET id = " + current.id);
			return current.id;
		}
		return "";
	}

	public static String getAuthToken() {
		if(current != null) {
			return current.auth_token;
		}
		return null;
	}

	public static String getAtoken() {
		if(current != null) {
			return current.atoken;
		}
		return null;
	}

	public static void clear() {
		current = null;
		new SPWriter(THIS_USER_FILE).putString(THIS_USER_COLUMN, "").commit();
//		User.current.clearAlbumList(); // force album list refresh
		MessageListFragment.clearMessages();
		ContactListFragment.clearContacts();
	}


	public static String getName() {
		return StrUtils.emptyIfNull(current.first_name) + " " + StrUtils.emptyIfNull(current.last_name); 
	}

	public static String getId() {
		return current.id;
	}
	
	public static String getAddress() {
		return current.address;
	}
	
	public static String getPhone() {
		return current.phone;
	}
	
	public static String getMobile() {
		return current.mobile;
	}
	
	public static String getCity() {
		return current.city;	
	}
	
	public static String getCurrentYear() {
		return current.current_year;
	}
	
	public static String getUpdate() {
		return current.update;
	}


	public static boolean isParent() {
		return isParentType(current.type);
	}
	  
	public static boolean isTeacher() {
		if(current == null){
			return false;
		}
		return !isParentType(current.type) && !isStudentType(current.type);
	}

	public static boolean isStaff() {
		return isStaffType(current.type);
	}

	public static boolean isStudent() {
		return isStudentType(current.type);
	}

	public static boolean isStudentType(String type) {
		return Type_Student.equals(type);
	}

	public static boolean isParentType(String type) { 
		return Type_Parent.equals(type) || Type_Father.equals(type) ||
				Type_Mother.equals(type) || Type_Staff.equals(type) || Type_Student.equals(type);
	}



	public static boolean isMotherType(String type) { 
		return Type_Mother.equals(type);
	}

	public static boolean isStaffType(String type) {
		return Type_Staff.equals(type);
	}
	
	public static void updateWithUserkids(GetUserKids_Response[] response) {
		current.userkids = response;
		async_writeUserToLocaCache();
	}

	public static void updateKidPic(String kid_id, String kid_pic) {
		if (kid_id==null) {
			return;
		}
		if (current.userkids == null) {
			return;
		}
		int len = current.userkids.length;
		for (int i = 0; i < len; i++) {
			String _id = current.userkids[i].kid_id;
			if (kid_id.equals(_id)) {
				current.userkids[i].kid_pic = kid_pic;
			}
		}
	}

	public static void updateWithClasses(getclass_Response response) {
		current.classList = response;
		MainActivity.updateClassList();
		async_writeUserToLocaCache();
	}
	
	public static void updateWithCreateSession(createsession_response response) {
		current.max_image_size_android = response.max_image_size_android;
		current.max_images_batch_upload_android = response.max_images_batch_upload_android;
		current.video_size = response.video_size;
		current.current_year = response.current_year;
		current.new_year = response.new_year;
		current.like_forbidden = response.like_forbidden;
		current.comment_forbidden = response.comment_forbidden;
		current.contactsForbidden = response.contact_forbidden;
		current.auth_token = response.auth_token;
		current.atoken = response.atoken;
		async_writeUserToLocaCache();
	}

	
	public GetUserKids_Response getKidById(String kid_id) {
		if (kid_id==null) {
			return null;
		}
		if (current.userkids == null) {
			return null;
		}
		int len = current.userkids.length;
		for (int i = 0; i < len; i++) {
			String _id = current.userkids[i].kid_id;
			if (kid_id.equals(_id)) {
				return current.userkids[i];
			}
		}
		return null;
	}
	
	
	public NameAndId[] getKidNameArray() {
		if (current.userkids == null) {
			return new NameAndId[]{};
		}
		int len = current.userkids.length;
		NameAndId[] res = new NameAndId[len];
		for (int i = 0; i < len; i++) {
			String name = current.userkids[i].kid_name;
			String id = current.userkids[i].kid_id;
			res[i] = new NameAndId(name, id); 
		}
		return res;
	}
	
	
	public NameAndId[] getClassNameIdArray() {
		if (current.classList == null || current.classList.classes==null) {
			return new NameAndId[]{};
		}
		int len = current.classList.classes.length;
		NameAndId[] res = new NameAndId[len];
		for (int i = 0; i < len; i++) {
			String name = current.classList.classes[i].class_name;
			String id = current.classList.classes[i].class_id;
			res[i] = new NameAndId(name, id); 
			
		}
		return res;
	}

	public String[] getClassNameArray() {
		if(isTeacher())
		{
			if (current.classList == null || current.classList.classes==null) {
				return new String[]{};
			}
			int len = current.classList.classes.length;
			String[] res = new String[len];
			for (int i = 0; i < len; i++) {
				res[i] = current.classList.classes[i].class_name;
			}
			return res;
		}
		else
		{
			if (current.userkids == null) {
				return new String[]{};
			}
			
			Set<String> res = new HashSet<String>();
			
			for (GetUserKids_Response kid : current.userkids) {
				if(kid.class_name != null)
				{
					res.add(kid.gan_name + " - " + kid.class_name + "|" + kid.gan_code);
				}					
			}
			
			return res.toArray(new String[res.size()]);
		}
	}
	
	public ClassDetails_2 getClass(int position) {
		if (current.classList == null || current.classList.classes==null) {
			return null;
		}
		int len = current.classList.classes.length;
		if (position < 0 || position >= len) {
			return null;
		}
		return current.classList.classes[position];
	}

	public HashMap<String, ArrayList<getfavorite_Response>> getFavorites()
	{
		return favorites;
	}

	public void updateFavorite(HashMap<String, ArrayList<getfavorite_Response>> favorites)
	{
		current.favorites = favorites;
		async_writeUserToLocaCache();
	}

	public boolean hasAlbumList() {
		return albumMap != null;// && !albumMap.isEmpty();
	}

	public boolean hasEvetList() {
		return eventMap != null;// && !albumMap.isEmpty();
	}

	public void clearAlbumList() {
		Log.i("noanoa","clearAlbumList" );
		albumMap = null;
	}
	
	public HashMap<String, ArrayList<getalbum_response>> getAlbumList() {
//		if(albumMap == null)
//		{
//			albumMap = new HashMap<String, ArrayList<getalbum_response>>();
//		}
		return albumMap;
	}

	public TreeMap<String, ArrayList<getevents_Response.Event>> getEventList() {

		return eventMap;
	}


	public void addtoAlbumList(String year, ArrayList<getalbum_response> albumList) {
		if(albumMap == null)
		{
			albumMap = new HashMap<String, ArrayList<getalbum_response>>();
		}
		
		this.albumMap.put(year, albumList);
		async_writeUserToLocaCache();
	}

	public void deleteFromEventList(String event_id)
	{
		if(eventMap == null)
		{
			return;
		}

		TreeMap<String, ArrayList<getevents_Response.Event>> resMap = new TreeMap<String, ArrayList<getevents_Response.Event>>();

		for(String dateKey : eventMap.keySet())
		{
			ArrayList<getevents_Response.Event> res = new ArrayList<getevents_Response.Event>();

			ArrayList<getevents_Response.Event> eventList = eventMap.get(dateKey);

			for (getevents_Response.Event event: eventList ) {
				if(!event.event_id.equals(event_id))
				{
					res.add(event);
				}
			}

			if(!res.isEmpty())
			{
				resMap.put(dateKey, res);
			}
		}

		eventMap = resMap;

		async_writeUserToLocaCache();
	}

	public void updateEventList(String event_id,TreeMap<String, ArrayList<getevents_Response.Event>> addMap)
	{
		if (eventMap == null) {
			return;
		}

		TreeMap<String, ArrayList<getevents_Response.Event>> resMap = new TreeMap<String, ArrayList<getevents_Response.Event>>();

		// just to remove the prev event id
		for(String dateKey : eventMap.keySet())
		{
			ArrayList<getevents_Response.Event> eventList = eventMap.get(dateKey);
			ArrayList<getevents_Response.Event> resList = new ArrayList<getevents_Response.Event>();

			for(getevents_Response.Event event : eventList)
			{
				if(!event_id.equals(event.event_id))
				{
					resList.add(event);
				}
			}

			if(!resList.isEmpty()) {
				resMap.put(dateKey, resList);
			}
		}

		eventMap = resMap;

		addToEventList(addMap);
	}

	public void addToEventList(TreeMap<String, ArrayList<getevents_Response.Event>> addMap)
	{
		if(eventMap == null)
		{
			return;
		}

		for(String dateKey : addMap.keySet())
		{
			ArrayList<getevents_Response.Event> eventList = eventMap.get(dateKey);
			ArrayList<getevents_Response.Event> addEventList = addMap.get(dateKey);

			if(eventList == null)
			{
				eventList = new ArrayList<getevents_Response.Event>();
			}

			eventList.addAll(addEventList);

			Collections.sort(eventList, new Comparator<getevents_Response.Event>() {
				@Override
				public int compare(getevents_Response.Event event1, getevents_Response.Event event2) {
					SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm");
					Date date1 = null;
					Date date2 = null;

					try {
						date1 = format.parse(event1.event_start_date);
						date2 = format.parse(event2.event_start_date);
					} catch (ParseException e) {
						e.printStackTrace();
					}

					return date1.compareTo(date2);
				}
			});

			eventMap.put(dateKey, eventList);
		}


		async_writeUserToLocaCache();
	}


	public void deleteFromAlbumList(String year, String album_id) {
		if(albumMap == null)
		{
			return;
		}

		ArrayList<getalbum_response> albumList = this.albumMap.get(year);
		ArrayList<getalbum_response> res = new ArrayList<getalbum_response>();

		for (getalbum_response album: albumList ) {
			if(!album.album_id.equals(album_id))
			{
				res.add(album);
			}
		}

		this.albumMap.put(year, res);
		async_writeUserToLocaCache();
	}

	public void setEventList(TreeMap<String, ArrayList<getevents_Response.Event>> map)
	{
		this.eventMap = map;
		async_writeUserToLocaCache();
	}
	
	public void setAlbumList(HashMap<String, ArrayList<getalbum_response>> albumList) {
		
//		Log.i("noanoa","setAlbumList" + albumList);
		
		this.albumMap = albumList;
		
//		for (HistoryDetails history : getCurrentKidHistory()) {
//			this.albumMap.add(new Pair(history.class_year,new ArrayList<getalbum_response>()));
//		}			
		
		async_writeUserToLocaCache();
	}
	
	
	public boolean firstTimeTeacher() {
		return isTeacher() &&  classList == null;
	}

	public ClassDetails_2 getCurrentClass() {
		return current.classList.classes[current_ClassInd];		
	}


	
	public String getCurrentGanId() {
		if (isParent()) {
			return getCurrentKid().gan_id;
		} 
		else {
			if (current.classList != null) {
				return current.classList.gan_id;
			} else if (current.ganDetails != null) {
				return current.ganDetails.gan_id;
			}
			return null;
		}
	}

	public static boolean getCurrentLikeForbidden()
	{
		if(current != null) {
			return current.like_forbidden;
		}
		return false;
	}

	public static boolean getCurrentCommentForbidden()
	{
		if(current != null) {
			return current.comment_forbidden;
		}
		return false;
	}

	public static void setCurrentCommentForbidden(boolean comment_forbidden)
	{
		if(current != null) {
			current.comment_forbidden = comment_forbidden;
		}
	}

	public static void setCurrentLikeForbidden(boolean like_forbidden)
	{
		if(current != null) {
			current.like_forbidden = like_forbidden;
		}
	}


	public static boolean getCurrentContactsForbidden()
	{
		if(current != null) {
			return current.contactsForbidden;
		}
		return false;
	}

	public static void setCurrentContactsForbidden(boolean contactsForbidden) {
		if(current != null) {
			current.contactsForbidden = contactsForbidden;
		}
	}

	
	public String getCurrentGanCode() {
		if (isParent()) {
			return getCurrentKid().gan_code;
		} 
		else {
			if (current.classList != null) {
				return current.classList.gan_code;
			} else if (current.ganDetails != null) {
				return current.ganDetails.gan_code;
			}
			return null;
		}
	}
	
	public String getCurrentTeacherName() {
		String first_name = "";
		String last_name = "";
		
		if (isParent()) {
			first_name = getCurrentKid().teacher_first_name;
			last_name = getCurrentKid().teacher_last_name;
		} 
		else {
			first_name = current.classList.teacher_first_name;
			last_name = current.classList.teacher_last_name;
		}
		
		if(last_name == null)
		{
			return first_name;
		}
		else
		{
			return first_name + " " + last_name;
		}
	}
	
	public String getCurrentTeacherFirstName() {
		if (isParent()) {
			return getCurrentKid().teacher_first_name;
		} 
		else {
			return current.classList.teacher_first_name;
		}
	}
	
	public String getCurrentTeacherLastName() {
		if (isParent()) {
			return getCurrentKid().teacher_last_name;
		} 
		else {
			return current.classList.teacher_last_name;
		}
	}
	
	public String getCurrentTeacherMobile() {
		if (isParent()) {
			return getCurrentKid().teacher_mobile;
		} 
		else {
			return current.classList.teacher_mobile;
		}
	}
	
	public String getCurrentTeacherMail() {
		if (isParent()) {
			return getCurrentKid().teacher_mail;
		} 
		else {
			return current.classList.teacher_mail;
		}
	}

	public String getCurrentClassMeeting() {

		if (isParent()) {
			return getCurrentKid().meetingActive;
		} else {
			return getCurrentClass().meetings_active;

		}
	}

	public void setCurrentClassMeeting(String meetingActive) {
		getCurrentClass().meetings_active = meetingActive;
	}

	public String getCurrentTeacherId() {
		if (isParent()) {
			return getCurrentKid().teacher_id;
		}
		else {
			return current.classList.teacher_id;
		}
	}
	
	public String getCurrentGanAddress() {
		if (isParent()) {
			return getCurrentKid().gan_address;
		} 
		else {
			return current.classList.gan_address;
		}
	}
	
	public String getCurrentGanCity() {
		if (isParent()) {
			return getCurrentKid().gan_city;
		} 
		else {
			return current.classList.gan_city;
		}
	}
	
	public String getCurrentGanPhone() {
		if (isParent()) {
			return getCurrentKid().gan_phone;
		} else {
			return current.classList.gan_phone;
		} 
	} 

	public String getCurrentGanName() {
		if (isParent()) {
			return getCurrentKid().gan_name;
		} else {
			return current.classList.gan_name;
		} 
	} 
	
	public void markCurrentKidAsWaitingApproval() {
		int jj=234;
		jj++; 
		if (isParent()) {
			GetUserKids_Response kid = getCurrentKid();
			if (kid != null) {
				kid.markAsWaitingApproval();
			}
		}
	}


	public boolean kidIsWaitingApproval() {
		return "2".equals(getCurrentKid().kid_active) ;
	}
	
	public boolean kidCodeNotSent() {
		if (isParent()) {
			GetUserKids_Response kid = getCurrentKid();			
//			boolean not_sent = (kid != null && !kid.codeWasSent());
			boolean was_sent = inSentCodeMap(kid.kid_id);
			return !was_sent;
		}
		return false;
	}


	private static HashMap<String, String> sentCodeMap;
	
	private boolean inSentCodeMap(String kid_id) {
		loadSentCodeMapIfNeeded();
		return sentCodeMap.containsKey(kid_id);
	}  
	
	
	public static void addToSentCodeMap(String kid_id) {
		loadSentCodeMapIfNeeded();
		try {   
			sentCodeMap.put(kid_id, kid_id); 
//			Set<String> k_set = sentCodeMap.keySet();
//			String json = new Gson().toJson(k_set);
			String filePath = MyApp.context.getFilesDir().getAbsolutePath();//returns current directory.
			File file = new File(filePath, SENT_CODES);				
	        FileOutputStream fos= new FileOutputStream(file);
	        ObjectOutputStream oos= new ObjectOutputStream(fos);
	        oos.writeObject(sentCodeMap);
	        oos.close();
	        fos.close();		  
			int jj=234;  
			jj++;   
//			new SPWriter(SENT_CODES).putString(SENT_CODE_JSON, json);
			jj=234;
			jj++;
		}
		catch (Exception e) {
			int jj=234;
			jj++;
		}
	}
	

	private static String __json; 
	
	private static void loadSentCodeMapIfNeeded() {
		if (sentCodeMap == null) {
			try { 
				String filePath = MyApp.context.getFilesDir().getAbsolutePath();//returns current directory.
				File file = new File(filePath, SENT_CODES);				
			    FileInputStream fis = new FileInputStream(file);
	            ObjectInputStream ois = new ObjectInputStream(fis);
	            sentCodeMap = (HashMap<String, String>) ois.readObject();
	            ois.close();
	            fis.close(); 
	            //				sentCodeMap = new HashMap<String, String>();				
//				__json = new SPReader(SENT_CODES).getString(SENT_CODE_JSON, null);
//				int jj=234;
//				jj++;
//				if (__json != null) {
//					Type t_token = new TypeToken<Set<String>>(){}.getType();
//					Set<String> k_set = new Gson().fromJson(__json, t_token);
//					if (k_set != null) {
//						for (String k: k_set) {
//							sentCodeMap.put(k,k);
//						}
//					}
////					sentCodeMap = new Gson().fromJson(__json, t_token);
//					jj=234;
//					jj++;
//				}
			} 
			catch (Exception e) {
				int jj=234;
				jj++;
			}
			if (sentCodeMap == null) {
				sentCodeMap = new HashMap<String, String>();
			}
		}
		
	}

	public boolean kidWithoutGan() {
		int jj=234;
		jj++; 
		if (isParent() && current.getCurrentKid() != null)
		{
			return current.getCurrentKid().kid_active == null || !"1".equals(current.getCurrentKid().kid_active);
//			return !"1".equals(getCurrentKid().kid_active);
//			GetUserKids_Response kid = getCurrentKid();
//			boolean no_gan = kid != null && StrUtils.isEmpty(kid.gan_name);
//			return no_gan;
		}
		return false;
	}
	
	public String getCurrentKidUnreadMessage()
	{
		return getCurrentKid().unread_messages;
	}

	public String getCurrentKidUnseenPhotos()
	{
		GetUserKids_Response getUserKids_response = getCurrentKid();
		if(getUserKids_response != null)
		{
			return getUserKids_response.unseen_photos;
		}
		return null;
	}

	public boolean getCurrentKidPTA()
	{
		GetUserKids_Response getUserKids_response = getCurrentKid();
		if(getUserKids_response != null)
		{
			if("0".equals(getUserKids_response.vaad_type))
			{
				return false;
			}
			else
			{
				return true;
			}
		}
		return false;
	}
	
	public void setCurrentKidUnseenPhotos(String unseen_photos)
	{
		getCurrentKid().unseen_photos = unseen_photos;
	}
	
	public String getCurrentClassPendingParents()
	{
		return getCurrentClass().pending_parents;
	}
	
	public void setCurrentKidUnseenPhotosBySeenPhotos(String seen_photos)
	{
		String unseen_photos = getCurrentKid().unseen_photos;
		int unseen_photos_int = 0;
		int seen_photos_int = 0;
		
		if(seen_photos != null)
		{
			seen_photos_int = Integer.valueOf(seen_photos);
		}
		
		if(unseen_photos != null)
		{
			unseen_photos_int = Integer.valueOf(unseen_photos);
		}
		
		getCurrentKid().unseen_photos = String.valueOf(unseen_photos_int - seen_photos_int);
	}

	public boolean ganWasSet() {
		if (isParent()) {
			GetUserKids_Response kid = getCurrentKid();
			return kid != null && StrUtils.notEmpty(kid.gan_name);
		} else {
			getclass_Response c_list = current.classList; 
			return c_list != null && StrUtils.notEmpty(c_list.gan_name);
		}
	} 

	
	public String getComaSeparatedClassList() {
		if (isParent()) {
			return ""; // not relevant
		}
		
		if (classList == null || classList.classes==null) {
			return "";
		}
		String c_list = "";
		for (ClassDetails_2 c: classList.classes) {
			if (c_list.isEmpty()) {
				c_list += c.class_name;
			} else {
				c_list += ("," + c.class_name);
			}			
		}
		return c_list;
	}
	
	public String getClassPendingParents(String class_id)
	{
		if (isParent()) {
			return ""; // not relevant
		}
		
		if (classList == null || classList.classes==null) {
			return "";
		}
		for (ClassDetails_2 c: classList.classes) {
			if (class_id.equals(c.class_id))
			{
				return c.pending_parents;
			}
		}
		return null;
	}
	
	public String getCurrentClassId() {
		if (isParent()) {
			return getCurrentKid().class_id;
		} else {
			return getCurrentClass().class_id;
		}
	}

	public String getCurrentClassName() {
		if (isParent()) {
			return getCurrentKid().class_name;
		} else {
			return getCurrentClass().class_name;
		}
	}
	
	public int getSelectedItemIndex() {
		int jj=234;
		jj++;
		if (isParent()) { 
			return current_KidInd; 
		} else {
			return current_ClassInd;
		}
	}

	public static KidDetails[] getKidDetails() {
		GetUserKids_Response[] userkids = current.userkids;
		if (userkids==null) {
			return new KidDetails[0];
		}
		int len = userkids.length;
		KidDetails[] res = new KidDetails[len];
		for (int i = 0; i < len; i++) {
			res[i] = new KidDetails(userkids[i], null); //gggFix android_pic_path ??
			res[i].name = "noa";
		}
		return res;
	}

	public static void updateKidConnect(String kid_id)
	{
		if (kid_id==null) {
			return;
		}
		if (current.userkids == null) {
			return;
		}
		int len = current.userkids.length;
		for (int i = 0; i < len; i++) {
			String _id = current.userkids[i].kid_id;
			if (kid_id.equals(_id)) {
				current.userkids[i].kid_active = ActiveUtils.APPROVED;

				break;
			}
		}
		async_writeUserToLocaCache();
	}

	public static void updateKidDisconnect(String kid_id)
	{
		if (kid_id==null) {
			return;
		}
		if (current.userkids == null) {
			return;
		}
		int len = current.userkids.length;
		for (int i = 0; i < len; i++) {
			String _id = current.userkids[i].kid_id;
			if (kid_id.equals(_id)) {
				current.userkids[i].kid_active = ActiveUtils.DISCONNECTED;
				break;
			}
		}
		async_writeUserToLocaCache();
	}
 
	public static void updateTeacher(String gan_name, String teacher_name, String teacher_mobile,
			String gan_phone, String gan_address, String gan_city, String[] classNames) {
		updateUserDetails(gan_name, teacher_name, teacher_mobile, gan_phone, gan_address, gan_city);
		updateClassNames(classNames);
	}

	
	private static void updateClassNames(String[] new_classNames) {
		if (new_classNames == null) {
			return;
		}
		if (current.classList == null || current.classList.classes == null) {
			return;
		}
		
		ClassDetails_2[] _classes = current.classList.classes;
		
		int len = Math.min(new_classNames.length, _classes.length);
		for (int i = 0; i < len; i++) {
			_classes[i].class_name = new_classNames[i];
		}
	}
	
	public static void updateParent(String new_name, String new_mobile,
			String new_phone, String new_address, String new_city) {
		updateUserDetails(null, new_name, new_mobile, new_phone, new_address, new_city);
	}


	private static void updateUserDetails(String gan_name, String new_name, String new_mobile,
			String new_phone, String new_address, String new_city) {
		MainActivity.updateUserName(new_name);
		if (current == null) {
			return;
		}
		String[] names = NameUtils.breakName(new_name);
		
		if (isParent()) {
			if (names != null) { 
				current.first_name = names[0];
				current.last_name = names[1];
			}
			current.phone = new_phone;
			current.mobile = new_mobile;
			current.address = new_address;
			current.city = new_city;
		} 
		else {
			if (names != null) { 
				current.classList.teacher_first_name = names[0];
				current.classList.teacher_last_name = names[1];
				
				current.first_name = names[0];
				current.last_name = names[1];
			}
			
			current.classList.gan_phone = new_phone;
			current.classList.gan_address = new_address;
			current.classList.teacher_mobile = new_mobile;
			current.classList.gan_city = new_city;
			current.classList.gan_name = gan_name;
		}
				
	}

	public static String getEmail() {
		return (current==null) ? "" : current.mail;
	}
	
	public static int getDeafultKidImg(String kid_gender)
	{
		int defImageResId;
		
		if(kid_gender.equals(GenderUtils.BOY))
		{
			defImageResId = R.drawable.boydefault;
		}
		else if(kid_gender.equals(GenderUtils.GIRL))
		{
			defImageResId = R.drawable.girldefault;
		}
		else
		{
			defImageResId = R.drawable.add_pic_default;
		}
		
		return defImageResId;
	}
	
	public static getalbum_response getAlbumObject(String album_id) {
		if (album_id==null || current==null) {
			return null;
		}
		
		if(current.albumMap == null)
		{
			return null;
		}
	
		for (String year : current.albumMap.keySet()) {
			ArrayList<getalbum_response> arrayList = current.albumMap.get(year);
			
			for (getalbum_response a: arrayList) {
				if (album_id.equals(a.album_id)) {
					return a;
				}
			}
		}
		
		return null;
	}

	public static int getKidPosition(String class_id) {
		if (current==null || current.userkids == null) {
			return -1;
		}
		int len = current.userkids.length;
		for (int i = 0; i < len; i++) {
			String _id = current.userkids[i].class_id;
			if (class_id.equals(_id)) {
				return i;
			}
		}
		return -1;
	}

	public static int getClassPosition(String clsId) {
		if (current==null || current.classList == null || current.classList.classes==null) {
			return -1;
		}
		int len = current.classList.classes.length;
		for (int i = 0; i < len; i++) {
			String id = current.classList.classes[i].class_id;
			if (clsId.equals(id)) {
				return i;
			}
		}
		return -1;
	}

	public void updateFavoritePic(getpicture_Response picture, String year, String class_id, String gan_id) {

		if(favorites == null)
		{
			return;
		}

		if(picture.favorite)
		{
			getfavorite_Response getfavorite_response = new getfavorite_Response(picture,class_id,gan_id, year);
			ArrayList<getfavorite_Response> getfavorite_responses = favorites.get(year);
			if(getfavorite_responses == null)
			{
				getfavorite_responses = new ArrayList<getfavorite_Response>();
			}
			getfavorite_responses.add(getfavorite_response);
			//sort!!!

			Collections.sort(getfavorite_responses, new Comparator<getfavorite_Response>() {
				public int compare(getfavorite_Response f1, getfavorite_Response f2) {

					long i1 = Long.parseLong(f1.picture_id);
					long i2 = Long.parseLong(f2.picture_id);

					return (i1 > i2 ? 1 : (i1 == i2 ? 0 : -1));
				}
			});

			favorites.put(year,getfavorite_responses);
		}
		else
		{
			for(String _year : favorites.keySet())
			{
				ArrayList<getfavorite_Response> getfavorite_responses = favorites.get(_year);
				for(getfavorite_Response response : getfavorite_responses)
				{
					if(response.picture_id.equals(picture.picture_id))
					{
						getfavorite_responses.remove(response);
						if(getfavorite_responses.isEmpty())
						{
							favorites.remove(year);
						}
						else {
							favorites.put(year, getfavorite_responses);
						}
						return;
					}
				}
			}
		}
	}

	private long getTime(String dateString)
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");
		Date date = null;
		try {
			date = sdf.parse(dateString);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		long dateLong = date.getTime();
		return dateLong;
	}

/*
	public void setDefaultDropPick()
	{
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
			dropOff_from = sdf.parse("08:00");
			dropOff_to = sdf.parse("10:00");
			pickUp_from = sdf.parse("16:00");
			pickUp_to = sdf.parse("18:00");

			setDropPickMinutes();
		}
		catch (Exception e) {

		}
	}

	public void setDropPickFromResponse(String response) {
		try {
			Map<String, Object> map = new Gson().fromJson(
					response, new TypeToken<HashMap<String, Object>>() {
					}.getType()
			);

			Boolean success = (Boolean)map.get("success");
			if (!success)
				return;

			ArrayList data = (ArrayList)map.get("data");
			Map<String, Object> hours = (Map<String, Object>)data.get(0);

			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

			Date kiddropoffhour_from = sdf.parse((String)hours.get("kiddropoffhour_from"));
			Date kiddropoffhour_to = sdf.parse((String)hours.get("kiddropoffhour_to"));
			Date kidpickuphour_from = sdf.parse((String)hours.get("kidpickuphour_from"));
			Date kidpickuphour_to = sdf.parse((String)hours.get("kidpickuphour_to"));

			setDropPickHours(kiddropoffhour_from, kiddropoffhour_to, kidpickuphour_from, kidpickuphour_to);
		}
		catch (Exception e) {
			e.printStackTrace();
			setDefaultDropPick();
		}
	}

	public void setDropPickHours(Date kiddropoffhour_from, Date kiddropoffhour_to, Date kidpickuphour_from, Date kidpickuphour_to)
	{
		dropOff_from = kiddropoffhour_from;
		dropOff_to = kiddropoffhour_to;
		pickUp_from = kidpickuphour_from;
		pickUp_to = kidpickuphour_to;

		setDropPickMinutes();

		async_writeUserToLocaCache();
	}

	public void setDropPickMinutes()
	{
		dropOff_from_minutes = User.timeInMinutes(dropOff_from);
		dropOff_to_minutes = User.timeInMinutes(dropOff_to);
		pickUp_from_minutes = User.timeInMinutes(pickUp_from);
		pickUp_to_minutes = User.timeInMinutes(pickUp_to);
	}


	public static long timeInMinutes(Date date)
	{
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTime(date);
		return calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE);
	}

	public static void getPickDropHours()
	{
		String userId = User.getId();

		JsonTransmitter.send_getpickupdropoffhours(userId, new ICompletionHandler() {
			@Override
			public void onComplete(ResultObj result) {
				if (result.succeeded)
					User.current.setDropPickFromResponse(result.result);
			}
		});
	}
*/
}

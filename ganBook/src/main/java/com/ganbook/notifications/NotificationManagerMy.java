package com.ganbook.notifications;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import com.ganbook.activities.SplashActivity;
import com.ganbook.app.MyApp;
import com.ganbook.gcm.PushMsgPayload;
import com.ganbook.gcm.PushRepository;
import com.project.ganim.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import androidx.core.app.NotificationCompat;

public class NotificationManagerMy {

	public static final String DEFAULT_CHANNEL = "ganbook_channel";
	private static NotificationManager notificationManager;
	private NotificationManagerMy() {}

	public static void write(int notificationId, String text, String class_id, String loc_key,
							 String pic_url) {
		Log.d("OREO", Build.VERSION.SDK_INT + " ");
		Context context = MyApp.context;
		notificationManager= (NotificationManager)
				context.getSystemService(Context.NOTIFICATION_SERVICE);

		if(pic_url == null || PushMsgPayload.NEW_VID_UPLD.equals(loc_key)) {

			String title = context.getResources().getString(R.string.notif_title);


			notificationManager.notify(notificationId, makeBuilder(notificationManager, context, loc_key, class_id, text, title,
					new NotificationCompat.BigTextStyle().bigText(text)).build());

		}
		else {
			new DownloadBitmap(pic_url, notificationManager, notificationId, text, context, class_id, loc_key).execute();
		}

	}

	private static NotificationCompat.Builder makeBuilder(NotificationManager manager, Context context, String loc_key,
														  String class_id, String text, String title,
														  NotificationCompat.Style style) {

		Intent intent = new Intent(context, SplashActivity.class);

		intent.putExtra(PushRepository.PUSH_LOC_KEY, loc_key);
		intent.putExtra(PushRepository.PUSH_CLASS_ID, class_id);
		intent.putExtra(PushRepository.PUSH_JSON_MSG, text);

		PendingIntent contentIntent = PendingIntent.getActivity(context, 0,	intent, PendingIntent.FLAG_UPDATE_CURRENT);
		Log.d("SDK INT", String.valueOf(android.os.Build.VERSION.SDK_INT));
		if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

			int importance = NotificationManager.IMPORTANCE_HIGH;
			NotificationChannel mChannel = new NotificationChannel(DEFAULT_CHANNEL, title, importance);
			manager.createNotificationChannel(mChannel);

			NotificationCompat.Builder builder = new NotificationCompat.Builder(context, DEFAULT_CHANNEL);

			builder.setContentTitle(title)
					.setSmallIcon(getSmallIcon())
					.setContentText(text)
					.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher))
					.setDefaults(Notification.DEFAULT_SOUND)
					.setAutoCancel(true)
					.setChannelId(DEFAULT_CHANNEL)
					.setPriority(NotificationManager.IMPORTANCE_DEFAULT)
					.setDefaults(Notification.DEFAULT_ALL)
					.setContentIntent(contentIntent)
					.setStyle(style);

			return builder;
		} else {

			NotificationCompat.Builder builder =
					new NotificationCompat.Builder(context, DEFAULT_CHANNEL)
							.setContentTitle(title)
							.setStyle(style)
							.setContentText(text)
							.setSmallIcon(getSmallIcon())
							.setChannelId(DEFAULT_CHANNEL)
							.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher))
							.setDefaults(Notification.DEFAULT_SOUND)
							.setStyle(style)
							.setContentIntent(contentIntent)
							.setAutoCancel(true);

			return builder;
		}
	}

	public static  class DownloadBitmap extends AsyncTask<String, Void, Bitmap> {

		NotificationManager manager;
		String url;
		int notificationId;
		String text;
		Context context;
		String class_id;
		String loc_key;

		public DownloadBitmap(String pic_url, NotificationManager manager, int notificationId, String text,
							  Context context, String class_id, String loc_key){
			url = pic_url;
			this.manager = manager;
			this.notificationId = notificationId;
			this.text = text;
			this.context = context;
			this.class_id = class_id;
			this.loc_key = loc_key;
		}

		@Override
		protected Bitmap doInBackground(String... strings) {
			Bitmap remote_picture = null;
			try {
				remote_picture = BitmapFactory.decodeStream((InputStream) new URL(url).getContent());
			} catch (IOException e) {
				e.printStackTrace();
			}
			return remote_picture;
		}

		public void onPostExecute(Bitmap bitmap){

			String title = context.getResources().getString(R.string.notif_title);

			manager.notify(notificationId, makeBuilder(manager, context, loc_key, class_id, text, title,
					new NotificationCompat.BigPictureStyle().bigPicture(bitmap).setSummaryText(text).setBigContentTitle(title))
					.build());
		}
	}

	public static int getSmallIcon() {

		int icon;

		if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			icon = R.drawable.ic_stat_ganbooksilo4;
		} else {
			icon = R.drawable.ic_launcher;
		}

		return icon;
	}

}
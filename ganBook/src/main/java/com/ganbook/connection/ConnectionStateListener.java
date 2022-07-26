package com.ganbook.connection;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.util.Log;

import com.ganbook.models.events.NoInternetEvent;
import com.ganbook.utils.Const;

import org.greenrobot.eventbus.EventBus;

public class ConnectionStateListener  extends BroadcastReceiver {

	private static final String TAG = ConnectionStateListener.class.getName();
	private ConnectivityManager connManager;
	private LocalBroadcastManager broadcastManager;

	@Override
	public void onReceive(Context context, Intent intent) {

		broadcastManager = LocalBroadcastManager.getInstance(context);

		connManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
			boolean networkConnected = isNetworkConnected();
			Log.d(TAG, "Network connected: " + networkConnected);

			if (!networkConnected) {

				Intent networkIntent = new Intent(Const.NETWORK_INTENT);

				networkIntent.putExtra(Const.INTERNET_AVAIL, false);

				broadcastManager.sendBroadcast(networkIntent);
				EventBus.getDefault().postSticky(new NoInternetEvent(false));
			} else {
				Intent networkIntent = new Intent(Const.NETWORK_INTENT);

				networkIntent.putExtra(Const.INTERNET_AVAIL, true);
				EventBus.getDefault().postSticky(new NoInternetEvent(true));
				broadcastManager.sendBroadcast(networkIntent);
			}
		}
	}

	/**
	 * Gets the status of network connectivity.
	 *
	 * @return true if network is connected, false otherwise.
	 */
	boolean isNetworkConnected() {
		NetworkInfo info = connManager.getActiveNetworkInfo();
		return info != null && info.isConnected();
	}
}
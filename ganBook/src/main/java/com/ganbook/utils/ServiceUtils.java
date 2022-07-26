package com.ganbook.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.util.Log;

/**
 * Created by dmytro_vodnik on 6/30/16.
 * working on ganbook1 project
 */
public class ServiceUtils {

    private static String TAG = ServiceUtils.class.getName();

    public static boolean isMyServiceRunning(Context context, Class<?> serviceClass) {

        Log.d(TAG, "isMyServiceRunning: checking " + serviceClass.getName());

        if (context != null) {
            ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                if (serviceClass.getName().equals(service.service.getClassName())) {

                    Log.d(TAG, "isMyServiceRunning: service " + serviceClass.getName() + " running");

                    return true;
                }
            }

            Log.d(TAG, "isMyServiceRunning: service " + serviceClass.getName() + " not running");
        }
        return false;
    }
}

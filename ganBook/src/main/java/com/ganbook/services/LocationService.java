package com.ganbook.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

public class LocationService extends Service {
    private static final int LOCATION_INTERVAL = 1000;
    private static final float LOCATION_DISTANCE = 1.0f;
    private static final int LOCATION_INVALIDATION_PERIOD = 10000; // every 10 sec location is reset

    private static final String TAG = "GPS_LOG";

    private LocationManager mLocationManager = null;
    private static Location mLastLocation = null;
    private static boolean mInvalidationThreadRunning = true;
    private static ArrayList<ILocationServiceDelegate> mDelegates = new ArrayList<ILocationServiceDelegate>();

    private class LocationListener implements android.location.LocationListener
    {
        public LocationListener(String provider)
        {

        }

        @Override
        public void onLocationChanged(Location location)
        {
            String strLocation = location.getLatitude() + "  " + location.getLongitude();

            mLastLocation = location;

            for(ILocationServiceDelegate delegate : LocationService.mDelegates)
            {
                delegate.onLocationChanged(location);
            }
        }

        @Override
        public void onProviderDisabled(String provider)
        {
            Log.e(TAG, "onProviderDisabled: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider)
        {
            Log.e(TAG, "onProviderEnabled: " + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras)
        {
            Log.e(TAG, "onStatusChanged: " + provider);
        }
    }

    LocationListener[] mLocationListeners = new LocationListener[] {
            new LocationListener(LocationManager.GPS_PROVIDER),/*
            new LocationListener(LocationManager.NETWORK_PROVIDER)*/
    };

    public LocationService()
    {

    }

    @Override
    public IBinder onBind(Intent arg0)
    {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.e(TAG, "onStartCommand");
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onCreate()
    {
        Log.e(TAG, "onCreate  ");
        initLocationManager();
    }

    @Override
    public void onDestroy()
    {
        Log.e(TAG, "onDestroy");
        super.onDestroy();
        mInvalidationThreadRunning = false;

        if (mLocationManager != null) {
            for (int i = 0; i < mLocationListeners.length; i++) {
                try {
                    mLocationManager.removeUpdates(mLocationListeners[i]);
                } catch (Exception ex) {
                    Log.i(TAG, "fail to remove location listners, ignore", ex);
                }
            }
        }
    }

    private void initLocationManager() {
        Log.e(TAG, "initializeLocationManager");
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }

        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, 0,
                    mLocationListeners[0]);
        }
        catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        }
        catch (IllegalArgumentException ex) {
            Log.d(TAG, "gps provider does not exist " + ex.getMessage());
        }
    }

    private void initInvalidationThread()
    {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                while(mInvalidationThreadRunning)
                {
                    try {
                        Thread.sleep(LOCATION_INVALIDATION_PERIOD);
                        mLastLocation = null;
                    }
                    catch (Exception e) {

                    }
                }
            }
        });
    }

    public static Location getLastLocation()
    {
        return mLastLocation;
    }

    public static void addDelegate(ILocationServiceDelegate delegate)
    {
        if ((delegate != null) && !mDelegates.contains(delegate))
            mDelegates.add(delegate);
    }

    public static void removeDelegate(ILocationServiceDelegate delegate)
    {
        if (delegate != null)
            mDelegates.remove(delegate);
    }

}

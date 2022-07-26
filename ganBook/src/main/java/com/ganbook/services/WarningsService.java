package com.ganbook.services;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;

import androidx.core.app.NotificationCompat;

import com.ganbook.activities.MainActivity;
import com.ganbook.activities.PickDropActivity;
import com.ganbook.communication.ICompletionHandler;
import com.ganbook.communication.datamodel.ResultObj;
import com.ganbook.communication.json.GetUserKids_Response;
import com.ganbook.communication.json.transmitter.JsonTransmitter;
import com.ganbook.datamodel.UserKids;
import com.ganbook.user.User;
import com.project.ganim.R;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by Grigory on 6/19/2017.
 */

public class WarningsService {
    private final String TAG = "WarningsService";

    private final int TIME_THRESHOLD = 2;
    private final int DISTANCE_THRESHOLD = 500;

    private Activity mOwner;
    private boolean mIsRunning = false;
    private Thread mThread;
    private boolean mDropWarningIssued = false;
    private boolean mPickWarningIssued = false;
    private Location mLocation;
    private boolean mHaveLocation = false;
    private boolean mCheckingInProgress = false;
    private boolean mIsInBackground = false;
    private int mScheduledMessageId = -1;
    private String mScheduledMessageType = "";


    static float[] distResults = new float[1];

    public WarningsService() {
    }

    public void start(Activity owner) {
        mOwner = owner;

        if (User.isParent()) {
            mIsRunning = true;
            mDropWarningIssued = false;
            mPickWarningIssued = false;
            mHaveLocation = false;
            mCheckingInProgress = false;

            mThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    threadProc();
                }
            });

            mThread.start();
        }
    }

    public void stop() {
        mIsRunning = false;
    }

    public void threadProc() {
        while (mIsRunning) {
            try {
                Thread.sleep(2000);
                checkWarnings(false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void setLocation(Location location)
    {
        mHaveLocation = true;
        mLocation = location;
    }

    public boolean checkWarnings(boolean from_gps) {
        if (mCheckingInProgress || User.current == null)
            return false;

        boolean isPickDropVisible = PickDropActivity.loadVisible(mOwner);
        if (!isPickDropVisible)
            return false;

        mCheckingInProgress = true;

        Date date = new Date();
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(date);

        int h = calendar.get(Calendar.HOUR_OF_DAY);
        int m = calendar.get(Calendar.MINUTE);
        long now = h * 60 + m;

        if (now == 0)
        {
            if (mDropWarningIssued)
                mDropWarningIssued = false;

            if (mPickWarningIssued)
                mPickWarningIssued = false;
        }

        if (!mDropWarningIssued && (now >= User.current.dropOff_to_minutes) && ((now - User.current.dropOff_to_minutes) <= TIME_THRESHOLD)) {
            mDropWarningIssued = true;
            int msg = from_gps ? R.string.drop_off_gps_warning : R.string.drop_off_warning;
            showMessage(msg, "dropoff");
            mCheckingInProgress = false;
            return true;
        }

        if (!mPickWarningIssued && (now >= User.current.pickUp_to_minutes) && ((now - User.current.pickUp_to_minutes) <= TIME_THRESHOLD)) {
            mPickWarningIssued = true;
            int msg = from_gps ? R.string.pick_up_gps_warning : R.string.pick_up_warning;
            showMessage(msg, "pickup");
            mCheckingInProgress = false;
            return true;
        }

        if (!mHaveLocation) {
            mCheckingInProgress = false;
            return false;
        }

        if (!mDropWarningIssued && (now >= User.current.dropOff_from_minutes) && (now <= User.current.dropOff_to_minutes))
        {
            if (isInRange()) {
                mDropWarningIssued = true;
                showMessage(R.string.drop_off_warning,"dropoff");
                mCheckingInProgress = false;
                return true;
            }
        }

        if (!mPickWarningIssued && (now >= User.current.pickUp_from_minutes) && (now <= User.current.pickUp_to_minutes))
        {
            if (isInRange()) {
                mPickWarningIssued = true;
                showMessage(R.string.pick_up_warning, "pickup");
                mCheckingInProgress = false;
                return true;
            }
        }

        mCheckingInProgress = false;
        return false;
    }

    void showMessage(final int stringId, final String type)
    {
        if (mOwner == null)
            return;

        if (mIsInBackground) {
            Intent intent = new Intent(mOwner, MainActivity.class);
            PendingIntent contentIntent = PendingIntent.getActivity(mOwner, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            mScheduledMessageId = stringId;
            mScheduledMessageType = type;

            NotificationCompat.Builder b = new NotificationCompat.Builder(mOwner, "default");
            b.setAutoCancel(true)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setTicker("Hearty365")
                    .setContentTitle(mOwner.getString(R.string.warning))
                    .setContentText(mOwner.getString(stringId))
                    .setDefaults(Notification.DEFAULT_LIGHTS| Notification.DEFAULT_SOUND)
                    .setContentIntent(contentIntent)
                    .setContentInfo(mOwner.getString(R.string.warning));

            NotificationManager notificationManager = (NotificationManager) mOwner.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(1, b.build());
        }
        else {
            showWarningDialog(stringId, type);
        }

    }

    public void showWarningDialog(final int stringId, final String type) {
        mOwner.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mOwner);
                dialogBuilder.setCancelable(false);
                String msg = mOwner.getResources().getString(stringId);

                dialogBuilder.setTitle(msg);
                dialogBuilder.setIcon(R.drawable.ic_launcher);

                dialogBuilder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        pushEventToBackend(true, type);
                    }
                });

                dialogBuilder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        pushEventToBackend(false, type);
                    }
                });

                dialogBuilder.show();
            }
        });
    }

    public void showScheduledMessage() {
        if (mScheduledMessageId == -1)
            return;

        showWarningDialog(mScheduledMessageId, mScheduledMessageType);

        mScheduledMessageId = -1;
        mScheduledMessageType = "";
    }

    boolean isInRange()
    {
        if (!mHaveLocation)
            return false;

        for(GetUserKids_Response kid : UserKids.allKids)
        {
            if ((kid == null) || (kid.latitude == null) || (kid.longitude == null))
                continue;

            Location.distanceBetween(kid.latitude, kid.longitude, mLocation.getLatitude(), mLocation.getLongitude(), distResults);
            if (distResults[0] <= DISTANCE_THRESHOLD)
                return true;
        }

        return false;
    }

    void pushEventToBackend(boolean yes, String type)
    {
        JsonTransmitter.send_saveparentnotification(User.getId(), type, yes ? 1 : 0, new ICompletionHandler() {
            @Override
            public void onComplete(ResultObj result) {
            }
        });
    }

    public void setBackground(boolean newVal) {
        mIsInBackground = newVal;
    }
}
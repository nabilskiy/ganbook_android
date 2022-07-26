package com.ganbook.gcm;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.ganbook.activities.AddKidActivity;
import com.ganbook.activities.EnterCodeActivity;
import com.ganbook.activities.MainActivity;
import com.ganbook.activities.ParentDetailsActivity;
import com.ganbook.app.MyApp;
import com.ganbook.communication.ICompletionHandler;
import com.ganbook.communication.datamodel.ResultObj;
import com.ganbook.communication.json.GetUserKids_Response;
import com.ganbook.communication.json.transmitter.JsonTransmitter;
import com.ganbook.interfaces.KidWithoutGanInterface;
import com.ganbook.notifications.PushNotificationUtils;
import com.ganbook.user.User;
import com.parse.ParsePushBroadcastReceiver;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Noa on 22/11/2015.
 */
public class CustomeParsePushBroadcastReceiver extends ParsePushBroadcastReceiver {

    private final String TAG = CustomeParsePushBroadcastReceiver.class.getSimpleName();
    private Intent parseIntent;

    @Override
    protected void onPushReceive(Context context, Intent intent) {
        //super.onPushReceive(context, intent);

        Log.d(TAG, "ON PUSH RECEIVE");

        try {
            JSONObject json = new JSONObject(intent.getExtras().getString("com.parse.Data"));

            Log.e(TAG, "Push received: " + json);

            parseIntent = intent;

            String message = json.getString("alert");
            String class_id = null;
            if(!json.isNull("class_id"))
            {
                class_id = json.getString("class_id");
            }
            String loc_key = null;
            if(!json.isNull("loc_key")) {
                loc_key = json.getString("loc_key");
            }
            String pic_url = null;
            if(!json.isNull("pic_url")) {
                pic_url = json.getString("pic_url");
            }


            if (PushMsgPayload.NEW_PICS_UPLD.equals(loc_key) || PushMsgPayload.NEW_PICS_UPLD_1.equals(loc_key)) {
                message = new String(Character.toChars(0x1F4F7)) + " " + message;
            }
            else if(PushMsgPayload.NEW_VID_UPLD.equals(loc_key)) {
                message = new String(Character.toChars(0x1F4F9)) + " " + message;
            }
            else if(PushMsgPayload.MSG.equals(loc_key) || PushMsgPayload.MSG_PTA.equals(loc_key)) {
                message = new String(Character.toChars(0x1F4DD)) + " " + message;
            }
            else if(loc_key.contains(PushMsgPayload.EVNT)) {
                if(loc_key.contains(PushMsgPayload.EVNT_BD)) {
                    message = new String(Character.toChars(0x1F389)) + " " + message;
                }
                else {
                    message = new String(Character.toChars(0x1F4C5)) + " " + message;
                }
            } else if (loc_key.equals(PushMsgPayload.CONFIRM_CLASS)) {
                Toast.makeText(context, "Kid approved", Toast.LENGTH_LONG).show();
                refreshKids();
            } else if(loc_key.equals(PushMsgPayload.MEETING_MSG)) {
                message = new String(Character.toChars(0x1F4C6)) + " " + message;
            }

            PushNotificationUtils.notify(message, class_id, loc_key, pic_url);

            String push_id = json.getString("push_id");
            call_createUserPush(push_id);

        } catch (JSONException e) {
            Log.e(TAG, "Push message json exception: " + e.getMessage());
        }
    }

    @Override
    protected void onPushOpen(Context context, Intent intent) {
        Log.e("Push", "Clicked");
        super.onPushOpen(context, intent);
    }

    private void call_createUserPush(String push_id) {
        JsonTransmitter.send_createuserpush(push_id);
    }

    protected void refreshKids() {

        assert User.getId() != null;
        String userId = User.getId();
        JsonTransmitter.send_getuserkids(userId, result -> {
            if (!result.succeeded) {
                if(JsonTransmitter.NO_NETWORK_MODE.equals(result.result))
                {
                    return;
                }
                String errmsg = result.result;
                MyApp.toast(errmsg, Toast.LENGTH_SHORT);
                return;
            }
            int num = result.getNumResponses();
            GetUserKids_Response[] responses = new GetUserKids_Response[num];
            for (int i = 0; i < num; i++) {
                responses[i] = (GetUserKids_Response) result.getResponse(i);
            }
            User.updateWithUserkids(responses);
            MainActivity.refresh();

        });

    }

}

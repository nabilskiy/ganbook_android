package com.ganbook.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ganbook.amazinglist.special.CustomToast;
import com.ganbook.communication.ICompletionHandler;
import com.ganbook.communication.datamodel.ResultObj;
import com.ganbook.communication.json.GetUserKids_Response;
import com.ganbook.communication.json.getclass_Response;
import com.ganbook.communication.json.transmitter.JsonTransmitter;
import com.ganbook.sharedprefs.SPWriter;
import com.ganbook.user.User;
import com.project.ganim.R;

/**
 * Created by Noa on 05/07/2015.
 */
public class NewYearActivity extends BaseAppCompatActivity {

    private TextView new_year_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_year);

        new_year_text = (TextView) findViewById(R.id.new_year_text);

        if(User.isTeacher())
        {
            String text = String.format(getString(R.string.end_year_text_teacher), User.current.getCurrentGanCode());
            new_year_text.setText(text);
        }
        else
        {
            new_year_text.setText(getString(R.string.end_year_text_parent));
        }
    }

    public void thanks(View v)
    {
        new SPWriter("NEW_YEAR_NEW").putBool("was_shown", true).commit();

        if(User.isTeacher())
        {
            JsonTransmitter.send_sendkindergartenmail‎();

            JsonTransmitter.send_getclass(User.getId(), new ICompletionHandler() {
                @Override
                public void onComplete(ResultObj result) {
                    int jj = 234;
                    jj++;
                    if (!result.succeeded) {
                        if (JsonTransmitter.NO_NETWORK_MODE.equals(result.result)) {
                            set_no_internet();
                            return;
                        }
                        String errmsg = result.result;
                        CustomToast.show(NewYearActivity.this, errmsg);
                        return;
                    }
                    getclass_Response response = (getclass_Response) result.getResponse(0);
                    User.updateWithClasses(response);
                    goToMain();
                }
            });
        }
        else {
            JsonTransmitter.send_getuserkids(User.getId(), new ICompletionHandler() {
                @Override
                public void onComplete(ResultObj result) {
                    if (!result.succeeded) {
                        if (JsonTransmitter.NO_NETWORK_MODE.equals(result.result)) {
                            set_no_internet();
                            return;
                        }
                        String errmsg = result.result;
                        CustomToast.show(NewYearActivity.this, errmsg);
                        return;
                    }
                    int num = result.getNumResponses();
                    GetUserKids_Response[] responses = new GetUserKids_Response[num];
                    for (int i = 0; i < num; i++) {
                        responses[i] = (GetUserKids_Response) result.getResponse(i);
                    }
                    User.updateWithUserkids(responses);
                    goToMain();
                }
            });
        }


    }

    private void goToMain()
    {
        finish();
        LoginActivity.callFinish();
        EntryScreenActivity.callFinish();
        startActivity(new Intent(NewYearActivity.this, MainActivity.class));
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
    }

    private void set_no_internet()
    {
        setContentView(R.layout.no_internet_layout);

        ((Button) findViewById(R.id.register_btn)).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                finish();
                startActivity(getIntent());
            }
        });
    }
}

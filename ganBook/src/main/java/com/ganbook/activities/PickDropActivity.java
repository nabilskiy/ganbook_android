package com.ganbook.activities;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TimePicker;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.ganbook.communication.ICompletionHandler;
import com.ganbook.communication.datamodel.ResultObj;
import com.ganbook.communication.json.transmitter.JsonTransmitter;
import com.ganbook.user.User;
import com.ganbook.utils.LocaleGetter;
import com.project.ganim.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

public class PickDropActivity extends BaseAppCompatActivity {

    private EditText txtDropFrom;
    private EditText txtDropTo;
    private EditText txtPickFrom;
    private EditText txtPickTo;
    private TimePicker timePicker;
    private EditText fucusedEditText;
    private boolean blockPickerRefresh = true;
    private Switch switchOnOff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Locale l = getCurrentLocale();
        String lang = l.getLanguage();
        int activityResId = R.layout.activity_pick_drop;

        if(LocaleGetter.isHebrew(getResources().getConfiguration())) {
            activityResId = R.layout.activity_pick_drop_he;
        }

        setContentView(activityResId);

        setActionBar(getString(R.string.pickup_drop_hours_text), true);

        switchOnOff = (Switch)findViewById(R.id.switchOnOff);
        switchOnOff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                onOff(b);
            }
        });

        boolean isVisible = loadVisible(this);
        switchOnOff.setChecked(isVisible);
        onOff(isVisible);

        Button b;

//        b = (Button)findViewById(R.id.pickdrop_notifications);
//        b.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                start(PickDropNotificationsActivity.class);
//            }
//        });



        b = (Button)findViewById(R.id.pickdrop_save);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doSave();
            }
        });

        txtDropFrom = (EditText)findViewById(R.id.drop_from);
        txtDropTo = (EditText)findViewById(R.id.drop_to);
        txtPickFrom = (EditText)findViewById(R.id.pick_from);
        txtPickTo = (EditText)findViewById(R.id.pick_to);

        timePicker = (TimePicker)findViewById(R.id.timePicker1);

        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int i, int i1) {
                if (fucusedEditText != null && !blockPickerRefresh) {
                    int h, m;

                    if (Build.VERSION.SDK_INT < 23) {
                        h = timePicker.getCurrentHour();
                        m = timePicker.getCurrentMinute();
                    }
                    else {
                        h = timePicker.getHour();
                        m = timePicker.getMinute();
                    }

                    fucusedEditText.setText(""  + String.format("%02d", h) + ":" + String.format("%02d", m));
                }
            }
        });

        txtDropFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                focusEditText(txtDropFrom);
            }
        });

        txtDropTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                focusEditText(txtDropTo);
            }
        });

        txtPickFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                focusEditText(txtPickFrom);
            }
        });

        txtPickTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                focusEditText(txtPickTo);
            }
        });

        setHours();

       // Button btnSave = (Button)findViewById(R.id.pickdrop_notifications);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                onBackPressed();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    void focusEditText(EditText et) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            Date d = sdf.parse(et.getText().toString());

            Calendar calendar = GregorianCalendar.getInstance(); // creates a new calendar instance
            calendar.setTime(d);
            timePicker.setVisibility(View.VISIBLE);

            blockPickerRefresh = true;

            if (Build.VERSION.SDK_INT < 23) {
                timePicker.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
                timePicker.setCurrentMinute(calendar.get(Calendar.MINUTE));
            }
            else {
                timePicker.setHour(calendar.get(Calendar.HOUR_OF_DAY));
                timePicker.setMinute(calendar.get(Calendar.MINUTE));
            }

            blockPickerRefresh = false;

            if (fucusedEditText != null)
                fucusedEditText.setBackground(ContextCompat.getDrawable(this, R.drawable.edit_bg));

            fucusedEditText = et;

            fucusedEditText.setBackground(ContextCompat.getDrawable(this, R.drawable.edit_bg_light));

        }
        catch (Exception e) {

        }

    }

    boolean validate()
    {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

            Date kiddropoffhour_from = sdf.parse(txtDropFrom.getText().toString());
            Date kiddropoffhour_to = sdf.parse(txtDropTo.getText().toString());
            Date kidpickuphour_from = sdf.parse(txtPickFrom.getText().toString());
            Date kidpickuphour_to = sdf.parse(txtPickTo.getText().toString());

            return (kiddropoffhour_to.after(kiddropoffhour_from) &&
                    kidpickuphour_from.after(kiddropoffhour_to) &&
                    kidpickuphour_to.after(kidpickuphour_from));
        }
        catch (Exception e) {
            return false;
        }
    }

    void doSave()
    {
        if (!validate()) {
            showMessage(R.string.pickup_drop_hours_error, false);
            return;
        }

        final String kiddropoffhour_from = txtDropFrom.getText().toString();
        final String kiddropoffhour_to = txtDropTo.getText().toString();
        final String kidpickuphour_from = txtPickFrom.getText().toString();
        final String kidpickuphour_to = txtPickTo.getText().toString();

        String userId = User.getId();

        JsonTransmitter.send_setpickupdropoffhours(userId, kiddropoffhour_from, kiddropoffhour_to,
                kidpickuphour_from, kidpickuphour_to, new ICompletionHandler() {
            @Override
            public void onComplete(ResultObj result) {
                if (result.succeeded)
                    showMessage(R.string.operation_succeeded, true);
                else
                    showMessage(R.string.operation_failed, true);

                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
//                    User.current.setDropPickHours(sdf.parse(kiddropoffhour_from), sdf.parse(kiddropoffhour_to),
//                            sdf.parse(kidpickuphour_from), sdf.parse(kidpickuphour_to));
                }
                catch (Exception e) {
                }
            }
        });
    }

    void setHours()
    {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

        txtDropFrom.setText(sdf.format(User.current.dropOff_from));
        txtDropTo.setText(sdf.format(User.current.dropOff_to));
        txtPickFrom.setText(sdf.format(User.current.pickUp_from));
        txtPickTo.setText(sdf.format(User.current.pickUp_to));
    }

    void showMessage(final int stringId, final boolean close)
    {
        final PickDropActivity This = this;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(This);
                dialogBuilder.setCancelable(false);
                String msg = getResources().getString(stringId);

                dialogBuilder.setTitle(msg);
                dialogBuilder.setIcon(R.drawable.ic_launcher);

                dialogBuilder.setPositiveButton(R.string.popup_btn_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (close) {
                            View view = This.getCurrentFocus();
                            if (view != null) {
                                InputMethodManager imm = (InputMethodManager)getSystemService(This.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                            }

                            This.finish();
                        }
                    }
                });

                dialogBuilder.show();
            }
        });
    }

    void onOff(boolean b) {

        View v1 = findViewById(R.id.drop_from);
        View v2 = findViewById(R.id.drop_to);
        View v3 = findViewById(R.id.pick_from);
        View v4 = findViewById(R.id.pick_to);
        View v5 = findViewById(R.id.pickdrop_save);
        View v6 = findViewById(R.id.timePicker1);

        v1.setEnabled(b);
        v2.setEnabled(b);
        v3.setEnabled(b);
        v4.setEnabled(b);
        v5.setEnabled(b);

        v1.setFocusable(b);
        v2.setFocusable(b);
        v3.setFocusable(b);
        v4.setFocusable(b);
        v5.setFocusable(b);

        if (!b) {
            if (fucusedEditText != null)
                fucusedEditText.setBackground(ContextCompat.getDrawable(this, R.drawable.edit_bg));

            fucusedEditText = null;

            v6.setVisibility(View.GONE);
        }

        saveVisible(this, b);
    }

    public static SharedPreferences prefs(Context ctx) {
        return ctx.getSharedPreferences("prefs", MODE_PRIVATE);
    }

    public static void saveVisible(Context ctx, boolean visible) {
        SharedPreferences.Editor ed = prefs(ctx).edit();
        ed.putBoolean("pick_drop_visible", visible);
        ed.commit();
    }

    public static boolean loadVisible(Context ctx) {
        return prefs(ctx).getBoolean("pick_drop_visible", false);
    }

    @TargetApi(Build.VERSION_CODES.N)
    public Locale getCurrentLocale(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            return getResources().getConfiguration().getLocales().get(0);
        } else{
            //noinspection deprecation
            return getResources().getConfiguration().locale;
        }
    }


}

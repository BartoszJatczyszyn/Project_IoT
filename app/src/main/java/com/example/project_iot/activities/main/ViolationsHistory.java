package com.example.project_iot.activities.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.project_iot.R;
import com.example.project_iot.activities.authorisation.Login;
import com.example.project_iot.database.DatabaseHelperFactory;
import com.example.project_iot.database.IDatabaseHelper;
import com.example.project_iot.objects.Alarm;

import java.util.ArrayList;
import java.util.Collections;

public class ViolationsHistory extends AppCompatActivity {

    ImageView btn_back;

    LinearLayout layout_devices;

    private Activity activity;

    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = this;

        setContentView(R.layout.activity_history_violations);

        userId = getApplicationContext().getSharedPreferences("ProjectIoTPref", 0)
                .getInt("session_user_id", -1);

        if (userId < 0) {
            Intent intent = new Intent(getBaseContext(), Login.class);
            startActivity(intent);
            return;
        }

        /*
            Alerts
         */

        layout_devices = (LinearLayout) findViewById(R.id.lista_naruszen);
        fillAlerts();

        /*
            Buttons
         */

        btn_back = (ImageView) findViewById(R.id.naruszenia_wroc);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getBaseContext(), Menu.class);
                startActivity(intent);
            }
        });
    }

    private void fillAlerts() {

        TextView view = new TextView(activity.getApplicationContext());
        view.setText("Ładowanie...");
        view.setTextSize(16f);
        layout_devices.addView(view);

        new Thread() {
            @Override
            public void run() {

                IDatabaseHelper idh = DatabaseHelperFactory.getMysqlDatabase();
                if (!idh.open()) {
                    activity.runOnUiThread(() -> {
                        Toast.makeText(getApplicationContext(), "Nie udało się połączyć z systemem.", Toast.LENGTH_SHORT).show();
                    });
                    return;
                }

                ArrayList<Integer> userDevices = idh.getUserDevicesIds(userId);
                ArrayList<Alarm> alarms = new ArrayList<>();
                for (int deviceId : userDevices)
                    alarms.addAll(idh.getAlarms(deviceId, userId));
                Collections.sort(alarms);

                idh.close();

                activity.runOnUiThread(() -> {
                    layout_devices.removeAllViews();
                });

                for (Alarm alarm : alarms) {

                    activity.runOnUiThread(() -> {

                    View view = activity.getLayoutInflater().inflate(R.layout.layout_single_alert_listing, null);
                    TextView dateTextView = view.findViewById(R.id.date);
                    TextView alertTextView = view.findViewById(R.id.alert);

                    dateTextView.setText(alarm.getInsertDate().toString());
                    alertTextView.setText(alarm.getMessage());

                    layout_devices.addView(view);

                    });
                }

            }
        }.start();
    }

}

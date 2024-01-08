package com.example.project_iot.activities.main;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.project_iot.R;
import com.example.project_iot.activities.authorisation.Login;
import com.example.project_iot.database.DatabaseHelperFactory;
import com.example.project_iot.database.IDatabaseHelper;
import com.example.project_iot.objects.Alarm;
import com.example.project_iot.utils.AlarmWatcher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class Menu extends AppCompatActivity {
    Button btn_logout;
    Button btn_devices;
    Button btn_armall;
    Button btn_unarmall;
    Button btn_alerts_history;
    Button btn_notifications;

    Button zmien_haslo;

    LinearLayout layout_alerts;


    private Activity activity;

    private int userId;

    private HashMap<View, Alarm> alarmsByView = new HashMap<View, Alarm>();
    private ScheduledExecutorService scheduler;
    private AlarmWatcher alarmWatcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = this;

        setContentView(R.layout.activity_menu);

        userId = getApplicationContext().getSharedPreferences("ProjectIoTPref", 0)
                .getInt("session_user_id", -1);

        if (userId < 0) {
            activity = null;
            Intent intent=new Intent(getBaseContext(), Login.class);
            startActivity(intent);
            return;
        }

        this.scheduler =  Executors.newScheduledThreadPool(1);
        this.alarmWatcher = new AlarmWatcher(activity, getApplicationContext());
        scheduler.scheduleAtFixedRate(alarmWatcher, 2, 10, TimeUnit.SECONDS);

        /*
            Alerts
         */

        layout_alerts = (LinearLayout) findViewById(R.id.alarmy);
        fillAlerts();

        /*
            Buttons
         */

        btn_logout = (Button) findViewById(R.id.logout);
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

        btn_devices = (Button) findViewById(R.id.lista_czujnikow);
        btn_devices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getBaseContext(), Sensors.class);
                startActivity(intent);
                //Toast.makeText(getApplicationContext(), "czujniki", Toast.LENGTH_SHORT).show();
            }
        });

        btn_armall = (Button) findViewById(R.id.zalacz);
        btn_armall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                armAll();
            }
        });

        btn_unarmall = (Button) findViewById(R.id.rozbroj);
        btn_unarmall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unarmAll();
            }
        });

        btn_alerts_history = (Button) findViewById(R.id.historia_naruszen);
        btn_alerts_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity = null;
                Intent intent=new Intent(getBaseContext(), ViolationsHistory.class);
                startActivity(intent);
            }
        });

        btn_notifications = (Button) findViewById(R.id.powiadomienia);
        btn_notifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity = null;
                Intent intent=new Intent(getBaseContext(), NotificationsHistory.class);
                startActivity(intent);
            }
        });

        zmien_haslo = (Button) findViewById(R.id.zmien_haslo);
        zmien_haslo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), Change_Password.class);
                startActivity(intent);
            }
        });
    }

    public void logout() {

        getApplicationContext().getSharedPreferences("ProjectIoTPref", 0)
                .edit().putInt("session_user_id", -1).commit();
        Log.d("IOT", "Trying to stop scheduled AlarmWatcher");
        scheduler.shutdown();
        ///finish();
        Intent intent=new Intent(this, Login.class);
        startActivity(intent);
    }

    public void armAll() {

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
                for (int deviceId : idh.getUserDevicesIds(userId)) {
                    idh.updateDeviceActiveStatus(deviceId, true);
                }
                idh.close();

                activity.runOnUiThread(() -> {
                    Toast.makeText(getApplicationContext(), "Uzbrojono wszystkie.", Toast.LENGTH_SHORT).show();
                });
            }
        }.start();

    }

    public void unarmAll(){

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
                for(int deviceId : idh.getUserDevicesIds(userId)){
                    idh.updateDeviceActiveStatus(deviceId, false);
                }
                idh.close();

                activity.runOnUiThread(() -> {
                    Toast.makeText(getApplicationContext(), "Rozbrojono wszystkie.", Toast.LENGTH_SHORT).show();
                });
            }
        }.start();
    }

    public void fillAlerts() {

        activity.runOnUiThread(() -> {
            layout_alerts.removeAllViews();
            TextView view = new TextView(activity.getApplicationContext());
            view.setText("Ładowanie...");
            view.setTextSize(16f);
            layout_alerts.addView(view);
        });

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
                for (int deviceId : userDevices) {
                    alarms.addAll(idh.getAlarmsWithStatus(deviceId, userId, Alarm.Status.ACTIVE.name()));
                    alarms.addAll(idh.getAlarmsWithStatus(deviceId, userId, Alarm.Status.SURPRESSED.name()));
                }
                Collections.sort(alarms);

                idh.close();

                if (activity == null)
                    return;

                activity.runOnUiThread(() -> {

                    layout_alerts.removeAllViews();
                    alarmsByView.clear();

                    if (alarms.isEmpty()){

                        TextView view = new TextView(activity.getApplicationContext());
                        view.setText("Nie ma nowych alarmów.");
                        view.setTextSize(16f);
                        layout_alerts.addView(view);
                        return;
                    }

                    for (Alarm alarm : alarms) {

                        View view = activity.getLayoutInflater().inflate(R.layout.layout_single_alert, null);
                        TextView dateTextView = view.findViewById(R.id.date);
                        TextView alertTextView = view.findViewById(R.id.alert);
                        ImageView dismissButton = view.findViewById(R.id.dismiss_button);

                        dateTextView.setText(alarm.getInsertDate().toString());
                        alertTextView.setText(alarm.getMessage());

                        dismissButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                new Thread(){
                                    @Override
                                    public void run(){
                                        IDatabaseHelper idh = DatabaseHelperFactory.getMysqlDatabase();
                                        if (!idh.open()) {
                                            activity.runOnUiThread(() -> {
                                                Toast.makeText(getApplicationContext(), "Nie udało się połączyć z systemem.", Toast.LENGTH_SHORT).show();
                                            });
                                            return;
                                        }

                                        idh.updateAlarmStatus(alarmsByView.get(v).getId(), Alarm.Status.ARCHIVED);

                                        idh.close();

                                        activity.runOnUiThread(() -> {
                                            Toast.makeText(getApplicationContext(), "Zarchiwizowano.", Toast.LENGTH_SHORT).show();
                                        });

                                        fillAlerts();

                                    }
                                }.start();
                            }
                        });

                        layout_alerts.addView(view);
                        alarmsByView.put(dismissButton, alarm);
                    }
                });
            }
        }.start();

    }
}

package com.example.project_iot.activities.main;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.project_iot.R;
import com.example.project_iot.activities.authorisation.Login;
import com.example.project_iot.database.DatabaseHelperFactory;
import com.example.project_iot.database.IDatabaseHelper;
import com.example.project_iot.objects.Alarm;


public class Menu extends AppCompatActivity {
    Button btn_logout;
    Button btn_devices;
    Button btn_armall;
    Button btn_unarmall;
    Button btn_alerts_history;
    Button btn_notifications;


    private static Activity activity;

    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = this;

        setContentView(R.layout.activity_menu);

        userId = this.getIntent().getIntExtra("USER_ID", -1);

        if (userId < 0) {
            userId = getApplicationContext().getSharedPreferences("ProjectIoTPref", 0)
                    .getInt("session_user_id", -1);
        }

        if (userId < 0) {
            activity = null;
            Intent intent=new Intent(getBaseContext(), Login.class);
            startActivity(intent);
            return;
        }

        btn_logout = (Button) findViewById(R.id.logout);
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

        btn_devices = (Button) findViewById(R.id.czujniki);
        btn_devices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "czujniki", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getApplicationContext(), "historia_naruszen", Toast.LENGTH_SHORT).show();
            }
        });

        btn_notifications = (Button) findViewById(R.id.powiadomienia);
        btn_notifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "powiadomienia", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void logout() {
        activity = null;
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

    public void getAlerts() {

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

                for (Alarm alarm : idh.getAlarmsWithStatus(userId, Alarm.Status.ACTIVE.name())){
                    //TODO: add to alert menu
                }

                idh.close();
            }
        }.start();

    }
}

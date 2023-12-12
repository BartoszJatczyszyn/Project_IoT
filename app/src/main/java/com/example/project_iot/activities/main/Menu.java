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

    private static Activity activity;

    int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = this;

        setContentView(R.layout.activity_menu);

        userId = savedInstanceState.getInt("USER_ID");

        btn_logout = (Button) findViewById(R.id.btn_logout);

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
    }

    public void logout() {
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
                for(int deviceId : idh.getUserDevices(userId)){
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
                for(int deviceId : idh.getUserDevices(userId)){
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

package com.example.project_iot.activities.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
import com.example.project_iot.objects.devices.ADevice;

import java.util.ArrayList;
import java.util.HashMap;

public class Sensors extends AppCompatActivity {

    ImageView btn_back;

    LinearLayout layout_devices;

    private Activity activity;

    private int userId;

    private HashMap<View, ADevice> devicesByView = new HashMap<View, ADevice>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = this;

        setContentView(R.layout.activity_sensors);

        userId = getApplicationContext().getSharedPreferences("ProjectIoTPref", 0)
                .getInt("session_user_id", -1);

        if (userId < 0) {
            activity = null;
            Intent intent=new Intent(getBaseContext(), Login.class);
            startActivity(intent);
            return;
        }

                /*
            Alerts
         */

        layout_devices = (LinearLayout) findViewById(R.id.lista_czujnikow);
        fillDevices();

        /*
            Buttons
         */

        btn_back = (ImageView) findViewById(R.id.czujniki_wroc);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getBaseContext(), Menu.class);
                startActivity(intent);
            }
        });

    }

    public void fillDevices() {
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

                ArrayList<Integer> devicesIds = idh.getUserDevicesIds(userId);

                ArrayList<ADevice> devices = new ArrayList<ADevice>();

                for (int i : devicesIds) {
                    devices.add(idh.getDevice(i));
                }

                idh.close();

                activity.runOnUiThread(() -> {

                    layout_devices.removeAllViews();
                    devicesByView.clear();

                    for (ADevice device : devices) {

                        View view = activity.getLayoutInflater().inflate(R.layout.layout_single_sensor, null);
                        TextView deviceNameTextView = view.findViewById(R.id.device_name);

                        Button statusButton = view.findViewById(R.id.btn_status);
                        Button moreButton = view.findViewById(R.id.btn_more);

                        if (device.isActive()) {
                            //statusButton.setBackgroundResource(R.drawable.image_7);
                            statusButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.image_7);
                        } else {
                            //statusButton.setBackgroundResource(R.drawable.image_101);
                            statusButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.image_101);
                        }

                        deviceNameTextView.setText(device.getName());

                        statusButton.setOnClickListener(new View.OnClickListener() {
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

                                        if (device.isActive()){
                                            idh.updateDeviceActiveStatus(devicesByView.get(v).getId(), false);
                                            device.setActive(false);
                                        } else {
                                            idh.updateDeviceActiveStatus(devicesByView.get(v).getId(), true);
                                            device.setActive(true);
                                        }

                                        idh.close();

                                        fillDevices();

                                    }
                                }.start();
                            }
                        });

                        layout_devices.addView(view);
                        devicesByView.put(statusButton, device);
                        devicesByView.put(moreButton, device);
                    }
                });
            }
        }.start();
    }

}

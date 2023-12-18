package com.example.project_iot.activities.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.project_iot.R;
import com.example.project_iot.activities.authorisation.Login;
import com.example.project_iot.database.DatabaseHelperFactory;
import com.example.project_iot.database.IDatabaseHelper;
import com.example.project_iot.database.MySQLDatabaseHelper;

import java.util.ArrayList;

public class AddSensor extends AppCompatActivity {

    ImageView btn_back;

    Button btn_add;

    private Activity activity;

    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = this;

        setContentView(R.layout.activity_add_sensor);

        userId = getApplicationContext().getSharedPreferences("ProjectIoTPref", 0)
                .getInt("session_user_id", -1);

        if (userId < 0) {
            Intent intent = new Intent(getBaseContext(), Login.class);
            startActivity(intent);
            return;
        }

        /*
            Buttons
         */

        btn_back = (ImageView) findViewById(R.id.dodaj_sensor_wroc);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getBaseContext(), Sensors.class);
                startActivity(intent);
            }
        });

        btn_add = (Button) findViewById(R.id.add_sensor_add);
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addSensor();
            }
        });
    }

    private void addSensor() {

        EditText nameET = findViewById(R.id.add_sensor_nazwa);
        EditText serialET = findViewById(R.id.add_sensor_serial_number);
        EditText passwordET = findViewById(R.id.add_sensor_haslo);

        String name = nameET.getText().toString();
        String serial = serialET.getText().toString();
        String password = passwordET.getText().toString();

        if (name.isEmpty()){
            Toast.makeText(getApplicationContext(), "Wpisz nazwę!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (serial.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Wpisz numer seryjny!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Wpisz hasło urządzenia!", Toast.LENGTH_SHORT).show();
            return;
        }

        int serialInt = -1;

        try {
            serialInt = Integer.valueOf(serial);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Numer seryjny musi zawierać wyłącznie cyfry!", Toast.LENGTH_SHORT).show();
            return;
        }

        int serialIntFinal = serialInt;

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

                MySQLDatabaseHelper.DevicePairingInfo dpi = idh.getDevicePairingInfo(serialIntFinal);

                if (dpi == null || !dpi.getPassword().equals(password)) {
                    activity.runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Błędne dane!.", Toast.LENGTH_SHORT).show());
                    idh.close();
                    return;
                }

                if (idh.getUserDevicesIds(userId).contains(dpi.getDeviceId())) {
                    activity.runOnUiThread(() -> Toast.makeText(getApplicationContext(), "To urządzenie jest już dodane!.", Toast.LENGTH_SHORT).show());
                    idh.close();
                    return;
                }

                ArrayList<Integer> devices =  idh.getUserDevicesIds(userId);
                devices.add(dpi.getDeviceId());

                idh.updateUserDevicesIds(userId, devices);

                idh.updateDeviceName(dpi.getDeviceId(), name);

                idh.close();

                activity.runOnUiThread(() -> {
                    Intent intent = new Intent(getBaseContext(), Sensors.class);
                    startActivity(intent);
                    Toast.makeText(getApplicationContext(), "Dodano urządzenie!.", Toast.LENGTH_SHORT).show();
                });
            }
        }.start();

    }
}

package com.example.project_iot.activities.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import com.example.project_iot.objects.devices.ADevice;
import com.example.project_iot.objects.devices.VibrationSensorDevice;
import com.example.project_iot.utils.SftpHelper;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;

import java.io.File;
import java.io.IOException;

public class AdditionalInformation extends AppCompatActivity {

    ImageView btn_back;

    LinearLayout layout_additional_info;

    EditText name;
    EditText desc;

    EditText vibration;


    private Activity activity;

    private int userId;

    private int deviceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = this;

        setContentView(R.layout.activity_additional_info);

        userId = getApplicationContext().getSharedPreferences("ProjectIoTPref", 0)
                .getInt("session_user_id", -1);

        if (userId < 0) {
            Intent intent = new Intent(getBaseContext(), Login.class);
            startActivity(intent);
            return;
        }

        deviceId = getIntent().getIntExtra("device_id", -1);

        if (deviceId < 0) {
            Intent intent = new Intent(getBaseContext(), Sensors.class);
            startActivity(intent);
            return;
        }

        /*
            Alerts
         */

        layout_additional_info = (LinearLayout) findViewById(R.id.layout_additional_info);

        new Thread() {

            @Override
            public void run(){

                IDatabaseHelper idh = DatabaseHelperFactory.getMysqlDatabase();
                if (!idh.open()) {
                    activity.runOnUiThread(() -> {
                        Toast.makeText(getApplicationContext(), "Nie udało się połączyć z systemem.", Toast.LENGTH_SHORT).show();
                    });
                    return;
                }

                ADevice dev = idh.getDevice(deviceId);

                idh.close();

                activity.runOnUiThread(() -> {
                    fillMenu(dev);
                });


            }

        }.start();

        /*
            Buttons
         */

        btn_back = (ImageView) findViewById(R.id.additional_info_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getBaseContext(), Sensors.class);

                startActivity(intent);
            }
        });
    }

    private void fillMenu(ADevice dev) {
        fillName(dev);

        fillVibrationThreshold(dev);

        fillDesc(dev);

        fillSaveButton(dev);

        fillCameraFiles(dev);
    }

    private void fillName(ADevice dev){
        View view = activity.getLayoutInflater().inflate(R.layout.layout_info_short_text, null);

        name = view.findViewById(R.id.edit_text);
        name.setHint("Podaj nazwę");
        name.setText(dev.getName());

        TextView tv = view.findViewById(R.id.label);
        tv.setText("Podaj nazwę");

        layout_additional_info.addView(view);
    }

    private void fillDesc(ADevice dev){
        View view = activity.getLayoutInflater().inflate(R.layout.layout_info_long_text, null);

        desc = view.findViewById(R.id.edit_text_long);
        desc.setHint("Opis");
        desc.setText(dev.getDescription());

        TextView tv = view.findViewById(R.id.label_long);
        tv.setText("Notatki");

        layout_additional_info.addView(view);
    }

    private void fillVibrationThreshold(ADevice dev){

        if (dev.getType() != ADevice.Type.VIBRATE_SENSOR)
            return;

        View view = activity.getLayoutInflater().inflate(R.layout.layout_info_short_number, null);

        vibration = view.findViewById(R.id.edit_text_number);
        vibration.setHint("Podaj próg alarmu");
        vibration.setText(((VibrationSensorDevice) dev).getThreshold()+"");

        TextView tv = view.findViewById(R.id.label_number);
        tv.setText("Próg alarmu");

        layout_additional_info.addView(view);
    }

    private void fillCameraFiles(ADevice dev){

        if (dev.getType() != ADevice.Type.CAMERA)
            return;

        Context context = this.getBaseContext();
        File filesDir = this.getFilesDir();

        new Thread() {
            @Override
            public void run() {

                try {

                    SftpHelper sftp = new SftpHelper(context);

                    for (String fileName : sftp.getFiles(userId)){

                        String systemFileName = filesDir.getPath()+"/"+fileName;

                        sftp.getFile("/files/"+fileName, systemFileName);

                        File imgFile = new File(systemFileName);
                        if(imgFile.exists())  {

                            View view = activity.getLayoutInflater().inflate(R.layout.layout_info_image, null);
                            ImageView imageView = view.findViewById(R.id.image);
                            imageView.setImageURI(Uri.fromFile(imgFile));

                            TextView tv = view.findViewById(R.id.label);
                            tv.setText(fileName.split("_")[1].split("\\.")[0]);

                            activity.runOnUiThread(() -> {
                                layout_additional_info.addView(view);
                            });

                        }
                    }

                } catch (JSchException | IOException | SftpException e) {
                    throw new RuntimeException(e);
                }
            }
        }.start();

        //layout_additional_info.addView(view);
    }

    private void fillSaveButton(ADevice dev) {
        View view = activity.getLayoutInflater().inflate(R.layout.layout_info_save, null);

        Button b = view.findViewById(R.id.info_zapisz);

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String sname = name.getText().toString();
                String sdesc = desc.getText().toString();

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

                        if (sname != null && dev.getName() != null && !dev.getName().equals(sname)){
                            idh.updateDeviceName(dev.getId(), sname);
                        }

                        if (sdesc != null && dev.getDescription() != null &&  !dev.getDescription().equals(sdesc)){
                            idh.updateDeviceDescription(dev.getId(), sdesc);
                        }

                        if (dev.getType() == ADevice.Type.VIBRATE_SENSOR){
                            VibrationSensorDevice vdev = (VibrationSensorDevice) dev;
                            vdev.setThreshold(Integer.valueOf(vibration.getText().toString()));
                            idh.updateDeviceAdditionalSettings(vdev.getId(), vdev.serializeAdditionalSettings());
                        }

                        idh.close();

                        activity.runOnUiThread(() -> {
                            Toast.makeText(getApplicationContext(), "Zapisano.", Toast.LENGTH_SHORT).show();
                        });

                    }
                }.start();

            }
        });

        layout_additional_info.addView(view);
    }

}

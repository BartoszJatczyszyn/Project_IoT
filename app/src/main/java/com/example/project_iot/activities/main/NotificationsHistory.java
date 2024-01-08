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
import com.example.project_iot.objects.Notification;

import java.util.ArrayList;

public class NotificationsHistory extends AppCompatActivity {

    ImageView btn_back;

    LinearLayout layout_notifications;

    private Activity activity;

    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = this;

        setContentView(R.layout.activity_history_notifications);

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

        layout_notifications = (LinearLayout) findViewById(R.id.lista_powiadomien);
        fillAlerts();

        /*
            Buttons
         */

        btn_back = (ImageView) findViewById(R.id.powiadomienia_wroc);
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
        layout_notifications.addView(view);

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

                ArrayList<Notification> notifications = idh.getAllNotifications(userId);

                idh.close();

                activity.runOnUiThread(() -> {

                    layout_notifications.removeAllViews();

                    if (notifications.isEmpty()){
                        TextView view = new TextView(activity.getApplicationContext());
                        view.setText("Brak powiadomień.");
                        view.setTextSize(16f);
                        layout_notifications.addView(view);
                        return;
                    }

                    for (Notification notification : notifications) {

                        View view = activity.getLayoutInflater().inflate(R.layout.layout_single_alert_listing, null);
                        TextView dateTextView = view.findViewById(R.id.date);
                        TextView alertTextView = view.findViewById(R.id.alert);

                        dateTextView.setText(notification.getInsertDate().toString());
                        alertTextView.setText(notification.getContent());

                        layout_notifications.addView(view);
                    }
                });
            }
        }.start();
    }

}

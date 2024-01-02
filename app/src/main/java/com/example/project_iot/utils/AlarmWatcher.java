package com.example.project_iot.utils;


import static androidx.core.content.ContextCompat.getSystemService;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.project_iot.R;
import com.example.project_iot.activities.main.Menu;
import com.example.project_iot.database.DatabaseHelperFactory;
import com.example.project_iot.database.IDatabaseHelper;
import com.example.project_iot.objects.Alarm;

import java.util.ArrayList;

public class AlarmWatcher implements Runnable {
    private final Context context;
    private final NotificationChannel channel;
    private final NotificationManager notificationManager;
    private final int userId;
    private final IDatabaseHelper databaseHelper;
    //private final PendingIntent intent;
    private final Activity activity;
    public AlarmWatcher(Activity activity, Context context){
        this.context = context;
        this.activity = activity;
        this.notificationManager =
                (NotificationManager) getSystemService(context, NotificationManager.class);
        this.channel = new NotificationChannel("IOT_APP_CHANNEL_ID",
                "IOT_APP_CHANNEL_NAME",
                NotificationManager.IMPORTANCE_HIGH);
        this.channel.setDescription("YOUR_NOTIFICATION_CHANNEL_DESCRIPTION");
        assert this.notificationManager != null;
        this.notificationManager.createNotificationChannel(channel);
        this.userId = context.getSharedPreferences("ProjectIoTPref", 0)
                .getInt("session_user_id", -1);
        this.databaseHelper = DatabaseHelperFactory.getMysqlDatabase();
        //this.intent = new PendingIntent(this.activity, Menu.class);
    }

    @Override
    public void run() {
        Log.d("IOT", "AlarmWatcher running in background!");
        if (!databaseHelper.open()){
            Log.e("IOT", "Cannot connect to database");
            return;
        }

        ArrayList<Alarm> alarms = databaseHelper.getAlarmsWithStatus(userId, Alarm.Status.ACTIVE.toString());
        if (alarms.size() != 0) {
            for (Alarm alarm : alarms) {
                NotificationCompat.Builder builder = new NotificationCompat.Builder(context, this.channel.getId())
                        .setSmallIcon(R.drawable.alert)
                        .setContentTitle("New alarm!")
                        .setContentText(alarm.getMessage())
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                notificationManager.notify(0, builder.build());
            }
        } else {
            Log.d("IOT", "alarms has size = 0!");
        }
    }
}

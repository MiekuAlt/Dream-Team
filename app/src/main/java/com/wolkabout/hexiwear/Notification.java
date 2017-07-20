package com.wolkabout.hexiwear;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.wolkabout.hexiwear.activity.StepCountActivity;
import com.wolkabout.hexiwear.fragment.ChatFragment;


/**
 * Created by shuchenxin on 2017-07-18.
 */

public class Notification extends AppCompatActivity {
    NotificationCompat.Builder mBuilder;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);
    }
    public void Notification(View view) {
        mBuilder =
        new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.hello_kitty)
                .setContentTitle("My notification")
                .setContentText("Hello World!");
        Intent resultIntent = new Intent(this, ChatFragment.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(StepCountActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, mBuilder.build());
    }

    public void addNotification() {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.hello_kitty)
                        .setContentTitle("Notifications Example")
                        .setContentText("This is a test notification");

        //Intent notificationIntent = new Intent(this, this.getClass());
        //PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
        //        PendingIntent.FLAG_UPDATE_CURRENT);
        //builder.setContentIntent(contentIntent);

        // Add as notification
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());
    }

}

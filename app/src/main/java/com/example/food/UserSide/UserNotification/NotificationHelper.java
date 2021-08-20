package com.example.food.UserSide.UserNotification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.net.Uri;
import android.os.Build;

import com.example.food.R;

import androidx.annotation.RequiresApi;

public class NotificationHelper extends ContextWrapper {

    private static final String CHANNEL_ID = "com.example.food.Session";
    private static final String CHANNEL_NAME = "Food Status";
    NotificationManager manager;

    public NotificationHelper(Context base) {
        super(base);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            createChannel();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)


    private void createChannel() {
        NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID,
                CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);

        notificationChannel.enableLights(true);
        notificationChannel.enableVibration(true);
        notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        getManager().createNotificationChannel(notificationChannel);
    }

    public NotificationManager getManager() {
        if(manager == null)
            manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        return manager;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Notification.Builder BuildNotification(String title, String body, PendingIntent content,
                                                  Uri SoundUri)
    {
        return new Notification.Builder(getApplicationContext(),CHANNEL_ID).setContentIntent(content)
                .setContentTitle(title).setContentText(body).setSmallIcon(R.drawable.add)
                .setSound(SoundUri).setAutoCancel(false);
    }

}

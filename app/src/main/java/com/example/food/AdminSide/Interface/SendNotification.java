package com.example.food.AdminSide.Interface;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import com.example.food.AdminSide.MainMenuFragment.AdminMenu;
import com.example.food.R;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class SendNotification {

    public static void Notification(Context context,String Title, String Body){

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(AdminMenu.ID, AdminMenu.FoodLIST, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(AdminMenu.DES);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder Builder = new NotificationCompat.Builder(context, AdminMenu.ID).setSmallIcon(R.id.cart)
                .setContentTitle(Title).setContentText(Body).setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(1,Builder.build());

    }

}

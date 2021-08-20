package com.example.food.WorkManager;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.example.food.AdminSide.AdminMainActivity;
import com.example.food.Common.InternetConnection;
import com.example.food.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import static android.content.Context.NOTIFICATION_SERVICE;

public class worker extends Worker implements ChildEventListener {

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    static String Id;
    InternetConnection connection;

    public worker( @NonNull Context context, @NonNull WorkerParameters params){
            super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        int Count = 12;
        int s = 13;
        Log.e("Yoyo",String.valueOf(Count+s));
        // Do something after 5s = 5000ms
       /* firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("New");
        databaseReference.addChildEventListener(this);*/
        return Result.success();
    }

    @Override
    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
     /*   databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot1) {
                ShowNotificationToUser(String.valueOf(snapshot1.getChildrenCount()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });*/
    }

    private void ShowNotificationToUser(String Count) {

        Intent intent = new Intent(getApplicationContext(), AdminMainActivity.class);
        intent.putExtra("Notification",Count);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent intent1 = PendingIntent.getActivity(getApplicationContext(),0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            String name = "User_Channel";
            String description = "User";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("Channel1", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getApplicationContext().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(),"Channel1");
        builder.setAutoCancel(true).setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis()).setTicker("Food")
                .setContentInfo("Order Updated")
                .setSmallIcon(R.drawable.restaurant_menu)
                .setContentText(" You have " + Count + " New Order")
                .setContentInfo("Info").setContentIntent(intent1);

        /*int Random = new Random().nextInt(9999-1)+1;*/
        NotificationManager  notificationManager = (NotificationManager) getApplicationContext().getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(1,builder.build());
        /*startForeground(Random,builder.build());*/
    }

    @Override
    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

    }

    @Override
    public void onChildRemoved(@NonNull DataSnapshot snapshot) {

    }

    @Override
    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

    }

    @Override
    public void onCancelled(@NonNull DatabaseError error) {

    }
}

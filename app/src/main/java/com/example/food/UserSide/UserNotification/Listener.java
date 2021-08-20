package com.example.food.UserSide.UserNotification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import com.example.food.R;
import com.example.food.UserSide.ClientMainActivity;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class Listener extends Service implements ChildEventListener {

    FirebaseDatabase db;
    DatabaseReference request;
    static String Id;

    public Listener() {

    }

    public static String getId(String OrederId){
        Id = OrederId;
        return OrederId;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        db = FirebaseDatabase.getInstance();
        request = db.getReference().child("New").child(Id);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        request.addChildEventListener(this);
        return START_STICKY;
    }

    @Override
    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

    }

    @Override
    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
        String currentStatus = snapshot.getValue().toString();
        ShowNotificationToUser(currentStatus);
    }

    private void ShowNotificationToUser(String Status) {
        Intent intent = new Intent(getBaseContext(), ClientMainActivity.class);
        intent.putExtra("Notification",Status);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent intent1 = PendingIntent.getActivity(getBaseContext(),0,intent,PendingIntent.FLAG_UPDATE_CURRENT);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            String name = "User_Channel";
            String description = "User";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("Channel1", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getBaseContext(),"Channel1");
        builder.setAutoCancel(true).setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis()).setTicker("Food")
                .setContentInfo("Order Updated")
                .setSmallIcon(R.drawable.restaurant_menu)
                .setContentText(" Order Status : " + Status)
                .setContentInfo("Info").setContentIntent(intent1);

        NotificationManager  notificationManager = (NotificationManager) getBaseContext().getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(1,builder.build());
        /*startForeground(1,builder.build());*/
        /*stopForeground(true);*/
        /*stopSelf();*/
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

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}

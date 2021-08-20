package com.example.food.AdminSide.Notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import com.example.food.AdminSide.AdminMainActivity;
import com.example.food.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class NewOrder extends Service implements ChildEventListener {

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    static String Id;
    Handler handler;

    public NewOrder() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("New");
    }

    public static void GetId(String id){
        Id = id;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {


                Toast.makeText(getApplication(),"new",Toast.LENGTH_SHORT).show();
            }
        }, 5000);
        return android.app.Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot1) {
                ShowNotificationToUser(String.valueOf(snapshot1.getChildrenCount()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void ShowNotificationToUser(String Count) {

        Intent intent = new Intent(getBaseContext(), AdminMainActivity.class);
        intent.putExtra("Notification",Count);
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
                .setContentText(" You have " + Count + " New Order")
                .setContentInfo("Info").setContentIntent(intent1);

        /*int Random = new Random().nextInt(9999-1)+1;*/
        NotificationManager  notificationManager = (NotificationManager) getBaseContext().getSystemService(NOTIFICATION_SERVICE);
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
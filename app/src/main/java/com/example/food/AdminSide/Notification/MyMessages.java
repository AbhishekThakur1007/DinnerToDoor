package com.example.food.AdminSide.Notification;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.example.food.AdminSide.Interface.SendNotification;
import com.example.food.UserSide.Database;
import com.example.food.UserSide.ClientMainActivity;
import com.example.food.UserSide.UserNotification.NotificationHelper;
import com.example.food.Common.SessionManager;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

public class MyMessages extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            sendNotification26(remoteMessage);
        else
            sendNotification(remoteMessage);

        if(remoteMessage.getNotification() != null){
            String title = remoteMessage.getNotification().getTitle();
            String body = remoteMessage.getNotification().getBody();
            SendNotification.Notification(getApplicationContext(),title,body);

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void sendNotification26(RemoteMessage remoteMessage) {
        RemoteMessage.Notification notification = remoteMessage.getNotification();
        String title = notification.getTitle();
        String  content = notification.getBody();

        Intent intent = new Intent(this, ClientMainActivity.class);
        intent.putExtra("SendToOrders","Orders");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_ONE_SHOT);
        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationHelper helper = new NotificationHelper(this);
        Notification.Builder builder = helper.BuildNotification(title,content,pendingIntent,defaultSound);
        helper.getManager().notify(new Random().nextInt(),builder.build());
    }

    private void sendNotification(RemoteMessage remoteMessage) {
        RemoteMessage.Notification notification = remoteMessage.getNotification();
        Intent intent = new Intent(this, ClientMainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_ONE_SHOT);

        Uri Sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        SessionManager sessionManager = new SessionManager(getApplicationContext(),SessionManager.SESSION_USERSESSION);
        HashMap<String,String> UserId = sessionManager.getUserDetailFromSession();
        String Id = UserId.get(SessionManager.KEY_USERID);
        assert Id != null;
        Database db  = new Database(getApplicationContext());
        Bundle bundle = new Bundle();
        bundle.putString("token",s);
        long check = db.UpdateUserData(Id,5,bundle);
        if(check > -1){
            FirebaseDatabase.getInstance().getReference("Users").child(Id).child("token").setValue(s);
        }
    }

}

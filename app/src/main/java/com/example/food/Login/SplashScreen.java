package com.example.food.Login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import com.example.food.AdminSide.AdminMainActivity;
import com.example.food.UserSide.ClientMainActivity;
import com.example.food.R;
import com.example.food.Common.SessionManager;

import androidx.appcompat.app.AppCompatActivity;

public class SplashScreen extends AppCompatActivity {

    boolean isLoggedIn = true;
    public static int SPLASH_TIMER = 1500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SessionManager sessionManager = new SessionManager(SplashScreen.this,SessionManager.SESSION_USERSESSION);
                boolean i = sessionManager.checkLogin();

                SharedPreferences sharedPreferences = getSharedPreferences("tellMe",MODE_PRIVATE);
                String Finally = sharedPreferences.getString("UserOrAdmin","");
                if (i != isLoggedIn){
                    startActivity(new Intent(SplashScreen.this, Retailerstart.class));
                }
                else {
                    //Return whose session is saved
                    // Getting Session Started
                    if(Finally.equalsIgnoreCase("admin")){
                        Intent intent1 = new Intent(SplashScreen.this, AdminMainActivity.class);
                        startActivity(intent1);
                    }else{
                        Intent intent = new Intent(SplashScreen.this, ClientMainActivity.class);
                        startActivity(intent);
                    }
                }
                finish();
            }
        }, SPLASH_TIMER);

    }
}



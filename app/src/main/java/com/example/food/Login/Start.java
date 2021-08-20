package com.example.food.Login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.food.R;

import androidx.appcompat.app.AppCompatActivity;

public class Start extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
    }

    //Login
    public void calllogin(View view){

        Intent intent = new Intent(getApplicationContext(),loginuser1.class);
        startActivity(intent);

    }

    //Create
    public void Create(View view){

        Intent intent = new Intent(getApplicationContext(), signup1.class);
        startActivity(intent);
    }

}
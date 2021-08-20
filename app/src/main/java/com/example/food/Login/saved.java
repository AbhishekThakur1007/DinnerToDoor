package com.example.food.Login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.food.R;

import androidx.appcompat.app.AppCompatActivity;

public class saved extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved);

        findViewById(R.id.savedChanges).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callLoginAgain(v);
            }
        });
    }

    //Login
    public void callLoginAgain(View view){

        Intent intent = new Intent(getApplicationContext(),loginuser1.class);
        startActivity(intent);
        startActivity(intent);

    }
}
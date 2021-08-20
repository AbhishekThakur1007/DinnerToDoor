package com.example.food.Login;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.food.R;

import java.util.Calendar;

import androidx.appcompat.app.AppCompatActivity;

public class signup3 extends AppCompatActivity {

    //variables
    ImageButton backBtn;
    Button next;
    TextView titleText;
    DatePicker datePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup2);

        //Hooks
        backBtn = findViewById(R.id.sign_back2);
        next = findViewById(R.id.next2);
        titleText = findViewById(R.id.title_text);
        datePicker = findViewById(R.id.age);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callnextsignup3(v);
            }
        });

 }


    public void callnextsignup3(View view) {

         if(!validateAge()){
             return;
         }

         int day = datePicker.getDayOfMonth();
         int month = datePicker.getMonth();
         int year = datePicker.getYear();

         String date = day+"/"+month+"/"+year;

        //Retrieve Data from SignUp
        String nameS1= getIntent().getStringExtra("name");
        /*String userS2=  getIntent().getStringExtra("user");*/
        String emailS1=  getIntent().getStringExtra("email");
        String passwordS1=  getIntent().getStringExtra("password");
        String Admin=  getIntent().getStringExtra("Admin");
        String Staff=  getIntent().getStringExtra("staff");
        String Phone =  getIntent().getStringExtra("phone");

        Intent intent = new Intent(signup3.this, signup4.class);

        // Send to SignUp3
        intent.putExtra("name",nameS1);
        /*intent.putExtra("user",userS2);*/
        intent.putExtra("email",emailS1);
        intent.putExtra("password",passwordS1);
        intent.putExtra("date",date);
        intent.putExtra("phone",Phone);
        intent.putExtra("staff",Staff);
        intent.putExtra("Admin",Admin);

        //Add Transition
        Pair[] pairs = new Pair[3];

        pairs[0] = new Pair<View, String>(backBtn, "sign_back");
        pairs[1] = new Pair<View, String>(next, "next");
        pairs[2] = new Pair<View, String>(titleText, "title_text");

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(signup3.this, pairs);
            startActivity(intent, options.toBundle());
        } else {
            startActivity(intent);
        }
    }

    //Age Restriction
    private boolean validateAge() {
        int currentyear = Calendar.getInstance().get(Calendar.YEAR);
        int userAge = datePicker.getYear();
        int isAgeValid = currentyear - userAge;

        if (isAgeValid < 14) {
            Toast.makeText(this, "You are not eligible", Toast.LENGTH_SHORT).show();
            return false;
        } else
            return true;
    }

}
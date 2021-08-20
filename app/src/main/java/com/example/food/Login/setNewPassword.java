package com.example.food.Login;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;

import com.example.food.Common.CheckInternet;
import com.example.food.UserSide.Database;
import com.example.food.R;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class setNewPassword extends AppCompatActivity {

    TextInputLayout new_pass1,con_pass1;
    String Parent;
    /*ProgressBar progressBar;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_new_password);
        new_pass1 = findViewById(R.id.new_pass);
        con_pass1 = findViewById(R.id.con_pass);
    }

    //Set New Password
    public void setNew(View view){

        //Check Internet Connection
        CheckInternet checkInternet = new CheckInternet();
        if (!checkInternet.isConnected(setNewPassword.this)){
            showCustomDialog();
            return;
        }


        if (!validateNew() | !validateConfirm()){
            return;
        }
       /* progressBar.setVisibility(View.VISIBLE);*/

        //Get data from fields
        String _setNewPassword = Objects.requireNonNull(new_pass1.getEditText()).getText().toString().trim();
        String _phoneNumber = getIntent().getStringExtra("phoneNo");

        //Update Data in Db
        FirebaseDatabase.getInstance().getReference("Users").orderByChild("phoneNo").equalTo(_phoneNumber).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Parent = snapshot.getKey();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        Database db = new Database(this);
        Bundle bundle = new Bundle();
        bundle.putString("Password",_setNewPassword);
        long Check = db.UpdateUserData(Parent,4,bundle);
        if(Check > -1){
            Toast.makeText(this,"Password Changed",Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(),saved.class));
            finish();
        }
    }

    //Ask User For network Connection
    private void showCustomDialog()  {

        AlertDialog.Builder builder = new AlertDialog.Builder(setNewPassword.this);
        builder.setMessage("Please Connect to internet to proceed further")

                //Do other task by closing popup = true. If not then set to false
                .setCancelable(true)
                .setPositiveButton("Connect",new DialogInterface.OnClickListener() {

                    //Open Wifi Setting
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i){
                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                    }
                })
                .setNegativeButton("Cancel",new DialogInterface.OnClickListener() {

                    //Not Open Wifi Setting and head back to start
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i){
                        startActivity(new Intent(getApplicationContext(),Retailerstart.class));
                        finish();
                    }
                });
    }

    //New password check
    private boolean validateNew() {
        String value = new_pass1.getEditText().getText().toString().trim();
        String checkpassword = "^.{5,20}$";
        /*"^" +

                //"(?=.*[0-9])" +         //at least 1 digit
                //"(?=.*[a-z])" +         //at least 1 lower case letter
                //"(?=.*[A-Z])" +         //at least 1 upper case letter
               *//* "(?=.*[a-zA-Z])" +      //any letter*//*
                //"(?=.*[@#$%^&+=])" +    //at least 1 special character
                "(?=\\S+$)" +           //no white spaces
                ".{4,}" +               //at least 4 characters
                "$";*/


        if (value.isEmpty()) {
            new_pass1.setError("Field can not be empty");
            return false;
        } else if (!value.matches(checkpassword)) {
            new_pass1.setError("Minimum 5 characters!");
            return false;
        } else {
            new_pass1.setError(null);
            new_pass1.setErrorEnabled(false);
            return true;
        }
    }

   //ConfirmPassword
    private boolean validateConfirm() {
        String pho1 = new_pass1.getEditText().getText().toString().trim();
        String pho2 = con_pass1.getEditText().getText().toString().trim();

        if (pho2.isEmpty()) {
            con_pass1.setError("Field can not be empty");
            return false;
        } else if (!pho1.matches(pho2)) {
            con_pass1.setError("Password does not match.");
            return false;
        } else {
            con_pass1.setError(null);
            con_pass1.setErrorEnabled(false);
            return true;
        }
    }

}
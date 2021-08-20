package com.example.food.Login;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.example.food.Common.CheckInternet;
import com.example.food.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hbb20.CountryCodePicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class forgotpass extends AppCompatActivity {


    TextInputLayout phoneFor;
    TextInputEditText phoneFor1;
    CountryCodePicker ccp;
    ImageButton Back;
    RelativeLayout progressBar;
    String _getUserEnteredPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpass);

        phoneFor = findViewById(R.id.phone_number1);
        phoneFor1 = findViewById(R.id.phone_number12);
        ccp = findViewById(R.id.code_picker1);
        progressBar = findViewById(R.id.progress);
        Back = findViewById(R.id.forgetBack);
        progressBar.setVisibility(View.GONE);

        Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        phoneFor1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                phoneFor.setError(null);
                phoneFor.setErrorEnabled(false);

                    if(s.length() <= 10){
                        setFocus(phoneFor1,false);
                    }

                    if(s.length() == 10){
                        setFocus(phoneFor1,true);
                    }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void verifyPhoneNumber(View view) {

        //Check Internet Connection
        CheckInternet checkInternet = new CheckInternet();
        if (!checkInternet.isConnected(forgotpass.this)) {
            showCustomDialog();
        }

        //Get No.
        String _getUserEntered = phoneFor.getEditText().getText().toString().trim();

        if (_getUserEntered.isEmpty() || _getUserEntered.length() < 10) {
            phoneFor.setError("10 digits number required");
            phoneFor.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        if (_getUserEntered.charAt(0) == 0) {
            _getUserEnteredPhone = _getUserEnteredPhone.substring(1);
        }

        final String _completePhoneNumber = "+" + ccp.getFullNumber() + _getUserEntered;

        //Check weather user exists or not in Database
        Query checkUser = FirebaseDatabase.getInstance().getReference("Users").orderByChild("phoneNo").equalTo(_completePhoneNumber);
        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //If Phone no. exists then call OTP to verify
                if (dataSnapshot.exists()) {
                    phoneFor.setError(null);
                    phoneFor.setErrorEnabled(false);

                    Intent intent = new Intent(getApplicationContext(), otp.class);
                    intent.putExtra("phone", _completePhoneNumber);

                    // New thing !! Checker
                    intent.putExtra("whatToDo", "updateData");
                    startActivity(intent);
                    finish();
                    progressBar.setVisibility(View.GONE);

                } else {
                    progressBar.setVisibility(View.GONE);
                    phoneFor.setError("No Username with this No.");
                    phoneFor.requestFocus();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        {

        }
        ;
    }

    //Ask User For network Connection
    private void showCustomDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(forgotpass.this);
        builder.setMessage("Please Connect to internet to proceed further")

                //Do other task by closing popup = true. If not then set to false
                .setCancelable(true)
                .setPositiveButton("Connect", new DialogInterface.OnClickListener() {

                    //Open Wifi Setting
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                    //Not Open Wifi Setting and head back to start
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivity(new Intent(getApplicationContext(), Retailerstart.class));
                        finish();
                    }
                });
    }

    public void setFocus(TextInputEditText editText,boolean check){
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        editText.requestFocus();
        if(check){
            imm.hideSoftInputFromWindow(editText.getWindowToken(),0);
        }
        else{
            imm.showSoftInput(editText,InputMethodManager.SHOW_IMPLICIT);
        }

    }

}

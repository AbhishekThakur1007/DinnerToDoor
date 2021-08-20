package com.example.food.Login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.chaos.view.PinView;
import com.example.food.AdminSide.AdminMainActivity;
import com.example.food.Common.UserDetails_Constructor;
import com.example.food.UserSide.Database;
import com.example.food.UserSide.ClientMainActivity;
import com.example.food.R;
import com.example.food.Common.SessionManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class otp extends AppCompatActivity {

    PinView pin_view;
    String codeBySystem;
    TextView number;
    FirebaseAuth mAuth;
    String fullName, email, phoneNo, password, date, gender, WhatToDo, staff, address,Village, Latitude, Longitude;
    String Isstaff;
    DatabaseReference databaseReference,Address;
    FirebaseDatabase firebaseDatabase;
    Button finale1;
    boolean check;
    String UUID;
    String Final;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        //Hooks
        pin_view = findViewById(R.id.pin_view);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users");
        finale1 = findViewById(R.id.finale);
        mAuth = FirebaseAuth.getInstance();

        finale1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckStatus();
            }
        });

        //Display Side Connect No.
        number = findViewById(R.id.phone_Display);

        //Previous ke saath connect (phone no. from signUp4)
        Intent intent = getIntent();
        String phoneSe = intent.getStringExtra("phone");
        number.setText(phoneSe);

        //Save to SQLite Only
        address = getIntent().getStringExtra("Address");
        Village = getIntent().getStringExtra("Village");
        gender = getIntent().getStringExtra("gender");
        date = getIntent().getStringExtra("date");
        password = getIntent().getStringExtra("password");
        Latitude = getIntent().getStringExtra("Latitude");
        Longitude = getIntent().getStringExtra("Longitude");
        /*String userS3=  getIntent().getStringExtra("user");*/

        // Save to SQLite and Firebase
        fullName = getIntent().getStringExtra("name");
        staff = getIntent().getStringExtra("staff");
        phoneNo = getIntent().getStringExtra("Admin");

        email = getIntent().getStringExtra("email");
        //Update Date CHECK WITH signInUsing function
        WhatToDo = getIntent().getStringExtra("whatToDo");

        if(staff.equals("false")){
            sendVerificationCodeToUser(phoneSe);
        }else {
            sendVerificationCodeToUser(phoneNo);
        }
    }

    private void sendVerificationCodeToUser(String phoneNo) {

                PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNo)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();

        PhoneAuthProvider.verifyPhoneNumber(options);

    }

    private final PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks =
            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                @Override
                public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                    super.onCodeSent(s, forceResendingToken);
                    codeBySystem = s;
                }

                @Override
                public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                    String code = phoneAuthCredential.getSmsCode();
                    if (code != null) {
                        pin_view.setText(code);
                        verifyCode(code);
                    }
                }

                @Override
                public void onVerificationFailed(@NonNull FirebaseException e) {
                    Toast.makeText(otp.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            };

    private void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codeBySystem, code);
        signInWithPhoneAuthCredential(credential);
    }

    // Tell me where to go
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            check = true;
                            Toast.makeText(otp.this, "Verification Completed.", Toast.LENGTH_SHORT).show();
                            UpdateWhere();

                        } else {
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(otp.this, "Verification Not Completed! Try Again.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    private void UpdateWhere() {
        if (WhatToDo.equals("new")) {
            storeNewUserData();
        } else if(WhatToDo.equals("updateData")){
            updateOldUserData();
        }
    }

    //Update old User
    private void updateOldUserData() {
        Intent intent = new Intent(getApplicationContext(), setNewPassword.class);
        intent.putExtra("phoneNo", phoneNo);
        startActivity(intent);
        finish();
    }

    //DateBase
    private void storeNewUserData() {
        UUID = String.valueOf(System.currentTimeMillis());
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                String token = task.getResult();

            }
        });
        Upload(null);
    }

    private void Upload(String token) {
        FirebaseDatabase rootNode = FirebaseDatabase.getInstance();

        //Pointing to reference
        DatabaseReference User = rootNode.getReference("Users");
        DatabaseReference Staff = rootNode.getReference("Staff");

        //See Whether its staff entry
        phoneNo = getIntent().getStringExtra("phone");

        if (staff.equals("kitchenenter")) {
            Isstaff = "true";
        } else {
            Isstaff = "false";
        }
        Final = address + " + " + Village;

        UserDetails_Constructor addNewUser = null;
        try {
            addNewUser = new UserDetails_Constructor(UUID,fullName, email, phoneNo, password, date, gender, Isstaff, Final, Latitude, Longitude,token);
        }catch (Exception e){
            Toast.makeText(otp.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
        }

        Database database = new Database(otp.this);

        assert addNewUser != null;
        long Check = database.SetUserData(addNewUser);
        if(Check > -1){
            //Child
            UserDetails_Constructor addNewUserToFirebase = new UserDetails_Constructor(null,fullName, email, phoneNo, null, null, null, Isstaff,null,null,null,token);

            if(Isstaff.equals("true")){
                Staff.child(UUID).setValue(addNewUserToFirebase).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Coordinates();
                    }
                });
            }else{
                User.child(UUID).setValue(addNewUserToFirebase).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Coordinates();
                    }
                });
            }

        }else{
            Toast.makeText(this,"Error",Toast.LENGTH_SHORT).show();
        }
    }

    public void Coordinates() {

        Address = databaseReference.child(UUID).child("Address").child("Default");
        Address.child("Address").setValue(Final).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Address.child("Coordinates").child("Latitude").setValue(Latitude).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Address.child("Coordinates").child("Longitude").setValue(Longitude);
                                StartSession();
                            }
                        }
                    });
                }
            }
        });
    }

    private void StartSession() {

        //Create a Session
        SessionManager sessionManager = new SessionManager(this, SessionManager.SESSION_USERSESSION);
        sessionManager.createLoginSession(UUID, fullName, email, phoneNo, password, date, gender, staff, Final, null, null);
        SharedPreferences sharedPreferences = getSharedPreferences("tellMe", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("UserOrAdmin",staff);
        editor.apply();

        Intent intent;
        if(Isstaff.equals("true")){
            intent = new Intent(this, AdminMainActivity.class);
            intent.putExtra("Staff",Isstaff);
        }else{
            intent = new Intent(this, ClientMainActivity.class);
        }

        startActivity(intent);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }

    // Button for Next
    private void CheckStatus() {
        if(check){
            UpdateWhere();
        }else{
            Toast.makeText(this,"Please Type OTP",Toast.LENGTH_SHORT).show();
        }
    }

    public void saved(View view) {
        String code = pin_view.getText().toString();
        if (!code.isEmpty()) {
            verifyCode(code);
        }

    }

}
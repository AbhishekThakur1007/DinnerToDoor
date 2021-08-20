package com.example.food.Login;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.food.AdminSide.AdminMainActivity;
import com.example.food.Common.CheckInternet;
import com.example.food.Common.UserDetails_Constructor;
import com.example.food.UserSide.Database;
import com.example.food.UserSide.ClientMainActivity;
import com.example.food.R;
import com.example.food.Common.SessionManager;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hbb20.CountryCodePicker;

import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Objects;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class loginuser1 extends AppCompatActivity {

    //Variables
    Button button,Login,create;
    ImageButton backPressed;
    CountryCodePicker countryCodePicker;
    TextInputLayout phoneNumber, password;
    TextInputEditText passwordRem , phoneRem;
    RelativeLayout progressBar;
    CheckBox rememberme;
    String _getUserEnteredPhone;
    String AES = "AES";
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginuser1);

        //Hooks
        Login = findViewById(R.id.Login);
        button = findViewById(R.id.forgot_1);
        create = findViewById(R.id.create);
        phoneNumber = findViewById(R.id.login_phone_number);
        countryCodePicker = findViewById(R.id.login_code_picker);
        progressBar = findViewById(R.id.progress);
        password = findViewById(R.id.login_pass);
        rememberme = findViewById(R.id.rememberMe);
        passwordRem = findViewById(R.id.passwordRem);
        phoneRem = findViewById(R.id.phoneRem);
        backPressed = findViewById(R.id.backPressed);
        auth = FirebaseAuth.getInstance();

        progressBar.setVisibility(View.GONE);

        backPressed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        phoneRem.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                phoneNumber.setErrorEnabled(false);
                phoneNumber.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callForgetPassword(v);
            }
        });

        passwordRem.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                password.setErrorEnabled(false);
                password.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                create(v);
            }
        });

        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                letUserIn(v);
            }
        });

        //Check weather phone number and password is already saved in shared preferences or not
        SessionManager sessionManager = new SessionManager(loginuser1.this,SessionManager.SESSION_REMEMBERME);
        if(sessionManager.checkRememberME()){
            HashMap<String,String> rememberMEDetails = sessionManager.getRememberMeDetailFromSession();
            phoneRem.setText(rememberMEDetails.get(SessionManager.KEY_SESSIONPHONENUMBER));
            passwordRem.setText(rememberMEDetails.get(SessionManager.KEY_SESSIONPASSWORD));
        }


    }

    //Login
    public void letUserIn(final View view) {

        //Check Internet Connection
        CheckInternet checkInternet = new CheckInternet();
        if (!checkInternet.isConnected(this)){
            showCustomDialog();
        }

        //Get No.
        _getUserEnteredPhone = Objects.requireNonNull(phoneNumber.getEditText()).getText().toString().trim();
        final String[] value = {Objects.requireNonNull(password.getEditText()).getText().toString().trim()};


        // No. Field Check Required
        if(_getUserEnteredPhone.isEmpty()  || _getUserEnteredPhone.length()<10){
            phoneNumber.setError("10 digits number required");
            phoneNumber.requestFocus();
            return;
        }else{
            if(value[0].isEmpty()){
                password.setError("Please enter password");
                password.requestFocus();
                return;
            }
        }

        progressBar.setVisibility(View.VISIBLE);

        //Get data
        String _phoneNumber = phoneNumber.getEditText().getText().toString().trim();
        final String _password = password.getEditText().getText().toString().trim();

        if(_phoneNumber.charAt(0)==0){
           _phoneNumber = _phoneNumber.substring(1);
        }

        final String _completePhoneNumber = "+"+countryCodePicker.getFullNumber()+_phoneNumber;

        if(rememberme.isChecked()){
            //Create a Session
            SessionManager sessionManager = new SessionManager(loginuser1.this,SessionManager.SESSION_REMEMBERME);
            sessionManager.createRememberMeSession(_phoneNumber,_password);
        }

        final Database db = new Database(this);

        Bundle Details = db.UserID(_completePhoneNumber);
        final String ID = Details.getString("Id");
        final String Phone = Details.getString("Phone");
        final String Password = Details.getString("Password");

        if(ID != null){
            //Database Query
            Query checkUser = FirebaseDatabase.getInstance().getReference("Users").child(ID);
            checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                    if (datasnapshot.child("phoneNo").exists()) {
                        String CheckNo = Objects.requireNonNull(datasnapshot.child("phoneNo").getValue()).toString();
                        if (CheckNo.equals(_completePhoneNumber) && Phone.equals(_completePhoneNumber)) {
                            String Check;
                            try {
                                Check = Decrypt(Password, _password);
                            } catch (Exception e) {
                                e.printStackTrace();
                                Check = Password;
                            }

                            if (Check.equals(_password)) {
                                phoneNumber.setError(null);
                                phoneNumber.setErrorEnabled(false);
                                phoneNumber.setError(null);
                                password.setError(null);
                                UserDetails_Constructor LoginInfo = db.UserDetails(ID);

                                //Getting data from Db
                                String FinalID = LoginInfo.getUSER_ID();
                                String _fullName = LoginInfo.getFullName();
                                String _phoneNo = LoginInfo.getPhoneNo();
                                String _Gender = LoginInfo.getGender();
                                String _date = LoginInfo.getDate();
                                String _password = LoginInfo.getPassword();
                                String _mail = LoginInfo.getEmail();
                                String _Lat = LoginInfo.getDLatitude();
                                String _Long = LoginInfo.getDLongitude();
                                String _Address = LoginInfo.getIsstaff();
                                String _user = LoginInfo.getIsstaff();

                                //Create a Session
                                SessionManager sessionManager = new SessionManager(loginuser1.this, SessionManager.SESSION_USERSESSION);
                                sessionManager.createLoginSession(FinalID, _fullName, _mail, _phoneNo, _password, _date, _Gender, _user, _Address, _Lat, _Long);

                                //Send to Splash
                                SharedPreferences sharedPreferences = getSharedPreferences("tellMe", MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("UserOrAdmin",_user);
                                editor.apply();
                                Intent intent;
                                //Admin
                                assert _user != null;
                                if (_user.equalsIgnoreCase("admin")) {
                                    //Send to Splash
                                    intent = new Intent(getApplicationContext(), AdminMainActivity.class);
                                }
                                //User
                                else {
                                    intent = new Intent(getApplicationContext(), ClientMainActivity.class);
                                }
                                progressBar.setVisibility((View.GONE));
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();

                            } else {
                                progressBar.setVisibility(View.GONE);
                                password.setErrorEnabled(true);
                                password.setError("Wrong Password.");
                            }
                        } else {
                            progressBar.setVisibility(View.GONE);
                            phoneNumber.setErrorEnabled(true);
                            phoneNumber.setError("No such user.");
                        }
                    }else{
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(loginuser1.this,"No Such User with this number",Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(loginuser1.this, databaseError.getMessage(),Toast.LENGTH_SHORT).show();

                }
            });
        }else{
            progressBar.setVisibility(View.GONE);
            Toast.makeText(loginuser1.this,"No such user exists.",Toast.LENGTH_SHORT).show();
        }

    }

    private String Decrypt(String data,String pass) throws Exception{
        SecretKeySpec key = EncryptPassword(pass);
        Cipher c = Cipher.getInstance(AES);
        c.init(Cipher.DECRYPT_MODE,key);
        byte[] decode = Base64.decode(data,Base64.DEFAULT);
        byte[] decode1 = c.doFinal(decode);
        return new String(decode1);
    }

    private SecretKeySpec EncryptPassword(String passwordS) throws Exception {
        final MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        byte []  bytes = passwordS.getBytes("UTF-8");
        messageDigest.update(bytes,0,bytes.length);
        byte[] key = messageDigest.digest();
        return new SecretKeySpec(key,"AES");
    }

    //Ask User For network Connection
    private void showCustomDialog()  {

        AlertDialog.Builder builder = new AlertDialog.Builder(loginuser1.this);
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

    //Forget Password
    public void callForgetPassword(View view){

        Intent intent = new Intent(loginuser1.this,forgotpass.class);
        startActivity(intent);
    }

    //Create Account
    public void create(View view){

        Intent intent = new Intent(loginuser1.this, signup1.class);
        startActivity(intent);
    }

}
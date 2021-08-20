package com.example.food.Login;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Pair;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.food.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.security.MessageDigest;
import java.util.Objects;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import androidx.appcompat.app.AppCompatActivity;

public class signup2 extends AppCompatActivity {

    //variables
    Button next;
    ImageButton BackButton;
    TextView titleText;
    String GetPhoneNo,Staff,Admin;
    String FinalpasswordS;
    String AES = "AES";

    //Text Variables
    TextInputLayout name, email, password;
    TextInputEditText name1, email1, password1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        //Hooks for animation
        next = findViewById(R.id.next1);
        titleText = findViewById(R.id.title_text);
        BackButton = findViewById(R.id.sign_back);
        BackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Admin = getIntent().getStringExtra("Admin");
        Staff = getIntent().getStringExtra("staff");
        GetPhoneNo = getIntent().getStringExtra("phone");

        //Hooks for fetching data
        name = findViewById(R.id.fullname);
        /*username = findViewById(R.id.username);*/
        email = findViewById(R.id.mail);
        password = findViewById(R.id.password1);

        //Edit Text
        name1 = findViewById(R.id.fullname14);
        email1 = findViewById(R.id.mail1);
        password1 = findViewById(R.id.password11);

        setFocusNew(name1);

        name1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() == 0){
                    setFocusNew(name1);
                }
                name.setError(null);
                name.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        password1.addTextChangedListener(new TextWatcher() {
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
        email1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                email.setErrorEnabled(false);
                email.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        next.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                // Save to string from hooks
                String nameS = name.getEditText().getText().toString().trim();
                /*String userS = username.getEditText()getText().toString().trim();*/
                String emailS = email.getEditText().getText().toString().trim();
                String passwordS = password.getEditText().getText().toString().trim();


                // If- Check !(not) then it will come back  |or
                if (!validateName()){
                    return;
                }else{
                    if(!validatepassword()){
                        return;
                    }
                    if(emailS != null || emailS.length() > 4 ){
                        if(!validateemail()){
                            return;
                        }
                    }
                    try {
                        FinalpasswordS = encrypt(passwordS);
                    } catch (Exception e) {
                        e.printStackTrace();
                        FinalpasswordS = passwordS;
                    }
                }

                Intent intent = new Intent(getApplicationContext(), signup3.class);

                // Send to SignUp2
                intent.putExtra("name",nameS);
                /*intent.putExtra("user",userS);*/
                intent.putExtra("email",emailS);
                intent.putExtra("password",FinalpasswordS);
                intent.putExtra("Admin",Admin);
                intent.putExtra("staff",Staff);
                intent.putExtra("phone",GetPhoneNo);

                //Add Transition
                Pair[] pairs = new Pair[3];
                pairs[0] = new Pair<View, String>(BackButton, "sign_back");
                pairs[1] = new Pair<View, String>(next, "next");
                pairs[2] = new Pair<View, String>(titleText, "title_text");

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(signup2.this, pairs);
                    startActivity(intent, options.toBundle());

                } else {
                    startActivity(intent);
                }
            }
        });

    }

    private SecretKeySpec EncryptPassword(String passwordS) throws Exception {
        final MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        byte []  bytes = passwordS.getBytes("UTF-8");
        messageDigest.update(bytes,0,bytes.length);
        byte[] key = messageDigest.digest();
        SecretKeySpec secretKeySpec = new SecretKeySpec(key,"AES");
        return secretKeySpec ;
    }

    private String encrypt(String passwordS) throws Exception{
        SecretKeySpec key = EncryptPassword(passwordS);
        Cipher c = Cipher.getInstance(AES);
        c.init(Cipher.ENCRYPT_MODE,key);
        byte[] Val = c.doFinal(passwordS.getBytes());
        String encrypt = Base64.encodeToString(Val,Base64.DEFAULT);
        return encrypt;
    }

    //Name error check
    private boolean validateName() {

        if(Objects.requireNonNull(name1.getText()).toString().length() == 0){
            name.setError("Field can not be empty");
            setFocusNew(name1);
            return false;
        }else{
            name.setError(null);
            name.setErrorEnabled(false);
            return true;
        }
    }

    //username error check
    /*   private boolean validateusername() {
        String value = username.getEditText().toString().trim();
        String checkspace = "\\A\\w{1,20}\\z";

        if (value.isEmpty()) {
            name.setError("Field can not be empty");
            return false;
        } else if (value.length() > 20) {
            name.setError("Username is too big");
            return false;
        } else if (!value.matches(checkspace)) {
            name.setError("No white spaces allowed");
            return false;
        } else {
            username.setError((null));
            username.setErrorEnabled(false);
            return true;
        }

    }*/

    //Email check
    private boolean validateemail() {
        String value = Objects.requireNonNull(email.getEditText()).getText().toString().trim();
        String checkEmail = "[a-zA-Z0-9._-]+@[a-z]+.+[a-z]+";

        if (value.isEmpty()) {
            email.setError(null);
            email.setErrorEnabled(false);
            return true;
        } else if (!value.matches(checkEmail)) {
            email.setError("Invalid E-Mail");
            return false;
        } else {
            email.setError(null);
            email.setErrorEnabled(false);
            return true;
        }
    }

    //password check
    private boolean validatepassword() {
        String value = Objects.requireNonNull(password1.getText()).toString().trim();
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
            password.setError("Field can not be empty");
            setFocusNew(password1);
            return false;
        } else if (!value.matches(checkpassword)) {
            password.setError("Minimum 5 characters!");
            setFocusNew(password1);
            return false;
        } else {
            password.setError(null);
            password.setErrorEnabled(false);
            return true;
        }
    }

    public void setFocusNew(TextInputEditText editText){
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        editText.requestFocus();
        editText.setCursorVisible(true);
        imm.showSoftInput(editText,InputMethodManager.SHOW_IMPLICIT);
    }

}

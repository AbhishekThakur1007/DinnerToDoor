package com.example.food.Login;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.food.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hbb20.CountryCodePicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class signup1 extends AppCompatActivity {

    //Variables
    TextInputLayout phone;
    TextInputEditText phone1;
    ImageButton sign_back31;
    CountryCodePicker countryCodePicker;
    CheckBox entry;
    DatabaseReference CheckUser;
    FirebaseDatabase firebaseDatabase;
    int send,m;
    Button Login,ChangeNumber;
    TextView HeadingLocButton1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup3);

        //Hooks
        /*scrollView = findViewById(R.id.signup3);*/
        countryCodePicker = findViewById(R.id.code_picker);
        phone = findViewById(R.id.phone_number);
        phone1 = findViewById(R.id.phone_number31);
        entry = findViewById(R.id.isStaff);

        //Database Reference
        firebaseDatabase = FirebaseDatabase.getInstance();
        CheckUser = firebaseDatabase.getReference("Users");

        setFocus(phone1,false);

        phone1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                phone.setError(null);
                phone.setErrorEnabled(false);
                if(s.length() == 0){
                    setFocus(phone1,false);
                }

                if(s.length() == 10){
                    setFocus(phone1,true);
                    return;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        findViewById(R.id.sign_back3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        findViewById(R.id.next3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Get No.
                String _getUserEnteredPhone = phone.getEditText().getText().toString().trim();
                final String _phoneNo = "+" + countryCodePicker.getFullNumber() + _getUserEnteredPhone;

                if (_getUserEnteredPhone.isEmpty() || _getUserEnteredPhone.length() < 10) {
                    phone.setError("10 digits number required");
                    phone.requestFocus();
                    setFocus(phone1,false);
                    return;
                }

                CheckUser.orderByChild("phoneNo").equalTo(_phoneNo).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            Dialog();
                        }
                        else{
                            Intent intent = new Intent(getApplicationContext(), signup2.class);

                            if (entry.isChecked()){
                                String AdminphoneNo = "+" + countryCodePicker.getFullNumber() + "9815229895";
                                intent.putExtra("Admin", AdminphoneNo);
                                intent.putExtra("staff", "kitchenenter");
                                intent.putExtra("phone", _phoneNo);
                            }
                            else {
                                intent.putExtra("phone", _phoneNo);
                                intent.putExtra("staff", "false");
                            }
                            // Send to Signup
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }

    // Already User
    private void Dialog() {

        final Dialog dialog = new Dialog(this);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.setContentView(R.layout.signup4_dialog);
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialog.getWindow().setDimAmount(0.6f);

        Login = dialog.findViewById(R.id.Automatic1);
        ChangeNumber = dialog.findViewById(R.id.Manual1);
        HeadingLocButton1 = dialog.findViewById(R.id.HeadingLocButton);
        HeadingLocButton1.setText("User Already Exists");
        Login.setText("Login");
        ChangeNumber.setText("Change Number");

        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(signup1.this,loginuser1.class);
                dialog.dismiss();
                startActivity(intent);
            }
        });

        ChangeNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFocus(phone1,false);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    // True Remove and False Show Keyboard
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

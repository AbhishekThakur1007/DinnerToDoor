package com.example.food.UserSide.TotalItems;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chaos.view.PinView;
import com.example.food.Common.UserDetails_Constructor;
import com.example.food.UserSide.Database;
import com.example.food.UserSide.Order_Constructor;
import com.example.food.R;
import com.example.food.UserSide.ClientMainActivity;
import com.example.food.Common.FullScreenMap;
import com.example.food.UserSide.UserPayment.Payment;
import com.example.food.Common.SessionManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hbb20.CountryCodePicker;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentResultListener;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class FinalOrderDetails extends AppCompatActivity /*implements FullScreenMap.Listener*/{

    String UserID;

    //Reference
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    FirebaseDatabase database;
    DatabaseReference Coordinates,User;

    List<Order_Constructor> carts;
    CartAdapter adapter;
    TextView FinalAmount;
    Button btnPlace;
    ImageButton Back;
    // Total price
    int total = 0;

    // Dialog 1 Confirm Address #button 1
    TextView Mobile, Address;
    ImageView MobileEdit1,AddressEdit1;
    Button Confirm;

    //Get Intent
    String phoneNumber,Amount,CurrentAddress;

    // Send to Full Screen Dialog
    String DbLong,DbLat,codeBySystem1;
    String name,get;
    FragmentResultListener listener;
    BottomSheetDialog bottomSheetDialog;
    FirebaseAuth mAuth;
    PinView pin_view1;
    EditText getPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_order_details);

        //Firebase
        database = FirebaseDatabase.getInstance();
        User = database.getReference("Users");

        recyclerView = (RecyclerView) findViewById(R.id.listCart);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        SessionManager sessionManager1 = new SessionManager(getApplicationContext(),SessionManager.SESSION_USERSESSION);
        HashMap<String,String> ID  =  sessionManager1.getUserDetailFromSession();
        UserID =   ID.get(SessionManager.KEY_USERID);
        Database db = new Database(getApplication());
        UserDetails_Constructor userDetailsConstructor = db.UserDetails(UserID);
        phoneNumber = userDetailsConstructor.getPhoneNo();
        name = userDetailsConstructor.getFullName();
        CurrentAddress = userDetailsConstructor.getAddress().replace("+",",");

        FinalAmount = findViewById(R.id.Total);
        btnPlace = findViewById(R.id.placeOrder);
        Back = findViewById(R.id.finalDetails_back);
        Coordinates = database.getReference("Users").child(UserID).child("Address").child("Default");

        Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               onBackPressed();
            }
        });

        listener = new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                if(result != null){
                    CurrentAddress =  result.getString("Add").replace("+",",");
                }
            }
        };

        UpdateAdapter();

        btnPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (total == 0) {
                    Toast.makeText(FinalOrderDetails.this, "Order is Empty.", Toast.LENGTH_SHORT).show();
                } else {
                    GetGPS();
                }
            }

        });

        /*enableSwipeToDeleteAndUndo();*/
    }

   /* private void enableSwipeToDeleteAndUndo() {
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(this) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

                final int position = viewHolder.getAdapterPosition();
                final Order item = adapter.getData().get(position);

                adapter.removeItem(position);

                Snackbar snackbar = Snackbar
                        .make(coordinatorLayout, "Item was removed from the list.", Snackbar.LENGTH_LONG);
                snackbar.setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        adapter.restoreItem(item, position);
                        recyclerView.scrollToPosition(position);
                    }
                });

                snackbar.setActionTextColor(Color.YELLOW);
                snackbar.show();

            }
        };
     }*/

    private void UpdateAdapter() {
        carts = new ArrayList<>();
        carts = new Database(FinalOrderDetails.this).getCarts();
        total = 0;
        if(carts.size() != 0){
            TotalAmount();
            adapter = new CartAdapter(carts,this);
            recyclerView.setAdapter(adapter);
        }
    }

    public void TotalAmount() {
        carts = new ArrayList<>();
        carts = new Database(FinalOrderDetails.this).getCarts();
        total = 0;
        for(int i =0; i < carts.size();i++){
            total+= Integer.parseInt(carts.get(i).getPrice())*Integer.parseInt(carts.get(i).getQuantity());
        }
        Locale locale = new Locale("en","in");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
        Amount = fmt.toString();
        FinalAmount.setText(fmt.format(total));
    }

    @Override
    protected void onResume() {
        super.onResume();
        recyclerView.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(@NonNull View view) {

            }

            @Override
            public void onChildViewDetachedFromWindow(@NonNull View view) {
                TotalAmount();
            }
        });
    }

    // After Place Order button #1 and #2
    private void GetGPS() {

        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(FinalOrderDetails.this,R.style.BottomSheetDialog);
        View bottom = LayoutInflater.from(getApplicationContext()).inflate(
                R.layout.user_location,(LinearLayout) findViewById(R.id.bottomContainer));

        //At Home
        bottom.findViewById(R.id.allowLocation).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
                showDialog();
            }
        });

        //At Current Location
        bottom.findViewById(R.id.locationManual).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
                FullScreenDialog(false);
            }
        });

        bottomSheetDialog.setContentView(bottom);
        bottomSheetDialog.show();
    }

    // Button #1
    private void showDialog() {
        final Dialog dialog = new Dialog(FinalOrderDetails.this);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        dialog.setContentView(R.layout.confirm_details);
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialog.getWindow().setDimAmount(0.6f);

        Mobile = dialog.findViewById(R.id.Mobile);
        MobileEdit1 = dialog.findViewById(R.id.MobileEdit);

        Address = dialog.findViewById(R.id.Address);
        AddressEdit1 = dialog.findViewById(R.id.AddressEdit);

        Confirm = dialog.findViewById(R.id.pataConfirm);

        String dis = phoneNumber.substring(3);
        Address.setText(CurrentAddress);
        Mobile.setText(dis);

        AddressEdit1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FullScreenDialog(true);
            }
        });

        MobileEdit1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                DialogPhoneChange();
            }
        });

        Confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    database.getReference("Users").child(UserID).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(Objects.requireNonNull(snapshot.child("phoneNo").getValue()).toString().equals(phoneNumber) &&
                                    Objects.requireNonNull(snapshot.child("Address").child("Default").child("Address").getValue()).toString().replace("+",",").equals(CurrentAddress)){
                                     dialog.dismiss();
                                     Intent intent = new Intent(FinalOrderDetails.this, Payment.class);
                                     startActivity(intent);
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
            }
        });
        dialog.show();
    }

    // Button #2 (Dialog Fragment)
    private void FullScreenDialog(boolean State) {

        if(State){
            Coordinates.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot1) {
                    //Sending to Dialog
                    if(snapshot1.exists()){
                        DbLat = Objects.requireNonNull(snapshot1.child("Coordinates").child("Latitude").getValue()).toString();
                        DbLong = Objects.requireNonNull(snapshot1.child("Coordinates").child("Longitude").getValue()).toString();
                        CurrentAddress = Objects.requireNonNull(snapshot1.child("Address").getValue()).toString();
                        //Send Data to Fragment
                        FullScreenMap dialogFragment = new FullScreenMap();
                        dialogFragment.FullScreenMap(DbLat,DbLong,CurrentAddress,UserID);
                        dialogFragment.show(getSupportFragmentManager(),null);
                        getSupportFragmentManager().setFragmentResultListener("Send",FinalOrderDetails.this,listener);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }else{
            //Send Data to Fragment
            FullScreenMap dialogFragment = new FullScreenMap();
            dialogFragment.FullScreenMap(null,null,null,UserID);
            dialogFragment.show(getSupportFragmentManager(),null);
            getSupportFragmentManager().setFragmentResultListener("Send",FinalOrderDetails.this,listener);
        }
    }

    private void DialogPhoneChange() {

        bottomSheetDialog = new BottomSheetDialog(this,R.style.BottomSheetDialog);
        final View bottom = LayoutInflater.from(getApplication()).inflate(
                R.layout.bottom_phone_change,null);

        final CountryCodePicker countryCodePicker = bottom.findViewById(R.id.code_picker_Change);
        getPhone = bottom.findViewById(R.id.PhoneChange);
        Button cancel = bottom.findViewById(R.id.OTPCancel);
        Button Send = bottom.findViewById(R.id.DialogSave);
        //Before OTP
        final RelativeLayout  Nohai = bottom.findViewById(R.id.HideAfterOtp);
        //Type OTP
        final RelativeLayout Nohai1 = bottom.findViewById(R.id.typeOTP);
        //Load to verify
        pin_view1 = bottom.findViewById(R.id.pin_view1);
        Button  CheckOTP = bottom.findViewById(R.id.CheckOTP);
        //Heading
        final TextView Heading = bottom.findViewById(R.id.changeNo);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
            }
        });

       /* setFocusNew(getPhone);*/

        getPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() == 10){
                    Toast.makeText(getApplicationContext(),"10 digits completed",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        getPhone.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP){
                    getPhone.clearFocus();
                    bottomSheetDialog.dismiss();
                    return true;
                }
                return false;
            }
        });

        Send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String typedNo = getPhone.getText().toString();
                get = "+" + countryCodePicker.getFullNumber() + typedNo;
                int length = typedNo.length();
                if(length < 10){
                    getPhone.setError("10 digits required");
                    setFocusNew(getPhone);
                    return;
                }
                if(phoneNumber.equals(get)){
                    Toast.makeText(getApplicationContext(),"Same Number",Toast.LENGTH_SHORT).show();
                }else{
                    bottomSheetDialog.setCancelable(false);
                    Heading.setText("Type OTP");
                    Toast.makeText(getApplicationContext(),"OTP Send.",Toast.LENGTH_SHORT).show();
                    mAuth = FirebaseAuth.getInstance();
                    sendVerificationCodeToUser(get);
                    Nohai.setVisibility(View.GONE);
                    Nohai1.setVisibility(View.VISIBLE);
                }
            }
        });

        CheckOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savedPinView();
            }
        });
        bottomSheetDialog.setContentView(bottom);
        bottomSheetDialog.show();
    }

    private void setFocusNew(EditText getPhone) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        getPhone.requestFocus();
        getPhone.setCursorVisible(true);
        imm.showSoftInput(getPhone,InputMethodManager.SHOW_IMPLICIT);
    }

    private void sendVerificationCodeToUser(String get) {

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(get)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();

        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks =
            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                @Override
                public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                    super.onCodeSent(s, forceResendingToken);
                    codeBySystem1 = s;
                }

                @Override
                public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                    String code = phoneAuthCredential.getSmsCode();
                    if (code != null){
                        pin_view1.setText(code);
                        verifyCode(code);
                    }
                }

                @Override
                public void onVerificationFailed(@NonNull FirebaseException e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();

                }
            };

    private void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codeBySystem1, code);
        signInWithPhoneAuthCredential(credential);
    }

    // Tell me where to go
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                               Toast.makeText(getApplicationContext(), "Verification Updated.", Toast.LENGTH_SHORT).show();
                                Bundle bundle = new Bundle();
                                bundle.putString("Contact",get);
                                Database db = new Database(getApplicationContext());
                                long Check = db.UpdateUserData(UserID,3,bundle);
                                if(Check > 0){
                                    FirebaseDatabase.getInstance().getReference("Users").child(UserID).child("phoneNo").
                                          setValue(get).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            phoneNumber = get;
                                            Toast.makeText(getApplicationContext(),"Number Updated",Toast.LENGTH_SHORT).show();
                                            bottomSheetDialog.dismiss();
                                        }
                                    });
                                }
                        } else {
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(getApplicationContext(), "Verification Not Completed! Try Again.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    //Call this method to check
    public void savedPinView() {
        String code = Objects.requireNonNull(pin_view1.getText()).toString();
        if (!code.isEmpty()) {
            verifyCode(code);
        }
    }

    /*// Getting data from dialog Fragment
    @Override
    public void attach(String latitude, String Longitude, String Address) {
         DAddress = Address;
         DLatitude = latitude;
         DLongitude = Longitude;
        Toast.makeText(FinalOrderDetails.this,Longitude,Toast.LENGTH_SHORT).show();
    }*/

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, ClientMainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

}
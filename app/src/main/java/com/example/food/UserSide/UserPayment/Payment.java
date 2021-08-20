package com.example.food.UserSide.UserPayment;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.food.Common.UserDetails_Constructor;
import com.example.food.UserSide.Database;
import com.example.food.UserSide.Order_Constructor;
import com.example.food.R;
import com.example.food.UserSide.ClientMainActivity;
import com.example.food.Common.SessionManager;
import com.example.food.UserSide.TotalItems.FinalOrderDetails;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class Payment extends AppCompatActivity {

    Button GOOglePay,COD,NextGooglePay,PhonePe,NextPhonePay;
    DatabaseReference Order_Send,Pending;
    FirebaseDatabase database;
    String UserID,phoneNumber,name,address,Amount,OrderNo;
    //GPay Sheet
    /*private static final int LOAD_PAYMENT_DATA_REQUEST_CODE = 100;
    private PaymentsClient paymentsClient;*/

    public static final String GPAY_PACKAGE_NAME = "com.google.android.apps.nbu.paisa.user";
    Animation slide_down, slide_up,ArrowRotate,ArrowNormal;
    ImageView Arrow,Arrow2;
    boolean isFABOpen,is;
    RelativeLayout relativeLayout,DetailsData11,barCODLay,LayPhonePay;
    TextInputEditText UID,PhoneUID;
    TextInputLayout LayUID,LayPhonePe;
    String UID_String;
    String approvalRefNo;
    Uri uri;
    ImageButton back;
    public static String status;
    List<Order_Constructor> cart = new ArrayList<>();
    List<Coordinates> Coordinates = new ArrayList<>();
    protected Database db = new Database(Payment.this);
    protected Database Directions = new Database(Payment.this);
    int total = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        GOOglePay= findViewById(R.id.Google_Button);

        PhonePe= findViewById(R.id.PhonePe_Button);
        NextPhonePay= findViewById(R.id.NextPhonePay);
        PhoneUID= findViewById(R.id.UPI_IDPhonePe);
        LayPhonePe= findViewById(R.id.PhonePeLay);
        DetailsData11= findViewById(R.id.DetailsData1);
        Arrow2= findViewById(R.id.Teer1);

        LayPhonePay = findViewById(R.id.LayPhonePay);
        barCODLay = findViewById(R.id.barCOD);

        database = FirebaseDatabase.getInstance();
        Order_Send = database.getReference().child("Orders");
        Pending = database.getReference().child("New");

        Arrow = findViewById(R.id.Teer);
        relativeLayout = findViewById(R.id.DetailsData);
        NextGooglePay = findViewById(R.id.NextGooglePay);
        back = findViewById(R.id.PayBackButton_back);
        LayUID = findViewById(R.id.GpayLay);
        UID = findViewById(R.id.UPI_ID);

        SessionManager sessionManager1 = new SessionManager(getApplicationContext(),SessionManager.SESSION_USERSESSION);
        HashMap<String,String> ID  =  sessionManager1.getUserDetailFromSession();
        UserID =   ID.get(SessionManager.KEY_USERID);
        Database db = new Database(getApplication());
        UserDetails_Constructor userDetailsConstructor = db.UserDetails(UserID);
        phoneNumber = userDetailsConstructor.getPhoneNo().substring(3);
        name = userDetailsConstructor.getFullName();
        address = userDetailsConstructor.getAddress().replace("+",",");
        cart = db.getCarts();

        for(int i =0; i <cart.size();i++){
                total+= Integer.parseInt(cart.get(i).getTotal())*Integer.parseInt(cart.get(i).getQuantity());}
        Amount = String.valueOf(total);

        PhonePe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*requestPayment(v);*/
                if(!is){
                    showFABMenu1();
                }else{
                    closeFABMenu1();
                }
            }
        });

        NextPhonePay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UID_String = Objects.requireNonNull(UID.getText()).toString();
                if(!Check(UID_String)){
                }
            }

            private boolean Check(String uid_string) {
                if(uid_string.isEmpty()){
                    LayPhonePe.setError("Please Enter PhonePe ID");
                    LayPhonePe.setErrorEnabled(true);
                    return false;
                }else{
                    LayPhonePe.setErrorEnabled(false);
                    LayPhonePe.setError(null);
                    return true;
                }
            }
        });

        COD = findViewById(R.id.COD_Button);
        COD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Payment.this,"Thank you. Order Placed",Toast.LENGTH_SHORT).show();
                moveFirebaseRecord("Cash On Delivery");
                Intent i = new Intent(Payment.this, ClientMainActivity.class);
                i.putExtra("Payment_Mode","Cash On Delivery");
                startActivity(i);
                finish();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Payment.this, FinalOrderDetails.class);
                startActivity(i);
            }
        });

        /*paymentsClient = PaymentsUtil.createPaymentsClient(this);*/

        GOOglePay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*requestPayment(v);*/
                if(!isFABOpen){
                    showFABMenu();
                }else{
                    closeFABMenu();
                }
            }
        });


        NextGooglePay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UID_String = UID.getText().toString();
                if(!Check(UID_String)){
                    return;
                }
                uri = getUPIPaymentUri("Indo-Chinese Food",UID_String,"Complete transaction to complete Order.",Amount);
                payWithGpay(GPAY_PACKAGE_NAME);
            }

            private boolean Check(String uid_string) {
                if(uid_string.isEmpty()){
                    LayUID.setError("Please Enter Google Pay ID");
                    LayUID.setErrorEnabled(true);
                    return false;
                }else{
                    LayUID.setErrorEnabled(false);
                    LayUID.setError(null);
                   return true;
                }
            }
        });

        //Arrow Animation
        Arrow = findViewById(R.id.Teer);
        //Animation Hook
        slide_down = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.slide_down);
        slide_up = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.slide_up);
        ArrowRotate = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.arrow_rotate);
        ArrowNormal = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.arrow_normal);
}

    public void moveFirebaseRecord(final String Mode) {
        Coordinates = Directions.GetCoordinates();
        OrderNo = String.valueOf(System.currentTimeMillis());
        /*FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                String token = task.getResult();
                Database db  = new Database(getApplicationContext());
                String SavedToken = db.GetToken(UserID);
                if(!SavedToken.equals(token)){
                    Bundle bundle = new Bundle();
                    bundle.putString("token",token);
                    db.UpdateUserData(UserID,5,bundle);
                }
                Toast.makeText(Payment.this,token,Toast.LENGTH_SHORT).show();*/
                final Request request = new Request( phoneNumber, name, address, Amount , Mode , "Pending",cart, Coordinates,null);
                 FirebaseDatabase.getInstance().getReference("Users").child(UserID).child("Token").setValue(null);
                    Pending.child(OrderNo).setValue(request).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isComplete()){
                            Toast.makeText(Payment.this,"Uploaded",Toast.LENGTH_SHORT).show();
                            new Database(getBaseContext()).clearCart();
                        }else{
                            Toast.makeText(Payment.this,"Something went wrong.",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
   /*         }
        });*/
    }

    //GPay Sheet
    /*private void requestPayment(View v) {
        Optional<JSONObject> paymentDataRequestJson = PaymentsUtil.getPaymentDataRequest(AMO);
        if (!paymentDataRequestJson.isPresent()) {
            return;
        }
        PaymentDataRequest request =
                PaymentDataRequest.fromJson(paymentDataRequestJson.get().toString());

        if (request != null) {
            AutoResolveHelper.resolveTask(
                    paymentsClient.loadPaymentData(request),
                    this, LOAD_PAYMENT_DATA_REQUEST_CODE);
        }
    }*/

    private static Uri getUPIPaymentUri(String name, String upiId, String note , String amount){

        return new Uri.Builder()
                .scheme("upi")
                .authority("pay")
                .appendQueryParameter("pa",upiId)
                .appendQueryParameter("pn",name)
                .appendQueryParameter("tn",note)
                .appendQueryParameter("am",amount)
                .appendQueryParameter("cu","INR")
                .build();
    }

    private void payWithGpay(String packageName){
        if(isAppInstalled(this,packageName)){
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(uri);
            intent.setPackage(packageName);
            startActivityForResult(intent,0);
        }else {
            Toast.makeText(Payment.this,"Google pay is not installed.",Toast.LENGTH_SHORT).show();
        }
    }

    public static boolean isAppInstalled(Payment context, String packageName){
        try{
            context.getPackageManager().getApplicationInfo(packageName,0);
            return true;
        }catch (PackageManager.NameNotFoundException e){
            return false;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Google Sheet Check
        /*switch (requestCode) {
            // value passed in AutoResolveHelper
            case LOAD_PAYMENT_DATA_REQUEST_CODE:
                switch (resultCode) {

                    case Activity.RESULT_OK:
                        PaymentData paymentData = PaymentData.getFromIntent(data);
                        Toast.makeText(this,"Success",Toast.LENGTH_SHORT).show();
                        break;

                    case Activity.RESULT_CANCELED:
                        // The user cancelled the payment attempt
                        Toast.makeText(this,"Payment Cancelled",Toast.LENGTH_SHORT).show();
                        break;

                    case AutoResolveHelper.RESULT_ERROR:
                        Status status = AutoResolveHelper.getStatusFromIntent(data);
                        Toast.makeText(this,"Error in Transaction",Toast.LENGTH_SHORT).show();
                        break;
                }

                // Re-enables the Google Pay payment button.
                GOOglePay.setClickable(true);
        }*/

        if (data != null) {
            status = data.getStringExtra("Status").toLowerCase();
            approvalRefNo = data.getStringExtra("txnRef");
        }

        if ((RESULT_OK == resultCode) && status.equals("success")) {
            Toast.makeText(getApplication(), "Transaction successful. ", Toast.LENGTH_SHORT).show();
            moveFirebaseRecord("Online");
            Intent i = new Intent(Payment.this, ClientMainActivity.class);
            i.putExtra("Payment_Mode","Done");
            i.putExtra("Extra","CartFragment");
            startActivity(i);
            finish();
        }

        else{
            Toast.makeText(getApplicationContext(), "Transaction cancelled or failed please try again.", Toast.LENGTH_SHORT).show();
        }

    }

    public void showFABMenu1(){
        if(isFABOpen){
            closeFABMenu();
        }
        is = true;
        DetailsData11.setVisibility(View.VISIBLE);
        DetailsData11.startAnimation(slide_down);
        barCODLay.startAnimation(slide_down);
        Arrow2.startAnimation(ArrowRotate);
    }

    public void closeFABMenu1() {

            is = false;
            DetailsData11.startAnimation(slide_up);
            DetailsData11.setVisibility(View.GONE);
            barCODLay.startAnimation(AnimationUtils.loadAnimation(this,R.anim.top_send_up));
            Arrow2.startAnimation(ArrowNormal);
    }

    //Google pay Option
    public void showFABMenu() {
       /* if(is){
            closeFABMenu1();
        }*/
            isFABOpen = true;
            relativeLayout.setVisibility(View.VISIBLE);
            relativeLayout.startAnimation(slide_down);
            slide_down.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    /*LayPhonePay.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.top_send_down));
                    barCODLay.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.top_send_down));*/
                    Arrow.startAnimation(ArrowRotate);
                }

                @Override
                public void onAnimationEnd(Animation animation) {

                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

    }

    public void closeFABMenu() {
             isFABOpen = false;
             relativeLayout.startAnimation(slide_up);
             slide_up.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    /*LayPhonePay.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.top_send_up));
                    barCODLay.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.top_send_up));*/
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    relativeLayout.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            Arrow.startAnimation(ArrowNormal);
    }

}
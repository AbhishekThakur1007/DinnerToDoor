package com.example.food.Common;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.example.food.R;

public class InternetConnection{

    Activity activity;
    Dialog dialog;


    public InternetConnection(Activity mcontext){
        this.activity = mcontext;
    }

    public boolean CheckInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo network = connectivityManager.getActiveNetworkInfo();
        if (network != null && network.isConnected() && network.isAvailable()){
            /*Toast.makeText(activity, "Connected", Toast.LENGTH_SHORT).show();*/
            if(dialog != null && dialog.isShowing()){
                dialog.dismiss();
            }
        return true;
        }else{
            if(dialog == null){
                dialog = new Dialog(activity);
                dialog.setContentView(R.layout.check_internet_dialog);
                dialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT,WindowManager.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Dialog;
                final Button tryAgain = dialog.findViewById(R.id.NoInternetButton);
                tryAgain.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CheckInternet();
                    }
                });
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
            }
            return false;
        }
    }

}

package com.example.food.UserSide.UserCartFragment;

import android.view.View;
import android.widget.TextView;

import com.example.food.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class product_Adapter extends RecyclerView.ViewHolder {

    TextView userFood,userQuantity;

    public product_Adapter(@NonNull View itemView) {
        super(itemView);

        userFood = itemView.findViewById(R.id.PakwaanUser);
        userQuantity = itemView.findViewById(R.id.Quantity_item);
    }
}

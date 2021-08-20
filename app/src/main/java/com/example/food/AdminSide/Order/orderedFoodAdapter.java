package com.example.food.AdminSide.Order;

import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.food.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class orderedFoodAdapter extends RecyclerView.ViewHolder {
    public TextView Dish_name;
    public TextView Dish_Quantity;
    public TextView Dish_Size;
    public RelativeLayout LaySizeOrdered;

    public orderedFoodAdapter(@NonNull View itemView) {
        super(itemView);
        //Food Details
        Dish_name = itemView.findViewById(R.id.Pakwaan);
        Dish_Quantity = itemView.findViewById(R.id.Quantity_item);
        Dish_Size = itemView.findViewById(R.id.Dish_Size);
        LaySizeOrdered = itemView.findViewById(R.id.LaySizeOrdered);
    }
}

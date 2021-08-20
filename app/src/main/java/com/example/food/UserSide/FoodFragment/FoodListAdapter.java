package com.example.food.UserSide.FoodFragment;

import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.food.AdminSide.Interface.ItemClickListener;
import com.example.food.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class FoodListAdapter extends RecyclerView.ViewHolder implements View.OnClickListener{

    private ItemClickListener itemClickListener2;
    public TextView OptionsName1,listHeading1,Price;
    public CheckBox halfRb1;

    public FoodListAdapter(@NonNull View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);
        OptionsName1 = itemView.findViewById(R.id.OptionsName);
        halfRb1 = itemView.findViewById(R.id.halfRb);
        listHeading1 = itemView.findViewById(R.id.listHeading);
        Price = itemView.findViewById(R.id.priceFinal);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener2 = itemClickListener;
    }

    @Override
    public void onClick(View v) {
        itemClickListener2.onClick(v,getAdapterPosition(),false);
    }
}

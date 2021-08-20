package com.example.food.Common;

import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.food.AdminSide.Interface.ItemClickListener;
import com.example.food.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class FoodAdapter extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView foodName;
    public ImageView foodPhoto;
    public ImageButton delete1, edit1;
    public View ViewCustom;
    public ImageButton Fav,Favourites;
    public Button Availability;

    private ItemClickListener itemClickListener;

    public FoodAdapter(@NonNull View itemView) {
        super(itemView);

        ViewCustom = itemView;

        //Customer Side
        foodPhoto = itemView.findViewById(R.id.foodPhoto);
        foodName =  itemView.findViewById(R.id.foodName);
        Favourites =  itemView.findViewById(R.id.Favourites);

        //Admin Side

        Availability = itemView.findViewById(R.id.Availability);
        Fav = itemView.findViewById(R.id.Favourites1);
        delete1 = itemView.findViewById(R.id.delete_food);
        edit1 = itemView.findViewById(R.id.edit_food);

        itemView.setOnClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {

        itemClickListener.onClick(v,getAdapterPosition(),false);
    }
}

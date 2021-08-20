package com.example.food.AdminSide.StaffAdminCommon;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.food.AdminSide.Interface.ItemClickListener;
import com.example.food.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class UserOrderDetailsAdapter extends RecyclerView.ViewHolder implements View.OnClickListener {

    public ImageButton CallUser;
    public TextView name_Staff,Staff_Status_Order,UserName;
    public TextView Mode;
    public TextView COD_order,UserNumber,AddressUser;
    public ImageView green,red;

    public ItemClickListener itemClickListener;

    public UserOrderDetailsAdapter(@NonNull View itemView) {
        super(itemView);
        Staff_Status_Order = itemView.findViewById(R.id.StatusOrder);
        name_Staff = itemView.findViewById(R.id.NameStaff);
        UserName = itemView.findViewById(R.id.Order_Person);
        COD_order = itemView.findViewById(R.id.Amount);
        Mode = itemView.findViewById(R.id.Order_phone);
        CallUser = itemView.findViewById(R.id.CallUser);
        UserNumber = itemView.findViewById(R.id.Contact);
        AddressUser = itemView.findViewById(R.id.AddressUser);
        green = itemView.findViewById(R.id.greenTick);
        red = itemView.findViewById(R.id.redTick);
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

package com.example.food.Common;

import android.view.View;
import android.widget.TextView;

import com.example.food.AdminSide.Interface.ItemClickListener;
import com.example.food.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TopCategoryAdapter extends RecyclerView.ViewHolder implements View.OnClickListener{

    public de.hdodenhof.circleimageview.CircleImageView Image;
    public TextView Name;
    private ItemClickListener itemClickListener1;

    public TopCategoryAdapter(@NonNull View itemView1) {
        super(itemView1);

        Image= itemView1.findViewById(R.id.TopImage);
        Name= itemView1.findViewById(R.id.TopName);
        itemView.setOnClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener1 = itemClickListener;
    }

    @Override
    public void onClick(View v) {
        itemClickListener1.onClick(v,getAdapterPosition(),false);
    }
}

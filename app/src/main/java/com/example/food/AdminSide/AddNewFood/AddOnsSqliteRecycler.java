package com.example.food.AdminSide.AddNewFood;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.food.AdminSide.SQlDataAdminInformation;
import com.example.food.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AddOnsSqliteRecycler extends RecyclerView.Adapter<AddOnsSqliteRecycler.SqlViewHolder> {

    Context mContext;
    ArrayList<SizeAvailable> list;
    Activity Activity;

    public AddOnsSqliteRecycler(Context context, Activity activity, ArrayList<SizeAvailable> list1){
        this.mContext = context;
        this.list = list1;
        this.Activity = activity;
    }

    public static class SqlViewHolder extends RecyclerView.ViewHolder{
        TextView Size,Quan,Price,DiscountPrice;
        View view;
        ImageView DeleteFromRecycler;
        public SqlViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            Size  = itemView.findViewById(R.id.UnitName);
            Quan = itemView.findViewById(R.id.NameQuan);
            Price = itemView.findViewById(R.id.AmountName);
            DiscountPrice = itemView.findViewById(R.id.DiscountPrice);
            DeleteFromRecycler = itemView.findViewById(R.id.DeleteFromRecycler);
        }
    }

    @NonNull
    @Override
    public AddOnsSqliteRecycler.SqlViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.sqlrecycler,parent,false);
        return new SqlViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final AddOnsSqliteRecycler.SqlViewHolder holder, final int position) {

        holder.Quan.setText(list.get(position).getSizeCategory());
        holder.Size.setText(list.get(position).getQuantity());
        holder.Price.setText(list.get(position).getPrice());
        if(list.get(position).getDiscount() != null && !list.get(position).getDiscount().isEmpty()){
            holder.DiscountPrice.setText(list.get(position).getDiscount());
        }
        else{
            holder.DiscountPrice.setText("0");
        }

        holder.DeleteFromRecycler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext,"Clicked",Toast.LENGTH_SHORT).show();
                notifyItemRemoved(position);
                notifyItemRangeRemoved(position,list.size());
                SQlDataAdminInformation s = new SQlDataAdminInformation(mContext);
                s.DeleteSize(false,list.get(position).getPrice());
                list.remove(position);
                Update();
            }
        });
    }

    public int Update() {
         return list.size();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

}

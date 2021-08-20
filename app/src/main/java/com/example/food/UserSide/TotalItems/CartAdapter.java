package com.example.food.UserSide.TotalItems;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.food.AdminSide.Interface.ItemClickListener;
import com.example.food.UserSide.Database;
import com.example.food.UserSide.Order_Constructor;
import com.example.food.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

class CartViewHOLDER extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView text_cart,txt_price,Quantity,PlateDis1;
    public ImageView DishPhoto;
    public ImageButton ListDecrease1,ListIncrease1;
    ImageView Delete;
    private ItemClickListener itemClickListener;

    public CartViewHOLDER(@NonNull View itemView) {
        super(itemView);

        DishPhoto = (ImageView) itemView.findViewById(R.id.cart_item_Count);
        text_cart = (TextView) itemView.findViewById(R.id.cart_item_name);
        txt_price = (TextView) itemView.findViewById(R.id.cart_item_Price);
        Quantity = (TextView) itemView.findViewById(R.id.ListItemCount);
        ListDecrease1 = (ImageButton) itemView.findViewById(R.id.ListDecrease);
        ListIncrease1 = (ImageButton) itemView.findViewById(R.id.ListIncrease);
        Delete = (ImageView) itemView.findViewById(R.id.DeleteFromCart);
        PlateDis1 = (TextView) itemView.findViewById(R.id.PlateDis);
    }

    @Override
    public void onClick(View v) {

    }
}

public class CartAdapter extends RecyclerView.Adapter<CartViewHOLDER>{

    private final List<Order_Constructor> lisData;
    int Number,Price,multiply;
    boolean single;
    private final com.example.food.UserSide.TotalItems.FinalOrderDetails FinalOrderDetails;

    public CartAdapter(List<Order_Constructor> lisData, FinalOrderDetails finalOrderDetails) {
        this.lisData = lisData;
        this.FinalOrderDetails = finalOrderDetails;
    }

    @NonNull
    @Override
    public CartViewHOLDER onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(FinalOrderDetails);
        View itemview = inflater.inflate(R.layout.cart_layout,parent,false);
        return  new CartViewHOLDER(itemview);
    }

    @Override
    public void onBindViewHolder(@NonNull final CartViewHOLDER holder, final int position) {

         FirebaseDatabase.getInstance().getReference("Foods").child(lisData.get(position).getProductId()).child("image").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        Picasso.get().load(Objects.requireNonNull(snapshot.getValue()).toString()).into(holder.DishPhoto);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        //Name
        holder.text_cart.setText(lisData.get(position).getProductName());
        //Price
        holder.txt_price.setText(lisData.get(position).getTotal());
        // Quantity
        holder.Quantity.setText(lisData.get(position).getQuantity());

        if(lisData.get(position).getPlate() != null){
            holder.PlateDis1.setVisibility(View.VISIBLE);
            //Plate
            holder.PlateDis1.setText(lisData.get(position).getPlate());
        }

        holder.Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Database db = new Database(FinalOrderDetails);
                Order_Constructor orderConstructor2;
                // Delete from SQLite
                    if(lisData.get(position).getPlate() != null){
                        orderConstructor2 = new Order_Constructor(null,lisData.get(position).getProductName(),null,null,null,lisData.get(position).getPlate());
                        single = true;
                    }else{
                        orderConstructor2 = new Order_Constructor(String.valueOf(lisData.get(position).getProductId()),null,null,null,null,null);
                        single = false;
                    }
                    //From SQLite
                    long Check = db.DeleteItem(orderConstructor2,single);
                    if(Check > 0){
                        notifyItemRemoved(position);
                        // Change  the Adapter list count
                        notifyItemRangeChanged(position,lisData.size());
                        //Delete list
                        lisData.remove(position);
                        notifyDataSetChanged();
                    }
            }
        });

        holder.ListDecrease1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Number = Integer.parseInt(holder.Quantity.getText().toString());
                Price =  Integer.parseInt(lisData.get(position).getPrice());
                if(Number == 1){
                    Toast.makeText(FinalOrderDetails,String.valueOf(Number),Toast.LENGTH_SHORT).show();
                    Database db = new Database(FinalOrderDetails);
                    Order_Constructor orderConstructor2;
                    if(lisData.size() != 0){
                        if(lisData.get(position).getPlate() != null){
                            orderConstructor2 = new Order_Constructor(null,lisData.get(position).getProductName(),null,null,null,lisData.get(position).getPlate());
                            single = true;
                        }else{
                            orderConstructor2 = new Order_Constructor(String.valueOf(lisData.get(position).getProductId()),null,null,null,null,null);
                            single = false;
                        }
                        // Change  the Adapter list count
                        long Check = db.DeleteItem(orderConstructor2,single);
                        if(Check > 0){
                            notifyItemRemoved(position);
                            // Change  the Adapter list count
                            notifyItemRangeChanged(position,lisData.size());
                            //Delete list
                            lisData.remove(position);
                            notifyDataSetChanged();
                        }
                    }
                }else{
                    Number -= 1;
                    multiply = Price*Number;
                    updateValue(Number,position,multiply);
                    holder.Quantity.setText(String.valueOf(Number));
                    holder.txt_price.setText(String.valueOf(multiply));
                }
            }
        });

        holder.ListIncrease1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Number = Integer.parseInt(holder.Quantity.getText().toString());
                Price =  Integer.parseInt(lisData.get(position).getPrice());
                if(Number == 10){
                    Toast.makeText(FinalOrderDetails,"Not More than 10",Toast.LENGTH_SHORT).show();
                }else {
                    Number += 1;
                    multiply = Price*Number;
                    updateValue(Number,position,multiply);
                    holder.Quantity.setText(String.valueOf(Number));
                    holder.txt_price.setText(String.valueOf(multiply));

                }
            }
        });
    }

    //Update in SQLite
    private void updateValue(int number, int position,int multiply ) {

        Database db3 = new Database(FinalOrderDetails);
        Order_Constructor orderConstructor;
        if(lisData.get(position).getPlate() != null){ //Multiple Option Plate
            orderConstructor = new Order_Constructor(lisData.get(position).getProductId(),null,String.valueOf(number),null,String.valueOf(multiply),lisData.get(position).getPlate());
            db3.EditItem(orderConstructor);
        }else{ // Single Option
            orderConstructor = new Order_Constructor(lisData.get(position).getProductId(),null,String.valueOf(number),null,String.valueOf(multiply),null);
            db3.SingleOption(orderConstructor);
        }
        FinalOrderDetails.TotalAmount();
    }

    @Override
    public int getItemCount() {
        return lisData.size();
    }

}

package com.example.food.AdminSide.Order;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.food.AdminSide.StaffAdminCommon.UserName_Address;
import com.example.food.AdminSide.StaffAdminCommon.pickupList;
import com.example.food.R;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PickedOrderAdapter extends RecyclerView.Adapter<PickedOrderAdapter.AdapterPickup> {

    List<UserName_Address> list;
    Context context;

    public PickedOrderAdapter(Context mContext, List<UserName_Address> list1){
        this.context = mContext;
        this.list = list1;
    }

    public static class AdapterPickup extends RecyclerView.ViewHolder {

        public TextView name,Address,USerPhone;
        public RadioGroup Pickup;
        public RadioButton radioButton;

        public AdapterPickup(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.USerName);
            Address = itemView.findViewById(R.id.USerAdd);
            USerPhone = itemView.findViewById(R.id.USerPhone);
            Pickup = itemView.findViewById(R.id.DeliveryDetailSpinner);
            radioButton = itemView.findViewById(Pickup.getCheckedRadioButtonId());
        }
    }

    @NonNull
    @Override
    public AdapterPickup onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.pickupaddress,parent,false);
        return new AdapterPickup(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final AdapterPickup adapterPickup, final int position) {

        adapterPickup.name.setText(list.get(position).getName());
        adapterPickup.Address.setText(list.get(position).getAddress().replace("+",","));
        adapterPickup.USerPhone.setText(list.get(position).getAdminPhone());

        adapterPickup.Pickup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.yesCancel){
                    pickupList pickupList = new pickupList(context);
                    long i = pickupList.Delete(list.get(position).getAdminPhone());
                    if (i > 0) {
                        String ID = list.get(position).getOrderId();
                        FirebaseDatabase.getInstance().getReference("New").child(ID).child("Delivery").setValue(null);
                        notifyItemRemoved(position);
                        notifyItemRangeRemoved(position, 1);
                        list.remove(position);
                        /*retailerDashboard.GetOrderID();*/
                        int Count = pickupList.getCount();
                        if(Count == 0){
                            /*bottomSheetDialog.dismiss();*/
                        }
                        /*Toast.makeText(context, "Removed", Toast.LENGTH_SHORT).show();*/
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

}

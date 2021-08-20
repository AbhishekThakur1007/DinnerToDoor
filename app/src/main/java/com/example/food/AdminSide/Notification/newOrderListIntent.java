package com.example.food.AdminSide.Notification;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.food.AdminSide.StaffAdminCommon.OrderDetailsConstructor;
import com.example.food.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class newOrderListIntent extends DialogFragment {

    Context Context;
    View view;
    RecyclerView recyclerView;
    DatabaseReference AddOrder,New;
    Count count;
    ImageButton BackNewOrder;
    FirebaseRecyclerAdapter<OrderDetailsConstructor,NewOrders> adapter;

    public newOrderListIntent(Context context){
        this.Context = context;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Activity activity = (Activity) context;
        try {
            count = (Count) activity;
        }catch (ClassCastException e){
            throw new ClassCastException((activity).toString());
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, android.R.style.Widget_Holo_Light);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.accept_ot_not, container, false);

        recyclerView = view.findViewById(R.id.NewOrderList);
        BackNewOrder = view.findViewById(R.id.BackNewOrder);
        recyclerView.setLayoutManager(new LinearLayoutManager(Context));
        AddOrder = FirebaseDatabase.getInstance().getReference().child("Order");
        New = FirebaseDatabase.getInstance().getReference().child("New");
        DisplayOrders(New);

        BackNewOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        return view;
    }

    public void DisplayOrders(Query query) {

        FirebaseRecyclerOptions<OrderDetailsConstructor> options =
                new FirebaseRecyclerOptions.Builder<OrderDetailsConstructor>()
                        .setQuery(query, OrderDetailsConstructor.class).build();

        adapter = new FirebaseRecyclerAdapter<OrderDetailsConstructor, NewOrders>(options) {
            @Override
            protected void onBindViewHolder(@NonNull NewOrders newOrders, final int i, @NonNull OrderDetailsConstructor orderDetailsConstructor) {
                newOrders.OrderID.setText(adapter.getRef(i).getKey());
                newOrders.Name.setText(orderDetailsConstructor.getName());
                newOrders.Address.setText(orderDetailsConstructor.getAddress());
                newOrders.Phone.setText(orderDetailsConstructor.getAdminPhone());
                newOrders.Mode.setText(orderDetailsConstructor.getMode());
                newOrders.Total.setText(orderDetailsConstructor.getTotal());


                newOrders.Yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        adapter.getRef(i).getKey();
                        Toast.makeText(Context,adapter.getRef(i).getKey(),Toast.LENGTH_SHORT).show();
                        //Send Notification to User
                        moveFirebaseRecord(adapter.getItem(i).getAdminPhone(),adapter.getRef(i).getKey(),New.child(adapter.getRef(i).getKey()),AddOrder.child(adapter.getRef(i).getKey()));
                    }
                });

                newOrders.No.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Send Notification to User
                        NotifyUser(adapter.getRef(i).getKey(),adapter.getItem(i).getAdminPhone());
                    }
                });
            }

            @NonNull
            @Override
            public NewOrders onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                LayoutInflater inflater = LayoutInflater.from(Context);
                View view = inflater.inflate(R.layout.newlist,parent,false);
                return new NewOrders(view);
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    private void NotifyUser(String key, String adminPhone) {

    }


    public void moveFirebaseRecord(final String ID, final String Phone , final DatabaseReference fromPath, final DatabaseReference toPath){

        fromPath.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                toPath.setValue(snapshot.getValue(), new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                        if(error == null){
                            ref.child("status").setValue("Accepted");
                            AddOrder.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    count.SetCount((int) snapshot.getChildrenCount());
                                    Toast.makeText(getContext(),"Accepted",Toast.LENGTH_SHORT).show();
                                    NotifyUser(ID,Phone);
                                    fromPath.setValue(null);
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }else{
                            Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public interface Count {
        void SetCount(int Number);
    }

    public static class NewOrders extends RecyclerView.ViewHolder{
        TextView Name,Address,Phone,Mode,Total,OrderID;
        Button Yes,No;
        public NewOrders(@NonNull View itemView) {
            super(itemView);
            OrderID = itemView.findViewById(R.id.OrderID);
            Name = itemView.findViewById(R.id.NewName);
            Address = itemView.findViewById(R.id.NewAdd);
            Phone = itemView.findViewById(R.id.NewPhone);
            Mode = itemView.findViewById(R.id.Mode);
            Total = itemView.findViewById(R.id.TotalAmount);

            //Button
            Yes = itemView.findViewById(R.id.YesOrder);
            No = itemView.findViewById(R.id.NoOrder);

        }
    }
}

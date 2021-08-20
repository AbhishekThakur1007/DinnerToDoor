package com.example.food.AdminSide.Order;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.food.AdminSide.StaffAdminCommon.DeliveryDetailsConstructor;
import com.example.food.AdminSide.Interface.ItemClickListener;
import com.example.food.AdminSide.StaffAdminCommon.OrderDetailsConstructor;
import com.example.food.AdminSide.StaffAdminCommon.UserName_Address;
import com.example.food.Common.UserDetails_Constructor;
import com.example.food.Common.Cart.nameQuantity_Constructor;
import com.example.food.UserSide.Database;
import com.example.food.AdminSide.StaffAdminCommon.pickupList;
import com.example.food.R;
import com.example.food.Common.SessionManager;
import com.example.food.AdminSide.StaffAdminCommon.UserOrderDetailsAdapter;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class OrderDetailsAdmin extends Fragment {

    RecyclerView recyclerView;
    FirebaseDatabase root;
    DatabaseReference orders,Shop;
    FirebaseRecyclerAdapter<OrderDetailsConstructor, UserOrderDetailsAdapter> adapter;
    FirebaseRecyclerAdapter<nameQuantity_Constructor, orderedFoodAdapter> details;
    Animation slide_down, slide_up;
    public RecyclerView recyclerViewName;
    String Phone,Check,USerID,Name;
    String get;
    TextView NoOrder;
    boolean b,User;
    TextInputEditText NameType;
    Snackbar snackbar;
    List<UserName_Address> list;
    CoordinatorLayout coordinatorLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)  {
        final View Details = inflater.inflate(R.layout.fragment_order__details__admin, container, false);

        recyclerView = Details.findViewById(R.id.Order_status);
        ImageButton Limit = Details.findViewById(R.id.limit_back);
        NoOrder = Details.findViewById(R.id.NoOrder);
        coordinatorLayout = Details.findViewById(R.id.recyclerDodge);
        NameType = Details.findViewById(R.id.SearchCart1);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        slide_down = AnimationUtils.loadAnimation(getContext(),R.anim.slide_down);
        slide_up = AnimationUtils.loadAnimation(getContext(),R.anim.slide_up);

        root = FirebaseDatabase.getInstance();
        orders =  root.getReference("New");
        Shop =  root.getReference("Shop").child("Limit");

        SessionManager sessionManager = new SessionManager(getContext(), SessionManager.SESSION_USERSESSION);
        final HashMap<String, String> userDetails = sessionManager.getUserDetailFromSession();
        USerID = Objects.requireNonNull(userDetails.get(SessionManager.KEY_USERID));
        Database db = new Database(getContext());
        UserDetails_Constructor userDetailsConstructor = db.UserDetails(USerID);
        Name = userDetailsConstructor.getFullName();
        Phone = userDetailsConstructor.getPhoneNo().substring(3);

        orders.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    DisplayOrders(orders);
                    recyclerView.setVisibility(View.VISIBLE);
                    NoOrder.setVisibility(View.GONE);
                }else{
                    recyclerView.setVisibility(View.GONE);
                    NoOrder.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        NameType.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(s.length() >= 2)
                    FirebaseSearch(s.toString(),false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        NameType.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP){
                    NameType.clearFocus();
                    hideKeyboard(v);
                    DisplayOrders(orders);
                    return true;
                }
                return false;
            }
        });

        Limit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Limit Set
                WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
                layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
                layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
                final Dialog dialog = new Dialog(getContext());
                dialog.getWindow().setAttributes(layoutParams);
                dialog.setContentView(R.layout.orderlimit);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                dialog.getWindow().setDimAmount(0.6f);

                final TextInputEditText EditOder = dialog.findViewById(R.id.EditOder);
                final TextInputLayout SetLimitLay = dialog.findViewById(R.id.SetLimitLay);
                Button YesSpecial1,NoSpecial1;
                YesSpecial1 = dialog.findViewById(R.id.YesLimit);
                NoSpecial1 = dialog.findViewById(R.id.NoSpecial1);

                Shop.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            get = snapshot.getValue().toString();
                            EditOder.setText(get);
                            dialog.show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                YesSpecial1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(EditOder.getText().toString().length() == 0){
                            SetLimitLay.setErrorEnabled(true);
                            SetLimitLay.setError("Limit Cannot be empty.");
                        }else{
                            Shop.setValue(EditOder.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(getContext(),"Limited Updated",Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                }
                            });
                        }
                    }
                });

                NoSpecial1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }
        });

        return Details;
    }

    // # 1 (Get Order PickUp details)
    private void GetOrderID() {

        pickupList pickupList = new pickupList(getContext());
        int Count;
        Count = pickupList.getCount();
        Toast.makeText(getContext(),String.valueOf(Count),Toast.LENGTH_SHORT).show();
        if(Count > 0){
            snackbar = Snackbar.make(coordinatorLayout," Total Orders Picked : " + Count ,Snackbar.LENGTH_INDEFINITE).setAction("View", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ShowOrder();
                }
            }).setActionTextColor(Color.WHITE);
            View view = snackbar.getView();
            TextView textView = view.findViewById(com.google.android.material.R.id.snackbar_text);
            textView.setTextColor(Color.WHITE);
            snackbar.show();
        }
    }

    // # 2 Display Orders
    public void DisplayOrders(Query query) {

        FirebaseRecyclerOptions<OrderDetailsConstructor> options =
                new FirebaseRecyclerOptions.Builder<OrderDetailsConstructor>()
                        .setQuery(query, OrderDetailsConstructor.class).build();

        adapter = new FirebaseRecyclerAdapter<OrderDetailsConstructor, UserOrderDetailsAdapter>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final UserOrderDetailsAdapter userOrderDetailsAdapter, final int i, @NonNull OrderDetailsConstructor orderDetailsConstructor) {
                userOrderDetailsAdapter.Staff_Status_Order.setText(orderDetailsConstructor.getStatus());

                // Check Orders for green and red tick
                orders.child(adapter.getRef(i).getKey()).child("Delivery").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            Check = snapshot.child("dname").getValue().toString();
                            if(Check.equalsIgnoreCase(Name)){
                                userOrderDetailsAdapter.green.setVisibility(View.VISIBLE);
                                userOrderDetailsAdapter.name_Staff.setText(Name);
                            }else{
                                userOrderDetailsAdapter.name_Staff.setText(Check);
                                userOrderDetailsAdapter.red.setVisibility(View.VISIBLE);
                            }
                        }else{

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });

                userOrderDetailsAdapter.UserName.setText(orderDetailsConstructor.getName());
                String Total = "Total : " + orderDetailsConstructor.getTotal();
                userOrderDetailsAdapter.COD_order.setText(Total);
                userOrderDetailsAdapter.Mode.setText(orderDetailsConstructor.getMode());
                userOrderDetailsAdapter.UserNumber.setText(orderDetailsConstructor.getAdminPhone());

                userOrderDetailsAdapter.AddressUser.setText(orderDetailsConstructor.getAddress().replace("+",","));

                userOrderDetailsAdapter.CallUser.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse("tel:"+ adapter.getItem(i).getAdminPhone()));
                        startActivity(intent);
                    }
                });

                userOrderDetailsAdapter.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, final int position, boolean isLongClick) {
                        if(!b){
                            b = true;
                            /*Toast.makeText(getContext(),String.valueOf(b),Toast.LENGTH_SHORT).show();*/
                            orders.child(adapter.getRef(position).getKey()).child("Delivery").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    int Check;
                                    if(snapshot.exists()){
                                        if(snapshot.child("dname").getValue().toString().equalsIgnoreCase(Name)){
                                            Check = 1;
                                        }else{
                                            Check = 2;
                                        }
                                    }else{
                                        Check = 3;
                                    }
                                    // Id, Name, phone ,Address , Status , Position
                                    ShowDetailsDialog(adapter.getRef(i).getKey(),adapter.getItem(i).getName(),adapter.getItem(i).getAdminPhone(),adapter.getItem(i).getAddress().replace("+",","),adapter.getItem(position).getStatus(),Check);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }

                    }
                });
            }

            @NonNull
            @Override
            public UserOrderDetailsAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view =LayoutInflater.from(getContext()).inflate(R.layout.staff_delivery_details, parent, false);
                return new UserOrderDetailsAdapter(view);
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    // # 3
    private void ShowDetailsDialog(final String position, final String name, final String phone, final String address, String Status, final int Exist) {

        final BottomSheetDialog view = new BottomSheetDialog(requireContext());
        view.setContentView(R.layout.name_quantity);
        view.getBehavior().setExpandedOffset(200);
        view.getBehavior().setDraggable(false);

        if(NameType.getText().toString().length() > 0){
            NameType.getText().clear();
            NameType.clearFocus();
            DisplayOrders(orders);
        }

        RelativeLayout relativeLayout = view.findViewById(R.id.ShowDefault);
        RelativeLayout relativeLayout1 = view.findViewById(R.id.ShowSecondary);
        relativeLayout1.setVisibility(View.GONE);
        relativeLayout.setVisibility(View.VISIBLE);
        recyclerViewName = view.findViewById(R.id.Scoll_items);
        final TextView QuanTotal = view.findViewById(R.id.QuanTotal);
        final TextView DeliveryMeStatus = view.findViewById(R.id.DeliveryMeStatus);
        assert recyclerViewName != null;
        recyclerViewName.setLayoutManager(new LinearLayoutManager(getContext()));
        final Spinner spinner = view.findViewById(R.id.spinner);
        final RadioGroup radioGroup = view.findViewById(R.id.DeliverySpinner);

        if(Exist == 1){
            radioGroup.check(R.id.yesRadio);
            radioGroup.setVisibility(View.VISIBLE);
            DeliveryMeStatus.setVisibility(View.VISIBLE);
        }else if (Exist == 2){
            radioGroup.setVisibility(View.GONE);
            DeliveryMeStatus.setVisibility(View.GONE);
        }else if(Exist == 3){
            radioGroup.setVisibility(View.VISIBLE);
            DeliveryMeStatus.setVisibility(View.VISIBLE);
        }

        orders.child(position).child("foods").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                QuanTotal.setText(String.valueOf(snapshot.getChildrenCount()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        ArrayAdapter<String> myAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.Names));
        myAdapter.setDropDownViewResource(android.R.layout.simple_list_item_checked);
        spinner.setAdapter(myAdapter);

        List<String> List = Arrays.asList(getResources().getStringArray(R.array.Names));
        for(int i = 0; i <= List.size(); i++){
            if(List.get(i).equals(Status)){
                spinner.setSelection(i);
                break;
            }
        }

        FirebaseRecyclerOptions<nameQuantity_Constructor> options1 = new FirebaseRecyclerOptions.Builder<nameQuantity_Constructor>()
                .setQuery(orders.child(position).child("foods"), nameQuantity_Constructor.class).build();

        details =  new FirebaseRecyclerAdapter<nameQuantity_Constructor, orderedFoodAdapter>(options1) {
            @Override
            protected void onBindViewHolder(@NonNull final orderedFoodAdapter orderedFoodAdapter, int i, @NonNull nameQuantity_Constructor name_quantityConstructor) {

                orderedFoodAdapter.Dish_name.setText(name_quantityConstructor.getProductName());
                orderedFoodAdapter.Dish_Quantity.setText(name_quantityConstructor.getQuantity());
                if(name_quantityConstructor.getPlate() != null && name_quantityConstructor.getPlate().length() > 1){
                    orderedFoodAdapter.Dish_Size.setText(name_quantityConstructor.getPlate());
                }else{
                    orderedFoodAdapter.LaySizeOrdered.setVisibility(View.GONE);
                }

                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                        String status = spinner.getItemAtPosition(pos).toString();
                        orders.child(position).child("status").setValue(status);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }


            @Override
            public int getItemCount() {
                return super.getItemCount();
            }

            @NonNull
            @Override
            public orderedFoodAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(getContext()).inflate(R.layout.order_list, parent, false);
                return new orderedFoodAdapter(view);
            }
        };

        details.startListening();
        details.notifyDataSetChanged();
        recyclerViewName.setAdapter(details);

        view.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if(radioGroup.getCheckedRadioButtonId() != -1){
                    RadioButton radioButton = view.findViewById(radioGroup.getCheckedRadioButtonId());
                    // Current Staff selecting order
                    if(radioButton.getText().toString().equalsIgnoreCase("yes") && Exist == 3){
                        DeliveryDetailsConstructor deliveryDetailsConstructor = new DeliveryDetailsConstructor(Name,Phone);
                        orders.child(position).child("Delivery").setValue(deliveryDetailsConstructor).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                UserName_Address userName_address = new UserName_Address(position,name,phone,address);
                                pickupList pickUp = new pickupList(getContext());
                                long i = pickUp.putDetails(userName_address);
                                if(i > -1){
                                    adapter.notifyDataSetChanged();
                                    GetOrderID();
                                    /*adapter.notifyItemChanged(j);*/
                                }
                            }
                        });
                    }else if(radioButton.getText().toString().equalsIgnoreCase("no")  && Exist == 1){
                        orders.child(position).child("Delivery").setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                pickupList pickUp = new pickupList(getContext());
                                long i = pickUp.Delete(phone);
                                if(i > 0){
                                    int c = pickUp.getCount();
                                    if(c == 0){
                                        snackbar.dismiss();
                                    }else{
                                        GetOrderID();
                                    }
                                    DisplayOrders(orders);
                                }
                            }
                        });
                    }
                }
                b = false;
            }
        });

        view.show();
    }

    // SnackBar
    private void ShowOrder(){

        BottomSheetDialog view = new BottomSheetDialog(requireContext());
        view.setContentView(R.layout.name_quantity);
        view.getBehavior().setDraggable(false);

        RelativeLayout relativeLayout = view.findViewById(R.id.ShowSecondary);
        RelativeLayout relativeLayout1 = view.findViewById(R.id.ShowDefault);
        relativeLayout1.setVisibility(View.GONE);
        relativeLayout.setVisibility(View.VISIBLE);
        RecyclerView recyclerView = view.findViewById(R.id.Pickup_Scoll_items);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        pickupList pickup = new pickupList(getContext());
        PickedOrderAdapter pickedOrderAdapter;
        list = new ArrayList<>();
        list = pickup.GetDetails();
        final int Old = pickup.getCount();
        pickedOrderAdapter = new PickedOrderAdapter(getContext(),list);
        recyclerView.setAdapter(pickedOrderAdapter);

        view.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                pickupList pickUp = new pickupList(getContext());
                int c = pickUp.getCount();
                if(Old != c){
                    if(c == 0){
                        snackbar.dismiss();
                    }else{
                        GetOrderID();
                    }
                    DisplayOrders(orders);
                }else {
                    GetOrderID();
                }
            }
        });
        view.show();
    }

    // Search
    private void FirebaseSearch(final String item, boolean search) {
        int length = item.length();
        final String Forward = item.substring(0,1).toUpperCase().trim() + item.substring(1,length).toLowerCase();

        if(!search) {
            final Query Query = orders.orderByChild("name").startAt(Forward).endAt(Forward + "\uf8ff");
            Query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        User = true;
                        DisplayOrders(Query);
                    }else{
                        User = false;
                        FirebaseSearch(item,true);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    public void hideKeyboard(View view){
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(),0);
    }

}
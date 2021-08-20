package com.example.food.UserSide.UserCartFragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.food.AdminSide.Order.PickedOrderAdapter;
import com.example.food.AdminSide.StaffAdminCommon.DeliveryDetailsConstructor;
import com.example.food.AdminSide.Interface.ItemClickListener;
import com.example.food.AdminSide.StaffAdminCommon.OrderDetailsConstructor;
import com.example.food.AdminSide.StaffAdminCommon.UserName_Address;
import com.example.food.AdminSide.Order.orderedFoodAdapter;
import com.example.food.Common.UserDetails_Constructor;
import com.example.food.Common.Cart.nameQuantity_Constructor;
import com.example.food.UserSide.Database;
import com.example.food.AdminSide.StaffAdminCommon.pickupList;
import com.example.food.R;
import com.example.food.AdminSide.StaffAdminCommon.UserOrderDetailsAdapter;
import com.example.food.Common.SessionManager;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

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

public class CartFragment extends Fragment {

    // User
    FirebaseDatabase root;
    ImageButton CallUser;
    RecyclerView recyclerView,recyclerViewName;
    String USerID,parent,USerOrStaff,Phone;
    TextView OrderHeading1,PaymentStatus,OrderStatus,HelpNumber;
    String Longitude,Latitude;

    //Staff
    ImageButton Back;
    TextView NoOrder;
    RecyclerView delivery;
    FirebaseRecyclerAdapter<OrderDetailsConstructor, UserOrderDetailsAdapter> adapter;
    FirebaseRecyclerAdapter<nameQuantity_Constructor, orderedFoodAdapter> details;
    DatabaseReference  orders;
    RadioGroup radioGroup;
    RadioButton radioButton;
    String Check,Name;
    boolean User;
    TextInputEditText NameType;
    //From Activity
    CoordinatorLayout coordinatorLayout;
    BottomNavigationView bottom_nav;
    androidx.fragment.app.FragmentContainerView fragmentContainerView;
    Snackbar snackbar;
    int Count;
    boolean b;
    List<UserName_Address> list;
    UpdateBottomCount updateBottomCount;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Activity activity = (Activity) context;
        try {
            updateBottomCount = (UpdateBottomCount) activity;
        }catch (ClassCastException e){
            throw new ClassCastException((activity).toString());
        }
    }

    @Override
    public View onCreateView(@NotNull final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        SessionManager sessionManager = new SessionManager(getContext(), SessionManager.SESSION_USERSESSION);
        final HashMap<String, String> userDetails = sessionManager.getUserDetailFromSession();
        USerID = Objects.requireNonNull(userDetails.get(SessionManager.KEY_USERID));
        Database db = new Database(getContext());
        UserDetails_Constructor userDetailsConstructor = db.UserDetails(USerID);
        Name = userDetailsConstructor.getFullName();
        USerOrStaff = userDetailsConstructor.getIsstaff();
        Phone = userDetailsConstructor.getPhoneNo();
        root = FirebaseDatabase.getInstance();

        if(USerOrStaff.equals("true")){
            View staff = inflater.inflate(R.layout.staff_cart, container, false);
            return Staff(staff,Phone);
        }else if(USerOrStaff.equalsIgnoreCase("false")){
            View detail = inflater.inflate(R.layout.fragment_cart, container, false);
            return User(detail,Phone);
        }else{
            return inflater.inflate(R.layout.fragment_order__details__admin,container,false);
        }
    }

    //Staff
    @SuppressLint("ClickableViewAccessibility")
    private View Staff(View detail, final String Contact) {

        Back = detail.findViewById(R.id.cart_Staff);
        NoOrder = detail.findViewById(R.id.NoOrder1);
        delivery = detail.findViewById(R.id.Order_Delivery);
        NameType = detail.findViewById(R.id.SearchCart);

        //From Activity
        coordinatorLayout = detail.findViewById(R.id.Coordinator);
        bottom_nav  = getActivity().findViewById(R.id.bottom_nav);
        fragmentContainerView  = getActivity().findViewById(R.id.fragment_container);

        delivery.setLayoutManager(new LinearLayoutManager(getContext()));
        orders = root.getReference("Order");

        GetOrderID();
        DisplayOrders(orders);
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
        return detail;
    }

    // # 1 (Get Order PickUp details)
    private void GetOrderID() {

        pickupList pickupList = new pickupList(getContext());
        Count = 0;
        Count = pickupList.getCount();
        updateBottomCount.Update(Count);
        /*Toast.makeText(getContext(),String.valueOf(Count),Toast.LENGTH_SHORT).show();*/
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
           /* CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) snackbar.getView().getLayoutParams();*/
            /*snackbar.setAnchorView(bottom_nav);*/
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
                                        ShowDetailsDialog(adapter.getRef(i).getKey(),adapter.getItem(i).getName(),adapter.getItem(i).getAdminPhone(),adapter.getItem(i).getAddress().replace("+",","),adapter.getItem(position).getStatus(), Check);
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
        delivery.setAdapter(adapter);
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
        radioGroup = view.findViewById(R.id.DeliverySpinner);

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
                    radioButton = view.findViewById(radioGroup.getCheckedRadioButtonId());
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
                                        updateBottomCount.Update(c);
                                    }else{
                                        GetOrderID();
                                        DisplayOrders(orders);
                                    }
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
                            updateBottomCount.Update(c);
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

    // Interface
    public interface UpdateBottomCount{

         void Update(int Count);
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

        /*else{
            final ArrayList<String> ID = new ArrayList<>();
            orders.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot snapshot1: snapshot.getChildren()){
                        if(snapshot1.exists()){
                            ID.add(snapshot1.getKey());
                        }
                    }
                    Update();
                }

                private void Update() {
                    for(final int[] n = {0}; n[0] < ID.size();){
                        final Query Query = orders.child(ID.get(n[0])).child("Delivery").orderByChild("dname").startAt(Forward).endAt(Forward + "\uf8ff");
                        Query.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists()){
                                    User = true;
                                    DisplayOrders(Query);
                                }else{
                                    User = false;
                                    n[0]++;
                                    Toast.makeText(getContext(),"Nowhere",Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }*/
    }

    //User
    private View User(View detail,String Contact) {

        OrderHeading1 = detail.findViewById(R.id.OrderHeading);
        OrderStatus = detail.findViewById(R.id.User_oder_Info);
        PaymentStatus = detail.findViewById(R.id.User_Payment);
        recyclerView = detail.findViewById(R.id.UserList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        CallUser = detail.findViewById(R.id.CallUser);
        HelpNumber = detail.findViewById(R.id.HelpNumber);

        CallUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + HelpNumber.getText().toString()));
                requireContext().startActivity(intent);
            }
        });

        //Get Order Id and Show Order Status
        root.getReference("Orders").orderByChild("adminPhone").equalTo(Contact).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot snapshot1: dataSnapshot.getChildren()){
                        parent = snapshot1.getKey();
                        setCoordinates(parent);
                        Database(parent);
                        LoadStatus();
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return detail;
    }

    private void setCoordinates(String display) {
        //Get Longitude and Latitude
        root.getReference("Orders").child(display).child("coordinates").child("0").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    Latitude = Objects.requireNonNull(snapshot.child("latitude").getValue()).toString();
                    Longitude = Objects.requireNonNull(snapshot.child("longitude").getValue()).toString();
                    GooGleMapDisplayDirection(Longitude,Latitude);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //Display Direction
    private void GooGleMapDisplayDirection(String longitude, String latitude) {

        //Google Maps
        SupportMapFragment supportMapFragment =  (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.google_maps);
        final double Long = Double.parseDouble(longitude);
        final double Lat = Double.parseDouble(latitude);
        final LatLng latLng = new LatLng(Lat,Long);
        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                //OnCLick on map
                MarkerOptions markerOptions =new MarkerOptions();
                //Set  Position on marker
                markerOptions.position(latLng);
                googleMap.addMarker(markerOptions);
                //Zoom
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
            }
        });
    }

    //Display Payment mode and Status from firebase
    private void Database(String display) {
        root.getReference("Orders").child(display).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                 PaymentStatus.setText(Objects.requireNonNull(snapshot.child("mode").getValue()).toString());
                 OrderStatus.setText(Objects.requireNonNull(snapshot.child("status").getValue()).toString());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //Recycler View for list
    private void LoadStatus() {

        //Fetch Value from Array list
        FirebaseRecyclerOptions<nameQuantity_Constructor> options = new FirebaseRecyclerOptions.Builder<nameQuantity_Constructor>().setQuery( root.getReference().child("Orders").child(parent).child("foods"), nameQuantity_Constructor.class).build();

        FirebaseRecyclerAdapter<nameQuantity_Constructor,product_Adapter> adapter = new FirebaseRecyclerAdapter<nameQuantity_Constructor, product_Adapter>(options) {
            @Override
            protected void onBindViewHolder(@NonNull product_Adapter product_adapter, int i, @NonNull nameQuantity_Constructor name_quantity) {

                product_adapter.userFood.setText(name_quantity.getProductName());
                product_adapter.userQuantity.setText(name_quantity.getQuantity());
            }

            @NonNull
            @Override
            public product_Adapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(getContext()).inflate(R.layout.user_food_info,parent,false);
                return new product_Adapter(view);
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    public void hideKeyboard(View view){
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(),0);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(snackbar != null){
            if(snackbar.isShown()){
                snackbar.dismiss();
            }
        }
    }

}

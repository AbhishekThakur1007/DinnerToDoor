
package com.example.food.UserSide.FoodDetails;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.food.AdminSide.Interface.ItemClickListener;
import com.example.food.Common.Food_Constructor;
import com.example.food.R;
import com.example.food.UserSide.Database;
import com.example.food.UserSide.FoodFragment.FoodListAdapter;
import com.example.food.UserSide.Order_Constructor;
import com.example.food.UserSide.TotalItems.FinalOrderDetails;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class foodDetail extends AppCompatActivity {

    // Text view for display
    TextView foodName, priceTotal, foodDes, counter1, PriceCurrent;
    ImageView Food_image;

    Button btnCartFinal1, checkout, AddFinal1;
    String foodId = "",GetPlateFinal;
    String foodIntent = "";
    FirebaseDatabase database;
    DatabaseReference foods;
    Food_Constructor Currentfood;
    ImageButton increase1, decrease1;
    Integer noOfItems = 0;
    String PlateBottom = "";
    String Old;
    int Final, checkPosition = -1,sendStatus;
    Query query;
    boolean check,FirstOrSecond,FinalCheck,DecreaseValue,OptionsCheck,Double;
    String New,PlateGet;
    RelativeLayout TopDes1,chooseOption1,TotalColor1,checkLay1,quantityCart1;
    View bottom1;
    FirebaseRecyclerAdapter<fetch_plate_info, FoodListAdapter> recyclerAdapter;
    RecyclerView recyclerView;
    FirebaseRecyclerOptions<fetch_plate_info> options;
    Animation upToDown,DownTOup;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailsfood);

        //Firebase
        database = FirebaseDatabase.getInstance();
        foods = database.getReference("Foods");


        // Display
        Food_image = findViewById(R.id.imageView3);
        foodName = findViewById(R.id.DishName);
        foodDes = findViewById(R.id.description);

        //Add to Cart Buttons
        btnCartFinal1 = findViewById(R.id.btnCartFinal);
        checkout = findViewById(R.id.Pay1);

        // Select Amount
        increase1 = findViewById(R.id.increase);
        decrease1 = findViewById(R.id.decrease);
        counter1 = findViewById(R.id.counter);

        // Price
        priceTotal = findViewById(R.id.totalAmount);
        PriceCurrent = findViewById(R.id.halfPrice);

        //Layout
        TopDes1 = findViewById(R.id.TopDown);
        //Down
        chooseOption1 = findViewById(R.id.chooseOption);
        TotalColor1 = findViewById(R.id.TotalColor);
        quantityCart1 = findViewById(R.id.quantityCart);
        checkLay1 = findViewById(R.id.checkLay);

        //Animation
        upToDown = AnimationUtils.loadAnimation(this,R.anim.top_send_down);
        DownTOup = AnimationUtils.loadAnimation(this,R.anim.slide_in_bottom);

        Animate(TopDes1,upToDown);
        Animate(TotalColor1,DownTOup);
        Animate(quantityCart1,DownTOup);
        Animate(checkLay1,DownTOup);

        //Get intent from From Search or List of food
        if (getIntent() != null) {
            foodId = getIntent().getStringExtra("Details");
            if (foodId == null) {
                foodIntent = getIntent().getStringExtra("Search1");
                foods.orderByChild(foodIntent).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot1 : snapshot.getChildren()) {
                            if (Objects.requireNonNull(dataSnapshot1.child("name").getValue()).toString().equals(foodIntent)) {
                                foodId = dataSnapshot1.getKey();
                                getDetails(foodId);
                                break;
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            } else {
                getDetails(foodId);
            }
        }

        // Increase First check boolean if true then increase else select item
        increase1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (check) {
                    if (noOfItems >= 10) {
                        noOfItems = 10;
                        Toast.makeText(getApplication(), "Please contact Restaurant for.", Toast.LENGTH_SHORT).show();
                    } else if (noOfItems >= 0) {
                        noOfItems += 1;
                        Final = Integer.parseInt(PriceCurrent.getText().toString()) * noOfItems;
                        Display(noOfItems, Final);
                    }
                } else { //FinalCheck(false) first time or second time  (FirstOrSecond = true means 1st time )
                    if (!FinalCheck) {
                        // Added display
                        FirstOrSecond = true;
                    }
                    OpenPermissions();
                }
                }
        });

        // Decrease
        decrease1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(check){
                    if (noOfItems <= 0) {
                        noOfItems = 0;
                        // First time
                    } else if(!DecreaseValue){
                        noOfItems -= 1;
                        Final = Integer.parseInt(PriceCurrent.getText().toString()) * noOfItems;
                        Display(noOfItems, Final);
                    }else{
                        DecreaseValue = false;
                    }
                } else{
                    OpenPermissions();
                }

            }
        });

        // Add to Cart
        btnCartFinal1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(check){
                    UploadToSQlite();
                    // Select Quantity again
                    check= false;
                }
                else{
                    if (!FinalCheck) {
                        // First Time
                        FirstOrSecond = true;
                    }
                    OpenPermissions();
                }
            }
        });

        // Checkout
        checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Database db = new Database(foodDetail.this);
                if(db.GetTotalCount() == 0){
                    Toast.makeText(foodDetail.this,"Cart is empty",Toast.LENGTH_SHORT).show();
                }
                else {
                    Intent i = new Intent(foodDetail.this, FinalOrderDetails.class);
                    startActivity(i);
                    finish();
                }
            }
        });
    }

    private void OpenPermissions() {
        if(OptionsCheck){
            Double = true;
            OpenOptions();
        }else{
            //Now user can increase counter
            check = true;
            if(Integer.parseInt(counter1.getText().toString()) > 0){
                Toast.makeText(foodDetail.this,"Yes",Toast.LENGTH_SHORT).show();
                noOfItems = Integer.parseInt(counter1.getText().toString()) + 1;
                Final = noOfItems * Integer.parseInt(PriceCurrent.getText().toString());
            }else{
                noOfItems = 1;
                Final = Integer.parseInt(PriceCurrent.getText().toString());
            }
            Display(noOfItems, Final);
        }
    }

    private void Animate(RelativeLayout topDes1, Animation upToDown) {
        topDes1.setAnimation(upToDown);
    }

    // To Cart Add
    private void UploadToSQlite() {

        Order_Constructor orderConstructor = null;
        try {
            String amount = priceTotal.getText().toString();
            String Quantity = counter1.getText().toString();
            if(OptionsCheck){
                orderConstructor = new Order_Constructor(foodId, Currentfood.getName(),Quantity,PriceCurrent.getText().toString(), amount, GetPlateFinal);
            }else{
                orderConstructor = new Order_Constructor(foodId, Currentfood.getName(),Quantity,PriceCurrent.getText().toString(), amount, null);
            }

        } catch (Exception e) {
            Toast.makeText(foodDetail.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
        }
        Database Add = new Database(foodDetail.this);
        assert orderConstructor != null;
        sendStatus = 0;
        Check(Add, orderConstructor,true);
    }

    //check Whether Same item exists ot not
    private int Check(Database database , Order_Constructor orderConstructor, boolean from){

        if(OptionsCheck){
            // Check if First time added
            if (database.Check(orderConstructor,Double).getString("Plate").equals("NOT")) {
                if(from){
                    database.addToCart(orderConstructor);
                    Toast.makeText(getBaseContext(), "Added to Cart.", Toast.LENGTH_SHORT).show();
                }
                sendStatus = 1;
            }
            else {
                // Check if Selected item in List
                String again = database.Check(orderConstructor,Double).getString("Plate");
                // If Exists then display to user.
                if(again.equals(GetPlateFinal)){
                    if(from){
                        database.EditItem(orderConstructor);
                        Toast.makeText(foodDetail.this, "Edited.", Toast.LENGTH_SHORT).show();
                    }
                    sendStatus = 2;
                }
                else if(again.equals("diffPlate")){ // If Same but different plate.
                    Toast.makeText(getBaseContext(), "Added.", Toast.LENGTH_SHORT).show();
                    if(from){
                        database.addToCart(orderConstructor);
                        Toast.makeText(getBaseContext(), "Added.", Toast.LENGTH_SHORT).show();
                    }
                    sendStatus = 3;
                }
            }
        }

        else{

            if(database.SingleOption(orderConstructor).getString("Plate").equals("NOT")){

                    database.addToCart(orderConstructor);
                    Toast.makeText(getBaseContext(), "Added to Cart.", Toast.LENGTH_SHORT).show();
                sendStatus = 1;
            }else{
                // Edited.
                   Toast.makeText(foodDetail.this, "Edited.", Toast.LENGTH_SHORT).show();
            }
            sendStatus = 2;
        }

        check = false;
        return sendStatus;
    }

    // Get Options
    private void OpenOptions() {
        final BottomSheetDialog bottomSheetDialog1 = new BottomSheetDialog(foodDetail.this, R.style.BottomSheetDialog);

        bottom1 = LayoutInflater.from(getApplicationContext()).inflate(
                R.layout.bottom_plate, findViewById(R.id.bottomPlate));

            // Plus Click
            recyclerView = bottom1.findViewById(R.id.Options);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            AddFinal1 = bottom1.findViewById(R.id.AddFinal);

            // Get Plate String from recycler view
            getList();

            // Add
            AddFinal1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PlateGet = getList();
                    if (PlateGet.equals("")) {
                        Toast.makeText(foodDetail.this, "Not Selected", Toast.LENGTH_SHORT).show();
                        check = false;
                    } else {
                        bottomSheetDialog1.dismiss();
                        PlateBottom = "";

                        // First time
                        if(FirstOrSecond){
                            //First time
                            noOfItems = 1;
                            //For second time  set to false
                            FirstOrSecond = false;
                            // Allow to increase counter opposite of FirstOrSecond
                            FinalCheck = true;
                            Final = Integer.parseInt(PriceCurrent.getText().toString()) * noOfItems;
                        }

                        // Second time
                        else{
                            Database db = new Database(foodDetail.this);
                            Order_Constructor orderConstructor = new Order_Constructor(foodId,null,null,null,null,PlateGet);
                            if(Check(db, orderConstructor,false) == 2){     // Same plate
                                int k = db.Check(orderConstructor,Double).getInt("Quan");
                                int j = db.Check(orderConstructor,Double).getInt("Total");
                                noOfItems = k;
                                Final = j;
                                Toast.makeText(foodDetail.this,String.valueOf("Same"),Toast.LENGTH_SHORT).show();
                            }else if (Check(db, orderConstructor,false) == 3){    // Same different plate
                                noOfItems = 1;
                                Final = Integer.parseInt(PriceCurrent.getText().toString()) * noOfItems;
                                Toast.makeText(foodDetail.this,"Not same.",Toast.LENGTH_SHORT).show();
                            }
                        }
                        Display(noOfItems, Final);
                        //Now user can increase counter
                        check = true;
                    }
                }
            });

            recyclerView.setAdapter(recyclerAdapter);
            recyclerAdapter.startListening();

        bottomSheetDialog1.setContentView(bottom1);
        bottomSheetDialog1.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(messageFinal,new IntentFilter("Send_Message"));
    }

    public BroadcastReceiver messageFinal = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getExtras() != null){
                Display(intent.getIntExtra("noOfItems",0),intent.getIntExtra("Total",0));
            }
        }
    };

    private void Display(int number, int Total) {
        counter1.setText(String.valueOf(number));
        priceTotal.setText(String.valueOf(Total));
    }

    // Get String to check Whether check box selected or not
    private String getList() {

        query = foods.child(foodId).child("options");
        options = new FirebaseRecyclerOptions.Builder<fetch_plate_info>().setQuery(query, fetch_plate_info.class).build();
        recyclerAdapter = new FirebaseRecyclerAdapter<fetch_plate_info, FoodListAdapter>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final FoodListAdapter foodListAdapter1, int i, @NonNull fetch_plate_info fetch_plate_info1) {

                // Check heading
                New = recyclerAdapter.getItem(i).getSizeCategory();
                foodListAdapter1.listHeading1.setText(fetch_plate_info1.getSizeCategory());
                if(foodListAdapter1.getAdapterPosition() > 0){
                     Old = recyclerAdapter.getItem(i-1).getSizeCategory();
                    if (New.equals(Old)){
                        foodListAdapter1.listHeading1.setVisibility(View.GONE);
                    } else {
                        foodListAdapter1.listHeading1.setVisibility(View.VISIBLE);
                        foodListAdapter1.listHeading1.setText(fetch_plate_info1.getSizeCategory());
                    }
                }

                // half or full
                foodListAdapter1.OptionsName1.setText(fetch_plate_info1.getQuantity());

                // Price
                foodListAdapter1.Price.setText(fetch_plate_info1.getPrice());

                foodListAdapter1.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        // CHANGE COLOR ON CLICK
                        foodListAdapter1.OptionsName1.setTextColor(Color.parseColor("#FFFF00FF"));
                        foodListAdapter1.Price.setTextColor(Color.parseColor("#FFFF00FF"));

                        //SET CLICKED TRUE
                        foodListAdapter1.halfRb1.setChecked(true);
                        checkPosition = position;
                        recyclerAdapter.notifyDataSetChanged();

                        //Get Price
                        PriceCurrent.setText(foodListAdapter1.Price.getText().toString());

                        GetPlateFinal = foodListAdapter1.OptionsName1.getText().toString();

                        PlateBottom = foodListAdapter1.OptionsName1.getText().toString();
                    }
                });

                if (checkPosition == i) {
                    foodListAdapter1.halfRb1.setChecked(true);
                } else {
                    foodListAdapter1.halfRb1.setChecked(false);
                    foodListAdapter1.OptionsName1.setTextColor(Color.parseColor("#000000"));
                    foodListAdapter1.Price.setTextColor(Color.parseColor("#000000"));
                }

            }

            @NonNull
            @Override
            public FoodListAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list, parent, false);
                return new FoodListAdapter(view);
            }
        };
        return PlateBottom;
    }

    // Display from firebase
    private void getDetails(final String foodId) {

        foods.child(foodId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Currentfood = snapshot.getValue(Food_Constructor.class);

                //Set Image
                Picasso.get().load(Currentfood.getImage()).into(Food_image);
                foodName.setText(Currentfood.getName());
                foodDes.setText(Currentfood.getDescription());
                /*PriceCurrent.setText(Currentfood.getPrice());*/

                if(snapshot.child("options").exists()){
                    OptionsCheck = true;
                }
                // Get Value from SQLite
                Database db = new Database(foodDetail.this);
                Bundle get = db.GetCount(foodId);
                if(get.getInt("QuantitySent") != 0){
                    DecreaseValue = true;
                    noOfItems = 1;
                    FinalCheck = true;
                    Display(get.getInt("QuantitySent"),get.getInt("TotalSent"));
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}

package com.example.food.AdminSide;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.example.food.AdminSide.MainMenuFragment.AdminMenu;
import com.example.food.AdminSide.Notification.NewOrder;
import com.example.food.AdminSide.Notification.newOrderListIntent;
import com.example.food.AdminSide.Order.OrderDetailsAdmin;
import com.example.food.Common.InternetConnection;
import com.example.food.Common.ProfileFragment.ProfileFragment;
import com.example.food.Common.SessionManager;
import com.example.food.Login.Retailerstart;
import com.example.food.R;
import com.example.food.WorkManager.WorkRequest;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentOnAttachListener;
import androidx.fragment.app.FragmentTransaction;

public class AdminMainActivity extends AppCompatActivity implements newOrderListIntent.Count{

    BottomNavigationView chipNavigationBar;
    FragmentManager fragmentManager;
    boolean OnceMenu,foodListFrag;
    Fragment fragment;
    BadgeDrawable drawable;

    // Fragment plus tag
    AdminMenu adminMenu;
    final String adminMenu1 = "adminMenuadd";

    final String profile = "profileFrag";

    final String Order = "OrderDetails";

    final String foodList = "foodList";
    String fragmentName,CheckOrder;
    Bundle bundle;
    FirebaseDatabase firebaseDatabase;
    InternetConnection connection;
    FragmentContainerView fragment_container_Food;
    WorkRequest workRequest;
    String User;
    private static final String UserType = "AdminTab";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_admin);

        fragment_container_Food = findViewById(R.id.fragment_container_Food);
        fragment_container_Food.setVisibility(View.VISIBLE);
        //Fragment
        fragmentManager = getSupportFragmentManager();

        SessionManager sessionManager =  new SessionManager(getApplication(),SessionManager.SESSION_USERSESSION);
        HashMap<String,String> Data = sessionManager.getUserDetailFromSession();
        User = Data.get(SessionManager.KEY_STAFF);
        if(User.equalsIgnoreCase(UserType)){
            // For Tab
            chipNavigationBar.inflateMenu(R.menu.item_toolbar);
            chipNavigationBar.setSelectedItemId(R.id.menu1);
        }else{
            // For Admin phone
            chipNavigationBar = findViewById(R.id.bottom_nav);
            chipNavigationBar.setSelectedItemId(R.id.menu);
        }

        drawable =  chipNavigationBar.getOrCreateBadge(R.id.cart);
        drawable.setBackgroundColor(Color.RED);

        connection = new InternetConnection(AdminMainActivity.this);

        if(connection.CheckInternet()){
            // Check Internet
            firebaseDatabase = FirebaseDatabase.getInstance();
            firebaseDatabase.getReference("New").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        int Count = (int) snapshot.getChildrenCount();
                        drawable.setNumber(Count);
                        drawable.setVisible(true);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            workRequest = new WorkRequest();
            workRequest.Start();
              Intent intent = new Intent(this, NewOrder.class);
        /*if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startForegroundService(intent);
        else
            startService(intent);*/
        }

        CheckOrder = getIntent().getStringExtra("Notification");

        if(CheckOrder != null){
            bundle = new Bundle();
            bundle.putString("ID",CheckOrder);
            adminMenu = new AdminMenu();
            adminMenu.setArguments(bundle);
        }else{
            adminMenu = new AdminMenu();
        }
        if(!OnceMenu){
            OpenFragment(adminMenu,adminMenu1);
        }
        bottomMenu();

        //Set Options on NavBar
    }

    private void bottomMenu() {

        chipNavigationBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getTitle().toString().equalsIgnoreCase("Profile")){
                    fragment = null;
                    fragment = new ProfileFragment();
                    OpenFragment(fragment,profile);
                }else if(item.getTitle().toString().equalsIgnoreCase("Menu")){
                    fragment = null;
                    fragment = new AdminMenu();
                    if(fragmentManager.findFragmentByTag(profile) != null || fragmentManager.findFragmentByTag(Order) != null){
                        fragmentManager.popBackStack("Above",FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    }
                     OpenFragment(fragment,adminMenu1);
                }else{
                    fragment = null;
                    fragment = new OrderDetailsAdmin();
                    OpenFragment(fragment,Order);
                }
                return true;
            }
        });
    }

    public void OpenFragment(Fragment name, String Tag){
        if(Tag.equals(adminMenu1)) {
            //Put transaction with main name with menu
            if (!OnceMenu) {
                OnceMenu = true;
                fragmentManager.beginTransaction().add(R.id.fragment_container_Food, name, Tag).addToBackStack("Main").setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
            }else{
                fragmentManager.popBackStack("Main",0);
            }
        }else{
            //Put transaction with above name with cart and profile
            fragmentManager.beginTransaction().add(R.id.fragment_container_Food, name,Tag).addToBackStack("Above").setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
        }
        fragmentName = Tag;
    }

    @Override
    protected void onResume() {
        super.onResume();

        fragmentManager.addFragmentOnAttachListener(new FragmentOnAttachListener() {
            @Override
            public void onAttachFragment(@NonNull FragmentManager fragmentManager, @NonNull Fragment fragment) {
                if(fragmentManager.findFragmentByTag(foodList) != null){
                    fragmentName = foodList;
                    foodListFrag = true;
                }
            }
        });

        chipNavigationBar.findViewById(R.id.cart).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("Content","Pause");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("Content","Destroy");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (fragmentName.equals(adminMenu1)){
            finish();
        }else if ((fragmentName.equals(Order)) || (fragmentName.equals(profile))){

            //Set Options on NavBar
            chipNavigationBar.setSelectedItemId(R.id.menu);
            //Remove above fragment with popBackStack
            fragmentManager.popBackStack("Main",0);
            fragmentName = adminMenu1;
        }else if ((foodListFrag) && fragmentName.equals(foodList)) {
            fragmentManager.popBackStack("Main",0);
            fragmentName = adminMenu1;;
            foodListFrag = false;
        }
    }

    public void logout(View view) {
        SessionManager sessionManager = new SessionManager(AdminMainActivity.this, SessionManager.SESSION_USERSESSION);
        sessionManager.logoutUserFromSession();
        //Add menu activity here
        Intent intent = new Intent(AdminMainActivity.this, Retailerstart.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void SetCount(int Number) {
        if(Number > 0 ) {
            drawable.setNumber(Number);
            drawable.setVisible(true);
        }
        else{
            drawable.setNumber(0);
            drawable.setVisible(false);
        }
    }

}
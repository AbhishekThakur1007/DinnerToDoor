package com.example.food.UserSide;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.example.food.Common.ProfileFragment.ProfileFragment;
import com.example.food.Common.SessionManager;
import com.example.food.AdminSide.StaffAdminCommon.pickupList;
import com.example.food.Login.Retailerstart;
import com.example.food.R;
import com.example.food.UserSide.MenuFragment.UserMenuFragment;
import com.example.food.UserSide.TotalItems.FinalOrderDetails;
import com.example.food.UserSide.UserCartFragment.CartFragment;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentOnAttachListener;
import androidx.fragment.app.FragmentTransaction;

public class ClientMainActivity extends AppCompatActivity implements CartFragment.UpdateBottomCount{

    BottomNavigationView chipNavigationBar;
    FloatingActionButton btnCartFinal;
    String  fragmentName,USerOrStaff;
    Fragment fragment;
    FragmentManager fragmentManager;
    boolean OnceMenu;
    public BadgeDrawable drawable;
    private Intent Service;

    // Fragment plus tag
    UserMenuFragment userMenuFragment = new UserMenuFragment();
    private final String retailer = "retailerFrag";

    ProfileFragment profileFragment = new ProfileFragment();
    private final String profile = "profileFrag";

    CartFragment cartFragment = new CartFragment();
    private final String cart = "cartFrag";

    private final String food = "foodFrag";
    boolean foodListFrag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retailer_dashboard);

        // Set Menu option First
        chipNavigationBar = findViewById(R.id.bottom_nav);
        //Set Options on NavBar
        chipNavigationBar.setSelectedItemId(R.id.menu);
        drawable =  chipNavigationBar.getOrCreateBadge(R.id.cart);
        drawable.setBackgroundColor(Color.RED);

        SessionManager sessionManager = new SessionManager(getApplication(),SessionManager.SESSION_USERSESSION);
        HashMap<String,String> Id = sessionManager.getUserDetailFromSession();
        USerOrStaff = Id.get(SessionManager.KEY_STAFF);

        //Fragment
        fragmentManager = getSupportFragmentManager();
        // To cart
        btnCartFinal = findViewById(R.id.btnCart);

        /*itemViewModel = new ViewModelProvider(this).get(ItemViewModel.class);
        itemViewModel.getSelectedItem().observe(this,);*/

        /*Snackbar snackbar = Snackbar.make()*/

        if(USerOrStaff.equalsIgnoreCase("false")){
            Database db = new Database(getBaseContext());
            int i = db.GetTotalCount();
            if(i != 0){
                drawable.setNumber(i);
                drawable.setVisible(true);
            }else{
                drawable.setVisible(false);
            }
        }else{
            btnCartFinal.setVisibility(View.GONE);
            GetOrderID();
        }


        btnCartFinal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ClientMainActivity.this, FinalOrderDetails.class);
                startActivity(intent);
            }
        });

        // TO cart
        if (getIntent().getStringExtra("Extra") != null) {
            if ("CartFragment".equals(getIntent().getStringExtra("Extra"))) {
                String extra = getIntent().getStringExtra("Payment_Mode");
                String Longitude = getIntent().getStringExtra("Longitude");
                String Latitude = getIntent().getStringExtra("Latitude");
                Bundle extraFinal = new Bundle();
                extraFinal.putString("FinalHai", extra);
                extraFinal.putString("Longitude", Longitude);
                extraFinal.putString("Latitude", Latitude);
                CartFragment fragment = new CartFragment();
                fragment.setArguments(extraFinal);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
            }
        }

        // TO Menu From Splash
        else {
            //Get phone number
            if(!OnceMenu){
            OpenFragment(userMenuFragment,retailer);
            }
        }

       /* Service = new Intent(this,Listener.class);
        Listener.getId("1613070667688");
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startForegroundService(Service);
        else{
            startService(Service);
        }*/
        bottomMenu();
    }

    private void bottomMenu() {

        chipNavigationBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getTitle().toString().equals("Profile")){
                    fragment = null;
                    fragment = profileFragment;
                    OpenFragment(fragment,profile);
                }else if(item.getTitle().toString().equals("Menu")){
                    fragment = null;
                    fragment = new UserMenuFragment();
                    if(fragmentManager.findFragmentByTag(profile) != null || fragmentManager.findFragmentByTag(cart) != null){
                        fragmentManager.popBackStack("Above",FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    }
                    OpenFragment(fragment,retailer);
                }else{
                    fragment = null;
                    fragment = cartFragment;
                    OpenFragment(fragment,cart);
                }
                return true;
            }
        });
    }

    public void OpenFragment(Fragment name, String Tag){
        if(Tag.equals(retailer)) {
            //Put transaction with main name with menu
            if (!OnceMenu) {
                OnceMenu = true;
                fragmentManager.beginTransaction().add(R.id.fragment_container, name, Tag).addToBackStack("Main1").setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
            }else{
                fragmentManager.popBackStack("Main1",0);
            }
        }else{
            //Put transaction with above name with cart and profile
            fragmentManager.beginTransaction().add(R.id.fragment_container, name,Tag).addToBackStack("above").setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
        }
        fragmentName = Tag;
    }

    public void logout(View view) {
        SessionManager sessionManager = new SessionManager(ClientMainActivity.this, SessionManager.SESSION_USERSESSION);
        sessionManager.logoutUserFromSession();
        //Add menu activity here
        Intent intent = new Intent(ClientMainActivity.this, Retailerstart.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (fragmentName.equals(retailer)){
            finish();
        }else if ((fragmentName.equals(cart)) || (fragmentName.equals(profile))){

            //Set Options on NavBar
            chipNavigationBar.setSelectedItemId(R.id.menu);
            //Remove above fragment with popBackStack
            fragmentManager.popBackStack("Main1",0);
            fragmentName = retailer;

        }else if ((foodListFrag) && fragmentName.equals(food)) {
            fragmentManager.popBackStack("Main1",0);
            fragmentName = retailer;
            foodListFrag = false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();


        fragmentManager.addFragmentOnAttachListener(new FragmentOnAttachListener() {
            @Override
            public void onAttachFragment(@NonNull FragmentManager fragmentManager, @NonNull Fragment fragment) {

                if(fragmentManager.findFragmentByTag(food) != null){
                        fragmentName = food;
                        foodListFrag = true;
                    }
            }
        });

        if(USerOrStaff.equalsIgnoreCase("false")){
            Database db = new Database(getBaseContext());
            int Total = db.GetTotalCount();
            if(Total > 0){
                drawable.setNumber(Total);
                drawable.setVisible(true);
            }else{
                drawable.setNumber(0);
                drawable.setVisible(false);
            }
        }
    }

    public void GetOrderID() {
        pickupList pick = new pickupList(getBaseContext());
        int  Count =  pick.getCount();
        if(Count != 0){
            drawable.setNumber(Count);
            drawable.setVisible(true);
        }
    }

    @Override
    public void Update(int Count) {
        if(Count > 0){
            drawable.setNumber(Count);
            drawable.setVisible(true);
        }else{
            drawable.setNumber(0);
            drawable.setVisible(false);
        }
    }

}
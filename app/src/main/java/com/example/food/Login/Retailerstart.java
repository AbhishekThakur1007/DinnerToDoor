package com.example.food.Login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.food.R;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

public class Retailerstart extends AppCompatActivity {

    ViewPager viewPager2;
    intoViewPagerAdapter intro;
    TabLayout tabLayout;
    int position;
    Button button,ready;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retailerstart);

        List<ScreenItem> screenItems = new ArrayList<>();
        screenItems.add(new ScreenItem("Fresh Food","Sweet, delicious food with natural herbs and spices like never before.",R.drawable.img1));
        screenItems.add(new ScreenItem("Instant Payment","Pay with our payment partners and earn rewards.",R.drawable.img3));
        screenItems.add(new ScreenItem("Fast Delivery","30 minutes or Free! Free! Free!",R.drawable.img2));

        viewPager2 = findViewById(R.id.viewPager2);
        tabLayout = findViewById(R.id.tabLay);
        button =  findViewById(R.id.NextPager);
        ready =  findViewById(R.id.Ready);

        intro = new intoViewPagerAdapter(getApplicationContext(),screenItems);
        viewPager2.setAdapter(intro);
        tabLayout.setupWithViewPager(viewPager2);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(tab.getPosition() == screenItems.size()-1){
                    Load();
                }else{
                    ready.setVisibility(View.GONE);
                    tabLayout.setVisibility(View.VISIBLE);
                    button.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        ready.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),Start.class);
                startActivity(intent);
                finish();
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                position = viewPager2.getCurrentItem();
                if(position < screenItems.size()){
                    ready.setVisibility(View.GONE);
                    tabLayout.setVisibility(View.VISIBLE);
                    button.setVisibility(View.VISIBLE);
                    position ++;
                    viewPager2.setCurrentItem(position);
                }
                if(position == screenItems.size()-1){
                   Load();
                }
            }
        });
    }

    public void Load(){
        ready.setVisibility(View.VISIBLE);
        tabLayout.setVisibility(View.INVISIBLE);
        button.setVisibility(View.INVISIBLE);
    }


}
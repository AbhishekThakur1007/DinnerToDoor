package com.example.food.Login;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.food.R;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

public class signup4 extends FragmentActivity implements OnMapReadyCallback{

    Button button;
    ImageButton sign_back41;
    GoogleMap googleMap;
    SupportMapFragment fragment;
    TextInputEditText HomeAddress,Village1;
    SearchView searchView;
    RelativeLayout relativeLayout,DialogAddFloat1;
    boolean GpsLocationCheck;
    Animation slide_down, slide_up;
    public static final int REQUEST_CHECK_SETTING = 1001;
    public static final int REQUEST_CHECK = 1002;
    Button Location, Manual;
    ImageButton SetAuto;
    TextInputLayout HouseNumber,Vill;
    private LocationRequest locationRequest;
    FusedLocationProviderClient fusedLocationClient;
    String staffno, LatitudeFinal, LongitudeFinal;
    TextView Heading;
    android.location.Location locationFinal;
    LatLng displayLatLng;
    String nameS2,emailS2,passwordS2,genderS2,dateS2,phoneNo,staff;
    Animation FloatEditTextUp,FloatEditTextDown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup4);

        // Heading Address
        Heading = findViewById(R.id.Gpdgg);

        //Edit Text
        HomeAddress = findViewById(R.id.HomeAddress);
        Village1 = findViewById(R.id.Village);
        //Text Layout
        HouseNumber = findViewById(R.id.plz1);
        Vill = findViewById(R.id.plz2);

        //Save And Proceed to next
        button = findViewById(R.id.NextMap);
        DialogAddFloat1 = findViewById(R.id.LayAddEdit);
        FloatEditTextUp = AnimationUtils.loadAnimation(this,R.anim.top_send_up);
        FloatEditTextDown = AnimationUtils.loadAnimation(this,R.anim.top_send_down);

        //Map
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Gps Automatic Detection Button
        SetAuto = findViewById(R.id.SetAuto);

        // Back
        sign_back41 = findViewById(R.id.sign_back4);
        sign_back41.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // Animation
        slide_down = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);
        slide_up = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);

        //Retrieve Data from SignUp3
        nameS2 = getIntent().getStringExtra("name");
        /*String userS2=  getIntent().getStringExtra("user");*/
        emailS2 = getIntent().getStringExtra("email");
        passwordS2 = getIntent().getStringExtra("password");
        genderS2 = getIntent().getStringExtra("gender");
        dateS2 = getIntent().getStringExtra("date");
        phoneNo = getIntent().getStringExtra("phone");
        staff = getIntent().getStringExtra("staff");
        if (staff != null && staff.equals("kitchenenter")) {
            staffno = getIntent().getStringExtra("Admin");
        }

        // Automatically detect data
        SetAuto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleMap.clear();
                Village1.clearFocus();
                HomeAddress.clearFocus();
                Automatic();
            }
        });

        //Dialog Appears
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Check Edit Text
                if(!Check()){
                    return;
                }else{
                    if(!Check1()){
                        return;
                    }
                }

                Village1.clearFocus();
                HomeAddress.clearFocus();
                searchView.setVisibility(View.VISIBLE);
                SetAuto.setVisibility(View.VISIBLE);
                // Check GPS
                if(GpsLocationCheck){
                    SendToNextActivity();
                }else{
                    DialogGPS();
                }
            }
        });

        setFocus(HomeAddress);


        HomeAddress.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    SetAuto.setVisibility(View.GONE);
                    searchView.setVisibility(View.GONE);
                    GpsLocationCheck = false;
                    button.setText("Save Address");
                }
            }
        });


        HomeAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                HouseNumber.setErrorEnabled(false);
                HouseNumber.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // Remove Error
        Village1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Vill.setErrorEnabled(false);
                Vill.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        Village1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Vill.setErrorEnabled(false);
                SetAuto.setVisibility(View.GONE);
                searchView.setVisibility(View.GONE);
                GpsLocationCheck = false;
                button.setText("Save Address");
            }
        });

        fragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.MapSearch);
        searchView = findViewById(R.id.sv_Location);

        searchView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    DialogAddFloat1.setVisibility(View.GONE);
                    SetAuto.setVisibility(View.GONE);
                }
            }
        });

        fragment.getMapAsync(this);
    }

    private void SendToNextActivity() {
        GpsLocationCheck = false;
        Intent intent = new Intent(signup4.this, otp.class);
        intent.putExtra("name", nameS2);
        intent.putExtra("Village", Objects.requireNonNull(Village1.getText()).toString());
        intent.putExtra("email", emailS2);
        intent.putExtra("password", passwordS2);
        intent.putExtra("gender", genderS2);
        intent.putExtra("date", dateS2);
        intent.putExtra("phone", phoneNo);
        intent.putExtra("staff", staff);
        intent.putExtra("Address", Objects.requireNonNull(HomeAddress.getText()).toString());
        intent.putExtra("whatToDo", "new");
        intent.putExtra("Latitude", LatitudeFinal);
        intent.putExtra("Longitude", LongitudeFinal);
        if (staff.equals("kitchenenter")) {
            intent.putExtra("staffno", staffno);
        }
        startActivity(intent);
    }

    private boolean Check() {
        if(Objects.requireNonNull(HomeAddress.getText()).toString().length() == 0){
            HouseNumber.setError("Please Enter Address");
            setFocus(HomeAddress);
            return false;
        }else{
            HouseNumber.setError(null);
            HouseNumber.setErrorEnabled(false);
            return true;
        }
    }

    private  boolean Check1(){
        if(Objects.requireNonNull(Village1.getText()).toString().length() == 0){
            Vill.setError("Please Enter Village Name");
            setFocus(Village1);
            return false;
        }else{
            Vill.setError(null);
            Vill.setErrorEnabled(false);
            return true;
        }
    }

    // Ask GPS Option
    private void DialogGPS() {

        final Dialog dialog = new Dialog(this);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.setContentView(R.layout.signup4_dialog);
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialog.getWindow().setDimAmount(0.6f);

        Location = dialog.findViewById(R.id.Automatic1);
        Manual = dialog.findViewById(R.id.Manual1);

        Location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Village1.clearFocus();
                HomeAddress.clearFocus();
                Manual();
                OnMapClick();
                Automatic();
                dialog.dismiss();
            }
        });

        Manual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Manual();
                OnMapClick();
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void Automatic() {

        if (ActivityCompat.checkSelfPermission(signup4.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            //Ask for Permission of location
            GpsOnPermission();

        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(signup4.this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(signup4.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CHECK);
            }else{
                ActivityCompat.requestPermissions(signup4.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CHECK);
            }
            }
        }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CHECK) {
            GpsOnPermission();
        }
    }

    @SuppressLint("MissingPermission")
    private void GpsOnPermission() {
        //Get Coordinates


            //GPS On builder
        fusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<android.location.Location>() {
                @Override
                public void onComplete(@NonNull final Task<android.location.Location> task1) {
                    locationRequest = LocationRequest.create();
                    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    locationRequest.setInterval(3000);
                    locationRequest.setFastestInterval(2000);

                    LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
                    builder.setAlwaysShow(true);

                    Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(getApplicationContext()).checkLocationSettings(builder.build());

                    result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
                        @Override
                        public void onComplete(@NonNull Task<LocationSettingsResponse> task) {

                            try {
                                LocationSettingsResponse response = task.getResult(ApiException.class);
                                if(response.getLocationSettingsStates().isGpsPresent()){
                                    googleMap.setMyLocationEnabled(true);
                                    getLocation();
                                }
                            } catch (ApiException e) {
                                switch (e.getStatusCode()) {
                                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                        try {
                                            ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                                            resolvableApiException.startResolutionForResult(signup4.this, REQUEST_CHECK_SETTING);
                                        } catch (IntentSender.SendIntentException sendIntentException) {
                                            sendIntentException.printStackTrace();
                                        }
                                        break;

                                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                        break;
                                }
                            }
                        }

                        private void getLocation() {
                            locationFinal = task1.getResult();
                            if (locationFinal != null) {
                                LatLng latLng = new LatLng(locationFinal.getLatitude(), locationFinal.getLongitude());
                                GetLocationDetails(latLng);
                            } else {
                                GpsOnPermission();
                            }
                        }

                    });
                }
            });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CHECK_SETTING) {
            Toast.makeText(this, "GPS is On", Toast.LENGTH_SHORT).show();
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Do something after 5s = 5000ms
                    Automatic();
                }
            }, 2000);
        }
        else {
            Toast.makeText(this, "GPS needs to be On.", Toast.LENGTH_SHORT).show();
        }
    }

    private void Manual() {

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String location = searchView.getQuery().toString();
                List<Address> addresses = null;

                Geocoder geocoder = new Geocoder(signup4.this);
                try {
                    addresses = geocoder.getFromLocationName(location, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                assert addresses != null;
                Address addressFinal = addresses.get(0);
                LatLng latLng = new LatLng(addressFinal.getLatitude(), addressFinal.getLongitude());
                GetLocationDetails(latLng);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    public void setFocus(TextInputEditText editText){
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        editText.requestFocus();
        imm.showSoftInput(editText,InputMethodManager.SHOW_IMPLICIT);
    }

    private void OnMapClick() {
        //Async map
        fragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final GoogleMap googleMap) {
                googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        GetLocationDetails(latLng);
                    }
                });
            }
        });
    }

    public void GetLocationDetails(LatLng latLng){
        displayLatLng = latLng;
        googleMap.clear();
        LatitudeFinal = String.valueOf(latLng.latitude);
        LongitudeFinal = String.valueOf(latLng.longitude);
        GpsLocationCheck = true;
        MarkerOptions markerOptions = new MarkerOptions();
        if(displayLatLng != null){
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(displayLatLng, 20));
            markerOptions.position(displayLatLng).title("Saved Location");
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
            googleMap.addMarker(markerOptions);
        }
        if(Check1() && Check() && LatitudeFinal.length() != 0 && LongitudeFinal.length() != 0){
            button.setText("Proceed");
        }else{
            button.setText("Save Address");
        }
       }

    @Override
    public void onMapReady(GoogleMap Map) {
        googleMap = Map;
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        googleMap.setBuildingsEnabled(true);
        googleMap.setIndoorEnabled(true);
    }


}
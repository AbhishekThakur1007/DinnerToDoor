package com.example.food.Common;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.food.UserSide.Database;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

public class FullScreenMap extends DialogFragment implements OnMapReadyCallback, View.OnClickListener {

    GoogleMap mMap;
    String Latitude, Longitude, Address;
    private LocationRequest request;
    public static final int REQUEST_GPS_SETTING = 100;
    LatLng displayLatLng;
    FusedLocationProviderClient fusedLocation;
    Location GPSLocation;
    /*public Listener listener;*/
    boolean GPS;
    String AddressGet, VillageGet,UserId;
    ImageButton SetAuto;
    SearchView searchView;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        /*try {
            listener = (Listener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException((context.toString()) + "Must implement Listener");
        }*/
    }

    private void FullScreenMap() {
        // Required empty public constructor
    }

    public void FullScreenMap(String latitude, String longitude, String address,String Id) {
        this.Latitude = latitude;
        this.Longitude = longitude;
        this.Address = address;
        this.UserId = Id;
    }

    public static FullScreenMap newInstance() {
        return new FullScreenMap();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, android.R.style.Widget_Holo_Light);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.activity_signup4, container, false);

        Button Save = view.findViewById(R.id.NextMap);
        // Address
        final TextInputEditText AddressEdit = view.findViewById(R.id.HomeAddress);
        final TextInputEditText VillageEdit = view.findViewById(R.id.Village);

        // Address lay
        final TextInputLayout AddEditLay = view.findViewById(R.id.plz1);
        final TextInputLayout VillageEditLay = view.findViewById(R.id.plz2);

        SetAuto = view.findViewById(R.id.SetAuto);
        searchView = view.findViewById(R.id.sv_Location);
        searchView.setVisibility(View.GONE);
        SetAuto.setVisibility(View.GONE);

        // Change
        TextView Head = view.findViewById(R.id.Gpdgg);
        Head.setText("Change Address");

        fusedLocation = LocationServices.getFusedLocationProviderClient(getContext());

        AddressEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                AddEditLay.setErrorEnabled(false);
                AddEditLay.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // Remove Error
        VillageEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                VillageEditLay.setErrorEnabled(false);
                VillageEditLay.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // Back
        ImageButton Back = view.findViewById(R.id.sign_back4);
        Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                dialog.setTitle("Address Not Changed");
                dialog.setMessage("Continue ?");
                dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        getDialog().dismiss();
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });

        SetAuto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckPermission();
            }
        });

        if (Latitude != null && Longitude != null) {
            int AddressGetP = Address.indexOf("+");
            AddressGet = Address.substring(0, AddressGetP);
            VillageGet = Address.substring(AddressGetP + 1);
            AddressEdit.setText(AddressGet);
            VillageEdit.setText(VillageGet);
            GetLocationDetails(ConvertToLatLng(Double.parseDouble(Latitude), Double.parseDouble(Longitude)));
        }

        Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Objects.requireNonNull(AddressEdit.getText()).toString().length() == 0) {
                    AddEditLay.setError("Please Type Address");
                    setFocus(AddressEdit);
                    AddEditLay.setErrorEnabled(true);
                } else {
                    AddEditLay.setError(null);
                    AddEditLay.setErrorEnabled(false);
                    if (Objects.requireNonNull(VillageEdit.getText()).toString().length() == 0) {
                        VillageEditLay.setError("Village Name Can't be empty.");
                        setFocus(VillageEdit);
                        VillageEditLay.setErrorEnabled(true);
                     }else{
                        VillageEditLay.setError(null);
                        VillageEditLay.setErrorEnabled(false);
                        if(AddressGet.equals(AddressEdit.getText().toString()) && VillageGet.equals(VillageEdit.getText().toString()) ){
                            Toast.makeText(getActivity(),"Same", Toast.LENGTH_SHORT).show();
                            Objects.requireNonNull(getDialog()).dismiss();
                        }else{
                            if(!GPS){
                                DialogGPS();
                            }else{
                                final String FLat = Double.toString(displayLatLng.latitude);
                                final String FLong = Double.toString(displayLatLng.longitude);
                                final String Send = GetAddress(AddressEdit, VillageEdit);
                                DatabaseReference databaseReference =  FirebaseDatabase.getInstance().getReference("Users").child(UserId).child("Address").child("Default");
                                Database database = new Database(getContext());
                                final Bundle bundle = new Bundle();
                                bundle.putString("Lat",FLat);
                                bundle.putString("Long",FLong);
                                bundle.putString("Add",Send);
                                long i = database.UpdateUserData(UserId,2,bundle);
                                if(i > -1){
                                    databaseReference.child("Coordinates").child("Latitude").setValue(FLat);
                                    databaseReference.child("Coordinates").child("Longitude").setValue(FLong);
                                    databaseReference.child("Address").setValue(Send).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(getActivity(),"Address Updated.",Toast.LENGTH_SHORT).show();
                                                    /*listener.attach(FLat,FLong,Send);*/
                                                    getParentFragmentManager().setFragmentResult("Send",bundle);
                                                    Objects.requireNonNull(getDialog()).dismiss();
                                                }
                                            });
                                }
                            }
                        }

                    }
                }
            }
        });

        FragmentManager fm = this.getChildFragmentManager();
        SupportMapFragment map = (SupportMapFragment) fm.findFragmentById(R.id.MapSearch);
        if(Latitude !=  null){
            assert map != null;
            map.getMapAsync(this);
        }

        return view;
    }

    public String GetAddress(TextInputEditText First, TextInputEditText Second) {
        return First.getText().toString().trim() + " + " + Second.getText().toString().trim();
    }

    // Ask GPS Option
    private void DialogGPS() {

        final Dialog dialog = new Dialog(getContext());
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.setContentView(R.layout.signup4_dialog);
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialog.getWindow().setDimAmount(0.6f);

        Button Location = dialog.findViewById(R.id.Automatic1);
        Button Manual = dialog.findViewById(R.id.Manual1);

        Location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Village1.clearFocus();
                HomeAddress.clearFocus();
                Manual();
                OnMapClick();*/
                SetAuto.setVisibility(View.VISIBLE);
                searchView.setVisibility(View.VISIBLE);
                CheckPermission();
                dialog.dismiss();
            }
        });

        Manual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Manual();
                OnMapClick();*/
                SetAuto.setVisibility(View.VISIBLE);
                searchView.setVisibility(View.VISIBLE);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public LatLng ConvertToLatLng(Double Lat, Double Long) {
        return new LatLng(Lat, Long);
    }

    // Current location #1
    private void CheckPermission() {
        if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            CheckGPS();
        } else {
            //Denied
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
            Toast.makeText(requireContext(), "Please Press Ok to get your Current Location for delivery.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override // Current location #2
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 44) {
            Toast.makeText(getContext(), "Access Granted.", Toast.LENGTH_SHORT).show();
            CheckGPS();
        } else {
            Toast.makeText(getContext(), "Access Denied.", Toast.LENGTH_SHORT).show();
        }
    }

    // Current location #3
    private void CheckGPS() {

        //Create Fused Client
        @SuppressLint("MissingPermission") Task<android.location.Location> locationTask = fusedLocation.getLastLocation();

        locationTask.addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull final Task<Location> taskLocation) {
                request = LocationRequest.create();
                request.setInterval(3000);
                request.setFastestInterval(2000);
                request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

                LocationSettingsRequest.Builder builderFinal = new LocationSettingsRequest.Builder().addLocationRequest(request);
                builderFinal.setAlwaysShow(true);

                Task<LocationSettingsResponse> Result = LocationServices.getSettingsClient(getActivity()).checkLocationSettings(builderFinal.build());

                Result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                        try {
                            LocationSettingsResponse response = task.getResult(ApiException.class);
                            if (response.getLocationSettingsStates().isGpsPresent()) {
                                    mMap.setMyLocationEnabled(true);
                                    getLocation();
                                }
                            } catch (ApiException e) {
                                switch (e.getStatusCode()) {
                                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                        try {
                                            ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                                            startIntentSenderForResult(resolvableApiException.getResolution().getIntentSender(),REQUEST_GPS_SETTING,null,0,0,0,null);
                                        } catch (IntentSender.SendIntentException sendIntentException) {
                                            sendIntentException.printStackTrace();
                                        }
                                        break;

                                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                        break;
                                }
                            }
                        }

                        public void getLocation() {
                            GPSLocation = taskLocation.getResult();
                            if(GPSLocation != null){
                                LatLng latLngGPS = new LatLng(GPSLocation.getLatitude(),GPSLocation.getLongitude());
                                mMap.clear();
                                GetLocationDetails(latLngGPS);
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLngGPS, 20));
                                MarkerOptions markerOptions = new MarkerOptions();
                                markerOptions.position(displayLatLng).title("Saved Location");
                                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
                                mMap.addMarker(markerOptions);
                                GPS = true;
                            }else{
                                CheckGPS();
                          }
                        }
                    });

                }
            });

        }

    @Override // Current location #4 if GPS 0ff
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_GPS_SETTING ) {
            Toast.makeText(getContext(), "GPS is On", Toast.LENGTH_SHORT).show();
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Do something after 5s = 5000ms
                    CheckGPS();
                }
            }, 2000);
        } else {
            Toast.makeText(getContext(), "GPS is Off", Toast.LENGTH_SHORT).show();
        }
    }

    // Current location #5  Send LatLng to Display
    public void GetLocationDetails(LatLng latLng){
            displayLatLng = new LatLng(latLng.latitude,latLng.longitude);
    }

    @Override // Set Map Type
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setBuildingsEnabled(true);
        mMap.setIndoorEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(displayLatLng, 20));
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(displayLatLng).title("Saved Location");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        mMap.addMarker(markerOptions);
    }

    public void setFocus(TextInputEditText editText){
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        editText.requestFocus();
        imm.showSoftInput(editText,InputMethodManager.SHOW_IMPLICIT);
    }

    @Override
    public void onClick(View v) {

    }

  /*  public interface Listener{
        void attach(String latitude,String Longitude,String Address);
    }*/

}

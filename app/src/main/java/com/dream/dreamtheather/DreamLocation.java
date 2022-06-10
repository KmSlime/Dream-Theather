package com.dream.dreamtheather;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.dream.dreamtheather.Model.Cinema;
import com.dream.dreamtheather.databinding.ActivityDreamLocationBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.util.List;

public class DreamLocation extends FragmentActivity implements OnMapReadyCallback {
    private static final String TAG = "GoogleMap";
    private GoogleMap mMap;
    private ActivityDreamLocationBinding binding;
    private String cinemaName;

    private String cinemaAddress;
    Cinema cinema;

    double latItude;
    double longItude;

    FirebaseFirestore firebaseFirestore;

    private static final String[] permission = new String[]{
            ACCESS_FINE_LOCATION,
            ACCESS_COARSE_LOCATION
    };

    public DreamLocation(){
        locationPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                result -> {
                    Boolean fineLocationGranted = result.getOrDefault(
                            Manifest.permission.ACCESS_FINE_LOCATION, false);
                    Boolean coarseLocationGranted = result.getOrDefault(
                            Manifest.permission.ACCESS_COARSE_LOCATION,false);
                    if (fineLocationGranted != null && fineLocationGranted) {
                        // Precise location access granted.
                    } else if (coarseLocationGranted != null && coarseLocationGranted) {
                        // Only approximate location access granted.
                    } else {
                        // No location access granted.
                    }
                }
        );
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityDreamLocationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseFirestore = FirebaseFirestore.getInstance();
        Intent getIntent = getIntent();
        cinemaAddress = getIntent.getStringExtra("cinemaAddress");
        cinemaName = getIntent.getStringExtra("cinemaName");


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        //check location permission
        if(!checkPermissionGranted())
            locationPermissionLauncher.launch(permission);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        Geocoder geocoder = new Geocoder(this);
        List<Address> addressList;
        try {
            addressList = geocoder.getFromLocationName(cinemaAddress, 1);

            if (addressList != null) {
                latItude = addressList.get(0).getLatitude();
                longItude = addressList.get(0).getLongitude();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        // Add a marker in Sydney and move the camera
        LatLng location = new LatLng(latItude, longItude);
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(location).title(cinemaName));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 15), 2000, null);
//
//        mMap.setMyLocationEnabled(true);
    }

    ActivityResultLauncher locationPermissionLauncher;

    private boolean checkPermissionGranted() {
        int fine = ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION);
        int coarse = ContextCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION);
        return fine == PackageManager.PERMISSION_GRANTED && coarse == PackageManager.PERMISSION_GRANTED;
    }

    private boolean requiredLocationPermission(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
//            return;
            return true;
        }
        return false;
    }
}

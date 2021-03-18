package com.example.lab_net;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Objects;

public class MapActivity extends AppCompatActivity implements CoordinateListener {

    private static final String TAG = "TESTING ACTIVITY" ;
    FirebaseFirestore db;
    double trialLatitude;
    double trialLongitude;
    private boolean isLocationPermissionGranted = false;


    private void getLocationPermission() {
        Log.d(TAG, "getLocationPermission: START"+ isLocationPermissionGranted);
        if (ContextCompat.checkSelfPermission(Objects.requireNonNull(this.getApplicationContext()),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            isLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    9003);
        }
        Log.d(TAG, "getLocationPermission: END"+ isLocationPermissionGranted);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        isLocationPermissionGranted= false;
        switch (requestCode) {
            case 9003: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    isLocationPermissionGranted = true;

                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        Fragment fragment = new MapFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.map_layout,fragment).commit();

        if (!isLocationPermissionGranted){
            getLocationPermission();
            if (!isLocationPermissionGranted){
                finish();
            }
        }

        db = FirebaseFirestore.getInstance();
        final CollectionReference collectionReference = db.collection("Trials");
        Intent intent = getIntent();
        String trialId = intent.getStringExtra("trialId");


        Button saveLocationButton = (Button) findViewById(R.id.saveLocation);

        saveLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, Object> data = new HashMap<>();

                //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                //put location coordinates into firebase trial
                //trialId, trialLatitude and trialLongitude are here,
                //data.put("Location", )

                /*collectionReference
                        .document(trialId)
                        .set(data)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(MainActivity.this, "Location added", Toast.LENGTH_LONG).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MainActivity.this, "Location not added", Toast.LENGTH_LONG).show();
                            }
                        });*/

                finish();
            }
        });

    }

    @Override
    public void getCoordinates(double latitude, double longitude) {
        this.trialLatitude = latitude;
        this.trialLongitude = longitude;
        Log.d(TAG, "onCreate: " + trialLatitude + trialLongitude);

    }
}
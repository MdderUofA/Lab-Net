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

/**
 * MapActivity generates MapFragment to allow map to be displayed. Allows user to set location for
 * their trial. Acts as bridge between map fragment and rest of the app. Gets perimission to use
 * location.
 * @author gurjogsingh
 * @version 1.0
 * @see MapFragment
 * @see CoordinateListener
 */
public class MapActivity extends AppCompatActivity implements CoordinateListener {

    private static final String TAG = "TESTING ACTIVITY" ;
    private FirebaseFirestore db;
    private double trialLatitude;
    private double trialLongitude;
    private boolean isLocationPermissionGranted = false;
    private Fragment mapFragment;

    /**
     * Checks to see if app has location permission, if not prompts user to give permission. Updates
     * permission boolean.
     * @return void
     */
    private void getLocationPermission() {
        //Permissions implemented with the help from 'Google Services, GPS, and Location Permissions'
        //by CodingWithMitch (09/20/2018, YouTube) - https://www.youtube.com/watch?v=1f4b2-Y_q2A
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

    /**
     * Checks result of the prompt given to the user to give permission. Updates permission boolean.
     * @param requestCode
     * @param permissions
     * @param grantResults
     * @return void
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        //Permissions implemented with the help from 'Google Services, GPS, and Location Permissions'
        //by CodingWithMitch (09/20/2018, YouTube) - https://www.youtube.com/watch?v=1f4b2-Y_q2A
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

    /**
     * Creates MapFragment instance and stores location coordinate in firebase. If permission not
     * given, ends the activity.
     * @param savedInstanceState
     * @return void
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        //Help from 'How to Implement Google Map Inside Fragment in Android Studio | GoogleMap | Android Coding'
        //by Android Coding (09/12/2020, YouTube) - https://www.youtube.com/watch?v=YCFPClPjDIQ
        Fragment fragment = new MapFragment();
        mapFragment = fragment;
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
            /**
             * When user clicks on button, location is uploaded on firebase and activity ends.
             * @param v
             * @return void
             */
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
                                Toast.makeText(Signup.this, "Location added", Toast.LENGTH_LONG).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Signup.this, "Location not added", Toast.LENGTH_LONG).show();
                            }
                        });*/

                Intent intent = new Intent();
                intent.putExtra("latitude", trialLatitude);
                intent.putExtra("longitude", trialLongitude);
                setResult(2, intent);
                Log.d(TAG, "onClick: LAT SENT " + trialLatitude);
                Log.d(TAG, "onClick: LONG SENT " + trialLongitude);
                finish();
            }
        });

    }

    /**
     * Gets coordinates from MapFragment and updates MapActivity coordinate attributes.
     * @param latitude
     * @param longitude
     * @return void
     */
    @Override
    public void getCoordinates(double latitude, double longitude) {
        this.trialLatitude = latitude;
        this.trialLongitude = longitude;
        Log.d(TAG, "onCreate: " + trialLatitude + trialLongitude);

    }

    /**
     * gets Db attribute
     * @return db
     */
    public FirebaseFirestore getDb() {
        return db;
    }

    /**
     * sets Db attribute
     * @param db
     */
    public void setDb(FirebaseFirestore db) {
        this.db = db;
    }

    /**
     * gets TrialLatitude attribute
     * @return trialLatitude
     */
    public double getTrialLatitude() {
        return trialLatitude;
    }

    /**
     * sets TrialLatitude attribute
     * @param trialLatitude
     */
    public void setTrialLatitude(double trialLatitude) {
        this.trialLatitude = trialLatitude;
    }

    /**
     * gets TrialLongitude attribute
     * @return trialLongitude
     */
    public double getTrialLongitude() {
        return trialLongitude;
    }

    /**
     * sets TrialLongitude attribute
     * @param trialLongitude
     */
    public void setTrialLongitude(double trialLongitude) {
        this.trialLongitude = trialLongitude;
    }

    /**
     * gets isLocationPermissionGranted attribute
     * @return isLocationPermissionGranted
     */
    public boolean isLocationPermissionGranted() {
        return isLocationPermissionGranted;
    }

    /**
     * sets isLocationPermissionGranted attribute
     * @param locationPermissionGranted
     */
    public void setLocationPermissionGranted(boolean locationPermissionGranted) {
        isLocationPermissionGranted = locationPermissionGranted;
    }

    /**
     * gets MapFragment attribute
     * @return mapFragment
     */
    public Fragment getMapFragment() {
        return mapFragment;
    }

    /**
     * sets MapFragment attribute
     * @param mapFragment
     */
    public void setMapFragment(Fragment mapFragment) {
        this.mapFragment = mapFragment;
    }
}
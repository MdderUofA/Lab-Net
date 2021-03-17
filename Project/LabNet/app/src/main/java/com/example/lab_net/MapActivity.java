package com.example.lab_net;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
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

public class MapActivity extends AppCompatActivity implements CoordinateListener {

    private static final String TAG = "TESTING ACTIVITY" ;
    FirebaseFirestore db;
    double trialLatitude;
    double trialLongitude;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        Fragment fragment = new MapFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.map_layout,fragment).commit();

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
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;

public class plotLocActivity extends AppCompatActivity implements Serializable {

    private static final String TAG = "TESTING" ;
    private ArrayList<Double> latitude = new ArrayList<>();
    private ArrayList<Double> longitude = new ArrayList<>();
    private ArrayList<String> trialName = new ArrayList<>();
    private String experimentId;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plot_loc);

        Intent intent = getIntent();
        experimentId = intent.getStringExtra("ExperimentId");
        Log.d(TAG, "onCreate: EXPERIMENTID " + experimentId);

        db = FirebaseFirestore.getInstance();

        CollectionReference collectionReference = db.collection("Trials");

        collectionReference.whereEqualTo("ExperimentId", experimentId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    private static final String TAG = "Error";

                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                latitude.add((Double)document.getData().get("Lat"));
                                longitude.add((Double)document.getData().get("Long"));
                                trialName.add(document.getData().get("Title").toString());
                                Log.d(TAG, "onComplete: " + document.getData().get("Lat"));
                                Log.d(TAG, "onComplete: " + document.getData().get("Long"));
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }

                        if (latitude.get(0) == null || longitude.get(0) == null){
                            Toast.makeText(plotLocActivity.this, "No geolocations available", Toast.LENGTH_LONG).show();
                            finish();
                        }

                    }
                });

        Fragment fragment = new plotLocFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("latitude",latitude);
        bundle.putSerializable("longitude", longitude);
        bundle.putSerializable("trialName", trialName);

        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.map_layout_PL,fragment).commit();

    }
}
package com.example.lab_net;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import java.util.Locale;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;


public class ExperimentActivity extends AppCompatActivity {
    private String UserId;
    private String experimentName;
    private Experiment experiment;
    private final String Tag = "Sample";

    // EDIT EXPERIMENT

    Button editExperiment;


    //location
    Geocoder geocoder;
    double longitude;
    double latitude;
    FirebaseFirestore db;

    List<Address> addresses;

    TextView Description, Region, trials, titlee;
    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.experiment_owner_activity);

        //Intent intent = new Intent();

        //experiment = intent.getSerializableExtra(); // get full experiment object after pop up fragment in user so Team 1 will send this hear and we recieve it
        experiment = new Experiment ("1234", "Filtration", "Chemistry Experiment", "Edmonton", 2);
        Description = findViewById(R.id.experimentDescription);
        Region = findViewById(R.id.experimentRegion);
        trials = findViewById(R.id.trialsLabel);
        titlee = findViewById(R.id.experimentTitle);

        // adding to database

        Description.setText(experiment.getDescription());
        Region.setText("Region: " + experiment.getRegion());
        titlee.setText("Title: "+experiment.getTitle());

        db = FirebaseFirestore.getInstance();
        final CollectionReference collectionReference = db.collection("Experiments");

        HashMap <String, Object> data = new HashMap<>();

        data.put("Description", experiment.getDescription());
        data.put("Region", experiment.getRegion());
        data.put("Owner", "I8dQ05Ql11g62T0idONQ");
        data.put("Title", experiment.getTitle());

        String experimentId = collectionReference.document().getId();

        collectionReference
                .document(experimentId)
                .set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(Tag, "Experiment added");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(Tag, "cannot add experiment");
                    }
                });

        editExperiment = findViewById(R.id.editExperimentButton);
        editExperiment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), experimentEditPopUpActivity.class);
                intent.putExtra("title", experiment.getTitle());
                intent.putExtra("description", experiment.getDescription());
                intent.putExtra("region", experiment.getRegion());
                intent.putExtra("minTrials", experiment.getMinTrials());
                startActivity(intent);
            }
        });
    }
}
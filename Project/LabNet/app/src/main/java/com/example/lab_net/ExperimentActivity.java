package com.example.lab_net;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import java.util.Locale;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;


public class ExperimentActivity extends AppCompatActivity {
    private String UserId;
    private String experimentName;
    private Experiment experiment;
    private final String Tag = "Sample";
    private ListView trialList;

    // Trials- make a custom adapter and a class for trial adapter
    private ArrayAdapter<Trial> trialArrayAdapter;
    private ArrayList<Trial> trialDataList;
    private CustomTrialList customTrialList;

    // EDIT EXPERIMENT

    Button editExperiment;
    Button update;


    //location
    Geocoder geocoder;
    double longitude;
    double latitude;
    FirebaseFirestore db;

    List<Address> addresses;
    EditText setTitle, setDescription, setRegion;

    TextView Description, Region, trials, titlee;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.experiment_owner_activity);

        //Intent intent = new Intent();

        //experiment = intent.getSerializableExtra(); // get full experiment object after pop up fragment in user so Team 1 will send this hear and we recieve it
        experiment = new Experiment("2227Ch5GkbyPO8wfFGOG", "newExperiment", "Chemistry Experiment", "Edmonton", 2);
        Description = findViewById(R.id.experimentDescription);
        Region = findViewById(R.id.experimentRegion);
        trials = findViewById(R.id.trialsLabel);
        titlee = findViewById(R.id.experimentTitle);

        // adding to database

        Description.setText(experiment.getDescription());
        Region.setText("Region: " + experiment.getRegion());
        titlee.setText("Title: " + experiment.getTitle());

        db = FirebaseFirestore.getInstance();
        final CollectionReference collectionReference = db.collection("Experiments");

        HashMap<String, Object> data = new HashMap<>();

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

                AlertDialog.Builder settingsBuilder = new AlertDialog.Builder(ExperimentActivity.this);
                View settingsView = getLayoutInflater().inflate(R.layout.edit_experiment_dialog, null);

                setTitle = (EditText) settingsView.findViewById(R.id.editTitle);
                setDescription = (EditText) settingsView.findViewById(R.id.editDescription);
                setRegion = (EditText) settingsView.findViewById(R.id.editRegion);
                update = (Button) settingsView.findViewById(R.id.updateButton);

                settingsBuilder.setView(settingsView);
                AlertDialog setDialog = settingsBuilder.create();
                setDialog.setCanceledOnTouchOutside(true);
                setDialog.show();


                setDescription.setText(experiment.getDescription());
                setTitle.setText(experiment.getTitle());
                setRegion.setText(experiment.getRegion());
                update.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        db = FirebaseFirestore.getInstance();
                        DocumentReference noteRef = db.collection("Experiments")
                                .document(experiment.getExperimentId());
                        noteRef.update(
                                "Title", setTitle.getText().toString(),
                                "Description", setDescription.getText().toString(), "Region", setRegion.getText().toString()
                        ).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getApplicationContext(), "Changed", Toast.LENGTH_LONG).show();
                                    setDialog.dismiss();
                                } else {
                                    Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_LONG).show();

                                }
                            }
                        });
                    }
                });/*                Intent intent = new Intent(getApplicationContext(), experimentEditPopUpActivity.class);
                intent.putExtra("title", experiment.getTitle());
                intent.putExtra("description", experiment.getDescription());
                intent.putExtra("region", experiment.getRegion());
                intent.putExtra("minTrials", experiment.getMinTrials());
                intent.putExtra("experimentId", experimentId);
                startActivity(intent);*/
            }
        });
        trialList = findViewById(R.id.trial_list);

        trialDataList = new ArrayList<>();
        trialArrayAdapter = new CustomTrialList(this, trialDataList);
        trialList.setAdapter(trialArrayAdapter);

        trialDataList.add(new Trial("10"));
        trialDataList.add(new Trial("24"));


        trialArrayAdapter.notifyDataSetChanged();
    }
}

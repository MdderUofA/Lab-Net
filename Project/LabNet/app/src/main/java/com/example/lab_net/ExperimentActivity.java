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
import android.widget.AdapterView;
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

    //Trial
    Button addtrial, addRemoveTrial;
    EditText addtitle, addresult;
    Trial trial;

    //location
    Geocoder geocoder;
    double longitude;
    double latitude;
    FirebaseFirestore db;

    //stats
    Button done, stats;
    List<Integer> resultList = new ArrayList<Integer>();
    List<Address> addresses;
    EditText setTitle, setDescription, setRegion;
    int i, sum = 0;
    TextView meanView;

    TextView Description, Region, trials, titlee;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.experiment_owner_activity);


        //Intent intent = new Intent();

        //experiment = intent.getSerializableExtra(); // get full experiment object after pop up fragment in user so Team 1 will send this hear and we recieve it
        experiment = new Experiment("1JiPynLkFdB7G7kISTZn  ", "newExperiment", "Chemistry Experiment", "Edmonton", 2);
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


        // edit experiment button implementation
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
                });
            }
        });
        trialList = findViewById(R.id.trial_list);
        trialDataList = new ArrayList<>();
        trialArrayAdapter = new CustomTrialList(this, trialDataList);
        trialList.setAdapter(trialArrayAdapter);
        addRemoveTrial = (Button) findViewById(R.id.addRemoveTrialsButton);

        addRemoveTrial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder settingsBuilder = new AlertDialog.Builder(ExperimentActivity.this);
                View settingsView = getLayoutInflater().inflate(R.layout.edit_trial_dialog, null);
                addtrial = (Button) settingsView.findViewById(R.id.addTrial);
                addtitle = (EditText) settingsView.findViewById(R.id.addTitle);
                addresult = (EditText) settingsView.findViewById(R.id.addResult);


                settingsBuilder.setView(settingsView);
                AlertDialog setDialog = settingsBuilder.create();
                setDialog.setCanceledOnTouchOutside(true);
                setDialog.show();

                // add to firebase(trial document) and listview
                addtrial.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String result = addresult.getText().toString();
                        String title = addtitle.getText().toString();
                        resultList.add(Integer.valueOf(result));


                        // add to firebase

                        db = FirebaseFirestore.getInstance();
                        final CollectionReference collectionReference = db.collection("Trials");
                        String trialId = collectionReference.document().getId();
                        trialDataList.add(new Trial(trialId.toString(), ""+title, Integer.valueOf(result)));
                        HashMap<String, Object> data = new HashMap<>();

                        data.put("Title", title);
                        data.put("Result", result);

                        collectionReference
                                .document(trialId)
                                .set(data)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(Tag, "Trial added");
                                        Toast.makeText(ExperimentActivity.this, "Trial added", Toast.LENGTH_LONG).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d(Tag, "cannot add trial");
                                    }
                                });


                        setDialog.dismiss();
                    }
                });
                CollectionReference collectionReference = db.collection("Trials");

                // delete from listview and firebase Trial collection
                trialList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                        //String trialId = collectionReference.document().getId();

                        collectionReference.document(trialDataList.get(position).getId())
                                .delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(ExperimentActivity.this, "Trial Deleted", Toast.LENGTH_LONG).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(ExperimentActivity.this, "Trial not deleted", Toast.LENGTH_LONG).show();
                                    }
                                });
                        trialDataList.remove(position);
                        trialArrayAdapter.notifyDataSetChanged();


                        return false;
                    }


                });


            }
        });
        stats = (Button) findViewById(R.id.statisticsButton);
        stats.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            for(i = 0; i < resultList.size(); i++){
                sum = resultList.get(i) + sum;
            }
            int mean = (sum/(resultList.size()));
            AlertDialog.Builder settingsBuilder = new AlertDialog.Builder(ExperimentActivity.this);
            View settingsView = getLayoutInflater().inflate(R.layout.stats_dialog, null);

            meanView = (TextView) settingsView.findViewById(R.id.mean_view);
            done = (Button) settingsView.findViewById(R.id.updateButton);

            settingsBuilder.setView(settingsView);
            AlertDialog setDialog = settingsBuilder.create();
            setDialog.setCanceledOnTouchOutside(true);
            meanView.setText("Mean: " + mean);
            setDialog.show();

        }
    });

    }
}

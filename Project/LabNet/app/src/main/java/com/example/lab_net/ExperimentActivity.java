package com.example.lab_net;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;

import static android.media.CamcorderProfile.get;


/*public class ExperimentActivity extends AppCompatActivity {
    String trialId, trialTitle;
    Long resultLong;

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
    final String TAG = "Sample";

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
    List<Long> resultList = new ArrayList<Long>();
    List<Address> addresses;
    EditText setTitle, setDescription, setRegion;
    int i;
    double sum = 0, mean, median;
    TextView meanView, medianView;

    // get experiment data from firebase
    String title, description, region;
    TextView Description, Region, trials, titlee;
    String experimentId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.experiment_owner_activity);


        //Intent intent = new Intent();

        //experiment = intent.getSerializableExtra(); // get full experiment object after pop up fragment in user so Team 1 will send this hear and we recieve it
        //experiment = new Experiment("1JiPynLkFdB7G7kISTZn  ", "newExperiment", "Chemistry Experiment", "Edmonton", 2);
        Description = findViewById(R.id.experimentDescription);
        Region = findViewById(R.id.experimentRegion);
        trials = findViewById(R.id.trialsLabel);
        titlee = findViewById(R.id.experimentTitle);

        // adding to database

 Description.setText(experiment.getDescription());
        Region.setText("Region: " + experiment.getRegion());
        titlee.setText("Title: " + experiment.getTitle());


        db = FirebaseFirestore.getInstance();
        CollectionReference collectionReference = db.collection("Experiments");

        HashMap<String, Object> data = new HashMap<>();

        data.put("Description", experiment.getDescription());
        data.put("Region", experiment.getRegion());
        data.put("Owner", "I8dQ05Ql11g62T0idONQ");
        data.put("Title", experiment.getTitle());


        //experimentId = collectionReference.document().getId();
        experimentId = "0J1zACtdJ9wNhrrPsdxf";
        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                for(QueryDocumentSnapshot doc:value){
                    Log.d(TAG, "retreived data");

                    title = (String) doc.getData().get("Title");
                    description = (String) doc.getData().get("Description");
                   // region = (String) doc.getData().get("Region");
                }

            }
        });
        // fill trialDataList from firebase
        db.collection("Trials")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                trialId = document.getId();
                                trialTitle = document.getData().get("Title").toString();
                                resultList = (List<Long>) document.getData().get("Result");
                                trialDataList.add(new Trial(trialId, trialTitle, resultLong));

                            }
                        }
                    }

                });

        trialArrayAdapter.notifyDataSetChanged();



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


                setDescription.setText(description.toString());
                setTitle.setText(title.toString());
                //setRegion.setText(region.toString());

                // setting update button's functionality
                update.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        db = FirebaseFirestore.getInstance();
                        DocumentReference noteRef = db.collection("Experiments")
                                .document(experimentId);
                        noteRef.update(
                                "Title", setTitle.getText().toString(),
                                "Description", setDescription.getText().toString(), "Region", setRegion.getText().toString()
                        ).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getApplicationContext(), "Changed", Toast.LENGTH_LONG).show();
                                    Description.setText("Description: " + description);
                                    titlee.setText("Experiment Title: " + title);
                                    Region.setText("Region: " + region);
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

                // add to firebase(trial document) and listview and resultList
                addtrial.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Long result = Long.valueOf(addresult.getText().toString());
                        String title = addtitle.getText().toString();
                        resultList.add(Long.valueOf(result));


                        // add to firebase

                        db = FirebaseFirestore.getInstance();
                        final CollectionReference collectionReference = db.collection("Trials");
                        String trialId = collectionReference.document().getId();
                       // trialDataList.add(new Trial(trialId.toString(), ""+title, Long.valueOf(result)));
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
                //CollectionReference collectionReference3 = db.collection("Trials");

                // delete from listview and firebase Trial collection
                trialList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                        //String trialId = collectionReference.document().getId();
                        CollectionReference collectionReference3 = db.collection("Trials");
                        trialDataList.remove(position);

                        if(trialDataList != null) {
                            collectionReference3.document(trialDataList.get(position).getId())
                                    .delete()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(ExperimentActivity.this, "Trial Deleted", Toast.LENGTH_LONG).show();
                                            collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
                                                @Override
                                                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                                    resultList.clear();
                                                    for (QueryDocumentSnapshot doc : value) {
                                                        String trialId = doc.getId();
                                                        String trialTitle = (String) doc.getData().get("Title");
                                                        Long resultLong = (Long) doc.getData().get("Result");
                                                        //int resultInt = (Integer) resultLong;
                                                        resultList.add(resultLong);

                                                    }

                                                }
                                            });
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(ExperimentActivity.this, "Trial not deleted", Toast.LENGTH_LONG).show();
                                        }
                                    });
                        }
                        trialArrayAdapter.notifyDataSetChanged();
                        //trialDataList.remove(position);
                        //trialArrayAdapter.notifyDataSetChanged();


                        return false;
                    }


                });


            }
        });
        stats = (Button) findViewById(R.id.statisticsButton);
        stats.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            for (i = 0; i < resultList.size(); i++) {
                sum = resultList.get(i) + sum;
            }
            if (resultList.size() != 0) {
                mean = (sum / (Long.valueOf(resultList.size())));
            } else {
                mean = 0;
            }
            sum = 0;

            // median

            Collections.sort(resultList);
            int mid = 0;
            mid = mid / 2;

    if (resultList.size() % 2 == 0){
                median = (resultList.get(mid-1) + resultList.get(mid)) / 2;
            }
            else {
                median = resultList.get(mid);
            }



            AlertDialog.Builder settingsBuilder = new AlertDialog.Builder(ExperimentActivity.this);
            View settingsView = getLayoutInflater().inflate(R.layout.stats_dialog, null);

            meanView = (TextView) settingsView.findViewById(R.id.mean_view);
            medianView = (TextView) settingsView.findViewById(R.id.median_view);
            done = (Button) settingsView.findViewById(R.id.updateButton);

            settingsBuilder.setView(settingsView);
            AlertDialog setDialog = settingsBuilder.create();
            setDialog.setCanceledOnTouchOutside(true);
            meanView.setText("Mean: " + mean);
            setDialog.show();

        }
    });

    }
}*/


// New Version

public class ExperimentActivity extends AppCompatActivity {
    private ListView trialList;
    private ArrayAdapter<Trial> trialArrayAdapter;
    private ArrayList<Trial> trialDataList;
    private CustomTrialList customTrialList;
    String trialId, trialTitle;
    Long resultLong;

    TextView experiment_title, experiment_description, experiment_region;

    // Experiment
    Experiment experiment;
    String experimentId, experimentTitle, experimentDescription, experimentRegion;

    FirebaseFirestore db;
    Button add_trial_button;
    ImageButton edit_experiment_button;

    //stats
    Button statistics;

    Button questionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.experiment_owner_activity);
        experimentId = "1234";

        trialList = (ListView) findViewById(R.id.trial_list);
        trialDataList = new ArrayList<>();
        trialArrayAdapter = new CustomTrialList(this, trialDataList);
        trialList.setAdapter(trialArrayAdapter);

        db = FirebaseFirestore.getInstance();

        // Fills in trialDataList
        db.collection("Trials")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                trialId = document.getId();
                                trialTitle = document.getData().get("Title").toString();
                                resultLong = (Long) document.getData().get("Result");
                                trialDataList.add(new Trial(trialId, trialTitle));
                            }
                            trialArrayAdapter.notifyDataSetChanged();
                        }
                    }
                });

        // Delete item from trialDataList upon long click
        trialList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                deleteTrial(position);
                return true;

            }
        });
        experiment_title = findViewById(R.id.experimentTitle);
        experiment_description = findViewById(R.id.experimentDescription);
        experiment_region = findViewById(R.id.experimentRegion);

        DocumentReference documentReference = db.collection("Experiments").document( "NqsWogfL0gYCSYXzt40J");

        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if(documentSnapshot.exists()){
                        experimentTitle = documentSnapshot.getData().get("Title").toString();
                        experimentDescription = documentSnapshot.getData().get("Description").toString();
                        experimentRegion = documentSnapshot.getData().get("Region").toString();

                        // set textviews in experiment_owner_activity to experiment details
                        experiment_title.setText(experimentTitle);
                        experiment_description.setText("Description: " + experimentDescription);
                        experiment_region.setText("Region: " + experimentRegion);
                    }
                }
            }
        });



        edit_experiment_button = (ImageButton) findViewById(R.id.editExperimentButton);
        edit_experiment_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editExperiment();
            }
        });

        add_trial_button = (Button) findViewById(R.id.addRemoveTrialsButton);
        add_trial_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTrial();
            }
        });

        statistics = (Button) findViewById(R.id.ownerStatisticsButton);
        statistics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Statistics.class);
                intent.putExtra("trialList", trialDataList);

                startActivity(intent);
            }
        });

        questionButton = (Button) findViewById(R.id.questionAnswerBrowseButton);
        questionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), QuestionsActivity.class);
                i.putExtra("check","OwnerActivity");
                i.putExtra("experimentID", experimentId);
                startActivity(i);
            }
        });


    }

    private void editExperiment() {
        AlertDialog.Builder settingsBuilder = new AlertDialog.Builder(ExperimentActivity.this);
        View settingsView = getLayoutInflater().inflate(R.layout.edit_experiment_dialog, null);

        EditText edit_title = settingsView.findViewById(R.id.editTitle);
        EditText edit_description = settingsView.findViewById(R.id.editDescription);
        EditText edit_region = settingsView.findViewById(R.id.editRegion);

        settingsBuilder.setView(settingsView);
        AlertDialog setDialog = settingsBuilder.create();
        setDialog.setCanceledOnTouchOutside(true);

        setDialog.show();

        edit_title.setText(experimentTitle);
        edit_description.setText(experimentDescription);
        edit_region.setText(experimentRegion);

        Button update_button = (Button) settingsView.findViewById(R.id.updateExperimentButton);
        update_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DocumentReference updateExperimentDoc = db.collection("Experiments").document("NqsWogfL0gYCSYXzt40J");
                updateExperimentDoc.update("Title", edit_title.getText().toString(),
                        "Description", edit_description.getText().toString(),
                        "Region", edit_region.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(ExperimentActivity.this, "Experiment edited", Toast.LENGTH_LONG).show();
                            setDialog.dismiss();
                            /*TextView experiment_title = (TextView) findViewById(R.id.experimentTitle);
                            TextView experiment_description = (TextView) findViewById(R.id.experimentDescription);
                            TextView experiment_region = (TextView) findViewById(R.id.experimentRegion);*/

                            experimentTitle = edit_title.getText().toString();
                            experimentDescription = edit_description.getText().toString();
                            experimentRegion = edit_region.getText().toString();

                            experiment_title.setText(experimentTitle);
                            experiment_description.setText("Description: " + experimentDescription);
                            experiment_region.setText("Region: " + experimentRegion);
                        } else {
                            Toast.makeText(ExperimentActivity.this, "experiment not edited", Toast.LENGTH_LONG).show();
                            setDialog.dismiss();
                        }

                    }
                });
            }

        });
    }

        private void addTrial(){

            Button addTrialButton;
            EditText addTrialTitle, addTrialResult;

            AlertDialog.Builder settingsBuilder = new AlertDialog.Builder(ExperimentActivity.this);
            View settingsView = getLayoutInflater().inflate(R.layout.edit_trial_dialog,null);


            settingsBuilder.setView(settingsView);
            AlertDialog setDialog = settingsBuilder.create();
            setDialog.setCanceledOnTouchOutside(true);
            setDialog.show();

            addTrialButton = (Button) settingsView.findViewById(R.id.addButtonAnswer);
            addTrialTitle = (EditText) settingsView.findViewById(R.id.addAnswer);
            addTrialResult = (EditText) settingsView.findViewById(R.id.addResult);

            final CollectionReference collectionReference = db.collection("Trials");

            addTrialButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Long result = Long.valueOf(addTrialResult.getText().toString());
                    String title = addTrialTitle.getText().toString();

                    // add to firebase
                    String trialId = collectionReference.document().getId();
                    // trialDataList.add(new Trial(trialId.toString(), ""+title, Long.valueOf(result)));
                    HashMap<String, Object> data = new HashMap<>();

                    data.put("Title", title);
                    data.put("Result", result);

                    collectionReference
                            .document(trialId)
                            .set(data)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    //trialDataList.add(new Trial("1234", title, result));
                                    //trialArrayAdapter.notifyDataSetChanged();
                                    Toast.makeText(ExperimentActivity.this, "Trial added", Toast.LENGTH_LONG).show();
                                    setDialog.dismiss();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(ExperimentActivity.this, "Trial not added", Toast.LENGTH_LONG).show();
                                }
                            });


                    setDialog.dismiss();
                }
            });
            collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    trialDataList.clear();
                    for(QueryDocumentSnapshot doc : value){
                        String trialId = doc.getId();
                        String trialTitle = (String) doc.getData().get("Title");
                        Long trialResult = (Long) doc.getData().get("Result");
                        trialDataList.add(new Trial(trialId, trialTitle));
                    }
                    trialArrayAdapter.notifyDataSetChanged();
                }
            });

        }

        public void deleteTrial(int position) {
            Trial trial = trialDataList.get(position);
            trialDataList.remove(position);
            trialArrayAdapter.notifyDataSetChanged();

            db.collection("Trials").document(trial.getId())
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

        }
}


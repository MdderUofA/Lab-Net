package com.example.lab_net;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

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
import static com.google.android.material.internal.ContextUtils.getActivity;

public class ExperimentActivity extends AppCompatActivity {
    private ListView trialList;
    // Count adapters and lists
    private ArrayAdapter<CountTrial> trialArrayAdapter;
    private ArrayList<CountTrial> trialDataList;
    private CustomTrialList customTrialList;

    // Measurement
    private ArrayAdapter<MeasurementTrial> measurementTrialArrayAdapter;
    private ArrayList<MeasurementTrial>  measurementDataList;

    // NonNegative
    private ArrayAdapter<NonNegativeIntegerTrial> nonNegativeIntegerTrialArrayAdapter;
    private ArrayList<NonNegativeIntegerTrial>  nonNegativeDataList;

    // Binomial
    private ArrayAdapter<BinomialTrial> binomialTrialArrayAdapter;
    private ArrayList<BinomialTrial>  binomialDataList;



    String trialId, trialTitle;
    Long resultLong;

    TextView experiment_title, experiment_description, experiment_region;

    // Experiment
    Experiment experiment;
    String experimentId, experimentTitle, experimentDescription, experimentRegion, trialType;

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

        //count
        trialList = (ListView) findViewById(R.id.trial_list);
        trialDataList = new ArrayList<>();
        trialArrayAdapter = new CustomTrialList(this, trialDataList);
        trialList.setAdapter(trialArrayAdapter);

        //Measurement
        measurementDataList = new ArrayList<>();
        measurementTrialArrayAdapter = new MeasurementCustomTrialList(this, measurementDataList);
        trialList.setAdapter(measurementTrialArrayAdapter);

        // Non negative
        nonNegativeDataList = new ArrayList<>();
        nonNegativeIntegerTrialArrayAdapter = new NonNegativeCustomTrialList(this, nonNegativeDataList);
        trialList.setAdapter(nonNegativeIntegerTrialArrayAdapter);

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
                                trialDataList.add(new CountTrial(trialId, trialTitle, resultLong));
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

        DocumentReference documentReference = db.collection("Experiments").document( "FRweuiaELgeTD7cYzpDo");

        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if(documentSnapshot.exists()){
                        experimentTitle = documentSnapshot.getData().get("Title").toString();
                        experimentDescription = documentSnapshot.getData().get("Description").toString();
                        experimentRegion = documentSnapshot.getData().get("Region").toString();

                        //get trialtype to make respective dialog box appear
                        trialType = documentSnapshot.getData().get("TrialType").toString();

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

            Button addTrialButton, getLocationButton;
            EditText addTrialTitle, addTrialResult;

            if(trialType.equals("Count-based") || trialType.equals("Measurement") || trialType.equals("NonNegativeInteger")) {
                
                final CollectionReference collectionReference = db.collection("Trials");
                String trialId = collectionReference.document().getId();

                AlertDialog.Builder settingsBuilder = new AlertDialog.Builder(ExperimentActivity.this);
                View settingsView = getLayoutInflater().inflate(R.layout.edit_trial_dialog, null);
              

                settingsBuilder.setView(settingsView);
                AlertDialog setDialog = settingsBuilder.create();
                setDialog.setCanceledOnTouchOutside(true);
                setDialog.show();


                addTrialButton = (Button) settingsView.findViewById(R.id.addButtonAnswer);
                addTrialTitle = (EditText) settingsView.findViewById(R.id.addAnswer);
                addTrialResult = (EditText) settingsView.findViewById(R.id.addResult);
              
                //Add location button that launches map fragment
                //So needs collection reference to be initialized before this button is clicked
                // (NOT only in addtrialbutton method like before) to allow trialid to go through

                getLocationButton = (Button) settingsView.findViewById(R.id.getLocationButton);

                getLocationButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), MapActivity.class);
                        intent.putExtra("trialId",trialId);
                        startActivity(intent);
                }
            });

       
                addTrialButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Long result = Long.valueOf(addTrialResult.getText().toString());
                        String title = addTrialTitle.getText().toString();

   
                      // add to firebase
                       
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
                        for (QueryDocumentSnapshot doc : value) {
                            String trialId = doc.getId();
                            String trialTitle = (String) doc.getData().get("Title");
                            Long trialResult = (Long) doc.getData().get("Result");
                            trialDataList.add(new CountTrial(trialId, trialTitle, trialResult));
                        }
                        trialArrayAdapter.notifyDataSetChanged();
                    }
                });
            }
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


/*
package com.example.lab_net;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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

public class SubscribedExperimentActivity extends AppCompatActivity {


    private ListView trialList;
    private ArrayAdapter<CountTrial> trialArrayAdapter;
    private ArrayList<CountTrial> trialDataList;
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
    Button statisticsSub;

    Button questionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.experiment_subscribe_activity);
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
                                trialDataList.add(new CountTrial(trialId, trialTitle, resultLong.toString()));
                            }
                            trialArrayAdapter.notifyDataSetChanged();
                        }
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


        add_trial_button = (Button) findViewById(R.id.addRemoveTrialsButton);
        add_trial_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTrial();
            }
        });

        statisticsSub = (Button) findViewById(R.id.subscriberStatisticsButton);
        statisticsSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Statistics.class);
                intent.putExtra("trialList", trialDataList);
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
        AlertDialog.Builder settingsBuilder = new AlertDialog.Builder(SubscribedExperimentActivity.this);
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
                            Toast.makeText(SubscribedExperimentActivity.this, "Experiment edited", Toast.LENGTH_LONG).show();
                            setDialog.dismiss();
                            */
/*TextView experiment_title = (TextView) findViewById(R.id.experimentTitle);
                            TextView experiment_description = (TextView) findViewById(R.id.experimentDescription);
                            TextView experiment_region = (TextView) findViewById(R.id.experimentRegion);*//*


                            experimentTitle = edit_title.getText().toString();
                            experimentDescription = edit_description.getText().toString();
                            experimentRegion = edit_region.getText().toString();

                            experiment_title.setText(experimentTitle);
                            experiment_description.setText("Description: " + experimentDescription);
                            experiment_region.setText("Region: " + experimentRegion);
                        } else {
                            Toast.makeText(SubscribedExperimentActivity.this, "experiment not edited", Toast.LENGTH_LONG).show();
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

        AlertDialog.Builder settingsBuilder = new AlertDialog.Builder(SubscribedExperimentActivity.this);
        View settingsView = getLayoutInflater().inflate(R.layout.edit_trial_dialog,null);


        settingsBuilder.setView(settingsView);
        AlertDialog setDialog = settingsBuilder.create();
        setDialog.setCanceledOnTouchOutside(true);
        setDialog.show();

        addTrialButton = (Button) settingsView.findViewById(R.id.addButtonAnswer);
        addTrialTitle = (EditText) settingsView.findViewById(R.id.addAnswer);
        addTrialResult = (EditText) settingsView.findViewById(R.id.addResult1);

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
                                Toast.makeText(SubscribedExperimentActivity.this, "Trial added", Toast.LENGTH_LONG).show();
                                setDialog.dismiss();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(SubscribedExperimentActivity.this, "Trial not added", Toast.LENGTH_LONG).show();
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
                    trialDataList.add(new CountTrial(trialId, trialTitle, resultLong.toString()));
                }
                trialArrayAdapter.notifyDataSetChanged();
            }
        });

    }

}

*/

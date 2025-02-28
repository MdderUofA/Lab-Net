package com.example.lab_net;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
/**
 * Activity to show the userProfile of a subscribed user.
 * This allows the user to someone else's information, subscribed experiments, and owned experiments.
 */
public class SubscribedUserActivity extends AppCompatActivity {

    public static final String EXPERIMENT_ID_EXTRA = "com.example.lab_net.experiment_activity.id";
    public static final String USER_ID_EXTRA = "com.example.lab_net.user_profile.user_id";
    private final String Tag = "Sample";
    private String userId,firstNameText,lastNameText,emailText,phoneText;
    private FirebaseFirestore db;
    private DocumentReference documentReference;

    private String experimentId;

    private ListView subExpListView, myExpListView;
    private ArrayList<Experiment> myExperimentsDataList;
    private ArrayList<SubscribedExperiment> subscribedExperimentsDataList;
    private ArrayAdapter<Experiment> myExperimentAdapter;
    private ArrayAdapter<SubscribedExperiment> subscribedExperimentsAdapter;
    private TextView usernameTextView, firstNameTextView, lastNameTextView,emailTextView,phoneTextView;

    private String deviceId;

    String experimentTrialType;

    private Button returnToSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscribed_user);

        //initialize the database
        db = FirebaseFirestore.getInstance();
        Intent intent = getIntent();
        userId = intent.getStringExtra(UserProfile.USER_ID_EXTRA);
        documentReference = db.collection("UserProfile").document(userId);

        //initialize the user information
        usernameTextView = (TextView) findViewById(R.id.username);
        firstNameTextView = (TextView) findViewById(R.id.firstName);
        lastNameTextView = (TextView) findViewById(R.id.lastName);
        emailTextView = (TextView) findViewById(R.id.email);
        phoneTextView = (TextView) findViewById(R.id.phone);

        //my experiment variable
        myExpListView = findViewById(R.id.myExpListView);
        myExperimentsDataList = new ArrayList<>();
        myExperimentAdapter = new CustomMyExperimentsList(this, myExperimentsDataList);
        myExpListView.setAdapter(myExperimentAdapter);

        subExpListView = findViewById(R.id.subExpListView);
        subscribedExperimentsDataList = new ArrayList<>();
        subscribedExperimentsAdapter = new CustomSubscribedExperimentList(this, subscribedExperimentsDataList);
        subExpListView.setAdapter(subscribedExperimentsAdapter);

        getUserInfo();
        getMyExperiments();
        getSubscribedExperiments();
        subExpView();
        myExpView();

        deviceId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);

        //initialize button
        returnToSearch = (Button)findViewById(R.id.returnToSearchButton);
        returnToSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent searchIntent = new Intent(SubscribedUserActivity.this,
                        UserProfile.class);
                searchIntent.putExtra(USER_ID_EXTRA, deviceId);
                startActivity(searchIntent);
            }
        });

    }

    /**
     * Get user's subscribed Experiments
     */
    public void getSubscribedExperiments() {
        db.collection("SubscribedExperiments")
                .whereEqualTo("Subscriber", userId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            subscribedExperimentsDataList.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String id = document.getData().get("ExperimentId").toString();
                                String title = document.getData().get("ExperimentTitle").toString();
                                String subscriber = document.getData().get("Subscriber").toString();
                                String type = document.getData().get("TrialType").toString();
                                subscribedExperimentsDataList
                                        .add(new SubscribedExperiment(id,title,subscriber,type));
                            }
                            subscribedExperimentsAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }

    /**
     * Get user's created Experiments.
     */
    public void getMyExperiments() {
        db.collection("Experiments")
                .whereEqualTo("Owner", userId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            myExperimentsDataList.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String experimentId = document.getId();
                                String experimentTitle = document.getData().get("Title").toString();
                                String experimentDescription = document.getData().get("Description").toString();
                                String experimentRegion = document.getData().get("Region").toString();
                                String experimentOwner = document.getData().get("Owner").toString();
                                int experimentMinTrials = Integer.valueOf(document.getData().get("MinTrials").toString());
                                String experimentTrialType = document.getData().get("TrialType").toString();
                                String experimentEnableLocation = document.getData().get("EnableLocation").toString();

                                myExperimentsDataList.add(new Experiment(experimentId,experimentTitle,
                                        experimentDescription,experimentRegion,experimentOwner,experimentMinTrials,experimentTrialType, experimentEnableLocation));
                            }
                            myExperimentAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }

    /**
     * Get user information from the database.
     */
    public void getUserInfo() {
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if(documentSnapshot.exists()){
                        firstNameText = documentSnapshot.getData().get("firstName").toString();
                        lastNameText = documentSnapshot.getData().get("lastName").toString();
                        emailText = documentSnapshot.getData().get("email").toString();
                        phoneText = documentSnapshot.getData().get("phone").toString();

                        usernameTextView.setText(userId);
                        firstNameTextView.setText(firstNameText);
                        lastNameTextView.setText(lastNameText);
                        emailTextView.setText(emailText);
                        phoneTextView.setText(phoneText);
                    }
                }
            }
        });
    }

    /**
     * Display subscribed experiments in the User Profile.
     */
    private void subExpView() {
        subExpListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SubscribedExperiment subsExp = subscribedExperimentsDataList.get(position);
                String checkType = subsExp.getTrialType();
                String expId = subsExp.getId();
                if(checkType.equals("Binomial")){
                    Intent intent = new Intent(SubscribedUserActivity.this, SubscribedBinomialExperimentActivity.class);
                    intent.putExtra(EXPERIMENT_ID_EXTRA,expId);
                    startActivity(intent);
                }
                if(checkType.equals("Count-based")) {
                    Intent intent = new Intent(SubscribedUserActivity.this, SubscribedCountExperimentActivity.class);
                    //intent.putExtra("experimentId",expId);
                    intent.putExtra(EXPERIMENT_ID_EXTRA,expId);
                    startActivity(intent);
                }
                if(checkType.equals("NonNegativeInteger")) {
                    Intent intent = new Intent(SubscribedUserActivity.this, SubscribedNonNegativeExperimentActivity.class);
                    intent.putExtra(EXPERIMENT_ID_EXTRA,expId);
                    startActivity(intent);
                }
                if(checkType.equals("Measurement")) {
                    Intent intent = new Intent(SubscribedUserActivity.this, SubscribedMeasurementExperimentActivity.class);
                    intent.putExtra(EXPERIMENT_ID_EXTRA,expId);
                    startActivity(intent);
                }
            }
        });
    }


    /**
     * Display user created experiments in the User Profile.
     */
    private void myExpView (){
            myExpListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Experiment experiment = myExperimentsDataList.get(position);
                    experimentTrialType = experiment.getTrialType();
                    // add condition to check trial type
                    if(experimentTrialType.equals("Binomial")){
                        Intent intent = new Intent(SubscribedUserActivity.this, SubscribedBinomialExperimentActivity.class);
                        intent.putExtra(EXPERIMENT_ID_EXTRA,experiment.getExperimentId());
                        startActivity(intent);
                    }
                    if(experimentTrialType.equals("Count-based")) {
                        Intent intent = new Intent(SubscribedUserActivity.this, SubscribedCountExperimentActivity.class);
                        intent.putExtra(EXPERIMENT_ID_EXTRA,experiment.getExperimentId());
                        startActivity(intent);
                    }
                    if(experimentTrialType.equals("NonNegativeInteger")) {
                        Intent intent = new Intent(SubscribedUserActivity.this, SubscribedNonNegativeExperimentActivity.class);
                        //intent.putExtra("experimentId", experiment.getExperimentId());
                        intent.putExtra(EXPERIMENT_ID_EXTRA,experiment.getExperimentId());
                        startActivity(intent);
                    }
                    if(experimentTrialType.equals("Measurement")) {
                        Intent intent = new Intent(SubscribedUserActivity.this, SubscribedMeasurementExperimentActivity.class);
                        //intent.putExtra("experimentId", experiment.getExperimentId());
                        intent.putExtra(EXPERIMENT_ID_EXTRA,experiment.getExperimentId());
                        startActivity(intent);
                    }

                }
            });
        }

    }
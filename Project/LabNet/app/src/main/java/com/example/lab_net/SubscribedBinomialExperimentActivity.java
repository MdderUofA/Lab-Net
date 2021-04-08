package com.example.lab_net;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SubscribedBinomialExperimentActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener {

    public static final String EXPERIMENT_ID_EXTRA = "com.example.lab_net.experiment_activity.id";
    private static final String TAG = "TESTING";


    private ListView trialList;
    // Count adapters and lists
    private ArrayAdapter<BinomialTrial> trialArrayAdapter;
    private ArrayList<BinomialTrial> trialDataList;
    private CustomTrialList customTrialList;
    private ArrayList<String> dates;


    Button addTrialDialogButton;

    String trialId, trialTitle;
    String resultLong;
    String owner;
    Boolean isUnlisted;
    TextView experiment_title, experiment_description, experiment_region;

    // Experiment
    Experiment experiment;
    String experimentId, experimentTitle, experimentDescription, experimentRegion, trialType, status;

    FirebaseFirestore db;
    Button add_trial_button;

    //stats
    List<Long> resultList = new ArrayList<Long>();

    EditText addTrialTitle, addTrialResult;

    //location
    private Double trialLatitude;
    private Double trialLongitude;
    private String isLocationEnabled;
    private Boolean trialButtonEnabled = false;


    //side menu
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    private String deviceId;

    Button subscribed_users_button;
    Button subscribeButton;

    Date date;
    String formattedDate;
    SimpleDateFormat simpleDateFormat;
    String getDate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.experiment_subscribe_activity);

        //side menu
        setToolbar();
        deviceId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);

        // apps crashing using this line
        experimentId = getIntent().getStringExtra(EXPERIMENT_ID_EXTRA);
        //experimentId = getIntent().getStringExtra("experimentId");

        //count
        trialList = (ListView) findViewById(R.id.trial_list);
        trialDataList = new ArrayList<>();
        trialArrayAdapter = new CustomBinomialTrialList(this, trialDataList);
        trialList.setAdapter(trialArrayAdapter);
        db = FirebaseFirestore.getInstance();

        dates = new ArrayList<>();

        experiment_title = findViewById(R.id.experimentTitle);
        experiment_description = findViewById(R.id.experimentDescription);
        experiment_region = findViewById(R.id.experimentRegion);

        checkExperimentEnded();
        checkSubscription();

        DocumentReference documentReference = db.collection("Experiments").document(experimentId);

        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.exists()) {
                        experimentTitle = documentSnapshot.getData().get("Title").toString();
                        experimentDescription = documentSnapshot.getData().get("Description").toString();
                        experimentRegion = documentSnapshot.getData().get("Region").toString();
                        owner = documentSnapshot.getData().get("Owner").toString();
                        //get trialtype to make respective dialog box appear
                        trialType = documentSnapshot.getData().get("TrialType").toString();
                        status = documentSnapshot.getData().get("Status").toString();
                        isLocationEnabled = documentSnapshot.getData().get("EnableLocation").toString();

                        // set textviews in experiment_owner_activity to experiment details
                        experiment_title.setText(experimentTitle);
                        experiment_description.setText("Description: " + experimentDescription);
                        experiment_region.setText("Region: " + experimentRegion);
                    }
                }
            }
        });
        // Fills in trialDataList
        //get trials
        db.collection("Trials").whereEqualTo("ExperimentId", experimentId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                trialId = document.getId();
                                trialTitle = document.getData().get("Title").toString();/*
                                trialType = document.getData().get("Title").toString();*/
                                resultLong = (String) document.getData().get("Result");
                                //getDate = (String) document.getData().get("Date");
                                isUnlisted = (Boolean) document.getData().get("isUnlisted");
                                if(!isUnlisted){
                                    trialDataList.add(new BinomialTrial(trialId, trialTitle, resultLong));
                                }
                                dates.add(getDate);

                            }
                            trialArrayAdapter.notifyDataSetChanged();
                        }

                    }
                });

        checkExperimentEnded();

        add_trial_button = (Button) findViewById(R.id.addRemoveTrialsButton);
        add_trial_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTrial();
            }
        });

        subscribed_users_button = (Button) findViewById(R.id.subscribedUsersBrowseButton);
        subscribed_users_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent searchIntent = new Intent(SubscribedBinomialExperimentActivity.this,
                        SearchableListActivity.class);
                searchIntent.putExtra(SearchableList.SEARCHABLE_FILTER_EXTRA,
                        SearchableList.SEARCH_USERS);
                startActivity(searchIntent);
            }
        });

        //check if user already subscribed, button grey out
        //otherwise give option to subscribe
        subscribeButton = (Button) findViewById(R.id.subscribeButton);
        subscribeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String,Object> data = new HashMap<>();
                data.put("ExperimentId",experimentId);
                data.put("ExperimentTitle",experimentTitle);
                data.put("Subscriber",deviceId);
                data.put("TrialType",trialType);
                db.collection("SubscribedExperiments").document().set(data)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(SubscribedBinomialExperimentActivity.this,"Subscribed",Toast.LENGTH_LONG).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(SubscribedBinomialExperimentActivity.this,"Not Subscribed",Toast.LENGTH_LONG).show();
                            }
                        });
                subscribeButton.setEnabled(false);
                subscribeButton.setText("Subscribed");
                add_trial_button.setEnabled(true);
            }
        });
    }

    //side menu created from youtube: Android Navigation Drawer Menu Material Design
    // by Coding With Tea
    private void setToolbar(){
        drawerLayout = findViewById(R.id.subscribe_drawer_layout);
        navigationView = findViewById(R.id.subscribe_nav_view);
        navigationView.bringToFront();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout,
                toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.app_teal));
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.nav_profile:
                Intent profileIntent = new Intent(getApplicationContext(),UserProfile.class);
                profileIntent.putExtra(UserProfile.USER_ID_EXTRA, deviceId);
                startActivity(profileIntent);
                break;
            case R.id.nav_qr:
                //TODO
                break;
            case R.id.nav_statistics:
                if (trialDataList.size() == 0) {
                    Toast.makeText(SubscribedBinomialExperimentActivity.this,
                            "No stats available for this experiment", Toast.LENGTH_LONG).show();
                } else {
                    Intent statsIntent = new Intent(getApplicationContext(), Statistics.class);
                    statsIntent.putExtra("trialDataList", (Serializable) trialDataList);
                    statsIntent.putExtra("check",3);
                    statsIntent.putExtra("expId", experimentId);
                    statsIntent.putExtra("subscribed", true);
                    startActivity(statsIntent);
                }
                break;
            case R.id.nav_graphs:
                //TODO
                break;
            case R.id.nav_locationPlot:
                Intent locationIntent = new Intent(getApplicationContext(), plotLocActivity.class);
                locationIntent.putExtra("ExperimentId", experimentId);
                startActivity(locationIntent);
                break;
            case R.id.nav_qa:
                Intent qaIntent = new Intent(getApplicationContext(), QuestionsActivity.class);
                qaIntent.putExtra("check", "SubscriberActivity");
                qaIntent.putExtra(EXPERIMENT_ID_EXTRA, experimentId);
                startActivity(qaIntent);
                break;
        }
        return true;
    }

    private void checkExperimentEnded() {
        db.collection("Experiments").document(experimentId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            if (documentSnapshot.exists()) {
                                status = documentSnapshot.getData().get("Status").toString();
                                if ("closed".equals(status)) {
                                    subscribeButton.setEnabled(false);
                                    add_trial_button.setEnabled(false);
                                    Toast.makeText(SubscribedBinomialExperimentActivity.this, "Experiment has Ended", Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    }
                });
    }

    private void checkSubscription() {
        db.collection("SubscribedExperiments")
                .whereEqualTo("ExperimentId",experimentId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String subscriber = document.getData().get("Subscriber").toString();
                                if (subscriber.equals(deviceId)) {
                                    subscribeButton.setEnabled(false);
                                    subscribeButton.setText("Subscribed");
                                }
                            }
                        }
                    }
                });
    }

    private void getLocation(String trialId) {
        Intent sendTrialId = new Intent(this, MapActivity.class);
        sendTrialId.putExtra("trialId", trialId);
        startActivityForResult(sendTrialId, 2);
    }

    private void checkLocationReq(){
        Log.d(TAG, "checkLocationReq: ISLOCATIONENABLED " + isLocationEnabled);
        Log.d(TAG, "checkLocationReq: Latitude " + trialLatitude);
        Log.d(TAG, "checkLocationReq: Longitude " + trialLatitude);

        if (isLocationEnabled.equalsIgnoreCase("No")){
            //add_trial_button.setEnabled(true);
            trialButtonEnabled = true;
        } else {
            if ((trialLatitude == null) || (trialLongitude) == null){
                trialButtonEnabled = false;
                addTrialDialogButton.setEnabled(false);
            } else {
                trialButtonEnabled = true;
                addTrialDialogButton.setEnabled(true);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==2)
        {
            trialLatitude = data.getDoubleExtra("latitude", 0);
            trialLongitude = data.getDoubleExtra("longitude", 0);
            Log.d(TAG, "onActivityResult: LAT RECIEVED " + trialLatitude);
            Log.d(TAG, "onActivityResult: LONG RECIEVED " + trialLongitude);
        }
        checkLocationReq();
    }


    //add new trial
    private void addTrial() {
        AlertDialog.Builder settingsBuilder = new AlertDialog.Builder(SubscribedBinomialExperimentActivity.this);
        View settingsView = getLayoutInflater().inflate(R.layout.edit_trial_dialog, null);


        settingsBuilder.setView(settingsView);
        AlertDialog setDialog = settingsBuilder.create();
        setDialog.setCanceledOnTouchOutside(true);
        setDialog.show();

        addTrialDialogButton = (Button) settingsView.findViewById(R.id.addTrial);
        addTrialTitle = (EditText) settingsView.findViewById(R.id.addTrialTitle);
        addTrialResult = (EditText) settingsView.findViewById(R.id.addTrialResult);
        Toast.makeText(SubscribedBinomialExperimentActivity.this, "Enter a pass or fail", Toast.LENGTH_LONG).show();
        if (!trialButtonEnabled){
            addTrialDialogButton.setEnabled(false);
        }

        addTrialTitle.addTextChangedListener(addTextWatcher);
        addTrialResult.addTextChangedListener(addTextWatcher);

        final CollectionReference collectionReference = db.collection("Trials");
        String trialId = collectionReference.document().getId();

        // date
        date = Calendar.getInstance().getTime();
        simpleDateFormat = new SimpleDateFormat("ddMMYYYY", Locale.getDefault());
        formattedDate = simpleDateFormat.format(date);
        dates.add(formattedDate);

        Button getLocationButton = (Button) settingsView.findViewById(R.id.getLocationButton);
        getLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLocation(trialId);
            }
        });

        addTrialDialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String result = addTrialResult.getText().toString();
                String title = addTrialTitle.getText().toString();
                // add to firebase
                HashMap<String, Object> data = new HashMap<>();
                data.put("Title", title);
                data.put("Result", result);
                data.put("ExperimentId", experimentId);
                data.put("Date", formattedDate);
                data.put("isUnlisted", false);
                if ((trialLatitude != null) && (trialLongitude != null)){
                    if ((trialLatitude != 0) && (trialLongitude != 0)){
                        data.put("Lat", trialLatitude);
                        data.put("Long", trialLongitude);
                    }
                }
                collectionReference
                        .document(trialId)
                        .set(data)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                trialDataList.add(new BinomialTrial(trialId, title, result));
                                trialArrayAdapter.notifyDataSetChanged();
                                Toast.makeText(SubscribedBinomialExperimentActivity.this, "Trial added", Toast.LENGTH_LONG).show();
                                setDialog.dismiss();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(SubscribedBinomialExperimentActivity.this, "Trial not added", Toast.LENGTH_LONG).show();
                            }
                        });

                setDialog.dismiss();
            }
        });
    }
    private TextWatcher addTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String checkResult = addTrialResult.getText().toString();
            String checkTitle = addTrialTitle.getText().toString();

            checkLocationReq();
            if (trialButtonEnabled){
                addTrialDialogButton.setEnabled(checkResult.toLowerCase().equals("pass") || checkResult.toLowerCase().equals("fail") && !checkTitle.isEmpty());
            }

        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    @Override
    public void onBackPressed() { }

}


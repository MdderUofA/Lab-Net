package com.example.lab_net;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
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

/**
 * Activity to show the user a Measurement experiment they have subscribed to.
 */
public class SubscribedMeasurementExperimentActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener {

    public static final String EXPERIMENT_ID_EXTRA = "com.example.lab_net.experiment_activity.id";


    private ListView trialList;
    // Count adapters and lists
    private ArrayAdapter<MeasurementTrial> trialArrayAdapter;
    private ArrayList<MeasurementTrial> trialDataList;
    private CustomCountTrialList customCountTrialList;
    private ArrayList<String> dates;


    Button addTrialDialogButton;
    ImageButton saveTrialDialogButton;

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
    private String subUserId;
    private ListView subUsersList;
    private ArrayList<String> subUsersNameList;
    private ArrayList<String> subUsersDataList;
    private ArrayAdapter<String> subUsersArrayAdapter;

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

        experimentId = getIntent().getStringExtra(EXPERIMENT_ID_EXTRA);

        //count
        trialList = (ListView) findViewById(R.id.trial_list);
        trialDataList = new ArrayList<>();
        trialArrayAdapter = new CustomMeasurementTrialList(this, trialDataList);
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
                                getDate = (String) document.getData().get("Date");
                                isUnlisted = (Boolean) document.getData().get("isUnlisted");
                                if(!isUnlisted){
                                    trialDataList.add(new MeasurementTrial(trialId, trialTitle, Double.valueOf(resultLong)));
                                }
                                dates.add(getDate);

                            }
                            trialArrayAdapter.notifyDataSetChanged();
                        }

                    }
                });

        add_trial_button = (Button) findViewById(R.id.addRemoveTrialsButton);
        add_trial_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                trialButtonEnabled = false;
                addTrial();
            }
        });

        subscribed_users_button = (Button) findViewById(R.id.subscribedUsersBrowseButton);
        subscribed_users_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent searchIntent = new Intent(SubscribedMeasurementExperimentActivity.this,
                        SearchableListActivity.class);
                searchIntent.putExtra(SearchableList.SEARCHABLE_FILTER_EXTRA,
                        SearchableList.SEARCH_USERS);
                startActivity(searchIntent);
            }
        });

        subUsersList = (ListView) findViewById(R.id.subscribed_Users_list);
        subUsersDataList = new ArrayList<>();
        subUsersNameList = new ArrayList<>();
        subUsersArrayAdapter = new CustomSubscribedUserList(this, subUsersNameList);
        subUsersList.setAdapter(subUsersArrayAdapter);
        getSubscribedUsers();

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
                                Toast.makeText(SubscribedMeasurementExperimentActivity.this,"Subscribed",Toast.LENGTH_LONG).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(SubscribedMeasurementExperimentActivity.this,"Not Subscribed",Toast.LENGTH_LONG).show();
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
    /**
     * set side menu on subscriber experiment activity
     */
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

    /**
     * Handle clicks on the side menu
     * @param item
     * @return boolean(true or false)
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.nav_profile:
                Intent profileIntent = new Intent(getApplicationContext(),UserProfile.class);
                profileIntent.putExtra(UserProfile.USER_ID_EXTRA, deviceId);
                startActivity(profileIntent);
                break;
            case R.id.nav_qr:
                scanQR();
                break;
            case R.id.nav_statistics:
                if (trialArrayAdapter.getCount() == 0) {
                    Toast.makeText(SubscribedMeasurementExperimentActivity.this,
                            "No stats available for this experiment", Toast.LENGTH_LONG).show();
                } else {
                    Intent statsIntent = new Intent(getApplicationContext(), Statistics.class);
                    statsIntent.putExtra("trialDataList", (Serializable) trialDataList);
                    statsIntent.putExtra("check",1);
                    statsIntent.putExtra("expId", experimentId);
                    statsIntent.putExtra("subscribed", true);
                    startActivity(statsIntent);
                }
                break;
            case R.id.nav_graphs:
                if (trialArrayAdapter.getCount() == 0) {
                    Toast.makeText(SubscribedMeasurementExperimentActivity.this, "No Histograms available for this experiment", Toast.LENGTH_LONG).show();
                } else {
                    Intent intent = new Intent(getApplicationContext(), Histogram.class);
                    intent.putExtra("trialDataList", (Serializable) trialDataList);
                    intent.putExtra("ExperimentId", experimentId);
                    intent.putExtra("dateDataList", (Serializable) dates);
                    intent.putExtra("check", 3);
                    startActivity(intent);
                }
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

    /**
     * checks if the experiment has ended by the user
     */
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
                                    Toast.makeText(SubscribedMeasurementExperimentActivity.this, "Experiment has Ended", Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    }
                });
    }

    /**
     * gets subscribed users from firebase collection called SubscribedExperiments
     */
    private void getSubscribedUsers(){
        db.collection("SubscribedExperiments").whereEqualTo("ExperimentId", experimentId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                subUserId = document.getData().get("Subscriber").toString();

                                db.collection("UserProfile")
                                        .whereEqualTo(FieldPath.documentId(),subUserId).get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                        String userId = document.getId();
                                                        String userFirstName = document.getData().get("firstName").toString();
                                                        String userLastName = document.getData().get("lastName").toString();
                                                        String fullname = userFirstName + " " + userLastName;

                                                        subUsersNameList.add(fullname);
                                                        subUsersDataList.add(userId);
                                                    }
                                                    subUsersArrayAdapter.notifyDataSetChanged();
                                                }
                                            }
                                        });
                            }

                        }

                    }

                });

        subUsersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String subscriber = subUsersDataList.get(position);
                Intent intent = new Intent(SubscribedMeasurementExperimentActivity.this, SubscribedUserActivity.class);
                intent.putExtra(UserProfile.USER_ID_EXTRA, subscriber);
                startActivity(intent);

            }
        });
    }

    /**
     * checks if the user is already a subscriber
     */
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

    /**
     * Launches MapActivity so user can retrieve their device location for experiment. Needs trialId.
     * @param trialId
     */
    private void getLocation(String trialId) {
        Intent sendTrialId = new Intent(this, MapActivity.class);
        sendTrialId.putExtra("trialId", trialId);
        startActivityForResult(sendTrialId, 2);
    }

    private void scanQR() {
        Intent qr = new Intent(this, QRScanner.class);
        startActivityForResult(qr, 3);
    }

    /**
     * Checks to see if experiment requires location, or if latitude and longitude is provided. Based
     * on this it enables/disables the addTrialDialogButton. So user must get location if required, else
     * not a must.
     */
    private void checkLocationReq(){

        if (isLocationEnabled.equalsIgnoreCase("No")){
            //add_trial_button.setEnabled(true);
            trialButtonEnabled = true;
        } else {
            if ((trialLatitude == null) || (trialLongitude) == null){
                trialButtonEnabled = false;
                addTrialDialogButton.setEnabled(false);
                saveTrialDialogButton.setEnabled(false);
            } else {
                String checkTitle = addTrialTitle.getText().toString();
                if (checkTitle.isEmpty()){
                    trialButtonEnabled = false;
                    addTrialDialogButton.setEnabled(false);
                    saveTrialDialogButton.setEnabled(false);
                    saveTrialDialogButton.setImageAlpha(64);
                } else {
                    trialButtonEnabled = true;
                    addTrialDialogButton.setEnabled(true);
                    saveTrialDialogButton.setEnabled(true);
                    saveTrialDialogButton.setImageAlpha(255);
                }
            }
        }
    }

    /**
     * Retrieves and saves location coordinates from MapActivity once user has selected their location.
     * Checks to see if location requirements have been met by calling checkLocationReq.
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==2)
        {
            if (data != null) {
                trialLatitude = data.getDoubleExtra("latitude", 0);
                trialLongitude = data.getDoubleExtra("longitude", 0);
            }
            checkLocationReq();
        } else if(requestCode == 3) {
            if(data!=null) {
                List<String> result = data.getStringArrayListExtra(QRScanner.QR_RESULT_EXTRA);
                if(result.size()<1)
                    return;
                String s1 = result.remove(0);
                String s2 = result.remove(0);
                if(!s2.equals("MEASUREMENT_TRIAL"))
                    return;
                if(s1.equals("CREATE_TRIAL")) {
                    createTrialFromCommands(result);
                }
            }
        }
    }

    /**
     * enables adding trials for experiments
     */
    private void addTrial() {
        AlertDialog.Builder settingsBuilder = new AlertDialog.Builder(SubscribedMeasurementExperimentActivity.this);
        View settingsView = getLayoutInflater().inflate(R.layout.edit_trial_dialog, null);


        settingsBuilder.setView(settingsView);
        AlertDialog setDialog = settingsBuilder.create();
        setDialog.setCanceledOnTouchOutside(true);
        setDialog.show();

        addTrialDialogButton = (Button) settingsView.findViewById(R.id.addTrial);
        saveTrialDialogButton = (ImageButton) settingsView.findViewById(R.id.saveTrialQR);
        addTrialTitle = (EditText) settingsView.findViewById(R.id.addTrialTitle);
        addTrialResult = (EditText) settingsView.findViewById(R.id.addTrialResult);
        Toast.makeText(SubscribedMeasurementExperimentActivity.this, "Enter a double type", Toast.LENGTH_LONG).show();
        if (!trialButtonEnabled){
            addTrialDialogButton.setEnabled(false);
            saveTrialDialogButton.setEnabled(false);
            saveTrialDialogButton.setImageAlpha(64);
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
                                trialDataList.add(new MeasurementTrial(trialId, title, Double.valueOf(result)));
                                trialArrayAdapter.notifyDataSetChanged();
                                Toast.makeText(SubscribedMeasurementExperimentActivity.this, "Trial added", Toast.LENGTH_LONG).show();
                                setDialog.dismiss();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(SubscribedMeasurementExperimentActivity.this, "Trial not added", Toast.LENGTH_LONG).show();
                            }
                        });

                setDialog.dismiss();
            }
        });

        saveTrialDialogButton.setOnClickListener(v -> {
            if(addTrialDialogButton.isEnabled()) {
                Double result = Double.parseDouble(addTrialResult.getText().toString());
                String title = addTrialTitle.getText().toString();
                // add to firebase
                HashMap<String, Object> data = getSkeletonTrial(title, result);


                QRManager.printQRFromString(this, QRManager.toQRString(createTrialCommandsFromMap(data)));

            }
        });
    }

    public List<String> createTrialCommandsFromMap(HashMap<String, Object> data) { // to be written into QR
        List<String> l = new ArrayList<>();

        l.add("CREATE_TRIAL"); // check
        l.add("MEASUREMENT_TRIAL");

        l.add(data.get("Title").toString());
        l.add(data.get("Date").toString());
        l.add(data.get("Result").toString());
        if(data.get("Lat")!=null && data.get("Long")!=null) {
            l.add(data.get("Lat").toString());
            l.add(data.get("Long").toString());
        }
        return l;
    }

    private void createTrialFromCommands(List<String> result) {
        HashMap<String, Object> data = getSkeletonTrial(result.get(0),Long.parseLong(result.get(2)));

        date = Calendar.getInstance().getTime();
        simpleDateFormat = new SimpleDateFormat("ddMMYYYY", Locale.getDefault());
        formattedDate = simpleDateFormat.format(date);
        data.put("Date",formattedDate);

        if(result.size()>3) { // location data exists.
            data.put("Lat",Double.parseDouble(result.get(3)));
            data.put("Long",Double.parseDouble(result.get(4)));
        }
        final CollectionReference collectionReference = db.collection("Trials");
        String trialId = collectionReference.document().getId();

        collectionReference
                .document(trialId)
                .set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        trialDataList.add(new MeasurementTrial(trialId, result.get(0), Double.parseDouble(result.get(2))));
                        trialArrayAdapter.notifyDataSetChanged();
                        Toast.makeText(SubscribedMeasurementExperimentActivity.this, "Trial added", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SubscribedMeasurementExperimentActivity.this, "Trial not added", Toast.LENGTH_LONG).show();
                    }
                });
    }

    /**
     * Creates a skeleton of a Trial for use in the Database.
     * @param title {Optional} The title of the trial
     * @param result {Optional} The result of the trial
     * @return A Map representing the trial as stored in FireBase.
     */
    private HashMap<String, Object> getSkeletonTrial(String title, Object result) {
        // date
        date = Calendar.getInstance().getTime();
        simpleDateFormat = new SimpleDateFormat("ddMMYYYY", Locale.getDefault());
        formattedDate = simpleDateFormat.format(date);

        HashMap<String, Object> data = new HashMap<>();
        if(title!=null) {
            data.put("Title", title);
        }
        if(result!=null) {
            data.put("Result", result);
        }
        data.put("ExperimentId", experimentId);
        data.put("Date", formattedDate);
        data.put("isUnlisted", false);
        if ((trialLatitude != null) && (trialLongitude != null)){
            if ((trialLatitude != 0) && (trialLongitude != 0)){
                data.put("Lat", trialLatitude);
                data.put("Long", trialLongitude);
            }
        }
        return data;
    }

    /**
     * Responsible for the validation of values used for adding trial
     */
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
                addTrialDialogButton.setEnabled(!checkResult.isEmpty() && !checkTitle.isEmpty());
                saveTrialDialogButton.setEnabled(addTrialDialogButton.isEnabled());
                saveTrialDialogButton.setImageAlpha(addTrialDialogButton.isEnabled() ? 255 : 64);
            }

        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

}


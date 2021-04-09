package com.example.lab_net;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import java.util.Locale;

/**
 * Activity for Experiments with Count Trials
 */
public class CountExperimentActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    private ListView trialList, ignoredTrialList, subUsersList;
    public static final String EXPERIMENT_ID_EXTRA = "com.example.lab_net.experiment_activity.id";


    // Count adapters and lists
    private ArrayAdapter<CountTrial> trialArrayAdapter;
    private ArrayList<CountTrial> trialDataList;
    private ArrayAdapter<CountTrial> ignoredTrialArrayAdapter;
    private ArrayList<CountTrial> ignoredTrialDataList;
    private FirebaseFirestore db;

    // experiment
    private String experimentId;
    private String experimentTitle;
    private String experimentDescription;
    private String experimentRegion;
    private String trialType;
    private String owner;


    private Date date;
    private String formattedDate;
    private SimpleDateFormat simpleDateFormat;
    private String getDate;

    // TextViews
    private TextView experiment_title;
    private TextView experiment_description;
    private TextView experiment_region;

    // Trials
    private String trialId;
    private String trialTitle;
    private Long result;
    private Long getResult;
    private Button addTrialDialogButton;
    private EditText addTrialTitle;
    private EditText addTrialResult;

    // edit experiment
    private Button add_new_trial_button;
    private ImageButton edit_experiment_button;

    //side menu
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;

    private Button subscribed_users_button;
    private String subUserId;
    private ArrayList<String> subUsersNameList;
    private ArrayList<String> subUsersDataList;
    private ArrayAdapter<String> subUsersArrayAdapter;

    // date
    private ArrayList<String> dates;

    private Boolean isUnlisted;
    private String status;

    //location
    private Double trialLatitude;
    private Double trialLongitude;
    private String isLocationEnabled;
    private Boolean trialButtonEnabled = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.experiment_owner_activity);

        //side menu
        setToolbar();

        // date
        /*date = Calendar.getInstance().getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-YYYY", Locale.getDefault());
        String formattedDate = simpleDateFormat.format(date);*/
        dates = new ArrayList<>();

        experimentId = getIntent().getStringExtra(EXPERIMENT_ID_EXTRA);
        //experimentId = getIntent().getStringExtra("experimentId");
        experiment_title = findViewById(R.id.experimentTitle);
        experiment_description = findViewById(R.id.experimentDescription);
        experiment_region = findViewById(R.id.experimentRegion);

        trialList = (ListView) findViewById(R.id.trial_list);
        ignoredTrialList = (ListView) findViewById(R.id.ignored_trials_list);
        trialDataList = new ArrayList<>();
        ignoredTrialDataList = new ArrayList<>();
        trialArrayAdapter = new CustomCountTrialList(this, trialDataList);
        ignoredTrialArrayAdapter = new CustomCountTrialList(this, ignoredTrialDataList);

        trialList.setAdapter(trialArrayAdapter);
        ignoredTrialList.setAdapter(ignoredTrialArrayAdapter);
        db = FirebaseFirestore.getInstance();
        add_new_trial_button = (Button) findViewById(R.id.addRemoveTrialsButton);
        edit_experiment_button = (ImageButton) findViewById(R.id.editExperimentButton);

        checkExperimentEnded();
        // get experiment info
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
                        status = documentSnapshot.getData().get("Status").toString();

                        //get trialtype to make respective dialog box appear
                        trialType = documentSnapshot.getData().get("TrialType").toString();

                        // set textviews in experiment_owner_activity to experiment details
                        experiment_title.setText(experimentTitle);
                        experiment_description.setText("Description: " + experimentDescription);
                        experiment_region.setText("Region: " + experimentRegion);

                        isLocationEnabled = documentSnapshot.getData().get("EnableLocation").toString();
                    }
                }
            }
        });

        //get trials
        db.collection("Trials").whereEqualTo("ExperimentId", experimentId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                trialId = document.getId();
                                trialTitle = document.getData().get("Title").toString();
                                trialType = document.getData().get("Title").toString();
                                getResult = (Long) document.getData().get("Result");
                                getDate = document.getData().get("Date").toString();
                                isUnlisted = (Boolean) document.getData().get("isUnlisted");
                                if(isUnlisted){
                                    ignoredTrialDataList.add(new CountTrial(trialId, trialTitle, getResult));
                                }
                                else {
                                    trialDataList.add(new CountTrial(trialId, trialTitle, getResult));
                                }
                                dates.add(getDate);
                            }
                            ignoredTrialArrayAdapter.notifyDataSetChanged();
                            trialArrayAdapter.notifyDataSetChanged();
                        }
                    }
                });

        trialList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder alert = new AlertDialog.Builder(CountExperimentActivity.this);
                alert.setTitle("Alert");
                alert.setMessage("Confirm un-list Trial?");
                alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        moveToUnlisted(position);
                        dialog.dismiss();
                    }
                });
                alert.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alert.show();
            }
        });
        ignoredTrialList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder alert = new AlertDialog.Builder(CountExperimentActivity.this);
                alert.setTitle("Alert");
                alert.setMessage("Confirm move to listed?");
                alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        moveTrial(position);
                        dialog.dismiss();
                    }
                });
                alert.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alert.show();
            }
        });

        edit_experiment_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editExperiment();
            }
        });

        add_new_trial_button.setOnClickListener(new View.OnClickListener() {
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
                Intent searchIntent = new Intent(CountExperimentActivity.this,
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

    }

    /**
     * checks to see if the experiment has already ended
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
                                    add_new_trial_button.setEnabled(false);
                                    edit_experiment_button.setEnabled(false);
                                    Toast.makeText(CountExperimentActivity.this, "Experiment has Ended", Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    }
                });
    }

    //side menu created from youtube: Android Navigation Drawer Menu Material Design
    // by Coding With Tea
    /**
     * set side menu on owner experiment activity
     */
    private void setToolbar(){
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
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
                profileIntent.putExtra(UserProfile.USER_ID_EXTRA, owner);
                startActivity(profileIntent);
                break;
            case R.id.nav_qr:
                //TODO
                break;
            case R.id.nav_statistics:
                if (trialArrayAdapter.getCount() == 0) {
                    Toast.makeText(CountExperimentActivity.this, "No stats available for this experiment", Toast.LENGTH_LONG).show();
                } else {
                    Intent intent = new Intent(getApplicationContext(), Statistics.class);
                    intent.putExtra("trialDataList", (Serializable) trialDataList);
                    intent.putExtra("expId", experimentId);
                    intent.putExtra("check",0);
                    startActivity(intent);
                }
                break;
            case R.id.nav_graphs:
                if (trialArrayAdapter.getCount() == 0) {
                    Toast.makeText(CountExperimentActivity.this, "No Histograms available for this experiment", Toast.LENGTH_LONG).show();
                } else {
                    Intent intent = new Intent(getApplicationContext(), Histogram.class);
                    intent.putExtra("trialDataList", (Serializable) trialDataList);
                    intent.putExtra("dateDataList", (Serializable) dates);
                    intent.putExtra("ExperimentId", experimentId);
                    intent.putExtra("check", 1);
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
                qaIntent.putExtra("check", "OwnerActivity");
                qaIntent.putExtra(EXPERIMENT_ID_EXTRA, experimentId);
                startActivity(qaIntent);
                break;
            case R.id.nav_endExp:
                Toast.makeText(this, "Experiment already " + status, Toast.LENGTH_LONG).show();
           
                if(status.equals("open")) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(CountExperimentActivity.this);
                    alert.setTitle("Alert");
                    alert.setMessage("Confirm end Experiment?");
                    alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            status = "closed";
                            DocumentReference updateExperimentDoc = db.collection("Experiments").document(experimentId);
                            updateExperimentDoc.update("Status", status).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(CountExperimentActivity.this, "Experiment Ended", Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(CountExperimentActivity.this, "Experiment already Ended", Toast.LENGTH_LONG).show();
                                    }

                                }
                            });
                            add_new_trial_button.setEnabled(false);
                            edit_experiment_button.setEnabled(false);
                            dialog.dismiss();
                        }
                    });
                    alert.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    alert.show();
                }

                break;
            case R.id.nav_deleteExp:
                deleteExperiment();
                break;
        }
        return true;
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
                Intent intent = new Intent(CountExperimentActivity.this, SubscribedUserActivity.class);
                intent.putExtra(UserProfile.USER_ID_EXTRA, subscriber);
                startActivity(intent);

            }
        });
    }

    /**
     * Allows owner to edit experiment details(i.e. name, description, region)
     */
    private void editExperiment() {
        AlertDialog.Builder settingsBuilder = new AlertDialog.Builder(CountExperimentActivity.this);
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
                DocumentReference updateExperimentDoc = db.collection("Experiments").document(experimentId);
                updateExperimentDoc.update("Title", edit_title.getText().toString(),
                        "Description", edit_description.getText().toString(),
                        "Region", edit_region.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(CountExperimentActivity.this, "Experiment edited", Toast.LENGTH_LONG).show();
                            setDialog.dismiss();

                            experimentTitle = edit_title.getText().toString();
                            experimentDescription = edit_description.getText().toString();
                            experimentRegion = edit_region.getText().toString();

                            experiment_title.setText(experimentTitle);
                            experiment_description.setText("Description: " + experimentDescription);
                            experiment_region.setText("Region: " + experimentRegion);
                        } else {
                            Toast.makeText(CountExperimentActivity.this, "experiment not edited", Toast.LENGTH_LONG).show();
                            setDialog.dismiss();
                        }

                    }
                });
            }

        });
    }

    /**
     * Launches MapActivity so user can retrieve their device location for experiment. Needs trialId.
     * @param trialId
     * @return void
     */
    private void getLocation(String trialId) {
        Intent sendTrialId = new Intent(this, MapActivity.class);
        sendTrialId.putExtra("trialId", trialId);
        startActivityForResult(sendTrialId, 2);
    }

    /**
     * Checks to see if experiment requires location, or if latitude and longitude is provided. Based
     * on this it enables/disables the addTrialDialogButton. So user must get location if required, else
     * not a must.
     * @return void
     */
    private void checkLocationReq(){

        if (isLocationEnabled.equalsIgnoreCase("No")){
            //add_trial_button.setEnabled(true);
            trialButtonEnabled = true;
        } else {
            if ((trialLatitude == null) || (trialLongitude) == null){
                trialButtonEnabled = false;
                addTrialDialogButton.setEnabled(false);
            } else {
                String checkResult = addTrialResult.getText().toString();
                String checkTitle = addTrialTitle.getText().toString();
                if (checkResult.isEmpty() || checkTitle.isEmpty()){
                    trialButtonEnabled = false;
                    addTrialDialogButton.setEnabled(false);
                } else {
                    trialButtonEnabled = true;
                    addTrialDialogButton.setEnabled(true);
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
     * @return void
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

        }
        checkLocationReq();
    }

    /**
     * enables adding trials for experiments, also calls TextWatcher to validate user input
     */
    private void addTrial() {
        AlertDialog.Builder settingsBuilder = new AlertDialog.Builder(CountExperimentActivity.this);
        View settingsView = getLayoutInflater().inflate(R.layout.edit_trial_dialog, null);


        settingsBuilder.setView(settingsView);
        AlertDialog setDialog = settingsBuilder.create();
        setDialog.setCanceledOnTouchOutside(true);
        setDialog.show();

        addTrialDialogButton = (Button) settingsView.findViewById(R.id.addTrial);
        addTrialTitle = (EditText) settingsView.findViewById(R.id.addTrialTitle);
        addTrialResult = (EditText) settingsView.findViewById(R.id.addTrialResult);
        if (isLocationEnabled.equalsIgnoreCase("No")){
            Toast.makeText(CountExperimentActivity.this,
                    "Enter any integer. Location not required.",
                    Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(CountExperimentActivity.this,
                    "Enter any integer. Location required.",
                    Toast.LENGTH_LONG).show();
        }
        if (!trialButtonEnabled){
            addTrialDialogButton.setEnabled(false);
        }

        addTrialTitle.addTextChangedListener(addTextWatcher);
        addTrialResult.addTextChangedListener(addTextWatcher);

        final CollectionReference collectionReference = db.collection("Trials");
        String trialId = collectionReference.document().getId();

        // get date when trial added
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
                Long result = (Long) Long.valueOf(addTrialResult.getText().toString());
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
                                trialDataList.add(new CountTrial(trialId, title, result));
                                trialArrayAdapter.notifyDataSetChanged();
                                Toast.makeText(CountExperimentActivity.this, "Trial added", Toast.LENGTH_LONG).show();
                                setDialog.dismiss();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(CountExperimentActivity.this, "Trial not added", Toast.LENGTH_LONG).show();
                            }
                        });
                setDialog.dismiss();
            }
        });
    }

    /**
     * Allows owner to permanently delete an experiment they own from the app and firebase
     */
    private void deleteExperiment() {
        final CollectionReference collectionReference = db.collection("Experiments");

        AlertDialog.Builder alert = new AlertDialog.Builder(CountExperimentActivity.this);
        alert.setTitle("Alert");
        alert.setMessage("Confirm delete experiment?");
        alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                collectionReference.document(experimentId)
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(CountExperimentActivity.this, "experiment deleted", Toast.LENGTH_LONG).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(CountExperimentActivity.this, "experiment not deleted", Toast.LENGTH_LONG).show();
                            }
                        });
                dialog.dismiss();
                deleteSubscribedExperiment();
                Intent intent = new Intent(getApplicationContext(), UserProfile.class);
                intent.putExtra(UserProfile.USER_ID_EXTRA, owner);
                startActivity(intent);

            }
        });
        alert.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alert.show();
    }

    /**
     * deletes subscribed experiments when an experiment is deleted by the owner
     */
    private void deleteSubscribedExperiment() {
        CollectionReference collectionReference = db.collection("SubscribedExperiments");
        collectionReference.whereEqualTo("ExperimentId",experimentId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String deleteId = document.getId();
                                collectionReference.document(deleteId).delete();
                            }
                        }
                    }
                });
    }
    /**
     * method responsible for un-listing trials.
     * @param position
     */
    public void moveToUnlisted(int position) {
        CountTrial trial = trialDataList.get(position);
        //isUnlisted = true;
        //trialDataList.remove(position);
        //trialDataList.get(position).setB
        //trialArrayAdapter.notifyDataSetChanged();
        ignoredTrialDataList.add(trialDataList.get(position));
        ignoredTrialArrayAdapter.notifyDataSetChanged();
        trialDataList.remove(position);
        trialArrayAdapter.notifyDataSetChanged();
        isUnlisted = true;
        DocumentReference updateTrialDoc = db.collection("Trials").document(trial.getId());
        updateTrialDoc.update("isUnlisted", isUnlisted).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(CountExperimentActivity.this, "Trial unlisted", Toast.LENGTH_LONG).show();


                } else {
                    Toast.makeText(CountExperimentActivity.this, "Trial not moved", Toast.LENGTH_LONG).show();
                }

            }
        });
       /* db.collection("Trials").document(trial.getId())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(CountExperimentActivity.this, "Trial Deleted", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(CountExperimentActivity.this, "Trial not deleted", Toast.LENGTH_LONG).show();
                    }
                });*/
        db.collection("Trials").whereEqualTo("ExperimentId", experimentId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            trialDataList.clear();
                            ignoredTrialDataList.clear();
                            dates.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                trialId = document.getId();
                                trialTitle = document.getData().get("Title").toString();
                                trialType = document.getData().get("Title").toString();
                                getDate = document.getData().get("Date").toString();
                                result = (Long) document.getData().get("Result");
                                isUnlisted = (Boolean) document.getData().get("isUnlisted");
                                if(isUnlisted) {
                                    ignoredTrialDataList.add(new CountTrial(trialId, trialTitle, Long.valueOf(result)));

                                }
                                else{
                                    trialDataList.add(new CountTrial(trialId, trialTitle, Long.valueOf(result)));

                                }
                                dates.add(getDate);
                            }
                            trialArrayAdapter.notifyDataSetChanged();
                            ignoredTrialArrayAdapter.notifyDataSetChanged();
                        }
                    }
                });

    }

    /**
     * method responsible for listing un-listed
     * @param position
     */
    private void moveTrial(int position) {
        CountTrial trial = ignoredTrialDataList.get(position);
        //isUnlisted = true;
        //trialDataList.remove(position);
        //trialDataList.get(position).setB
        //trialArrayAdapter.notifyDataSetChanged();
        trialDataList.add(ignoredTrialDataList.get(position));
        trialArrayAdapter.notifyDataSetChanged();
        ignoredTrialDataList.remove(position);
        ignoredTrialArrayAdapter.notifyDataSetChanged();
        isUnlisted = false;
        DocumentReference updateTrialDoc = db.collection("Trials").document(trial.getId());
        updateTrialDoc.update("isUnlisted", isUnlisted).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(CountExperimentActivity.this, "Trial listed", Toast.LENGTH_LONG).show();


                } else {
                    Toast.makeText(CountExperimentActivity.this, "Trial not moved", Toast.LENGTH_LONG).show();
                }

            }
        });

        db.collection("Trials").whereEqualTo("ExperimentId", experimentId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            trialDataList.clear();
                            ignoredTrialDataList.clear();
                            dates.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                trialId = document.getId();
                                trialTitle = document.getData().get("Title").toString();
                                trialType = document.getData().get("Title").toString();
                                getDate = document.getData().get("Date").toString();
                                result = (Long) document.getData().get("Result");
                                isUnlisted = (Boolean) document.getData().get("isUnlisted");
                                if(isUnlisted) {
                                    ignoredTrialDataList.add(new CountTrial(trialId, trialTitle, Long.valueOf(result)));

                                }
                                else{
                                    trialDataList.add(new CountTrial(trialId, trialTitle, Long.valueOf(result)));

                                }
                                dates.add(getDate);
                            }
                            trialArrayAdapter.notifyDataSetChanged();
                            ignoredTrialArrayAdapter.notifyDataSetChanged();
                        }
                    }
                });

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
            if(trialButtonEnabled){
                addTrialDialogButton.setEnabled((TextUtils.isDigitsOnly(checkResult))  && !checkTitle.isEmpty() && !checkResult.isEmpty());
            }

        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    /**
     * Disables going back using androids back button
     */
    @Override
    public void onBackPressed() { }


}
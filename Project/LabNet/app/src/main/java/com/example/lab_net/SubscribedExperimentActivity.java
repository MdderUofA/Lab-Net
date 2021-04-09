package com.example.lab_net;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
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

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * An activity meant to handle all types of experiments. It is responsible for UI of the Experiment
 * @author Dhval, Marcus
 */
public class SubscribedExperimentActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener {

    public static final String EXPERIMENT_ID_EXTRA = "com.example.lab_net.experiment_activity.id";


    private ListView trialList;
    // Count adapters and lists
    private ArrayAdapter<Trial> trialArrayAdapter;
    private ArrayList<Trial> trialDataList = new ArrayList<>();

    private ExperimentHandler handler = null;


    private Button addTrialDialogButton;
    private ImageButton saveTrialDialogButton;
    private TextView experimentTitleView, experimentDescriptionView, experimentRegionView;
    private Button addTrialButton;

    //stats

    private EditText addTrialTitle;
    private EditText addTrialResult;
    private Boolean trialButtonEnabled = false;

    //side menu
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private String deviceId;

    private Button subscribed_users_button;
    private Button subscribeButton;
    private String subUserId;
    private ListView subUsersList;
    private ArrayList<String> subUsersDataList;
    private ArrayAdapter<String> subUsersArrayAdapter;

    private Date date;
    private String formattedDate;
    private SimpleDateFormat simpleDateFormat;

    private Double trialLatitude = null; // the currently selected location.
    private Double trialLongitude = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.experiment_subscribe_activity);

        //side menu
        createToolbar();

        deviceId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);

        String experimentId = getIntent().getStringExtra(EXPERIMENT_ID_EXTRA);

        experimentTitleView = findViewById(R.id.experimentTitle);
        experimentDescriptionView = findViewById(R.id.experimentDescription);
        experimentRegionView = findViewById(R.id.experimentRegion);

        initializeFromDatabase(experimentId);
    }

    /**
     * Queries the database and Initializes this experiment from the specified experimentId.
     * This is the first part of the chain of events that will be called to
     * initialize this experiment.
     * @param experimentId The experimentId of the experiment.
     */
    private void initializeFromDatabase(String experimentId) {
        // find the experiment
        DocumentReference ref =
                Utils.collection(DatabaseCollections.EXPERIMENTS).document(experimentId);

        // step 1: get the overall data of the experiment and then build our handler.
        ref.get().addOnCompleteListener((task)-> {

            if (!task.isSuccessful()) {
                Toast.makeText(SubscribedExperimentActivity.this,
                        "Failed to load experiment",Toast.LENGTH_LONG).show();
                return;
            }

            DocumentSnapshot documentSnapshot = task.getResult();
            if (!documentSnapshot.exists()) {
                throw new IllegalStateException("Experiment does not exist in database.");
            }

            // All checks passed, create the experiment.

            handler = createExperimentHandler(documentSnapshot);

            // set text views in experiment_owner_activity to experiment details
            experimentTitleView.setText(handler.getTitle());
            experimentDescriptionView.setText("Description: " + handler.getDescription());
            experimentRegionView.setText("Region: " + handler.getRegion());

            setupListFromDatabase();
        });
    }

    /**
     * Called when the database data has been retrieved and applied.
     */
    private void onAllDataReceived() {
        checkExperimentEnded();
        checkSubscription();

        createTrialsList();

        createUsersList();

        createSubscribeButton();
    }

    private void createSubscribeButton() {
        //check if user already subscribed, button grey out
        //otherwise give option to subscribe
        subscribeButton = (Button) findViewById(R.id.subscribeButton);
        subscribeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String,Object> data = new HashMap<>();
                data.put("ExperimentId",handler.getId());
                data.put("ExperimentTitle",handler.getTitle());
                data.put("Subscriber",deviceId);
                data.put("TrialType",handler.getType());
                Utils.collection(DatabaseCollections.SUBSCRIBED_EXPERIMENTS)
                        .document()
                        .set(data)
                        .addOnSuccessListener((vv)->{
                            Toast.makeText(SubscribedExperimentActivity.this,
                                    "Subscribed",Toast.LENGTH_LONG).show();
                        }).addOnFailureListener((vv)->{
                            Toast.makeText(SubscribedExperimentActivity.this,
                                    "Network error",Toast.LENGTH_LONG).show();
                        });
                subscribeButton.setEnabled(false);
                subscribeButton.setText("Subscribed");
                addTrialButton.setEnabled(true);
            }
        });
    }

    /**
     * Creates the trial list and listeners.
     */
    private void createTrialsList() {
        trialList = (ListView) findViewById(R.id.trial_list);
        trialArrayAdapter = handler.getListAdapter(this, trialDataList);
        trialList.setAdapter(trialArrayAdapter);

        addTrialButton = (Button) findViewById(R.id.addRemoveTrialsButton);
        addTrialButton.setOnClickListener(v -> {
            trialButtonEnabled = false;
            addTrial();
        });
    }

    /**
     * Creates the user list, populates it, and adds listeners.
     */
    private void createUsersList() {
        // TODO: match the format of createTrialsList.
        subscribed_users_button = (Button) findViewById(R.id.subscribedUsersBrowseButton);
        subscribed_users_button.setOnClickListener(v -> {
            Intent searchIntent = new Intent(SubscribedExperimentActivity.this,
                    SearchableListActivity.class);
            searchIntent.putExtra(SearchableList.SEARCHABLE_FILTER_EXTRA,
                    SearchableList.SEARCH_USERS);
            startActivity(searchIntent);
        });

        subUsersList = (ListView) findViewById(R.id.subscribed_Users_list);
        subUsersDataList = new ArrayList<>();
        subUsersArrayAdapter = new CustomSubscribedUserList(this, subUsersDataList);
        subUsersList.setAdapter(subUsersArrayAdapter);

        getSubscribedUsers();

        subUsersList.setOnItemClickListener((parent, view, position, id) -> {
            String subscriber = subUsersDataList.get(position);
            Intent intent = new Intent(SubscribedExperimentActivity.this, SubscribedUserActivity.class);
            intent.putExtra(UserProfile.USER_ID_EXTRA, subscriber);
            startActivity(intent);

        });
    }

    /**
     * Creates the experiment handler for this Experiment baased on the DocumentSnapshot.
     *
     * The experiment handler is responsible for holding backend information about the experiment.
     */
    private ExperimentHandler<?> createExperimentHandler(DocumentSnapshot snap) {
        String type = snap.getData().get("TrialType").toString();
        ExperimentHandler<?> handler = null;

        switch (type) {
            case(ExperimentTypes.COUNT_STRING):
                handler = new CountExperimentHandler();
                break;
            case(ExperimentTypes.BINOMIAL_STRING):
                handler = new BinomialExperimentHandler();
                 break;
            case(ExperimentTypes.MEASUREMENT_STRING):
                handler = new MeasurementExperimentHandler();
                 break;
            case(ExperimentTypes.NON_NEGATIVE_NUMBER_STRING):
                handler = new NonNegativeExperimentHandler();
                 break;
            default:
                throw new IllegalArgumentException("Type "+type+" does not" +
                        " match to a valid experiment type.");
        }

        handler.applyFromSnapshot(snap);

        return handler;
    }

    /**
     * Queries the database and receives the information about trials that exist, then applies
     * the trials.
     */
    private void setupListFromDatabase() {
        // step 2: fill out data that already exists.
        Utils.collection(DatabaseCollections.TRIALS)
                .whereEqualTo("ExperimentId", handler.getId())
                .get()
                .addOnCompleteListener(Utils.applyAll((doc) -> {
                    if(!handler.shouldShow(doc))
                        return;

                    Trial t = handler.trialFromDatabase(doc);
                    trialDataList.add(t);
                }))
                .addOnCompleteListener((o) -> {
                    onAllDataReceived();
                });
    }

    //side menu created from youtube: Android Navigation Drawer Menu Material Design
    // by Coding With Tea
    /**
     * set side menu on subscriber experiment activity
     */
    private void createToolbar(){
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
                //TODO
                break;
            case R.id.nav_statistics:
                if (trialArrayAdapter.getCount() == 0) {
                    Toast.makeText(this,
                            "No stats available for this experiment", Toast.LENGTH_LONG).show();
                } else {
                    Intent statsIntent = new Intent(getApplicationContext(), Statistics.class);
                    statsIntent.putExtra("trialDataList", (Serializable) trialDataList);
                    statsIntent.putExtra(Statistics.STATISTICS_TYPE_EXTRA,handler.getType());
                    statsIntent.putExtra("expId", handler.getId());
                    statsIntent.putExtra("subscribed", true);
                    startActivity(statsIntent);
                }
                break;
            case R.id.nav_graphs:
                if (trialArrayAdapter.getCount() == 0) {
                    Toast.makeText(this, "No Histograms available for this experiment", Toast.LENGTH_LONG).show();
                } else {
                    Intent intent = new Intent(getApplicationContext(), Histogram.class);
                    intent.putExtra("trialDataList", (Serializable) trialDataList);
                    intent.putExtra("ExperimentId", handler.getId());
                    intent.putExtra("check", 3);
                    startActivity(intent);
                }
                break;
            case R.id.nav_locationPlot:
                Intent locationIntent = new Intent(getApplicationContext(), plotLocActivity.class);
                locationIntent.putExtra("ExperimentId", handler.getId());
                startActivity(locationIntent);
                break;

            case R.id.nav_qa:
                Intent qaIntent = new Intent(getApplicationContext(), QuestionsActivity.class);
                qaIntent.putExtra("check", "SubscriberActivity");
                qaIntent.putExtra(EXPERIMENT_ID_EXTRA, handler.getId());
                startActivity(qaIntent);
                break;
        }
        return true;
    }

    /**
     * checks if the experiment has ended by the user
     */
    private void checkExperimentEnded() {
        if(handler.isEnded()) {
            subscribeButton.setEnabled(false);
            addTrialButton.setEnabled(false);
            Toast.makeText(SubscribedExperimentActivity.this,
                    "Experiment has Ended", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * gets subscribed users from firebase collection called SubscribedExperiments
     */
    private void getSubscribedUsers(){
        Utils.collection(DatabaseCollections.SUBSCRIBED_EXPERIMENTS)
                .whereEqualTo("ExperimentId", handler.getId())
                .get()
                .addOnCompleteListener(Utils.applyAll((doc) -> {
                    subUserId = doc.getData().get("Subscriber").toString();
                    subUsersDataList.add(subUserId);
                }))
                .addOnCompleteListener((o) -> {
                    subUsersArrayAdapter.notifyDataSetChanged();
                });
    }

    /**
     * checks if the user is already a subscriber
     */
    private void checkSubscription() {
        Utils.collection(DatabaseCollections.SUBSCRIBED_EXPERIMENTS)
                .whereEqualTo("ExperimentId",handler.getId())
                .get()
                .addOnCompleteListener(Utils.find((doc) -> {
                    final String subscriber = doc.getData().get("Subscriber").toString();
                    return subscriber.equals(deviceId);
                }, (doc) -> { // called if document is found
                    subscribeButton.setEnabled(false);
                    subscribeButton.setText("Subscribed");
                }));

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
     */
    private boolean checkLocationRequirement(){
        return handler.isLocationEnabled() && ((trialLatitude == null) || (trialLongitude) == null);
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
        if(requestCode==2 && data!=null) { // location
                trialLatitude = data.getDoubleExtra("latitude", 0);
                trialLongitude = data.getDoubleExtra("longitude", 0);

        }
        checkLocationRequirement();
    }



    //add new trial
    /**
     * enables adding trials for experiments
     */
    private void addTrial() {
        // first, create the dialog
        AlertDialog.Builder settingsBuilder = new AlertDialog.Builder(this);
        View settingsView = getLayoutInflater().inflate(handler.getAddTrialLayout(), null);

        settingsBuilder.setView(settingsView);
        AlertDialog setDialog = settingsBuilder.create();
        setDialog.setCanceledOnTouchOutside(true);
        setDialog.show();

        addTrialDialogButton = (Button) settingsView.findViewById(R.id.addTrial);
        saveTrialDialogButton = (ImageButton) settingsView.findViewById(R.id.saveTrialQR);
        addTrialTitle = (EditText) settingsView.findViewById(R.id.addTrialTitle);
        addTrialResult = (EditText) settingsView.findViewById(R.id.addTrialResult);
        //Toast.makeText(this, "Enter a double type", Toast.LENGTH_LONG).show();
        if (!trialButtonEnabled){
            addTrialDialogButton.setEnabled(false);
            saveTrialDialogButton.setEnabled(false);
            saveTrialDialogButton.setImageAlpha(64);
        }

        addTrialTitle.addTextChangedListener(addTextWatcher);
        addTrialResult.addTextChangedListener(addTextWatcher);

        final CollectionReference collectionReference = Utils.collection(DatabaseCollections.TRIALS);
        String trialId = collectionReference.document().getId();

        Button getLocationButton = (Button) settingsView.findViewById(R.id.getLocationButton);
        getLocationButton.setOnClickListener(v -> getLocation(trialId));

        addTrialDialogButton.setOnClickListener(v -> {
            String result = addTrialResult.getText().toString();
            String title = addTrialTitle.getText().toString();
            // add to firebase
            HashMap<String, Object> data = getSkeletonTrial(title, result);

            collectionReference
                    .document(trialId)
                    .set(data)
                    .addOnSuccessListener(aVoid -> {
                        Trial t = handler.trialFromMap(data, trialId);
                        trialDataList.add(t);
                        trialArrayAdapter.notifyDataSetChanged();
                        Toast.makeText(SubscribedExperimentActivity.this,
                                "Trial added.", Toast.LENGTH_LONG).show();
                        setDialog.dismiss();
                    })
                    .addOnFailureListener(e -> Toast.makeText(SubscribedExperimentActivity.this,
                            "A network error has occurred.", Toast.LENGTH_LONG).show());

            setDialog.dismiss();
        });

        saveTrialDialogButton.setOnClickListener(v -> {
            String result = addTrialResult.getText().toString();
            String title = addTrialTitle.getText().toString();
            // add to firebase
            HashMap<String, Object> data = getSkeletonTrial(title, result);
            QRManager.printQRFromString(this, QRManager.toQRString(handler.commandsFromMap(data)));
        });
    }

    /**
     * Creates a skeleton of a Trial for use in the Database.
     * @param title {Optional} The title of the trial
     * @param result {Optional} The result of the trial
     * @return A Map representing the trial as stored in FireBase.
     */
    private HashMap<String, Object> getSkeletonTrial(String title, String result) {
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
        data.put("ExperimentId", handler.getId());
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
            trialButtonEnabled = checkRequirements();
            addTrialDialogButton.setEnabled(trialButtonEnabled);
            saveTrialDialogButton.setEnabled(trialButtonEnabled);
            saveTrialDialogButton.setImageAlpha(trialButtonEnabled ? 255 : 64);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private boolean checkRequirements() {
        return checkDialog() && checkLocationRequirement();
    }

    private boolean checkDialog() {
        String checkTitle = addTrialTitle.getText().toString();
        return (!checkTitle.isEmpty() && handler.isResultValid(addTrialResult));
    }

    /**
     * Disables going back using androids back button
     */
    @Override
    public void onBackPressed() { }

}


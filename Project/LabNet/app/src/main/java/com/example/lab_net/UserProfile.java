/**
 * CMPUT 301
 * @version 1.0
 * March 19, 2021
 *
 */
package com.example.lab_net;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.model.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * This class includes the User Profile activity that displays the user information,
 * as well as buttons for editing user info, browsing and creating experiments, and QR codes.
 *
 * @author Vidhi Patel, Qasim Akhtar
 */
public class UserProfile extends AppCompatActivity implements View.OnClickListener {

    private String userId,firstNameText,lastNameText,emailText,phoneText;
    private FirebaseFirestore db;
    private DocumentReference documentReference;

    private String experimentId;

    private ImageButton editUser;
    private Button browse, addExp, qrCode;
    private ListView subExpListView, myExpListView;
    private ArrayList<Experiment> myExperimentsDataList;
    private ArrayAdapter<Experiment> myExperimentAdapter;
    private TextView usernameTextView, firstNameTextView, lastNameTextView,emailTextView,phoneTextView;

    // Make EditTexts in add Experiment global to ensure they aren't empty

    EditText expTitle, expDescription, expRegion, expMinTrials;
    Button create;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        //initialize the database
        db = FirebaseFirestore.getInstance();
        Intent intent = getIntent();
        userId = intent.getStringExtra("UserId");
        documentReference = db.collection("UserProfile").document(userId);

        //initialize the user information
        usernameTextView = (TextView) findViewById(R.id.username);
        firstNameTextView = (TextView) findViewById(R.id.firstName);
        lastNameTextView = (TextView) findViewById(R.id.lastName);
        emailTextView = (TextView) findViewById(R.id.email);
        phoneTextView = (TextView) findViewById(R.id.phone);

        myExpListView = findViewById(R.id.myExpListView);
        myExperimentsDataList = new ArrayList<>();
        myExperimentAdapter = new CustomMyExperimentsList(this, myExperimentsDataList);
        myExpListView.setAdapter(myExperimentAdapter);

        getUserInfo();
        getExperiments();

        //initialize the buttons
        editUser = (ImageButton) findViewById(R.id.editUserInfo);
        editUser.setOnClickListener(this);
        browse = (Button) findViewById(R.id.browseButton);
        browse.setOnClickListener(this);
        addExp = (Button) findViewById(R.id.addExpButton);
        addExp.setOnClickListener(this);
        qrCode = (Button) findViewById(R.id.qrButton);
        qrCode.setOnClickListener(this);

        subExpView();
        myExpView();

    }

    /**
     * Set buttons to their appropriate actions.
     *
     * @author Vidhi Patel
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.editUserInfo:
                editUserDialog();
                break;
            case R.id.browseButton:
                //TODO
                //startActivity(new Intent());
                //should lead to 'search for experiments' activity
                break;
            case R.id.addExpButton:
                addExpDialog();
                break;
            case R.id.qrButton:
                //TODO
                //startActivity(new Intent());
                //should it just lead to camera/scanner?
                break;

        }
    }

    /**
     * Create a dialog with the user information that can be edited.
     * Update the database with the new information, or delete the profile is user requested.
     *
     * @author Vidhi Patel, Qasim Akhtar
     */
    private void editUserDialog() {
        AlertDialog.Builder settingsBuilder = new AlertDialog.Builder(UserProfile.this);
        View settingsView = getLayoutInflater().inflate(R.layout.edit_user_dialog,null);

        EditText setFirstName = (EditText) settingsView.findViewById(R.id.editTextFirstName);
        EditText setLastName = (EditText) settingsView.findViewById(R.id.editTextLastName);
        EditText setEmail = (EditText) settingsView.findViewById(R.id.editTextEmail);
        EditText setPhone = (EditText) settingsView.findViewById(R.id.editTextSettingsPhone);
        Button update = (Button) settingsView.findViewById(R.id.updateButton);
        Button deleteProfile = (Button) settingsView.findViewById(R.id.deleteUserButton);

        settingsBuilder.setView(settingsView);
        AlertDialog setDialog = settingsBuilder.create();
        setDialog.setCanceledOnTouchOutside(true);

        setFirstName.setText(firstNameText);
        setLastName.setText(lastNameText);
        setEmail.setText(emailText);
        setPhone.setText(phoneText);

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String updatedFirst = setFirstName.getText().toString();
                String updatedLast = setLastName.getText().toString();
                String updatedEmail = setEmail.getText().toString();
                String updatedPhone = setPhone.getText().toString();

                documentReference.update(
                        "email", updatedEmail,
                        "firstName", updatedFirst,
                        "lastName", updatedLast,
                        "phone", updatedPhone
                ).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Changes Saved", Toast.LENGTH_LONG).show();
                            setDialog.dismiss();
                            getUserInfo();
                        } else {
                            Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

        deleteProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CollectionReference collectionReference = db.collection("UserProfile");
                collectionReference.document(userId).delete();
                Intent intent1 = new Intent(UserProfile.this, Signup.class);
                startActivity(intent1);
            }
        });

        setDialog.show();
    }

    /**
     * Create a dialog to create a new experiment with title, description, and location.
     * Select types of trials and if location is required.
     *
     * @author Qasim Akhtar
     */
    private void addExpDialog() {
        AlertDialog.Builder addBuilder = new AlertDialog.Builder(UserProfile.this);
        View addView = getLayoutInflater().inflate(R.layout.add_exp_dialog,null);
        final CollectionReference collectionReference = db.collection("Experiments");

        String trialTypes[] = {"Count-based", "Binomial", "Measurement", "NonNegativeInteger"};

        //location spinner
        String enableLocation[] = {"No", "Yes"};

        expTitle = (EditText) addView.findViewById(R.id.addExpTitle);
        expDescription = (EditText) addView.findViewById(R.id.addExpDescription);
        expRegion = (EditText) addView.findViewById(R.id.addExpRegion);
        expMinTrials = addView.findViewById(R.id.addExpMinTrials);
        Spinner dropdown = (Spinner) addView.findViewById(R.id.dropdownTrialType);
        Spinner dropdown2 = (Spinner) addView.findViewById(R.id.dropdownLocation);
        create = (Button) addView.findViewById(R.id.createButton);

        expTitle.addTextChangedListener(addTextWatcher);
        expDescription.addTextChangedListener(addTextWatcher);
        expRegion.addTextChangedListener(addTextWatcher);
        expMinTrials.addTextChangedListener(addTextWatcher);
        create.setEnabled(false);

        addBuilder.setView(addView);
        AlertDialog addDialog = addBuilder.create();
        addDialog.setCanceledOnTouchOutside(true);

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,trialTypes);
        dropdown.setAdapter(adapter);

        ArrayAdapter<String> adapter2 =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,enableLocation);
        dropdown2.setAdapter(adapter2);

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //userprofile collection, have to update experiment list
                String title = expTitle.getText().toString().trim();
                String description = expDescription.getText().toString().trim();
                String region = expRegion.getText().toString().trim();
                int minTrials = Integer.valueOf(expMinTrials.getText().toString());
                String trialType = dropdown.getSelectedItem().toString();
                String enableLocation = dropdown2.getSelectedItem().toString();

                Map<String,Object> data = new HashMap<>();
                data.put("Title",title);
                data.put("Description",description);
                data.put("Region",region);
                data.put("MinTrials",minTrials);
                data.put("TrialType",trialType);
                data.put("Owner",userId);
                data.put("EnableLocation", enableLocation);

                experimentId = collectionReference.document().getId();
                collectionReference.document(experimentId).set(data)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(UserProfile.this, "Experiment created", Toast.LENGTH_LONG).show();
                                addDialog.dismiss();
                                getExperiments();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(UserProfile.this, "Experiment not created", Toast.LENGTH_LONG).show();
                                addDialog.dismiss();
                            }
                        });
            }
        });

        addDialog.show();

    }

    /**
     * The view to display subscribed experiments in the User Profile.
     *
     * @author Vidhi Patel
     */
    private void subExpView() {
        subExpListView = findViewById(R.id.subExpListView);

        String[] test1 = new String[]{
                "Exp1", "Exp2", "Exp3", "Exp4", "Exp5", "Exp6", "Exp7", "Exp8", "Exp9"
        };

        ArrayAdapter<String> subExpAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1, test1);
        subExpListView.setAdapter(subExpAdapter);

        subExpListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //TODO
//                if (position == 0){
//                    Intent intent = new Intent(view.getContext(), Exp1.class);
//                    startActivity(intent);
//                }
//                if (position == 1){
//                    Intent intent = new Intent(view.getContext(), Exp2.class);
//                    startActivity(intent);
//                }
            }
        });
    }

    /**
     * The view to display user created experiments in the User Profile.
     *
     * @author Vidhi Patel
     */
    private void myExpView (){

        myExpListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Experiment experiment = myExperimentsDataList.get(position);
                Intent intent = new Intent(UserProfile.this, ExperimentActivity.class);
                intent.putExtra("ExperimentId", experiment.getExperimentId());
                startActivity(intent);
            }
        });
    }

    /**
     * Get experiments from the database that were created by the user by matching user ID.
     *
     * @author Qasim Akhtar
     */
    public void getExperiments() {
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
                                myExperimentAdapter.notifyDataSetChanged();

                            }
                        }
                    }

                });

    }

    /**
     * Get user information from the database.
     *
     * @author Qasim Akhtar
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
    @Override
    public void onBackPressed() { }

    private TextWatcher addTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String checkTitle = expTitle.getText().toString();
            String checkDescription = expDescription.getText().toString();
            String checkRegion = expRegion.getText().toString();
            String checkMinTrials = expMinTrials.getText().toString();

            create.setEnabled(!checkTitle.isEmpty()
                    && !checkDescription.isEmpty()
                    && !checkRegion.isEmpty()
                    && !checkMinTrials.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
}
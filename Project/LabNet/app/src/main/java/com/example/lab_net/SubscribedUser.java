package com.example.lab_net;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class SubscribedUser extends AppCompatActivity {

    private String userId,firstNameText,lastNameText,emailText,phoneText;
    private FirebaseFirestore db;
    private DocumentReference documentReference;

    private ListView subExpListView, myExpListView;
    private ArrayList<Experiment> myExperimentsDataList;
    private ArrayAdapter<Experiment> myExperimentAdapter;
    private TextView usernameTextView, firstNameTextView, lastNameTextView,emailTextView,phoneTextView;

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

        myExpListView = findViewById(R.id.myExpListView);
        myExperimentsDataList = new ArrayList<>();
        myExperimentAdapter = new CustomMyExperimentsList(this, myExperimentsDataList);
        myExpListView.setAdapter(myExperimentAdapter);

        getUserInfo();
        getExperiments();
        subExpView();
        myExpView();

        //initialize button
        returnToSearch = (Button)findViewById(R.id.returnToSearchButton);
        returnToSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent searchIntent = new Intent(SubscribedUser.this,
                        SearchableListActivity.class);
                searchIntent.putExtra(SearchableList.SEARCHABLE_FILTER_EXTRA,
                        SearchableList.SEARCH_USERS);
                startActivity(searchIntent);
            }
        });



    }

    /**
     * Get experiments from the database that were created by the user by matching user ID.
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
     * The view to display subscribed experiments in the User Profile.
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
     */
    private void myExpView (){
        myExpListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Experiment experiment = myExperimentsDataList.get(position);
                Intent intent = new Intent(SubscribedUser.this, ExperimentActivity.class);
                intent.putExtra(ExperimentActivity.EXPERIMENT_ID_EXTRA, experiment.getExperimentId());
                startActivity(intent);
            }
        });
    }
}
package com.example.lab_net;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.model.Document;
//import com.google.firebase.database.ValueEventListener;

public class UserProfile extends AppCompatActivity implements View.OnClickListener {

    private User user;
    private FirebaseFirestore db;

    private ImageButton editUser;
    private Button browse, addExp, qrCode;
    private ListView subExpListView, myExpListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        db = FirebaseFirestore.getInstance();
        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("User");

        final TextView usernameTextView = (TextView) findViewById(R.id.username);
        final TextView firstNameTextView = (TextView) findViewById(R.id.firstName);
        final TextView lastNameTextView = (TextView) findViewById(R.id.lastName);
        final TextView emailTextView = (TextView) findViewById(R.id.email);
        final TextView phoneTextView = (TextView) findViewById(R.id.phone);

        usernameTextView.setText(user.getUserId());
        firstNameTextView.setText(user.getFirstName());
        lastNameTextView.setText(user.getLastName());
        emailTextView.setText(user.getEmail());
        phoneTextView.setText(user.getPhone());

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

        setFirstName.setText(user.getFirstName());
        setLastName.setText(user.getLastName());
        setEmail.setText(user.getEmail());
        setPhone.setText(user.getPhone());

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DocumentReference updateDoc = db.collection("UserProfile").document(user.getUserId());
                String updatedFirst = setFirstName.getText().toString();
                String updatedLast = setLastName.getText().toString();
                String updatedEmail = setEmail.getText().toString();
                String updatedPhone = setPhone.getText().toString();

                user.setFirstName(updatedFirst);
                user.setLastName(updatedLast);
                user.setEmail(updatedEmail);
                user.setPhone(updatedPhone);

                updateDoc.update(
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
                            final TextView usernameTextView = (TextView) findViewById(R.id.username);
                            final TextView firstNameTextView = (TextView) findViewById(R.id.firstName);
                            final TextView lastNameTextView = (TextView) findViewById(R.id.lastName);
                            final TextView emailTextView = (TextView) findViewById(R.id.email);
                            final TextView phoneTextView = (TextView) findViewById(R.id.phone);

                            usernameTextView.setText(user.getUserId());
                            firstNameTextView.setText(user.getFirstName());
                            lastNameTextView.setText(user.getLastName());
                            emailTextView.setText(user.getEmail());
                            phoneTextView.setText(user.getPhone());
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
                collectionReference.document(user.getUserId()).delete();
                Intent intent1 = new Intent(UserProfile.this, Signup.class);
                startActivity(intent1);
                //Delete from the list of users/user manager
            }
        });

        setDialog.show();
    }

    private void addExpDialog() {
        AlertDialog.Builder addBuilder = new AlertDialog.Builder(UserProfile.this);
        View addView = getLayoutInflater().inflate(R.layout.add_exp_dialog,null);

        EditText expTitle = (EditText) addView.findViewById(R.id.addExpTitle);
        EditText expDescription = (EditText) addView.findViewById(R.id.addExpDescription);
        EditText expRegion = (EditText) addView.findViewById(R.id.addExpRegion);
        Button create = (Button) addView.findViewById(R.id.createButton);

        addBuilder.setView(addView);
        AlertDialog addDialog = addBuilder.create();
        addDialog.setCanceledOnTouchOutside(true);

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO create experiment
                // lead to experiment activity
            }
        });

        addDialog.show();

    }

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

    private void myExpView (){
        myExpListView = findViewById(R.id.myExpListView);

        String[] test2 = new String[]{
                "My1", "My2", "My3", "My3", "My4", "My5", "My6", "My7", "My8", "My9"
        };

        ArrayAdapter<String> myExpAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1, test2);
        myExpListView.setAdapter(myExpAdapter);

        myExpListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //TODO
//                if (position == 0){
//                    Intent intent = new Intent(view.getContext(), My1.class);
//                    startActivity(intent);
//                }
//                if (position == 1){
//                    Intent intent = new Intent(view.getContext(), My2.class);
//                    startActivity(intent);
//                }
            }
        });
    }
}
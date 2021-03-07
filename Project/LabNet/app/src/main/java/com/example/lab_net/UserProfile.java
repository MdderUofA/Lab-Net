package com.example.lab_net;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.database.ValueEventListener;

public class UserProfile extends AppCompatActivity {

//    private FirebaseUser user;
//    private DatabaseReference ref;
//    private String userID;
    private User user;
    //private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

//        user = FirebaseAuth.getInstance().getCurrentUser();
//        ref = FirebaseDatabase.getInstance().getReference("Users");
//        userID = user.getUid();
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

//        ref.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                User userProfile = snapshot.getValue(User.class);
//
//                if (userProfile != null){
//                    String username = userID;
//
//                    usernameTextView.setText(username);
//                    firstNameTextView.setText(userProfile.firstName);
//                    lastNameTextView.setText(userProfile.lastName);
//                    emailTextView.setText(userProfile.email);
//                    phoneTextView.setText(userProfile.phone);
//
//                }
//            }

//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Toast.makeText(UserProfile.this, "Error displaying profile", Toast.LENGTH_LONG).show();
//            }
//        });


    }
}
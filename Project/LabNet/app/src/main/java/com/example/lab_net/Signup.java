/**
 * CMPUT 301
 * @version 1.0
 * March 19, 2021
 *
 */

package com.example.lab_net;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.provider.Settings.Secure;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

/**
 * This class is the homepage activity of the app that encourages the user to sign up or
 * if they have already signed up than takes then to the User Profile.
 *
 * @author Vidhi Patel, Qasim Akhtar
 */
public class Signup extends AppCompatActivity implements View.OnClickListener {

    private Button signUp;
    private EditText editTextFirstName, editTextLastName, editTextEmail, editTextPhone;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    final CollectionReference collectionReference =
            db.collection(DatabaseCollections.USER_PROFILE.value());
    private final String Tag = "Sample";
    private User user;
    private String deviceID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        //initialize Sign Up button
        signUp = (Button) findViewById(R.id.signupButton);
        signUp.setOnClickListener(this);

        //initialize fields
        editTextFirstName = (EditText) findViewById(R.id.FirstName);
        editTextLastName = (EditText) findViewById(R.id.LastName);
        editTextEmail = (EditText) findViewById(R.id.EmailAddress);
        editTextPhone = (EditText) findViewById(R.id.PhoneNumber);

        //get device ID
        deviceID = Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);

        /**
         * @author Qasim Akhtar
         *
         * if the deviceID already exists in the database,
         * than move to UserProfile with user data from database,
         * else do nothing.
         */
        collectionReference
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (QueryDocumentSnapshot document: task.getResult()) {
                                String id = document.getId();
                                if (deviceID.equals(id)) {
                                    Intent intent = new Intent(Signup.this, UserProfile.class);
                                    intent.putExtra(UserProfile.USER_ID_EXTRA, deviceID);
                                    startActivity(intent);
                                }
                            }
                        }

                    }
                });
    }

    /**
     * On clicking the Sign Up button, check if all the fields are filled. If yes then create user,
     * else check again.
     *
     * @author Vidhi Patel
     */
    @Override
    public void onClick(View v) {
        checkSignUp();
        if (checkSignUp()){
            createUser();
        }
        else {
            checkSignUp();
        }

    }

    /**
     * Check to insure the user has filled out every field or else request focus.
     *
     * @return true if all fields are filled.
     *
     * @author Vidhi Patel
     */
    private boolean checkSignUp() {
        String firstName = editTextFirstName.getText().toString().trim();
        String lastName = editTextLastName.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String phone = editTextPhone.getText().toString().trim();

        if (firstName.isEmpty()) {
            editTextFirstName.setError("First Name is required!");
            editTextFirstName.requestFocus();
            return false;

        }
        else if (lastName.isEmpty()) {
            editTextLastName.setError("Last Name is required!");
            editTextLastName.requestFocus();
            return false;
        }
        else if (email.isEmpty()) {
            editTextEmail.setError("Email is required!");
            editTextEmail.requestFocus();
            return false;

        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Please enter a valid email!");
            editTextEmail.requestFocus();
            return false;

        }
        else if (phone.isEmpty()) {
            editTextPhone.setError("Phone is required!");
            editTextPhone.requestFocus();
            return false;

        }
        else if (!Patterns.PHONE.matcher(phone).matches()) {
            editTextPhone.setError("Please enter a valid phone number!");
            editTextPhone.requestFocus();
            return false;
        }

        return true;


    }

    /**
     * Create user by adding the user inputted information into the Firestore database and,
     * the User class. Store the device ID as the UserID in Firestore.
     *
     * @author Qasim Akhtar
     */
    private void createUser(){
        String firstName = editTextFirstName.getText().toString().trim();
        String lastName = editTextLastName.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String phone = editTextPhone.getText().toString().trim();


        Map<String,Object> dataSet= new HashMap<>();
        dataSet.put("email",email);
        dataSet.put("firstName",firstName);
        dataSet.put("lastName",lastName);
        dataSet.put("phone",phone);

        collectionReference.document(deviceID).set(dataSet)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(Tag,"User added");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(Tag,"not added");
                    }
                });
        user = new User(deviceID,firstName,lastName,email,phone);

        Intent intent1 = new Intent(Signup.this,UserProfile.class);
        intent1.putExtra(UserProfile.USER_ID_EXTRA, user.getUserId());
        startActivity(intent1);

    }

}






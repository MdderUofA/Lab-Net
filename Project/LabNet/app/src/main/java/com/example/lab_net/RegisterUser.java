package com.example.lab_net;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterUser extends AppCompatActivity implements View.OnClickListener {

    //private FirebaseAuth mAuth;
    private Button signUp;
    private EditText editTextFirstName, editTextLastName, editTextEmail, editTextPhone, editTextPassword;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    final CollectionReference collectionReference = db.collection("UserProfile");
    private final String Tag = "Sample";
    private String userId;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        signUp = (Button) findViewById(R.id.signupButton);
        signUp.setOnClickListener(this);

        editTextFirstName = (EditText) findViewById(R.id.FirstName);
        editTextLastName = (EditText) findViewById(R.id.LastName);
        editTextEmail = (EditText) findViewById(R.id.EmailAddress);
        editTextPhone = (EditText) findViewById(R.id.editTextPhone);
        editTextPassword = (EditText) findViewById(R.id.password);

    }

    @Override
    public void onClick(View v) {
        checkSignUp();
        Intent intent1 = new Intent(RegisterUser.this,UserProfile.class);
        intent1.putExtra("User",user);
        startActivity(intent1);
    }

    private void checkSignUp() {
        String firstName = editTextFirstName.getText().toString().trim();
        String lastName = editTextLastName.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String phone = editTextPhone.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (firstName.isEmpty()) {
            editTextFirstName.setError("First Name is required!");
            editTextFirstName.requestFocus();
            return;
        }
        if (lastName.isEmpty()) {
            editTextLastName.setError("Last Name is required!");
            editTextLastName.requestFocus();
            return;
        }
        if (email.isEmpty()) {
            editTextEmail.setError("Email is required!");
            editTextEmail.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Please enter a valid email!");
            editTextEmail.requestFocus();
            return;
        }
        if (phone.isEmpty()) {
            editTextPhone.setError("Phone is required!");
            editTextPhone.requestFocus();
            return;
        }
        if (!Patterns.PHONE.matcher(phone).matches()) {
            editTextPhone.setError("Please enter a valid phone number!");
            editTextPhone.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            editTextPassword.setError("Password is required!");
            editTextPassword.requestFocus();
            return;
        }
        if (password.length() < 6) {
            editTextPassword.setError("Password needs to be longer than 6 characters!");
            editTextPassword.requestFocus();
            return;
        }

        //TODO add password ??
        Map<String,Object> dataSet= new HashMap<>();
        dataSet.put("email",email);
        dataSet.put("firstName",firstName);
        dataSet.put("lastName",lastName);
        dataSet.put("phone",phone);

        userId = collectionReference.document().getId();
        collectionReference.document(userId).set(dataSet)
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
        user = new User(userId,firstName,lastName,email,phone);
    }
}

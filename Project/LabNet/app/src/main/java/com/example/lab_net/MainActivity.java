package com.example.lab_net;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity implements View.OnClickListener  {

    private FirebaseAuth mAuth;
    private String androidId = Settings.Secure.getString (this.getContentResolver(), Settings.Secure.ANDROID_ID);
    private Button signUp;
    private EditText editTextFirstName, editTextLastName, editTextEmail, editTextPhone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        signUp = (Button) findViewById(R.id.SignUpButton);
        signUp.setOnClickListener(this);

        editTextFirstName = (EditText) findViewById(R.id.FirstName);
        editTextLastName = (EditText) findViewById(R.id.LastName);
        editTextEmail = (EditText) findViewById(R.id.EmailAddress);
        editTextPhone = (EditText) findViewById(R.id.editTextPhone);


    }


    @Override
    public void onClick(View v) {

        checkSignup();

    }

    private void checkSignup() {
        String firstName = editTextFirstName.getText().toString().trim();
        String lastName = editTextLastName.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String phone = editTextPhone.getText().toString().trim();

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

        mAuth.signInAnonymously()
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
//                            Log.d(TAG, "signInAnonymously:success");
                            User user = new User(firstName, lastName, email, phone);
                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(MainActivity.this, "User registered", Toast.LENGTH_SHORT);
                                        startActivity(new Intent(MainActivity.this, UserProfile.class ));
                                    } else {
                                        Toast.makeText(MainActivity.this, "Failed to register.",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
//                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
//                            Log.w(TAG, "signInAnonymously:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Failed to register.",
                                    Toast.LENGTH_SHORT).show();
//                            updateUI(null);
                        }
                    }
                });
    }

    //Connect User to Firebase
    //TODO

}


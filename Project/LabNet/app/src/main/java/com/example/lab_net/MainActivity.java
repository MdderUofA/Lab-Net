package com.example.lab_net;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

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

public class MainActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private CollectionReference collectionReference;
    private String deviceId;
    private final String Tag = "Sample";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        deviceId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);

        db = FirebaseFirestore.getInstance();
        collectionReference = db.collection("UserProfile");

        collectionReference
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            String x = "0";
                            for (QueryDocumentSnapshot document: task.getResult()) {
                                String id = document.getId();
                                if (deviceId.equals(id)) {
                                    x = "1";
                                    Intent intent = new Intent(MainActivity.this, UserProfile.class);
                                    intent.putExtra("userID", deviceId);
                                    startActivity(intent);
                                }
                            }
                            if(x.equals("0")) {
                                createUser();
                            }
                        }
                    }
                });
    }

    private void createUser(){

        Map<String,Object> dataSet= new HashMap<>();
        dataSet.put("email","");
        dataSet.put("firstName","");
        dataSet.put("lastName","");
        dataSet.put("phone","");

        collectionReference.document(deviceId).set(dataSet)
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

        Intent intent1 = new Intent(MainActivity.this, UserProfile.class);
        intent1.putExtra("userID", deviceId);
        startActivity(intent1);

    }
}
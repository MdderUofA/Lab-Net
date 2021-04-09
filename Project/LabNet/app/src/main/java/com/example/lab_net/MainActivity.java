package com.example.lab_net;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

/**
 * Starts the application, creating the user if necessary.
 * @author Vidhi Patel
 */
public class MainActivity extends AppCompatActivity {

    private String deviceId;
    private final String Tag = "Sample";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        deviceId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);

        // I had too much fun.
        Utils.queryDatabase(DatabaseCollections.USER_PROFILE,
                Utils.find(this::testUserIsUs,this::onFoundUser,this::createUser));
    }

    /**
     * Test function for Utils.find
     * @param doc The QueryDocumentSnapshot to test if us
     * @return
     */
    private boolean testUserIsUs(QueryDocumentSnapshot doc) {
        return deviceId.equals(doc.getId());
    }

    private void onFoundUser(QueryDocumentSnapshot doc) {
        Intent intent = new Intent(MainActivity.this, UserProfile.class);
        intent.putExtra(UserProfile.USER_ID_EXTRA, deviceId);
        startActivity(intent);
    }

    private void createUser(boolean b){

        Map<String,Object> dataSet= new HashMap<>();
        dataSet.put("email","");
        dataSet.put("firstName","");
        dataSet.put("lastName","");
        dataSet.put("phone","");

        Utils.writeDatabase(DatabaseCollections.USER_PROFILE,deviceId,dataSet)
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
        intent1.putExtra(UserProfile.USER_ID_EXTRA, deviceId);
        startActivity(intent1);

    }
}
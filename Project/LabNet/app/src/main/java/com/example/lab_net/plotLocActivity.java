package com.example.lab_net;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * plotLocActivity generates plotLocFragment to allow map with location coordinates plotted
 * to be displayed. Passes experiment ID to plotLocFragment.
 * @author gurjogsingh
 * @version 1.0
 * @see plotLocFragment
 */

public class plotLocActivity extends AppCompatActivity implements Serializable {

    private static final String TAG = "TESTING" ;
    private ArrayList<Double> latitude = new ArrayList<>();
    private ArrayList<Double> longitude = new ArrayList<>();
    private ArrayList<String> trialName = new ArrayList<>();
    private String experimentId;

    /**
     * Creates plotLocFragment instance and send experiment ID to it.
     * @param savedInstanceState
     * @return void
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plot_loc);

        Intent intent = getIntent();
        experimentId = intent.getStringExtra("ExperimentId");
        Log.d(TAG, "onCreate: EXPERIMENTID " + experimentId);

        Fragment fragment = new plotLocFragment();
        Bundle bundle = new Bundle();
        bundle.putString("experimentId", experimentId);

        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.map_layout_PL,fragment).commit();

    }
}
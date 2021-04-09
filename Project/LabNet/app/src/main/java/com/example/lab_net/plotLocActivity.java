package com.example.lab_net;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import java.io.Serializable;

/**
 * plotLocActivity generates plotLocFragment to allow map with location coordinates plotted
 * to be displayed. Passes experiment ID to plotLocFragment.
 * @author gurjogsingh
 * @version 1.0
 * @see plotLocFragment
 */

public class plotLocActivity extends AppCompatActivity implements Serializable {

    private static final String TAG = "TESTING" ;
    private String experimentId;
    private Fragment plotLocFragment;

    /**
     * Creates plotLocFragment instance and send experiment ID to it.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plot_loc);

        Intent intent = getIntent();
        experimentId = intent.getStringExtra("ExperimentId");
        Log.d(TAG, "onCreate: EXPERIMENTID " + experimentId);


        Fragment fragment = new plotLocFragment();
        plotLocFragment = fragment;
        Bundle bundle = new Bundle();
        bundle.putString("experimentId", experimentId);

        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.map_layout_PL,fragment).commit();

    }

}
package com.example.lab_net;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class Statistics extends AppCompatActivity {

    TextView meanView;
    long sum = 0, mean = 0;
    ArrayList<Trial> trials;
    ArrayList<Long> results;
    int i;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stats_dialog);
        meanView = (TextView) findViewById(R.id.mean_view);

        //get from ExperimentActivity
        Intent intent = getIntent();
        trials = new ArrayList<Trial>();
        trials = (ArrayList<Trial>) intent.getSerializableExtra("trialDataList");

        results = new ArrayList<>();
        for (i = 0; i < trials.size(); i++) {
            Trial trial = trials.get(0);
            results.add((long) 4);
            sum = results.get(i) + sum;


            if (results.size() != 0) {
                mean = (sum / (Long.valueOf(results.size())));
            } else {
                mean = 0;
            }
            sum = 0;

            // mean = (sum/(trials.size()));

            meanView.setText("Mean: " + mean);


        }
    }
}
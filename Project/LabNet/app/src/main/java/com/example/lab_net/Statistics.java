package com.example.lab_net;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

public class Statistics extends AppCompatActivity {

    private TextView meanView, medianView, lowerQuartileView;

    private float sum = 0;
    private double mean = 0, median = 0;
    private double lowerQuartile = 0;
    private double upperQuartile = 0;
    private double standardDeviation = 0;

    private ArrayList<CountTrial> trials;
    private ArrayList<Long> results;
    private String expId;
    private int i;

    private Button doneButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stats_dialog);
        meanView = (TextView) findViewById(R.id.mean_view);

        //get from ExperimentActivity
        Intent intent = getIntent();
        results = new ArrayList<>();
        results = (ArrayList<Long>) intent.getSerializableExtra("resultList");
        expId = intent.getStringExtra("ExperimentId");

        for (i = 0; i < results.size(); i++) {
            sum = results.get(i) + sum;
        }
        if (results.size() != 0) {
            mean = (sum / (results.size()));
        } else {
            mean = 0;
        }
        sum = 0;

        doneButton = (Button) findViewById(R.id.doneButton);
        meanView.setText("Mean: " + mean);

        // median
        Collections.sort(results);
        int mid = results.size() / 2;

        if (results.size() % 2 == 0){
            median = (results.get(mid-1) + results.get(mid)) / 2.0;
        }
        else {
            median = results.get(mid);
        }

/*        int lq = results.size()/4;
        if(results.size() % 2 == 0 && results.size() != 1)  {
            lowerQuartile = (results.get(lq - 1) + results.get(lq))/2;
        }
        else if(results.size() == 1){
            lowerQuartile = results.get(0);
        }
        else{
            lowerQuartile = results.get(lq - 1);
        }

        int uq = ((3 * results.size())/4);
        if(results.size() % 2 == 0 && results.size() != 1)  {
            upperQuartile = (results.get(uq - 1) + results.get(uq+1))/2;
        }
        else if(results.size() == 1){
            upperQuartile = results.get(0);
        }
        else{
            lowerQuartile = results.get(uq + 1);
        }*/



        medianView = findViewById(R.id.median_view);
        medianView.setText("Median: " + median);

        lowerQuartileView = findViewById(R.id.q1);

        lowerQuartileView.setText("Lower Quartile: " + lowerQuartile);

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(getApplicationContext(), ExperimentActivity.class);
                intent1.putExtra(ExperimentActivity.EXPERIMENT_ID_EXTRA, expId);
                startActivity(intent1);
            }
        });
    }

}
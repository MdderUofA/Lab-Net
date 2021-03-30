package com.example.lab_net;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.lang.reflect.Type;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Collections;

public class Statistics extends AppCompatActivity {

    private TextView meanView, medianView, lowerQuartileView,upperQuartileView,standardDeviationView;

    private double mean = 0;
    private double median = 0;
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
        setContentView(R.layout.activity_stats);

        //get from ExperimentActivity
        Intent intent = getIntent();
        results = new ArrayList<>();
        results = (ArrayList<Long>) intent.getSerializableExtra("resultList");
        expId = intent.getStringExtra("ExperimentId");

//        System.out.println(results.getClass().getName());
//        System.out.println(sum.getClass().getName());
        calculateMean();
        calculateMedian();
        calculateLowerQuartile();
        calculateUpperQuartile();
        calculateStandardDeviation();

        doneButton = (Button) findViewById(R.id.doneButton);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(getApplicationContext(), ExperimentActivity.class);
                intent1.putExtra(ExperimentActivity.EXPERIMENT_ID_EXTRA, expId);
                startActivity(intent1);
            }
        });
    }

    private void calculateMean() {
        double sum = 0;
        for (i = 0; i < results.size(); i++) {
            sum = (results.get(i) + sum);
        }
        if (results.size() != 0) {
            mean = (sum / (results.size()));
        } else {
            mean = 0;
        }

        meanView = (TextView) findViewById(R.id.mean_value);
        meanView.setText(String.format("%.2f",mean));

    }

    private void calculateMedian() {
        Collections.sort(results);
        int mid = results.size() / 2;

        if (results.size() % 2 == 0){
            median = (results.get(mid-1) + results.get(mid)) / 2.0;
        }
        else {
            median = results.get(mid);
        }
        medianView = findViewById(R.id.median_value);
        medianView.setText(String.valueOf(median));
    }

    private void calculateLowerQuartile() {
        Collections.sort(results);
        ArrayList<Long> lqArray = new ArrayList<>();
        int mid = results.size() / 2;
        int l;
        for (l = 0; l < mid; l++) {
            lqArray.add(results.get(l));
        }
        int lq = lqArray.size()/2;
        if (lqArray.size() % 2 == 0){
            double add = lqArray.get(lq-1) + lqArray.get(lq);
            lowerQuartile = add / 2;


        }
        else {
            lowerQuartile = lqArray.get(lq);
        }
        lowerQuartileView = findViewById(R.id.q1);
        lowerQuartileView.setText(String.valueOf(lowerQuartile));
    }

    private void calculateUpperQuartile() {
        Collections.sort(results);
        ArrayList<Long> uqArray = new ArrayList<>();
        int mid = results.size() / 2;
        int u;
        if (results.size() % 2 == 0){
            u = mid;
        }
        else{
            u = mid + 1;
        }

        for (; u < results.size(); u++) {
            uqArray.add(results.get(u));
        }
        int uq = uqArray.size()/2;
        if (uqArray.size() % 2 == 0){
            double add = uqArray.get(uq-1) + uqArray.get(uq);
            upperQuartile = add / 2;
        }
        else {
            upperQuartile = uqArray.get(uq);
        }
        upperQuartileView = findViewById(R.id.q3);
        upperQuartileView.setText(String.valueOf(upperQuartile));

    }

    private void calculateStandardDeviation() {
        ArrayList<Double> stdevArray = new ArrayList<>();
        for (i = 0; i < results.size();i++) {
            double num = (results.get(i) - mean);
            double x = Math.pow(num, 2);
            stdevArray.add(x);
        }
        int j;
        double stdevSum = 0;
        for (j = 0; j < stdevArray.size();j++) {
            stdevSum = stdevArray.get(j) + stdevSum;
        }
        if (stdevArray.size() != 0) {
            double y = (stdevSum / (stdevArray.size()));
            standardDeviation = Math.sqrt(y);
        } else {
            standardDeviation = 0;
        }

        standardDeviationView = findViewById(R.id.stdev);
        standardDeviationView.setText(String.format("%.2f",standardDeviation));
    }



}
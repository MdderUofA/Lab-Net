package com.example.lab_net;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;

public class Statistics extends AppCompatActivity {

    private TextView meanView, medianView, lowerQuartileView, upperQuartileView, standardDeviationView;

    private double sum = 0;
    private double mean = 0;
    private double median = 0;
    private double lq = 0;
    private double uq = 0;
    private double standardDeviation = 0;

    // trials
    private ArrayList<CountTrial> countTrials;
    private ArrayList<MeasurementTrial> measurementTrials;
    private ArrayList<NonNegativeIntegerTrial> nonNegativeTrials;
    private ArrayList<BinomialTrial> binomialTrials;

    //statistic measures
    private ArrayList<Double> results;
    private ArrayList<Double> lqResults;
    private ArrayList<Double> uqResults;
    private ArrayList<Double> stdDeviation;

    // measurement
    private ArrayList<Double> measurementResults;

    private String expId;
    private int i;

    private Button doneButton;
    private boolean subscribed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        Intent intent = getIntent();
        expId = intent.getStringExtra("expId");

        subscribed = intent.getBooleanExtra("subscribed", false);


        meanView = (TextView) findViewById(R.id.mean_value);

        results = new ArrayList<>();
        lqResults = new ArrayList<>();
        uqResults = new ArrayList<>();
        stdDeviation = new ArrayList<>();
        int requestCode = intent.getExtras().getInt("check");

        if (requestCode == 0) {
            count();
        }
        if (requestCode == 1) {
            measurement();
        }

        if (requestCode == 2) {
            nonNegative();
        }

        if (requestCode == 3) {
            binomial();
        }
    }

    private void binomial() {

        Intent intent = getIntent();
        binomialTrials = new ArrayList<>();
        binomialTrials = (ArrayList<BinomialTrial>) intent.getSerializableExtra("trialDataList");

        //get mean
        for (i = 0; i < binomialTrials.size(); i++) {
            if (binomialTrials.get(i).getResult().toLowerCase().equals("pass")) {
                results.add(1.0);
            } else {
                results.add(0.0);
            }

        }
        mean = calculateMean(results);
        meanView.setText(String.format("%.2f", mean));

        //get standard deviation
        for (i = 0; i < results.size(); i++) {
            double std = Math.pow((results.get(i) - mean), 2);
            stdDeviation.add(std);
        }
        standardDeviationView = findViewById(R.id.stdev);

        if (results.size() > 1) {
            standardDeviation = Math.pow((calculateMean(stdDeviation)), 0.5);
            standardDeviationView.setText(String.format("%.2f", standardDeviation));
        } else {
            standardDeviationView.setText("0.00");
            Toast.makeText(this, "Std. Dev cannot be calculated for 1 number", Toast.LENGTH_LONG).show();
        }

        //get median
        median = calculateMedian(results);
        medianView = findViewById(R.id.median_value);
        medianView.setText(String.format("%.2f", median));

        //get lower quartile
        int mid = results.size() / 2;
        if (results.size() > 1) {
            for (i = 0; i < (mid); i++) {
                lqResults.add(results.get(i));
            }
            lq = calculateMedian(lqResults);
        } else {
            lq = results.get(0);
        }
        lowerQuartileView = findViewById(R.id.q1);
        lowerQuartileView.setText(String.format("%.2f", lq));

        //get upper quartile
        if (results.size() > 1) {
            int size = results.size() - 1;
            for (i = size; i >= mid; i--) {
                uqResults.add(results.get(i));
            }
            uq = calculateMedian(uqResults);
        } else {
            uq = results.get(0);
        }
        upperQuartileView = findViewById(R.id.q3);
        upperQuartileView.setText(String.format("%.2f", uq));

        //go back to experiment
        doneButton = (Button) findViewById(R.id.doneButton);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent doneIntent = new Intent(getApplicationContext(), BinomialExperimentActivity.class);
                doneIntent.putExtra("experimentId", expId);
                startActivity(doneIntent);
            }
        });

    }

    private void nonNegative() {

        Intent intent = getIntent();
        nonNegativeTrials = new ArrayList<>();
        nonNegativeTrials = (ArrayList<NonNegativeIntegerTrial>) intent.getSerializableExtra("trialDataList");

        //get mean
        for (i = 0; i < nonNegativeTrials.size(); i++) {
            results.add(Double.valueOf(nonNegativeTrials.get(i).getNonNegativeCount()));
        }
        mean = calculateMean(results);
        meanView.setText(String.format("%.2f", mean));

        //get standard deviation
        for(i = 0; i<results.size(); i++){
            double std = Math.pow((results.get(i) - mean), 2);
            stdDeviation.add(std);
        }
        standardDeviationView = findViewById(R.id.stdev);

        if(results.size() > 1){
            standardDeviation = Math.pow((calculateMean(stdDeviation)), 0.5);
            standardDeviationView.setText(String.format("%.2f", standardDeviation));
        }
        else{
            standardDeviationView.setText("0.00");
            Toast.makeText(this, "Std. Dev cannot be calculated for 1 number", Toast.LENGTH_LONG).show();
        }

        //get median
        median = calculateMedian(results);
        medianView = findViewById(R.id.median_value);
        medianView.setText(String.format("%.2f",median));

        //get lower quartile
        int mid = results.size() / 2;
        if(results.size() > 1){
            for (i = 0; i < (mid); i++){
                lqResults.add(results.get(i));
            }
            lq = calculateMedian(lqResults);
        }
        else{
            lq = results.get(0);
        }
        lowerQuartileView = findViewById(R.id.q1);
        lowerQuartileView.setText(String.format("%.2f",lq));

        //get upper quartile
        if(results.size() > 1) {
            int size = results.size() - 1;
            for (i = size; i >= mid; i--) {
                uqResults.add(results.get(i));
            }
            uq = calculateMedian(uqResults);
        }
        else{
            uq = results.get(0);
        }
        upperQuartileView = findViewById(R.id.q3);
        upperQuartileView.setText(String.format("%.2f", uq));

        //go back to experiment
        doneButton = (Button) findViewById(R.id.doneButton);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(getApplicationContext(), NonNegativeExperimentActivity.class);
                intent1.putExtra("experimentId", expId);
                startActivity(intent1);
            }
        });
    }

    private void measurement() {

        Intent intent = getIntent();
        measurementTrials = new ArrayList<>();
        measurementTrials = (ArrayList<MeasurementTrial>) intent.getSerializableExtra("trialDataList");

        //get mean
        for (i = 0; i < measurementTrials.size(); i++) {
            results.add(Double.valueOf(measurementTrials.get(i).getMeasurement()));
        }
        mean = calculateMean(results);
        meanView.setText(String.format("%.2f", mean));

        //get standard deviation
        for(i = 0; i<results.size(); i++){
            double std = Math.pow((results.get(i) - mean), 2);
            stdDeviation.add(std);
        }
        standardDeviationView = findViewById(R.id.stdev);

        if(results.size() > 1){
            standardDeviation = Math.pow((calculateMean(stdDeviation)), 0.5);
            standardDeviationView.setText( String.format("%.2f", standardDeviation));
        }
        else{
            standardDeviationView.setText("0.00");
            Toast.makeText(this, "Std. Dev cannot be calculated for 1 number", Toast.LENGTH_LONG).show();
        }

        //get median
        median = calculateMedian(results);
        medianView = findViewById(R.id.median_value);
        medianView.setText(String.format("%.2f",median));

        //get lower quartile
        int mid = results.size() / 2;
        if(results.size() > 1){
            for (i = 0; i < (mid); i++){
                lqResults.add(results.get(i));
            }
            lq = calculateMedian(lqResults);
        }
        else{
            lq = results.get(0);
        }
        lowerQuartileView = findViewById(R.id.q1);
        lowerQuartileView.setText( String.format("%.2f",lq));

        //get upper quartile
        if(results.size() > 1) {
            int size = results.size() - 1;
            for (i = size; i >= mid; i--) {
                uqResults.add(results.get(i));
            }
            uq = calculateMedian(uqResults);
        }
        else{
            uq = results.get(0);
        }
        upperQuartileView = findViewById(R.id.q3);
        upperQuartileView.setText(String.format("%.2f", uq));

        //go back to experiment
        doneButton = (Button) findViewById(R.id.doneButton);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(getApplicationContext(), MeasurementExperimentActivity.class);
                intent1.putExtra("experimentId", expId);
                startActivity(intent1);
            }
        });
    }

    private void count() {

        Intent intent = getIntent();
        countTrials = new ArrayList<CountTrial>();
        countTrials = (ArrayList<CountTrial>) intent.getSerializableExtra("trialDataList");

        //get mean
        for (i = 0; i < countTrials.size(); i++) {
            results.add(Double.valueOf(countTrials.get(i).getCount()));
        }
        mean = calculateMean(results);
        meanView.setText(String.format("%.2f", mean));

        //get standard deviation
        for(i = 0; i<results.size(); i++){
            double std = Math.pow((results.get(i) - mean), 2);
            stdDeviation.add(std);
        }
        standardDeviation = Math.pow((calculateMean(stdDeviation)), 0.5);
        standardDeviationView = findViewById(R.id.stdev);

        if(results.size() > 1){
            standardDeviation = Math.pow((calculateMean(stdDeviation)), 0.5);
            standardDeviationView.setText(String.format("%.2f", standardDeviation));
        }
        else{
            standardDeviationView.setText("0.00");
            Toast.makeText(this, "Std. Dev cannot be calculated for 1 number",
                    Toast.LENGTH_LONG).show();
        }

        //get median
        median = calculateMedian(results);
        medianView = findViewById(R.id.median_value);
        medianView.setText(String.format("%.2f",median));

        //get lower quartile
        int mid = results.size() / 2;
        if(results.size() > 1){
            for (i = 0; i < (mid); i++){
                lqResults.add(results.get(i));
            }
            lq = calculateMedian(lqResults);
        }
        else{
            lq = results.get(0);
        }
        lowerQuartileView = findViewById(R.id.q1);
        lowerQuartileView.setText( String.format("%.2f",lq));

        //get upper quartile
        if(results.size() > 1) {
            int size = results.size() - 1;
            for (i = size; i >= mid; i--) {
                uqResults.add(results.get(i));
            }
            uq = calculateMedian(uqResults);
        }
        else{
            uq = results.get(0);
        }
        upperQuartileView = findViewById(R.id.q3);
        upperQuartileView.setText(String.format("%.2f", uq));

        //go back to experiment
        Class next;
        if (subscribed == true) {
            next = SubscribedCountExperimentActivity.class;
        }
        else{
            next = CountExperimentActivity.class;
        }

        doneButton = (Button) findViewById(R.id.doneButton);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent doneIntent = new Intent(getApplicationContext(), next);
                doneIntent.putExtra("experimentId", expId);
                startActivity(doneIntent);
            }
        });

    }

    /**
     * calculates mean
     * @param list
     * @return mean
     */
    private double calculateMean(ArrayList<Double> list) {
        for (i = 0; i < list.size(); i++) {
            sum = list.get(i) + sum;
        }
        if (list.size() != 0) {
            mean = (sum / (list.size()));
        } else {
            mean = 0;
        }
        sum = 0;
        return(mean);
    }

    /**
     * calculates median
     * @param list
     * @return median
     */
    public double calculateMedian(ArrayList<Double> list){
        Collections.sort(list);
        double median = 0;
        int mid = list.size() / 2;
        if(list.size() % 2 != 0){
            median = list.get(mid);
        }
        else{
            median = (list.get(mid - 1) + list.get(mid)) / 2.0;
        }
        return(median);
    }

}
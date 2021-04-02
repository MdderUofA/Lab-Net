package com.example.lab_net;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Statistics extends AppCompatActivity {

    private TextView meanView, medianView, lowerQuartileView, upperQuartileView, standardDeviationView;

    private double sum = 0;
    private double mean;
    private double median;
    private double lq = 0;
    private double uq = 0;
    private double standardDeviation = 0;

    // count
    private ArrayList<CountTrial> countTrials;
    private ArrayList<MeasurementTrial> measurementTrials;
    private ArrayList<NonNegativeIntegerTrial> nonNegativeTrials;
    private ArrayList<BinomialTrial> binomialTrials;
    private ArrayList<Double> results;
    private ArrayList<Double> lqResults;
    private ArrayList<Double> uqResults;

    private ArrayList<Double> stdDeviation;

    // measurement
    private ArrayList<Double> measurementResults;

    private String expId;
    private int i;

    private Button doneButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);
        meanView = (TextView) findViewById(R.id.mean_view);

        //get from ExperimentActivity
        Intent intent = getIntent();
        //countTrials = new ArrayList<>();
        results = new ArrayList<>();
        lqResults = new ArrayList<>();
        uqResults = new ArrayList<>();
        stdDeviation = new ArrayList<>();
        int requestCode = intent.getExtras().getInt("check");

        if (requestCode == 0) {
            countTrials = new ArrayList<CountTrial>();
            countTrials = (ArrayList<CountTrial>) intent.getSerializableExtra("trialDataList");
            for (i = 0; i < countTrials.size(); i++) {
                results.add(Double.valueOf(countTrials.get(i).getCount()));
            }
            expId = intent.getStringExtra("ExperimentId");
            mean = calculateMean(results);
            meanView.setText("Mean: " + String.format("%.2f", mean));

            for(i = 0; i<results.size(); i++){
                double std = Math.pow((results.get(i) - mean), 2);
                stdDeviation.add(std);
            }

            standardDeviation = Math.pow((calculateMean(stdDeviation)), 0.5);
            standardDeviationView = findViewById(R.id.std_view);

            if(results.size() > 1){
                standardDeviation = Math.pow((calculateMean(stdDeviation)), 0.5);
                standardDeviationView.setText("Standard Deviation= " + String.format("%.2f", standardDeviation));
            }
            else{
                standardDeviationView.setText("Standard Deviation= 0.00");
                Toast.makeText(this, "Std. Dev cannot be calculated for 1 number", Toast.LENGTH_LONG).show();
            }

            median = calculateMedian(results);
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

            medianView = findViewById(R.id.median_view);
            medianView.setText("Median: " + median);
            lowerQuartileView = findViewById(R.id.q1);
            lowerQuartileView.setText("Lower Quartile: " + lq);
            upperQuartileView = findViewById(R.id.q3);
            upperQuartileView.setText("Upper Quartile:" + uq);
            doneButton = (Button) findViewById(R.id.doneButton);
        }


        if (requestCode == 1) {
            measurementTrials = new ArrayList<>();
            measurementTrials = (ArrayList<MeasurementTrial>) intent.getSerializableExtra("trialDataList");
            for (i = 0; i < measurementTrials.size(); i++) {
                results.add(Double.valueOf(measurementTrials.get(i).getMeasurement()));
            }
            expId = intent.getStringExtra("ExperimentId");
            mean = calculateMean(results);
            meanView.setText("Mean = " + String.format("%.2f", mean));

            for(i = 0; i<results.size(); i++){
                double std = Math.pow((results.get(i) - mean), 2);
                stdDeviation.add(std);
            }
            standardDeviationView = findViewById(R.id.std_view);

            if(results.size() > 1){
                standardDeviation = Math.pow((calculateMean(stdDeviation)), 0.5);
                standardDeviationView.setText("Standard Deviation = " + String.format("%.2f", standardDeviation));
            }
            else{
                standardDeviationView.setText("Standard Deviation= 0.00");
                Toast.makeText(this, "Std. Dev cannot be calculated for 1 number", Toast.LENGTH_LONG).show();
            }
            median = calculateMedian(results);
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


            medianView = findViewById(R.id.median_view);
            medianView.setText("Median = " + String.format("%.2f",median));
            lowerQuartileView = findViewById(R.id.q1);
            lowerQuartileView.setText("Lower Quartile = " + String.format("%.2f",lq));
            upperQuartileView = findViewById(R.id.q3);
            upperQuartileView.setText("Upper Quartile = " + String.format("%.2f", uq));
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

        if (requestCode == 2) {
            nonNegativeTrials = new ArrayList<>();
            nonNegativeTrials = (ArrayList<NonNegativeIntegerTrial>) intent.getSerializableExtra("trialDataList");
            for (i = 0; i < nonNegativeTrials.size(); i++) {
                results.add(Double.valueOf(nonNegativeTrials.get(i).getNonNegativeCount()));
            }
            expId = intent.getStringExtra("ExperimentId");
            mean = calculateMean(results);
            meanView.setText("Mean = " + String.format("%.2f", mean));

            for(i = 0; i<results.size(); i++){
                double std = Math.pow((results.get(i) - mean), 2);
                stdDeviation.add(std);
            }
            standardDeviationView = findViewById(R.id.std_view);

            if(results.size() > 1){
                standardDeviation = Math.pow((calculateMean(stdDeviation)), 0.5);
                standardDeviationView.setText("Standard Deviation = " + String.format("%.2f", standardDeviation));
            }
            else{
                standardDeviationView.setText("Standard Deviation= 0.00");
                Toast.makeText(this, "Std. Dev cannot be calculated for 1 number", Toast.LENGTH_LONG).show();
            }
            median = calculateMedian(results);
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


            medianView = findViewById(R.id.median_view);
            medianView.setText("Median = " + String.format("%.2f",median));
            lowerQuartileView = findViewById(R.id.q1);
            lowerQuartileView.setText("Lower Quartile = " + String.format("%.2f",lq));
            upperQuartileView = findViewById(R.id.q3);
            upperQuartileView.setText("Upper Quartile = " + String.format("%.2f", uq));
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

        if (requestCode == 3) {
            binomialTrials = new ArrayList<>();
            binomialTrials = (ArrayList<BinomialTrial>) intent.getSerializableExtra("trialDataList");
            for (i = 0; i < binomialTrials.size(); i++) {
                if(binomialTrials.get(i).getResult().toLowerCase().equals("pass")){
                    results.add(1.0);
                }
                else{
                    results.add(0.0);
                }

            }
            expId = intent.getStringExtra("ExperimentId");
            mean = calculateMean(results);
            meanView.setText("Mean = " + String.format("%.2f", mean));

            for(i = 0; i<results.size(); i++){
                double std = Math.pow((results.get(i) - mean), 2);
                stdDeviation.add(std);
            }
            standardDeviationView = findViewById(R.id.std_view);

            if(results.size() > 1){
                standardDeviation = Math.pow((calculateMean(stdDeviation)), 0.5);
                standardDeviationView.setText("Standard Deviation = " + String.format("%.2f", standardDeviation));
            }
            else{
                standardDeviationView.setText("Standard Deviation= 0.00");
                Toast.makeText(this, "Std. Dev cannot be calculated for 1 number", Toast.LENGTH_LONG).show();
            }
            median = calculateMedian(results);
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


            medianView = findViewById(R.id.median_view);
            medianView.setText("Median = " + String.format("%.2f",median));
            lowerQuartileView = findViewById(R.id.q1);
            lowerQuartileView.setText("Lower Quartile = " + String.format("%.2f",lq));
            upperQuartileView = findViewById(R.id.q3);
            upperQuartileView.setText("Upper Quartile = " + String.format("%.2f", uq));
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
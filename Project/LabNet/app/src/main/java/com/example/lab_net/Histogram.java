package com.example.lab_net;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.DefaultAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class Histogram extends AppCompatActivity {

    BarChart barChart;
    ArrayList<BarEntry> barEntries;
    LineChart lineChart;
    ArrayList<ILineDataSet> iLineDataSets;
    ArrayList<Entry> entries;
    private ArrayList<Double> results;
    private ArrayList<NonNegativeIntegerTrial> nonNegativeTrials;
    private ArrayList <CountTrial> countTrials;
    private ArrayList <MeasurementTrial> measurementTrials;
    private ArrayList <BinomialTrial> binomialTrials;
    private ArrayList<Double> frequency;
    private int i;
    int checkActivity;
    float j;
    private ArrayList<String> dateDataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_histogram);
        Intent intent = getIntent();
        checkActivity = intent.getExtras().getInt("check");
        if(checkActivity == 0){
            nonNegative();
        }
        if(checkActivity == 1){
            count();
        }
        if(checkActivity == 2){
            binomial();
        }
        if(checkActivity == 3){
            measurement();
        }
    }

    public void nonNegative() {
        barChart = (BarChart) findViewById(R.id.barGraph);

        Intent intent = getIntent();
        results = new ArrayList<>();

        nonNegativeTrials = new ArrayList<>();
        nonNegativeTrials = (ArrayList<NonNegativeIntegerTrial>) intent.getSerializableExtra("trialDataList");
        for (i = 0; i < nonNegativeTrials.size(); i++) {
            results.add(Double.valueOf(nonNegativeTrials.get(i).getNonNegativeCount()));
        }
        barEntries = new ArrayList<>();
        frequency = new ArrayList<>();

        /*for (i = 0; i < results.size(); i++) {
            frequency.add()
        }*/
        Collections.sort(results);
        for (i = 0; i < results.size(); i++) {
            barEntries.add(new BarEntry(Float.valueOf(String.valueOf(results.get(i))), Collections.frequency(results, results.get(i))));
        }
        BarDataSet barDataSet = new BarDataSet(barEntries, "results");

        BarData barData = new BarData(barDataSet);
        barChart.setData(barData);

        barChart.setTouchEnabled(true);
        barChart.setDragEnabled(true);
        barChart.setScaleEnabled(true);
    }

    public void count() {
        barChart = (BarChart) findViewById(R.id.barGraph);

        Intent intent = getIntent();
        results = new ArrayList<>();

        countTrials = new ArrayList<>();
        countTrials = (ArrayList<CountTrial>) intent.getSerializableExtra("trialDataList");
        for (i = 0; i < countTrials.size(); i++) {
            results.add(Double.valueOf(countTrials.get(i).getCount()));
        }
        barEntries = new ArrayList<>();
        frequency = new ArrayList<>();


        Collections.sort(results);
        for (i = 0; i < results.size(); i++) {
            barEntries.add(new BarEntry(Float.valueOf(String.valueOf(results.get(i))), Collections.frequency(results, results.get(i))));
        }
        BarDataSet barDataSet = new BarDataSet(barEntries, "results");

        BarData barData = new BarData(barDataSet);
        barChart.setData(barData);

        barChart.setTouchEnabled(true);
        barChart.setDragEnabled(true);
        barChart.setScaleEnabled(true);

        lineChart = (LineChart) findViewById(R.id.lineChart);
        entries = new ArrayList<>();
        dateDataList = new ArrayList<>();
        dateDataList = (ArrayList<String>) intent.getSerializableExtra("dateDataList");
        ArrayList<String> xAxisValues = new ArrayList<>();


        XAxis xAxis = lineChart.getXAxis();
        lineChart.getXAxis().setGranularityEnabled(true);
        lineChart.getXAxis().setGranularity(1.0f);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {

                String s = Float.toString(value);
                s = Double.toString(Double.parseDouble(s));
                int decIndex = 0;
                for (int i=0;i<s.length();i++) {
                    if (s.charAt(i)=='.') {
                        decIndex = i-1;
                        break;
                    }
                }

                String year = "";
                for (int i=0;i<=decIndex-4;i++) {
                    year = year + s.charAt(i) ;
                }
                String month = "";
                for (int i=decIndex-3;i<=decIndex-2;i++) {
                    month = month + s.charAt(i) ;
                }
                String day = "";
                for (int i=decIndex-1;i<=decIndex;i++) {
                    day = day + s.charAt(i);
                }
                Log.d("Tag", "Format is" +  value + s);
                return day+"-"+month+"-"+year;
            }

        });
        int max=0, min=999999999;
        for(i = 0; i < dateDataList.size(); i++){
            StringBuffer str = new StringBuffer(dateDataList.get(i));
            String utilString = "";
            utilString = utilString + str.toString().substring(0,2);
            utilString = str.toString().substring(2,4) + utilString;
            utilString = str.toString().substring(6,8) + utilString;
            entries.add(new Entry(Integer.valueOf(utilString),i));
            if (Integer.valueOf(utilString)>max) {
                max = Integer.valueOf(utilString);
            } else if (Integer.valueOf(utilString)<min) {
                min = Integer.valueOf(utilString);
            }
        }
        LineDataSet lineDataSet = new LineDataSet(entries, "dateDataList");

        iLineDataSets = new ArrayList<>();
        iLineDataSets.add(lineDataSet);

        Log.d("Tag", "*****" + entries.toString() + Float.toString(max) + Float.toString(min));
        LineData lineData = new LineData(iLineDataSets);
        lineChart.setData(lineData);
        //lineChart.getXAxis().setLabelCount(max-min+1, true);
        lineChart.getXAxis().setAxisMinimum(min-0.5f);
        lineChart.getXAxis().setAxisMaximum(max+0.5f);

        lineChart.setTouchEnabled(true);
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);

    }
    private void binomial() {
        barChart = (BarChart) findViewById(R.id.barGraph);

        Intent intent = getIntent();
        results = new ArrayList<>();

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
        barEntries = new ArrayList<>();
        frequency = new ArrayList<>();

        /*for (i = 0; i < results.size(); i++) {
            frequency.add()
        }*/
        Collections.sort(results);
        for (i = 0; i < results.size(); i++) {
            barEntries.add(new BarEntry(Float.valueOf(String.valueOf(results.get(i))), Collections.frequency(results, results.get(i))));
        }
        BarDataSet barDataSet = new BarDataSet(barEntries, "results");

        BarData barData = new BarData(barDataSet);
        barChart.setData(barData);

        barChart.setTouchEnabled(true);
        barChart.setDragEnabled(true);
        barChart.setScaleEnabled(true);
    }
    private void measurement() {
        barChart = (BarChart) findViewById(R.id.barGraph);

        Intent intent = getIntent();
        results = new ArrayList<>();

        measurementTrials = new ArrayList<>();
        measurementTrials = (ArrayList<MeasurementTrial>) intent.getSerializableExtra("trialDataList");
        for (i = 0; i < measurementTrials.size(); i++) {
            results.add(Double.valueOf(measurementTrials.get(i).getMeasurement()));
        }
        barEntries = new ArrayList<>();
        frequency = new ArrayList<>();

        /*for (i = 0; i < results.size(); i++) {
            frequency.add()
        }*/
        Collections.sort(results);
        for (i = 0; i < results.size(); i++) {
            barEntries.add(new BarEntry(Float.valueOf(String.valueOf(results.get(i))), Collections.frequency(results, results.get(i))));
        }
        BarDataSet barDataSet = new BarDataSet(barEntries, "results");

        BarData barData = new BarData(barDataSet);
        barChart.setData(barData);

        barChart.setTouchEnabled(true);
        barChart.setDragEnabled(true);
        barChart.setScaleEnabled(true);
    }
}
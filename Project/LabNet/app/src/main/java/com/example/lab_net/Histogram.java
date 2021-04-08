package com.example.lab_net;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.Collections;

public class Histogram extends AppCompatActivity {

    BarChart barChart;
    ArrayList<BarEntry> barEntries;
    LineChart lineChart;
    ArrayList<ILineDataSet> iLineDataSets;
    ArrayList<Entry> entries;
    private ArrayList<Double> results;
    ArrayList<String> trialTitles;
    private ArrayList<NonNegativeIntegerTrial> nonNegativeTrials;
    private ArrayList <CountTrial> countTrials;
    private ArrayList <MeasurementTrial> measurementTrials;
    private ArrayList <BinomialTrial> binomialTrials;
    private ArrayList<Double> frequency;

    TextView xAxisTitle, yAxisTitle;
    private int i;
    int checkActivity;
    float j;
    private ArrayList<String> dateDataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_histogram);

         xAxisTitle = (TextView)findViewById(R.id.xAxisTitle);
         yAxisTitle = (TextView)findViewById(R.id.tyAxisTitle);

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
        trialTitles = new ArrayList<>();
        results = new ArrayList<>();

        nonNegativeTrials = new ArrayList<>();
        nonNegativeTrials = (ArrayList<NonNegativeIntegerTrial>) intent.getSerializableExtra("trialDataList");
        for (i = 0; i < nonNegativeTrials.size(); i++) {
            results.add(Double.valueOf(nonNegativeTrials.get(i).getNonNegativeCount()));
        }
        for (i = 0; i < nonNegativeTrials.size(); i++) {
            trialTitles.add(nonNegativeTrials.get(i).getTitle());
        }

        xAxisTitle.setText("Trials");
        yAxisTitle.setText("Results");

        barEntries = new ArrayList<>();

        for (i = 0; i < results.size(); i++) {
            barEntries.add(new BarEntry(i, Float.valueOf(String.valueOf(results.get(i)))));
        }
        BarDataSet barDataSet = new BarDataSet(barEntries, null);
        barDataSet.setColors(new int[] {Color.rgb(132, 180, 200),
                                        Color.rgb(244, 220, 214)});

        BarData barData = new BarData(barDataSet);
        barChart.setData(barData);

        ValueFormatter formatter = new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                return trialTitles.get((int) value);
            }
        };
        XAxis xAxis = barChart.getXAxis();
        xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
        xAxis.setValueFormatter(formatter);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(5);
        barChart.getAxisRight().setEnabled(false);
        barChart.getLegend().setEnabled(false);
        barChart.getDescription().setEnabled(false);
        barChart.setExtraBottomOffset(10);

        barChart.setTouchEnabled(true);
        barChart.setDragEnabled(true);
        barChart.setScaleEnabled(true);

        //line chart
        lineChart = (LineChart) findViewById(R.id.lineChart);
        entries = new ArrayList<>();
        for(i = 0; i < results.size(); i++){
            entries.add(new Entry(i,Float.valueOf(String.valueOf(results.get(i)))));
        }

        LineDataSet lineDataSet = new LineDataSet(entries, null);

        iLineDataSets = new ArrayList<>();
        iLineDataSets.add(lineDataSet);
        lineDataSet.setColor(Color.rgb(132, 180, 200));
        LineData lineData = new LineData(iLineDataSets);
        lineChart.setData(lineData);
        XAxis lxAxis = lineChart.getXAxis();
        lxAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        lineChart.getAxisRight().setEnabled(false);
        lineChart.getLegend().setEnabled(false);
        lineChart.getDescription().setEnabled(false);
        lineChart.setExtraBottomOffset(10);

        lineChart.setTouchEnabled(true);
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);

    }

    public void count() {
        barChart = (BarChart) findViewById(R.id.barGraph);

        Intent intent = getIntent();
        trialTitles = new ArrayList<>();
        results = new ArrayList<>();
        countTrials = new ArrayList<>();
        countTrials = (ArrayList<CountTrial>) intent.getSerializableExtra("trialDataList");

        for (i = 0; i < countTrials.size(); i++) {
            results.add(Double.valueOf(countTrials.get(i).getCount()));
        }
        for (i = 0; i < countTrials.size(); i++) {
            trialTitles.add(countTrials.get(i).getTitle());
        }

        xAxisTitle.setText("Trials");
        yAxisTitle.setText("Results");

        barEntries = new ArrayList<>();
        for (i = 0; i < results.size(); i++) {
            barEntries.add(new BarEntry(i, Float.valueOf(String.valueOf(results.get(i)))));
        }

        BarDataSet barDataSet = new BarDataSet(barEntries,null);
        barDataSet.setColors(new int[] {Color.rgb(132, 180, 200),
                Color.rgb(244, 220, 214)});

        BarData barData = new BarData(barDataSet);
        barChart.setData(barData);

        ValueFormatter formatter = new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                return trialTitles.get((int) value);
            }
        };
        XAxis xAxis = barChart.getXAxis();
        xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
        xAxis.setValueFormatter(formatter);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(5);
        barChart.getAxisRight().setEnabled(false);
        barChart.getLegend().setEnabled(false);
        barChart.getDescription().setEnabled(false);
        barChart.setExtraBottomOffset(10);

        barChart.setTouchEnabled(true);
        barChart.setDragEnabled(true);
        barChart.setScaleEnabled(true);

        //line chart
        lineChart = (LineChart) findViewById(R.id.lineChart);
        entries = new ArrayList<>();
        for(i = 0; i < results.size(); i++){
            entries.add(new Entry(i,Float.valueOf(String.valueOf(results.get(i)))));
        }

        LineDataSet lineDataSet = new LineDataSet(entries, null);

        iLineDataSets = new ArrayList<>();
        iLineDataSets.add(lineDataSet);
        lineDataSet.setColor(Color.rgb(132, 180, 200));
        LineData lineData = new LineData(iLineDataSets);
        lineChart.setData(lineData);
        XAxis lxAxis = lineChart.getXAxis();
        lxAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        lineChart.getAxisRight().setEnabled(false);
        lineChart.getLegend().setEnabled(false);
        lineChart.getDescription().setEnabled(false);
        lineChart.setExtraBottomOffset(10);

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
        int passFrequency = 0;
        int failFrequency = 0;

        for (i = 0; i < results.size(); i++) {
            //if pass
            if (results.get(i) == 1.0){
                passFrequency++;
            }
            else if (results.get(i) == 0.0){
                failFrequency++;
            }

        }

        xAxisTitle.setText("Results");
        yAxisTitle.setText("Frequency");

        barEntries.add(new BarEntry(0f, passFrequency));
        barEntries.add(new BarEntry(1f, failFrequency));

        BarDataSet barDataSet = new BarDataSet(barEntries,null);
        barDataSet.setColors(new int[] {Color.rgb(132, 180, 200),
                                        Color.rgb(244, 220, 214)});

        BarData barData = new BarData(barDataSet);
        barChart.setData(barData);

        final String[] xAxisLabels = new String[] { "Pass", "Fail"};
        ValueFormatter formatter = new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                return xAxisLabels[(int) value];
            }
        };
        XAxis xAxis = barChart.getXAxis();
        xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
        xAxis.setValueFormatter(formatter);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(20);
        barChart.getAxisRight().setEnabled(false);
        barChart.getLegend().setEnabled(false);
        barChart.getDescription().setEnabled(false);
        barChart.setExtraBottomOffset(10);

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
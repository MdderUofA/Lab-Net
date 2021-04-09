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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

/**
 * Analysis trail data and create bar charts and line charts for each experiment.
 */
public class Histogram extends AppCompatActivity {

    TextView xAxisTitle, yAxisTitle;

    BarChart barChart;
    ArrayList<BarEntry> barEntries;
    LineChart lineChart;
    ArrayList<ILineDataSet> iLineDataSets;
    ArrayList<Entry> entries;
    ArrayList<Double> results;
    ArrayList<String> trialTitles;

    private ArrayList<NonNegativeIntegerTrial> nonNegativeTrials;
    private ArrayList <CountTrial> countTrials;
    private ArrayList <MeasurementTrial> measurementTrials;
    private ArrayList <BinomialTrial> binomialTrials;

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

        //bar chart
        barChart = (BarChart) findViewById(R.id.barGraph);
        //y-axis
        trialTitles = new ArrayList<>();
        //x-axis
        results = new ArrayList<>();

        //get values
        Intent intent = getIntent();
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

        //add values to bar entries
        barEntries = new ArrayList<>();
        for (i = 0; i < results.size(); i++) {
            barEntries.add(new BarEntry(i, Float.valueOf(String.valueOf(results.get(i)))));
        }
        BarDataSet barDataSet = new BarDataSet(barEntries, null);

        //set color of the bars
        barDataSet.setColors(new int[] {Color.rgb(132, 180, 200),
                                        Color.rgb(244, 220, 214)});

        //set data to chart
        BarData barData = new BarData(barDataSet);
        barChart.setData(barData);

        //set x-axis to trial titles
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

        //get data
        entries = new ArrayList<>();
//        for(i = 0; i < results.size(); i++){
//            entries.add(new Entry(i,Float.valueOf(String.valueOf(results.get(i)))));
//        }
        dateDataList = new ArrayList<>();
        dateDataList = (ArrayList<String>) intent.getSerializableExtra("dateDataList");
        for(i = 0; i < dateDataList.size(); i++){
            entries.add(new Entry(Float.valueOf(dateDataList.get(i)),i));
        }

        //set data
        LineDataSet lineDataSet = new LineDataSet(entries, null);
        iLineDataSets = new ArrayList<>();
        iLineDataSets.add(lineDataSet);
        LineData lineData = new LineData(iLineDataSets);
        lineChart.setData(lineData);

        //set color of line
        lineDataSet.setColor(Color.rgb(132, 180, 200));

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
        //bar chart
        barChart = (BarChart) findViewById(R.id.barGraph);
        //y-axis
        trialTitles = new ArrayList<>();
        //x-axis
        results = new ArrayList<>();

        //get values
        Intent intent = getIntent();
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

        //add values to bar entries
        barEntries = new ArrayList<>();
        for (i = 0; i < results.size(); i++) {
            barEntries.add(new BarEntry(i, Float.valueOf(String.valueOf(results.get(i)))));
        }
        //set data
        BarDataSet barDataSet = new BarDataSet(barEntries,null);
        BarData barData = new BarData(barDataSet);
        barChart.setData(barData);

        //set color of bars
        barDataSet.setColors(new int[] {Color.rgb(132, 180, 200),
                Color.rgb(244, 220, 214)});

        //set x-axis to trial titles
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



        //get data
        entries = new ArrayList<>();
//        for(i = 0; i < results.size(); i++){
//            entries.add(new Entry(i,Float.valueOf(String.valueOf(results.get(i)))));
//        }
        dateDataList = new ArrayList<>();
        dateDataList = (ArrayList<String>) intent.getSerializableExtra("dateDataList");
        for(i = 0; i < dateDataList.size(); i++){
            entries.add(new Entry(Float.valueOf(dateDataList.get(i)),i));
        }

        //set data
        LineDataSet lineDataSet = new LineDataSet(entries, null);
        iLineDataSets = new ArrayList<>();
        iLineDataSets.add(lineDataSet);
        LineData lineData = new LineData(iLineDataSets);
        lineChart.setData(lineData);

        ArrayList<String> values = new ArrayList<>();
        for(i = 0; i < dateDataList.size(); i++){
            values.add(dateDataList.get(i));
        }
        XAxis xAxis1 = lineChart.getXAxis();

        xAxis1.setValueFormatter(new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                Date date = new Date((long)value);
                //Specify the format you'd like
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/YYYY", Locale.getDefault());
                return sdf.format(date);
            }
        });

        xAxis1.setPosition(XAxis.XAxisPosition.BOTTOM);


        //set color of line
        lineDataSet.setColor(Color.rgb(132, 180, 200));

/*        XAxis lxAxis = lineChart.getXAxis();
        lxAxis.setPosition(XAxis.XAxisPosition.BOTTOM);*/
        lineChart.getAxisRight().setEnabled(false);
        lineChart.getLegend().setEnabled(false);
        lineChart.getDescription().setEnabled(false);
        lineChart.setExtraBottomOffset(10);

        lineChart.setTouchEnabled(true);
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);

    }

    private void binomial() {
        //bar chart
        barChart = (BarChart) findViewById(R.id.barGraph);
        results = new ArrayList<>();

        //get values
        Intent intent = getIntent();
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

        barEntries = new ArrayList<>();
        barEntries.add(new BarEntry(0f, passFrequency));
        barEntries.add(new BarEntry(1f, failFrequency));

        //set data
        BarDataSet barDataSet = new BarDataSet(barEntries,null);
        BarData barData = new BarData(barDataSet);
        barChart.setData(barData);

        //set color of bars
        barDataSet.setColors(new int[] {Color.rgb(132, 180, 200),
                Color.rgb(244, 220, 214)});

        //set x-axis to pass or fail
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

        lineChart = (LineChart) findViewById(R.id.lineChart);
        entries = new ArrayList<>();
        dateDataList = new ArrayList<>();
        dateDataList = (ArrayList<String>) intent.getSerializableExtra("dateDataList");
        for(i = 0; i < dateDataList.size(); i++){
            entries.add(new Entry(Float.valueOf(dateDataList.get(i)),i));
        }
        LineDataSet lineDataSet = new LineDataSet(entries, "dateDataList");

        iLineDataSets = new ArrayList<>();
        iLineDataSets.add(lineDataSet);

        LineData lineData = new LineData(iLineDataSets);
        lineChart.setData(lineData);

        lineChart.setTouchEnabled(true);
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);

    }
}
package com.example.lab_net;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

/**
 * Analysis trail data and create bar charts and line charts for each experiment.
 *
 * @author Dhaval, Qasim Akhtar, Aryan Kalwani
 */
public class Histogram extends AppCompatActivity {

    TextView xAxisTitle, yAxisTitle;
    TextView barChartTitle;

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
    double pass = 0, fail = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_histogram);

        xAxisTitle = (TextView)findViewById(R.id.xAxisTitle);
        yAxisTitle = (TextView)findViewById(R.id.yAxisTitle);
        barChartTitle = (TextView)findViewById(R.id.barChartTitle);

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

    /**
     * called for Non-Negative trials
     */
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
        barChartTitle.setText("Record of the Trial Values");

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

        HashMap<String, ArrayList<String>> hash1 = new HashMap<String, ArrayList<String>>();

        for (i = 0; i<nonNegativeTrials.size();i++) {
            String trialID = nonNegativeTrials.get(i).getId();
            Log.d("TAGS", "dates are "+trialID);
            FirebaseFirestore db;
            db = FirebaseFirestore.getInstance();

            DocumentReference documentReference = db.collection("Trials").document(trialID);

            documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        if (documentSnapshot.exists()) {
                            String utilDate = documentSnapshot.getData().get("Date").toString();
                            if (hash1.containsKey(utilDate)) {
                                ArrayList<String> util = hash1.get(utilDate);
                                util.add(trialID);
                                hash1.replace(utilDate, util);
                            } else {
                                ArrayList<String> util = new ArrayList<>();
                                util.add(trialID);
                                hash1.put(utilDate, util);
                            }
                        }
                    }
                }
            });
        }
        HashMap<String, Double> results = new HashMap<String, Double>();
        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d("TAG", "dates are" + hash1.toString());
                ArrayList<String> utilDates = new ArrayList<String>(hash1.keySet());


                Collections.sort(utilDates);
                for (int j=0;j<utilDates.size();j++) {
                    ArrayList util = hash1.get(utilDates.get(j));
                    double length = util.size();
                    Log.d("TAG", "length is " + length);

                    for (int k=0;k<util.size();k++) {
                        String trialID = util.get(k).toString();
                        FirebaseFirestore db;
                        db = FirebaseFirestore.getInstance();
                        DocumentReference documentReference = db.collection("Trials").document(trialID);


                        final int index = j;
                        final int index1 = k;
                        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot documentSnapshot = task.getResult();
                                    if (documentSnapshot.exists()) {
                                        double number = Double.valueOf(documentSnapshot.getData().get("Result").toString());
                                        String utilDate = documentSnapshot.getData().get("Date").toString();
                                        if (results.containsKey(utilDate)) {
                                            results.replace(utilDate, (((double) results.get(utilDate))+(number/length)));
                                        } else {
                                            results.put(utilDate, number/length);
                                        }
                                        Log.d("TAG", "aloo1" + results.toString());
                                    }
                                }
                            }
                        });
                    }
                    Log.d("TAG", "abcd" + results.toString());
                }
            }
        }, 500);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d("TAG", "alooo" + results.toString());
            }
        },1000);
        Log.d("TAG", "alooo" + results.toString());
        //linechart
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                lineChart = (LineChart) findViewById(R.id.lineChart);
                entries = new ArrayList<>();
                dateDataList = new ArrayList<>();
                dateDataList = (ArrayList<String>) intent.getSerializableExtra("dateDataList");
                ArrayList<String> xAxisValues = new ArrayList<>();


                XAxis xaxis = lineChart.getXAxis();
                lineChart.getXAxis().setGranularityEnabled(true);
                lineChart.getXAxis().setGranularity(1.0f);

                xaxis.setValueFormatter(new ValueFormatter() {
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
                        return day+"-"+month+"-"+year;
                    }

                });
                HashMap<String, Integer> hashmap = new HashMap<String, Integer>();
                ArrayList<String> utilDates = new ArrayList<String>(hash1.keySet());


                Collections.sort(utilDates);
                dateDataList = new ArrayList<String>(hashmap.keySet());
                Collections.sort(dateDataList);
        /*
        StringBuffer str = new StringBuffer(dateDataList.get(0));
        String utilString = "";
        utilString = utilString + str.toString().substring(0,2);
        utilString = str.toString().substring(2,4) + utilString;
        utilString = str.toString().substring(6,8) + utilString;

        int max=0, min=Integer.valueOf(utilString);
        */
                for(i = 0; i < utilDates.size(); i++){
                    StringBuffer str = new StringBuffer(utilDates.get(i));
                    String utilString = "";
                    utilString = utilString + str.toString().substring(0,2);
                    utilString = str.toString().substring(2,4) + utilString;
                    utilString = str.toString().substring(6,8) + utilString;
                    entries.add(new Entry(Integer.valueOf(utilString), Float.valueOf(String.valueOf(results.get(utilDates.get(i))))));
                }
                LineDataSet lineDataSet = new LineDataSet(entries, "(XAXIS=date) , (YAXIS=count)");

                iLineDataSets = new ArrayList<>();
                iLineDataSets.add(lineDataSet);


                LineData lineData = new LineData(iLineDataSets);
                lineChart.setData(lineData);
                //lineChart.getXAxis().setLabelCount(max-min+1, true);
                lineChart.getDescription().setEnabled(false);
                //lineChart.getXAxis().setAxisMinimum(min-0.5f);
                //lineChart.getXAxis().setAxisMaximum(max+0.5f);
                lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
                lineChart.setTouchEnabled(true);
                lineChart.setDragEnabled(true);
                lineChart.setScaleEnabled(true);
            }
        }, 10000);

    }

    /**
     * called for count trials
     */
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
        barChartTitle.setText("Record of the Trial Values");


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

        //linechart
        lineChart = (LineChart) findViewById(R.id.lineChart);
        entries = new ArrayList<>();
        dateDataList = new ArrayList<>();
        dateDataList = (ArrayList<String>) intent.getSerializableExtra("dateDataList");
        ArrayList<String> xAxisValues = new ArrayList<>();


        XAxis xaxis = lineChart.getXAxis();
        lineChart.getXAxis().setGranularityEnabled(true);
        lineChart.getXAxis().setGranularity(1.0f);

        xaxis.setValueFormatter(new ValueFormatter() {
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
                return day+"-"+month+"-"+year;
            }

        });
        HashMap<String, Integer> hashmap = new HashMap<String, Integer>();

        for (i=0;i<dateDataList.size();i++) {
            if (hashmap.containsKey(dateDataList.get(i))) {
                int temp = hashmap.get(dateDataList.get(i));
                hashmap.replace(dateDataList.get(i),temp+1);
            } else {
                hashmap.put(dateDataList.get(i),1);
            }
        }
        dateDataList = new ArrayList<String>(hashmap.keySet());
        Collections.sort(dateDataList);

        StringBuffer str = new StringBuffer(dateDataList.get(0));
        String utilString = "";
        utilString = utilString + str.toString().substring(0,2);
        utilString = str.toString().substring(2,4) + utilString;
        utilString = str.toString().substring(6,8) + utilString;

        int max=0, min=Integer.valueOf(utilString);

        for(i = 0; i < dateDataList.size(); i++){
            str = new StringBuffer(dateDataList.get(i));
            utilString = "";
            utilString = utilString + str.toString().substring(0,2);
            utilString = str.toString().substring(2,4) + utilString;
            utilString = str.toString().substring(6,8) + utilString;
            entries.add(new Entry(Integer.valueOf(utilString),hashmap.get(dateDataList.get(i))));
            if (Integer.valueOf(utilString)>max) {
                max = Integer.valueOf(utilString);
            }
        }
        LineDataSet lineDataSet = new LineDataSet(entries, "(XAXIS=date) , (YAXIS=count)");

        iLineDataSets = new ArrayList<>();
        iLineDataSets.add(lineDataSet);


        LineData lineData = new LineData(iLineDataSets);
        lineChart.setData(lineData);
        //lineChart.getXAxis().setLabelCount(max-min+1, true);
        lineChart.getDescription().setEnabled(false);
        lineChart.getXAxis().setAxisMinimum(min-0.5f);
        lineChart.getXAxis().setAxisMaximum(max+0.5f);
        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        lineChart.setTouchEnabled(true);
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);
    }

    /**
     * called for binomial trials
     */
    private void binomial() {
        //bar chart
        barChart = (BarChart) findViewById(R.id.barGraph);
        results = new ArrayList<>();

        //get values
        Intent intent = getIntent();
        binomialTrials = new ArrayList<>();
        binomialTrials = (ArrayList<BinomialTrial>) intent.getSerializableExtra("trialDataList");

        ArrayList<String> dateList;

        HashMap<String, ArrayList<String>> hash = new HashMap<>();
        for (i = 0; i < binomialTrials.size(); i++) {
            if (binomialTrials.get(i).getResult().toLowerCase().equals("pass")) {
                results.add(1.0);
            } else {
                results.add(0.0);
            }

            String trialID = binomialTrials.get(i).getId();
            Log.d("TAGS", "dates are "+trialID);
            FirebaseFirestore db;
            db = FirebaseFirestore.getInstance();

            DocumentReference documentReference = db.collection("Trials").document(trialID);

            documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        if (documentSnapshot.exists()) {
                            String utilDate = documentSnapshot.getData().get("Date").toString();
                            if (hash.containsKey(utilDate)) {
                                ArrayList<String> util = hash.get(utilDate);
                                util.add(trialID);
                                hash.replace(utilDate, util);
                            } else {
                                ArrayList<String> util = new ArrayList<>();
                                util.add(trialID);
                                hash.put(utilDate, util);

                            }
                        }
                    }
                }
            });

        }
        HashMap<String, Double> dateResult = new HashMap<String, Double>();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ArrayList dateList = new ArrayList<String>(hash.keySet());
                Collections.sort(dateList);

                Log.d("TAG", "binomial" + dateList.toString());
                for (int i = 0; i < dateList.size(); i++) {
                    ArrayList insideArray = hash.get(dateList.get(i));

                    final int index = i;
                    for (int j = 0; j < insideArray.size(); j++) {
                        FirebaseFirestore db;
                        db = FirebaseFirestore.getInstance();
                        DocumentReference documentReference = db.collection("Trials").document(insideArray.get(j).toString());

                        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot documentSnapshot = task.getResult();
                                    if (documentSnapshot.exists()) {
                                        String utilResult = documentSnapshot.getData().get("Result").toString();
                                        if (utilResult.equals("pass")) {
                                            pass = pass + 1;
                                        } else {
                                            fail = fail + 1;
                                        }
                                        dateResult.put((String) dateList.get(index),(pass)/(pass+fail));
                                        Log.d("TAG", "dateResults " + dateResult.toString() + pass + fail);
                                    }
                                }
                            }
                        });
                    }

                    Log.d("TAG", "dateResults " + dateResult.toString());
                    Log.d("TAGS", "dates are "+ hash.toString());
                }
            }
        }, 500);




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
        barChartTitle.setText("Record of the Trial Values");


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

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //linechart
                lineChart = (LineChart) findViewById(R.id.lineChart);
                entries = new ArrayList<>();
                dateDataList = new ArrayList<>();
                dateDataList = (ArrayList<String>) intent.getSerializableExtra("dateDataList");
                ArrayList<String> xAxisValues = new ArrayList<>();


                XAxis xaxis = lineChart.getXAxis();
                lineChart.getXAxis().setGranularityEnabled(true);
                lineChart.getXAxis().setGranularity(1.0f);

                xaxis.setValueFormatter(new ValueFormatter() {
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
                        return day+"-"+month+"-"+year;
                    }

                });

                dateDataList = new ArrayList<String>(dateResult.keySet());
                Collections.sort(dateDataList);

                StringBuffer str = new StringBuffer(dateDataList.get(0));
                String utilString = "";
                utilString = utilString + str.toString().substring(0,2);
                utilString = str.toString().substring(2,4) + utilString;
                utilString = str.toString().substring(6,8) + utilString;

                int max = 0, min = Integer.valueOf(utilString);
                for(i = 0; i < dateDataList.size(); i++){
                    str = new StringBuffer(dateDataList.get(i));
                    utilString = "";
                    utilString = utilString + str.toString().substring(0,2);
                    utilString = str.toString().substring(2,4) + utilString;
                    utilString = str.toString().substring(6,8) + utilString;
                    entries.add(new Entry(Integer.valueOf(utilString),Float.valueOf(String.valueOf(dateResult.get(dateDataList.get(i))))));
                    if (Integer.valueOf(utilString)>max) {
                        max = Integer.valueOf(utilString);
                    }
                }
                LineDataSet lineDataSet = new LineDataSet(entries, "(XAXIS=date) , (YAXIS=count)");

                iLineDataSets = new ArrayList<>();
                iLineDataSets.add(lineDataSet);


                LineData lineData = new LineData(iLineDataSets);
                lineChart.setData(lineData);
                //lineChart.getXAxis().setLabelCount(max-min+1, true);
                lineChart.getDescription().setEnabled(false);
                lineChart.getXAxis().setAxisMinimum(min-0.5f);
                lineChart.getXAxis().setAxisMaximum(max+0.5f);
                lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
                lineChart.setTouchEnabled(true);
                lineChart.setDragEnabled(true);
                lineChart.setScaleEnabled(true);
            }
        },1000);


    }

    /**
     * called for measurement Trials
     */
    private void measurement() {
        //bar chart
        barChart = (BarChart) findViewById(R.id.barGraph);
        //y-axis
        trialTitles = new ArrayList<>();
        //x-axis
        results = new ArrayList<>();

        //get values
        Intent intent = getIntent();
        measurementTrials = new ArrayList<>();
        measurementTrials = (ArrayList<MeasurementTrial>) intent.getSerializableExtra("trialDataList");
        for (i = 0; i < measurementTrials.size(); i++) {
            results.add(Double.valueOf(measurementTrials.get(i).getMeasurement()));
            trialTitles.add(measurementTrials.get(i).getTitle());
        }
        /*
        for (i = 0; i < nonNegativeTrials.size(); i++) {
            trialTitles.add(nonNegativeTrials.get(i).getTitle());
        }
    */


        xAxisTitle.setText("Trials");
        yAxisTitle.setText("Results");
        barChartTitle.setText("Record of the Trial Values");

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

        HashMap<String, ArrayList<String>> hash1 = new HashMap<String, ArrayList<String>>();

        for (i = 0; i<measurementTrials.size();i++) {
            String trialID = measurementTrials.get(i).getId();
            Log.d("TAGS", "dates are "+trialID);
            FirebaseFirestore db;
            db = FirebaseFirestore.getInstance();

            DocumentReference documentReference = db.collection("Trials").document(trialID);

            documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        if (documentSnapshot.exists()) {
                            String utilDate = documentSnapshot.getData().get("Date").toString();
                            if (hash1.containsKey(utilDate)) {
                                ArrayList<String> util = hash1.get(utilDate);
                                util.add(trialID);
                                hash1.replace(utilDate, util);
                            } else {
                                ArrayList<String> util = new ArrayList<>();
                                util.add(trialID);
                                hash1.put(utilDate, util);
                            }
                        }
                    }
                }
            });
        }
        HashMap<String, Double> results = new HashMap<String, Double>();
        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d("TAG", "dates are" + hash1.toString());
                ArrayList<String> utilDates = new ArrayList<String>(hash1.keySet());


                Collections.sort(utilDates);
                for (int j=0;j<utilDates.size();j++) {
                    ArrayList util = hash1.get(utilDates.get(j));
                    double length = util.size();

                    for (int k=0;k<util.size();k++) {
                        String trialID = util.get(k).toString();
                        FirebaseFirestore db;
                        db = FirebaseFirestore.getInstance();
                        DocumentReference documentReference = db.collection("Trials").document(trialID);


                        final int index = j;
                        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot documentSnapshot = task.getResult();
                                    if (documentSnapshot.exists()) {
                                        double number = Double.valueOf(documentSnapshot.getData().get("Result").toString());
                                        String utilDate = documentSnapshot.getData().get("Date").toString();
                                        if (results.containsKey(utilDate)) {
                                            results.replace(utilDate, ((double) results.get(utilDate))+(number/length));
                                        } else {
                                            results.put(utilDate, number/length);
                                        }
                                        Log.d("TAG", "aloo1" + results.toString());
                                    }
                                }
                            }
                        });
                    }
                    Log.d("TAG", "abcd" + results.toString());
                }
            }
        }, 500);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d("TAG", "alooo" + results.toString());
            }
        },1000);
        Log.d("TAG", "alooo" + results.toString());
        //linechart
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                lineChart = (LineChart) findViewById(R.id.lineChart);
                entries = new ArrayList<>();
                dateDataList = new ArrayList<>();
                dateDataList = (ArrayList<String>) intent.getSerializableExtra("dateDataList");
                ArrayList<String> xAxisValues = new ArrayList<>();


                XAxis xaxis = lineChart.getXAxis();
                lineChart.getXAxis().setGranularityEnabled(true);
                lineChart.getXAxis().setGranularity(1.0f);

                xaxis.setValueFormatter(new ValueFormatter() {
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
                        return day+"-"+month+"-"+year;
                    }

                });
                HashMap<String, Integer> hashmap = new HashMap<String, Integer>();
                ArrayList<String> utilDates = new ArrayList<String>(hash1.keySet());


                Collections.sort(utilDates);
                dateDataList = new ArrayList<String>(hashmap.keySet());
                Collections.sort(dateDataList);
        /*
        StringBuffer str = new StringBuffer(dateDataList.get(0));
        String utilString = "";
        utilString = utilString + str.toString().substring(0,2);
        utilString = str.toString().substring(2,4) + utilString;
        utilString = str.toString().substring(6,8) + utilString;

        int max=0, min=Integer.valueOf(utilString);
        */
                for(i = 0; i < utilDates.size(); i++){
                    StringBuffer str = new StringBuffer(utilDates.get(i));
                    String utilString = "";
                    utilString = utilString + str.toString().substring(0,2);
                    utilString = str.toString().substring(2,4) + utilString;
                    utilString = str.toString().substring(6,8) + utilString;
                    entries.add(new Entry(Integer.valueOf(utilString), Float.valueOf(String.valueOf(results.get(utilDates.get(i))))));
                }
                LineDataSet lineDataSet = new LineDataSet(entries, "(XAXIS=date) , (YAXIS=count)");

                iLineDataSets = new ArrayList<>();
                iLineDataSets.add(lineDataSet);


                LineData lineData = new LineData(iLineDataSets);
                lineChart.setData(lineData);
                //lineChart.getXAxis().setLabelCount(max-min+1, true);
                lineChart.getDescription().setEnabled(false);
                //lineChart.getXAxis().setAxisMinimum(min-0.5f);
                //lineChart.getXAxis().setAxisMaximum(max+0.5f);
                lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
                lineChart.setTouchEnabled(true);
                lineChart.setDragEnabled(true);
                lineChart.setScaleEnabled(true);
            }
        }, 10000);

    }
}


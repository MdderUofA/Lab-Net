package com.example.lab_net;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class Statistics extends AppCompatActivity {

    TextView meanView;
    long sum = 0, mean = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stats_dialog);


        AlertDialog.Builder settingsBuilder = new AlertDialog.Builder(Statistics.this);
        View settingsView = getLayoutInflater().inflate(R.layout.stats_dialog, null);

        meanView = (TextView) settingsView.findViewById(R.id.mean_view);
        //medianView = (TextView) settingsView.findViewById(R.id.median_view);
        //done = (Button) settingsView.findViewById(R.id.updateButton);

        settingsBuilder.setView(settingsView);
        AlertDialog setDialog = settingsBuilder.create();
        setDialog.setCanceledOnTouchOutside(true);
        setDialog.show();


        ArrayList<Trial> trials = (ArrayList<Trial>) getIntent().getSerializableExtra("trialList");
        ArrayList<Long> results = new ArrayList<>();

        for(int i = 0; i < trials.size(); i++){
            Trial trial = trials.get(0);
            Long result = trial.getResult();
            results.add(result);
            sum = sum + result;
        }

        mean = (sum/(trials.size()));
        setDialog.show();

        meanView.setText("Mean: " + mean);




    }
}
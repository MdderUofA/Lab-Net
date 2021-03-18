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
    ArrayList<CountTrial> trials;
    ArrayList<Long> results;
    int i;

    Button doneButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stats_dialog);
        meanView = (TextView) findViewById(R.id.mean_view);

        //get from ExperimentActivity
        Intent intent = getIntent();
        results = new ArrayList<>();
        results = (ArrayList<Long>) intent.getSerializableExtra("resultList");

        for (i = 0; i < results.size(); i++) {
            sum = results.get(i) + sum;
        }
        if (results.size() != 0) {
            mean = (sum / (Long.valueOf(results.size())));
        } else {
            mean = 0;
        }
        sum = 0;

        doneButton = (Button) findViewById(R.id.doneButton);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(getApplicationContext(), ExperimentActivity.class);
                startActivity(intent1);
            }
        });
        /*for (i = 0; i < trials.size(); i++) {
            Trial trial = trials.get(1);
            results.add((long) 4);
            sum = results.get(i) + sum;

            if (results.size() != 0) {
                mean = (sum / (Long.valueOf(results.size())));
            } else {
                mean = 0;
            }
            sum = 0;*/

        // mean = (sum/(trials.size()));

        meanView.setText("Mean: " + mean);


    }

}
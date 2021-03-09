package com.example.lab_net;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

public class ExperimentActivity extends AppCompatActivity {
    private String UserId;
    private String experimentName;
    private Experiment experiment;
    TextView Description, Region, trials ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.experiment_owner_activity);

        //Intent intent = new Intent();

        //experiment = intent.getSerializableExtra(); // get full experiment object after pop up fragment in user so Team 1 will send this hear and we recieve it
        experiment = new Experiment ("1234", "Titration", "Edmonton", 2);
        Description = findViewById(R.id.experimentDescription);
        Region = findViewById(R.id.experimentRegion);
        trials = findViewById(R.id.trialsLabel);


        Description.setText(experiment.getDescription());
        Region.setText("Region: " + experiment.getRegion());



    }
}
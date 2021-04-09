/**
 * CMPUT 301
 * @version 1.0
 * March 19, 2021
 *
 */
package com.example.lab_net;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

/**
 * Custom listview to display experiments created by the user
 * @author Qasim Akhtar
 */
public class CustomMyExperimentsList extends ArrayAdapter<Experiment> {
    private ArrayList<Experiment> experiments;
    private Context context;

    public CustomMyExperimentsList(Context context, ArrayList<Experiment> experiments){
        super(context,0, experiments);
        this.experiments = experiments;
        this.context = context;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        Experiment experiment= experiments.get(position);
        String type = experiment.getTrialType();
        if(view == null){
            if (type.equals("Binomial")){
                view = LayoutInflater.from(context).inflate(R.layout.content_binomial_experiment, parent,false);
            }
            if (type.equals("Count-based")){
                view = LayoutInflater.from(context).inflate(R.layout.content_count_experiment, parent,false);
            }
            if (type.equals("Measurement")){
                view = LayoutInflater.from(context).inflate(R.layout.content_measuremnt_experiment, parent,false);
            }
            if (type.equals("NonNegativeInteger")){
                view = LayoutInflater.from(context).inflate(R.layout.content_nonnegative_experiment, parent,false);
            }

        }

        TextView experimentTitleText = view.findViewById(R.id.experiment_title);

        experimentTitleText.setText(experiment.getTitle());

        return view;

    }
}

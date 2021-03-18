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
 * This class is for displaying the experiments created by the user.
 *
 * @author  Qasim Akhtar
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

        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.content_experiment, parent,false);
        }

        Experiment experiment= experiments.get(position);
        TextView experimentTitleText = view.findViewById(R.id.experiment_title);

        experimentTitleText.setText(experiment.getTitle());

        return view;

    }
}

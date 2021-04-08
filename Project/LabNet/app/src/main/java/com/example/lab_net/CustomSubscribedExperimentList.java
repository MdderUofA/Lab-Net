package com.example.lab_net;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class CustomSubscribedExperimentList extends ArrayAdapter<SubscribedExperiment> {
    private ArrayList<SubscribedExperiment> subscribedExperiments;
    private Context context;
    FirebaseFirestore db;

    public CustomSubscribedExperimentList(Context context, ArrayList<SubscribedExperiment> subscribedExperiments){
        super(context,0, subscribedExperiments);
        this.subscribedExperiments = subscribedExperiments;
        this.context = context;
        db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        SubscribedExperiment subexperiment= subscribedExperiments.get(position);
        String type = subexperiment.getTrialType();
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

        experimentTitleText.setText(subexperiment.getTitle());

        return view;

    }
}

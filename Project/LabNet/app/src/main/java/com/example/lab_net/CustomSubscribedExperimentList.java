package com.example.lab_net;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
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

        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.content_experiment, parent,false);
        }

        SubscribedExperiment subscribedExperiment = subscribedExperiments.get(position);
        TextView experimentTitleText = view.findViewById(R.id.experiment_title);

        experimentTitleText.setText(subscribedExperiment.getTitle());

        return view;

    }
}

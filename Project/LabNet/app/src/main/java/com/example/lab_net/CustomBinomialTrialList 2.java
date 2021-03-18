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

public class CustomBinomialTrialList extends ArrayAdapter<BinomialTrial> {
    private ArrayList<BinomialTrial> trials;
    private Context context;

    public CustomBinomialTrialList(Context context, ArrayList<BinomialTrial> trials){
        super(context,0, trials);
        this.trials = trials;
        this.context = context;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//        return super.getView(position, convertView, parent);
        View view = convertView;

        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.content_binomial_trial, parent,false);
        }

        BinomialTrial trial = trials.get(position);
        TextView titleText = view.findViewById(R.id.title);
        TextView pass = view.findViewById(R.id.pass);
        TextView fail = view.findViewById(R.id.fail);

        titleText.setText(trial.getTitle());
        pass.setText(""+trial.getPass());
        fail.setText(""+trial.getFail());


        return view;

    }
}


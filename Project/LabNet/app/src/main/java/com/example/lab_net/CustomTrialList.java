package com.example.lab_net;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class CustomTrialList extends ArrayAdapter<CountTrial> {

    private ArrayList<CountTrial> trials;
    private Context context;

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public CustomTrialList(Context context, ArrayList<CountTrial> trials){
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
            view = LayoutInflater.from(context).inflate(R.layout.content, parent,false);
        }

        CountTrial trial = (CountTrial) trials.get(position);
        TextView titleText = view.findViewById(R.id.title_text);
        TextView resultText = view.findViewById(R.id.result_text);
        ImageView trialLocationIcon = view.findViewById(R.id.trialLocationIcon);
        trialLocationIcon.setVisibility(View.INVISIBLE);

        //check if location exists
        db.collection("Trials")
                .whereEqualTo(FieldPath.documentId(), trial.getId())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document.getData().get("Lat") != null) {
                                    trialLocationIcon.setVisibility(View.VISIBLE);
                                }

                            }

                        }
                    }
                });


        titleText.setText(trial.getTitle());
        resultText.setText(trial.getCount().toString());


        return view;

    }
}


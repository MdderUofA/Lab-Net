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

public class CustomAnswerList extends ArrayAdapter<Answer> {
    private ArrayList<Answer> answers;
    private Context context;

    public CustomAnswerList(Context context, ArrayList<Answer> answers){
        super(context,0, answers);
        this.answers = answers;
        this.context = context;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.content_answer, parent,false);
        }

        Answer answer = answers.get(position);
        TextView answerText = view.findViewById(R.id.answer_text);

        answerText.setText(answer.getAnswerText());

        return view;

    }
}

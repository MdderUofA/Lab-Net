package com.example.lab_net;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.lab_net.BinomialTrial;
import com.example.lab_net.Question;
import com.example.lab_net.R;

import java.util.ArrayList;


public class CustomQuestionList extends ArrayAdapter<Question> {
    private ArrayList<Question> questions;
    private Context context;

    public CustomQuestionList(Context context, ArrayList<Question> questions){
        super(context,0, questions);
        this.questions = questions;
        this.context = context;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.content_question, parent,false);
        }

        Question question = questions.get(position);
        TextView questionText = view.findViewById(R.id.question_text);

        questionText.setText(question.getQuestionText());

        return view;

    }
}

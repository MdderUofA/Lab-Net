package com.example.lab_net;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;

public class Questions extends AppCompatActivity {

    private FirebaseFirestore db;
    final String TAG = "sample";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions);

        db = FirebaseFirestore.getInstance();

        String experimentID = getIntent().getStringExtra("experimentID");

        ArrayList<String> questionList = new ArrayList<String>();
        ArrayAdapter<String> questionAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, questionList);

        Button addQuestion = findViewById(R.id.addQuestionButton);

        addQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder settingsBuilder = new AlertDialog.Builder(Questions.this);
                View settingsView = getLayoutInflater().inflate(R.layout.add_question_popup, null);

                EditText questionText = settingsView.findViewById(R.id.addQuestion);
                Button addButtonQuestion = settingsView.findViewById(R.id.addButtonQuestion);

                settingsBuilder.setView(settingsView);
                AlertDialog setDialog = settingsBuilder.create();
                setDialog.setCanceledOnTouchOutside(true);

                addButtonQuestion.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        HashMap<String, String> newQuestion = new HashMap<String, String>();
                        newQuestion.put("experimentID", experimentID);
                        newQuestion.put("questionText", questionText.getText().toString());

                        String questionID = db.collection("Questions").document().getId();

                        db.collection("Questions")
                                .document(questionID)
                                .set(newQuestion)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "Question added");
                                        setDialog.dismiss();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d(TAG, "cannot add question");
                                        setDialog.dismiss();
                                    }
                                });
                    }
                });

                setDialog.show();


            }
        });
    }

}
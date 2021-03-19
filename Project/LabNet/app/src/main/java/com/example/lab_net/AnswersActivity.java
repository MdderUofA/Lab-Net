package com.example.lab_net;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;

public class AnswersActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    final String TAG = "sample";
    private ArrayList<Answer> answersDataList;
    private ArrayAdapter<Answer> answerAdapter;
    private ListView answerList;
    private String questionID;
    private String question_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answers);

        db = FirebaseFirestore.getInstance();

        questionID = getIntent().getStringExtra("questionID");
        question_text = getIntent().getStringExtra("question_text");

        Button addAnswerButton = findViewById(R.id.addAnswerButton);
        answerList = findViewById(R.id.answerList);
        TextView questionText = findViewById(R.id.questionText);
        questionText.setText(question_text);

        answersDataList = new ArrayList<>();
        answerAdapter = new CustomAnswerList(this, answersDataList);

        answerList.setAdapter(answerAdapter);

        CollectionReference collectionReference = db.collection("Answers");

        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                answersDataList.clear();
                for(QueryDocumentSnapshot doc : value){
                    String answerId = doc.getId();
                    String questionId = doc.getData().get("questionID").toString();
                    if(questionId.equals(questionID)) {
                        String answer_text = (String) doc.getData().get("answerText");
                        answersDataList.add(new Answer(questionId, answerId, answer_text));
                        answerAdapter.notifyDataSetChanged();
                    }
                }
            }
        });

        getAnswers();

        addAnswerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder settingsBuilder = new AlertDialog.Builder(AnswersActivity.this);
                View settingsView = getLayoutInflater().inflate(R.layout.add_answer_popup, null);

                EditText answerText = settingsView.findViewById(R.id.addAnswer);
                Button addButtonAnswer = settingsView.findViewById(R.id.addButtonAnswer);

                settingsBuilder.setView(settingsView);
                AlertDialog setDialog = settingsBuilder.create();
                setDialog.setCanceledOnTouchOutside(true);

                addButtonAnswer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        HashMap<String, String> newAnswer = new HashMap<String, String>();
                        newAnswer.put("questionID", questionID);
                        newAnswer.put("answerText", answerText.getText().toString());

                        String answerID = db.collection("Answers").document().getId();

                        db.collection("Answers")
                                .document(answerID)
                                .set(newAnswer)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "Answer added");
                                        setDialog.dismiss();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d(TAG, "cannot add answer");
                                        setDialog.dismiss();
                                    }
                                });
                    }
                });
                setDialog.show();
            }
        });

    }
    public void getAnswers() {
        db.collection("Answers")
                .whereEqualTo("questionID", questionID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            answersDataList.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String answerId = document.getId();
                                String answerText = document.getData().get("answerText").toString();
                                answersDataList.add(new Answer(questionID,answerId,answerText));
                                answerAdapter.notifyDataSetChanged();
                            }
                        }
                    }

                });

    }
}

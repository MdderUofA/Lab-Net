package com.example.lab_net;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

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

public class QuestionsActivity extends AppCompatActivity {

    public static final String EXPERIMENT_ID_EXTRA = "com.example.lab_net.experiment_activity.id";


    public static final String CHECK_EXTRA = "com.example.lab_net.questions_activity.check";

    private FirebaseFirestore db;
    final String TAG = "sample";
    private ArrayList<Question> questionsDataList;
    private ArrayAdapter<Question> questionAdapter;
    private ListView questionList;
    private String experimentID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions);

        db = FirebaseFirestore.getInstance();

        experimentID = getIntent().getStringExtra(EXPERIMENT_ID_EXTRA);
        String check = getIntent().getStringExtra(QuestionsActivity.CHECK_EXTRA);

        Button addQuestion = findViewById(R.id.addQuestionButton);
        questionList = findViewById(R.id.questionList);

        questionsDataList = new ArrayList<>();
        questionAdapter = new CustomQuestionList(this, questionsDataList);

        // sorting questionDataList


        questionList.setAdapter(questionAdapter);

        CollectionReference collectionReference = db.collection("Questions");

//        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
//            @Override
//            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
//                questionsDataList.clear();
//                for(QueryDocumentSnapshot doc : value){
//                    String questionId = doc.getId();
//                    String experimentId = doc.getData().get("experimentID").toString();
//                    if(experimentId.equals(experimentID)) {
//                        String questionText = (String) doc.getData().get("questionText");
//                        questionsDataList.add(new Question(questionId, questionText));
//                        questionAdapter.notifyDataSetChanged();
//                    }
//                }
//            }
//        });


        getQuestions();

        addQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder settingsBuilder = new AlertDialog.Builder(QuestionsActivity.this);
                View settingsView = getLayoutInflater().inflate(R.layout.add_question_popup, null);

                EditText questionText = settingsView.findViewById(R.id.addAnswer);
                Button addButtonQuestion = settingsView.findViewById(R.id.addButtonAnswer);

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
                                        getQuestions();
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

        questionList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String questionID = questionsDataList.get(position).getQuestionId();
                String question_text = questionsDataList.get(position).getQuestionText();
                Intent i = new Intent(getApplicationContext(), AnswersActivity.class);
                i.putExtra(AnswersActivity.QUESTION_ID_EXTRA, questionID);
                i.putExtra(AnswersActivity.QUESTION_TEXT_EXTRA, question_text);
                startActivity(i);
            }
        });

    }

    public void getQuestions() {
        db.collection("Questions")
                .whereEqualTo("experimentID", experimentID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            questionsDataList.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String questionId = document.getId();
                                String questionText = document.getData().get("questionText").toString();
                                questionsDataList.add(new Question(questionId,questionText));
                                //Collections.sort("questionsDataList);
                                questionAdapter.notifyDataSetChanged();

                            }
                        }
                    }

                });
    }

}
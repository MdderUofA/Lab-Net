/*
package com.example.lab_net;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class experimentEditPopUpActivity extends Activity {

    FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_experiment_edit_pop_up);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width*.8), (int)(height*.6));

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.CENTER;
        params.x = 0;
        params.y = -20;
        getWindow().setAttributes(params);

        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        String description = intent.getStringExtra("description");
        String region = intent.getStringExtra("region");
        String experimentId = intent.getStringExtra("experimentId");

        EditText editText = findViewById(R.id.editTitle);
        editText.setText(title);

        EditText editText1 = findViewById(R.id.editDescription);
        editText1.setText(description);

        EditText editText2 = findViewById(R.id.editRegion);
        editText2.setText(region);

        Button save;
        save = findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent(getApplicationContext(), ExperimentActivity.class);
                db = FirebaseFirestore.getInstance();
                DocumentReference noteRef = db.collection("Experiments")
                        .document(experimentId);
                noteRef.update(
                        "Title", editText.getText(),
                         "Description", editText1.getText(), "Region", editText2.getText()
                ).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(getApplicationContext(), "Changed", Toast.LENGTH_LONG).show();
                        }
                        else{
                            Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_LONG).show();

                        }
                    }
                });
                startActivity(sendIntent);

            }
        });

    }
}*/

package com.example.lab_net;

import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BinomialExperimentHandler extends ExperimentHandler<BinomialTrial> {
    @Override
    public BinomialTrial trialFromDatabase(QueryDocumentSnapshot doc) {
        String trialId = doc.getId();
        String trialTitle = doc.getData().get("Title").toString();
        String date = doc.getData().get("Date").toString();
        String result = doc.getData().get("Result").toString();
        return new BinomialTrial(trialId,trialTitle,date,result);
    }

    @Override
    public ArrayAdapter<Trial> getListAdapter(AppCompatActivity activity,
                                              ArrayList<Trial> trialDataList) {
        return new CustomBinomialTrialList(activity, trialDataList);
    }

    @Override
    public String getType() {
        return ExperimentTypes.BINOMIAL_STRING;
    }

    @Override
    public int getAddTrialLayout() {
        return R.layout.edit_trial_dialog;
    }

    @Override
    public BinomialTrial trialFromMap(Map<String, Object> data, String id) {
        String title = (String) data.get("Title");
        String date = (String) data.get("Date");
        String result = (String) data.get("Result");
        return new BinomialTrial(id, title, date, result);
    }

    @Override
    public List<String> commandsFromMap(Map<String, Object> data) {
        List<String> l = new ArrayList<>();
        l.add((String) data.get("Title"));
        l.add((String) data.get("Date"));
        l.add((String) data.get("Result"));
        return l;
    }

    @Override
    public Map<String, Object> mapFromCommands(List<String> commands) {
        return null;
    }

    @Override
    public boolean isResultValid(View addTrialResult) {
        String t = ((EditText)addTrialResult).getText().toString().toLowerCase();
        return t.equals("pass") || t.equals("fail");
    }
}

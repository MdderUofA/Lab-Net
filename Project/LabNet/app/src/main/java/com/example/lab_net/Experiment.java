package com.example.lab_net;

import java.util.ArrayList;

public class Experiment {
    String experimentId;
    String title;
    String description;
    String region;
    int minTrials;
    ArrayList<Trial> trials;// aggregation with Trial
   // ArrayList<Question> questions; // aggregate with Question
    //qrCode QR; // aggregate with QR

    public Experiment(String experimentId, String title, String description, String region, int minTrials) {
        this.experimentId = experimentId;
        this.title = title;
        this.description = description;
        this.region = region;
        this.minTrials = minTrials;
        trials = new ArrayList<>();
    }

    public String getExperimentId() {
        return experimentId;
    }

    public void setExperimentId(String experimentId) {
        this.experimentId = experimentId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public int getMinTrials() {
        return minTrials;
    }

    public void setMinTrials(int minTrials) {
        this.minTrials = minTrials;
    }
}

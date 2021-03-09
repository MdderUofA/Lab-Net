package com.example.lab_net;

import java.util.ArrayList;

public class Experiment {
    String description;
    String region;
    int minTrials;
    ArrayList<Trial> trials; // aggregation with Trial
    ArrayList<Question> questions; // aggregate with Question
    qrCode QR; // aggregate with QR

    public Experiment(String description, String region, int minTrials) {
        this.description = description;
        this.region = region;
        this.minTrials = minTrials;
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

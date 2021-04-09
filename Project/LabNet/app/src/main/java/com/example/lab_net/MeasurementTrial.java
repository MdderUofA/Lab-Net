package com.example.lab_net;

import java.util.Date;

public class MeasurementTrial extends Trial {
    private double measurement;


    public MeasurementTrial(String id, String title, String date, double measurement) {
        super(id, title, date);
        this.measurement = measurement;
    }

    public double getMeasurement() {
        return measurement;
    }

    public void setMeasurement(double measurement) {
        this.measurement = measurement;
    }
}

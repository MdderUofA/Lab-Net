package com.example.lab_net;

public class MeasurementTrial extends Trial{
    private double measurement;


    public MeasurementTrial(String id, String title, double measurement) {
        super(id, title);
        this.measurement = measurement;
    }

    public double getMeasurement() {
        return measurement;
    }

    public void setMeasurement(double measurement) {
        this.measurement = measurement;
    }
}

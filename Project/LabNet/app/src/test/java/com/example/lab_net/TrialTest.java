package com.example.lab_net;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Testing the different types of Trials
 */
public class TrialTest {

    @Test
    public void getCountResult() {
        CountTrial countTrial = new CountTrial("1234", "new count", Long.valueOf(5));
        assertEquals(countTrial.getCount(), Long.valueOf(5));
    }

    @Test
    public void getBinomialResult() {
        BinomialTrial binomialTrial = new BinomialTrial("4321", "new binomial", "PAss");
        assertEquals(binomialTrial.getResult().toLowerCase(), "pass");
    }

    @Test
    public void setMeasurementResult() {
        MeasurementTrial measurementTrial = new MeasurementTrial("1234", "new measurement", 2.9);
        measurementTrial.setMeasurement(2.4);
        assertEquals(measurementTrial.getMeasurement(), 2.4, 0);
    }
}

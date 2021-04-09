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
    public void getMeasurementResult() {
        MeasurementTrial measurementTrial = new MeasurementTrial("1212", "test1",5.0);
        assertEquals(measurementTrial.getMeasurement(),5.0,0 );
    }

    @Test
    public void getNonNegativeResult() {
        NonNegativeIntegerTrial trial = new NonNegativeIntegerTrial("4545", "test2",Long.valueOf(5));
        assertEquals(trial.getNonNegativeCount(), Long.valueOf(5));
    }

    @Test
    public void setCountResult() {
        CountTrial countTrial = new CountTrial("1234", "new count", Long.valueOf(5));
        countTrial.setCount(Long.valueOf(6));
        assertEquals(countTrial.getCount(), Long.valueOf(6));
    }

    @Test
    public void setBinomialResult() {
        BinomialTrial binomialTrial = new BinomialTrial("4321", "new binomial", "Pass");
        binomialTrial.setResult("fail");
        assertEquals(binomialTrial.getResult().toLowerCase(), "fail");
    }

    @Test
    public void setMeasurementResult() {
        MeasurementTrial measurementTrial = new MeasurementTrial("1212", "test1",5.0);
        measurementTrial.setMeasurement(4.6);
        assertEquals(measurementTrial.getMeasurement(),4.6,0 );
    }

    @Test
    public void setNonNegativeResult() {
        NonNegativeIntegerTrial trial = new NonNegativeIntegerTrial("4545", "test2",Long.valueOf(5));
        trial.setNonNegativeCount(Long.valueOf(6));
        assertEquals(trial.getNonNegativeCount(), Long.valueOf(6));
    }

}

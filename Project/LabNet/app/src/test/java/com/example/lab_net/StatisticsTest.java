package com.example.lab_net;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;

/**
 * Test for statistical calculations
 */
public class StatisticsTest {

    @Test
    public void testCalculateMedian() {
        ArrayList<Double> list = new ArrayList<>(Arrays.asList(5.6,4.5,6.7,7.8,3.0));
        Collections.sort(list);
        double median = 0;
        int mid = list.size() / 2;
        if(list.size() % 2 != 0){
            median = list.get(mid);
        }
        else{
            median = (list.get(mid - 1) + list.get(mid)) / 2.0;
        }

        assertEquals(median, 5.6, 0);

    }

    @Test
    public void testCalculateMean() {
        ArrayList<Double> list = new ArrayList<>(Arrays.asList(5.6,4.5,6.7,7.8,3.0));
        int i;
        double sum = 0;
        double mean = 0;
        for (i = 0; i < list.size(); i++) {
            sum = list.get(i) + sum;
        }
        if (list.size() != 0) {
            mean = (sum / (list.size()));
        } else {
            mean = 0;
        }
        assertEquals(mean, 5.5200000000000005, 0);

    }


}
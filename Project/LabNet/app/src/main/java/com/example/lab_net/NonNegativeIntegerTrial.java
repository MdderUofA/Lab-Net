package com.example.lab_net;

import java.util.Date;

public class NonNegativeIntegerTrial extends Trial{
    private long nonNegativeCount;


    public NonNegativeIntegerTrial(String id, String title, String date, long nonNegativeCount) {
        super(id, title, date);
        this.nonNegativeCount = nonNegativeCount;
    }

    public long getNonNegativeCount() {
        return nonNegativeCount;
    }

    public void setNonNegativeCount(Long nonNegativeCount) {
        this.nonNegativeCount = nonNegativeCount;
    }
}

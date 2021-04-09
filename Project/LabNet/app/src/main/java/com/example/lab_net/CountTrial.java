package com.example.lab_net;

import java.util.Date;

public class CountTrial extends Trial {
    private long count;


    public CountTrial(String id, String title, String date, Long count) {
        super(id, title, date);
        this.count = count;
    }

    public long getCount() {
        return count;
    }
}

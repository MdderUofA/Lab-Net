package com.example.lab_net;

/**
 * Defining the Count Trial
 */
public class CountTrial extends Trial{
    private Long count;


    public CountTrial(String id, String title, Long count) {
        super(id, title);
        this.count = count;
    }

    public Long getCount() {
        return count;
    }
}

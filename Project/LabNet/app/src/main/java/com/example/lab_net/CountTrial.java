package com.example.lab_net;

public class CountTrial extends Trial{
    private String count;


    public CountTrial(String id, String title, String count) {
        super(id, title);
        this.count = count;
    }

    public String getCount() {
        return count;
    }
}

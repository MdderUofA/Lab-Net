package com.example.lab_net;

public class CountTrial extends Trial{
    private long count;

    public CountTrial(String id, String title, Long count) {
        super(id, title);
        this.count = count;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }
}

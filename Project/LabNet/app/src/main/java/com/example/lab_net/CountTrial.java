package com.example.lab_net;

public class CountTrial extends Trial{
    private Long count;


    public CountTrial(String id, String title, Long count) {
        super(id, title);
        this.count = count;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
}

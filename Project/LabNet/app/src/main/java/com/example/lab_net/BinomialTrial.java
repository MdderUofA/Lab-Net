package com.example.lab_net;

import java.util.Date;

public class BinomialTrial extends Trial{
    private String result;



    public BinomialTrial(String id, String title, String date, String result) {
        super(id, title, date);
        this.result = result;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String pass) {
        this.result = result;
    }

}


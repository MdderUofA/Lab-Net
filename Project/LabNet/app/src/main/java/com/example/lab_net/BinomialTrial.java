package com.example.lab_net;

public class BinomialTrial extends Trial{
    private String result;



    public BinomialTrial(String id, String title, String result) {
        super(id, title);
        this.result = result;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String pass) {
        this.result = result;
    }

}


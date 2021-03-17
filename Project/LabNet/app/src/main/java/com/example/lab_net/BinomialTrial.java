package com.example.lab_net;

public class BinomialTrial extends Trial{
    String title;
    private Long pass;
    private Long fail;


    public BinomialTrial(String id, String title, Long result1, Long result2) {
        super(id, title);
    }

    public Long getPass() {
        return pass;
    }

    public Long getFail() {
        return fail;
    }

    public void setPass(Long pass) {
        this.pass = pass;
    }

    public void setFail(Long fail) {
        this.fail = fail;
    }
}


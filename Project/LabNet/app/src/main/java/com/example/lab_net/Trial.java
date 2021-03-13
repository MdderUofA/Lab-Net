package com.example.lab_net;

public class Trial {
    private String id;
    private String title;
    private int result;

    public Trial(String id, String title, int result) {
        this.id = id;
        this.title = title;
        this.result = result;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}

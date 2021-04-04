package com.example.lab_net;

public class SubscribedExperiment {
    private String id;
    private String title;
    private String subscriber;

    public SubscribedExperiment(String id, String title, String subscriber) {
        this.id = id;
        this.title = title;
        this.subscriber = subscriber;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSubscriber() {
        return subscriber;
    }

    public void setSubscriber(String subscriber) {
        this.subscriber = subscriber;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}

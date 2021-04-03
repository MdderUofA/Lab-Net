package com.example.lab_net;

public class SubscribedExperiment {
    private String id;
    private String subscriber;

    public SubscribedExperiment(String id, String subscriber) {
        this.id = id;
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
}

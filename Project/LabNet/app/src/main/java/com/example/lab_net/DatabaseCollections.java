package com.example.lab_net;

public enum DatabaseCollections {
    ANSWERS ("Answers"),
    BINOMIAL_TRIAL ("BinomialTrial"),
    EXPERIMENTS ("Experiments"),
    KEYWORDS ("Keywords"),
    QUESTIONS ("Questions"),
    TRIALS ("Trials"),
    USER_PROFILE ("UserProfile"),
    SUBSCRIBED_EXPERIMENTS ("SubscribedExperiments");

    private final String mappedValue;

    DatabaseCollections(String value) {
        this.mappedValue=value;
    }

    public final String value() {
        return this.mappedValue;
    }
}
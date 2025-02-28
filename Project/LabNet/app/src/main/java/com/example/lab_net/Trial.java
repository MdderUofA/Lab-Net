package com.example.lab_net;

import java.io.Serializable;

/**
 * This class defines the Trial and its attributes.
 */
public class Trial implements Serializable {
    private String id;
    private String title;

    public Trial(String id, String title) {
        this.id = id;
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}

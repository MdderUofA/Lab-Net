package com.example.lab_net;

import com.google.firebase.firestore.CollectionReference;

import java.util.Date;

public class SearchableUser implements Searchable {
    private String name = "NOT_YET_DEFINED";
    private String description = "NOT_YET_DEFINED";
    private Date date = null;
    private SearchableDocumentReference ref;

    public SearchableUser(String experimentName, String experimentDescription,
                                SearchableDocumentReference ref) {
        this.name = experimentName;
        this.description = experimentDescription;
        this.ref = ref;
        this.date = null;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public Date getDate() {
        return date;
    }

    @Override
    public SearchableDocumentReference getDocumentReference() {
        return ref;
    }
}

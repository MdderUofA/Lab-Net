package com.example.lab_net;

import com.google.firebase.firestore.CollectionReference;

import java.util.Date;

/**
 * An interface that represents an object that can be searched for.
 * @see SearchableList
 */
public interface Searchable {

    public String getName();
    public String getDescription();
    public Date getDate();
    public SearchableDocumentReference getDocumentReference();

}

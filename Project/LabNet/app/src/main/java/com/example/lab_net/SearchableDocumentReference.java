package com.example.lab_net;

/**
 * A container class to hold information about a search. This information is used when
 * clicking on a result to help the program navigate to the correct activity.
 */
public class SearchableDocumentReference {

    private final String documentCollection;
    private final String documentId;

    public SearchableDocumentReference(String documentCollection, String documentId) {
        this.documentCollection = documentCollection;
        this.documentId = documentId;
    }

    public String getCollection() {
        return this.documentCollection;
    }

    public String getDocumentId() {
        return this.documentId;
    }
}

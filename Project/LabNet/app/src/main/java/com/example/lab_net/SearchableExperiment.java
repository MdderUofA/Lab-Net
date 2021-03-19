package com.example.lab_net;

import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Date;

public class SearchableExperiment extends Searchable {

    @Override
    public SearchableExperiment applyFromDatabase(QueryDocumentSnapshot snapshot) {
        this.name =(String)snapshot.get("Title");
        this.description = (String)snapshot.get("Description");
        this.date = null;
        this.reference = new SearchableDocumentReference(DatabaseCollections.EXPERIMENTS.value(),
                snapshot.getId());
        return this;
    }
}

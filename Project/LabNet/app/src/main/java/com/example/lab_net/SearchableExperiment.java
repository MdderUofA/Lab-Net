package com.example.lab_net;

import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Date;

public class SearchableExperiment extends Searchable {

    private boolean open = true;
    private String ownerId = "NOT_YET_DEFINED";

    @Override
    public SearchableExperiment applyFromDatabase(QueryDocumentSnapshot snapshot) {
        this.name =(String)snapshot.get("Title");
        this.description = (String)snapshot.get("Description");
        this.date = null;
        this.reference = new SearchableDocumentReference("Experiments",
                snapshot.getId());
        this.ownerId = (String)snapshot.get("Owner");
        this.open=true;
        return this;
    }

    /**
     * Getter for whether or not the experiment is currently open.
     * @return Whether or not the experiment is currently open.
     */
    public boolean getStatus() {
        return this.open;
    }

    /**
     * Getter for the experiment owner's ID
     * @return The experiment owner's ID
     */
    public String getOwnerId() {
        return this.ownerId;
    }
}

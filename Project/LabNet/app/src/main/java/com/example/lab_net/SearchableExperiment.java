package com.example.lab_net;

import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Date;

/**
 * A Searchable which represents an experiment.
 * @author Marcus
 */
public class SearchableExperiment extends Searchable {

    private boolean open = true;
    private String type = "NOT_YET_DEFINED";
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
        this.type = (String)snapshot.get("TrialType");
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

    /**
     * Getter for the String type of the Experiment. One of {}
     * @return
     */
    public String getType() {
        return this.type;
    }
}

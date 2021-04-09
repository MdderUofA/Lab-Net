package com.example.lab_net;

import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

/**
 * An abstract class that represents the backend of an Experiment.
 * @author Marcus
 */
public abstract class ExperimentHandler<T extends Trial> {

    private String experimentTitle;
    private String experimentDescription;
    private String experimentRegion;
    private String experimentId;
    private boolean isEnded;
    private boolean isLocationEnabled;

    /**
     * Creates a new Trial from a QueryDocumentSnapshot.
     * @param doc The snapshot.
     * @return The new trial.
     */
    public abstract T trialFromDatabase(QueryDocumentSnapshot doc);

    /**
     * Creates a new ArrayAdapter for a list that can display our experiment types.
     * @param activity The activity the list is bound to.
     * @param trialDataList The data list that holds the Trials.
     * @return A new ArrayAdaper that can display the specified trials.
     */
    public abstract ArrayAdapter<T> getListAdaper(AppCompatActivity activity,
                                                  ArrayList<T> trialDataList);

    /**
     * Returns the string type of this Handler, which is one of the String
     * constants in ExperimentTypes.
     * @see ExperimentTypes
     * @return The type of this handler.
     */
    public abstract String getType();

    public void applyFromSnapshot(DocumentSnapshot snap) {
        experimentTitle = snap.getData().get("Title").toString();
        experimentDescription = snap.getData().get("Description").toString();
        experimentRegion = snap.getData().get("Region").toString();
        experimentId = snap.getId();
        isEnded = snap.getData().get("Status").toString().equals("closed");
        isLocationEnabled = !snap.getData().get("EnableLocation").toString().equals("no"); // w h y
    }

    /**
     * Returns whether or not the QueryDocumentSnapshot of a trial should be shown.
     * @param doc The document to check.
     * @return True if it should be shown, false otherwise.
     */
    public boolean shouldShow(QueryDocumentSnapshot doc) {
        return !(boolean)doc.get("isUnlisted"); // thanks autoboxing
    };

    public String getTitle() {
        return experimentTitle;
    }

    public String getDescription() {
        return experimentDescription;
    }

    public String getRegion() {
        return experimentRegion;
    }

    public String getId() {
        return experimentId;
    }

    public boolean isEnded() {
        return isEnded;
    }

    public boolean isLocationEnabled() {
        return isLocationEnabled;
    }
}

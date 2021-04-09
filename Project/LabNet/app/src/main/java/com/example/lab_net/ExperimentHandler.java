package com.example.lab_net;

import android.view.View;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
     * @return A new ArrayAdapter that can display the specified trials.
     */
    public abstract ArrayAdapter<Trial> getListAdapter(AppCompatActivity activity,
                                                   ArrayList<Trial> trialDataList);

    /**
     * Returns the string type of this Handler, which is one of the String
     * constants in ExperimentTypes.
     * @see ExperimentTypes
     * @return The type of this handler.
     */
    public abstract String getType();

    /**
     * Returns the R.layout that represents adding a trial to this experiment.
     * @return the layout.
     */
    public abstract int getAddTrialLayout(); // R.layout.edit_trial_dialog

    /**
     * Sets up this ExperimentHandler using the database info for the Experiment.
     * @param snap The snapshot of the database.
     */
    public void applyFromSnapshot(DocumentSnapshot snap) {
        experimentTitle = snap.getData().get("Title").toString();
        experimentDescription = snap.getData().get("Description").toString();
        experimentRegion = snap.getData().get("Region").toString();
        experimentId = snap.getId();
        isEnded = snap.getData().get("Status").toString().equals("closed");
        isLocationEnabled = !snap.getData().get("EnableLocation").toString().equals("no"); // w h y
    }

    /**
     * Creates a new Trial from the specified Map data.
     * @param data The data to create the trial from.
     * @param id The unique ID of the trial.
     * @return A new Trial.
     */
    public abstract T trialFromMap(Map<String, Object> data, String id);

    /**
     * Generates a list of strings that represents a Trial from the supplied HashMap.
     * This list must later be able to be parsed by mapFromCommands. back into the a Map
     * capable of creating a trial.
     * @param data The Map that represents the Trial
     * @return A List of strings representing commands to make the trial.
     */
    public abstract List<String> commandsFromMap(Map<String, Object> data);

    // this must return a map solely because the data stored in the database is not the same as
    // the data stored in Trial
    /**
     * Creates a new Trial from a list of commands. Usually these commands come from a QR code
     * @param commands The commands that make up the trial
     * @return A new Trial
     */
    public abstract Map<String, Object> mapFromCommands(List<String> commands);

    /**
     * Returns whether the entered result is valid. The Object passed in will be a UI element
     * such as an EditText.
     *
     * @param addTrialResult The UI element that handles entering in text
     * @return True if the result is valid, false otherwise.
     */
    public abstract boolean isResultValid(View addTrialResult);

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

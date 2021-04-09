package com.example.lab_net;

import android.content.Context;
import android.provider.Settings;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

/**
 * A singleton class meant to hold the current user information.
 */
public class ApplicationUserContext {
    private static ApplicationUserContext instance = null;

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final String deviceId;
    private final Context context;

    private boolean loaded = false;

    private ArrayList<String> subscribedExperiments = new ArrayList<String>();

    public static ApplicationUserContext getInstance(Context context) {
        if(instance==null)
            instance = new ApplicationUserContext(context);
        return ApplicationUserContext.instance;
    }

    private ApplicationUserContext(Context context) {
        this.context = context;
        this.deviceId = Settings.Secure.getString(context.getContentResolver(),
                                    Settings.Secure.ANDROID_ID);
        this.load();
    };

    public void isSubscribedTo(String experimentId) {

    }

    public void subscribeTo(String experimentId){
        checkSubscribed(experimentId);
    }

    private void load() {

    }

    private boolean isLoaded() {
        return this.loaded;
    }

    private void checkSubscribed(String experimentId) {
        throw new IllegalArgumentException("User is already subscribed to experiment " + experimentId);
    }

}

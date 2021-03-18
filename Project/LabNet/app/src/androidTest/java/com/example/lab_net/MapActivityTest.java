package com.example.lab_net;

import android.content.pm.PackageManager;

import androidx.core.content.ContextCompat;
import androidx.test.platform.app.InstrumentationRegistry;

import com.robotium.solo.Solo;

import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Objects;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class MapActivityTest {

    private Solo solo;

    @Rule
    public ActivityTestRule<MapActivity> rule =
            new ActivityTestRule<>(MapActivity.class, true, true);

    @Before
    public void setUp() {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
    }

    @Test
    public void checkPermissionConsistency(){
        MapActivity mapActivity = (MapActivity) solo.getCurrentActivity();
        boolean permission = mapActivity.isLocationPermissionGranted();
        if (permission){
            assertTrue(ContextCompat.checkSelfPermission(Objects.requireNonNull(solo.getCurrentActivity()),
                    android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED);
        } else {
            assertFalse(ContextCompat.checkSelfPermission(Objects.requireNonNull(solo.getCurrentActivity()),
                    android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED);
        }

    }

    @Test
    public void checkIfLocationIsUpdated() {
        //Checking to see if location coordinates are updated from MapFragment.
        assertTrue("Permission to get location not given. Please give application location permission" +
                "for test to work" +
                "", ContextCompat.checkSelfPermission(Objects.requireNonNull(solo.getCurrentActivity()),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED);
        MapActivity mapActivity = (MapActivity) solo.getCurrentActivity();
        assertNotEquals(mapActivity.getTrialLatitude(), 0.0);
        assertNotEquals(mapActivity.getTrialLatitude(), 0.0);
    }

    @After
    public void tearDown() {
        solo.finishOpenedActivities();
    }

}

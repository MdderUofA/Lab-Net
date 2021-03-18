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
import static org.junit.Assert.assertTrue;

public class MapActivityTest {

    private Solo solo;

    @Rule
    public ActivityTestRule<ExperimentActivity> rule =
            new ActivityTestRule<>(ExperimentActivity.class, true, true);

    @Before
    public void setUp() {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
        solo.waitForText(solo.getView(R.id.experimentTitle).toString());
        solo.clickOnView(solo.getView(R.id.addRemoveTrialsButton));
        solo.waitForDialogToOpen();
    }

    @Test
    public void checkPermission(){
        solo.clickOnView(solo.getView(R.id.getLocationButton));
        solo.assertCurrentActivity("Wrong Activity", MapActivity.class);

        /*boolean permission = solo.getCurrentActivity().isLocationPermissionGranted();
        if (permission){
            assertTrue(ContextCompat.checkSelfPermission(Objects.requireNonNull(solo.getCurrentActivity()),
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED);
        } else {
            assertFalse(ContextCompat.checkSelfPermission(Objects.requireNonNull(solo.getCurrentActivity()),
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED);
        }
        solo.assertCurrentActivity("Wrong Activity", ExperimentActivity.class);*/


    }


    @After
    public void tearDown(){
        solo.finishOpenedActivities();
    }





}

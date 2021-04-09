package com.example.lab_net;
import android.content.pm.PackageManager;

import androidx.core.content.ContextCompat;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.robotium.solo.Condition;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

/**
 * Testing Map Fragment
 */
public class MapFragmentTest {
    private Solo solo;

    @Rule
    public ActivityTestRule<MapActivity> rule =
            new ActivityTestRule<>(MapActivity.class, true, true);

    @Before
    public void setUp() {

        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
        solo.assertCurrentActivity("Not MapActivity", MapActivity.class);
        assertTrue("Permission to get location not given. Please give application location permission" +
                "for test to work" +
                "", ContextCompat.checkSelfPermission(Objects.requireNonNull(solo.getCurrentActivity()),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED);
    }

    @Test
    public void checkIfDeviceLocationIsCorrect() {
        MapActivity mapActivity = (MapActivity) solo.getCurrentActivity();
        MapFragment mapFragment = (MapFragment) mapActivity.getMapFragment();

        //my current location coordinates from google.com
        //change to your current location to test
        double myCurrentLocationLatitude = -4.0585018676036295;
        double myCurrentLocationLongitude = 39.66499157809425;

        solo.waitForCondition(new Condition(){

            @Override
            public boolean isSatisfied() {
                return mapFragment.getMainLatitude()>0 && mapFragment.getMainLongitude() > 0;
            }
        },5000);

        assertEquals(myCurrentLocationLatitude, mapFragment.getMainLatitude(), 0.0001);
        assertEquals(myCurrentLocationLongitude, mapFragment.getMainLongitude(), 0.0001);

    }
    @Test
    public void checkIfLocationIsUpdatedWhenClicked(){
        MapActivity mapActivity = (MapActivity) solo.getCurrentActivity();
        MapFragment mapFragment = (MapFragment) mapActivity.getMapFragment();

        //my current location coordinates from google.com
        //change to your current location to test
        double myCurrentLocationLatitude = -4.0585018676036295;
        double myCurrentLocationLongitude = 39.66499157809425;

        solo.waitForCondition(new Condition(){

            @Override
            public boolean isSatisfied() {
                return mapFragment.getMainLatitude()>0 && mapFragment.getMainLongitude() > 0;
            }
        },5000);

        assertEquals(myCurrentLocationLatitude, mapFragment.getMainLatitude(), 0.0001);
        assertEquals(myCurrentLocationLongitude, mapFragment.getMainLongitude(), 0.0001);

        solo.clickOnScreen(810,690);//Random locationOne on map

        double locationOneLatitude = mapFragment.getMainLatitude();
        double locationOneLongitude = mapFragment.getMainLongitude();

        assertNotEquals(myCurrentLocationLatitude, locationOneLatitude);
        assertNotEquals(myCurrentLocationLongitude, locationOneLongitude);

        solo.clickOnScreen(510,390);//Random locationTwo on map

        double locationTwoLatitude = mapFragment.getMainLatitude();
        double locationTwoLongitude = mapFragment.getMainLongitude();

        assertNotEquals(myCurrentLocationLatitude, locationTwoLatitude);
        assertNotEquals(myCurrentLocationLongitude, locationTwoLongitude);

        assertNotEquals(locationOneLatitude, locationTwoLatitude);
        assertNotEquals(locationOneLongitude, locationTwoLongitude);

    }

    @After
    public void tearDown(){
        solo.finishOpenedActivities();
    }

}

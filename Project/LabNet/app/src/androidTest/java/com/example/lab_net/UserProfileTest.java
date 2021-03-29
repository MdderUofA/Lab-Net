package com.example.lab_net;

import android.app.Activity;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.*;

public class UserProfileTest {

    private Solo solo;
    @Rule
    //public ActivityScenarioRule<Signup> rule = new ActivityScenarioRule<>(Signup.class);
    public ActivityTestRule<UserProfile> rule = new ActivityTestRule<>(UserProfile.class, true, true);

    @Before
    public void setUp() throws Exception {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
    }

    /**
     * Test to see if activity is launched
     */
    @Test
    public void testLaunch() {
        Activity activity = rule.getActivity();
    }


    @Test
    public void testGetExperiments() {
    }

    @Test
    public void testGetUserInfo() {
    }

    @Test
    public void testEditUserDialog () {

    }

    @Test
    public void testAddExpDialog () {

    }

    @Test
    public void testSubExpView () {

    }

    @Test void testMyExpView () {

    }
    @After
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }
}
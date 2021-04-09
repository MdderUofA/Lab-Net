package com.example.lab_net;

import androidx.test.rule.ActivityTestRule;

import android.app.Activity;

import androidx.test.platform.app.InstrumentationRegistry;

import com.robotium.solo.Solo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class CountExperimentActivityTest {

    private Solo solo;

    @Rule
    public ActivityTestRule<CountExperimentActivity> countExperimentActivityActivityTestRule = new ActivityTestRule<>(CountExperimentActivity.class, true, true);

    @Before
    public void setUp() {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), countExperimentActivityActivityTestRule.getActivity());
    }

    @Test
    public void start() throws Exception{
        solo.assertCurrentActivity("Experiment owner activity", UserProfile.class);
        solo.clickInList(1);
        solo.waitForActivity("showActivity");
        solo.assertCurrentActivity("Go to count experiment activity", CountExperimentActivity.class);
    }
}

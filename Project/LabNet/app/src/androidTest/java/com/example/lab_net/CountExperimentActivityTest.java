package com.example.lab_net;

import android.widget.EditText;
import android.widget.ListView;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.robotium.solo.Solo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Testing the Count Experiment Activity,
 * which is the same process for other trial type Experiment Activites.
 */
public class CountExperimentActivityTest {

    private Solo solo;

    @Rule
    public ActivityTestRule<CountExperimentActivity> countExperimentActivityActivityTestRule = new ActivityTestRule<>(CountExperimentActivity.class, true, true);

    @Before
    public void setUp() {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), countExperimentActivityActivityTestRule.getActivity());
    }

    @Test
    public void checkListItem() {
        solo.assertCurrentActivity("Wrong activity", CountExperimentActivity.class);
        solo.clickOnButton("ADD TRIAL");
        solo.enterText((EditText) solo.getView(R.id.addTrialTitle), "New Trial");
        solo.enterText((EditText) solo.getView(R.id.addTrialResult),"2");
        solo.clickOnView(solo.getView(R.id.addTrial));
        solo.waitForText("New Trial", 1, 2000);
        solo.clickInList(0);
        ListView listView = (ListView) solo.getView(R.id.trial_list);

        assertEquals("New Trial, 2", listView);

    }
}

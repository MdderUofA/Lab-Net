package com.example.lab_net;

import android.content.Context;
import android.content.Intent;
import android.widget.EditText;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * Testing UserProfile Activity.
 */
public class UserProfileTest {

    private Solo solo;
    private String deviceId = "303a758afa615c9b" ;

    @Rule
    public ActivityTestRule<UserProfile> rule = new ActivityTestRule<UserProfile>(UserProfile.class, true, true) {
        @Override
        //code adapted from Stack Overflow
        //Max (2017, Feb. 13) Mocking Intent Extras in Espresso Tests.
        //https://stackoverflow.com/questions/34628700/mocking-intent-extras-in-espresso-tests
        protected Intent getActivityIntent() {
            Context targetContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
            Intent intentTest = new Intent(targetContext, UserProfile.class);
            intentTest.putExtra(UserProfile.USER_ID_EXTRA, deviceId);
            return intentTest;

        }
    };

    @Before
    public void setUp() throws Exception {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
    }

    @Test
    public void testEditUserDialog () {
        solo.assertCurrentActivity("Start on User Profile Activity", UserProfile.class);
        solo.clickOnImageButton(0);
        solo.waitForDialogToOpen(2);
        solo.clearEditText((EditText) solo.getView(R.id.editTextFirstName));
        solo.clearEditText((EditText) solo.getView(R.id.editTextLastName));
        solo.clearEditText((EditText) solo.getView(R.id.editTextEmail));
        solo.clearEditText((EditText) solo.getView(R.id.editTextSettingsPhone));
        solo.enterText((EditText) solo.getView(R.id.editTextFirstName),"tom");
        solo.enterText((EditText) solo.getView(R.id.editTextLastName),"jerry");
        solo.enterText((EditText) solo.getView(R.id.editTextEmail),"tom@gmail.com");
        solo.enterText((EditText) solo.getView(R.id.editTextSettingsPhone),"1212121212");
        solo.clickOnButton("Update");
        solo.waitForDialogToClose(2);
        solo.assertCurrentActivity("Stay on User Profile Activity", UserProfile.class);
    }

    @Test
    public void testBrowse () {
        solo.assertCurrentActivity("Start on User Profile Activity", UserProfile.class);
        solo.clickOnButton("Browse");
        solo.assertCurrentActivity("Move to SearchableListActivity", SearchableListActivity.class);

    }
    @Test
    public void testAddExpDialog () {
        solo.assertCurrentActivity("Start on User Profile Activity", UserProfile.class);
        solo.clickOnButton("Add");
        solo.waitForDialogToOpen(2);
        solo.enterText((EditText) solo.getView(R.id.addExpTitle),"testing title");
        solo.enterText((EditText) solo.getView(R.id.addExpDescription),"testing Description");
        solo.enterText((EditText) solo.getView(R.id.addExpRegion),"testing Region");
        solo.enterText((EditText) solo.getView(R.id.addExpMinTrials),"10");
        solo.clickOnButton("Create");
        solo.waitForDialogToClose(2);
        solo.assertCurrentActivity("Stay on UserProfile Activity", UserProfile.class);
    }


    @After
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }
}
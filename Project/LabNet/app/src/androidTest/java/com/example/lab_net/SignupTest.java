package com.example.lab_net;

import android.app.Activity;
import android.widget.EditText;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * Test class for Signup activity.
 *
 * @author Vidhi Patel
 */
public class SignupTest {

    private Solo solo;

    @Rule
    //public ActivityScenarioRule<Signup> rule = new ActivityScenarioRule<>(Signup.class);
    public ActivityTestRule<Signup> rule = new ActivityTestRule<>(Signup.class, true, true);

    @Before
    public void setUp() throws Exception {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
    }

    /**
     * Test to see if activity is launched
     */
    @Test
    public void testLaunch()  {
        Activity activity = rule.getActivity();
    }


    /**
     * Test to see when user Signed up, activity moves to user profile
     */
    @Test
    public void testActivityChange() {
        solo.assertCurrentActivity("Start on Signup activity", Signup.class);
        solo.clearEditText((EditText) solo.getView(R.id.FirstName));
        solo.clearEditText((EditText) solo.getView(R.id.LastName));
        solo.clearEditText((EditText) solo.getView(R.id.EmailAddress));
        solo.clearEditText((EditText) solo.getView(R.id.PhoneNumber));
        solo.enterText((EditText) solo.getView(R.id.FirstName),"tom");
        solo.enterText((EditText) solo.getView(R.id.LastName),"jerry");
        solo.enterText((EditText) solo.getView(R.id.EmailAddress),"tom@gmail.com");
        solo.enterText((EditText) solo.getView(R.id.PhoneNumber),"1234567890");
        solo.clickOnButton("Sign-Up");
        solo.assertCurrentActivity("Change to User Profile", UserProfile.class);
    }

    /**
     * Test to see activity doesn't change upon empty fields
     */
    @Test
    public void testEmptyFields() {
        solo.assertCurrentActivity("Start on Signup Acvity", Signup.class);
        solo.clearEditText((EditText) solo.getView(R.id.FirstName));
        solo.clearEditText((EditText) solo.getView(R.id.LastName));
        solo.clearEditText((EditText) solo.getView(R.id.EmailAddress));
        solo.clearEditText((EditText) solo.getView(R.id.PhoneNumber));
        solo.enterText((EditText) solo.getView(R.id.FirstName),"tom");
        solo.enterText((EditText) solo.getView(R.id.LastName),"jerry");
        solo.clickOnButton("Sign-Up");
        solo.assertCurrentActivity("Remain on Signup Activity", Signup.class);

    }



    @After
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }
}
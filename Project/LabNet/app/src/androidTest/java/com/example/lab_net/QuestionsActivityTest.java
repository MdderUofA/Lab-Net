package com.example.lab_net;

import android.app.Activity;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.google.android.gms.location.ActivityTransition;
import com.robotium.solo.Solo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests the questions activity
 * @author Qasim
 */
public class QuestionsActivityTest {
    private Solo solo;
    private String experimentID = "5FJzGw2hDCb2jGZWqkAz";

    @Rule
    public ActivityTestRule<QuestionsActivity> rule =
            new ActivityTestRule<>(QuestionsActivity.class,true,true);

    @Before
    public void setUp() {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(),rule.getActivity());
    }

    @Test
    public void start() {
        Activity activity = rule.getActivity();
    }

    @Test
    public void checkList() {
        solo.assertCurrentActivity("Wrong Activity", QuestionsActivity.class);
        solo.clickOnView(solo.getView(R.id.addQuestionButton));
        solo.enterText((EditText) solo.getView(R.id.addAnswer), "This is a question");
        solo.clickOnView(solo.getView(R.id.addButtonAnswer));
        solo.clearEditText((EditText) solo.getView(R.id.addAnswer));
        assertTrue(solo.waitForText("This is a question", 1, 2000));
    }

    @Test
    public void activitySwitch() {
        solo.assertCurrentActivity("Wrong Activity", QuestionsActivity.class);
        solo.clickOnView(solo.getView(R.id.addQuestionButton));
        solo.enterText((EditText) solo.getView(R.id.addAnswer), "This is a question");
        solo.clickOnView(solo.getView(R.id.addButtonAnswer));
        solo.waitForText("This is a question", 1, 2000);
        solo.clickInList(0);
        solo.waitForActivity("AnswersActivity");
        solo.assertCurrentActivity("Error has occurred", AnswersActivity.class);
    }

    @Test
    public void questionConsistent() {
        solo.assertCurrentActivity("Wrong Activity", QuestionsActivity.class);
        solo.clickOnView(solo.getView(R.id.addQuestionButton));
        solo.enterText((EditText) solo.getView(R.id.addAnswer), "This is a question");
        solo.clickOnView(solo.getView(R.id.addButtonAnswer));
        solo.waitForText("This is a question", 1, 2000);
        solo.clickInList(0);
        solo.waitForActivity("AnswersActivity");
        solo.assertCurrentActivity("Error has occurred", AnswersActivity.class);
        solo.waitForText("This is a question", 1, 2000);
    }

}
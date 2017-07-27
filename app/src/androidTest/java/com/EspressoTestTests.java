package com;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.wolkabout.hexiwear.R;
import com.wolkabout.hexiwear.activity.StepCountActivity;
import com.wolkabout.hexiwear.activity.NavActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class EspressoTestTests {

    @Rule
    public ActivityTestRule<NavActivity> mActivityRule = new IntentsTestRule<NavActivity>(
            NavActivity.class);

    @Test
    public void opensActivity() { //checks if step counter activity is opened when corresponding button clicked
        onView(withId(R.id.stepCountBut)).perform(click());
        intended(hasComponent(StepCountActivity.class.getName()));
        //intended(hasComponent(MainActivity.class.getName()));
    }

    /**
     * Created by Nathan on 2017-07-20.
     */

    public static class CoachAthleteTests {
    }
}

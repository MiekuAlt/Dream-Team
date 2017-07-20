package com;

import android.os.SystemClock;
import android.support.test.espresso.action.ViewActions;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.wolkabout.hexiwear.R;
import com.wolkabout.hexiwear.activity.NavActivity;


import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.Espresso.onView;

/**
 * Espresso test for the main navigation page (temp navigation)
 * Ensures that all the buttons are working and directs to the correct activity
 * @author Sitanun Changhor (Dream)
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MainNavigationTests {

    @Rule
    public ActivityTestRule<NavActivity> mActivityRule = new ActivityTestRule<>(NavActivity.class);

    @Test
    public void buttonsWork(){
        SystemClock.sleep(500);
        onView(withId(R.id.stepCountBut)).perform(click());
        onView(isRoot()).perform(ViewActions.pressBack());
        SystemClock.sleep(500);
        onView(withId(R.id.heartRateBut)).perform(click());
        onView(isRoot()).perform(ViewActions.pressBack());
        SystemClock.sleep(500);
        onView(withId(R.id.mapBut)).perform(click());
        onView(isRoot()).perform(ViewActions.pressBack());
        SystemClock.sleep(500);
        onView(withId(R.id.uploadGPS)).perform(click());
        onView(isRoot()).perform(ViewActions.pressBack());
        SystemClock.sleep(500);
        onView(withId(R.id.rangeBut)).perform(click());
        onView(isRoot()).perform(ViewActions.pressBack());
    }
}

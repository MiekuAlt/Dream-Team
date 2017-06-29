package com;

import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.wolkabout.hexiwear.R;
import com.wolkabout.hexiwear.StepCountActivity;
import com.wolkabout.hexiwear.TempNav;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by Nathan on 2017-06-29.
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ChatTests {

    private String testText;

    @Rule
    public ActivityTestRule<TempNav> mActivityRule = new IntentsTestRule<TempNav>(
            TempNav.class);

    @Before
    public void init(){
        testText = "test";
        mActivityRule.getActivity().getSupportFragmentManager().beginTransaction();
    }

    @Test
    public void beginChat() {
        onView(withId(R.id.chatButton)).perform(click());
    }

    @Test
    public void composeMessage() {
        onView(withId(R.id.chatButton)).perform(click());
        onView(withId(R.id.message_text)).perform(typeText(testText));
    }

    @Test
    public void sendEmptyMessage() {
        onView(withId(R.id.chatButton)).perform(click());
        onView(withId(R.id.send_button)).perform(click());
    }

    /*@Test
    public void checkSuccessfullySent() {
        onView(withId(R.id.chatButton)).perform(click());
        onView(withId(R.id.message_text)).perform(typeText(testText));
        onView(withId(R.id.send_button)).perform(click());
        onView(withText(testText)).check(matches(withText(testText)));
    }*/

}

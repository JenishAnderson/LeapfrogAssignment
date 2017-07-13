package com.example.leapfrogassignment;

import android.content.ComponentName;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.example.leapfrogassignment.activities.FirebasefeedActivity;
import com.example.leapfrogassignment.activities.LoginActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class LoginActivityTest {

    @Rule
    public IntentsTestRule<LoginActivity> mActivityRule =
            new IntentsTestRule(LoginActivity.class);

    @Test
    public void listGoesOverTheFold() {
        onView(withText("LOGIN")).check(matches(isDisplayed()));
    }


    @Test
    public void checkLoginCredentials() {
        onView(withId(R.id.etEmailLogin)).perform(typeText("alia@gmail.com"), closeSoftKeyboard());
        onView(withId(R.id.etPasswordLogin)).perform(typeText("alia123"), closeSoftKeyboard());
        onView(withId(R.id.btn_login)).perform(click());
        intended(hasComponent(new ComponentName(getTargetContext(), FirebasefeedActivity.class)));
    }
}

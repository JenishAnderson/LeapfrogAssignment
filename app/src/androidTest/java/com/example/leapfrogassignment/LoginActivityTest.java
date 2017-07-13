package com.example.leapfrogassignment;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.ComponentName;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.example.leapfrogassignment.activities.FirebasefeedActivity;
import com.example.leapfrogassignment.activities.LoginActivity;
import com.example.leapfrogassignment.activities.RegisterActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class LoginActivityTest {

    @Rule
    public IntentsTestRule<LoginActivity> mActivityRule =
            new IntentsTestRule(LoginActivity.class);

    private ServiceIdlingResource mServiceIdlingResource;


    @Test
    public void checkLoginCredentials() {
        onView(withId(R.id.etEmailLogin)).perform(typeText("kamal@gmail.com"));
        onView(withId(R.id.etPasswordLogin)).perform(typeText("kamal123"), closeSoftKeyboard());
        onView(withId(R.id.btn_login)).perform(click());
        intended(hasComponent(new ComponentName(getTargetContext(), FirebasefeedActivity.class)));
    }

    
    @Before
    public void setUp() {
        Instrumentation instrumentation
                = InstrumentationRegistry.getInstrumentation();
        mServiceIdlingResource = new ServiceIdlingResource(
                instrumentation.getTargetContext());
        Espresso.registerIdlingResources(mServiceIdlingResource);
    }


    @After
    public void unregisterIdlingResource() {
        if (mServiceIdlingResource != null) {
            Espresso.unregisterIdlingResources(mServiceIdlingResource);
        }
    }


}

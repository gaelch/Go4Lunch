package com.cheyrouse.gael.go4lunch.controller.activity;


import android.support.test.espresso.ViewInteraction;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.rule.GrantPermissionRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.cheyrouse.gael.go4lunch.R;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class HomeActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Rule
    public GrantPermissionRule mGrantPermissionRule =
            GrantPermissionRule.grant(
                    "android.permission.ACCESS_FINE_LOCATION",
                    "android.permission.CALL_PHONE",
                    "android.permission.ACCESS_COARSE_LOCATION",
                    "android.permission.VIBRATE",
                    "android.permission.RECORD_AUDIO",
                    "android.permission.WRITE_EXTERNAL_STORAGE",
                    "android.permission.READ_PHONE_STATE",
                    "android.permission.SET_TIME");

    @Test
    public void homeActivityTest() {
        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction bottomNavigationItemView = onView(
                allOf(withId(R.id.action_list_view),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.activity_home_bottom_navigation),
                                        0),
                                1),
                        isDisplayed()));
        bottomNavigationItemView.perform(click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction bottomNavigationItemView2 = onView(
                allOf(withId(R.id.action_workmates),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.activity_home_bottom_navigation),
                                        0),
                                2),
                        isDisplayed()));
        bottomNavigationItemView2.perform(click());

        ViewInteraction bottomNavigationItemView3 = onView(
                allOf(withId(R.id.action_maps_view),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.activity_home_bottom_navigation),
                                        0),
                                0),
                        isDisplayed()));
        bottomNavigationItemView3.perform(click());

        ViewInteraction frameLayout = onView(
                allOf(withId(R.id.action_list_view),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.activity_home_bottom_navigation),
                                        0),
                                1),
                        isDisplayed()));
        frameLayout.check(matches(isDisplayed()));

        ViewInteraction frameLayout2 = onView(
                allOf(withId(R.id.action_workmates),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.activity_home_bottom_navigation),
                                        0),
                                2),
                        isDisplayed()));
        frameLayout2.check(matches(isDisplayed()));

        ViewInteraction frameLayout3 = onView(
                allOf(withId(R.id.action_maps_view),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.activity_home_bottom_navigation),
                                        0),
                                0),
                        isDisplayed()));
        frameLayout3.check(matches(isDisplayed()));

        ViewInteraction imageButton = onView(
                allOf(withContentDescription("Open navigation drawer"),
                        childAtPosition(
                                allOf(withId(R.id.toolbar),
                                        childAtPosition(
                                                IsInstanceOf.<View>instanceOf(android.view.ViewGroup.class),
                                                0)),
                                0),
                        isDisplayed()));
        imageButton.check(matches(isDisplayed()));
    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}

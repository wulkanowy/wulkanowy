package io.github.wulkanowy.activity.main;


import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.action.ViewActions;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.github.wulkanowy.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    private MainActivity mainActivity = null;

    @Before
    public void setUp() {
        mainActivity = mActivityTestRule.getActivity();
    }

    @Test
    public void existsWidgetTest() {
        okClick();

        ViewInteraction textView = onView(
                allOf(withText("Login"),
                        childAtPosition(
                                allOf(withId(R.id.action_bar),
                                        childAtPosition(
                                                withId(R.id.action_bar_container),
                                                0)),
                                0),
                        isDisplayed()));
        textView.check(matches(withText("Login")));

        ViewInteraction textView2 = onView(
                allOf(withId(R.id.appName), withText("Wulkanowy"),
                        childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.widget.ScrollView.class),
                                        0),
                                0),
                        isDisplayed()));
        textView2.check(matches(withText("Wulkanowy")));

        ViewInteraction editText = onView(
                allOf(withId(R.id.emailText),
                        childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.widget.ScrollView.class),
                                        0),
                                1),
                        isDisplayed()));
        editText.check(matches(isDisplayed()));

        ViewInteraction editText2 = onView(
                allOf(withId(R.id.passwordText),
                        childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.widget.ScrollView.class),
                                        0),
                                2),
                        isDisplayed()));
        editText2.check(matches(isDisplayed()));

        ViewInteraction editText3 = onView(
                allOf(withId(R.id.symbolText),
                        childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.widget.ScrollView.class),
                                        0),
                                3),
                        isDisplayed()));
        editText3.check(matches(isDisplayed()));

        ViewInteraction button = onView(
                allOf(withId(R.id.agreeButton),
                        childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.widget.ScrollView.class),
                                        0),
                                4),
                        isDisplayed()));
        button.check(matches(isDisplayed()));

        ViewInteraction viewGroup = onView((withId(R.id.action_bar)));
        viewGroup.check(matches(isDisplayed()));

    }

    @Test
    public void autoCompleteTextViewTwoSuggestions() {
        okClick();

        onView(withId(R.id.symbolText))
                .perform(ViewActions.typeText("jaros"), closeSoftKeyboard());

        onView(withText("Jarosław"))
                .inRoot(withDecorView(not(is(mainActivity.getWindow().getDecorView()))))
                .check(matches(isDisplayed()));

        onView(withText("Powiat jarosławski"))
                .inRoot(withDecorView(not(is(mainActivity.getWindow().getDecorView()))))
                .check(matches(isDisplayed()));

    }

    @Test
    public void autoCompleteTextViewOneSuggestions() {
        okClick();

        onView(withId(R.id.symbolText))
                .perform(ViewActions.typeText("Powiat jaros"), closeSoftKeyboard());

        onView(withText("Powiat jarosławski"))
                .inRoot(withDecorView(not(is(mainActivity.getWindow().getDecorView()))))
                .check(matches(isDisplayed()));

        onView(withText("Jarosław"))
                .inRoot(withDecorView(not(is(mainActivity.getWindow().getDecorView()))))
                .check(doesNotExist());
    }

    @Test
    public void loginEmptyTest() {
        okClick();

        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.agreeButton), withText("Log in")));
        appCompatButton.perform(scrollTo(), click());

        onView(withText(R.string.data_text)).inRoot(withDecorView(not(mainActivity.getWindow().getDecorView())))
                .check(matches(isDisplayed()));
    }

    private void okClick() {
        ViewInteraction appCompatButton = onView(
                allOf(withId(android.R.id.button1), withText("OK")));
        appCompatButton.perform(click());
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

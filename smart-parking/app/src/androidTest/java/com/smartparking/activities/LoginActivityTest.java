package com.smartparking.activities;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.smartparking.R;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class LoginActivityTest {

    @Rule
    public ActivityScenarioRule<LoginActivity> activityRule =
            new ActivityScenarioRule<>(LoginActivity.class);

    @Test
    public void loginButton_InitiallyDisabled() {
        onView(withId(R.id.loginButton))
                .check(matches(ViewMatchers.isNotEnabled()));
    }

    @Test
    public void invalidEmail_ShowsError() {
        // Type invalid email
        onView(withId(R.id.emailInput))
                .perform(typeText("invalid-email"), closeSoftKeyboard());

        // Type any password
        onView(withId(R.id.passwordInput))
                .perform(typeText("password123"), closeSoftKeyboard());

        // Click login button
        onView(withId(R.id.loginButton)).perform(click());

        // Verify error message
        onView(withId(R.id.emailInput))
                .check(matches(hasErrorText("Enter valid email address")));
    }

    @Test
    public void shortPassword_ShowsError() {
        // Type valid email
        onView(withId(R.id.emailInput))
                .perform(typeText("test@example.com"), closeSoftKeyboard());

        // Type short password
        onView(withId(R.id.passwordInput))
                .perform(typeText("123"), closeSoftKeyboard());

        // Click login button
        onView(withId(R.id.loginButton)).perform(click());

        // Verify error message
        onView(withId(R.id.passwordInput))
                .check(matches(hasErrorText("Password must be at least 6 characters")));
    }

    @Test
    public void validCredentials_StartsMainActivity() {
        // Type valid email
        onView(withId(R.id.emailInput))
                .perform(typeText("test@example.com"), closeSoftKeyboard());

        // Type valid password
        onView(withId(R.id.passwordInput))
                .perform(typeText("password123"), closeSoftKeyboard());

        // Click login button
        onView(withId(R.id.loginButton)).perform(click());

        // Verify DashboardActivity is launched
        onView(withId(R.id.toolbar))
                .check(matches(isDisplayed()));
    }

    @Test
    public void forgotPassword_OpensResetActivity() {
        // Click forgot password link
        onView(withId(R.id.forgotPasswordLink)).perform(click());

        // Verify ResetPasswordActivity is launched
        onView(withText(R.string.reset_password))
                .check(matches(isDisplayed()));
    }

    @Test
    public void signupLink_OpensSignupActivity() {
        // Click signup link
        onView(withId(R.id.signupLink)).perform(click());

        // Verify SignupActivity is launched
        onView(withText(R.string.create_account))
                .check(matches(isDisplayed()));
    }

    @Test
    public void emptyFields_ShowsError() {
        // Click login button without entering credentials
        onView(withId(R.id.loginButton)).perform(click());

        // Verify error messages
        onView(withId(R.id.emailInput))
                .check(matches(hasErrorText("Email is required")));
        onView(withId(R.id.passwordInput))
                .check(matches(hasErrorText("Password is required")));
    }

    @Test
    public void validInput_EnablesLoginButton() {
        // Type valid email
        onView(withId(R.id.emailInput))
                .perform(typeText("test@example.com"), closeSoftKeyboard());

        // Type valid password
        onView(withId(R.id.passwordInput))
                .perform(typeText("password123"), closeSoftKeyboard());

        // Verify login button is enabled
        onView(withId(R.id.loginButton))
                .check(matches(ViewMatchers.isEnabled()));
    }

    @Test
    public void clearFields_DisablesLoginButton() {
        // Type and clear email
        onView(withId(R.id.emailInput))
                .perform(typeText("test@example.com"), ViewActions.clearText(), closeSoftKeyboard());

        // Type and clear password
        onView(withId(R.id.passwordInput))
                .perform(typeText("password123"), ViewActions.clearText(), closeSoftKeyboard());

        // Verify login button is disabled
        onView(withId(R.id.loginButton))
                .check(matches(ViewMatchers.isNotEnabled()));
    }
}

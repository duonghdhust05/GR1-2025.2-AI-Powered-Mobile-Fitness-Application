package com.example.afit_gr1

import android.content.Context
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.afit_gr1.views.activity.OnboardingActivity
import com.example.afit_gr1.views.fragment.preference.PreferenceUtils
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class OnboardingFlowUITest {

    @Before
    fun setUp() {
        // Clear preferences to ensure Onboarding shows up
        val context = ApplicationProvider.getApplicationContext<Context>()
        val prefs = context.getSharedPreferences("MyPref", Context.MODE_PRIVATE)
        prefs.edit().clear().commit()
    }

    @After
    fun tearDown() {
        // Reset preferences
    }

    @Test
    fun testOnboardingFlow() {
        // Since SplashActivity determines routing (Onboarding vs Main),
        // we can just launch OnboardingActivity directly or SplashActivity.
        // Let's launch OnboardingActivity directly to test the flow.
        val scenario = ActivityScenario.launch(OnboardingActivity::class.java)

        // Give it a moment to render
        Thread.sleep(1000)

        // 1. First onboarding screen (FirstOnboardingFragment)
        // Check if nextButton is displayed and click it
        onView(withId(R.id.nextButton)).check(matches(isDisplayed())).perform(click())

        // 2. Second onboarding screen (SecondOnboardingFragment)
        Thread.sleep(1000)
        onView(withId(R.id.nextButton)).check(matches(isDisplayed())).perform(click())

        // 3. Third onboarding screen (ThirdOnboardingFragment)
        Thread.sleep(1000)
        onView(withId(R.id.nextButton)).check(matches(isDisplayed())).perform(click())

        // 4. Fourth onboarding screen (ForthOnboardingFragment)
        Thread.sleep(1000)
        // Click Get Started button (same ID, text changes)
        onView(withId(R.id.nextButton)).check(matches(isDisplayed())).perform(click())

        // 5. At this point, it should route to MainActivity (HomeFragment)
        // Wait for MainActivity to launch
        Thread.sleep(2000)

        // The button_start_exercise or btn_record_now should be visible on HomeFragment
        onView(withId(R.id.btn_record_now)).check(matches(isDisplayed()))

        scenario.close()
    }
}

package com.example.afit_gr1

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.recyclerview.widget.RecyclerView
import com.example.afit_gr1.views.activity.MainActivity
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class WorkoutFlowUITest {

    @Before
    fun setUp() {
        // Initialize the app state or clear preferences if needed
    }

    @After
    fun tearDown() {
        // Clean up
    }

    @Test
    fun testWorkoutFlow() {
        // Launch MainActivity
        val scenario = ActivityScenario.launch(MainActivity::class.java)

        // Ensure we are on HomeFragment by checking if btn_record_now is displayed
        // We use Thread.sleep for simplicity since animations might take time, 
        // though IdlingResource is preferred in production.
        Thread.sleep(1500)
        
        // 1. Click on workout tab
        onView(withId(R.id.workoutFragment)).perform(click())

        // 2. We are in WorkoutFragment. Click Start button.
        Thread.sleep(1000)
        onView(withId(R.id.button_start_exercise)).check(matches(isDisplayed())).perform(click())

        // 3. Skip tutorial if it appears
        Thread.sleep(1000)
        try {
            onView(withId(R.id.skipButton)).perform(click())
        } catch (e: Exception) {
            // Tutorial might not appear if already shown
        }

        // 4. Click complete exercise
        Thread.sleep(1000)
        onView(withId(R.id.button_complete_exercise)).check(matches(isDisplayed())).perform(click())

        // 5. Verify we navigate to CompletedFragment (checking for goToHomeFromComplete button)
        Thread.sleep(1000)
        onView(withId(R.id.goToHomeFromComplete)).check(matches(isDisplayed())).perform(click())

        // 6. Verify we are back on HomeFragment
        Thread.sleep(1000)
        onView(withId(R.id.btn_record_now)).check(matches(isDisplayed()))

        scenario.close()
    }
}

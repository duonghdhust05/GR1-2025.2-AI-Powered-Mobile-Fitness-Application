package com.example.afit_gr1

import android.content.Context
import android.content.Intent
import com.example.afit_gr1.R
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiScrollable
import androidx.test.uiautomator.UiSelector
import androidx.test.uiautomator.Until
import org.hamcrest.CoreMatchers
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 * Tests the entire End-to-End UI flow covering Profile, Plan, Home, Workout, Cancel, and Complete fragments.
 */
@RunWith(AndroidJUnit4::class)
class AutomatedUITest {
    private lateinit var device: UiDevice

    @Before
    fun setUp() {
        // Initialize UiDevice instance
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        device.pressHome()
        // Wait for the launcher
        val launcherPackage: String = device.currentPackageName
        ViewMatchers.assertThat(launcherPackage, CoreMatchers.notNullValue())
        device.wait(
            Until.hasObject(By.pkg(launcherPackage).depth(0)),
            5000
        )
        // Launch the app
        val context = ApplicationProvider.getApplicationContext<Context>()
        val intent =
            context.packageManager.getLaunchIntentForPackage("com.example.afit_gr1")?.apply {
                // Clear out any previous instances
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            }
        context.startActivity(intent)

        // wait until the app's UI displays
        device.wait(
            Until.hasObject(By.pkg("com.example.afit_gr1").depth(0)),
            5000
        )
    }

    @Test
    fun endToEndUITest() {
        // --- 1. ONBOARDING FLOW ---
        device.wait(Until.hasObject(By.res("com.example.afit_gr1:id/onboardingLayout")), 10000)
        device.findObject(By.res("com.example.afit_gr1:id/nextButton")).click()
        onView(withText("Elevate Your Workout Experience")).check(matches(isDisplayed()))
        device.findObject(By.res("com.example.afit_gr1:id/nextButton")).click()
        onView(withText("Hassle-Free Repetition Tracking")).check(matches(isDisplayed()))
        device.findObject(By.res("com.example.afit_gr1:id/nextButton")).click()

        // --- 2. HOME FRAGMENT ---
        device.wait(Until.hasObject(By.res("com.example.afit_gr1:id/btn_record_now")), 10000)

        // --- 3. PROFILE FRAGMENT ---
        device.findObject(By.res("com.example.afit_gr1:id/profileFragment")).click()
        device.wait(Until.hasObject(By.res("com.example.afit_gr1:id/textView1")), 5000)
        onView(withId(R.id.textView1)).check(matches(isDisplayed()))

        // --- 4. PLAN TAB & PLAN STEP ONE ---
        device.findObject(By.res("com.example.afit_gr1:id/planStepOneFragment")).click()
        device.wait(Until.hasObject(By.res("com.example.afit_gr1:id/recycler_view")), 5000)
        device.wait(Until.hasObject(By.text("Squat")), 5000)
        device.findObject(By.text("Squat")).click()

        // --- 5. PLAN STEP TWO ---
        device.wait(Until.hasObject(By.res("com.example.afit_gr1:id/exercise_name")), 5000)
        val repeatInput = device.findObject(By.res("com.example.afit_gr1:id/repeat_count"))
        repeatInput.text = "15"
        val daysList = UiScrollable(UiSelector().resourceId("com.example.afit_gr1:id/days_list"))
        
        daysList.scrollIntoView(UiSelector().text("Wednesday"))
        device.wait(Until.hasObject(By.text("Wednesday")), 5000)
        device.findObject(By.text("Wednesday")).click()
        
        daysList.scrollIntoView(UiSelector().text("Friday"))
        device.wait(Until.hasObject(By.text("Friday")), 5000)
        device.findObject(By.text("Friday")).click()
        
        daysList.scrollIntoView(UiSelector().text("Sunday"))
        device.wait(Until.hasObject(By.text("Sunday")), 5000)
        device.findObject(By.text("Sunday")).click()
        // Click Add Plan button
        onView(withId(R.id.btn_add_plan)).perform(click())

        // --- 6. BACK TO HOME ---
        device.wait(Until.hasObject(By.res("com.example.afit_gr1:id/btn_record_now")), 5000)
        onView(withId(R.id.btn_record_now)).check(matches(isDisplayed()))

        // --- 7. WORKOUT FRAGMENT (CANCEL FLOW) ---
        device.findObject(By.res("com.example.afit_gr1:id/workoutFragment")).click()
        device.wait(Until.hasObject(By.res("com.example.afit_gr1:id/button_start_exercise")), 5000)
        
        // Verify and click Camera Switch Button
        onView(withId(R.id.facing_switch)).check(matches(isDisplayed()))
        onView(withId(R.id.facing_switch)).perform(click())

        // Click Start Button
        device.findObject(By.res("com.example.afit_gr1:id/button_start_exercise")).click()

        // Wait for and click Skip Button (to dismiss tutorial)
        device.wait(Until.hasObject(By.res("com.example.afit_gr1:id/skipButton")), 5000)
        device.findObject(By.res("com.example.afit_gr1:id/skipButton")).click()

        // Verify and click Cancel Button
        device.wait(Until.hasObject(By.res("com.example.afit_gr1:id/button_cancel_exercise")), 5000)
        onView(withId(R.id.button_cancel_exercise)).check(matches(isDisplayed()))
        device.findObject(By.res("com.example.afit_gr1:id/button_cancel_exercise")).click()

        // --- 8. CANCEL FRAGMENT ---
        device.wait(Until.hasObject(By.res("com.example.afit_gr1:id/textView4")), 5000) // textView4 is "No Worries" or similar in CancelFragment
        onView(withId(R.id.textView4)).check(matches(isDisplayed()))
        // Click Home button to return to Home
        device.findObject(By.res("com.example.afit_gr1:id/goToHomeFromCancel")).click()

        // --- 9. WORKOUT FRAGMENT (COMPLETE FLOW) ---
        device.wait(Until.hasObject(By.res("com.example.afit_gr1:id/btn_record_now")), 5000)
        device.findObject(By.res("com.example.afit_gr1:id/workoutFragment")).click()
        device.wait(Until.hasObject(By.res("com.example.afit_gr1:id/button_start_exercise")), 5000)
        
        // Click Start Button
        device.findObject(By.res("com.example.afit_gr1:id/button_start_exercise")).click()

        // Wait for and click Skip Button (to dismiss tutorial)
        device.wait(Until.hasObject(By.res("com.example.afit_gr1:id/skipButton")), 5000)
        device.findObject(By.res("com.example.afit_gr1:id/skipButton")).click()

        // Verify and click Complete Button
        device.wait(Until.hasObject(By.res("com.example.afit_gr1:id/button_complete_exercise")), 5000)
        onView(withId(R.id.button_complete_exercise)).check(matches(isDisplayed()))
        device.findObject(By.res("com.example.afit_gr1:id/button_complete_exercise")).click()

        // --- 10. COMPLETED FRAGMENT ---
        device.wait(Until.hasObject(By.res("com.example.afit_gr1:id/textView4")), 5000) // textView4 in CompletedFragment
        onView(withId(R.id.textView4)).check(matches(isDisplayed()))
        // Click Home button to return to Home
        device.findObject(By.res("com.example.afit_gr1:id/goToHomeFromComplete")).click()

        // Verify we are back on HomeFragment
        device.wait(Until.hasObject(By.res("com.example.afit_gr1:id/btn_record_now")), 5000)
        onView(withId(R.id.btn_record_now)).check(matches(isDisplayed()))
    }
}
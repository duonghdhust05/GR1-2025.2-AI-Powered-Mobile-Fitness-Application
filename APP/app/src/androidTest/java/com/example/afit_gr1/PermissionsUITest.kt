package com.example.afit_gr1

import android.content.Context
import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PermissionsUITest {

    private lateinit var device: UiDevice

    @Before
    fun setUp() {
        // Initialize UiDevice
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        device.pressHome()

        val context = ApplicationProvider.getApplicationContext<Context>()
        
        // Revoke camera permission before test to ensure the dialog appears
        // Note: This shell command works on devices with API >= 28 usually, or we just rely on fresh install
        InstrumentationRegistry.getInstrumentation().uiAutomation.executeShellCommand(
            "pm revoke ${context.packageName} android.permission.CAMERA"
        )
        Thread.sleep(1000)

        val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)?.apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
        context.startActivity(intent)

        device.wait(Until.hasObject(By.pkg(context.packageName).depth(0)), 5000)
    }

    @Test
    fun testCameraPermissionDenyHandling() {
        // Navigate to WorkoutFragment which requests Camera Permission
        // Assumes we are at HomeFragment
        val workoutTab = device.wait(Until.findObject(By.res("com.example.afit_gr1:id/workoutFragment")), 5000)
        workoutTab?.click()

        // Wait for permission dialog (Android OS dialog)
        // Usually contains words like "Allow" or "Deny"
        val denyButton = device.wait(
            Until.findObject(
                By.res("com.android.permissioncontroller:id/permission_deny_button")
            ),
            5000
        )
        
        if (denyButton != null) {
            denyButton.click()
            
            // App should handle the denial gracefully, e.g., showing a Toast or staying on the screen
            // Here we just verify it didn't crash by checking if the app package is still in foreground
            val isAppStillOpen = device.wait(Until.hasObject(By.pkg("com.example.afit_gr1").depth(0)), 2000)
            assert(isAppStillOpen) { "App crashed or closed unexpectedly after denying permission" }
        }
    }
}

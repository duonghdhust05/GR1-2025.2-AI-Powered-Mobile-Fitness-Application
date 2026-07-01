package com.example.afit_gr1.views.fragment.preference;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.test.core.app.ApplicationProvider;

import com.example.afit_gr1.R;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 33)
public class PreferenceUtilsTest {

    private Context context;
    private SharedPreferences sharedPreferences;

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();
        sharedPreferences = context.getSharedPreferences(context.getPackageName() + "_preferences", Context.MODE_PRIVATE);
    }

    @Test
    public void testPreferGPUForPoseDetection_DefaultValue() {
        // By default, it should return true
        assertTrue(PreferenceUtils.preferGPUForPoseDetection(context));
    }

    @Test
    public void testPreferGPUForPoseDetection_CustomValue() {
        // Set custom value to false
        String prefKey = context.getString(R.string.pref_key_pose_detector_prefer_gpu);
        sharedPreferences.edit().putBoolean(prefKey, false).commit();

        assertFalse(PreferenceUtils.preferGPUForPoseDetection(context));
    }

    @Test
    public void testIsCameraLiveViewportEnabled_DefaultValue() {
        // By default, it should return false
        assertFalse(PreferenceUtils.isCameraLiveViewportEnabled(context));
    }
    
    @Test
    public void testIsCameraLiveViewportEnabled_CustomValue() {
        // Set custom value to true
        String prefKey = context.getString(R.string.pref_key_camera_live_viewport);
        sharedPreferences.edit().putBoolean(prefKey, true).commit();

        assertTrue(PreferenceUtils.isCameraLiveViewportEnabled(context));
    }
}

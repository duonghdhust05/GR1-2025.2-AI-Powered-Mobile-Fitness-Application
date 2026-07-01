package com.example.afit_gr1.posedetector

import android.content.Context
import android.graphics.Bitmap
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.afit_gr1.viewmodels.CameraXViewModel
import com.google.android.gms.tasks.Tasks
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.pose.defaults.PoseDetectorOptions
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class PoseDetectorIntegrationTest {

    private lateinit var context: Context
    private lateinit var processor: TestPoseDetectorProcessor
    private lateinit var viewModel: CameraXViewModel

    // A subclass to expose the protected method for testing
    class TestPoseDetectorProcessor(
        context: Context,
        options: PoseDetectorOptions,
        viewModel: CameraXViewModel
    ) : PoseDetectorProcessor(
        context, options, false, false, false, true, false, viewModel, emptyList()
    ) {
        fun runDetection(image: InputImage) = detectInImage(image)
    }

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        val options = PoseDetectorOptions.Builder()
            .setDetectorMode(PoseDetectorOptions.SINGLE_IMAGE_MODE)
            .build()
        // Initialize an empty view model to receive live data
        viewModel = CameraXViewModel(context as android.app.Application)
        processor = TestPoseDetectorProcessor(context, options, viewModel)
    }

    @Test
    fun testPoseDetectionPipeline() {
        // Create a dummy bitmap (100x100)
        val bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
        val inputImage = InputImage.fromBitmap(bitmap, 0)

        // Run the detection pipeline (ML Kit Pose -> Classification)
        val task = processor.runDetection(inputImage)

        // Wait for ML Kit task to complete (max 5 seconds)
        val result = Tasks.await(task, 5, TimeUnit.SECONDS)

        // Assert that the pipeline executed and returned a valid object
        // Even if no human pose is found in the blank image, it shouldn't crash
        // and should return a PoseWithClassification object.
        assertNotNull("Pipeline should return a non-null result", result)
        assertNotNull("Pose object should not be null", result.pose)

        // Clean up
        processor.stop()
    }
}

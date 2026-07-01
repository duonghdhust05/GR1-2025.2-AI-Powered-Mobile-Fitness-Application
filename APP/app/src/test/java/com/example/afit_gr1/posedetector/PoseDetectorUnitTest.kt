package com.example.afit_gr1.posedetector

import com.example.afit_gr1.posedetector.classification.ClassificationResult
import com.example.afit_gr1.posedetector.classification.EMASmoothing
import com.example.afit_gr1.posedetector.classification.RepetitionCounter
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * PoseDetectorUnitTest covers the Blackbox & Whitebox Unit Tests for the ML Kit Pose Classification logic.
 *
 * **Scope**: Focuses on `RepetitionCounter` and `EMASmoothing` inside the `classification` package.
 * 
 * **Workflow**:
 * - In the real app, these classes take continuous stream of probabilities (e.g., 80% Squat, 20% Stand)
 *   from the ML model and use time-series algorithms (EMA) to smooth out camera jitter, and then
 *   count full cycles (Stand -> Squat -> Stand = 1 Rep).
 * - Here, we feed mock probability values artificially to verify the logic correctly filters noise
 *   and increments the rep count under the right conditions.
 *
 * **Coupling/Cohesion**:
 * - High cohesion: These test pure logic isolated from the Android Context and ML Kit cameras.
 */
class PoseDetectorUnitTest {

    /**
     * **Test Type**: Whitebox Unit Test.
     * **Description**: Tests the `RepetitionCounter` workflow.
     * **Workflow**: 
     * Simulates a user performing a Squat by feeding high confidence values for 'squat_down',
     * followed by high confidence for 'neutral_standing'. The counter should only increment
     * when a full cycle is completed and the confidence threshold is crossed.
     */
    @Test
    fun testRepetitionCounter_ValidCycle() {
        // Arrange
        val counter = RepetitionCounter("squat_down")
        // Provide a confidence of 10.0f (max) which easily crosses the DEFAULT_ENTER_THRESHOLD of 6.0f
        val poseSquat = ClassificationResult().apply { putClassConfidence("squat_down", 10.0f) }
        val poseStand = ClassificationResult().apply { putClassConfidence("neutral_standing", 10.0f) }

        // Act & Assert
        // 1. Initial State (No Reps)
        assertEquals(0, counter.getNumRepeats())

        // 2. User squats down (Confidence high for squat)
        counter.addClassificationResult(poseSquat)
        assertEquals("Still 0, cycle not complete", 0, counter.getNumRepeats())

        // 3. User stands back up (Cycle complete)
        counter.addClassificationResult(poseStand)
        assertEquals("Should be 1 rep after returning to standing", 1, counter.getNumRepeats())
    }

    /**
     * **Test Type**: Blackbox Unit Test.
     * **Description**: Tests `EMASmoothing` (Exponential Moving Average).
     * **Workflow**: 
     * Ensures that when anomalous/jittery classifications are passed in, the EMA algorithm 
     * smooths the curve by relying on historical data, preventing false-positive rep counts.
     */
    @Test
    fun testEMASmoothing_SmoothsAnomalies() {
        val smoothing = EMASmoothing(10, 0.2f) // Window 10, alpha 0.2
        
        // Feed consistent 100% confidence for Squat
        for (i in 1..5) {
            val result = ClassificationResult().apply { putClassConfidence("squat_down", 1.0f) }
            smoothing.getSmoothedResult(result)
        }

        // Introduce a sudden noise/glitch (0% confidence)
        val glitch = ClassificationResult().apply { putClassConfidence("squat_down", 0.0f) }
        val smoothedGlitch = smoothing.getSmoothedResult(glitch)

        // The smoothed output should NOT instantly drop to 0, because of the moving average history
        val smoothedConfidence = smoothedGlitch.getClassConfidence("squat_down")
        assert(smoothedConfidence > 0.5f) { "EMA should prevent sudden drops. Actual: $smoothedConfidence" }
    }
}

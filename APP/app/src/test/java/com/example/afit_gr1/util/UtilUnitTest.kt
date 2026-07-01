package com.example.afit_gr1.util

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * UtilUnitTest covers the Blackbox & Whitebox Unit Tests for utility functions.
 *
 * **Scope**: Focuses on pure math/string mapping logic inside the `util` package, such as `MyUtils`.
 * 
 * **Workflow**:
 * - Feeds various standard, edge-case, and invalid inputs into the static utility methods.
 * - Asserts the mapped output or mathematical result against the expected outcome.
 *
 * **Coupling/Cohesion**:
 * - Extremely low coupling: These tests run incredibly fast because they require zero Android Context
 *   or external dependencies.
 */
class UtilUnitTest {

    /**
     * **Test Type**: Whitebox Unit Test.
     * **Description**: Tests `exerciseNameToDisplay` mapping.
     * **Workflow**: 
     * Verifies the exhaustive `when` statement correctly converts DB/ML Kit strings into
     * human-readable strings. Tests the default branch (fallback) logic as well.
     */
    @Test
    fun testExerciseNameToDisplay_ValidAndFallback() {
        // Valid mappings
        assertEquals("Squat", MyUtils.exerciseNameToDisplay("squats"))
        assertEquals("Push up", MyUtils.exerciseNameToDisplay("pushups_down"))

        // Fallback (if not mapped, should return the exact string passed in)
        assertEquals("unknown_exercise", MyUtils.exerciseNameToDisplay("unknown_exercise"))
    }

    /**
     * **Test Type**: Blackbox Unit Test.
     * **Description**: Tests `convertTimeStringToMinutes`.
     * **Workflow**: 
     * Inputs various time string formats ("mm:ss", "hh:mm:ss", invalid formats) and 
     * checks if the mathematical conversion to total minutes is strictly accurate.
     */
    @Test
    fun testConvertTimeStringToMinutes_AllFormats() {
        // "mm:ss" format -> 5 mins 30 secs = 5.5 minutes
        val mmSsResult = MyUtils.convertTimeStringToMinutes("05:30")
        assertEquals("5 minutes and 30 seconds should be 5.5 minutes", 5.5, mmSsResult, 0.001)

        // "hh:mm:ss" format -> 1 hour, 30 mins, 0 secs = 90.0 minutes
        val hhMmSsResult = MyUtils.convertTimeStringToMinutes("01:30:00")
        assertEquals("1h 30m should be 90.0 minutes", 90.0, hhMmSsResult, 0.001)

        // Invalid format
        val invalidResult = MyUtils.convertTimeStringToMinutes("invalid_time_format")
        assertEquals("Invalid format should return 0.0 safely", 0.0, invalidResult, 0.001)
    }
}

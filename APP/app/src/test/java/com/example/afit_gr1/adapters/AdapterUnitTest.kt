package com.example.afit_gr1.adapters

import com.example.afit_gr1.data.plan.ExerciseData
import com.example.afit_gr1.data.plan.ExercisePlan
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * AdapterUnitTest covers the logic of RecyclerView adapters.
 * 
 * **Scope**: Focuses on `WorkoutAdapter` and similar classes.
 * 
 * **Workflow**:
 * - Tests the adapter's ability to process and combine multiple data sources 
 *   (e.g., `exerciseList` vs `workoutPlan`) accurately without needing a full Android Context or UI.
 * 
 * **Coupling/Cohesion**:
 * - Low coupling (mocked data). Tests purely the data mapping logic of the Adapter.
 */
class AdapterUnitTest {

    /**
     * **Test Type**: Blackbox Unit Test.
     * **Description**: Tests `getItemCount()` for `WorkoutAdapter`.
     * **Workflow**: 
     * Verifies that the adapter correctly reflects the size of the active `exerciseList`, 
     * ignoring the size of the `workoutPlan` (which might contain future exercises not yet started).
     */
    @Test
    fun testWorkoutAdapter_ItemCount() {
        // Arrange
        val currentProgress = listOf(
            ExerciseData(planId = 1, exerciseName = "Squat", repetitions = 5, confidence = 0.9f, isComplete = false),
            ExerciseData(planId = 2, exerciseName = "Push up", repetitions = 0, confidence = 0.0f, isComplete = false)
        )
        val goalPlan = listOf(
            ExercisePlan(planId = 1, exerciseName = "Squat", repetitions = 10),
            ExercisePlan(planId = 2, exerciseName = "Push up", repetitions = 10),
            ExercisePlan(planId = 3, exerciseName = "Sit up", repetitions = 15) // Future exercise
        )

        val adapter = WorkoutAdapter(currentProgress, goalPlan)

        // Act
        val count = adapter.itemCount

        // Assert
        assertEquals("Adapter item count should strictly match the active exercise list size", 2, count)
    }
}

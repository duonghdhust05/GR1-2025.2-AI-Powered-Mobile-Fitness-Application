package com.example.afit_gr1.data

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.afit_gr1.data.database.AppDatabase
import com.example.afit_gr1.data.plan.Plan
import com.example.afit_gr1.data.plan.PlanDataDao
import com.example.afit_gr1.data.results.WorkoutResult
import com.example.afit_gr1.data.results.WorkoutResultDao
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

/**
 * DataLayerTest covers both Unit and Integration Tests for the `data` package.
 * 
 * **Scope**: This class specifically targets the Data Access Objects (DAOs) and the Room Database.
 * **Workflow**: 
 * - In the overall project architecture, the DAOs are responsible for executing SQL queries and bridging 
 *   the gap between the SQLite database and the Kotlin data classes (`Plan` and `WorkoutResult`).
 * - We utilize `Room.inMemoryDatabaseBuilder` to ensure tests run in RAM and are completely isolated.
 *   This prevents test data from polluting the real application database.
 */
@RunWith(AndroidJUnit4::class)
class DataLayerTest {

    // Ensures LiveData operations happen synchronously for testing
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var planDao: PlanDataDao
    private lateinit var resultDao: WorkoutResultDao
    private lateinit var db: AppDatabase

    /**
     * Initializes the in-memory Room database and the corresponding DAOs before each test.
     * This acts as the setup phase for our Whitebox Integration Testing.
     */
    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java
        ).allowMainThreadQueries().build()
        planDao = db.planDao()
        resultDao = db.resultDao()
    }

    /**
     * Closes the database connection after each test completes, ensuring memory is freed.
     */
    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    // ==========================================
    // UNIT TESTS & BLACKBOX TESTS FOR PLAN DAO
    // ==========================================

    /**
     * **Test Type**: Blackbox & Whitebox Unit Test.
     * **Description**: Tests the `insert` and `getPlansByDay` functionality of `PlanDataDao`.
     * **Workflow**: 
     * 1. Creates a dummy `Plan` object with a specific selected day ("Monday").
     * 2. Inserts the object via DAO (Blackbox input).
     * 3. Retrieves the list of plans for "Monday" using the custom SQL query in DAO.
     * 4. Asserts that the data returned matches the input, verifying both persistence and query logic (Whitebox).
     */
    @Test
    @Throws(Exception::class)
    fun writePlanAndReadByDay() = runBlocking {
        val plan = Plan(
            id = 1,
            exercise = "Squat",
            calories = 150.0,
            repeatCount = 10,
            selectedDays = "Monday",
            completed = false
        )
        planDao.insert(plan)

        val retrievedPlans = planDao.getPlansByDay("Monday")
        assertNotNull("The retrieved plan list should not be null", retrievedPlans)
        assertEquals("There should be exactly 1 plan for Monday", 1, retrievedPlans.size)
        assertEquals("The exercise name must match the inserted data", "Squat", retrievedPlans[0].exercise)
    }

    /**
     * **Test Type**: Whitebox Unit Test.
     * **Description**: Tests the `addCompletedTime` method.
     * **Workflow**: 
     * Verifies that updating a plan's completion status via direct SQL UPDATE query 
     * correctly modifies the boolean flag in the database without affecting other fields.
     */
    @Test
    @Throws(Exception::class)
    fun updatePlanCompletionStatus() = runBlocking {
        val plan = Plan(id = 2, exercise = "Lunge", calories = 100.0, repeatCount = 15, selectedDays = "Friday", completed = false)
        planDao.insert(plan)

        // Update the completion status
        val timeStamp = System.currentTimeMillis()
        planDao.addCompletedTime(completed = true, time = timeStamp, id = 2)

        val updatedPlans = planDao.getPlansByDay("Friday")
        assertTrue("The plan should be marked as completed", updatedPlans[0].completed)
        assertEquals("The completion timestamp should match", timeStamp, updatedPlans[0].timeCompleted)
    }

    // ==================================================
    // UNIT TESTS & BLACKBOX TESTS FOR RESULT DAO
    // ==================================================

    /**
     * **Test Type**: Blackbox Integration Test.
     * **Description**: Tests `WorkoutResultDao` insertion and `getRecentWorkout` fetching.
     * **Workflow**: 
     * 1. Inserts multiple `WorkoutResult` records into the database.
     * 2. Calls `getRecentWorkout()` to ensure the DAO correctly aggregates and returns 
     *    the most recently saved results according to its underlying query structure.
     */
    @Test
    @Throws(Exception::class)
    fun writeAndReadWorkoutResults() = runBlocking {
        val result1 = WorkoutResult(id = 1, exerciseName = "Push Up", repeatedCount = 20, confidence = 0.95f, timestamp = System.currentTimeMillis(), calorie = 50.0, workoutTimeInMin = 5.0)
        val result2 = WorkoutResult(id = 2, exerciseName = "Sit Up", repeatedCount = 15, confidence = 0.85f, timestamp = System.currentTimeMillis() + 1000, calorie = 40.0, workoutTimeInMin = 4.0)
        
        resultDao.insert(result1)
        resultDao.insert(result2)

        val recentWorkouts = resultDao.getRecentWorkout()
        assertNotNull("Recent workouts should not be null", recentWorkouts)
        assertEquals("Should retrieve the inserted workouts", 2, recentWorkouts.size)
    }
}

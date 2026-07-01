package com.example.afit_gr1.viewmodels

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.afit_gr1.data.plan.Plan
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * ViewModelLayerTest covers both Unit and Integration Tests for the `viewmodels` package.
 * 
 * **Scope**: This class specifically targets the ViewModels like `AddPlanViewModel` and `HomeViewModel`.
 * **Workflow**: 
 * - In the overall project, ViewModels act as the intermediary between the UI (Fragments) and the Data Layer (Repository).
 * - They expose `LiveData` streams to the UI and contain coroutine blocks to perform asynchronous database operations.
 * - By running these as Instrumented tests (@RunWith(AndroidJUnit4::class)), we can pass a real Android 
 *   Application context to `AndroidViewModel`, allowing it to instantiate the `AppRepository` naturally.
 */
@RunWith(AndroidJUnit4::class)
class ViewModelLayerTest {

    // Ensures LiveData and coroutine operations happen synchronously for predictable testing
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var addPlanViewModel: AddPlanViewModel
    private lateinit var homeViewModel: HomeViewModel

    /**
     * Initializes the ViewModels with the testing Application Context.
     */
    @Before
    fun setUp() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        addPlanViewModel = AddPlanViewModel(app)
        homeViewModel = HomeViewModel(app)
    }

    // ==========================================================
    // INTEGRATION & BLACKBOX TESTS FOR VIEWMODELS
    // ==========================================================

    /**
     * **Test Type**: Blackbox Integration Test.
     * **Description**: Tests the entire pipeline from creating a plan via `AddPlanViewModel` 
     * to retrieving it via `HomeViewModel`.
     * **Workflow**: 
     * 1. Generates a valid `Plan` entity.
     * 2. Calls `addPlanViewModel.insert()` which launches a coroutine to save it in the DB.
     * 3. Calls `homeViewModel.getNotCompletePlans()` to query the DB for the newly added plan.
     * 4. Asserts that the plan exists and contains the exact values inserted. 
     * This proves that the ViewModels successfully communicate through the underlying Repository/DAO layer.
     */
    @Test
    fun insertPlanAndRetrieveViaHomeViewModel() = runBlocking {
        // 1. Arrange: Create a Plan object
        val plan = Plan(
            id = 0, // 0 allows Room to auto-generate the ID
            exercise = "Deadlift",
            calories = 200.0,
            repeatCount = 5,
            selectedDays = "Tuesday",
            completed = false
        )

        // 2. Act: Insert via AddPlanViewModel
        addPlanViewModel.insert(plan)

        // Give it a brief moment to ensure DB transaction finishes (though runBlocking usually handles it)
        Thread.sleep(500)

        // 3. Act: Retrieve via HomeViewModel
        val retrievedPlans = homeViewModel.getNotCompletePlans("Tuesday")

        // 4. Assert: Verify the data was passed down to DB and back up to the other ViewModel
        assertNotNull("The retrieved plans list should not be null", retrievedPlans)
        assertTrue("The list should contain at least one plan", retrievedPlans!!.isNotEmpty())
        
        // Find the specific plan we just inserted
        val insertedPlan = retrievedPlans.find { it.exercise == "Deadlift" }
        assertNotNull("The inserted Deadlift plan should exist in Tuesday's list", insertedPlan)
        assertEquals("Repeat count should match", 5, insertedPlan?.repeatCount)
    }

    /**
     * **Test Type**: Whitebox Unit Test.
     * **Description**: Tests the edge case of retrieving a plan for a day with no data.
     * **Workflow**: 
     * Asks `HomeViewModel` for plans on a day where nothing was registered. 
     * It should gracefully return an empty list rather than throwing a NullPointerException or crashing.
     */
    @Test
    fun retrievePlansForEmptyDayReturnsEmpty() = runBlocking {
        // Query for a highly unlikely day string to ensure DB is empty for it
        val retrievedPlans = homeViewModel.getPlanByDay("NonExistentDay899")
        
        assertNotNull("Should return an empty list, not null", retrievedPlans)
        assertEquals("The list should have exactly 0 items", 0, retrievedPlans?.size)
    }
}

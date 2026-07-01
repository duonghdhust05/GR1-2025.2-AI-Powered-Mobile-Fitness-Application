package com.example.afit_gr1.data.plan

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.afit_gr1.data.database.AppDatabase
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class PlanDataDaoTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: AppDatabase
    private lateinit var planDao: PlanDataDao

    @Before
    fun setup() {
        // Use an in-memory database so information is not persisted when the process dies.
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()

        planDao = database.planDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertPlanAndGetByDay() = runBlocking {
        // Arrange
        val plan = Plan(
            id = 1,
            exercise = "Pushups",
            calories = 50.0,
            repeatCount = 15,
            selectedDays = "Monday,Wednesday,Friday",
            completed = false
        )

        // Act
        planDao.insert(plan)
        val mondayPlans = planDao.getPlansByDay("Monday")
        val tuesdayPlans = planDao.getPlansByDay("Tuesday")

        // Assert
        assertEquals(1, mondayPlans.size)
        assertEquals("Pushups", mondayPlans[0].exercise)
        assertTrue(tuesdayPlans.isEmpty())
    }

    @Test
    fun getNotCompletePlanByDay() = runBlocking {
        // Arrange
        val plan1 = Plan(
            id = 1,
            exercise = "Squats",
            calories = 60.0,
            repeatCount = 20,
            selectedDays = "Monday",
            completed = false
        )
        val plan2 = Plan(
            id = 2,
            exercise = "Pullups",
            calories = 40.0,
            repeatCount = 10,
            selectedDays = "Monday",
            completed = true
        )
        planDao.insert(plan1)
        planDao.insert(plan2)

        // Act
        val notCompleted = planDao.getNotCompletePlanByDay("Monday")

        // Assert
        assertEquals(1, notCompleted.size)
        assertEquals("Squats", notCompleted[0].exercise)
    }
}

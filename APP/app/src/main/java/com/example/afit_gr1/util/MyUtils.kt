package com.example.afit_gr1.util

import android.util.Log

/**
 * Utility functions for data transformation and formatting.
 * 
 * **Meaning & Role**: A stateless helper class containing static methods (in `companion object`) 
 * used to convert strings and times between different formats across the app.
 * 
 * **Workflow**:
 * - `exerciseNameToDisplay`: Converts the raw ML Kit classification label (e.g., "squats") 
 *   into a human-readable UI string ("Squat").
 * - `databaseNameToClassification`: Does the reverse mapping, converting DB/UI strings back 
 *   to ML Kit readable constants.
 * - `convertTimeStringToMinutes`: Parses "mm:ss" or "hh:mm:ss" strings into a total `Double` of minutes.
 * 
 * **Coupling**:
 * - Low coupling. This class has zero dependencies on ML Kit, Android Context, or DAOs.
 * - Used heavily by `Adapters`, `ViewModels`, and `PoseClassifier`.
 * 
 * **Cohesion**:
 * - High cohesion. Purely focused on standalone text/time transformation functions.
 */
class MyUtils {

    companion object {
        fun exerciseNameToDisplay(variableName: String): String {
            return when (variableName) {
                "squats" -> "Squat"
                "pushups_down" -> "Push up"
                "lunges" -> "Lunge"
                "situp_up" -> "Sit up"
                "chestpress_down" -> "Chest press"
                "deadlift_down" -> "Dead lift"
                "shoulderpress_down" -> "Shoulder press"
                "warrior" -> "Warrior Yoga"
                "tree_pose" -> "Tree Yoga"
                // Add more cases as needed
                else -> variableName // Default to the original name if not matched
            }
        }

        fun convertTimeStringToMinutes(timeString: String): Double {
            val components = timeString.split(":")
            val total: Double

            when (components.size) {
                2 -> {
                    val (minutes, seconds) = components.map { it.toDouble() }
                    total = minutes + seconds / 60
                    Log.d("total-time $minutes $seconds", total.toString())
                }

                3 -> {
                    val (hours, minutes, seconds) = components.map { it.toDouble() }
                    total = hours * 60 + minutes + seconds / 60
                    Log.d("total-time $hours $minutes $seconds", total.toString())
                }

                else -> {
                    Log.e(
                        "Invalid time format",
                        "The time string must be in 'hh:mm' or 'hh:mm:ss' format."
                    )
                    return 0.0
                }
            }
            return total
        }

        fun databaseNameToClassification(variableName: String): String {
            return when (variableName) {
                "Push up" -> "pushups_down"
                "Lunge" -> "lunges"
                "Squat" -> "squats"
                "Sit up" -> "situp_up"
                "Chest press" -> "chestpress_down"
                "Dead lift" -> "deadlift_down"
                "Shoulder press" -> "shoulderpress_down"
                "Warrior Yoga" -> "warrior"
                "Tree Yoga" -> "tree_pose"
                // Add more cases as needed
                else -> variableName // Default to the original name if not matched
            }
        }
    }
}
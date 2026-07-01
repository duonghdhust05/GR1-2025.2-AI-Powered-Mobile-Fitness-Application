package com.example.afit_gr1.adapters

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.afit_gr1.R
import com.example.afit_gr1.data.plan.ExerciseData
import com.example.afit_gr1.data.plan.ExercisePlan
import com.example.afit_gr1.util.MyUtils.Companion.exerciseNameToDisplay
import com.example.afit_gr1.viewmodels.WorkoutViewHolder

/**
 * Adapter for rendering the current workout progress in a RecyclerView.
 * 
 * **Meaning & Role**: While the `PlanAdapter` shows *intended* workouts, the `WorkoutAdapter` 
 * shows the *live progress* during an active workout session.
 * 
 * **Workflow**:
 * 1. Receives two lists: `exerciseList` (Current live progress) and `workoutPlan` (The goal).
 * 2. In `onBindViewHolder`, it matches the current exercise with its planned goal 
 *    to display progress (e.g., "5/10 Reps").
 * 3. Dynamically changes the text color to GREEN if the exercise is marked `isComplete`.
 * 
 * **Coupling**:
 * - Coupled to `RecyclerView`.
 * - Coupled to two separate data models: `ExerciseData` and `ExercisePlan`.
 * - Uses `MyUtils.exerciseNameToDisplay` for string formatting.
 * 
 * **Cohesion**:
 * - High cohesion. Focuses exclusively on combining real-time progress data with planned data 
 *   to render a unified progress item.
 */
class WorkoutAdapter(
    private val exerciseList: List<ExerciseData>,
    private val workoutPlan: List<ExercisePlan>
) : RecyclerView.Adapter<WorkoutViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.exercise_item, parent, false)
        return WorkoutViewHolder(itemView)
    }


    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: WorkoutViewHolder, position: Int) {
        // Retrieve the current exercise data
        val currentExercise = exerciseList[position]

        // Find the corresponding exercise plan for the current exercise
        val repetition: Int? = workoutPlan.find {
            it.exerciseName.equals(
                currentExercise.exerciseName,
                ignoreCase = true
            )
        }?.repetitions

        // Set text sizes for consistency
        holder.exerciseNameTextView.textSize = 22f
        holder.repetitionsTextView.textSize = 22f
        holder.isCompleteTextView.textSize = 22f

        // Adjust text color based on whether the exercise is complete or not
        if (currentExercise.isComplete) {
            holder.exerciseNameTextView.setTextColor(Color.GREEN)
            holder.repetitionsTextView.setTextColor(Color.GREEN)
            holder.isCompleteTextView.setTextColor(Color.GREEN)
        }

        // Set text for exercise name, repetitions, and completion status
        holder.exerciseNameTextView.text =
            "${exerciseNameToDisplay(currentExercise.exerciseName)}: "
        holder.repetitionsTextView.text = if (repetition != null) {
            "${currentExercise.repetitions}/${repetition} "
        } else {
            "${currentExercise.repetitions}"
        }
        holder.isCompleteTextView.text = if (currentExercise.isComplete) {
            "Complete"
        } else {
            ""
        }
    }

    override fun getItemCount(): Int {
        // Return the number of items in the exercise list
        return exerciseList.size
    }

}
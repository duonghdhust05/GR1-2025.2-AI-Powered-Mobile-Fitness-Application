package com.example.afit_gr1.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.afit_gr1.R
import com.example.afit_gr1.data.plan.Plan
import com.example.afit_gr1.posedetector.classification.PoseClassifierProcessor.CHEST_PRESS_CLASS
import com.example.afit_gr1.posedetector.classification.PoseClassifierProcessor.DEAD_LIFT_CLASS
import com.example.afit_gr1.posedetector.classification.PoseClassifierProcessor.LUNGES_CLASS
import com.example.afit_gr1.posedetector.classification.PoseClassifierProcessor.PUSHUPS_CLASS
import com.example.afit_gr1.posedetector.classification.PoseClassifierProcessor.SHOULDER_PRESS_CLASS
import com.example.afit_gr1.posedetector.classification.PoseClassifierProcessor.SITUP_UP_CLASS
import com.example.afit_gr1.posedetector.classification.PoseClassifierProcessor.SQUATS_CLASS
import com.example.afit_gr1.util.MyUtils.Companion.databaseNameToClassification
import java.util.Collections

/**
 * Adapter for rendering the user's exercise plans in a RecyclerView.
 * 
 * **Meaning & Role**: This adapter acts as the bridge between the raw list of `Plan` entities 
 * (coming from the database/ViewModel) and the visual UI items on the Home screen.
 * 
 * **Workflow**:
 * 1. Takes a `MutableList<Plan>`.
 * 2. In `onCreateViewHolder`, inflates the `plan_list_item.xml` layout.
 * 3. In `onBindViewHolder`, binds the `Plan` data to the text views (Exercise name, rep count) 
 *    and maps the exercise name to a local drawable image via `exerciseImages` map.
 * 4. Listens for delete button clicks and triggers the injected `ItemListener` interface.
 * 
 * **Coupling**:
 * - Highly coupled with the Android `RecyclerView` framework.
 * - Coupled to the `Plan` data model.
 * - Coupled to specific drawable resources (e.g., `R.drawable.squat`).
 * 
 * **Cohesion**:
 * - High cohesion. Purely responsible for data-to-view binding for Plans.
 */
class PlanAdapter internal constructor(context: Context) :
    RecyclerView.Adapter<PlanAdapter.ViewHolder>() {
    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var planList: MutableList<Plan> = Collections.emptyList()
    private lateinit var listener: ItemListener


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val workoutImage: ImageView = itemView.findViewById(R.id.imageView)
        val workoutName: TextView = itemView.findViewById(R.id.exercise_title)
        val repeat: TextView = itemView.findViewById(R.id.exercise_rep)
        val deleteButton: Button = itemView.findViewById(R.id.delete_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Inflate the item view from the layout
        val itemView = inflater.inflate(R.layout.plan_list_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        // Return the number of items in the plan list
        return planList.size
    }

    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Bind data to the item views
        val currentPlan = planList[position]
        holder.workoutImage.setImageResource(
            getDrawableResourceIdExercise(
                databaseNameToClassification(currentPlan.exercise)
            )
        )
        holder.workoutName.text = currentPlan.exercise
        holder.repeat.text = "${currentPlan.repeatCount} ${currentPlan.exercise} a day"
        holder.deleteButton.setOnClickListener {
            // Handle delete button click and notify the listener
            listener.onItemClicked(currentPlan.id, position)
            notifyDataSetChanged()
        }
    }

    // Interface for item click events
    interface ItemListener {
        fun onItemClicked(planId: Int, position: Int)
    }

    // Set the listener for item click events
    fun setListener(listener: ItemListener) {
        this.listener = listener
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setPlans(plans: MutableList<Plan>) {
        // Set the plan list and notify data set changed
        this.planList = plans
        notifyDataSetChanged()
    }

    /**
     * List of yoga images
     */
    private val exerciseImages = mapOf(
        SQUATS_CLASS to R.drawable.squat,
        LUNGES_CLASS to R.drawable.reverse_lunges,
        SITUP_UP_CLASS to R.drawable.sit_ups,
        PUSHUPS_CLASS to R.drawable.push_up,
        CHEST_PRESS_CLASS to R.drawable.chest_press,
        DEAD_LIFT_CLASS to R.drawable.dead_lift,
        SHOULDER_PRESS_CLASS to R.drawable.shoulder_press
    )

    private fun getDrawableResourceIdExercise(exerciseKey: String): Int {
        // Get the image resource ID for the given exercise key
        return exerciseImages[exerciseKey]
            ?: throw IllegalArgumentException("Invalid yoga pose key: $exerciseKey")
    }

}
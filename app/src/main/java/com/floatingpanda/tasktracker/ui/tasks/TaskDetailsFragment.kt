package com.floatingpanda.tasktracker.ui.tasks

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.floatingpanda.tasktracker.R
import com.floatingpanda.tasktracker.data.Day
import com.floatingpanda.tasktracker.data.task.RepeatableTaskTemplate
import com.floatingpanda.tasktracker.databinding.FragmentTaskDetailsBinding
import com.floatingpanda.tasktracker.ui.history.TaskCompletionHistoryAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.mongodb.kbson.BsonObjectId
import org.mongodb.kbson.ObjectId

class TaskDetailsFragment : Fragment() {
    private var _binding: FragmentTaskDetailsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val taskViewModel: TaskViewModel by viewModels<TaskViewModel> { TaskViewModel.Factory }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTaskDetailsBinding.inflate(inflater, container, false)
        val taskArgs: TaskDetailsFragmentArgs by navArgs()
        val root: View = binding.root
        val id: ObjectId =
            BsonObjectId.invoke(taskArgs.taskIdHexString)
        val template: RepeatableTaskTemplate? = taskViewModel.getTemplate(id)

        if (template == null) {
            val title = binding.taskTitle
            title.text = "ERROR: NO TASK FOUND"

            Log.e("onCreateView", "Unable to find task for $id")
        } else {
            val title = binding.taskTitle
            title.text = template.title

            val category = binding.taskCategory
            category.text = template.category

            val info = binding.taskInfo
            if (template.info == null)
                info.visibility = View.INVISIBLE
            else
                info.text = template.info

            val period = binding.taskSchedulePeriod
            period.text = template.repeatPeriod.value

            val timesPerPeriod = binding.taskScheduleTimesPerPeriod
            timesPerPeriod.text = template.timesPerPeriod.toString()

            val subPeriod = binding.taskScheduleSubPeriod
            if (template.subRepeatPeriod == null)
                subPeriod.setText("None")
            else
                subPeriod.setText(template.subRepeatPeriod!!.value)

            val timesPerSubPeriod = binding.taskScheduleTimesPerSubPeriod
            if (template.maxTimesPerSubPeriod == null)
                timesPerSubPeriod.text = "N/A"
            else
                timesPerSubPeriod.text = template.maxTimesPerSubPeriod!!.toString()

            val eligibleDays = template.eligibleDays

            val monday = binding.daysCheckboxMonday
            monday.isChecked = eligibleDays.contains(Day.MONDAY)

            val tuesday = binding.daysCheckboxTuesday
            tuesday.isChecked = eligibleDays.contains(Day.TUESDAY)

            val wednesday = binding.daysCheckboxWednesday
            wednesday.isChecked = eligibleDays.contains(Day.WEDNESDAY)

            val thursday = binding.daysCheckboxThursday
            thursday.isChecked = eligibleDays.contains(Day.THURSDAY)

            val friday = binding.daysCheckboxFriday
            friday.isChecked = eligibleDays.contains(Day.FRIDAY)

            val saturday = binding.daysCheckboxSaturday
            saturday.isChecked = eligibleDays.contains(Day.SATURDAY)

            val sunday = binding.daysCheckboxSunday
            sunday.isChecked = eligibleDays.contains(Day.SUNDAY)

            val completionsList = binding.recordCompletionsList
            completionsList.adapter =
                TaskCompletionHistoryAdapter(taskViewModel.getRecordCompletionsForTemplate(template.id)) {
                    val action =
                        TaskDetailsFragmentDirections.actionTaskDetailsFragmentToIndividualRecordCompletionsFragment(
                            it
                        )
                    root.findNavController().navigate(action)
                }

            val editButton = binding.root.findViewById<FloatingActionButton>(R.id.fab);
            editButton.setOnClickListener {
                val action =
                    TaskDetailsFragmentDirections.actionTaskDetailsFragmentToTaskUpsertDetailsFragment(
                        id.toHexString()
                    )
                root.findNavController().navigate(action)
            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
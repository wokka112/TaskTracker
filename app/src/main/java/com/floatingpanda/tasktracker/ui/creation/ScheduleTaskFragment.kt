package com.floatingpanda.tasktracker.ui.creation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.floatingpanda.tasktracker.R
import com.floatingpanda.tasktracker.data.Day
import com.floatingpanda.tasktracker.data.Period

class ScheduleTaskFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //TODO check how to make sure the viewmodel is the same as the one from the CreateTaskFragment
        val taskCreationViewModel =
            ViewModelProvider(this)[TaskCreationViewModel::class.java]
        val view = inflater.inflate(R.layout.fragment_task_creation_schedule, container)

        setupEditTexts(view, taskCreationViewModel)
        setupSpinners(view, taskCreationViewModel)
        setupEligibleDaysLogic(view, taskCreationViewModel)

        // TODO add in on click listeners for back and continue buttons
        //  For now could just create new element when hitting continue

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    private fun setupEligibleDaysLogic(
        rootView: View,
        taskCreationViewModel: TaskCreationViewModel
    ) {
        val mondayCheckBox: CheckBox = rootView.findViewById(R.id.days_checkbox_monday)
        val tuesdayCheckBox: CheckBox = rootView.findViewById(R.id.days_checkbox_tuesday)
        val wednesdayCheckBox: CheckBox = rootView.findViewById(R.id.days_checkbox_wednesday)
        val thursdayCheckBox: CheckBox = rootView.findViewById(R.id.days_checkbox_thursday)
        val fridayCheckBox: CheckBox = rootView.findViewById(R.id.days_checkbox_friday)
        val saturdayCheckBox: CheckBox = rootView.findViewById(R.id.days_checkbox_saturday)
        val sundayCheckBox: CheckBox = rootView.findViewById(R.id.days_checkbox_sunday)

        mondayCheckBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked)
                taskCreationViewModel.addEligibleDay(Day.MONDAY)
            else
                taskCreationViewModel.removeEligibleDay(Day.MONDAY)
        }
        tuesdayCheckBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked)
                taskCreationViewModel.addEligibleDay(Day.TUESDAY)
            else
                taskCreationViewModel.removeEligibleDay(Day.TUESDAY)
        }
        wednesdayCheckBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked)
                taskCreationViewModel.addEligibleDay(Day.WEDNESDAY)
            else
                taskCreationViewModel.removeEligibleDay(Day.WEDNESDAY)
        }
        thursdayCheckBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked)
                taskCreationViewModel.addEligibleDay(Day.THURSDAY)
            else
                taskCreationViewModel.removeEligibleDay(Day.THURSDAY)
        }
        fridayCheckBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked)
                taskCreationViewModel.addEligibleDay(Day.FRIDAY)
            else
                taskCreationViewModel.removeEligibleDay(Day.FRIDAY)
        }
        saturdayCheckBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked)
                taskCreationViewModel.addEligibleDay(Day.SATURDAY)
            else
                taskCreationViewModel.removeEligibleDay(Day.SATURDAY)
        }
        sundayCheckBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked)
                taskCreationViewModel.addEligibleDay(Day.SUNDAY)
            else
                taskCreationViewModel.removeEligibleDay(Day.SUNDAY)
        }
    }

    private fun setupSpinners(rootView: View, taskCreationViewModel: TaskCreationViewModel) {
        val periodSpinner: Spinner = rootView.findViewById(R.id.period_dropdown);
        val subPeriodSpinner: Spinner = rootView.findViewById(R.id.period_dropdown);

        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.period_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            periodSpinner.adapter = adapter
        }

        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.period_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            subPeriodSpinner.adapter = adapter
        }

        taskCreationViewModel.period.observe(this.viewLifecycleOwner) {
            // Set spinner to be equal to period value
        }
        periodSpinner.onItemSelectedListener =
            PeriodSpinnerOnItemSelectedListener(taskCreationViewModel)

        taskCreationViewModel.subPeriod.observe(this.viewLifecycleOwner) {
            // Set spinner to be equal to period value
        }
        subPeriodSpinner.onItemSelectedListener =
            SubPeriodSpinnerOnItemSelectedListener(taskCreationViewModel)
    }

    private fun setupEditTexts(rootView: View, taskCreationViewModel: TaskCreationViewModel) {
        val timesPerPeriodInput: EditText = rootView.findViewById(R.id.times_per_period_input)
        val timesPerSubPeriodInput: EditText =
            rootView.findViewById(R.id.times_per_sub_period_input)

        taskCreationViewModel.timesPerPeriod.observe(this.viewLifecycleOwner) {
            timesPerPeriodInput.setText(it, TextView.BufferType.EDITABLE)
        }
        timesPerPeriodInput.doAfterTextChanged {
            if (taskCreationViewModel.timesPerPeriod.value != Integer.parseInt(it.toString()))
                taskCreationViewModel.timesPerPeriod.postValue(Integer.parseInt(it.toString()))
        }

        taskCreationViewModel.timesPerSubPeriod.observe(this.viewLifecycleOwner) {
            if (it != null)
                timesPerSubPeriodInput.setText(it, TextView.BufferType.EDITABLE)
        }
        timesPerSubPeriodInput.doAfterTextChanged {
            if (taskCreationViewModel.timesPerSubPeriod.value != Integer.parseInt(it.toString()))
                taskCreationViewModel.timesPerSubPeriod.postValue(Integer.parseInt(it.toString()))
        }
    }

    class PeriodSpinnerOnItemSelectedListener(private val taskCreationViewModel: TaskCreationViewModel) :
        AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            parent?.getItemAtPosition(position).also { item ->
                taskCreationViewModel.setPeriod(
                    Period.convertFromString(item.toString())
                )
            }
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {
        }
    }

    class SubPeriodSpinnerOnItemSelectedListener(private val taskCreationViewModel: TaskCreationViewModel) :
        AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            parent?.getItemAtPosition(position).also { item ->
                taskCreationViewModel.setSubPeriod(
                    Period.convertFromString(item.toString())
                )
            }
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {
        }
    }
}
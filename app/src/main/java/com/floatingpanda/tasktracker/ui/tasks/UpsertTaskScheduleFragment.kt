package com.floatingpanda.tasktracker.ui.tasks

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.floatingpanda.tasktracker.R
import com.floatingpanda.tasktracker.data.Day
import com.floatingpanda.tasktracker.data.Period
import java.util.stream.Collectors

class UpsertTaskScheduleFragment : Fragment() {
    private val taskUpsertViewModel: TaskUpsertViewModel by activityViewModels { TaskUpsertViewModel.Factory }
    private lateinit var createButton: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_task_upsert_schedule, container, false)
        createButton = view.findViewById(R.id.create_button)

        setupEditTexts(view, taskUpsertViewModel)
        setupSpinners(view, taskUpsertViewModel)
        setupEligibleDaysLogic(view, taskUpsertViewModel)
        setupSubPeriodEnabledLogic(view, taskUpsertViewModel)

        view.findViewById<Button>(R.id.cancel_button).setOnClickListener {
            findNavController().navigateUp()
        }
        createButton.setOnClickListener {
            try {
                taskUpsertViewModel.createTask()
                findNavController().navigate(R.id.nav_todos)
            } catch (e: Exception) {
                Log.e("task creation", "Error creating task. Template creation failed")
            }
        }

        return view
    }

    private fun setupSubPeriodEnabledLogic(
        rootView: View,
        taskUpsertViewModel: TaskUpsertViewModel
    ) {
        val subPeriodEnabledCheckBox: CheckBox =
            rootView.findViewById(R.id.sub_period_enabled_checkbox)
        val subPeriodSpinner: Spinner = rootView.findViewById(R.id.sub_period_dropdown)
        val timesPerSubPeriodInput: EditText =
            rootView.findViewById(R.id.max_times_per_sub_period_input)


        taskUpsertViewModel.getIsSubPeriodEnabled().observe(this.viewLifecycleOwner) { enabled ->
            subPeriodSpinner.visibility = if (enabled) View.VISIBLE else View.GONE
            subPeriodSpinner.isEnabled = enabled
            timesPerSubPeriodInput.visibility = if (enabled) View.VISIBLE else View.GONE
            timesPerSubPeriodInput.isEnabled = enabled
            enableCreateButtonIfParametersValid()
        }

        subPeriodEnabledCheckBox.setOnCheckedChangeListener { _, checked ->
            taskUpsertViewModel.setIsSubPeriodEnabled(checked)
        }

        taskUpsertViewModel.getPeriod().observe(this.viewLifecycleOwner) { period ->
            if (period == Period.DAILY) {
                subPeriodEnabledCheckBox.isChecked = false
                subPeriodEnabledCheckBox.isEnabled = false
            } else {
                subPeriodEnabledCheckBox.isEnabled = true
            }
        }
    }

    private fun enableCreateButtonIfParametersValid() {
        createButton.isEnabled = taskUpsertViewModel.hasValidRecordScheduleDetails()
    }

    private fun setupEligibleDaysLogic(
        rootView: View,
        taskUpsertViewModel: TaskUpsertViewModel
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
                taskUpsertViewModel.addEligibleDay(Day.MONDAY)
            else
                taskUpsertViewModel.removeEligibleDay(Day.MONDAY)

            enableCreateButtonIfParametersValid()
        }
        tuesdayCheckBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked)
                taskUpsertViewModel.addEligibleDay(Day.TUESDAY)
            else
                taskUpsertViewModel.removeEligibleDay(Day.TUESDAY)

            enableCreateButtonIfParametersValid()
        }
        wednesdayCheckBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked)
                taskUpsertViewModel.addEligibleDay(Day.WEDNESDAY)
            else
                taskUpsertViewModel.removeEligibleDay(Day.WEDNESDAY)

            enableCreateButtonIfParametersValid()
        }
        thursdayCheckBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked)
                taskUpsertViewModel.addEligibleDay(Day.THURSDAY)
            else
                taskUpsertViewModel.removeEligibleDay(Day.THURSDAY)

            enableCreateButtonIfParametersValid()
        }
        fridayCheckBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked)
                taskUpsertViewModel.addEligibleDay(Day.FRIDAY)
            else
                taskUpsertViewModel.removeEligibleDay(Day.FRIDAY)

            enableCreateButtonIfParametersValid()
        }
        saturdayCheckBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked)
                taskUpsertViewModel.addEligibleDay(Day.SATURDAY)
            else
                taskUpsertViewModel.removeEligibleDay(Day.SATURDAY)

            enableCreateButtonIfParametersValid()
        }
        sundayCheckBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked)
                taskUpsertViewModel.addEligibleDay(Day.SUNDAY)
            else
                taskUpsertViewModel.removeEligibleDay(Day.SUNDAY)

            enableCreateButtonIfParametersValid()
        }
    }

    private fun setupSpinners(rootView: View, taskUpsertViewModel: TaskUpsertViewModel) {
        val periodSpinner: Spinner = rootView.findViewById(R.id.period_dropdown);
        val subPeriodSpinner: Spinner = rootView.findViewById(R.id.sub_period_dropdown);

        val periodAdapter = ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_spinner_item,
            taskUpsertViewModel.getValidPeriods().stream().map(Period::value).collect(
                Collectors.toList()
            )
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            periodSpinner.adapter = adapter
        }

        val validPeriodAdapter: ValidPeriodAdapter = ValidPeriodAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            taskUpsertViewModel.getValidPeriods(),
            Period.getValidSubPeriods(Period.DAILY)
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            subPeriodSpinner.adapter = adapter
        }
        taskUpsertViewModel.getValidSubPeriods().observe(this.viewLifecycleOwner) {
            validPeriodAdapter.setValidPeriods(it)
        }

        taskUpsertViewModel.getPeriod().observe(this.viewLifecycleOwner) {
            val selectedPeriod = periodSpinner.selectedItem
            if (it != null && selectedPeriod != it)
                periodSpinner.setSelection(periodAdapter.getPosition(it.value))

            enableCreateButtonIfParametersValid()
        }
        periodSpinner.onItemSelectedListener =
            PeriodSpinnerOnItemSelectedListener(taskUpsertViewModel)

        taskUpsertViewModel.getSubPeriod().observe(this.viewLifecycleOwner) {
            val selectedPeriod = subPeriodSpinner.selectedItem
            if (it != null && selectedPeriod != it)
                subPeriodSpinner.setSelection(
                    validPeriodAdapter.getPosition(
                        it.value
                    )
                )

            enableCreateButtonIfParametersValid()
        }
        subPeriodSpinner.onItemSelectedListener =
            SubPeriodSpinnerOnItemSelectedListener(taskUpsertViewModel)
    }

    private fun setupEditTexts(rootView: View, taskUpsertViewModel: TaskUpsertViewModel) {
        val timesPerPeriodInput: EditText = rootView.findViewById(R.id.times_per_period_input)
        val timesPerSubPeriodInput: EditText =
            rootView.findViewById(R.id.max_times_per_sub_period_input)

        taskUpsertViewModel.getTimesPerPeriod().observe(this.viewLifecycleOwner) {
            if (timesPerPeriodInput.text == null || (timesPerPeriodInput.text.toString()
                    .isNotBlank() && timesPerPeriodInput.text.toString() != it.toString())
            ) {
                timesPerPeriodInput.setText(it?.toString(), TextView.BufferType.EDITABLE)
            }

            enableCreateButtonIfParametersValid()
        }
        timesPerPeriodInput.doAfterTextChanged {
            if (it.toString().isBlank())
                taskUpsertViewModel.setTimesPerPeriod(0)
            else if (taskUpsertViewModel.getTimesPerPeriod().value != Integer.parseInt(it.toString()))
                taskUpsertViewModel.setTimesPerPeriod(Integer.parseInt(it.toString()))
        }

        taskUpsertViewModel.getMaxTimesPerSubPeriod().observe(this.viewLifecycleOwner) {
            if (timesPerSubPeriodInput.text == null || (timesPerPeriodInput.text.toString()
                    .isNotBlank() && timesPerSubPeriodInput.text.toString() != it.toString())
            ) {
                timesPerSubPeriodInput.setText(it?.toString(), TextView.BufferType.EDITABLE)
            }

            enableCreateButtonIfParametersValid()
        }
        timesPerSubPeriodInput.doAfterTextChanged {
            if (it.toString().isBlank())
                taskUpsertViewModel.setMaxTimesPerSubPeriod(0)
            else if (taskUpsertViewModel.getMaxTimesPerSubPeriod().value != Integer.parseInt(it.toString()))
                taskUpsertViewModel.setMaxTimesPerSubPeriod(Integer.parseInt(it.toString()))
        }
    }

    class PeriodSpinnerOnItemSelectedListener(private val taskUpsertViewModel: TaskUpsertViewModel) :
        AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            parent?.getItemAtPosition(position).also { item ->
                taskUpsertViewModel.setPeriod(
                    Period.convertFromString(item.toString())
                )
            }
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {
        }
    }

    class SubPeriodSpinnerOnItemSelectedListener(private val taskUpsertViewModel: TaskUpsertViewModel) :
        AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            parent?.getItemAtPosition(position).also { item ->
                taskUpsertViewModel.setSubPeriod(
                    Period.convertFromString(item.toString())
                )
            }
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {
        }
    }
}
package com.floatingpanda.tasktracker.ui.template

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.floatingpanda.tasktracker.data.Day
import com.floatingpanda.tasktracker.data.task.RepeatableTaskTemplate
import com.floatingpanda.tasktracker.databinding.FragmentTemplateDetailsBinding
import org.mongodb.kbson.BsonObjectId
import org.mongodb.kbson.ObjectId

class TemplateDetailsFragment : Fragment() {
    private var _binding: FragmentTemplateDetailsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val templateViewModel: TemplateViewModel by viewModels<TemplateViewModel> { TemplateViewModel.Factory }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTemplateDetailsBinding.inflate(inflater, container, false)
        val templateArgs: TemplateDetailsFragmentArgs by navArgs()
        val root: View = binding.root
        val id: ObjectId =
            BsonObjectId.invoke(templateArgs.templateIdHexString)
        val template: RepeatableTaskTemplate? = templateViewModel.getTemplate(id)

        if (template == null) {
            val title = binding.templateTitle
            title.text = "ERROR: NO TEMPLATE FOUND"

            Log.e("onCreateView", "Unable to find template for $id")
        } else {
            val title = binding.templateTitle
            title.text = template.title

            val category = binding.templateCategory
            category.text = template.category

            val info = binding.templateInfo
            if (template.info == null)
                info.visibility = View.INVISIBLE
            else
                info.text = template.info

            val period = binding.templateSchedulePeriod
            period.text = template.repeatPeriod.value

            val timesPerPeriod = binding.templateScheduleTimesPerPeriod
            timesPerPeriod.text = template.timesPerPeriod.toString()

            val subPeriod = binding.templateScheduleSubPeriod
            if (template.subRepeatPeriod == null)
                subPeriod.setText("None")
            else
                subPeriod.setText(template.subRepeatPeriod!!.value)

            val timesPerSubPeriod = binding.templateScheduleTimesPerSubPeriod
            if (template.maxTimesPerSubPeriod == null)
                timesPerSubPeriod.text = "0"
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

            val editButton = binding.editButton
            //TODO add in onClickListener to take to an edit screen
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
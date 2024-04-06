package com.floatingpanda.tasktracker.ui.home

import androidx.lifecycle.ViewModel
import com.floatingpanda.tasktracker.data.Period
import com.floatingpanda.tasktracker.data.task.RepeatableTaskRecord
import com.floatingpanda.tasktracker.data.task.TaskDetails
import java.util.Calendar

class HomeViewModel : ViewModel() {
    private val records: List<RepeatableTaskRecord>

    init {
        val today = Calendar.getInstance()
        val tomorrow = Calendar.getInstance()
        tomorrow.add(Calendar.DAY_OF_YEAR, 1)
        val nextWeek = Calendar.getInstance()
        nextWeek.add(Calendar.WEEK_OF_YEAR, 1);
        val nextMonth = Calendar.getInstance()
        nextMonth.add(Calendar.MONTH, 1);
        val nextYear = Calendar.getInstance()
        nextYear.add(Calendar.YEAR, 1);

        // I want: a daily record,
        val wakeUpTemplate = TaskDetails("Wake up", null, "none", Period.DAILY, 1, false);
        val wakeUpRecord = RepeatableTaskRecord(wakeUpTemplate, today.timeInMillis, tomorrow.timeInMillis, 0);
        // a daily record with 3 times in period,
        val stretchTemplate = TaskDetails("Stretch", null, "Health", Period.DAILY, 3, false);
        val stretchRecord = RepeatableTaskRecord(stretchTemplate, today.timeInMillis, tomorrow.timeInMillis, 0);
        // a weekly record,
        val ocdGroupTemplate = TaskDetails("Attend OCD group", null, "Health", Period.WEEKLY, 1, false)
        val ocdGroupRecord = RepeatableTaskRecord(ocdGroupTemplate, today.timeInMillis, nextWeek.timeInMillis, 0);
        // a weekly record with 3 times in period,
        val runTemplate = TaskDetails("Run", null, "Health", Period.WEEKLY, 3, true);
        val runRecord = RepeatableTaskRecord(runTemplate, today.timeInMillis, nextWeek.timeInMillis, 0);
        // a weekly record with 3 times in period and allowing more than once per day,
        val readTemplate = TaskDetails("Read", null, "Leisure", Period.WEEKLY, 3, false);
        val readRecord = RepeatableTaskRecord(readTemplate, today.timeInMillis, nextWeek.timeInMillis, 0);
        // a monthly record,
        val exploreWildernessTemplate = TaskDetails("Explore the wilderness", null, "Leisure", Period.MONTHLY, 2, true);
        val exploreWildernessRecord = RepeatableTaskRecord(exploreWildernessTemplate, today.timeInMillis, nextMonth.timeInMillis, 0);
        // A quarterly record,
        val breakFromWorkTemplate = TaskDetails("Take a break from work", null, "Leisure", Period.QUARTERLY, 1, true);
        // a yearly record
        val holidayTemplate = TaskDetails("Go on holiday", null, "Leisure", Period.YEARLY, 3, true);

    }

}
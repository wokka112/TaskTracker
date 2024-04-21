package com.floatingpanda.tasktracker.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.floatingpanda.tasktracker.data.Day
import com.floatingpanda.tasktracker.data.Period
import com.floatingpanda.tasktracker.data.task.RepeatableTaskRecord
import com.floatingpanda.tasktracker.data.task.RepeatableTaskTemplate
import com.floatingpanda.tasktracker.data.task.TaskDetails
import java.util.Calendar
import java.util.stream.Collectors

class HomeViewModel : ViewModel() {
    private val records: MutableLiveData<List<RepeatableTaskRecord>>

    fun getRecords(): LiveData<List<RepeatableTaskRecord>> {
        return records
    }

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

        val days = listOf<Day>(
            Day.MONDAY,
            Day.TUESDAY,
            Day.WEDNESDAY,
            Day.THURSDAY,
            Day.FRIDAY,
            Day.SATURDAY,
            Day.SUNDAY
        )

        val weekdays = listOf<Day>(
            Day.MONDAY,
            Day.TUESDAY,
            Day.WEDNESDAY,
            Day.THURSDAY,
            Day.FRIDAY
        )

        // a daily record,
        val wakeUpDetails = TaskDetails("Wake up", null, "none")
        val wakeUpTemplate =
            RepeatableTaskTemplate(wakeUpDetails, Period.DAILY, 1, null, null, days)
        val wakeUpRecord =
            RepeatableTaskRecord(wakeUpTemplate, today.timeInMillis, tomorrow.timeInMillis)

        // a daily record with 3 times in period,
        val stretchDetails = TaskDetails("Stretch", null, "Health")
        val stretchTemplate =
            RepeatableTaskTemplate(stretchDetails, Period.DAILY, 3, null, null, days)
        val stretchRecord =
            RepeatableTaskRecord(stretchTemplate, today.timeInMillis, tomorrow.timeInMillis)

        // a weekly record with once per period,
        val ocdGroupDetails =
            TaskDetails("Attend OCD group", null, "Health")
        val ocdGroupTemplate =
            RepeatableTaskTemplate(ocdGroupDetails, Period.WEEKLY, 1, null, null, days)
        val ocdGroupRecord =
            RepeatableTaskRecord(ocdGroupTemplate, today.timeInMillis, nextWeek.timeInMillis)

        // a weekly record with 3 times in period and only once daily, but no weekends
        val runDetails = TaskDetails("Run", null, "Health")
        val runTemplate =
            RepeatableTaskTemplate(runDetails, Period.WEEKLY, 3, Period.DAILY, 1, weekdays)
        val runRecord =
            RepeatableTaskRecord(runTemplate, today.timeInMillis, nextWeek.timeInMillis)

        // a weekly record with 3 times in period and allowing twice daily,
        val readDetails = TaskDetails("Read", null, "Leisure")
        val readTemplate =
            RepeatableTaskTemplate(readDetails, Period.WEEKLY, 3, Period.DAILY, 2, days)
        val readRecord =
            RepeatableTaskRecord(readTemplate, today.timeInMillis, nextWeek.timeInMillis)

        // a weekly record with 3 times in period and allowing unlimited times daily
        val walkDetails = TaskDetails("Walk", null, "Leisure")
        val walkTemplate = RepeatableTaskTemplate(walkDetails, Period.WEEKLY, 3, null, null, days);
        val walkRecord =
            RepeatableTaskRecord(walkTemplate, today.timeInMillis, nextWeek.timeInMillis)

        // a monthly record,
        val exploreWildernessDetails =
            TaskDetails("Explore the wilderness", null, "Leisure")
        val exploreWildernessTemplate =
            RepeatableTaskTemplate(exploreWildernessDetails, Period.MONTHLY, 2, null, null, days)
        val exploreWildernessRecord = RepeatableTaskRecord(
            exploreWildernessTemplate,
            today.timeInMillis,
            nextMonth.timeInMillis
        )

        // a monthly record allowing 2 times per week
        val rosieDetails = TaskDetails("Do stuff with Rosie", null, "Leisure")
        val rosieTemplate =
            RepeatableTaskTemplate(rosieDetails, Period.MONTHLY, 4, Period.WEEKLY, 2, days)
        val rosieRecord =
            RepeatableTaskRecord(rosieTemplate, today.timeInMillis, nextMonth.timeInMillis)

        // a yearly record
        val holidayDetails = TaskDetails("Go on holiday", null, "Leisure");
        val holidayTemplate =
            RepeatableTaskTemplate(holidayDetails, Period.YEARLY, 4, null, null, days)
        val holidayRecord =
            RepeatableTaskRecord(holidayTemplate, today.timeInMillis, nextYear.timeInMillis)

        // A yearly record allowing 2 times per month,
        val breakFromWorkDetails =
            TaskDetails("Take a break from work", null, "Leisure")
        val breakFromWorkTemplate =
            RepeatableTaskTemplate(breakFromWorkDetails, Period.YEARLY, 6, Period.MONTHLY, 2, days)
        val breakFromWorkRecord =
            RepeatableTaskRecord(breakFromWorkTemplate, today.timeInMillis, nextYear.timeInMillis)

        records = MutableLiveData()
        records.value = listOf(
            wakeUpRecord,
            stretchRecord,
            ocdGroupRecord,
            runRecord,
            readRecord,
            walkRecord,
            exploreWildernessRecord,
            rosieRecord,
            holidayRecord,
            breakFromWorkRecord
        )
    }

    fun updateRecord(record: RepeatableTaskRecord) {
        var storedRecords: List<RepeatableTaskRecord>? = records.value

        if (storedRecords == null)
            return

        records.postValue(storedRecords.stream().map {
            if (it.title == record.title)
                record
            else
                it
        }.collect(Collectors.toList()))
    }
}
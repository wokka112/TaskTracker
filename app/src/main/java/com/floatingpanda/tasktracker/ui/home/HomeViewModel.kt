package com.floatingpanda.tasktracker.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.floatingpanda.tasktracker.data.Day
import com.floatingpanda.tasktracker.data.Period
import com.floatingpanda.tasktracker.data.old.OldRepeatableTaskRecord
import com.floatingpanda.tasktracker.data.old.OldRepeatableTaskTemplate
import com.floatingpanda.tasktracker.data.old.OldTaskDetails
import java.time.LocalDate
import java.util.stream.Collectors

class HomeViewModel : ViewModel() {
    private val records: MutableLiveData<List<OldRepeatableTaskRecord>>

    fun getRecords(): LiveData<List<OldRepeatableTaskRecord>> {
        return records
    }

    fun addRecord(template: OldRepeatableTaskTemplate) {
        val newRecords = records.value as ArrayList
        val today = LocalDate.now()

        newRecords.add(
            OldRepeatableTaskRecord(
                template,
                today,
                calculateEndDay(today, template.repeatPeriod)
            )
        )

        records.postValue(newRecords)
    }

    private fun calculateEndDay(today: LocalDate, period: Period): LocalDate {
        return when (period) {
            Period.WEEKLY -> {
                val dayOfWeek = today.dayOfWeek.value
                today.plusDays((7 - (dayOfWeek - 1)).toLong())
            }

            Period.MONTHLY -> {
                today.plusMonths(1).withDayOfMonth(1)
            }

            Period.YEARLY -> {
                today.plusYears(1).withDayOfYear(1)
            }

            else -> {
                today.plusDays(1)
            }
        }
    }

    init {
        val today = LocalDate.now()
        val tomorrow = today.plusDays(1)
        val dayOfWeek = today.dayOfWeek.value
        val nextWeek = today.plusDays((7 - (dayOfWeek - 1)).toLong())
        val nextMonth = today.plusMonths(1).withDayOfMonth(1)
        val nextYear = today.plusYears(1).withDayOfYear(1)

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
        val wakeUpDetails = OldTaskDetails("Wake up", null, "none")
        val wakeUpTemplate =
            OldRepeatableTaskTemplate(wakeUpDetails, Period.DAILY, 1, null, null, days)
        val wakeUpRecord =
            OldRepeatableTaskRecord(wakeUpTemplate, today, tomorrow)

        // a daily record with 3 times in period,
        val stretchDetails = OldTaskDetails("Stretch", null, "Health")
        val stretchTemplate =
            OldRepeatableTaskTemplate(stretchDetails, Period.DAILY, 3, null, null, days)
        val stretchRecord =
            OldRepeatableTaskRecord(stretchTemplate, today, tomorrow)

        // a weekly record with once per period,
        val ocdGroupDetails =
            OldTaskDetails("Attend OCD group", null, "Health")
        val ocdGroupTemplate =
            OldRepeatableTaskTemplate(ocdGroupDetails, Period.WEEKLY, 1, null, null, days)
        val ocdGroupRecord =
            OldRepeatableTaskRecord(ocdGroupTemplate, today, nextWeek)

        // a weekly record with 3 times in period and only once daily, but no weekends
        val runDetails = OldTaskDetails("Run", null, "Health")
        val runTemplate =
            OldRepeatableTaskTemplate(runDetails, Period.WEEKLY, 3, Period.DAILY, 1, weekdays)
        val runRecord =
            OldRepeatableTaskRecord(runTemplate, today, nextWeek)

        // a weekly record with 3 times in period and allowing twice daily,
        val readDetails = OldTaskDetails("Read", null, "Leisure")
        val readTemplate =
            OldRepeatableTaskTemplate(readDetails, Period.WEEKLY, 3, Period.DAILY, 2, days)
        val readRecord =
            OldRepeatableTaskRecord(readTemplate, today, nextWeek)

        // a weekly record with 3 times in period and allowing unlimited times daily
        val walkDetails = OldTaskDetails("Walk", null, "Leisure")
        val walkTemplate = OldRepeatableTaskTemplate(walkDetails, Period.WEEKLY, 3, null, null, days);
        val walkRecord =
            OldRepeatableTaskRecord(walkTemplate, today, nextWeek)

        // a monthly record,
        val exploreWildernessDetails =
            OldTaskDetails("Explore the wilderness", null, "Leisure")
        val exploreWildernessTemplate =
            OldRepeatableTaskTemplate(exploreWildernessDetails, Period.MONTHLY, 2, null, null, days)
        val exploreWildernessRecord = OldRepeatableTaskRecord(
            exploreWildernessTemplate,
            today,
            nextMonth
        )

        // a monthly record allowing 2 times per week
        val rosieDetails = OldTaskDetails("Do stuff with Rosie", null, "Leisure")
        val rosieTemplate =
            OldRepeatableTaskTemplate(rosieDetails, Period.MONTHLY, 4, Period.WEEKLY, 2, days)
        val rosieRecord =
            OldRepeatableTaskRecord(rosieTemplate, today, nextMonth)

        // a yearly record
        val holidayDetails = OldTaskDetails("Go on holiday", null, "Leisure");
        val holidayTemplate =
            OldRepeatableTaskTemplate(holidayDetails, Period.YEARLY, 4, null, null, days)
        val holidayRecord =
            OldRepeatableTaskRecord(holidayTemplate, today, nextYear)

        // A yearly record allowing 2 times per month,
        val breakFromWorkDetails =
            OldTaskDetails("Take a break from work", null, "Leisure")
        val breakFromWorkTemplate =
            OldRepeatableTaskTemplate(breakFromWorkDetails, Period.YEARLY, 6, Period.MONTHLY, 2, days)
        val breakFromWorkRecord =
            OldRepeatableTaskRecord(breakFromWorkTemplate, today, nextYear)

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

    fun updateRecord(record: OldRepeatableTaskRecord) {
        var storedRecords: List<OldRepeatableTaskRecord>? = records.value

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
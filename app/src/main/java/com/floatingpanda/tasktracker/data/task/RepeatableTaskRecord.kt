package com.floatingpanda.tasktracker.data.task

import com.floatingpanda.tasktracker.data.Period
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.ChronoField

class RepeatableTaskRecord(
    private val template: RepeatableTaskTemplate,
    private val startDate: Long,
    // TODO endDate seems unnecessary, we can calculate that
    private val endDate: Long,
) {
    private val completionsPerDate: MutableMap<String, Int> = HashMap()
    //TODO should we just add an int to track how many completions in the sub-period?

    val isComplete: Boolean
        get() = completionsPerDate.size >= template.timesPerPeriod
    val title: String
        get() = template.title
    val info: String?
        get() = template.info;
    val category: String
        get() = template.category

    fun getTimesLeftForSubPeriod(): Int {
        var timesLeft = template.timesPerSubPeriod ?: template.timesPerPeriod
        return timesLeft - getCompletionsForSubPeriod()
    }

    fun getCompletionsForSubPeriod(): Int {
        return getCompletionsForPeriod(template.subPeriod ?: template.repeatPeriod)
    }

    fun isCompleteForSubPeriod(): Boolean {
        if (isComplete)
            return true

        var timesPerSubPeriod = template.timesPerSubPeriod
        if (timesPerSubPeriod == null)
            timesPerSubPeriod = template.timesPerPeriod

        return getCompletionsForSubPeriod() >= timesPerSubPeriod
    }

    fun doTaskOnce() {
        val today = LocalDate.now()
        val completionsForToday = completionsPerDate.getOrDefault(today.toString(), 0)
        completionsPerDate[today.toString()] = completionsForToday + 1
    }

    private fun getCompletionsForPeriod(period: Period): Int {
        var earlierDayInPeriod = datesFromStartOfPeriod(period)
        val today = LocalDate.now()

        var completionsInPeriod = 0;
        do {
            completionsInPeriod += completionsPerDate.getOrDefault(earlierDayInPeriod.toString(), 0)
            earlierDayInPeriod = earlierDayInPeriod.plusDays(1)
        } while (!earlierDayInPeriod.isAfter(today))

        return completionsInPeriod
    }

    private fun datesFromStartOfPeriod(period: Period): LocalDate {
        if (period == Period.WEEKLY) {
            return LocalDate.now().with(
                ChronoField.DAY_OF_WEEK,
                DayOfWeek.MONDAY.getLong(ChronoField.DAY_OF_WEEK)
            )
        } else if (period == Period.MONTHLY) {
            return LocalDate.now().withDayOfMonth(1)
        }

        return LocalDate.now()
    }
}
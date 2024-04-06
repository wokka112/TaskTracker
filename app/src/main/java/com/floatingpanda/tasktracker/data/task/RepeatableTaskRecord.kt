package com.floatingpanda.tasktracker.data.task

import com.floatingpanda.tasktracker.data.Period
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.ChronoField

class RepeatableTaskRecord(
    private val template: RepeatableTaskTemplate,
    private val startDate: Long,
    private val endDate: Long,
    private val completionsPerDate: Map<String, Int>
) {
    val isComplete: Boolean
        get() = completionsPerDate.size >= template.timesPerPeriod

    fun isCompleteForSubPeriod(): Boolean {
        var earlierDayInSubPeriod = datesFromStartOfSubPeriod()
        val today = LocalDate.now()

        var completionsInSubPeriod = 0;
        do {
            completionsInSubPeriod += completionsPerDate[earlierDayInSubPeriod.toString()] ?: 0
            if (completionsInSubPeriod >= template.timesPerSubPeriod)
                return true

            earlierDayInSubPeriod = earlierDayInSubPeriod.plusDays(1)
        } while (!earlierDayInSubPeriod.isAfter(today))

        return false
    }

    private fun datesFromStartOfSubPeriod(): LocalDate {
        if (template.subPeriod == Period.WEEKLY) {
            return LocalDate.now().with(
                ChronoField.DAY_OF_WEEK,
                DayOfWeek.MONDAY.getLong(ChronoField.DAY_OF_WEEK)
            )
        } else if (template.subPeriod == Period.MONTHLY) {
            return LocalDate.now().withDayOfMonth(1)
        }

        return LocalDate.now()
    }
}
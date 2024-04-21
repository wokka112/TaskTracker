package com.floatingpanda.tasktracker.data.task

import com.floatingpanda.tasktracker.data.Day
import com.floatingpanda.tasktracker.data.Period
import java.util.Objects

// TODO make template constructor private and then provide constructors for different types like daily, etc.
//   Or do we use a builder maybe?

// TODO do we need a template creation date in here?

// TODO By default we'll start the task immediately and refresh it at the start of the next period.
//  This means that we could start a task on the last day of the week, and it'll refresh immediately
//  the next day. Should we provide an option to start the record immediately and then refresh it
//  after the days in the period have passed?
data class RepeatableTaskTemplate(
    private val details: TaskDetails,
    val repeatPeriod: Period,
    val timesPerPeriod: Int,
    val subPeriod: Period?,
    val timesPerSubPeriod: Int?,
    val eligibleDays: List<Day>
) {
    val title: String
        get() = details.title
    val info: String?
        get() = details.info;
    val category: String
        get() = details.category

    override fun equals(other: Any?): Boolean {
        if (other == null)
            return false

        if (other !is RepeatableTaskTemplate)
            return false

        return other.details == this.details
                && other.repeatPeriod == this.repeatPeriod
                && other.timesPerPeriod == this.timesPerPeriod
                && other.subPeriod == this.subPeriod
                && other.timesPerSubPeriod == this.timesPerSubPeriod
                && other.eligibleDays == this.eligibleDays
    }

    override fun hashCode(): Int {
        return Objects.hash(
            details,
            repeatPeriod,
            timesPerPeriod,
            subPeriod,
            timesPerSubPeriod,
            eligibleDays
        )
    }
}
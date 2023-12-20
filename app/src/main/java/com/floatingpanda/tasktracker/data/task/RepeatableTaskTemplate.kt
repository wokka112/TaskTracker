package com.floatingpanda.tasktracker.data.task

import com.floatingpanda.tasktracker.data.Day
import com.floatingpanda.tasktracker.data.Period

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
    val subPeriod: Period,
    val timesPerSubPeriod: Int,
    val eligibleDays: List<Day>
) {
    val title: String
        get() = details.title
    val info: String?
        get() = details.info;
    val category: String
        get() = details.category
}
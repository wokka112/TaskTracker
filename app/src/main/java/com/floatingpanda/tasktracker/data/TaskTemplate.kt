package com.floatingpanda.tasktracker.data

class TaskTemplate(
    private val title: String,
    private val info: String?,
    private val period: Period,
    private val timesPerPeriod: Int,
    private val isOnceADayOnly: Boolean
) {
}
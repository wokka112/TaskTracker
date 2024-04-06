package com.floatingpanda.tasktracker.data

import java.util.Calendar

class TaskRecord(
    private val title: String,
    private val info: String?,
    private val startDate: Long,
    private val endDate: Long,
    private val timesPerPeriod: Int,
    private var timesCompleted: Int,
    private val isOnceADayOnly: Boolean
) {
    fun isComplete(): Boolean {
        return timesCompleted >= timesPerPeriod;
    }
}
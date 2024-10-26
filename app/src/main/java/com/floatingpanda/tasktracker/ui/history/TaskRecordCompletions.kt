package com.floatingpanda.tasktracker.ui.history

import com.floatingpanda.tasktracker.data.Period
import org.mongodb.kbson.ObjectId
import java.time.LocalDate

data class TaskRecordCompletions(
    val recordId: ObjectId,
    val recordTitle: String,
    val recordPeriod: Period,
    val periodStartDate: LocalDate,
    val completions: Int,
    val totalCompletions: Int
)
package com.floatingpanda.tasktracker.ui.history

import com.floatingpanda.tasktracker.data.Period
import org.mongodb.kbson.ObjectId
import java.time.OffsetDateTime

data class IndividualRecordCompletion(
    val recordId: ObjectId,
    val recordTitle: String,
    val recordPeriod: Period,
    val completionNumber: Int,
    val completionDateTime: OffsetDateTime
)
package com.floatingpanda.tasktracker.data.task

import com.floatingpanda.tasktracker.data.Period
import io.realm.kotlin.ext.realmDictionaryOf
import io.realm.kotlin.types.RealmMap
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.BsonObjectId
import org.mongodb.kbson.ObjectId
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.ChronoField
import java.util.Objects

open class RepeatableTaskRecord(
    var template: RepeatableTaskTemplate?,
    startDate: LocalDate,
    // TODO endDate seems unnecessary, we can calculate that
    endDate: LocalDate
) : RealmObject {
    constructor() : this(
        null,
        LocalDate.now(),
        LocalDate.now()
    ) // Empty constructor required by Realm

    @PrimaryKey
    var id: ObjectId = BsonObjectId()
    var completionsPerDate: RealmMap<String, Int> = realmDictionaryOf()
    var startDateInternal: String
    var endDateInternal: String
    var startDate: LocalDate
        get() {
            return LocalDate.parse(startDateInternal)
        }
        set(date: LocalDate) {
            startDateInternal = date.toString()
        }
    var endDate: LocalDate
        get() {
            return LocalDate.parse(endDateInternal)
        }
        set(date: LocalDate) {
            endDateInternal = date.toString()
        }

    val isComplete: Boolean
        get() = if (template == null) true else completionsPerDate.size >= template!!.timesPerPeriod
    val title: String?
        get() = template?.title
    val info: String?
        get() = template?.info;
    val category: String?
        get() = template?.category


    init {
        startDateInternal = startDate.toString()
        endDateInternal = endDate.toString()
    }


    fun getTimesLeftForSubPeriod(): Int {
        if (template == null)
            return -1

        var timesLeft = template!!.timesPerSubPeriod ?: template!!.timesPerPeriod
        return timesLeft - getCompletionsForSubPeriod()
    }

    fun getCompletionsForSubPeriod(): Int {
        if (template == null)
            return -1

        return getCompletionsForPeriod(template!!.subRepeatPeriod ?: template!!.repeatPeriod)
    }

    fun isCompleteForSubPeriod(): Boolean {
        if (isComplete || template == null)
            return true

        var timesPerSubPeriod = template!!.timesPerSubPeriod
        if (timesPerSubPeriod == null)
            timesPerSubPeriod = template!!.timesPerPeriod

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

    override fun equals(other: Any?): Boolean {
        if (other == null)
            return false

        if (other !is RepeatableTaskRecord)
            return false

        return other.template == this.template
                && other.startDate == this.startDate
                && other.endDate == this.endDate
    }

    override fun hashCode(): Int {
        return Objects.hash(template, startDate, endDate)
    }
}
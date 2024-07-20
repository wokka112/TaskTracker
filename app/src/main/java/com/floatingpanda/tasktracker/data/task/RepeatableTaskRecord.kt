package com.floatingpanda.tasktracker.data.task

import com.floatingpanda.tasktracker.data.Period
import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.BsonObjectId
import org.mongodb.kbson.ObjectId
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.temporal.ChronoField
import java.util.Objects
import java.util.stream.Collectors

//TODO should we use local dates for start and end date? Or OffsetDateTime?
class RepeatableTaskRecord(
    //TODO should this just be an id pointing to the latest template? Or will the template automatically update?
    // Actually it'd be best to keep them separate so you could see what the template was at the time?
    var template: RepeatableTaskTemplate,
    startDate: LocalDate,
    // TODO endDate seems unnecessary, we can calculate that
    endDate: LocalDate
) : RealmObject {
    constructor() : this(
        RepeatableTaskTemplate(),
        LocalDate.now(),
        LocalDate.now()
    ) // Empty constructor required by Realm

    @PrimaryKey
    var id: ObjectId = BsonObjectId()
    var completionsInternal: RealmList<String> = realmListOf()
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
    var completions: List<OffsetDateTime>
        get() {
            return completionsInternal.stream().map(OffsetDateTime::parse)
                .collect(Collectors.toList())
        }
        set(completions: List<OffsetDateTime>) {
            realmListOf(
                completions.stream().map(OffsetDateTime::toString).collect(Collectors.toList())
            )
        }

    val isComplete: Boolean
        get() = completionsInternal.size >= template.timesPerPeriod
    val title: String
        get() = template.title
    val info: String?
        get() = template.info;
    val category: String
        get() = template.category
    val repeatPeriod: Period
        get() = template.repeatPeriod


    init {
        startDateInternal = startDate.toString()
        endDateInternal = endDate.toString()
    }

    fun getTimesLeftForSubPeriod(): Int {
        val timesLeft = template.maxTimesPerSubPeriod ?: template.timesPerPeriod
        return timesLeft - getCompletionsForSubPeriod()
    }

    fun getCompletionsForSubPeriod(): Int {
        return getCompletionsForPeriod(template.subRepeatPeriod ?: template.repeatPeriod)
    }

    fun isCompleteForSubPeriod(): Boolean {
        if (isComplete)
            return true

        var timesPerSubPeriod = template.maxTimesPerSubPeriod
        if (timesPerSubPeriod == null)
            timesPerSubPeriod = template.timesPerPeriod

        return getCompletionsForSubPeriod() >= timesPerSubPeriod
    }

    fun doTaskOnce() {
        completionsInternal.add(OffsetDateTime.now().toString())
    }

    private fun getCompletionsForPeriod(period: Period): Int {
        val startOfPeriod = getOffsetDateTimeAtStartOfPeriod(period)
        val now = OffsetDateTime.now()

        var completionsInPeriod = 0
        for (completion: OffsetDateTime in completions) {
            if (completion.isAfter(startOfPeriod) && completion.isBefore(now))
                completionsInPeriod++
        }

        return completionsInPeriod
    }

    private fun getOffsetDateTimeAtStartOfPeriod(period: Period): OffsetDateTime {
        when (period) {
            Period.WEEKLY -> {
                return OffsetDateTime.now()
                    .with(
                        ChronoField.DAY_OF_WEEK,
                        DayOfWeek.MONDAY.getLong(ChronoField.DAY_OF_WEEK)
                    )
                    .withHour(0).withMinute(0).withSecond(0)
            }

            Period.MONTHLY -> {
                return OffsetDateTime.now().withDayOfMonth(1)
                    .withHour(0).withMinute(0).withSecond(0)
            }

            Period.YEARLY -> {
                return OffsetDateTime.now().withDayOfYear(1).withHour(0).withMinute(0).withSecond(0)
            }

            else -> return OffsetDateTime.now().withHour(0).withMinute(0).withSecond(0)
        }

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
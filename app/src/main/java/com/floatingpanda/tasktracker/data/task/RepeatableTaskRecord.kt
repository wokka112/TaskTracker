package com.floatingpanda.tasktracker.data.task

import com.floatingpanda.tasktracker.data.Period
import com.floatingpanda.tasktracker.ui.history.IndividualRecordCompletion
import com.floatingpanda.tasktracker.ui.history.TaskRecordCompletions
import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.ext.toRealmList
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

class RepeatableTaskRecord(
    template: RepeatableTaskTemplate,
    startDate: LocalDate,
) : RealmObject {
    constructor() : this(
        RepeatableTaskTemplate(),
        LocalDate.now(),
    ) // Empty constructor required by Realm

    @PrimaryKey
    var id: ObjectId = BsonObjectId()
    var templateId: ObjectId = template.id
    var templateTitle: String = template.title
    var templateInfo: String? = template.info
    var templateCategory: String = template.category
    var targetCompletionsPerRepeatPeriod: Int = template.timesPerPeriod
    var maxCompletionsPerSubPeriod: Int? = template.maxTimesPerSubPeriod

    private var completionsInternal: RealmList<String> = realmListOf()
    private var startDateInternal: String
    private var repeatPeriodInternal: String = template.repeatPeriod.name
    private var subPeriodInternal: String? = template.subRepeatPeriod?.name
    var completions: List<OffsetDateTime>
        get() {
            return completionsInternal.stream().map(OffsetDateTime::parse)
                .collect(Collectors.toList())
        }
        set(completions: List<OffsetDateTime>) {
            completionsInternal =
                completions.stream().map(OffsetDateTime::toString).collect(Collectors.toList())
                    .toRealmList()
        }
    var startDate: LocalDate
        get() {
            return LocalDate.parse(startDateInternal)
        }
        set(date: LocalDate) {
            startDateInternal = date.toString()
        }
    var repeatPeriod: Period
        get() {
            return try {
                Period.valueOf(repeatPeriodInternal)
            } catch (e: Exception) {
                throw IllegalStateException("Period is stored internally in an unrecognised format - " + repeatPeriodInternal)
            }
        }
        set(period: Period) {
            repeatPeriodInternal = period.name
        }
    var subPeriod: Period?
        get() {
            return try {
                Period.valueOf(repeatPeriodInternal)
            } catch (e: Exception) {
                null
            }
        }
        set(period: Period?) {
            subPeriodInternal = period?.name
        }

    val endDate: LocalDate
        get() {
            val startDate = startDate
            return startDate.plus(1, repeatPeriod.convertToTemporalUnit()).minusDays(1)
        }

    val isComplete: Boolean
        get() = completionsInternal.size >= targetCompletionsPerRepeatPeriod

    // A record is active if we have not gone past its end date
    val isActive: Boolean
        get() = !LocalDate.now().isAfter(endDate)

    init {
        startDateInternal = startDate.toString()
    }

    fun convertIntoRecordCompletions(): TaskRecordCompletions {
        return TaskRecordCompletions(
            id,
            templateTitle,
            repeatPeriod,
            startDate,
            completions.size,
            targetCompletionsPerRepeatPeriod
        )
    }

    fun getIndividualRecordCompletions(): List<IndividualRecordCompletion> {
        val individualCompletions = ArrayList<IndividualRecordCompletion>()
        val recordCompletions = completions
        for (i in recordCompletions.indices)
            individualCompletions.add(
                IndividualRecordCompletion(
                    id,
                    templateTitle,
                    repeatPeriod,
                    i + 1,
                    recordCompletions[i]
                )
            )
        return individualCompletions.sortedByDescending { it.completionDateTime }
    }

    fun getTimesLeftForSubPeriod(): Int {
        val timesLeft = maxCompletionsPerSubPeriod ?: targetCompletionsPerRepeatPeriod
        return timesLeft - getCompletionsForSubPeriod()
    }

    fun getCompletionsForSubPeriod(): Int {
        return getCompletionsForPeriod(subPeriod ?: repeatPeriod)
    }

    fun isCompleteForSubPeriod(): Boolean {
        if (isComplete)
            return true

        var timesPerSubPeriod = maxCompletionsPerSubPeriod
        if (timesPerSubPeriod == null)
            timesPerSubPeriod = targetCompletionsPerRepeatPeriod

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

        return other.templateId == this.templateId
                && other.targetCompletionsPerRepeatPeriod == this.targetCompletionsPerRepeatPeriod
                && other.repeatPeriod == this.repeatPeriod
                && other.subPeriod == this.subPeriod
                && other.maxCompletionsPerSubPeriod == this.maxCompletionsPerSubPeriod
                && other.templateTitle == this.templateTitle
                && other.templateCategory == this.templateCategory
                && other.templateInfo == this.templateInfo
                && other.startDate == this.startDate
    }

    override fun hashCode(): Int {
        return Objects.hash(
            templateId,
            targetCompletionsPerRepeatPeriod,
            repeatPeriod,
            subPeriod,
            maxCompletionsPerSubPeriod,
            templateTitle,
            templateInfo,
            startDate
        )
    }
}
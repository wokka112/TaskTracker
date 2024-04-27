package com.floatingpanda.tasktracker.data.task

import com.floatingpanda.tasktracker.data.Day
import com.floatingpanda.tasktracker.data.Period
import io.realm.kotlin.ext.realmSetOf
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.RealmSet
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId
import java.util.Objects

// TODO By default we'll start the task immediately and refresh it at the start of the next period.
//  This means that we could start a task on the last day of the week, and it'll refresh immediately
//  the next day. Should we provide an option to start the record immediately and then refresh it
//  after the days in the period have passed?
open class RepeatableTaskTemplate() : RealmObject {
    @PrimaryKey
    var id: ObjectId = ObjectId()
    var title: String = ""
    var info: String? = null
    // TODO should categories be expanded to be a list? So we can have multiple categories per task?
    var category: String = ""
    private var repeatPeriodInternal: String = Period.DAILY.value
    var timesPerPeriod: Int = 1
    private var subRepeatPeriodInternal: String? = null
    var timesPerSubPeriod: Int? = null
    private var eligibleDaysInternal: RealmSet<String> = realmSetOf(
        Day.SUNDAY.day,
        Day.MONDAY.day,
        Day.TUESDAY.day,
        Day.WEDNESDAY.day,
        Day.THURSDAY.day,
        Day.FRIDAY.day,
        Day.SATURDAY.day
    )
    var repeatPeriod: Period
        get() {
            return try {
                Period.valueOf(repeatPeriodInternal)
            } catch (e: Exception) {
                Period.DAILY
            }
        }
        set(period: Period) {
            repeatPeriodInternal = period.value
        }
    var subRepeatPeriod: Period?
        get() {
            return try {
                Period.valueOf(repeatPeriodInternal)
            } catch (e: Exception) {
                null
            }
        }
        set(period: Period?) {
            subRepeatPeriodInternal = period?.value
        }
    var eligibleDays: Set<Day>
        get() {
            return eligibleDaysInternal.asSequence().map { Day.parse(it) }.filterNotNull().toSet()
        }
        set(days: Set<Day>) {
            val blip = realmSetOf<String>()
            blip.addAll(days.asSequence().map { it.day })
            eligibleDaysInternal = blip
        }

    override fun equals(other: Any?): Boolean {
        if (other == null)
            return false

        if (other !is RepeatableTaskTemplate)
            return false

        return other.id == this.id
                && other.title == this.title
                && other.info == this.info
                && other.repeatPeriod == this.repeatPeriod
                && other.timesPerPeriod == this.timesPerPeriod
                && other.subRepeatPeriod == this.subRepeatPeriod
                && other.timesPerSubPeriod == this.timesPerSubPeriod
                && other.eligibleDays == this.eligibleDays
    }

    override fun hashCode(): Int {
        return Objects.hash(
            id,
            title,
            info,
            category,
            repeatPeriodInternal,
            timesPerPeriod,
            subRepeatPeriodInternal,
            timesPerSubPeriod,
            eligibleDaysInternal
        )
    }
}
package com.floatingpanda.tasktracker.data.task

import android.util.Log
import com.floatingpanda.tasktracker.data.Period
import io.realm.kotlin.MutableRealm
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.notifications.ResultsChange
import io.realm.kotlin.query.RealmQuery
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.launch
import org.mongodb.kbson.ObjectId
import java.time.LocalDate

class RepeatableTaskTemplateRepository(private val realm: Realm) {
    fun getTemplates(): RealmQuery<RepeatableTaskTemplate> {
        return realm.query(RepeatableTaskTemplate::class)
    }

    fun findTemplate(id: ObjectId): RepeatableTaskTemplate? {
        return realm.query<RepeatableTaskTemplate>("id == $0", id).find().getOrNull(0)
    }

    fun observeTemplates(templateObserver: FlowCollector<ResultsChange<RepeatableTaskTemplate>>) {
        CoroutineScope(Dispatchers.Default).launch {
            getTemplates().asFlow().collect(templateObserver);
        }
    }

    fun writeTemplate(
        writer: MutableRealm,
        template: RepeatableTaskTemplate
    ): RepeatableTaskTemplate {
        Log.d("WriteTemplate", "Writing Template")
        return writer.copyToRealm(template)
    }

    private fun calculateEndDay(today: LocalDate, period: Period): LocalDate {
        return when (period) {
            Period.WEEKLY -> {
                val dayOfWeek = today.dayOfWeek.value
                today.plusDays((7 - (dayOfWeek - 1)).toLong())
            }

            Period.MONTHLY -> {
                today.plusMonths(1).withDayOfMonth(1)
            }

            Period.YEARLY -> {
                today.plusYears(1).withDayOfYear(1)
            }

            else -> {
                today.plusDays(1)
            }
        }
    }


    suspend fun updateTemplate(template: RepeatableTaskTemplate) {
        realm.write {
            val liveTemplate = query<RepeatableTaskTemplate>(
                RepeatableTaskTemplate::class,
                "id == $0",
                template.id
            ).find().first()
            liveTemplate.title = template.title
            liveTemplate.info = template.info
            liveTemplate.category = template.category
            liveTemplate.timesPerPeriod = template.timesPerPeriod
            liveTemplate.maxTimesPerSubPeriod = template.maxTimesPerSubPeriod
            liveTemplate.repeatPeriod = template.repeatPeriod
            liveTemplate.subRepeatPeriod = template.subRepeatPeriod
            liveTemplate.eligibleDays = template.eligibleDays
        }
    }
}
package com.floatingpanda.tasktracker.data.task

import io.realm.kotlin.Realm
import io.realm.kotlin.notifications.ResultsChange
import io.realm.kotlin.query.RealmQuery
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.launch

class RepeatableTaskTemplateRepository(private val realm: Realm) {
    fun getTemplates(): RealmQuery<RepeatableTaskTemplate> {
        return realm.query(RepeatableTaskTemplate::class)
    }

    fun observeTemplates(templateObserver: FlowCollector<ResultsChange<RepeatableTaskTemplate>>) {
        CoroutineScope(Dispatchers.Default).launch {
            getTemplates().asFlow().collect(templateObserver);
        }
    }

    suspend fun writeTemplate(template: RepeatableTaskTemplate) {
        realm.write { copyToRealm(template) }
    }

    suspend fun updateTemplate(template: RepeatableTaskTemplate) {
        realm.write {
            val liveTemplate = query<RepeatableTaskTemplate>(
                RepeatableTaskTemplate::class,
                "_id == $0",
                template.id.toString()
            ).find().first()
            liveTemplate.title = template.title
            liveTemplate.info = template.info
            liveTemplate.category = template.category
            liveTemplate.timesPerPeriod = template.timesPerPeriod
            liveTemplate.timesPerSubPeriod = template.timesPerSubPeriod
            liveTemplate.repeatPeriod = template.repeatPeriod
            liveTemplate.subRepeatPeriod = template.subRepeatPeriod
            liveTemplate.eligibleDays = template.eligibleDays
        }
    }
}
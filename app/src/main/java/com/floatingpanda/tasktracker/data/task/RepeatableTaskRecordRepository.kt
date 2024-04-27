package com.floatingpanda.tasktracker.data.task

import io.realm.kotlin.Realm
import io.realm.kotlin.notifications.ResultsChange
import io.realm.kotlin.query.RealmQuery
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.launch

class RepeatableTaskRecordRepository(private val realm: Realm) {

    fun getRecords(): RealmQuery<RepeatableTaskRecord> {
        return realm.query(RepeatableTaskRecord::class)
    }

    fun observeRecords(recordObserver: FlowCollector<ResultsChange<RepeatableTaskRecord>>) {
        CoroutineScope(Dispatchers.Default).launch {
            getRecords().asFlow().collect(recordObserver);
        }
    }

    suspend fun writeRecord(record: RepeatableTaskRecord) {
        realm.write { copyToRealm(record) }
    }

    suspend fun updateRecord(record: RepeatableTaskRecord) {
        realm.write {
            val liveRecord = query<RepeatableTaskRecord>(
                RepeatableTaskRecord::class,
                "_id == $0",
                record.id.toString()
            ).find().first()
            liveRecord.template = record.template
            liveRecord.startDate = record.startDate
            liveRecord.endDate = record.endDate
            liveRecord.completionsPerDate = record.completionsPerDate
        }
    }
}
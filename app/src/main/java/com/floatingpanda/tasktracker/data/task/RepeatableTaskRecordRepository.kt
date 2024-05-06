package com.floatingpanda.tasktracker.data.task

import android.util.Log
import io.realm.kotlin.MutableRealm
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

    fun writeRecord(writer: MutableRealm, record: RepeatableTaskRecord): RepeatableTaskRecord {
        Log.d("WriteRecord", "Writing Record")
        return writer.copyToRealm(record)
    }

    suspend fun updateRecord(record: RepeatableTaskRecord) {
        Log.d("UpdateRecord", "Updating Record")
        realm.write {
            val liveRecord = query<RepeatableTaskRecord>(
                RepeatableTaskRecord::class,
                "id == $0",
                record.id
            ).find().first()
            liveRecord.template = record.template
            liveRecord.startDate = record.startDate
            liveRecord.endDate = record.endDate
            liveRecord.completionsPerDate = record.completionsPerDate
        }
    }
}
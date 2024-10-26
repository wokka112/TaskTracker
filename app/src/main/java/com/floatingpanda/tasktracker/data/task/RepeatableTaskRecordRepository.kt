package com.floatingpanda.tasktracker.data.task

import android.util.Log
import com.floatingpanda.tasktracker.ui.history.TaskRecordCompletions
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

class RepeatableTaskRecordRepository(private val realm: Realm) {

    fun getRecords(): RealmQuery<RepeatableTaskRecord> {
        return realm.query(RepeatableTaskRecord::class)
    }

    fun findRecord(id: ObjectId): RepeatableTaskRecord? {
        return realm.query<RepeatableTaskRecord>("id == $0", id).find().getOrNull(0)
    }

    fun findRecordCompletionsForTemplate(templateId: ObjectId): List<TaskRecordCompletions> {
        return realm.query<RepeatableTaskRecord>("template.id == $0", templateId).find().map { it ->
            it.convertIntoRecordCompletions()
        }
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
            //TODO fix up updating record
            liveRecord.startDate = record.startDate
            liveRecord.endDate = record.endDate
            liveRecord.completions = record.completions
        }
    }
}
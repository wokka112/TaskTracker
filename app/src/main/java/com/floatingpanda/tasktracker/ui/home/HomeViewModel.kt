package com.floatingpanda.tasktracker.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.floatingpanda.tasktracker.data.Period
import com.floatingpanda.tasktracker.data.task.RepeatableTaskRecord
import com.floatingpanda.tasktracker.data.task.RepeatableTaskTemplate
import io.realm.kotlin.Realm
import io.realm.kotlin.notifications.ResultsChange
import io.realm.kotlin.notifications.UpdatedResults
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate

class HomeViewModel(val realm: Realm) : ViewModel() {
    private val records: MutableLiveData<List<RepeatableTaskRecord>>

    init {
        records = MutableLiveData<List<RepeatableTaskRecord>>(listOf())

        val recordsQuery = realm.query(RepeatableTaskRecord::class)

        val job = CoroutineScope(Dispatchers.Default).launch {
            val recordsFlow = recordsQuery.asFlow()
            val subscription = recordsFlow.collect { changes: ResultsChange<RepeatableTaskRecord> ->
                when (changes) {
                    is UpdatedResults -> {
                        records.postValue(changes.list)
                    }

                    else -> {}
                }
            }
        }
    }

    fun getRecords(): LiveData<List<RepeatableTaskRecord>> {
        return records
    }

    fun addRecord(template: RepeatableTaskTemplate) {
        val today = LocalDate.now()
        val newRecord =
            RepeatableTaskRecord(template, today, calculateEndDay(today, template.repeatPeriod))

        viewModelScope.launch { writeRecordToRealm(newRecord) }
    }

    fun updateRecord(record: RepeatableTaskRecord) {
        viewModelScope.launch { updateRecordToRealm(record) }
    }

    private suspend fun writeRecordToRealm(record: RepeatableTaskRecord) {
        realm.write { copyToRealm(record) }
    }

    private suspend fun updateRecordToRealm(record: RepeatableTaskRecord) {
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
}
package com.floatingpanda.tasktracker.ui.history

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.floatingpanda.tasktracker.data.task.RepeatableTaskRecord
import com.floatingpanda.tasktracker.data.task.RepeatableTaskRecordRepository
import io.realm.kotlin.notifications.InitialResults
import io.realm.kotlin.notifications.ResultsChange
import io.realm.kotlin.notifications.UpdatedResults

class CompletionHistoryViewModel(recordRepository: RepeatableTaskRecordRepository) : ViewModel() {
    private val records: MutableLiveData<List<RepeatableTaskRecord>> =
        MutableLiveData<List<RepeatableTaskRecord>>(listOf())

    init {
        recordRepository.observeRecords { changes: ResultsChange<RepeatableTaskRecord> ->
            when (changes) {
                is InitialResults -> {
                    records.postValue(changes.list)
                }

                is UpdatedResults -> {
                    records.postValue(changes.list)
                }

                else -> {}
            }
        }
    }

    private fun buildTaskRecordCompletions(records: List<RepeatableTaskRecord>): List<IndividualRecordCompletion> {
        val allCompletions = ArrayList<IndividualRecordCompletion>()
        for (record in records) {
            val recordCompletions = record.completions.sortedByDescending { it }
            for (i in 0..recordCompletions.size)
                allCompletions.add(
                    IndividualRecordCompletion(
                        record.id,
                        record.title,
                        record.repeatPeriod,
                        i + 1,
                        recordCompletions[i]
                    )
                )
        }
        allCompletions.sortByDescending { it.completionDateTime }
        return allCompletions
    }
}
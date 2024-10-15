package com.floatingpanda.tasktracker.ui.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.floatingpanda.tasktracker.MainApplication
import com.floatingpanda.tasktracker.data.task.RepeatableTaskRecord
import com.floatingpanda.tasktracker.data.task.RepeatableTaskRecordRepository
import io.realm.kotlin.notifications.InitialResults
import io.realm.kotlin.notifications.ResultsChange
import io.realm.kotlin.notifications.UpdatedResults
import org.mongodb.kbson.ObjectId

class RecordCompletionHistoryViewModel(val recordRepository: RepeatableTaskRecordRepository) :
    ViewModel() {
    private val records: MutableLiveData<List<RepeatableTaskRecord>> =
        MutableLiveData<List<RepeatableTaskRecord>>(listOf())
    private val recordCompletions: MutableLiveData<List<RecordCompletions>> =
        MutableLiveData<List<RecordCompletions>>(listOf())

    init {
        recordRepository.observeRecords { changes: ResultsChange<RepeatableTaskRecord> ->
            when (changes) {
                is InitialResults -> {
                    records.postValue(changes.list)
                    recordCompletions.postValue(buildTaskRecordCompletions(changes.list))
                }

                is UpdatedResults -> {
                    records.postValue(changes.list)
                    recordCompletions.postValue(buildTaskRecordCompletions(changes.list))
                }

                else -> {}
            }
        }
    }

    fun getRecordCompletions(): LiveData<List<RecordCompletions>> {
        return recordCompletions;
    }

    fun getRecord(recordId: ObjectId): RepeatableTaskRecord? {
        return recordRepository.findRecord(recordId)
    }

    private fun buildTaskRecordCompletions(records: List<RepeatableTaskRecord>): List<RecordCompletions> {
        val allCompletions = ArrayList<RecordCompletions>()
        for (record in records) {
            allCompletions.add(
                RecordCompletions(
                    record.id,
                    record.title,
                    record.repeatPeriod,
                    record.startDate,
                    record.completions.size,
                    record.template.timesPerPeriod
                )
            )
        }

        return allCompletions
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val appContainer =
                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MainApplication).getAppContainer()
                RecordCompletionHistoryViewModel(
                    recordRepository = appContainer.repeatableTaskRecordRepository,
                )
            }
        }
    }
}
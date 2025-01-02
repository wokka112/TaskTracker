package com.floatingpanda.tasktracker.ui.todos

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.floatingpanda.tasktracker.MainApplication
import com.floatingpanda.tasktracker.data.task.RepeatableTaskRecord
import com.floatingpanda.tasktracker.data.task.RepeatableTaskRecordRepository
import io.realm.kotlin.notifications.InitialResults
import io.realm.kotlin.notifications.ResultsChange
import io.realm.kotlin.notifications.UpdatedResults
import kotlinx.coroutines.launch

class TodoViewModel(
    private val recordRepository: RepeatableTaskRecordRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val records: MutableLiveData<List<RepeatableTaskRecord>> =
        MutableLiveData<List<RepeatableTaskRecord>>(listOf())

    init {
        recordRepository.observeRecords() { changes: ResultsChange<RepeatableTaskRecord> ->
            when (changes) {
                is InitialResults -> {
                    records.postValue(changes.list.filter { r -> r.isActive })
                }

                is UpdatedResults -> {
                    records.postValue(changes.list.filter { r -> r.isActive })
                }

                else -> {}
            }
        }
    }

    fun getRecords(): LiveData<List<RepeatableTaskRecord>> {
        return records
    }

    fun updateRecord(record: RepeatableTaskRecord) {
        viewModelScope.launch { recordRepository.updateRecord(record) }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val savedStateHandle = createSavedStateHandle()
                val appContainer =
                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MainApplication).getAppContainer()
                TodoViewModel(
                    recordRepository = appContainer.repeatableTaskRecordRepository,
                    savedStateHandle = savedStateHandle
                )
            }
        }
    }
}
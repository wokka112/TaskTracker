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
import com.floatingpanda.tasktracker.data.task.RepeatableTaskTemplate
import com.floatingpanda.tasktracker.data.task.RepeatableTaskTemplateRepository
import io.realm.kotlin.notifications.InitialResults
import io.realm.kotlin.notifications.ResultsChange
import io.realm.kotlin.notifications.UpdatedResults
import org.mongodb.kbson.ObjectId

class TaskCompletionHistoryViewModel(
    val recordRepository: RepeatableTaskRecordRepository,
    val templateRepository: RepeatableTaskTemplateRepository
) : ViewModel() {
    private val templates: MutableLiveData<List<RepeatableTaskTemplate>> =
        MutableLiveData<List<RepeatableTaskTemplate>>(listOf())
    private val taskRecordCompletions: MutableLiveData<List<TaskRecordCompletions>> =
        MutableLiveData<List<TaskRecordCompletions>>(listOf())

    init {
        recordRepository.observeRecords { changes: ResultsChange<RepeatableTaskRecord> ->
            when (changes) {
                is InitialResults -> {
                    taskRecordCompletions.postValue(buildTaskRecordCompletions(changes.list))
                }

                is UpdatedResults -> {
                    taskRecordCompletions.postValue(buildTaskRecordCompletions(changes.list))
                }

                else -> {}
            }
        }
        templateRepository.observeTemplates { changes: ResultsChange<RepeatableTaskTemplate> ->
            when (changes) {
                is InitialResults -> {
                    templates.postValue(changes.list)
                }

                is UpdatedResults -> {
                    templates.postValue(changes.list)
                }

                else -> {}
            }
        }
    }

    fun getTemplates(): LiveData<List<RepeatableTaskTemplate>> {
        return templates
    }

    fun getTaskRecordCompletions(id: String): LiveData<List<TaskRecordCompletions>> {
        if (id == ALL_TEMPLATES_ID)
            return taskRecordCompletions

        return MutableLiveData(recordRepository.findRecordCompletionsForTemplate(ObjectId.invoke(id)))
    }

    fun getRecord(recordId: ObjectId): RepeatableTaskRecord? {
        return recordRepository.findRecord(recordId)
    }

    private fun buildTaskRecordCompletions(records: List<RepeatableTaskRecord>): List<TaskRecordCompletions> {
        val allCompletions = ArrayList<TaskRecordCompletions>()
        for (record in records) {
            allCompletions.add(
                TaskRecordCompletions(
                    record.id,
                    record.templateTitle,
                    record.repeatPeriod,
                    record.startDate,
                    record.completions.size,
                    record.targetCompletionsPerRepeatPeriod
                )
            )
        }

        return allCompletions
    }

    companion object {
        val ALL_TEMPLATES_ID = "*"
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val appContainer =
                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MainApplication).getAppContainer()
                TaskCompletionHistoryViewModel(
                    recordRepository = appContainer.repeatableTaskRecordRepository,
                    templateRepository = appContainer.repeatableTaskTemplateRepository
                )
            }
        }
    }
}
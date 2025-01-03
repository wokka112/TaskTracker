package com.floatingpanda.tasktracker.ui.tasks

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
import com.floatingpanda.tasktracker.RealmHelper
import com.floatingpanda.tasktracker.data.task.RepeatableTaskRecord
import com.floatingpanda.tasktracker.data.task.RepeatableTaskRecordRepository
import com.floatingpanda.tasktracker.data.task.RepeatableTaskTemplate
import com.floatingpanda.tasktracker.data.task.RepeatableTaskTemplateRepository
import com.floatingpanda.tasktracker.ui.history.TaskRecordCompletions
import io.realm.kotlin.notifications.InitialResults
import io.realm.kotlin.notifications.ResultsChange
import io.realm.kotlin.notifications.UpdatedResults
import kotlinx.coroutines.launch
import org.mongodb.kbson.ObjectId
import java.time.LocalDate

class TaskViewModel(
    private val templateRepository: RepeatableTaskTemplateRepository,
    private val recordRepository: RepeatableTaskRecordRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val templates: MutableLiveData<List<RepeatableTaskTemplate>> =
        MutableLiveData<List<RepeatableTaskTemplate>>(listOf())

    init {
        templateRepository.observeTemplates { changes: ResultsChange<RepeatableTaskTemplate> ->
            when (changes) {
                is InitialResults -> {
                    templates.postValue(changes.list)
                }

                is UpdatedResults -> {
                    templates.postValue(changes.list)
                }
            }
        }
    }

    fun getTemplates(): LiveData<List<RepeatableTaskTemplate>> {
        return templates
    }

    fun getTemplate(id: ObjectId): RepeatableTaskTemplate? {
        return templateRepository.findTemplate(id)
    }

    fun getRecordCompletionsForTemplate(templateId: ObjectId): List<TaskRecordCompletions> {
        return recordRepository.findRecordCompletionsForTemplate(templateId)
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val savedStateHandle = createSavedStateHandle()
                val appContainer =
                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MainApplication).getAppContainer()
                TaskViewModel(
                    templateRepository = appContainer.repeatableTaskTemplateRepository,
                    recordRepository = appContainer.repeatableTaskRecordRepository,
                    savedStateHandle = savedStateHandle
                )
            }
        }
    }
}
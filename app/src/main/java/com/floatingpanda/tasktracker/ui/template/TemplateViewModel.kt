package com.floatingpanda.tasktracker.ui.template

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.floatingpanda.tasktracker.MainApplication
import com.floatingpanda.tasktracker.data.task.RepeatableTaskTemplate
import com.floatingpanda.tasktracker.data.task.RepeatableTaskTemplateRepository
import io.realm.kotlin.notifications.InitialResults
import io.realm.kotlin.notifications.ResultsChange
import io.realm.kotlin.notifications.UpdatedResults
import org.mongodb.kbson.ObjectId

class TemplateViewModel(
    private val templateRepository: RepeatableTaskTemplateRepository,
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

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val savedStateHandle = createSavedStateHandle()
                val appContainer =
                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MainApplication).getAppContainer()
                TemplateViewModel(
                    templateRepository = appContainer.repeatableTaskTemplateRepository,
                    savedStateHandle = savedStateHandle
                )
            }
        }
    }
}
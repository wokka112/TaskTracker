package com.floatingpanda.tasktracker.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.floatingpanda.tasktracker.data.Period
import com.floatingpanda.tasktracker.data.task.RepeatableTaskRecord
import com.floatingpanda.tasktracker.data.task.RepeatableTaskRecordRepository
import com.floatingpanda.tasktracker.data.task.RepeatableTaskTemplate
import io.realm.kotlin.notifications.ResultsChange
import io.realm.kotlin.notifications.UpdatedResults
import kotlinx.coroutines.launch
import java.time.LocalDate

class HomeViewModel(private val recordRepository: RepeatableTaskRecordRepository) :
    ViewModel() {
    private val records: MutableLiveData<List<RepeatableTaskRecord>>

    init {
        records = MutableLiveData<List<RepeatableTaskRecord>>(listOf())
        recordRepository.observeRecords { changes: ResultsChange<RepeatableTaskRecord> ->
            when (changes) {
                is UpdatedResults -> {
                    records.postValue(changes.list)
                }

                else -> {}
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

        viewModelScope.launch { recordRepository.writeRecord(newRecord) }
    }

    fun updateRecord(record: RepeatableTaskRecord) {
        viewModelScope.launch { recordRepository.updateRecord(record) }
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
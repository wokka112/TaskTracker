package com.floatingpanda.tasktracker.ui.creation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.floatingpanda.tasktracker.data.Day
import com.floatingpanda.tasktracker.data.Period
import com.floatingpanda.tasktracker.data.task.RepeatableTaskRecord
import com.floatingpanda.tasktracker.data.task.RepeatableTaskRecordRepository
import com.floatingpanda.tasktracker.data.task.RepeatableTaskTemplate
import com.floatingpanda.tasktracker.data.task.RepeatableTaskTemplateRepository
import kotlinx.coroutines.launch
import java.time.LocalDate

class TaskCreationViewModel(
    private val templateRepository: RepeatableTaskTemplateRepository,
    private val recordRepository: RepeatableTaskRecordRepository
) : ViewModel() {
    var title: MutableLiveData<String> = MutableLiveData("")
    var category: MutableLiveData<String> = MutableLiveData("")
    var info: MutableLiveData<String> = MutableLiveData("")
    var period: MutableLiveData<Period> = MutableLiveData(Period.DAILY)
    var timesPerPeriod: MutableLiveData<Int> = MutableLiveData(1)
    var isSubPeriodEnabled: MutableLiveData<Boolean> = MutableLiveData(false)
    var subPeriod: MutableLiveData<Period?> = MutableLiveData(null)
    var timesPerSubPeriod: MutableLiveData<Int?> = MutableLiveData(null)

    var eligibleDays: MutableLiveData<Set<Day>> = MutableLiveData(
        setOf<Day>(
            Day.MONDAY,
            Day.TUESDAY,
            Day.WEDNESDAY,
            Day.THURSDAY,
            Day.FRIDAY,
            Day.SATURDAY,
            Day.SUNDAY
        )
    )

    fun createTemplate() {
        if (title.value == null || title.value!!.isBlank())
            throw Exception("Title is null or blank")

        if (category.value == null || category.value!!.isBlank())
            throw Exception("Category is null or blank")

        if (period.value == null)
            throw Exception("Period is null")

        if (timesPerPeriod.value == null || timesPerPeriod.value!! <= 0)
            throw Exception("Times per period is null or less than or equals to 0")

        if (info.value == null)
            Log.e("CreateTemplate", "Info is null")

        if (isSubPeriodEnabled.value == null)
            Log.e("CreateTemplate", "IsSubPeriodEnabled is null")
        if (isSubPeriodEnabled.value != null && !isSubPeriodEnabled.value!!)
            Log.e("CreateTemplate", "IsSubPeriodEnabled is false")

        if (info.value == null)
            Log.e("CreateTemplate", "Info is null")

        val template = RepeatableTaskTemplate()
        template.title = title.value!!
        template.info = info.value
        template.category = category.value!!
        template.repeatPeriod = period.value!!
        template.timesPerPeriod = timesPerPeriod.value!!
        template.eligibleDays = eligibleDays.value!!

        if (isSubPeriodEnabled.value != null && isSubPeriodEnabled.value!!) {
            if (subPeriod.value == null)
                throw Exception("Sub period is enabled but sub period is null")

            if (timesPerSubPeriod.value == null && timesPerSubPeriod.value!! <= 0)
                throw Exception("Sub period is enabled but times per sub period is null or less than or equal to 0")

            template.subRepeatPeriod = subPeriod.value
            template.timesPerSubPeriod = timesPerSubPeriod.value
        }

        viewModelScope.launch {
            templateRepository.writeTemplate(template)

            //TODO Update date functionality to give actual end date based on period and current date
            //TODO Do this somewhere else??? Or maybe always create initial one with initial template? Makes sense...
            val initialRecord =
                RepeatableTaskRecord(template, LocalDate.now(), LocalDate.now().plusDays(1))
            recordRepository.writeRecord(initialRecord)
        }
    }

    fun setTitle(title: String) {
        this.title.postValue(title)
    }

    fun getTitle(): LiveData<String> {
        return title
    }

    fun setCategory(category: String) {
        this.category.postValue(category)
    }

    fun getCategory(): LiveData<String> {
        return category
    }

    fun setInfo(info: String) {
        this.info.postValue(info)
    }

    fun getInfo(): LiveData<String> {
        return info
    }

    fun setPeriod(period: Period) {
        this.period.postValue(period)
    }

    fun getPeriod(): LiveData<Period> {
        return period
    }

    fun setTimesPerPeriod(timesPerPeriod: Int) {
        this.timesPerPeriod.postValue(timesPerPeriod)
    }

    fun getTimesPerPeriod(): LiveData<Int> {
        return timesPerPeriod
    }

    fun setIsSubPeriodEnabled(enabled: Boolean) {
        this.isSubPeriodEnabled.postValue(enabled)
    }

    fun getIsSubPeriodEnabled(): LiveData<Boolean> {
        return isSubPeriodEnabled
    }

    fun setSubPeriod(subPeriod: Period?) {
        this.subPeriod.postValue(subPeriod)
    }

    fun getSubPeriod(): LiveData<Period?> {
        return subPeriod
    }

    fun setTimesPerSubPeriod(timesPerSubPeriod: Int?) {
        this.timesPerSubPeriod.postValue(timesPerSubPeriod)
    }

    fun getTimesPerSubPeriod(): LiveData<Int?> {
        return timesPerSubPeriod
    }

    fun setEligibleDays(days: Set<Day>) {
        this.eligibleDays.postValue(days)
    }

    fun getEligibleDays(): LiveData<Set<Day>> {
        return eligibleDays
    }

    fun addEligibleDay(day: Day) {
        val days: HashSet<Day> = eligibleDays.value as HashSet<Day>
        days.add(day)
        setEligibleDays(days)
    }

    fun removeEligibleDay(day: Day) {
        val days: HashSet<Day> = eligibleDays.value as HashSet<Day>
        days.remove(day)
        setEligibleDays(days)
    }
}
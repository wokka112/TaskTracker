package com.floatingpanda.tasktracker.ui.creation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.floatingpanda.tasktracker.MainApplication
import com.floatingpanda.tasktracker.RealmHelper
import com.floatingpanda.tasktracker.data.Day
import com.floatingpanda.tasktracker.data.Period
import com.floatingpanda.tasktracker.data.task.RepeatableTaskRecord
import com.floatingpanda.tasktracker.data.task.RepeatableTaskRecordRepository
import com.floatingpanda.tasktracker.data.task.RepeatableTaskTemplate
import com.floatingpanda.tasktracker.data.task.RepeatableTaskTemplateRepository
import kotlinx.coroutines.launch
import java.time.LocalDate

//TODO how to deal with tasks which are one offs rather than repeating?
//TODO how to create records when periods change? Would need a polling thing maybe...
class TaskCreationViewModel(
    private val templateRepository: RepeatableTaskTemplateRepository,
    private val recordRepository: RepeatableTaskRecordRepository,
    private val realmHelper: RealmHelper,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private var title: MutableLiveData<String> = MutableLiveData("")
    private var category: MutableLiveData<String> = MutableLiveData("")
    private var info: MutableLiveData<String> = MutableLiveData("")
    private var period: MutableLiveData<Period> = MutableLiveData(Period.DAILY)
    private var timesPerPeriod: MutableLiveData<Int> = MutableLiveData(1)
    private var isSubPeriodEnabled: MutableLiveData<Boolean> = MutableLiveData(false)
    private var subPeriod: MutableLiveData<Period> = MutableLiveData(Period.NONE)
    private var maxTimesPerSubPeriod: MutableLiveData<Int?> = MutableLiveData(null)

    private var validSubPeriods: MutableLiveData<List<Period>> =
        MutableLiveData(Period.getValidSubPeriods(Period.DAILY))
    private var eligibleDays: MutableLiveData<Set<Day>> = MutableLiveData(
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

    val validPeriods: List<Period> = Period.entries.filter { p -> p != Period.NONE }

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

            if (maxTimesPerSubPeriod.value == null && maxTimesPerSubPeriod.value!! <= 0)
                throw Exception("Sub period is enabled but times per sub period is null or less than or equal to 0")

            template.subRepeatPeriod = subPeriod.value
            template.maxTimesPerSubPeriod = maxTimesPerSubPeriod.value
        }

        viewModelScope.launch {
            realmHelper.writeTransaction {
                val savedTemplate = templateRepository.writeTemplate(this, template)
                val today = LocalDate.now()
                //TODO Do this somewhere else??? Or maybe always create initial one with initial template? Makes sense...
                val initialRecord =
                    RepeatableTaskRecord(
                        savedTemplate,
                        LocalDate.now(),
                        calculateEndDay(today, template.repeatPeriod)
                    )
                recordRepository.writeRecord(this, initialRecord)
            }
        }
        clearVariables()
    }

    private fun clearVariables() {
        title.postValue("")
        category.postValue("")
        info.postValue("")
        period.postValue(Period.DAILY)
        timesPerPeriod.postValue(0)
        isSubPeriodEnabled.postValue(false)
        subPeriod.postValue(Period.DAILY)
        maxTimesPerSubPeriod.postValue(null)
        eligibleDays.postValue(
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
        this.validSubPeriods.postValue(Period.getValidSubPeriods(period))

        if (subPeriod.value != null
            && (subPeriod.value!! == period
                    || Period.isPeriodGreaterThanOtherPeriod(subPeriod.value!!, period))
        ) {
            subPeriod.postValue(Period.NONE)
        }
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
        if (subPeriod != null && Period.isPeriodGreaterThanOtherPeriod(subPeriod, period.value!!)) {
            Log.d(
                "setSubPeriod",
                "Attempted to set sub period greater than period: " + subPeriod + " - " + period.value + ". Setting to NONE instead"
            )
            this.subPeriod.postValue(Period.NONE)
        } else {
            this.subPeriod.postValue(subPeriod)
        }
    }

    fun getSubPeriod(): LiveData<Period?> {
        return subPeriod
    }

    fun setMaxTimesPerSubPeriod(maxTimesPerSubPeriod: Int?) {
        this.maxTimesPerSubPeriod.postValue(maxTimesPerSubPeriod)
    }

    fun getMaxTimesPerSubPeriod(): LiveData<Int?> {
        return maxTimesPerSubPeriod
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

    fun getValidSubPeriods(): LiveData<List<Period>> {
        return validSubPeriods
    }

    fun hasValidTemplateDetails(): Boolean {
        val METHOD_NAME = "hasValidTemplateDetails"
        val LOG_PREFIX = "Template details invalid - "

        if (title.value.isNullOrBlank()) {
            Log.d(METHOD_NAME, LOG_PREFIX + " title is invalid")
            return false
        }

        if (category.value.isNullOrBlank()) {
            Log.d(METHOD_NAME, LOG_PREFIX + " category is invalid")
            return false
        }
        return true
    }

    fun hasValidRecordScheduleDetails(): Boolean {
        val METHOD_NAME = "hasValidRecordScheduleDetails"
        val LOG_PREFIX = "Record schedule details invalid - "

        if (isSubPeriodEnabled.value!!) {
            if (maxTimesPerSubPeriod.value == null || maxTimesPerSubPeriod.value!! <= 0 || maxTimesPerSubPeriod.value!! > timesPerPeriod.value!!) {
                Log.d(
                    METHOD_NAME,
                    LOG_PREFIX + "sub period enabled but max times per sub period is invalid"
                )
            }

            if (subPeriod.value == Period.NONE)
                Log.d(
                    METHOD_NAME,
                    LOG_PREFIX + "sub period is set to NONE which is invalid"
                )

            return false
        }

        if (timesPerPeriod.value == null || timesPerPeriod.value!! <= 0) {
            Log.d(METHOD_NAME, LOG_PREFIX + "times per period invalid")
            return false
        }

        if (eligibleDays.value.isNullOrEmpty()) {
            Log.d(METHOD_NAME, LOG_PREFIX + "eligible days invalid")
            return false
        }

        return true
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

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val savedStateHandle = createSavedStateHandle()
                val appContainer = (this[APPLICATION_KEY] as MainApplication).getAppContainer()
                TaskCreationViewModel(
                    templateRepository = appContainer.repeatableTaskTemplateRepository,
                    recordRepository = appContainer.repeatableTaskRecordRepository,
                    realmHelper = appContainer.realmHelper,
                    savedStateHandle = savedStateHandle
                )
            }
        }
    }
}
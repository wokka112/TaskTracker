package com.floatingpanda.tasktracker.ui.tasks

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
import org.mongodb.kbson.ObjectId
import java.time.LocalDate

//TODO how to deal with tasks which are one offs rather than repeating?
class TaskUpsertViewModel(
    private val templateRepository: RepeatableTaskTemplateRepository,
    private val recordRepository: RepeatableTaskRecordRepository,
    private val realmHelper: RealmHelper,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private var title: MutableLiveData<String> = MutableLiveData("")
    private var category: MutableLiveData<String> = MutableLiveData("")
    private var info: MutableLiveData<String> = MutableLiveData("")
    private var repeatPeriod: MutableLiveData<Period> = MutableLiveData(Period.DAILY)
    private var timesPerPeriod: MutableLiveData<Int> = MutableLiveData(0)
    private var isSubPeriodEnabled: MutableLiveData<Boolean> = MutableLiveData(false)
    private var subRepeatPeriod: MutableLiveData<Period> = MutableLiveData(Period.NONE)
    private var maxTimesPerSubPeriod: MutableLiveData<Int?> = MutableLiveData(null)

    private var validSubPeriods: MutableLiveData<List<Period>> =
        MutableLiveData(Period.getValidSubPeriodsWithoutNone(Period.DAILY))
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

    fun populateTaskIfTaskNotPresent(id: ObjectId) {
        if (isTaskPresent())
            return

        val template = templateRepository.findTemplate(id)
        if (template != null) {
            title.postValue(template.title)
            category.postValue(template.category)
            info.postValue(template.info)
            repeatPeriod.postValue(template.repeatPeriod)
            timesPerPeriod.postValue(template.timesPerPeriod)
            isSubPeriodEnabled.postValue(template.subRepeatPeriod != null && template.subRepeatPeriod != Period.NONE)
            subRepeatPeriod.postValue(template.subRepeatPeriod)
            maxTimesPerSubPeriod.postValue(template.maxTimesPerSubPeriod)
            eligibleDays.postValue(template.eligibleDays)
        }
    }

    fun isTaskPresent(): Boolean {
        return !title.value.isNullOrBlank()
                && !category.value.isNullOrBlank()
                && !info.value.isNullOrBlank()
    }

    fun createTask() {
        if (title.value == null || title.value!!.isBlank())
            throw Exception("Title is null or blank")

        if (category.value == null || category.value!!.isBlank())
            throw Exception("Category is null or blank")

        if (repeatPeriod.value == null)
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
        template.repeatPeriod = repeatPeriod.value!!
        template.timesPerPeriod = timesPerPeriod.value!!
        template.eligibleDays = eligibleDays.value!!

        if (isSubPeriodEnabled.value != null && isSubPeriodEnabled.value!!) {
            if (subRepeatPeriod.value == null)
                throw Exception("Sub period is enabled but sub period is null")

            if (maxTimesPerSubPeriod.value == null && maxTimesPerSubPeriod.value!! <= 0)
                throw Exception("Sub period is enabled but times per sub period is null or less than or equal to 0")

            template.subRepeatPeriod = subRepeatPeriod.value
            template.maxTimesPerSubPeriod = maxTimesPerSubPeriod.value
        }

        viewModelScope.launch {
            realmHelper.writeTransaction {
                val savedTemplate = templateRepository.writeTemplate(this, template)
                //TODO grab latest record if it exists and is active before creating this one to account for updating a template
                val initialRecord =
                    RepeatableTaskRecord(
                        savedTemplate,
                        LocalDate.now(),
                    )
                recordRepository.writeRecord(this, initialRecord)
            }
        }
        clear()
    }

    fun clear() {
        title.postValue("")
        category.postValue("")
        info.postValue("")
        repeatPeriod.postValue(Period.DAILY)
        timesPerPeriod.postValue(0)
        isSubPeriodEnabled.postValue(false)
        subRepeatPeriod.postValue(Period.DAILY)
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
        this.repeatPeriod.postValue(period)

        this.validSubPeriods.postValue(Period.getValidSubPeriodsWithoutNone(period))

        if (subRepeatPeriod.value != null
            && (subRepeatPeriod.value!! == period
                    || Period.isPeriodGreaterThanOtherPeriod(subRepeatPeriod.value!!, period))
        )
            subRepeatPeriod.postValue(Period.DAILY)
    }

    fun getPeriod(): LiveData<Period> {
        return repeatPeriod
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
        if (subPeriod != null && Period.isPeriodGreaterThanOtherPeriod(
                subPeriod,
                repeatPeriod.value!!
            )
        ) {
            this.subRepeatPeriod.postValue(Period.NONE)
        } else {
            this.subRepeatPeriod.postValue(subPeriod)
        }
    }

    fun getSubPeriod(): LiveData<Period?> {
        return subRepeatPeriod
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

    /**
     * Gets all the valid periods - i.e. all except for NONE.
     */
    fun getValidPeriods(): List<Period> {
        return Period.getPeriodsWithoutNone()
    }

    /**
     * Get's the valid sub periods, which are all periods that are shorter than the current selected
     * period except for NONE.
     */
    fun getValidSubPeriods(): LiveData<List<Period>> {
        return validSubPeriods
    }

    fun hasValidTemplateDetails(): Boolean {
        val METHOD_NAME = "hasValidTemplateDetails"
        val LOG_PREFIX = "Template details invalid - "
        Log.d(METHOD_NAME, "Checking template details are valid")

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

            if (subRepeatPeriod.value == Period.NONE)
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

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val savedStateHandle = createSavedStateHandle()
                val appContainer = (this[APPLICATION_KEY] as MainApplication).getAppContainer()
                TaskUpsertViewModel(
                    templateRepository = appContainer.repeatableTaskTemplateRepository,
                    recordRepository = appContainer.repeatableTaskRecordRepository,
                    realmHelper = appContainer.realmHelper,
                    savedStateHandle = savedStateHandle
                )
            }
        }
    }
}
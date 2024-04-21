package com.floatingpanda.tasktracker.ui.creation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.floatingpanda.tasktracker.data.Day
import com.floatingpanda.tasktracker.data.Period

class TaskCreationViewModel : ViewModel() {
    var title: MutableLiveData<String> = MutableLiveData("")
    var category: MutableLiveData<String> = MutableLiveData("")
    var info: MutableLiveData<String> = MutableLiveData("")
    var period: MutableLiveData<Period> = MutableLiveData(Period.DAILY)
    var timesPerPeriod: MutableLiveData<Int> = MutableLiveData(1)
    var isSubPeriodEnabled: MutableLiveData<Boolean> = MutableLiveData(false)
    var subPeriod: MutableLiveData<Period?> = MutableLiveData(null)
    var timesPerSubPeriod: MutableLiveData<Int?> = MutableLiveData(null)

    //TODO switch to set???
    var eligibleDays: MutableLiveData<List<Day>> = MutableLiveData(
        listOf<Day>(
            Day.MONDAY,
            Day.TUESDAY,
            Day.WEDNESDAY,
            Day.THURSDAY,
            Day.FRIDAY,
            Day.SATURDAY,
            Day.SUNDAY
        )
    )

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

    fun setEligibleDays(days: List<Day>) {
        this.eligibleDays.postValue(days)
    }

    fun getEligibleDays(): LiveData<List<Day>> {
        return eligibleDays
    }

    fun addEligibleDay(day: Day) {
        // TODO implement
    }

    fun removeEligibleDay(day: Day) {
        // TODO implement
    }
}
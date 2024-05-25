package com.floatingpanda.tasktracker

import android.app.Application
import android.util.Log
import com.floatingpanda.tasktracker.data.Day
import com.floatingpanda.tasktracker.data.Period
import com.floatingpanda.tasktracker.data.task.RepeatableTaskRecord
import com.floatingpanda.tasktracker.data.task.RepeatableTaskTemplate
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.UpdatePolicy
import java.time.LocalDate

class MainApplication : Application() {
    private lateinit var appContainer: AppContainer

    override fun onCreate() {
        super.onCreate()
        Log.d("onCreate", "Creating main application")

        val templates = createTestTemplates()
        val records = createTestRecords(templates)

        val realmConfig = RealmConfiguration.Builder(
            schema = setOf(
                RepeatableTaskTemplate::class,
                RepeatableTaskRecord::class
            )
        ).inMemory()
            .initialData {
                templates.forEach { copyToRealm(it, UpdatePolicy.ALL) }
                records.forEach { copyToRealm(it, UpdatePolicy.ALL) }
            }.build()

        val realm: Realm = Realm.open(realmConfig)
        appContainer = AppContainer(realm)
    }

    fun getAppContainer(): AppContainer {
        return appContainer
    }

    private fun createTestRecords(templates: List<RepeatableTaskTemplate>): List<RepeatableTaskRecord> {
        val today = LocalDate.now()
        val tomorrow = today.plusDays(1)
        val dayOfWeek = today.dayOfWeek.value
        val nextWeek = today.plusDays((7 - (dayOfWeek - 1)).toLong())
        val nextMonth = today.plusMonths(1).withDayOfMonth(1)
        val nextYear = today.plusYears(1).withDayOfYear(1)

        val wakeUpTemplate =
            templates.stream().filter { it.title.equals("Wake up") }.findAny().get()
        val stretchTemplate =
            templates.stream().filter { it.title.equals("Stretch") }.findAny().get()
        val ocdGroupTemplate =
            templates.stream().filter { it.title.equals("Attend OCD group") }.findAny().get()
        val runTemplate = templates.stream().filter { it.title.equals("Run") }.findAny().get()
        val readTemplate = templates.stream().filter { it.title.equals("Read") }.findAny().get()
        val walkTemplate = templates.stream().filter { it.title.equals("Walk") }.findAny().get()
        val exploreWildernessTemplate =
            templates.stream().filter { it.title.equals("Explore the wilderness") }.findAny().get()
        val rosieTemplate =
            templates.stream().filter { it.title.equals("Do stuff with Rosie") }.findAny().get()
        val holidayTemplate =
            templates.stream().filter { it.title.equals("Go on holiday") }.findAny().get()
        val breakFromWorkTemplate =
            templates.stream().filter { it.title.equals("Take a break from work") }.findAny().get()

        val wakeUpRecord = RepeatableTaskRecord(wakeUpTemplate, today, tomorrow)
        val stretchRecord = RepeatableTaskRecord(stretchTemplate, today, tomorrow)
        val ocdGroupRecord = RepeatableTaskRecord(ocdGroupTemplate, today, nextWeek)
        val runRecord = RepeatableTaskRecord(runTemplate, today, nextWeek)
        val readRecord = RepeatableTaskRecord(readTemplate, today, nextWeek)
        val walkRecord = RepeatableTaskRecord(walkTemplate, today, nextWeek)
        val exploreWildernessRecord = RepeatableTaskRecord(
            exploreWildernessTemplate,
            today,
            nextMonth
        )
        val rosieRecord = RepeatableTaskRecord(rosieTemplate, today, nextMonth)
        val holidayRecord = RepeatableTaskRecord(holidayTemplate, today, nextYear)
        val breakFromWorkRecord = RepeatableTaskRecord(breakFromWorkTemplate, today, nextYear)

        return listOf(
            wakeUpRecord,
            stretchRecord,
            ocdGroupRecord,
            runRecord,
            readRecord,
            walkRecord,
            exploreWildernessRecord,
            rosieRecord,
            holidayRecord,
            breakFromWorkRecord
        )
    }

    private fun createTestTemplates(): List<RepeatableTaskTemplate> {
        val days = setOf<Day>(
            Day.MONDAY,
            Day.TUESDAY,
            Day.WEDNESDAY,
            Day.THURSDAY,
            Day.FRIDAY,
            Day.SATURDAY,
            Day.SUNDAY
        )

        val weekdays = setOf<Day>(
            Day.MONDAY,
            Day.TUESDAY,
            Day.WEDNESDAY,
            Day.THURSDAY,
            Day.FRIDAY
        )

        // a daily record,
        val wakeUpTemplate = RepeatableTaskTemplate()
        wakeUpTemplate.title = "Wake up"
        wakeUpTemplate.category = "None"
        wakeUpTemplate.repeatPeriod = Period.DAILY
        wakeUpTemplate.timesPerPeriod = 1
        wakeUpTemplate.eligibleDays = days

        // a daily record with 3 times in period,
        val stretchTemplate = RepeatableTaskTemplate()
        stretchTemplate.title = "Stretch"
        stretchTemplate.category = "Health"
        stretchTemplate.repeatPeriod = Period.DAILY
        stretchTemplate.timesPerPeriod = 3
        stretchTemplate.eligibleDays = days

        // a weekly record with once per period,
        val ocdGroupTemplate = RepeatableTaskTemplate()
        ocdGroupTemplate.title = "Attend OCD group"
        ocdGroupTemplate.category = "Health"
        ocdGroupTemplate.repeatPeriod = Period.WEEKLY
        ocdGroupTemplate.timesPerPeriod = 1
        ocdGroupTemplate.eligibleDays = days

        // a weekly record with 3 times in period and only once daily, but no weekends
        val runTemplate = RepeatableTaskTemplate()
        runTemplate.title = "Run"
        runTemplate.category = "Health"
        runTemplate.repeatPeriod = Period.WEEKLY
        runTemplate.timesPerPeriod = 3
        runTemplate.subRepeatPeriod = Period.DAILY
        runTemplate.maxTimesPerSubPeriod = 1
        runTemplate.eligibleDays = weekdays

        // a weekly record with 3 times in period and allowing twice daily,
        val readTemplate = RepeatableTaskTemplate()
        readTemplate.title = "Read"
        readTemplate.category = "Leisure"
        readTemplate.repeatPeriod = Period.WEEKLY
        readTemplate.timesPerPeriod = 3
        readTemplate.subRepeatPeriod = Period.DAILY
        readTemplate.maxTimesPerSubPeriod = 2
        readTemplate.eligibleDays = days

        // a weekly record with 3 times in period and allowing unlimited times daily
        val walkTemplate = RepeatableTaskTemplate()
        walkTemplate.title = "Walk"
        walkTemplate.category = "Leisure"
        walkTemplate.repeatPeriod = Period.WEEKLY
        walkTemplate.timesPerPeriod = 3
        walkTemplate.eligibleDays = days

        // a monthly record,
        val exploreWildernessTemplate = RepeatableTaskTemplate()
        exploreWildernessTemplate.title = "Explore the wilderness"
        exploreWildernessTemplate.category = "Leisure"
        exploreWildernessTemplate.repeatPeriod = Period.MONTHLY
        exploreWildernessTemplate.timesPerPeriod = 2
        exploreWildernessTemplate.eligibleDays = days

        // a monthly record allowing 2 times per week
        val rosieTemplate = RepeatableTaskTemplate()
        rosieTemplate.title = "Do stuff with Rosie"
        rosieTemplate.category = "Leisure"
        rosieTemplate.repeatPeriod = Period.MONTHLY
        rosieTemplate.timesPerPeriod = 4
        rosieTemplate.subRepeatPeriod = Period.WEEKLY
        rosieTemplate.maxTimesPerSubPeriod = 2
        rosieTemplate.eligibleDays = days

        // a yearly record
        val holidayTemplate = RepeatableTaskTemplate()
        holidayTemplate.title = "Go on holiday"
        holidayTemplate.category = "Leisure"
        holidayTemplate.repeatPeriod = Period.YEARLY
        holidayTemplate.timesPerPeriod = 4
        holidayTemplate.eligibleDays = days

        // A yearly record allowing 2 times per month,
        val breakFromWorkTemplate = RepeatableTaskTemplate()
        breakFromWorkTemplate.title = "Take a break from work"
        breakFromWorkTemplate.category = "Leisure"
        breakFromWorkTemplate.repeatPeriod = Period.YEARLY
        breakFromWorkTemplate.timesPerPeriod = 6
        breakFromWorkTemplate.subRepeatPeriod = Period.MONTHLY
        breakFromWorkTemplate.maxTimesPerSubPeriod = 2
        breakFromWorkTemplate.eligibleDays = days

        return listOf(
            wakeUpTemplate,
            stretchTemplate,
            ocdGroupTemplate,
            runTemplate,
            readTemplate,
            walkTemplate,
            exploreWildernessTemplate,
            rosieTemplate,
            holidayTemplate,
            breakFromWorkTemplate
        )
    }
}
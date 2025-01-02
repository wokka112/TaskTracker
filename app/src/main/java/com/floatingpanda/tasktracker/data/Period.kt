package com.floatingpanda.tasktracker.data

import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalUnit

enum class Period(val value: String) {
    NONE("None"),
    DAILY("Daily"),
    WEEKLY("Weekly"),
    MONTHLY("Monthly"),
    YEARLY("Yearly");

    fun convertToTemporalUnit(): TemporalUnit {
        if (this == DAILY)
            return ChronoUnit.DAYS

        if (this == WEEKLY)
            return ChronoUnit.WEEKS

        if (this == MONTHLY)
            return ChronoUnit.MONTHS

        if (this == YEARLY)
            return ChronoUnit.YEARS

        return ChronoUnit.FOREVER
    }

    companion object {
        fun getValidSubPeriods(period: Period): List<Period> {
            return entries.filter { entry -> entry.ordinal < period.ordinal }
        }

        fun getValidSubPeriodsWithoutNone(period: Period): List<Period> {
            return getValidSubPeriods(period).filter { p -> p != NONE }
        }

        fun getPeriodsWithoutNone(): List<Period> {
            return Period.entries.filter { p -> p != NONE }
        }

        fun isPeriodGreaterThanOtherPeriod(period: Period, otherPeriod: Period): Boolean {
            return period.ordinal > otherPeriod.ordinal
        }

        fun convertFromString(str: String?): Period {
            if (YEARLY.toString().uppercase() == str?.uppercase())
                return YEARLY

            if (MONTHLY.toString().uppercase() == str?.uppercase())
                return MONTHLY

            if (WEEKLY.toString().uppercase() == str?.uppercase())
                return WEEKLY

            if (DAILY.toString().uppercase() == str?.uppercase())
                return DAILY

            return NONE
        }
    }
}
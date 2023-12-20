package com.floatingpanda.tasktracker.data

import java.util.Calendar

// TODO expand this to include more periods? What about fortnightly? Quarterly? etc.?
//   Are quarterly and yearly overkill for now?
enum class Period() {
    DAILY,
    WEEKLY,
    MONTHLY,
    YEARLY;

    fun convertToCalendarKey(): Int {
        if (this == DAILY)
            return Calendar.DAY_OF_WEEK

        if (this == WEEKLY)
            return Calendar.WEEK_OF_YEAR

        if (this == MONTHLY)
            return Calendar.MONTH

        if (this == YEARLY)
            return Calendar.YEAR

        return -1
    }
}
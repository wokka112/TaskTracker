package com.floatingpanda.tasktracker.data

enum class Day(val day: String) {
    MONDAY("Monday"),
    TUESDAY("Tuesday"),
    WEDNESDAY("Wednesday"),
    THURSDAY("Thursday"),
    FRIDAY("Friday"),
    SATURDAY("Saturday"),
    SUNDAY("Sunday");

    companion object {
        fun parse(value: String): Day? {
            return entries.asSequence().filter { it.day == value }.firstOrNull()
        }
    }
}

package com.floatingpanda.tasktracker.data.old

// TODO should categories be expanded to be a list? So we can have multiple categories per task?
data class OldTaskDetails(
    val title: String,
    val info: String?,
    val category: String,
)
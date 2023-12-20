package com.floatingpanda.tasktracker.data.task

// TODO should categories be expanded to be a list? So we can have multiple categories per task?
data class TaskDetails(
    val title: String,
    val info: String?,
    val category: String,
)
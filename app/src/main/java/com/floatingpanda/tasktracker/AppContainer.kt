package com.floatingpanda.tasktracker

import com.floatingpanda.tasktracker.data.task.RepeatableTaskRecordRepository
import com.floatingpanda.tasktracker.data.task.RepeatableTaskTemplateRepository
import io.realm.kotlin.Realm

class AppContainer(realm: Realm) {
    val repeatableTaskRecordRepository: RepeatableTaskRecordRepository =
        RepeatableTaskRecordRepository(realm)
    val repeatableTaskTemplateRepository: RepeatableTaskTemplateRepository =
        RepeatableTaskTemplateRepository(realm)
}
package com.floatingpanda.tasktracker.command

import com.floatingpanda.tasktracker.RealmHelper
import com.floatingpanda.tasktracker.data.task.RepeatableTaskRecord
import com.floatingpanda.tasktracker.data.task.RepeatableTaskRecordRepository
import com.floatingpanda.tasktracker.data.task.RepeatableTaskTemplate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.LocalDate

class CreateNewRecordFromTemplateCommand(
    val realmScope: CoroutineScope,
    val realmHelper: RealmHelper,
    val recordRepository: RepeatableTaskRecordRepository
) {
    fun execute(template: RepeatableTaskTemplate) {
        realmScope.launch {
            realmHelper.writeTransaction {
                val newRecord =
                    RepeatableTaskRecord(
                        template,
                        LocalDate.now(),
                    )
                recordRepository.writeRecord(this, newRecord)
            }
        }
    }
}
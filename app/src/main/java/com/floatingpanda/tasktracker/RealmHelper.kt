package com.floatingpanda.tasktracker

import io.realm.kotlin.MutableRealm
import io.realm.kotlin.Realm

class RealmHelper(val realm: Realm) {
    suspend fun <R> writeTransaction(block: MutableRealm.() -> R): R {
        return realm.write(block)
    }
}
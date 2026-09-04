package com.example.calculist

import android.app.Application
import com.example.calculist.data.AppDatabase
import com.example.calculist.data.CalcRepository

/**
 * Manual dependency container. In a larger app this would be Hilt/Koin,
 * but for two screens a simple Application-scoped container is cleanest.
 */
class CalcuListApp : Application() {

    val repository: CalcRepository by lazy {
        val db = AppDatabase.get(this)
        CalcRepository(db.listDao(), db.itemDao())
    }
}

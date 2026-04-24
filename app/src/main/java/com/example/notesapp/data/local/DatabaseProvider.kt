package com.example.notesapp.data.local

import android.content.Context
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.example.notesapp.db.NotesDatabase

object DatabaseProvider {
    @Volatile
    private var instance: NotesDatabase? = null

    fun getDatabase(context: Context): NotesDatabase {
        return instance ?: synchronized(this) {
            instance ?: NotesDatabase(
                driver = AndroidSqliteDriver(
                    schema = NotesDatabase.Schema,
                    context = context,
                    name = "notes.db"
                )
            ).also { instance = it }
        }
    }
}
package com.example.notesapp.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.example.notesapp.db.Note
import com.example.notesapp.db.NotesDatabase
import com.example.notesapp.model.SortOrder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class NotesRepository(
    private val database: NotesDatabase
) {
    private val queries = database.noteQueries

    fun getNotes(sortOrder: SortOrder): Flow<List<Note>> {
        return when (sortOrder) {
            SortOrder.NEWEST -> queries.selectAllByNewest()
            SortOrder.OLDEST -> queries.selectAllByOldest()
            SortOrder.TITLE -> queries.selectAllByTitle()
        }.asFlow().mapToList(Dispatchers.IO)
    }

    fun searchNotes(query: String, sortOrder: SortOrder): Flow<List<Note>> {
        val keyword = "%$query%"
        return when (sortOrder) {
            SortOrder.NEWEST -> queries.searchByNewest(keyword, keyword)
            SortOrder.OLDEST -> queries.searchByOldest(keyword, keyword)
            SortOrder.TITLE -> queries.searchByTitle(keyword, keyword)
        }.asFlow().mapToList(Dispatchers.IO)
    }

    suspend fun getNoteById(id: Long): Note? = withContext(Dispatchers.IO) {
        queries.selectById(id).executeAsOneOrNull()
    }

    suspend fun addNote(title: String, content: String) = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        queries.insertNote(
            title = title,
            content = content,
            created_at = now,
            updated_at = now
        )
    }

    suspend fun updateNote(id: Long, title: String, content: String) = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        queries.updateNote(
            title = title,
            content = content,
            updated_at = now,
            id = id
        )
    }

    suspend fun deleteNote(id: Long) = withContext(Dispatchers.IO) {
        queries.deleteNote(id)
    }
}
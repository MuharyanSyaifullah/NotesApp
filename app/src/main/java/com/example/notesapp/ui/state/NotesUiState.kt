package com.example.notesapp.ui.state

import com.example.notesapp.db.Note

sealed class NotesUiState {
    data object Loading : NotesUiState()
    data object Empty : NotesUiState()
    data class Content(val notes: List<Note>) : NotesUiState()
}
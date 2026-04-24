package com.example.notesapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.notesapp.data.repository.NotesRepository
import com.example.notesapp.data.repository.SettingsRepository
import com.example.notesapp.db.Note
import com.example.notesapp.model.SortOrder
import com.example.notesapp.model.ThemeMode
import com.example.notesapp.ui.state.NotesUiState
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class NotesViewModel(
    private val notesRepository: NotesRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val searchQuery = MutableStateFlow("")
    private val isLoading = MutableStateFlow(true)

    val themeMode: StateFlow<ThemeMode> =
        settingsRepository.themeModeFlow.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            ThemeMode.SYSTEM
        )

    val sortOrder: StateFlow<SortOrder> =
        settingsRepository.sortOrderFlow.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            SortOrder.NEWEST
        )

    private val notesFlow: Flow<List<Note>> =
        combine(searchQuery, sortOrder) { query, sort -> query to sort }
            .flatMapLatest { (query, sort) ->
                if (query.isBlank()) {
                    notesRepository.getNotes(sort)
                } else {
                    notesRepository.searchNotes(query, sort)
                }
            }

    val uiState: StateFlow<NotesUiState> =
        combine(isLoading, notesFlow) { loading, notes ->
            when {
                loading -> NotesUiState.Loading
                notes.isEmpty() -> NotesUiState.Empty
                else -> NotesUiState.Content(notes)
            }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            NotesUiState.Loading
        )

    init {
        viewModelScope.launch {
            kotlinx.coroutines.delay(400)
            isLoading.value = false
        }
    }

    fun onSearchQueryChange(query: String) {
        searchQuery.value = query
    }

    fun addNote(title: String, content: String) {
        viewModelScope.launch {
            notesRepository.addNote(title, content)
        }
    }

    fun updateNote(id: Long, title: String, content: String) {
        viewModelScope.launch {
            notesRepository.updateNote(id, title, content)
        }
    }

    fun deleteNote(id: Long) {
        viewModelScope.launch {
            notesRepository.deleteNote(id)
        }
    }

    suspend fun getNoteById(id: Long): Note? {
        return notesRepository.getNoteById(id)
    }

    fun setThemeMode(themeMode: ThemeMode) {
        viewModelScope.launch {
            settingsRepository.setThemeMode(themeMode)
        }
    }

    fun setSortOrder(sortOrder: SortOrder) {
        viewModelScope.launch {
            settingsRepository.setSortOrder(sortOrder)
        }
    }
}

class NotesViewModelFactory(
    private val notesRepository: NotesRepository,
    private val settingsRepository: SettingsRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return NotesViewModel(notesRepository, settingsRepository) as T
    }
}
package com.example.notesapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.notesapp.data.local.DatabaseProvider
import com.example.notesapp.data.repository.NotesRepository
import com.example.notesapp.data.repository.SettingsRepository
import com.example.notesapp.model.ThemeMode
import com.example.notesapp.ui.NotesApp
import com.example.notesapp.viewmodel.NotesViewModel
import com.example.notesapp.viewmodel.NotesViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = DatabaseProvider.getDatabase(applicationContext)
        val notesRepository = NotesRepository(database)
        val settingsRepository = SettingsRepository(applicationContext)

        setContent {
            val vm: NotesViewModel = viewModel(
                factory = NotesViewModelFactory(notesRepository, settingsRepository)
            )

            val themeMode by vm.themeMode.collectAsState()

            val darkTheme = when (themeMode) {
                ThemeMode.SYSTEM -> androidx.compose.foundation.isSystemInDarkTheme()
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
            }

            NotesApp(
                viewModel = vm,
                darkTheme = darkTheme
            )
        }
    }
}
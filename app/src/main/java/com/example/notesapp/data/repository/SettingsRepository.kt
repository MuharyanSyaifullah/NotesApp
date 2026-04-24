package com.example.notesapp.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.notesapp.data.settings.SettingsKeys
import com.example.notesapp.model.SortOrder
import com.example.notesapp.model.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "notes_settings")

class SettingsRepository(private val context: Context) {

    private val themeKey = stringPreferencesKey(SettingsKeys.THEME_MODE)
    private val sortKey = stringPreferencesKey(SettingsKeys.SORT_ORDER)

    val themeModeFlow: Flow<ThemeMode> = context.dataStore.data.map { prefs ->
        ThemeMode.valueOf(prefs[themeKey] ?: ThemeMode.SYSTEM.name)
    }

    val sortOrderFlow: Flow<SortOrder> = context.dataStore.data.map { prefs ->
        SortOrder.valueOf(prefs[sortKey] ?: SortOrder.NEWEST.name)
    }

    suspend fun setThemeMode(themeMode: ThemeMode) {
        context.dataStore.edit { prefs ->
            prefs[themeKey] = themeMode.name
        }
    }

    suspend fun setSortOrder(sortOrder: SortOrder) {
        context.dataStore.edit { prefs ->
            prefs[sortKey] = sortOrder.name
        }
    }
}
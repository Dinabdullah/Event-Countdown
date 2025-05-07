package com.example.eventcountdown.presentation.theme

import android.content.Context
import androidx.compose.runtime.Stable
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "settings")

class SettingsRepository(private val context: Context) {

    companion object {
        private val THEME_KEY = stringPreferencesKey("theme_preference")
        private val LANGUAGE_KEY = stringPreferencesKey("language_preference")
    }

    val themePreferenceFlow: Flow<ThemePreference> = context.dataStore.data
        .map { preferences ->
            when (preferences[THEME_KEY]) {
                "LIGHT" -> ThemePreference.LIGHT
                "DARK" -> ThemePreference.DARK
                else -> ThemePreference.SYSTEM
            }
        }

    val languagePreferenceFlow: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[LANGUAGE_KEY] ?: "English"
        }

    suspend fun updateTheme(preference: ThemePreference) {
        context.dataStore.edit { preferences ->
            preferences[THEME_KEY] = preference.name
        }
    }

    suspend fun updateLanguage(language: String) {
        context.dataStore.edit { preferences ->
            preferences[LANGUAGE_KEY] = language
        }
    }
}

@Stable
data class AppSettings(
    val themePreference: ThemePreference,
    val languagePreference: String,
    val updateTheme: (ThemePreference) -> Unit,
    val updateLanguage: (String) -> Unit
)


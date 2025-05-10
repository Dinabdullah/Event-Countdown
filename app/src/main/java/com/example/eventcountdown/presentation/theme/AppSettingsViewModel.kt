package com.example.eventcountdown.presentation.theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AppSettingsViewModel(
    private val repository: SettingsRepository
) : ViewModel() {
    val themePreference = repository.themePreferenceFlow.stateIn(
        viewModelScope, SharingStarted.Eagerly, ThemePreference.SYSTEM
    )

    val languagePreference = repository.languagePreferenceFlow.stateIn(
        viewModelScope, SharingStarted.Eagerly, "English"
    )


    fun updateTheme(theme: ThemePreference) {
        viewModelScope.launch {
            repository.updateTheme(theme)
        }
    }

    fun updateLanguage(language: String) {
        viewModelScope.launch {
            repository.updateLanguage(language)
        }
    }


}
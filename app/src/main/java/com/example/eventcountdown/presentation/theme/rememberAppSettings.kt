package com.example.eventcountdown.presentation.theme


import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted

@Composable
fun rememberAppSettings(repository: SettingsRepository): AppSettings {
    val factory = remember { AppSettingsViewModelFactory(repository) }
    val viewModel: AppSettingsViewModel = viewModel(factory = factory)

    val theme by viewModel.themePreference.collectAsState()
    val language by viewModel.languagePreference.collectAsState()

    return remember(theme, language) {
        AppSettings(
            themePreference = theme,
            languagePreference = language,
            updateTheme = viewModel::updateTheme,
            updateLanguage = viewModel::updateLanguage
        )
    }
}

package com.example.eventcountdown.presentation.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.SettingsBrightness
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.eventcountdown.presentation.theme.AppSettings
import com.example.eventcountdown.presentation.theme.ThemePreference

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    settings: AppSettings
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }
            )
        }
    )  { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                "Theme",
                style = MaterialTheme.typography.titleLarge
            )

            ThemeOptions(
                current = settings.themePreference,
                onSelect = settings.updateTheme
            )

            Divider()

            Text(
                "Language",
                style = MaterialTheme.typography.titleLarge
            )

            LanguageOptions(
                current = settings.languagePreference,
                onSelect = settings.updateLanguage
            )
        }
    }
}

@Composable
private fun ThemeOptions(
    current: ThemePreference,
    onSelect: (ThemePreference) -> Unit
) {
    val options = listOf(
        ThemeOption("Light", Icons.Default.LightMode, ThemePreference.LIGHT),
        ThemeOption("Dark", Icons.Default.DarkMode, ThemePreference.DARK),
        ThemeOption("System", Icons.Default.SettingsBrightness, ThemePreference.SYSTEM)
    )

    options.forEach { option ->
        Row(
            Modifier
                .fillMaxWidth()
                .selectable(
                    selected = current == option.preference,
                    onClick = { onSelect(option.preference) }
                )
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = option.icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp)
            )
            Spacer(Modifier.width(16.dp))
            Text(
                option.label,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

data class ThemeOption(
    val label: String,
    val icon: ImageVector,
    val preference: ThemePreference
)

@Composable
private fun LanguageOptions(
    current: String,
    onSelect: (String) -> Unit
) {
    val languages = listOf("English", "العربيه")

    languages.forEach { language ->
        Row(
            Modifier
                .fillMaxWidth()
                .selectable(
                    selected = current == language,
                    onClick = { onSelect(language) }
                )
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Language,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp)
            )
            Spacer(Modifier.width(16.dp))
            Text(
                language,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

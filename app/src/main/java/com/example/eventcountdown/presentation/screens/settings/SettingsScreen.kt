package com.example.eventcountdown.presentation.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.eventcountdown.R
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
                title = { Text(text = stringResource(id = R.string.settings_label),) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                text = stringResource(id = R.string.theme_label),
                style = MaterialTheme.typography.titleLarge
            )

            ThemeOptions(
                current = settings.themePreference,
                onSelect = settings.updateTheme
            )

            Divider()

            Text(
                text = stringResource(id = R.string.language_label),
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
        ThemeOption(stringResource(id = R.string.light_theme), Icons.Default.LightMode, ThemePreference.LIGHT),
        ThemeOption(stringResource(id = R.string.dark_theme), Icons.Default.DarkMode, ThemePreference.DARK),
        ThemeOption(stringResource(id = R.string.system_theme), Icons.Default.SettingsBrightness, ThemePreference.SYSTEM)
    )

    Column {
        options.forEach { option ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .clickable { onSelect(option.preference) }
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = current == option.preference,
                    onClick = null // Handled by the row's clickable modifier
                )
                Spacer(Modifier.width(16.dp))
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

    Column {
        languages.forEach { language ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .clickable { onSelect(language) }
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = current == language,
                    onClick = null // Handled by the row's clickable modifier
                )
                Spacer(Modifier.width(16.dp))
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
}
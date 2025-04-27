import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf
import com.example.eventcountdown.presentation.theme.ThemePreference

data class AppSettings(
    val themePreference: ThemePreference,
    val languagePreference: String,
    val updateTheme: (ThemePreference) -> Unit,
    val updateLanguage: (String) -> Unit
)


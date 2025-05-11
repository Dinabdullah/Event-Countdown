package com.example.eventcountdown.presentation.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat

// ألوان الوضع النهاري (Light Mode)
private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF006874),
    secondary = Color(0xFF4A6267),
    tertiary = Color(0xFF525E7D),
    background = Color(0xFFFBFCFE),
    surface = Color(0xFFFBFCFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF191C1D),
    onSurface = Color(0xFF191C1D),
    error = Color(0xFFBA1A1A),
    primaryContainer = Color(0xFF97F0FF),
    secondaryContainer = Color(0xFFCDE7ED)
)

// ألوان الوضع الليلي (Dark Mode)
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF4FD8EB),
    secondary = Color(0xFFB1CBD0),
    tertiary = Color(0xFFBAC6EA),
    background = Color.Black,          // True black background
    surface = Color.Black,             // True black surface
    onPrimary = Color(0xFF00363D),
    onSecondary = Color(0xFF01090A),
    onTertiary = Color(0xFF233044),
    onBackground = Color(0xFFE1E3E3),  // Light text/items on black
    onSurface = Color(0xFFE1E3E3),     // Light text/items on black
    error = Color(0xFFFFB4AB),
    primaryContainer = Color(0xFF004F58),
    secondaryContainer = Color(0xFF15333A)
)

// تعريف Typography مخصص
private val CustomTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = MyFonts.robotoSlab,
        fontWeight = FontWeight.Normal,
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = (-0.25).sp
    ),
    displayMedium = TextStyle(
        fontFamily = MyFonts.robotoSlab,
        fontWeight = FontWeight.Normal,
        fontSize = 45.sp,
        lineHeight = 52.sp,
        letterSpacing = 0.sp
    ),
    displaySmall = TextStyle(
        fontFamily = MyFonts.robotoSlab,
        fontWeight = FontWeight.Normal,
        fontSize = 36.sp,
        lineHeight = 44.sp,
        letterSpacing = 0.sp
    ),
    headlineLarge = TextStyle(
        fontFamily = MyFonts.robotoSlab,
        fontWeight = FontWeight.Normal,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = MyFonts.robotoSlab,
        fontWeight = FontWeight.Normal,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = MyFonts.robotoSlab,
        fontWeight = FontWeight.Normal,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp
    ),
    titleLarge = TextStyle(
        fontFamily = MyFonts.robotoSlab,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    titleMedium = TextStyle(
        fontFamily = MyFonts.robotoSlab,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp
    ),
    titleSmall = TextStyle(
        fontFamily = MyFonts.robotoSlab,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = MyFonts.robotoSlab,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = MyFonts.robotoSlab,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),
    bodySmall = TextStyle(
        fontFamily = MyFonts.robotoSlab,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    ),
    labelLarge = TextStyle(
        fontFamily = MyFonts.robotoSlab,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    labelMedium = TextStyle(
        fontFamily = MyFonts.robotoSlab,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),
    labelSmall = TextStyle(
        fontFamily = MyFonts.robotoSlab,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)

@Composable
fun EventCountdownTheme(
    themePreference: ThemePreference = ThemePreference.SYSTEM,
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val isDark = when (themePreference) {
        ThemePreference.DARK -> true
        ThemePreference.LIGHT -> false
        ThemePreference.SYSTEM -> isSystemInDarkTheme()
    }

    val colorScheme = if (isDark) DarkColorScheme else LightColorScheme
//    val colorScheme = when {
//        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
//            val context = LocalContext.current
//            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
//        }
//        darkTheme -> DarkColorScheme
//        else -> LightColorScheme
//    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !isDark

            // تغيير لون شريط التنقل (Navigation Bar) إذا لزم الأمر
            window.navigationBarColor = colorScheme.surface.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !isDark
        }
    }

    MaterialTheme(
        //colorScheme = if (useDarkTheme) DarkColorScheme else LightColorScheme,
        colorScheme = colorScheme,
        typography = CustomTypography,
        content = content
    )
}
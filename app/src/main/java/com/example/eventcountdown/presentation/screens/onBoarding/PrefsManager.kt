package com.example.eventcountdown.presentation.screens.onBoarding

import android.content.Context
import android.media.MediaFormat.KEY_LANGUAGE
import com.example.eventcountdown.presentation.theme.ThemePreference

class PreferencesHelper(context: Context) {
    private val prefs = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)

    companion object {
        private const val ONBOARDING_KEY = "onboardingCompleted"
        private const val KEY_THEME     = "key_theme_preference"
        private const val KEY_LANGUAGE  = "key_language_preference"
        private const val DEFAULT_LANGUAGE = "English"
    }

    // Theme
    fun getTheme(): ThemePreference =
        when (prefs.getString(KEY_THEME, ThemePreference.SYSTEM.name)!!) {
            ThemePreference.LIGHT.name  -> ThemePreference.LIGHT
            ThemePreference.DARK.name   -> ThemePreference.DARK
            else                        -> ThemePreference.SYSTEM
        }

    fun setTheme(theme: ThemePreference) {
        prefs.edit()
            .putString(KEY_THEME, theme.name)
            .apply()
    }

    // Language
    fun getLanguage(): String =
        prefs.getString(KEY_LANGUAGE, DEFAULT_LANGUAGE)!!

    fun setLanguage(language: String) {
        prefs.edit()
            .putString(KEY_LANGUAGE, language)
            .apply()
    }

    var onboardingCompleted: Boolean
        get() = prefs.getBoolean(ONBOARDING_KEY, false)
        set(value) = prefs.edit().putBoolean(ONBOARDING_KEY, value).apply()
}
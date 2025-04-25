package com.example.eventcountdown.presentation.screens.onBoarding

import android.content.Context

class PreferencesHelper(context: Context) {
    private val prefs = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)

    companion object {
        private const val ONBOARDING_KEY = "onboardingCompleted"
    }

    var onboardingCompleted: Boolean
        get() = prefs.getBoolean(ONBOARDING_KEY, false)
        set(value) = prefs.edit().putBoolean(ONBOARDING_KEY, value).apply()
}
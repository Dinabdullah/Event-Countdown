package com.example.eventcountdown.presentation.screens.onBoarding

import android.content.Context

class PreferencesHelper(context: Context) {
    private val prefs = context.getSharedPreferences("OnboardingPrefs", Context.MODE_PRIVATE)

    var isOnboardingComplete: Boolean
        get() = prefs.getBoolean("onboarding_complete", false)
        set(value) = prefs.edit().putBoolean("onboarding_complete", value).apply()

}
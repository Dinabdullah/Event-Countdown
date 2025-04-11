package com.example.eventcountdown.onBoarding

import android.content.Context

class PreferencesHelper(context: Context) {
        private val prefs = context.getSharedPreferences("OnboardingPrefs", Context.MODE_PRIVATE)
    var isOnboardingComplete: Boolean
        get() = prefs.getBoolean("onboarding_complete", false)
        set(value) = prefs.edit().putBoolean("onboarding_complete", value).apply()
      //  prefs.edit().putBoolean("onboardingCompleted", true).apply()


    fun isOnboardingCompleted(context: Context): Boolean {
        val prefs = context.getSharedPreferences("OnboardingPrefs", Context.MODE_PRIVATE)
        return prefs.getBoolean("onboardingCompleted", false)
    }
}
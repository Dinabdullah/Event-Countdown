package com.example.eventcountdown.onBoarding

import android.content.Context

fun setOnboardingCompleted(context: Context) {
    val prefs = context.getSharedPreferences("OnboardingPrefs", Context.MODE_PRIVATE)
    prefs.edit().putBoolean("onboardingCompleted", true).apply()
}

fun isOnboardingCompleted(context: Context): Boolean {
    val prefs = context.getSharedPreferences("OnboardingPrefs", Context.MODE_PRIVATE)
    return prefs.getBoolean("onboardingCompleted", false)
}
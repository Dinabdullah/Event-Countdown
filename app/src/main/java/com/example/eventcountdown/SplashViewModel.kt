package com.example.eventcountdown

import android.content.Context
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.Navigation
import com.example.eventcountdown.onBoarding.PreferencesHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SplashViewModel(
    private val preferencesHelper: PreferencesHelper
) : ViewModel() {
    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    private val _shouldShowOnboarding = MutableStateFlow(true)
    val shouldShowOnboarding = _shouldShowOnboarding.asStateFlow()


    init {
        viewModelScope.launch {
            delay(3000)
            _isLoading.value = false
            _shouldShowOnboarding.value = !preferencesHelper.isOnboardingComplete
        }
    }

    fun shouldShowOnboarding(): Boolean {
        return !preferencesHelper.isOnboardingComplete
    }
}
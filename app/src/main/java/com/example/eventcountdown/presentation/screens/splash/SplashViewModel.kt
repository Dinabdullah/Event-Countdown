package com.example.eventcountdown.presentation.screens.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventcountdown.presentation.screens.onBoarding.PreferencesHelper
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


}
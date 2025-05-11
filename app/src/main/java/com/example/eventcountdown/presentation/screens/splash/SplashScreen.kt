package com.example.eventcountdown.presentation.screens.splash

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.eventcountdown.R
import com.example.eventcountdown.presentation.auth.AuthState
import com.example.eventcountdown.presentation.screens.onBoarding.PreferencesHelper
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    authState: AuthState,
    prefsHelper: PreferencesHelper,
    onNavigate: (String) -> Unit
) {
    // Animation states
    var hourglassVisible by remember { mutableStateOf(false) }
    var textVisible by remember { mutableStateOf(false) }

    // Splash display state
    var splashDisplayed by remember { mutableStateOf(false) }

    // ViewModel setup
    val viewModel: SplashViewModel = viewModel(factory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SplashViewModel(prefsHelper) as T
        }
    })

    // Animation controls
    LaunchedEffect(Unit) {
        hourglassVisible = true
        delay(1500)
        textVisible = true
    }

    // Set splashDisplayed to true after a minimum delay
    LaunchedEffect(Unit) {
        delay(2000) // Minimum splash screen display time (2 seconds)
        splashDisplayed = true
    }

    // Consolidated navigation logic
    LaunchedEffect(splashDisplayed, authState) {
        if (splashDisplayed && authState !is AuthState.Loading) {
            if (!prefsHelper.onboardingCompleted) {
                onNavigate("onboarding")
            } else {
                when (authState) {
                    is AuthState.Authenticated -> onNavigate("home")
                    is AuthState.Unauthenticated -> onNavigate("login")
                    else -> onNavigate("login")
                }
            }
        }
    }

    // Hourglass animation
    val hourglassRotation by animateFloatAsState(
        targetValue = if (hourglassVisible) 360f else 0f,
        animationSpec = tween(1500, easing = FastOutSlowInEasing),
        label = "hourglassRotation"
    )

    // Text animation
    val textScale by animateFloatAsState(
        targetValue = if (textVisible) 1f else 0.7f,
        animationSpec = spring(dampingRatio = 0.5f),
        label = "textScale"
    )

    val gradientColors = listOf(Color(0xFF2962FF), Color(0xFF87CEEB))

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(gradientColors)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.hour),
                contentDescription = "App Logo",
                modifier = Modifier
                    .size(120.dp)
                    .rotate(hourglassRotation),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = stringResource(id = R.string.app_name),
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                modifier = Modifier.scale(textScale),
                fontSize = 36.sp
            )
        }
    }
}
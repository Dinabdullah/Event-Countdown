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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.eventcountdown.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(viewModel: SplashViewModel, navController: NavController) {
    val shouldShowOnboarding by viewModel.shouldShowOnboarding.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // Corrected LaunchedEffect to depend on both isLoading and shouldShowOnboarding
    LaunchedEffect(isLoading, shouldShowOnboarding) {
        if (!isLoading) {
            if (shouldShowOnboarding) {
                navController.navigate("onboarding") {
                    popUpTo("splash") { inclusive = true }
                }
            } else {
                navController.navigate("home") {
                    popUpTo("splash") { inclusive = true }
                }
            }
        }
    }
    // Animation states
    var hourglassVisible by remember { mutableStateOf(false) }
    var textVisible by remember { mutableStateOf(false) }
    var contentVisible by remember { mutableStateOf(false) }


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

    LaunchedEffect(Unit) {
        hourglassVisible = true
        delay(800)
        textVisible = true
    }

    LaunchedEffect(isLoading) {
        if (!isLoading) {
            delay(1500)
            contentVisible = true

            if (shouldShowOnboarding) {
                navController.navigate("onboarding") {
                    popUpTo("splash") { inclusive = true }
                }
            } else {
                navController.navigate("home") {
                    popUpTo("splash") { inclusive = true }
                }
            }
        }
    }

    val gradientColors = listOf(Color(0xFF2962FF), Color(0xFF87CEEB))

    // UI Content
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(gradientColors))
            .alpha(if (contentVisible) 0f else 1f),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.hour_glass),
                contentDescription = "App Logo",
                modifier = Modifier
                    .size(120.dp)
                    .rotate(hourglassRotation),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Eventat",
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
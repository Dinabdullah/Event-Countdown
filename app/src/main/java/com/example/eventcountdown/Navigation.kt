package com.example.eventcountdown

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.eventcountdown.onBoarding.OnboardingPager
import com.example.eventcountdown.ui.AddEventScreen

@Composable
fun EventNavigation(eventViewModel: EventViewModel) {
    val context = LocalContext.current
    val navController = rememberNavController()


    val startDestination = remember {
        val prefs = context.getSharedPreferences("OnboardingPrefs", Context.MODE_PRIVATE)
        if (prefs.getBoolean("onboardingCompleted", false)) "home" else "onboarding"
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable("onboarding") {
            OnboardingPager(
                navController = navController,
                context = context,
                onFinish = {
                    navController.navigate("home") {
                        popUpTo("onboarding") { inclusive = true }
                    }
                }
            )
        }
        composable("home") {
            HomeScreen(navController, eventViewModel)
        }
        composable("addEvent") {
            AddEventScreen(navController, eventViewModel)
        }
        composable("updateEvent/{eventId}") { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId")?.toIntOrNull()
            if (eventId != null) {
                UpdateEventScreen(
                    eventId = eventId,
                    navController = navController,
                    viewModel = eventViewModel
                )
            }
        }
    }
}
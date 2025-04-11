package com.example.eventcountdown

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.eventcountdown.onBoarding.OnboardingPager
import com.example.eventcountdown.onBoarding.PreferencesHelper
import com.example.eventcountdown.splash.SplashScreen
import com.example.eventcountdown.splash.SplashViewModel
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
        startDestination = "splash"
    ) {

        composable("splash") {
            val viewModel: SplashViewModel = viewModel(
                factory = object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return SplashViewModel(PreferencesHelper(context)) as T
                    }
                }
            )
            SplashScreen(viewModel = viewModel, navController = navController)
        }
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
        composable("countdownEvent/{eventId}") { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId")?.toIntOrNull()
            if (eventId != null) {
                CountdownScreen(
                    eventId = eventId,
                    navController = navController,
                    viewModel = eventViewModel
                )
            }
        }
    }
}
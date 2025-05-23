package com.example.eventcountdown.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.eventcountdown.presentation.EventViewModel
import com.example.eventcountdown.presentation.auth.AuthViewModel
import com.example.eventcountdown.presentation.auth.ForgotPasswordScreen
import com.example.eventcountdown.presentation.auth.LoginScreen
import com.example.eventcountdown.presentation.auth.SignupScreen
import com.example.eventcountdown.presentation.screens.pastevent.PastEventsScreen
import com.example.eventcountdown.presentation.screens.about.AboutScreen
import com.example.eventcountdown.presentation.screens.addevent.AddEventScreen
import com.example.eventcountdown.presentation.screens.countdown.CountdownScreen
import com.example.eventcountdown.presentation.screens.home.HomeScreen
import com.example.eventcountdown.presentation.screens.onBoarding.OnboardingPager
import com.example.eventcountdown.presentation.screens.onBoarding.PreferencesHelper
import com.example.eventcountdown.presentation.screens.settings.SettingsScreen
import com.example.eventcountdown.presentation.screens.splash.SplashScreen
import com.example.eventcountdown.presentation.screens.updateevent.UpdateEventScreen
import com.example.eventcountdown.presentation.theme.AppSettings
import com.example.eventcountdown.presentation.theme.AppSettingsViewModel

@Composable
fun EventNavigation(
    authViewModel: AuthViewModel,
    eventViewModel: EventViewModel,
    appSettingsViewModel: AppSettingsViewModel
) {
    val navController = rememberNavController()
    val context = LocalContext.current
    val prefsHelper = remember { PreferencesHelper(context) }

    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        composable("splash") {
            val authState by authViewModel.authState.collectAsState()
            SplashScreen(
                authState = authState,
                prefsHelper = prefsHelper,
                onNavigate = { destination ->
                    navController.navigate(destination) {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            )
        }

        composable("onboarding") {
            OnboardingPager(
                onFinish = {
                    navController.navigate("login") {
                        popUpTo("onboarding") { inclusive = true }
                    }
                }
            )
        }

        composable("login") {
            LoginScreen(
                onNavigateToSignUp = { navController.navigate("signup") },
                onNavigateToMain = {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavigateToForgotPassword = { navController.navigate("forgot_password") },
                viewModel = authViewModel
            )
        }

        composable("signup") {
            SignupScreen(
                onNavigateToLogin = { navController.navigate("login") },
                onNavigateToMain = {
                    navController.navigate("home") {
                        popUpTo("signup") { inclusive = true }
                    }
                },
                viewModel = authViewModel
            )
        }

        composable("forgot_password") {
            ForgotPasswordScreen(
                onNavigateBack = { navController.popBackStack() },
                viewModel = authViewModel
            )
        }

        composable("home") {
            HomeScreen(
                navController = navController,
                viewModel = eventViewModel,
                onSignOut = {
                    authViewModel.signOut()
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            )
        }

        composable("settings") {
            val themePref by appSettingsViewModel.themePreference.collectAsState()
            val languagePref by appSettingsViewModel.languagePreference.collectAsState()
            SettingsScreen(
                navController = navController,
                settings = AppSettings(
                    themePreference = themePref,
                    languagePreference = languagePref,
                    updateTheme = { appSettingsViewModel.updateTheme(it) },
                    updateLanguage = { appSettingsViewModel.updateLanguage(it) }
                )
            )
        }


        composable("pastEvents") {
            PastEventsScreen(
                navController = navController,
                viewModel = eventViewModel
            )
        }

        composable("about") {
            AboutScreen(navController)
        }

        composable("addEvent") {
            AddEventScreen(navController, eventViewModel)
        }

        composable("updateEvent/{eventId}") { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId")?.toIntOrNull()
            eventId?.let {
                UpdateEventScreen(
                    eventId = it,
                    navController = navController,
                    viewModel = eventViewModel
                )
            }
        }

        composable("countdownEvent/{eventId}") { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId")?.toIntOrNull()
            eventId?.let {
                CountdownScreen(
                    eventId = it,
                    navController = navController,
                    viewModel = eventViewModel
                )
            }
        }
    }
}

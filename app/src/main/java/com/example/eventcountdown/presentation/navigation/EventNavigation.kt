package com.example.eventcountdown.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.eventcountdown.presentation.activity.EventViewModel
import com.example.eventcountdown.presentation.auth.AuthState
import com.example.eventcountdown.presentation.auth.AuthViewModel
import com.example.eventcountdown.presentation.auth.ForgotPasswordScreen
import com.example.eventcountdown.presentation.auth.LoginScreen
import com.example.eventcountdown.presentation.auth.SignupScreen
import com.example.eventcountdown.presentation.screens.home.HomeScreen

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object SignUp : Screen("signup")
    object ForgotPassword : Screen("forgot_password")
    object Home : Screen("home")
}

@Composable
fun EventNavigation(
    authViewModel: AuthViewModel,
    eventViewModel: EventViewModel,
    navController: NavHostController = rememberNavController()
) {
    val authState by authViewModel.authState.collectAsState()

    NavHost(
        navController = navController,
        startDestination = when (authState) {
            is AuthState.Authenticated -> Screen.Home.route
            else -> Screen.Login.route
        }
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToSignUp = { navController.navigate(Screen.SignUp.route) },
                onNavigateToMain = { navController.navigate(Screen.Home.route) },
                onNavigateToForgotPassword = { navController.navigate(Screen.ForgotPassword.route) },
                viewModel = authViewModel
            )
        }

        composable(Screen.SignUp.route) {
            SignupScreen(
                onNavigateToLogin = { navController.navigate(Screen.Login.route) },
                onNavigateToMain = { navController.navigate(Screen.Home.route) },
                viewModel = authViewModel
            )
        }

        composable(Screen.ForgotPassword.route) {
            ForgotPasswordScreen(
                onNavigateBack = { navController.navigateUp() },
                viewModel = authViewModel
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                navController = navController,
                viewModel = eventViewModel,
                onSignOut = {
                    authViewModel.signOut()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }
    }
} 
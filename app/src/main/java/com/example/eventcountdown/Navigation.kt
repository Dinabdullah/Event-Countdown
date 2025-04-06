package com.example.eventcountdown

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.eventcountdown.ui.AddEventScreen

@Composable
fun EventNavigation(eventViewModel: EventViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {
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

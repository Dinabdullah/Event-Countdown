package com.example.eventcountdown

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.eventcountdown.ui.AddEventScreen
import com.example.eventcountdown.ui.EditEventScreen
import com.example.eventcountdown.ui.HomeScreen

@Composable
fun EventNavigation(viewModel: EventViewModel) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            HomeScreen(navController, viewModel)
        }
        composable("addEvent") {
            AddEventScreen(navController, viewModel)
        }
        composable("editEvent/{eventId}") { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId")?.toIntOrNull()
            val event = viewModel.events.value.find { it.id == eventId }
            if (event != null) {
                EditEventScreen(
                    event = event,
                    onUpdate = { updatedEvent ->
                        viewModel.updateEvent(updatedEvent)
                        navController.popBackStack()
                    },
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
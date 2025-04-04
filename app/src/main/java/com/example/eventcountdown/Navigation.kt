package com.example.eventcountdown

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

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
    }
}
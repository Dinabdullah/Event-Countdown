package com.example.eventcountdown.presentation.screens.about

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun AboutScreen(navController: NavController) {
    Row (
        modifier = Modifier.padding(top = 8.dp),
        horizontalArrangement = Arrangement.Start
    ){
        IconButton(onClick = { navController.popBackStack() }) {
            Icon(Icons.Default.ArrowBack, "Back")
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "About Us",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Event Countdown App helps you track important events in your life.\n\n" +
                    "Developed by DEPI students using Kotlin, Jetpack Compose, and Firebase.\n\n" +
                    "Under the supervision of Instructor and Mentor Ahmed Atef.",
            fontSize = 18.sp
        )
    }
}

package com.example.eventcountdown.presentation.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.eventcountdown.R
import com.example.eventcountdown.presentation.activity.EventViewModel
import com.example.eventcountdown.presentation.componants.PastEventsList
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PastEventsScreen(
    navController: NavController,
    viewModel: EventViewModel
) {
    val events by viewModel.events.collectAsState()
    var currentTime by remember { mutableStateOf(System.currentTimeMillis()) }
    val pastEvents = remember(events, currentTime) {
        events.filter { it.date.time <= currentTime }.sortedByDescending { it.date.time }
    }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            currentTime = System.currentTimeMillis()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = stringResource(id = R.string.past_events)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
                )
            )
        }
    ) { padding ->
        PastEventsList(
            pastEvents = pastEvents,
            currentTime = currentTime,
            onEventClick = { navController.navigate("countdownEvent/${it.id}") },
            onEdit = { navController.navigate("updateEvent/${it.id}") },
            onDelete = viewModel::deleteEvent,
            modifier = Modifier.padding(padding)
        )
    }
}
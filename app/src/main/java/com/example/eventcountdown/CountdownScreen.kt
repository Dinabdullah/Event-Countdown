package com.example.eventcountdown

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import java.util.*
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CountdownScreen(
    eventId: Int,
    navController: NavController,
    viewModel: EventViewModel
) {
    val events by viewModel.events.collectAsState()
    val event = events.find { it.id == eventId }

    if (event == null) {
        Text("Event not found")
        return
    }

    // State for countdown timer
    var remainingTime by remember { mutableStateOf(calculateTimeRemaining(event.date)) }

    // Update countdown every second
    LaunchedEffect(key1 = event.date) {
        while (true) {
            delay(1.seconds)
            remainingTime = calculateTimeRemaining(event.date)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(event.title) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = event.title,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = event.description,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(32.dp))

            if (remainingTime.isNegative()) {
                Text(
                    text = "Event has passed",
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.error
                )
            } else {
                CountdownDisplay(remainingTime)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Event date: ${formatDate(event.date)}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun CountdownDisplay(duration: TimeRemaining) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        CountdownUnit(value = duration.days, unit = "DAYS")
        Spacer(modifier = Modifier.width(16.dp))
        CountdownUnit(value = duration.hours, unit = "HOURS")
        Spacer(modifier = Modifier.width(16.dp))
        CountdownUnit(value = duration.minutes, unit = "MINUTES")
        Spacer(modifier = Modifier.width(16.dp))
        CountdownUnit(value = duration.seconds, unit = "SECONDS")
    }
}

@Composable
fun CountdownUnit(value: Long, unit: String) {
    val maxValue = when (unit) {
        "HOURS" -> 24f
        "MINUTES", "SECONDS" -> 60f
        else -> 1f  // For days, we'll use a full circle
    }
    val progress = value.toFloat() / maxValue

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 500, easing = LinearEasing)
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.size(90.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                progress = animatedProgress,
                modifier = Modifier.size(90.dp),
                strokeWidth = 8.dp,
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            )
            Text(
                text = value.toString(),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = unit,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
        )
    }
}

data class TimeRemaining(
    val days: Long,
    val hours: Long,
    val minutes: Long,
    val seconds: Long
) {
    fun isNegative(): Boolean {
        return days < 0 || hours < 0 || minutes < 0 || seconds < 0
    }
}

fun calculateTimeRemaining(eventDate: Date): TimeRemaining {
    val currentTime = System.currentTimeMillis()
    val eventTime = eventDate.time
    val diffMillis = eventTime - currentTime

    if (diffMillis <= 0) {
        return TimeRemaining(0, 0, 0, 0)
    }

    val seconds = (diffMillis / 1000) % 60
    val minutes = (diffMillis / (1000 * 60)) % 60
    val hours = (diffMillis / (1000 * 60 * 60)) % 24
    val days = diffMillis / (1000 * 60 * 60 * 24)

    return TimeRemaining(days, hours, minutes, seconds)
}

fun formatDate(date: Date): String {
    val calendar = Calendar.getInstance()
    calendar.time = date

    return "${calendar.get(Calendar.DAY_OF_MONTH)}/${calendar.get(Calendar.MONTH) + 1}/${calendar.get(Calendar.YEAR)} " +
            "${calendar.get(Calendar.HOUR_OF_DAY)}:${String.format("%02d", calendar.get(Calendar.MINUTE))}"
}


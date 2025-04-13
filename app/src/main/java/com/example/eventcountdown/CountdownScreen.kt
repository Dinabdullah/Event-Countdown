package com.example.eventcountdown

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
    viewModel: EventViewModel,
) {
    val events by viewModel.events.collectAsState()
    val event = events.find { it.id == eventId } ?: return Text("Event not found")

    var remainingTime by remember { mutableStateOf(calculateTimeRemaining(event.date)) }

    LaunchedEffect(event.date) {
        while (true) {
            delay(1.seconds)
            remainingTime = calculateTimeRemaining(event.date)
        }
    }

    val eventColor = remember { Color(event.color) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(event.title, color = MaterialTheme.colorScheme.onSurface) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Time display
            Text(
                text = formatTime(event.date),
                style = MaterialTheme.typography.displaySmall,
                color = eventColor,
                modifier = Modifier.padding(top = 32.dp, bottom = 16.dp)
            )

            // Countdown display
            if (remainingTime.isNegative()) {
                Text(
                    text = "Event has passed",
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            } else {
                CountdownDisplay(remainingTime, eventColor)
            }

            // Event details
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = event.description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Text(
                    text = formatFullDate(event.date),
                    style = MaterialTheme.typography.bodyMedium,
                    color = eventColor
                )
            }
        }
    }
}

@Composable
fun CountdownDisplay(
    duration: TimeRemaining,
    color: Color
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Main countdown circle
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(200.dp)
        ) {
            val progress by animateFloatAsState(
                targetValue = duration.progress(),
                animationSpec = tween(durationMillis = 1000, easing = LinearEasing),
                label = "countdown"
            )

            CircularProgressIndicator(
                progress = 1f,
                modifier = Modifier.fillMaxSize(),
                strokeWidth = 12.dp,
                color = color.copy(alpha = 0.2f)
            )

            CircularProgressIndicator(
                progress = progress,
                modifier = Modifier.fillMaxSize(),
                strokeWidth = 12.dp,
                color = color
            )

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "${duration.days}d ${duration.hours}h",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${duration.minutes}m ${duration.seconds}s",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Time unit breakdown
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            CountdownUnit(
                value = duration.days,
                unit = "DAYS",
                color = color,
                totalValue = duration.days + 1
            )
            CountdownUnit(
                value = duration.hours,
                unit = "HOURS",
                color = color,
                totalValue = 24
            )
            CountdownUnit(
                value = duration.minutes,
                unit = "MINUTES",
                color = color,
                totalValue = 60
            )
            CountdownUnit(
                value = duration.seconds,
                unit = "SECONDS",
                color = color,
                totalValue = 60
            )
        }
    }
}

@Composable
fun CountdownUnit(
    value: Long,
    unit: String,
    color: Color,
    totalValue: Long
) {
    val animatedProgress by animateFloatAsState(
        targetValue = if (totalValue > 0) value.toFloat() / totalValue.toFloat() else 0f,
        animationSpec = tween(durationMillis = 1000, easing = LinearEasing),
        label = "unitProgress"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 8.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(80.dp)
        ) {
            CircularProgressIndicator(
                progress = animatedProgress,
                modifier = Modifier.fillMaxSize(),
                strokeWidth = 6.dp,
                color = color,
                trackColor = color.copy(alpha = 0.2f)
            )
            Text(
                text = value.toString().padStart(2, '0'),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = unit,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}

// Helper functions
fun formatTime(date: Date): String {
    val calendar = Calendar.getInstance()
    calendar.time = date
    return "${calendar.get(Calendar.HOUR_OF_DAY)}:${"%02d".format(calendar.get(Calendar.MINUTE))}"
}

fun formatFullDate(date: Date): String {
    val calendar = Calendar.getInstance()
    calendar.time = date
    return "${calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())} " +
            "${calendar.get(Calendar.DAY_OF_MONTH)}, ${calendar.get(Calendar.YEAR)}"
}

data class TimeRemaining(
    val totalMillis: Long,
    val remainingMillis: Long,
    val days: Long,
    val hours: Long,
    val minutes: Long,
    val seconds: Long
) {
    fun isNegative(): Boolean = remainingMillis <= 0
    fun progress(): Float = if (totalMillis > 0) remainingMillis.toFloat() / totalMillis.toFloat() else 0f
}

fun calculateTimeRemaining(eventDate: Date): TimeRemaining {
    val currentTime = System.currentTimeMillis()
    val eventTime = eventDate.time
    val remainingMillis = eventTime - currentTime

    if (remainingMillis <= 0) {
        return TimeRemaining(0, 0, 0, 0, 0, 0)
    }

    return TimeRemaining(
        totalMillis = eventTime - eventDate.time,
        remainingMillis = remainingMillis,
        days = remainingMillis / (1000 * 60 * 60 * 24),
        hours = (remainingMillis / (1000 * 60 * 60)) % 24,
        minutes = (remainingMillis / (1000 * 60)) % 60,
        seconds = (remainingMillis / 1000) % 60
    )
}
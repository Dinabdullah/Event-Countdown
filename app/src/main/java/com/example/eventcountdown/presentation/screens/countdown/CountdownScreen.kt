package com.example.eventcountdown.presentation.screens.countdown

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.eventcountdown.R
import com.example.eventcountdown.presentation.EventViewModel
import kotlinx.coroutines.delay
import java.io.File
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.cos
import kotlin.math.sin
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
    val context = LocalContext.current

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Background Image with blur effect
        event.backgroundImageUri?.let { path ->
            val imageFile = remember(path) { File(path) }
            if (imageFile.exists()) {
                Image(
                    painter = rememberAsyncImagePainter(imageFile),
                    contentDescription = "Event background",
                    modifier = Modifier
                        .fillMaxSize()
                        .blur(radius = 4.dp),
                    contentScale = ContentScale.Crop,
                    alpha = 0.7f
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.4f))
        )

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            event.title,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        scrolledContainerColor = Color.Transparent
                    )
                )
            },
            containerColor = Color.Transparent
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Event details
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = event.title,
                        style = MaterialTheme.typography.headlineLarge,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Text(
                        text = event.description,
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White.copy(alpha = 0.9f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Text(
                        text = formatFullDate(event.date),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.8f),
                    )
                }

                // Time display
                Text(
                    text = formatTime(event.date),
                    style = MaterialTheme.typography.displaySmall,
                    color = Color.White,
                    modifier = Modifier.padding(top = 32.dp, bottom = 16.dp)

                )

                // Countdown display
                if (remainingTime.isNegative()) {
                    Text(
                        text = stringResource(id = R.string.passed_event),
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                } else {
                    CountdownDisplay(remainingTime, eventColor)
                }
            }
        }
    }
}

@Composable
fun CountdownDisplay(
    duration: TimeRemaining,
    color: Color,
) {
    val infiniteTransition = rememberInfiniteTransition()
    val rollingPosition by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(220.dp)
        ) {
            val progress by animateFloatAsState(
                targetValue = duration.progress(),
                animationSpec = tween(1000, easing = LinearEasing)
            )

            CircularProgressIndicator(
                progress = 1f,
                modifier = Modifier.size(200.dp),
                strokeWidth = 12.dp,
                color = color.copy(alpha = 0.2f)
            )

            CircularProgressIndicator(
                progress = progress,
                modifier = Modifier.size(200.dp),
                strokeWidth = 12.dp,
                color = color
            )

            Canvas(modifier = Modifier.fillMaxSize()) {
                val radius = 100.dp.toPx()
                val angle = (rollingPosition * 360f) * (Math.PI.toFloat() / 180f)
                val x = radius * cos(angle) + center.x
                val y = radius * sin(angle) + center.y

                drawCircle(
                    color = color,
                    radius = 16.dp.toPx(),
                    center = Offset(x, y)
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "${duration.days}d ${duration.hours}h",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "${duration.minutes}m ${duration.seconds}s",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            CountdownUnit(
                value = duration.days,
                unit = stringResource(id = R.string.days),
                color = color,
                totalValue = duration.days + 1
            )
            CountdownUnit(
                value = duration.hours,
                unit = stringResource(id = R.string.hours),
                color = color,
                totalValue = 24
            )
            CountdownUnit(
                value = duration.minutes,
                unit = stringResource(id = R.string.minutes),
                color = color,
                totalValue = 60
            )
            CountdownUnit(
                value = duration.seconds,
                unit = stringResource(id = R.string.seconds),
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
    totalValue: Long,
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
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = unit,
            style = MaterialTheme.typography.labelMedium,
            color = Color.White.copy(alpha = 0.8f)
        )
    }
}

// Helper functions remain the same
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
    val seconds: Long,
) {
    fun isNegative(): Boolean = remainingMillis <= 0
    fun progress(): Float =
        if (totalMillis > 0) remainingMillis.toFloat() / totalMillis.toFloat() else 0f
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
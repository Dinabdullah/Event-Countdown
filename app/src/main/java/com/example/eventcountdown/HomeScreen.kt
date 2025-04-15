package com.example.eventcountdown

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.eventcountdown.api.Holiday
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, viewModel: EventViewModel) {
    val events by viewModel.events.collectAsState()
    val holidays by viewModel.holidays.collectAsState()
    val now = remember { System.currentTimeMillis() }
    var currentTime by remember { mutableStateOf(now) }
    val isAddingHolidays by viewModel.isAddingHolidays.collectAsState()

    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            currentTime = System.currentTimeMillis()
        }
    }

    val scrollState = rememberLazyListState()
    val fabVisible by remember {
        derivedStateOf { scrollState.firstVisibleItemIndex == 0 }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Event Countdown",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
                )
            )
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = fabVisible,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                FloatingActionButton(
                    onClick = { navController.navigate("addEvent") },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier
                        .padding(16.dp)
                        .shadow(8.dp, shape = CircleShape)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Event")
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            if (events.isEmpty() && holidays.isEmpty()) {
                EmptyStateView(modifier = Modifier.padding(padding))
            } else {
                CombinedEventList(
                    events = events,
                    holidays = holidays,
                    currentTime = currentTime,
                    onEventClick = { navController.navigate("countdownEvent/${it.id}") },
                    onEdit = { navController.navigate("updateEvent/${it.id}") },
                    onDelete = viewModel::deleteEvent,
                    scrollState = scrollState,
                    modifier = Modifier.padding(padding)
                )
            }

            if (isAddingHolidays) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.7f))
                        .clickable(enabled = false) {},
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(48.dp),
                        strokeWidth = 4.dp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
private fun CombinedEventList(
    events: List<Event>,
    holidays: List<Holiday>,
    currentTime: Long,
    onEventClick: (Event) -> Unit,
    onEdit: (Event) -> Unit,
    onDelete: (Event) -> Unit,
    scrollState: LazyListState,
    modifier: Modifier = Modifier,
) {
    val upcomingEvents = remember(events, currentTime) {
        events.filter { it.date.time > currentTime }.sortedBy { it.date.time }
    }
    val pastEvents = remember(events, currentTime) {
        events.filter { it.date.time <= currentTime }.sortedByDescending { it.date.time }
    }

    LazyColumn(
        state = scrollState,
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Horizontal Scrollable Holidays Section
        if (holidays.isNotEmpty()) {
            item {
                Column {
                    SectionHeader("Public Holidays (${holidays.size})")
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp)
                    ) {
                        items(holidays) { holiday ->
                            HolidayCard(
                                holiday = holiday,
                                currentTime = currentTime,
                                modifier = Modifier.width(280.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }

        // Upcoming Events Section
        if (upcomingEvents.isNotEmpty()) {
            item { SectionHeader("Your Upcoming Events (${upcomingEvents.size})") }
            items(upcomingEvents, key = { it.id }) { event ->
                AnimatedEventCard(
                    event = event,
                    currentTime = currentTime,
                    onClick = { onEventClick(event) },
                    onEdit = { onEdit(event) },
                    onDelete = { onDelete(event) }
                )
            }
        }

        // Past Events Section
        if (pastEvents.isNotEmpty()) {
            item { SectionHeader("Past Events (${pastEvents.size})") }
            items(pastEvents, key = { it.id }) { event ->
                AnimatedEventCard(
                    event = event,
                    currentTime = currentTime,
                    onClick = { onEventClick(event) },
                    onEdit = { onEdit(event) },
                    onDelete = { onDelete(event) }
                )
            }
        }
    }
}

@Composable
private fun AnimatedEventCard(
    event: Event,
    currentTime: Long,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    val enterTransition = remember {
        fadeIn(animationSpec = tween(300)) +
                expandVertically(animationSpec = tween(300))
    }
    val exitTransition = remember {
        fadeOut(animationSpec = tween(300)) +
                shrinkVertically(animationSpec = tween(300))
    }

    AnimatedVisibility(
        visible = true,
        enter = enterTransition,
        exit = exitTransition
    ) {
        EventCard(
            event = event,
            currentTime = currentTime,
            onClick = onClick,
            onEdit = onEdit,
            onDelete = onDelete
        )
    }
}

@Composable
fun HolidayCard(
    holiday: Holiday,
    currentTime: Long,
    modifier: Modifier = Modifier,
) {
    val parsedDate = remember(holiday.date) {
        runCatching {
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(holiday.date) ?: Date()
        }.getOrElse { Date() }
    }

    val eventTime = parsedDate.time
    val isPast = eventTime <= currentTime
    val isToday = !isPast && (eventTime - currentTime) < 86400000

    val elevation by animateDpAsState(
        targetValue = if (isToday) 8.dp else 4.dp,
        animationSpec = tween(300)
    )

    Card(
        modifier = modifier
            .height(120.dp)
            .animateContentSize(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer
        ),
        elevation = CardDefaults.cardElevation(elevation),
        shape = MaterialTheme.shapes.large,
        border = if (isToday) BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
        ) else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Main Content
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = holiday.name,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = MaterialTheme.colorScheme.onTertiaryContainer,
                        maxLines = 1
                    )

                    if (holiday.localName != null && holiday.localName != holiday.name) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = holiday.localName,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.75f),
                            maxLines = 1
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = holiday.countryCode,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.6f)
                        )
                        Text(
                            text = formatHolidayDate(parsedDate),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    }

                    Surface(modifier = Modifier.height(60.dp),
                        shape = MaterialTheme.shapes.small,
                        color = when {
                            isPast -> MaterialTheme.colorScheme.errorContainer
                            isToday -> MaterialTheme.colorScheme.primaryContainer
                            else -> MaterialTheme.colorScheme.secondaryContainer
                        },
                        shadowElevation = 2.dp
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_holiday),
                                contentDescription = "Holiday Icon",
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(28.dp),
                            )
                            Text(
                                text = when {
                                    isPast -> "Passed"
                                    isToday -> "Today!"
                                    else -> "Upcoming"
                                },
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = when {
                                    isPast -> MaterialTheme.colorScheme.onErrorContainer
                                    isToday -> MaterialTheme.colorScheme.onPrimaryContainer
                                    else -> MaterialTheme.colorScheme.onSecondaryContainer
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyStateView(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition()
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.notes),
            contentDescription = "Empty events illustration",
            modifier = Modifier
                .size(250.dp)
                .scale(pulse),
            colorFilter = ColorFilter.tint(
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
            )
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No events or holidays",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )
        )
        Text(
            text = "Tap the + button to add your first event",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            ),
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Composable
private fun SectionHeader(title: String) {
    val infiniteTransition = rememberInfiniteTransition()
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary.copy(alpha = alpha)
            ),
            modifier = Modifier.padding(vertical = 8.dp)
        )
        Divider(
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
            thickness = 1.dp,
            modifier = Modifier.padding(bottom = 4.dp)
        )
    }
}

@Composable
private fun EventCard(
    event: Event,
    currentTime: Long,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    val eventTime = event.date.time
    val isPastEvent = eventTime <= currentTime
    val diffMillis = (eventTime - currentTime).coerceAtLeast(0)

    val days = diffMillis / (1000 * 60 * 60 * 24)
    val hours = (diffMillis / (1000 * 60 * 60)) % 24
    val minutes = (diffMillis / (1000 * 60)) % 60
    val seconds = (diffMillis / 1000) % 60

    val timeText by remember(diffMillis) {
        derivedStateOf {
            when {
                isPastEvent -> "Completed"
                days > 0 -> "${days}d ${hours}h"
                hours > 0 -> "${hours}h ${minutes}m"
                else -> "${minutes}m ${seconds}s"
            }
        }
    }

    val progress by remember(diffMillis) {
        derivedStateOf {
            when {
                isPastEvent -> 1f
                days > 7 -> 0.25f
                days > 3 -> 0.5f
                days > 0 -> 0.75f
                else -> 1 - (diffMillis.toFloat() / (24 * 60 * 60 * 1000))
            }
        }
    }

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(500, easing = FastOutSlowInEasing),
        label = "progressAnimation"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (isPastEvent) {
                MaterialTheme.colorScheme.surfaceVariant
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isPastEvent) 2.dp else 4.dp
        ),
        shape = RoundedCornerShape(16.dp),
        border = if (!isPastEvent) BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
        ) else null
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .animateContentSize()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = event.title,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = if (isPastEvent) {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            } else {
                                MaterialTheme.colorScheme.primary
                            }
                        ),
                        maxLines = 1
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = event.description,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        maxLines = 2
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = formatDateForDisplay(event.date),
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = MaterialTheme.colorScheme.outline
                        )
                    )
                }

                Box(
                    modifier = Modifier.size(72.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (!isPastEvent) {
                        CircularProgressIndicator(
                            progress = animatedProgress,
                            modifier = Modifier.size(72.dp),
                            strokeWidth = 6.dp,
                            color = when {
                                days < 1 -> MaterialTheme.colorScheme.error
                                days < 3 -> MaterialTheme.colorScheme.primary
                                else -> MaterialTheme.colorScheme.secondary
                            },
                            trackColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                        )
                    }
                    Text(
                        text = timeText,
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (isPastEvent) {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            } else {
                                MaterialTheme.colorScheme.onSurface
                            }
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = onEdit,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Edit")
                }
                Spacer(modifier = Modifier.width(8.dp))
                TextButton(
                    onClick = onDelete,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Delete")
                }
            }
        }
    }
}

private fun formatHolidayDate(date: Date): String {
    val dateFormat = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
    return dateFormat.format(date)
}

private fun formatDateForDisplay(date: Date): String {
    val dateFormat = SimpleDateFormat("EEE, MMM d • h:mm a", Locale.getDefault())
    return dateFormat.format(date)
}
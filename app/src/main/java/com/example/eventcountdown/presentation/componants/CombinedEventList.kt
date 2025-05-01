package com.example.eventcountdown.presentation.componants

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.ResistanceConfig
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.swipeable
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.ExperimentalWearMaterialApi
import androidx.wear.compose.material.rememberSwipeableState
import com.example.eventcountdown.data.local.Event
import com.example.eventcountdown.data.remote.model.Holiday
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

@Composable
fun CombinedEventList(
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

    LazyColumn(
        state = scrollState,
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
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
    }
}

@OptIn(ExperimentalWearMaterialApi::class)
@Composable
fun SwipeableEventCard(
    event: Event,
    currentTime: Long,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Convert 96dp to pixels for swipe threshold
    val swipeThreshold = with(LocalDensity.current) { 96.dp.toPx() }
    val swipeableState = rememberSwipeableState(initialValue = 0)
    val anchors = mapOf(0f to 0, -swipeThreshold to 1) // Maps anchor points to states

    // Trigger delete when fully swiped
    LaunchedEffect(swipeableState.currentValue) {
        if (swipeableState.currentValue == 1) {
            onDelete()
            delay(300)
            if (swipeableState.currentValue == 1) {
                swipeableState.snapTo(0)
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
    ) {
        // Delete background that appears when swiping
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.error)
                .padding(end = 96.dp),
            contentAlignment = Alignment.CenterEnd
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete",
                tint = MaterialTheme.colorScheme.onError,
                modifier = Modifier.size(24.dp)
            )
        }

    }
}
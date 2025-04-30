package com.example.eventcountdown.presentation.componants

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.eventcountdown.data.local.Event
import com.example.eventcountdown.data.remote.model.Holiday

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
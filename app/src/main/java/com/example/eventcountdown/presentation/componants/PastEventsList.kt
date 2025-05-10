package com.example.eventcountdown.presentation.componants

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.eventcountdown.R
import com.example.eventcountdown.data.local.Event



@Composable
fun PastEventsList(
    pastEvents: List<Event>,
    currentTime: Long,
    onEventClick: (Event) -> Unit,
    onEdit: (Event) -> Unit,
    onDelete: (Event) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (pastEvents.isNotEmpty()) {
            item { SectionHeader("${stringResource(id = R.string.past_events)} (${pastEvents.size})") }
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
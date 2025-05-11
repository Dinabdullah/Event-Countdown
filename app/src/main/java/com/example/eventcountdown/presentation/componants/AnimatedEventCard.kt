package com.example.eventcountdown.presentation.componants

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DismissDirection
import androidx.compose.material3.DismissValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismiss
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.eventcountdown.data.local.Event

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimatedEventCard(
    event: Event,
    currentTime: Long,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = { Text("Delete Event") },
            text = { Text("Are you sure you want to delete this event?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirmation = false
                        onDelete()
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteConfirmation = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    val dismissState = rememberDismissState(
        confirmValueChange = {
            when (it) {
                DismissValue.DismissedToEnd -> {
                    onEdit()
                    false // Don't dismiss the item
                }
                DismissValue.DismissedToStart -> {
                    showDeleteConfirmation = true
                    false // Don't dismiss the item until confirmed
                }
                else -> false
            }
        }
    )

    SwipeToDismiss(
        state = dismissState,
        directions = setOf(
            DismissDirection.StartToEnd, // Swipe right to edit
            DismissDirection.EndToStart  // Swipe left to delete
        ),
        background = {
            val direction = dismissState.dismissDirection ?: return@SwipeToDismiss

            val icon: ImageVector
            val description: String
            val alignment: Alignment
            val tint: Color

            when (direction) {
                DismissDirection.StartToEnd -> {
                    icon = Icons.Default.Edit
                    description = "Edit event"
                    alignment = Alignment.CenterStart
                    tint = MaterialTheme.colorScheme.primary
                }
                DismissDirection.EndToStart -> {
                    icon = Icons.Default.Delete
                    description = "Delete event"
                    alignment = Alignment.CenterEnd
                    tint = MaterialTheme.colorScheme.error
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                contentAlignment = alignment
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = description,
                    tint = tint
                )
            }
        },
        dismissContent = {
            AnimatedVisibility(
                visible = true,
                enter = fadeIn(tween(300)) + expandVertically(tween(300)),
                exit = fadeOut(tween(300)) + shrinkVertically(tween(300))
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
    )
}

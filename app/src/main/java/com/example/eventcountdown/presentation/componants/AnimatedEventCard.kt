package com.example.eventcountdown.presentation.componants

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.example.eventcountdown.data.local.Event

@Composable
fun AnimatedEventCard(
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





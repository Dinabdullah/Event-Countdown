package com.example.eventcountdown.presentation.componants

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.eventcountdown.data.local.Event
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun EventCard(
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
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        event.getBackgroundColor(),
                        event
                            .getBackgroundColor()
                            .copy(0.3f)
                    ),
                ),
                shape = RoundedCornerShape(24.dp)
            )
    ) {
        Column {
            Spacer(modifier = Modifier.height(20.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onClick)
                    .height(220.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isPastEvent) {
                        MaterialTheme.colorScheme.surfaceVariant
                    } else {
                        MaterialTheme.colorScheme.onSecondary
                    }
                ),
                shape = RoundedCornerShape(16.dp),
                border = if (!isPastEvent) BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                ) else null
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    event.backgroundImageUri?.let { uri ->

                        Image(
                            painter = rememberAsyncImagePainter(model = uri),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .alpha(0.5f)
                        )
                    }
                    Column(
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                                .animateContentSize()
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
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
                                                event.getBackgroundColor()
                                            }
                                        ),
                                        maxLines = 1
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = event.description,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            color = if (isPastEvent) {
                                                MaterialTheme.colorScheme.onSurfaceVariant.copy(0.7f)
                                            } else {
                                                event.getBackgroundColor().copy(0.7f)
                                            }
                                        ),
                                        maxLines = 2
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = formatDateForDisplay(event.date),
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            color = if (isPastEvent) {
                                                MaterialTheme.colorScheme.onSurfaceVariant.copy(0.7f)
                                            } else {
                                                MaterialTheme.colorScheme.onSurface.copy(0.7f)
                                            }
                                        )
                                    )
                                }

                                Spacer(modifier = Modifier.width(8.dp))

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
                                                days < 1 -> Color.Red
                                                days < 3 -> Color.Blue
                                                else -> Color.Gray
                                            },
                                            trackColor = MaterialTheme.colorScheme.surfaceVariant.copy(
                                                alpha = 0.3f
                                            )
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

                            Spacer(modifier = Modifier.height(40.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                if (!isPastEvent) {
                                    TextButton(
                                        onClick = onEdit,
                                        colors = ButtonDefaults.textButtonColors(
                                            contentColor = Color.White,
                                            containerColor = event.getBackgroundColor().copy(0.9f)
                                        ),
                                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
                                        shape = RoundedCornerShape(24.dp),
                                        modifier = Modifier.height(36.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Edit,
                                            contentDescription = "Edit",
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Text(text = "Edit")
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                }
                                TextButton(
                                    onClick = onDelete,
                                    modifier = Modifier
                                        .padding(start = 4.dp)
                                        .height(36.dp),
                                    colors = ButtonDefaults.textButtonColors(
                                        contentColor = Color.White,
                                        containerColor = Color.Red.copy(0.9f)
                                    ),
                                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
                                    shape = RoundedCornerShape(8.dp),

                                    ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete",
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Text(text = "Delete")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


fun formatDateForDisplay(date: Date): String {
    val dateFormat = SimpleDateFormat("EEE, MMM d • h:mm a", Locale.getDefault())
    return dateFormat.format(date)
}
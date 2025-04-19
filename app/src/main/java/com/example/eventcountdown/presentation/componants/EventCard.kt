package com.example.eventcountdown.presentation.componants

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .height(200.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isPastEvent) {
                MaterialTheme.colorScheme.surfaceVariant
            } else {
                event.getBackgroundColor()
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
        Box(modifier = Modifier.fillMaxSize()) {
            event.backgroundImageUri?.let { uri ->

                Image(
                    painter = rememberAsyncImagePainter(model = uri),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .alpha(0.4f)
                )
            }


            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize()
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
    }
}


fun formatDateForDisplay(date: Date): String {
    val dateFormat = SimpleDateFormat("EEE, MMM d • h:mm a", Locale.getDefault())
    return dateFormat.format(date)
}
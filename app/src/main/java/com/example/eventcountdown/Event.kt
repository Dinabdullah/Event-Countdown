package com.example.eventcountdown

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import java.util.Date

@Entity(tableName = "events")
@TypeConverters(Converters::class)
data class Event(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String,
    val date: Date,
    val color: Int = Color.Blue.toArgb(),
) {
    // Helper function to convert back to Color
    fun getColor(): Color {
        return Color(color)
    }
}
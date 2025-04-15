package com.example.eventcountdown

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.room.Room
import com.example.eventcountdown.api.HolidayRepository
import com.example.eventcountdown.api.RetrofitClient
import com.example.eventcountdown.ui.theme.EventCountdownTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "event_database.db"
        ).fallbackToDestructiveMigration().build()

        setContent {
            EventCountdownTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val holidayRepository = HolidayRepository(RetrofitClient.api)
                    EventNavigation(
                        EventViewModel(
                            application = application,
                            eventDao = db.eventDao(),
                            holidayRepository = holidayRepository
                        )
                    )
                }
            }
        }
        requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 100)
        createNotificationChannel()

    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(
                "event_countdown_channel",
                "Event Countdown Notifications",
                importance
            ).apply {
                description = "Notifications for when events are due"
                enableLights(true)
                lightColor = Color.RED
                enableVibration(true)
            }

            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}


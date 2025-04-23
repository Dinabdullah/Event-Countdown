package com.example.eventcountdown.presentation.activity

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.room.Room
import androidx.work.WorkManager
import com.example.eventcountdown.data.local.AppDatabase
import com.example.eventcountdown.data.remote.api.RetrofitClient
import com.example.eventcountdown.data.repository.HolidayRepository
import com.example.eventcountdown.presentation.navigation.EventNavigation
import com.example.eventcountdown.presentation.theme.EventCountdownTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val workManager = WorkManager.getInstance(this)
        Log.d("NotificationDebug", "WorkManager initialized: $workManager")

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
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "event_countdown_channel",
                "Event Countdown Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for when events are happening"
                enableLights(true)
                enableVibration(true)
            }

            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}


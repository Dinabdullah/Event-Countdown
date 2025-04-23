package com.example.eventcountdown.data.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.room.Room
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.eventcountdown.presentation.activity.MainActivity
import com.example.eventcountdown.R
import com.example.eventcountdown.data.local.AppDatabase
import kotlinx.coroutines.runBlocking

class EventNotificationWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        Log.d("NotificationDebug", "Worker started")

        val eventId = inputData.getInt("EVENT_ID", -1)
        val eventTitle = inputData.getString("EVENT_TITLE") ?: run {
            Log.e("NotificationDebug", "Missing EVENT_TITLE in input data")
            return Result.failure()
        }

        Log.d("NotificationDebug", "Processing notification for event: $eventId, title: $eventTitle")

        if (eventId < 0) {
            Log.e("NotificationDebug", "Invalid event ID")
            return Result.failure()
        }

        try {
            createNotificationChannelIfNeeded(context)
            showNotification(eventId, eventTitle)
            Log.d("NotificationDebug", "Notification shown successfully")
            return Result.success()
        } catch (e: Exception) {
            Log.e("NotificationDebug", "Notification failed", e)
            return Result.failure()
        }
    }

    private fun showNotification(eventId: Int, eventTitle: String) {
        Log.d("NotificationDebug", "Building notification for event $eventId")

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("NAVIGATE_TO_EVENT_ID", eventId)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            eventId,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, "event_countdown_channel")
            .setSmallIcon(R.drawable.alarm)
            .setContentTitle("Event Time!")
            .setContentText("$eventTitle is happening now!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(eventId, notification)
        Log.d("NotificationDebug", "Notification posted to notificationManager")
    }

    private fun createNotificationChannelIfNeeded(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            if (notificationManager.getNotificationChannel("event_countdown_channel") == null) {
                Log.d("NotificationDebug", "Creating notification channel")
                val channel = NotificationChannel(
                    "event_countdown_channel",
                    "Event Countdown Notifications",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Notifications for when events are happening"
                    enableLights(true)
                    enableVibration(true)
                }
                notificationManager.createNotificationChannel(channel)
            } else {
                Log.d("NotificationDebug", "Notification channel already exists")
            }
        }
    }
}

package com.example.eventcountdown

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters

class EventNotificationWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        val eventId = inputData.getInt("EVENT_ID", -1)
        val eventTitle = inputData.getString("EVENT_TITLE") ?: "Event Time!"

        showNotification(eventId, eventTitle)

        return Result.success()
    }

    private fun showNotification(eventId: Int, eventTitle: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create intent to open the app when notification is clicked
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
    }
}
package com.example.eventcountdown.data.worker

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
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
        val eventId = inputData.getInt("EVENT_ID", -1)
        if (eventId < 0) return Result.success()

        // 1. Open your database
        val db = Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "event_database.db"
        )
            .fallbackToDestructiveMigration()
            .build()

        // 2. Check if the event still exists
        val event = runBlocking {
            db.eventDao().getEventById(eventId)
        }
        if (event == null) {
            // event was deleted — nothing to do
            return Result.success()
        }

        // 3. Fire the notification using the up‑to‑date title
        showNotification(eventId, event.title)
        return Result.success()
    }

    private fun showNotification(eventId: Int, eventTitle: String) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

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

package com.example.eventcountdown

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class EventViewModel(
    application: Application,
    private val eventDao: EventDao
) : AndroidViewModel(application)
{
    private val _events = MutableStateFlow<List<Event>>(emptyList())
    val events: StateFlow<List<Event>> = _events.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadEvents()
    }

    fun loadEvents() {
        viewModelScope.launch {
            eventDao.getAllEvents().collect { _events.value = it }
        }
    }

    fun addEvent(event: Event) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                eventDao.insert(event)
                scheduleNotification(event)
                loadEvents()
            } catch (e: Exception) {
                _error.value = "Failed to add event: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun scheduleNotification(event: Event) {
        val currentTime = System.currentTimeMillis()
        val eventTime = event.date.time

        // Only schedule if event is in the future
        if (eventTime > currentTime) {
            val delay = eventTime - currentTime

            val inputData = workDataOf(
                "EVENT_ID" to event.id,
                "EVENT_TITLE" to event.title
            )

            val notificationWork = OneTimeWorkRequestBuilder<EventNotificationWorker>()
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .setInputData(inputData)
                .addTag("event_notification_${event.id}")
                .build()

            WorkManager.getInstance(getApplication())
                .enqueueUniqueWork(
                    "event_notification_${event.id}",
                    ExistingWorkPolicy.REPLACE,
                    notificationWork
                )
        }
    }

    fun updateEvent(event: Event) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                eventDao.upsertNote(event)
                // Cancel existing notification and schedule new one
                cancelExistingNotification(event.id)
                scheduleNotification(event)
                loadEvents()
            } catch (e: Exception) {
                _error.value = "Failed to update event: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun cancelExistingNotification(eventId: Int) {
        WorkManager.getInstance(getApplication())
            .cancelUniqueWork("event_notification_$eventId")
    }

    fun deleteEvent(event: Event) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                eventDao.deleteNote(event)
                cancelExistingNotification(event.id)
                loadEvents()
            } catch (e: Exception) {
                _error.value = "Failed to delete event: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}

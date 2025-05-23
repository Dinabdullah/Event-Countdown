package com.example.eventcountdown.presentation

import android.app.Application
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.eventcountdown.data.remote.model.Holiday
import com.example.eventcountdown.data.repository.HolidayRepository
import com.example.eventcountdown.data.local.EventDao
import com.example.eventcountdown.data.worker.EventNotificationWorker
import com.example.eventcountdown.data.local.Event
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class EventViewModel(
    application: Application,
    private val eventDao: EventDao,
    private val holidayRepository: HolidayRepository
) : AndroidViewModel(application) {


    // Theme state management
    private val _isDarkTheme = mutableStateOf(false)
    val isDarkTheme: State<Boolean> = _isDarkTheme
    fun toggleTheme() {
        _isDarkTheme.value = !_isDarkTheme.value
    }

    private val _isAddingHolidays = MutableStateFlow(false)
    val isAddingHolidays: StateFlow<Boolean> = _isAddingHolidays.asStateFlow()

    fun autoCreateHolidayEvents() {
        viewModelScope.launch {
            try {
                _isAddingHolidays.value = true
                val existingEvents = eventDao.getAllEventsOnce()

                holidays.value.forEach { holiday ->
                    val date = try {
                        val calendar = Calendar.getInstance().apply {
                            time = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                .parse(holiday.date) ?: return@forEach
                            set(Calendar.HOUR_OF_DAY, 9)
                            set(Calendar.MINUTE, 0)
                            set(Calendar.SECOND, 0)
                            set(Calendar.MILLISECOND, 0)
                        }
                        calendar.time
                    } catch (e: Exception) {
                        return@forEach
                    }

                    // Check if event already exists
                    val isDuplicate = existingEvents.any {
                        it.title == holiday.name && it.date == date
                    }

                    if (!isDuplicate) {
                        val event = Event(
                            title = holiday.name,
                            description = "Public Holiday: ${holiday.localName}",
                            date = date,
                            color = Color.Blue.copy(0.7f).toArgb() // Different color for holidays
                        )
                        eventDao.insert(event)
                    }
                }

                loadEvents() // Refresh the event list
            } catch (e: Exception) {
                _error.value = "Failed to add holidays: ${e.message}"
            } finally {
                _isAddingHolidays.value = false
            }
        }
    }

    private val _holidays = MutableStateFlow<List<Holiday>>(emptyList())
    val holidays: StateFlow<List<Holiday>> = _holidays.asStateFlow()


    private val _events = MutableStateFlow<List<Event>>(emptyList())
    val events: StateFlow<List<Event>> = _events.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadEvents()
    }

    private fun loadEvents() {
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
        val delay = eventTime - currentTime

        Log.d("NotificationDebug", "Attempting to schedule notification for event: ${event.id}")
        Log.d("NotificationDebug", "Current time: $currentTime, Event time: $eventTime, Delay: $delay ms")

        if (eventTime > currentTime) {
            val inputData = workDataOf(
                "EVENT_ID" to event.id,
                "EVENT_TITLE" to event.title
            )

            Log.d("NotificationDebug", "Creating work request with data: $inputData")

            val notificationWork = OneTimeWorkRequestBuilder<EventNotificationWorker>()
                .setInputData(inputData)
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .addTag("event_notification_${event.id}")
                .build()

            WorkManager.getInstance(getApplication()).enqueueUniqueWork(
                "event_notification_${event.id}",
                ExistingWorkPolicy.REPLACE,
                notificationWork
            )

            Log.d("NotificationDebug", "Work enqueued with ID: ${notificationWork.id}")
        } else {
            Log.d("NotificationDebug", "Event time is in the past, not scheduling")
        }
    }

    fun updateEvent(event: Event) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                eventDao.upsertEvent(event)
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
                eventDao.deleteEvent(event)
                cancelExistingNotification(event.id)
                loadEvents()
            } catch (e: Exception) {
                _error.value = "Failed to delete event: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }


    private fun loadHolidays(year: Int = getCurrentYear(), countryCode: String = "EG") {
        viewModelScope.launch {
            try {
                _holidays.value = holidayRepository.getHolidays(year, countryCode)
                autoCreateHolidayEvents()
            } catch (e: Exception) {
                _error.value = "Holiday load failed: ${e.message}"
            }
        }
    }

    private fun getCurrentYear(): Int {
        return Calendar.getInstance().get(Calendar.YEAR)
    }

    init {
        loadEvents()
        loadHolidays()
    }

    fun testNotification() {
        viewModelScope.launch {
            val testEvent = Event(
                title = "Test Event",
                description = "Test Notification",
                date = Date(System.currentTimeMillis() + 5000), // 5 seconds from now
                color = Color.Blue.toArgb()
            )

            Log.d("NotificationDebug", "Scheduling test notification")
            scheduleNotification(testEvent)
        }
    }

}

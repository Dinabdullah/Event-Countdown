package com.example.eventcountdown

import android.app.Application
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.eventcountdown.api.Holiday
import com.example.eventcountdown.api.HolidayRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit

class EventViewModel(
    application: Application,
    private val eventDao: EventDao,
    private val holidayRepository: HolidayRepository
) : AndroidViewModel(application) {


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
                            color = Color.Green.toArgb() // Different color for holidays
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
                eventDao.upsertEvent(event)
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


    fun loadHolidays(year: Int = 2024, countryCode: String = "EG") {
        viewModelScope.launch {
            try {
                _holidays.value = holidayRepository.getHolidays(year, countryCode)
                autoCreateHolidayEvents() // Auto-create after loading
            } catch (e: Exception) {
                _error.value = "Holiday load failed: ${e.message}"
            }
        }
    }

    init {
        loadEvents()
        loadHolidays()
    }

}

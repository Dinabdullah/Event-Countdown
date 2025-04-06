package com.example.eventcountdown

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EventViewModel(private val eventDao: EventDao) : ViewModel() {
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
                loadEvents()
            } catch (e: Exception) {
                _error.value = "Failed to add event: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateEvent(event: Event) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                eventDao.upsertNote(event)
                loadEvents()
            } catch (e: Exception) {
                _error.value = "Failed to update event: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteEvent(event: Event) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                eventDao.deleteNote(event)
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

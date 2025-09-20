package com.example.planit.ui.view_model

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.planit.data.model.CalendarEvent
import com.example.planit.data.repository.CalendarRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
@RequiresApi(Build.VERSION_CODES.O)
class CalendarViewModel @Inject constructor(
    private val repository: CalendarRepository
) : ViewModel() {

    val eventList: StateFlow<List<CalendarEvent>> =
        repository.events.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    fun addEvent(event: CalendarEvent) = repository.addEvent(event)

    fun deleteEvent(eventId: String) = repository.deleteEvent(eventId)

    fun modifyEvent(eventID: String, updatedEvent: CalendarEvent) = repository.modifyEvent(eventID, updatedEvent)

    fun getTomorrowEvents() = repository.getTomorrowEvents()
}
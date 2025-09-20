package com.example.planit.data.repository

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.planit.data.model.CalendarEvent
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@RequiresApi(Build.VERSION_CODES.O)
class CalendarRepository @Inject constructor(
    private val db: FirebaseFirestore
) {
    private val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
    private val _events = MutableStateFlow<List<CalendarEvent>>(emptyList())
    val events: StateFlow<List<CalendarEvent>> = _events

    init {
        db.collection("CalendarEvents")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w("Firestore", "Listen failed.", e)
                    return@addSnapshotListener
                }

                val items = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        CalendarEvent(
                            id = doc.id,
                            title = doc.getString("title"),
                            start = doc.getString("start")
                                ?.let { LocalDateTime.parse(it, dateTimeFormatter) },
                            end = doc.getString("end")
                                ?.let { LocalDateTime.parse(it, dateTimeFormatter) },
                            remindMe = doc.getBoolean("remindMe") ?: false
                        )
                    } catch (ex: Exception) {
                        Log.w("Firestore", "Error parsing document ${doc.id}", ex)
                        null
                    }
                } ?: emptyList()

                _events.value = items
            }
    }

    fun addEvent(event: CalendarEvent) {
        val data = hashMapOf(
            "title" to event.title,
            "start" to event.start?.format(dateTimeFormatter),
            "end" to event.end?.format(dateTimeFormatter),
            "remindMe" to event.remindMe
        )
        db.collection("CalendarEvents")
            .get()
            .addOnSuccessListener { snapshot ->
                val maxId = snapshot.documents
                    .mapNotNull { it.id.toIntOrNull() }
                    .maxOrNull() ?: 0

                val newId = (maxId + 1).toString()

                db.collection("CalendarEvents")
                    .document(newId)
                    .set(data)
                    .addOnSuccessListener {
                        Log.d("Firestore", "Document added with ID: $newId")
                    }
                    .addOnFailureListener { e ->
                        Log.w("Firestore", "Error adding document", e)
                    }
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Error getting documents", e)
            }
    }

    fun deleteEvent(eventId: String) {
        db.collection("CalendarEvents").document(eventId).delete()
            .addOnSuccessListener {
                Log.d("Firestore", "Document $eventId successfully deleted!")
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Error deleting document", e)
            }
    }

    fun modifyEvent(eventId: String, updatedEvent: CalendarEvent) {
        val data = mapOf(
            "title" to updatedEvent.title,
            "start" to updatedEvent.start?.format(dateTimeFormatter),
            "end" to updatedEvent.end?.format(dateTimeFormatter),
            "remindMe" to updatedEvent.remindMe
        )

        db.collection("CalendarEvents")
            .document(eventId)
            .set(data)
            .addOnSuccessListener {
                Log.d("Firestore", "Document $eventId successfully updated!")
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Error updating document $eventId", e)
            }
    }

    fun getTomorrowEvents(): List<CalendarEvent> {
        val tomorrow = LocalDate.now().plusDays(1)
        val startOfTomorrow = tomorrow.atStartOfDay()
        val startOfDayAfter = tomorrow.plusDays(1).atStartOfDay()

        return _events.value.filter { event ->
            val start = event.start
            start != null && !start.isBefore(startOfTomorrow) && start.isBefore(startOfDayAfter)
        }
    }
}
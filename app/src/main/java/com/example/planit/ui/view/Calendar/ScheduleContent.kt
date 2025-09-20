package com.example.planit.ui.view.Calendar

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import com.example.planit.data.model.CalendarEvent
import java.time.LocalDate


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ScheduleContent(
    selectedDate: LocalDate,
    events: List<CalendarEvent>,
    onDaySelected: (LocalDate) -> Unit,
    onEventLongPress: (CalendarEvent) -> Unit
) {
    Column {
        WeekDayStrip(
            selectedDate = selectedDate,
            onDaySelected = onDaySelected
        )
        DayTimeline(
            date = selectedDate,
            events = events.filter { it.start?.toLocalDate() == selectedDate },
            onEventLongPress = onEventLongPress
        )
    }
}
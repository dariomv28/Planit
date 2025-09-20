package com.example.planit.ui.view.Calendar

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import com.example.planit.data.model.CalendarEvent

import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarContent(
    navController: NavHostController,
    eventList: List<CalendarEvent>,
    onAddEvent: () -> Unit,
    onEventLongPress: (CalendarEvent) -> Unit
) {
    var selectedTab by remember { mutableStateOf("Schedule") }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }

    Scaffold(
        topBar = {
            CalendarTopBar(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it },
                onClose = { navController.popBackStack() },
                onAddEvent = onAddEvent
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (selectedTab == "Schedule") {
                ScheduleContent(
                    selectedDate = selectedDate,
                    events = eventList,
                    onDaySelected = { item -> selectedDate = item },
                    onEventLongPress = onEventLongPress
                )
            } else {
                ReminderContent(events = eventList)
            }
        }
    }
}
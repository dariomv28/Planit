package com.example.planit.ui.view.Calendar

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.planit.R
import com.example.planit.data.model.CalendarEvent

import com.example.planit.ui.view_model.CalendarViewModel
import java.time.LocalDate

enum class CalendarScreens {
    CALENDAR, CREATE_EVENT
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarScreen(
    navController: NavHostController,
    calendarViewModel: CalendarViewModel = hiltViewModel()
) {
    var selectedScreen by remember { mutableStateOf(CalendarScreens.CALENDAR) }
    val eventList by calendarViewModel.eventList.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var createDate by remember { mutableStateOf<LocalDate?>(null) }
    var remainingEvents by remember { mutableStateOf(0) }
    var selectedEvent by remember { mutableStateOf<CalendarEvent?>(null) }
    var showEventOptionsDialog by remember { mutableStateOf(false) }
    var isModifyEvent by remember { mutableStateOf(false) }

    val context = LocalContext.current.applicationContext

    if (showEventOptionsDialog && selectedEvent != null) {
        Dialog(onDismissRequest = { showEventOptionsDialog = false }) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White)
                    .padding(20.dp)
                    .width(300.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        painter = painterResource(R.drawable.ic_info),
                        contentDescription = null,
                        tint = Color.Blue,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        "Choose an action",
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "What do you want to do with this event?",
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        val buttonShape = RoundedCornerShape(8.dp)
                        val buttonWidth = 120.dp
                        val buttonHeight = 48.dp

                        TextButton(
                            onClick = {
                                selectedEvent?.let { event ->
                                    event.id?.let { calendarViewModel.deleteEvent(it) }
                                }
                                showEventOptionsDialog = false
                            },
                            modifier = Modifier
                                .width(buttonWidth)
                                .height(buttonHeight)
                                .border(
                                    width = 1.dp,
                                    color = Color.Red,
                                    shape = buttonShape
                                ),
                            shape = buttonShape,
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text("Delete", color = Color.Red)
                        }

                        Button(
                            onClick = {
                                isModifyEvent = true
                                createDate = selectedEvent?.start?.toLocalDate()
                                selectedScreen = CalendarScreens.CREATE_EVENT
                                showEventOptionsDialog = false
                            },
                            modifier = Modifier
                                .width(buttonWidth)
                                .height(buttonHeight),
                            shape = buttonShape,
                            contentPadding = PaddingValues(0.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Blue)
                        ) {
                            Text("Edit", color = Color.White)
                        }
                    }
                }
            }
        }
    }

    AnimatedContent(
        targetState = selectedScreen,
        transitionSpec = {
            if (targetState.ordinal > initialState.ordinal) {
                slideInVertically { height -> height } + fadeIn(animationSpec = tween(300)) togetherWith
                        slideOutVertically { height -> -height } + fadeOut(animationSpec = tween(300))
            } else {
                slideInVertically { height -> -height } + fadeIn(animationSpec = tween(300)) togetherWith
                        slideOutVertically { height -> height } + fadeOut(animationSpec = tween(300))
            }
        },
        label = "CalendarTransition"
    ) { targetScreen ->
        when (targetScreen) {
            CalendarScreens.CALENDAR -> CalendarContent(
                navController,
                eventList = eventList,
                onAddEvent = { selectedScreen = CalendarScreens.CREATE_EVENT },
                onEventLongPress = { event ->
                    selectedEvent = event
                    showEventOptionsDialog = true
                }
            )
            CalendarScreens.CREATE_EVENT -> CreateEventContent(
                defaultDate = createDate,
                initialEvent = if (isModifyEvent) selectedEvent else null,
                onClose = {
                    selectedScreen = CalendarScreens.CALENDAR
                    isModifyEvent = false
                    selectedEvent = null
                },
                onSave = { event ->
                    if (!event.title.isNullOrBlank()) {
                        if (isModifyEvent && selectedEvent != null) {
                            selectedEvent?.id?.let { id ->
                                calendarViewModel.modifyEvent(id, event)
                            }
                        } else {
                            calendarViewModel.addEvent(event)
                        }
                        isModifyEvent = false
                        selectedEvent = null
                        selectedScreen = CalendarScreens.CALENDAR
                    }
                }
            )
        }
    }
}
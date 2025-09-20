package com.example.planit.ui.view.Calendar

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.planit.R
import com.example.planit.data.model.CalendarEvent
import com.example.planit.ensureExactAlarmPermission
import com.example.planit.scheduleReminder

import com.example.planit.ui.components.CustomToggleButton
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*
import kotlin.math.sin

import com.example.planit.ui.components.CustomToggleButton

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEventContent(
    defaultDate: LocalDate? = null,
    initialEvent: CalendarEvent? = null,
    onClose: () -> Unit,
    onSave: (CalendarEvent) -> Unit
) {
    val context = LocalContext.current

    var eventName by remember { mutableStateOf(initialEvent?.title ?: "") }
    var selectedDate by remember { mutableStateOf(initialEvent?.start?.toLocalDate() ?: defaultDate ?: LocalDate.now()) }
    var startTime by remember {
        mutableStateOf(
            initialEvent?.start?.toLocalTime()?.let { String.format("%02d:%02d", it.hour, it.minute) } ?: "00:00"
        )
    }
    var endTime by remember {
        mutableStateOf(
            initialEvent?.end?.toLocalTime()?.let { String.format("%02d:%02d", it.hour, it.minute) } ?: "00:00"
        )
    }
    var remindMe by remember { mutableStateOf(initialEvent?.remindMe ?: false) }

    fun showTimePicker(onTimeSelected: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        TimePickerDialog(
            context,
            R.style.CustomTimePickerTheme,
            { _, h, m -> onTimeSelected(String.format("%02d:%02d", h, m)) },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        ).show()
    }

    fun showDatePicker(onDateSelected: (LocalDate) -> Unit) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            context,
            R.style.CustomDatePickerTheme,
            { _, y, m, d -> onDateSelected(LocalDate.of(y, m + 1, d)) },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(onClick = onClose) {
                            Text("Cancel", color = Color(0xFFE88181), fontWeight = FontWeight.SemiBold)
                        }
                        Text(
                            if (initialEvent != null) "Edit Event" else "New Event",
                            color = Color(0xFFE88181),
                            fontWeight = FontWeight.Bold,
                            fontSize = 25.sp
                        )
                        TextButton(onClick = {
                            val (sh, sm) = startTime.split(":").map { it.toInt() }
                            val (eh, em) = endTime.split(":").map { it.toInt() }

                            val event = CalendarEvent(
                                title = eventName,
                                start = LocalDateTime.of(selectedDate, LocalTime.of(sh, sm)),
                                end = LocalDateTime.of(selectedDate, LocalTime.of(eh, em)),
                                remindMe = remindMe
                            )
                            val activity = context as? ComponentActivity
                            if (remindMe && (activity?.ensureExactAlarmPermission() == true)) {
                                scheduleReminder(context, event)
                            }
                            onSave(event)
                        }) {
                            Text(
                                if (initialEvent != null) "Save" else "Add",
                                color = Color(0xFFE88181),
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                TextField(
                    value = eventName,
                    onValueChange = { eventName = it },
                    label = { Text("Title", color = Color.Gray) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedContainerColor = Color(0xFFFFDDDD),
                        unfocusedContainerColor = Color(0xFFFFDDDD),
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Gray
                    )
                )

                OutlinedButton(
                    onClick = { showDatePicker { selectedDate = it } },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0xFFE88181))
                ) {
                    Icon(Icons.Default.DateRange, contentDescription = null, tint = Color(0xFFE88181))
                    Spacer(Modifier.width(8.dp))
                    Text("Date: $selectedDate", fontWeight = FontWeight.Medium, color = Color(0xFFE88181))
                }


                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = { showTimePicker { startTime = it } },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color(0xFFE88181))
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_time),
                            contentDescription = null,
                            tint = Color(0xFFE88181)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("Start: $startTime", fontWeight = FontWeight.Medium, color = Color(0xFFE88181))
                    }

                    OutlinedButton(
                        onClick = { showTimePicker { endTime = it } },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color(0xFFE88181))
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_time),
                            contentDescription = null,
                            tint = Color(0xFFE88181)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("End: $endTime", fontWeight = FontWeight.Medium, color = Color(0xFFE88181))
                    }
                }

                // Reminds me toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Reminds me", fontWeight = FontWeight.Medium, color = Color(0xFFE88181), fontSize = 18.sp)
                    CustomToggleButton(checked = remindMe, onCheckedChange = { remindMe = it })
                }
            }


        }
    }
}
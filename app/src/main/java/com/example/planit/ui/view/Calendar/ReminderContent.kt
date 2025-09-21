package com.example.planit.ui.view.Calendar

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.planit.R
import com.example.planit.data.model.CalendarEvent
import kotlinx.coroutines.delay
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter



@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ReminderContent(events: List<CalendarEvent>) {
    var now by remember { mutableStateOf(LocalDateTime.now()) }

    LaunchedEffect(Unit) {
        while (true) {
            now = LocalDateTime.now()
            delay(1_000)
        }
    }

    // Filter + sort
    val upcomingEvents = remember(events, now) {
        events.filter { it.start != null && it.start.isAfter(now) }
            .sortedBy { it.start }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(upcomingEvents) { event ->
            ReminderCard(event)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ReminderCard(event: CalendarEvent) {
    val start = event.start ?: return
    val end = event.end

    val formatterTime = DateTimeFormatter.ofPattern("HH:mm")
    val formatterDate = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy")

    var now by remember { mutableStateOf(LocalDateTime.now()) }

    // Update current time every second
    LaunchedEffect(Unit) {
        while (true) {
            now = LocalDateTime.now()
            delay(1_000)
        }
    }

    // Calculate remaining time
    val duration = remember(now, start) {
        java.time.Duration.between(now, start).takeIf { !it.isNegative }
    }
    val remainingText = duration?.let {
        val hours = it.toHours()
        val minutes = it.toMinutes() % 60
        when {
            hours > 0 -> "Starts in $hours h $minutes m"
            minutes > 0 -> "Starts in $minutes m"
            else -> "Starting soon"
        }
    } ?: "Ongoing"

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.Gray)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Calendar icon
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White.copy(alpha = 0.3f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_reminder),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.weight(1f)
        ) {
            // Title
            Text(
                text = event.title ?: "(No title)",
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                color = Color.White
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Time row
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(R.drawable.ic_time),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${start.format(formatterTime)} - ${end?.format(formatterTime) ?: "--:--"}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White
                )
            }

            // Date
            Text(
                text = start.format(formatterDate),
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.9f)
            )

            // Remaining time
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = remainingText,
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                color = Color.White
            )
        }
    }
}
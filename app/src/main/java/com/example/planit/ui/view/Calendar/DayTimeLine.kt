package com.example.planit.ui.view.Calendar

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.planit.data.model.CalendarEvent
import kotlinx.coroutines.delay
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlin.math.max

// ----------- Data class phụ để lưu thông tin event đã tính toán ----------
data class PositionedEvent(
    val event: CalendarEvent,
    val column: Int,
    val totalColumns: Int
)

// ----------- Thuật toán chia cột ----------
@RequiresApi(Build.VERSION_CODES.O)
fun arrangeEvents(events: List<CalendarEvent>): List<PositionedEvent> {
    if (events.isEmpty()) return emptyList()

    val sorted = events.sortedBy { it.start }
    val columns = mutableListOf<MutableList<CalendarEvent>>()
    val positioned = mutableListOf<PositionedEvent>()

    for (event in sorted) {
        var placed = false
        for ((index, col) in columns.withIndex()) {
            if (col.last().end!! <= event.start) {
                col.add(event)
                placed = true
                positioned.add(PositionedEvent(event, index, columns.size))
                break
            }
        }
        if (!placed) {
            columns.add(mutableListOf(event))
            positioned.add(PositionedEvent(event, columns.lastIndex, columns.size))
        }
    }

    // Tất cả event trong nhóm này sẽ có totalColumns = số cột thực tế
    return positioned.map { it.copy(totalColumns = columns.size) }
}

// ----------- UI ----------
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DayTimeline(
    date: LocalDate,
    events: List<CalendarEvent>,
    modifier: Modifier = Modifier,
    hourHeight: Dp = 80.dp,      // height per hour
    timeColumnWidth: Dp = 50.dp,  // width of left column
    onEventLongPress: (CalendarEvent) -> Unit
) {
    val hours = (0..23)
    val scrollState = rememberScrollState() // ✅ dùng chung 1 scrollState

    val minuteHeight = remember(hourHeight) { hourHeight / 60f }
    val totalHeight = hourHeight * 24f
    val density = LocalDensity.current

    val positionedEvents = arrangeEvents(events)

    Row(modifier = modifier.fillMaxSize().padding(top = 20.dp)) {

        // ---------- LEFT TIME COLUMN ----------
        Column(
            modifier = Modifier
                .width(timeColumnWidth)
                .verticalScroll(scrollState) // ✅ cùng scrollState với timeline
                .padding(top = 2.dp, start = 2.dp)
        ) {
            hours.forEach { h ->
                Box(
                    modifier = Modifier.height(hourHeight),
                    contentAlignment = Alignment.TopEnd
                ) {
                    Text(
                        text = "%02d:00".format(h),
                        color = Color.Gray,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
            }
        }

        // ---------- TIMELINE AREA ----------
        Box(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState) // ✅ dùng cùng scrollState
                .padding(vertical = 4.dp)
        ) {
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(totalHeight)
            ) {
                val containerWidth = maxWidth

                // 1) Hour lines
                val lineColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                Canvas(modifier = Modifier.matchParentSize()) {
                    val strokePx = with(density) { 1.dp.toPx() }
                    val w = size.width
                    for (i in 0..23) {
                        val y = (hourHeight * i).toPx()
                        drawLine(
                            color = lineColor,
                            start = Offset(0f, y),
                            end = Offset(w, y),
                            strokeWidth = strokePx
                        )
                    }
                }

                // 2) Render events
                positionedEvents.forEach { pe ->
                    val event = pe.event

                    val startMinutes = max(
                        0,
                        if (event.start?.toLocalDate() == date)
                            (event.start?.toLocalTime()?.hour ?: 0) * 60 +
                                    (event.start?.toLocalTime()?.minute ?: 0)
                        else 0
                    )

                    val endMinutes = minOf(
                        24 * 60,
                        if (event.end?.toLocalDate() == date)
                            (event.end?.toLocalTime()?.hour ?: 23) * 60 +
                                    (event.end?.toLocalTime()?.minute ?: 59)
                        else 24 * 60
                    )

                    val durationMin = max(1, endMinutes - startMinutes)

                    val topOffsetPx = with(density) { (minuteHeight * startMinutes).toPx() }
                    val heightPx = with(density) { (minuteHeight * durationMin).toPx() }

                    val bgColor = event.colorArgb?.let { Color(it) } ?: Color(0xFFFFB3B3)
                    val bgWithAlpha = bgColor.copy(alpha = 0.7f)

                    val widthFraction = 1f / pe.totalColumns
                    val xOffsetFraction = pe.column * widthFraction

                    val eventWidth = containerWidth * widthFraction
                    val eventXOffset = containerWidth * xOffsetFraction

                    Box(
                        modifier = Modifier
                            .offset {
                                IntOffset(
                                    x = with(density) { eventXOffset.roundToPx() },
                                    y = topOffsetPx.toInt()
                                )
                            }
                            .width(eventWidth)
                            .height(with(density) { heightPx.toDp() })
                            .clip(RoundedCornerShape(8.dp))
                            .background(bgWithAlpha)
                            .padding(8.dp)
                            .pointerInput(event){
                                detectTapGestures(
                                    onLongPress = {
                                        onEventLongPress(event)
                                    }
                                )
                            }
                    ) {
                        Column {
                            Text(
                                text = event.title ?: "(No title)",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                maxLines = 2
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            val startText = String.format("%02d:%02d", startMinutes / 60, startMinutes % 60)
                            val endText = String.format("%02d:%02d", endMinutes / 60, endMinutes % 60)
                            Text(
                                text = "$startText — $endText",
                                color = Color.White,
                                fontWeight = FontWeight.Medium,
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                // 3) Current time line
                if (date == LocalDate.now()) {
                    var now by remember { mutableStateOf(LocalTime.now()) }
                    LaunchedEffect(Unit) {
                        while (true) {
                            now = LocalTime.now()
                            delay(1_000)
                        }
                    }
                    val currentMinutes = now.hour * 60 + now.minute
                    val currentOffsetPx = with(density) { (minuteHeight * currentMinutes).toPx() }

                    Box(
                        modifier = Modifier
                            .offset { IntOffset(0, currentOffsetPx.toInt()) }
                            .fillMaxWidth()
                            .height(2.dp)
                            .background(Color.Red)
                    )

                    Box(
                        modifier = Modifier
                            .offset { IntOffset(0, currentOffsetPx.toInt() - 20) }
                            .padding(start = 8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFD9667B))
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = now.format(DateTimeFormatter.ofPattern("HH:mm")),
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
package com.example.planit.ui.view.Calendar

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.indication
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.ui.graphics.Color
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WeekDayStrip(
    selectedDate: LocalDate,
    onDaySelected: (LocalDate) -> Unit
) {
    val today = LocalDate.now()
    val pagerState = rememberPagerState(initialPage = 10000, pageCount = { 20000 })

    // ✅ chỉ tính tuần gốc 1 lần, tránh reset về tuần hiện tại
    val baseWeek = remember { selectedDate.with(DayOfWeek.MONDAY) }

    // Lấy ngày đầu tuần cho một page
    fun weekForPage(page: Int): List<LocalDate> {
        val offset = (page - 10000).toLong()
        val startOfWeek = baseWeek.plusWeeks(offset)
        return (0..6).map { startOfWeek.plusDays(it.toLong()) }
    }

    // Theo dõi selected weekday (thứ mấy)
    var selectedDayOfWeek by remember { mutableStateOf(selectedDate.dayOfWeek) }

    // Nếu user lướt sang page khác → giữ nguyên thứ (DayOfWeek) cho selectedDate
    LaunchedEffect(pagerState.currentPage) {
        val currentWeekDays = weekForPage(pagerState.currentPage)
        val sameWeekday = currentWeekDays.firstOrNull { it.dayOfWeek == selectedDayOfWeek }
        if (sameWeekday != null) {
            onDaySelected(sameWeekday)
        }
    }

    Column {
        // Hiển thị theo selectedDate
        val headerText = selectedDate.format(
            DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy")
        )

        Text(
            text = headerText,
            style = MaterialTheme.typography.titleMedium,
            color = Color.Black,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(vertical = 8.dp)
        )

        // Label thứ trong tuần
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun").forEach { label ->
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Black,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }

        // Pager cho từng tuần
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) { page ->
            val days = weekForPage(page)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                days.forEach { date ->
                    val isSelected = date == selectedDate
                    val isToday = date == today

                    Text(
                        text = String.format("%02d", date.dayOfMonth),
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
                        ),
                        color = when {
                            isSelected -> Color.White
                            isToday -> Color(0xFFFF5722)
                            else -> Color.Black
                        },
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(
                                if (isSelected) Color(0xFFFF5722) else Color.Transparent,
                                CircleShape
                            )
                            .clickable(
//                                interactionSource = remember { MutableInteractionSource() },
//                                indication = rememberRipple(bounded = false, radius = 20.dp)
                            ) {
                                selectedDayOfWeek = date.dayOfWeek
                                onDaySelected(date)
                            }
                            .padding(12.dp)
                    )
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun WeekDayStripPreview() {
    WeekDayStrip(
        selectedDate = LocalDate.now(),
        onDaySelected = {}
    )
}
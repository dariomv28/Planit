package com.example.planit.data.model

import java.time.LocalDateTime

data class CalendarEvent(
    val id: String? = null,
    val title: String? = null,
    val start: LocalDateTime? = null,
    val end: LocalDateTime? = null,
    val colorArgb: Int = 0,
    val remindMe: Boolean = false
)
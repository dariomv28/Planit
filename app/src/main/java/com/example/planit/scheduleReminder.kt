package com.example.planit

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.planit.data.model.CalendarEvent
import java.time.ZoneId

@SuppressLint("ServiceCast")
@RequiresApi(Build.VERSION_CODES.O)
fun scheduleReminder(context: Context, event: CalendarEvent) {
    Log.d("Alarm", "OK baby")

    if (!event.remindMe || event.start == null) return

    val triggerAtMillis = event.start.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    if (triggerAtMillis < System.currentTimeMillis()) return

    val intent = Intent(context, ReminderReceiver::class.java).apply {
        putExtra("title", event.title)
        putExtra("time", event.start.toLocalTime().toString())
    }

    val pendingIntent = PendingIntent.getBroadcast(
        context.applicationContext,
        event.start.hashCode(), // unique id cho mỗi event
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    // ✅ Đặt alarm với AlarmClockInfo để chắc chắn Android không kill
    alarmManager.setAlarmClock(
        AlarmManager.AlarmClockInfo(triggerAtMillis, pendingIntent),
        pendingIntent
    )

    Log.d("Alarm", "Scheduled at: $triggerAtMillis")
}


fun cancelReminder(context: Context, event: CalendarEvent) {
    val intent = Intent(context, ReminderReceiver::class.java)
    val pendingIntent = PendingIntent.getBroadcast(
        context.applicationContext,
        event.start.hashCode(),
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    alarmManager.cancel(pendingIntent)
}
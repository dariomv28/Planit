package com.example.planit

import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.ComponentActivity

/**
 * Trả về true nếu đã có quyền exact alarm.
 * Nếu chưa có, sẽ mở Settings để người dùng bật (Android 12+).
 */
fun ComponentActivity.ensureExactAlarmPermission(): Boolean {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) { // API 31+
        val am = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Nếu dùng USE_EXACT_ALARM (lịch/nhắc việc), đa số sẽ luôn true.
        if (!am.canScheduleExactAlarms()) {
            // Mở màn hình "Alarms & reminders"
            val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                data = Uri.parse("package:$packageName")
            }
            startActivity(intent)
            return false
        }
    }
    return true
}
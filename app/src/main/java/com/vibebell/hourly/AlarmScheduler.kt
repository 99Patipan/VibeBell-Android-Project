package com.vibebell.hourly

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import java.util.Calendar

object AlarmScheduler {
    private const val REQUEST_CODE = 4201

    fun cancel(context: Context) {
        val manager = context.getSystemService(AlarmManager::class.java)
        manager.cancel(pendingIntent(context))
    }

    fun scheduleNext(context: Context) {
        cancel(context)
        if (!Prefs.enabled(context)) return
        val next = nextOccurrence(context, System.currentTimeMillis()) ?: return
        val manager = context.getSystemService(AlarmManager::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !manager.canScheduleExactAlarms()) return
        manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, next, pendingIntent(context))
    }

    fun nextOccurrence(context: Context, afterMillis: Long): Long? {
        val start = Prefs.startMinute(context)
        val end = Prefs.endMinute(context)
        val cal = Calendar.getInstance().apply {
            timeInMillis = afterMillis
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            add(Calendar.HOUR_OF_DAY, 1)
        }
        // Search up to 8 days to cover overnight windows and all edge cases.
        repeat(24 * 8) {
            val hourMinute = cal.get(Calendar.HOUR_OF_DAY) * 60
            val inWindow = if (start == end) true
                else if (start < end) hourMinute >= start && hourMinute <= end
                else hourMinute >= start || hourMinute <= end
            if (inWindow) return cal.timeInMillis
            cal.add(Calendar.HOUR_OF_DAY, 1)
        }
        return null
    }

    private fun pendingIntent(context: Context): PendingIntent {
        val intent = Intent(context, HourlyAlarmReceiver::class.java)
        return PendingIntent.getBroadcast(
            context, REQUEST_CODE, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}

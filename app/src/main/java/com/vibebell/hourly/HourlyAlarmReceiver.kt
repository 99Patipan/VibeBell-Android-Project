package com.vibebell.hourly

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class HourlyAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (!Prefs.enabled(context)) return
        VibratorHelper.vibrateTriple(context)
        AlarmScheduler.scheduleNext(context)
    }
}

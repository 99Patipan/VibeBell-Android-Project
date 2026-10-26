package com.vibebell.hourly

import android.content.Context

object Prefs {
    private const val FILE = "vibe_bell_settings"
    private fun prefs(context: Context) = context.getSharedPreferences(FILE, Context.MODE_PRIVATE)

    fun enabled(context: Context) = prefs(context).getBoolean("enabled", false)
    fun setEnabled(context: Context, value: Boolean) { prefs(context).edit().putBoolean("enabled", value).apply() }
    fun startMinute(context: Context) = prefs(context).getInt("start_minute", 8 * 60)
    fun endMinute(context: Context) = prefs(context).getInt("end_minute", 22 * 60)
    fun setStartMinute(context: Context, value: Int) { prefs(context).edit().putInt("start_minute", value).apply() }
    fun setEndMinute(context: Context, value: Int) { prefs(context).edit().putInt("end_minute", value).apply() }
}

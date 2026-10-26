package com.vibebell.hourly

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

object VibratorHelper {
    fun vibrateTriple(context: Context) {
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            context.getSystemService(VibratorManager::class.java).defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
        if (!vibrator.hasVibrator()) return
        // Three short pulses; exact amplitude depends on device hardware and system settings.
        val timings = longArrayOf(0, 180, 140, 180, 140, 180)
        val amplitudes = intArrayOf(0, 255, 0, 255, 0, 255)
        vibrator.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
    }
}

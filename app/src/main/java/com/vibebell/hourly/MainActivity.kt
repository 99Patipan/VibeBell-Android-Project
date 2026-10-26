package com.vibebell.hourly

import android.app.Activity
import android.app.AlarmManager
import android.app.TimePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : Activity() {
    private lateinit var status: TextView
    private lateinit var nextTime: TextView
    private lateinit var enabledSwitch: Switch
    private lateinit var startButton: Button
    private lateinit var endButton: Button
    private var startMinute = 8 * 60
    private var endMinute = 22 * 60

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startMinute = Prefs.startMinute(this)
        endMinute = Prefs.endMinute(this)
        buildUi()
        refresh()
    }

    private fun buildUi() {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(dp(24), dp(28), dp(24), dp(24))
            setBackgroundColor(0xFF10141D.toInt())
        }
        fun text(value: String, size: Float, color: Int, bold: Boolean = false): TextView =
            TextView(this).apply {
                text = value
                textSize = size
                setTextColor(color)
                if (bold) setTypeface(null, android.graphics.Typeface.BOLD)
                gravity = Gravity.CENTER
                setPadding(0, dp(8), 0, dp(8))
            }
        root.addView(text("VIBE BELL", 28f, 0xFFFFFFFF.toInt(), true))
        root.addView(text("เตือนต้นชั่วโมงด้วยการสั่นเท่านั้น", 15f, 0xFFB8C1D1.toInt()))
        status = text("", 18f, 0xFFFFFFFF.toInt(), true)
        root.addView(status)
        nextTime = text("", 16f, 0xFFB8C1D1.toInt())
        root.addView(nextTime)

        enabledSwitch = Switch(this).apply {
            text = "เปิดการเตือนรายชั่วโมง"
            textSize = 16f
            setTextColor(0xFFFFFFFF.toInt())
            isChecked = Prefs.enabled(this@MainActivity)
            setPadding(0, dp(12), 0, dp(12))
            setOnCheckedChangeListener { _, checked ->
                Prefs.setEnabled(this@MainActivity, checked)
                if (checked) {
                    if (!ensureExactAlarmPermission()) {
                        isChecked = false
                        Prefs.setEnabled(this@MainActivity, false)
                        return@setOnCheckedChangeListener
                    }
                    AlarmScheduler.scheduleNext(this@MainActivity)
                } else {
                    AlarmScheduler.cancel(this@MainActivity)
                }
                refresh()
            }
        }
        root.addView(enabledSwitch)

        startButton = Button(this).apply {
            setOnClickListener { chooseTime(true) }
        }
        endButton = Button(this).apply {
            setOnClickListener { chooseTime(false) }
        }
        root.addView(startButton, matchWrap())
        root.addView(endButton, matchWrap())

        root.addView(Button(this).apply {
            text = "ทดลองสั่น 3 ครั้ง"
            setOnClickListener { VibratorHelper.vibrateTriple(this@MainActivity) }
        }, matchWrap())
        root.addView(Button(this).apply {
            text = "ตรวจสอบสิทธิ์ปลุกตรงเวลา"
            setOnClickListener { ensureExactAlarmPermission() }
        }, matchWrap())
        root.addView(text("แอปทำงานแบบออฟไลน์ ไม่มีเสียง และไม่ส่งข้อมูลออกจากเครื่อง", 12f, 0xFF8D98AA.toInt()))
        setContentView(root)
    }

    private fun chooseTime(isStart: Boolean) {
        val value = if (isStart) startMinute else endMinute
        TimePickerDialog(this, { _, hour, minute ->
            val selected = hour * 60 + minute
            if (isStart) {
                startMinute = selected
                Prefs.setStartMinute(this, selected)
            } else {
                endMinute = selected
                Prefs.setEndMinute(this, selected)
            }
            if (Prefs.enabled(this)) AlarmScheduler.scheduleNext(this)
            refresh()
        }, value / 60, value % 60, true).show()
    }

    private fun refresh() {
        status.text = if (Prefs.enabled(this)) "การเตือน: เปิด" else "การเตือน: ปิด"
        startButton.text = "เวลาเริ่ม: ${formatMinute(startMinute)}"
        endButton.text = "เวลาสิ้นสุด: ${formatMinute(endMinute)}"
        val next = AlarmScheduler.nextOccurrence(this, System.currentTimeMillis())
        nextTime.text = if (Prefs.enabled(this) && next != null) {
            "ครั้งถัดไป: ${SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(next))}"
        } else if (Prefs.enabled(this)) {
            "ไม่มีรอบเตือนในช่วงเวลาที่เลือก"
        } else {
            "เปิดการเตือนเพื่อดูเวลาครั้งถัดไป"
        }
    }

    private fun ensureExactAlarmPermission(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val manager = getSystemService(AlarmManager::class.java)
            if (!manager.canScheduleExactAlarms()) {
                Toast.makeText(this, "กรุณาอนุญาตให้แอปตั้งปลุกตรงเวลา", Toast.LENGTH_LONG).show()
                startActivity(Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM,
                    Uri.parse("package:$packageName")))
                return false
            }
        }
        return true
    }


    private fun formatMinute(value: Int) = String.format(Locale.getDefault(), "%02d:%02d", value / 60, value % 60)
    private fun dp(value: Int) = (value * resources.displayMetrics.density).toInt()
    private fun matchWrap() = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
        topMargin = dp(4)
        bottomMargin = dp(4)
    }
}

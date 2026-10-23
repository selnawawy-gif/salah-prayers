package com.example.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.model.PrayerType
import com.example.util.PrayerCalculator
import com.example.util.PreferencesManager
import java.util.Calendar

object PrayerAlarmScheduler {

    fun scheduleAllPrayers(context: Context) {
        val prefs = PreferencesManager.getInstance(context)
        val city = prefs.getUserCity()
        val calcMethod = prefs.getCalculationMethod()
        val juristicMethod = prefs.getJuristicMethod()
        val highLat = prefs.getHighLatitudeRule()
        val adjustments = prefs.getAllMinuteAdjustments()
        val preReminderEnabled = prefs.getPrePrayerReminder()
        val preReminderMinutes = prefs.getPrePrayerMinutes()

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Schedule for today and tomorrow
        for (dayOffset in 0..1) {
            val cal = Calendar.getInstance().apply {
                add(Calendar.DAY_OF_YEAR, dayOffset)
            }
            val times = PrayerCalculator.calculateTimes(
                calendar = cal,
                latitude = city.latitude,
                longitude = city.longitude,
                timezoneOffsetHours = city.timezoneOffsetHours,
                method = calcMethod,
                juristicMethod = juristicMethod,
                highLatRule = highLat,
                minuteAdjustments = adjustments
            )

            val prayerMap = mapOf(
                PrayerType.FAJR to times.fajr,
                PrayerType.SUNRISE to times.sunrise,
                PrayerType.DHUHR to times.dhuhr,
                PrayerType.ASR to times.asr,
                PrayerType.MAGHRIB to times.maghrib,
                PrayerType.ISHA to times.isha
            )

            val now = System.currentTimeMillis()

            prayerMap.forEach { (prayerType, timeMillis) ->
                // Exact prayer alarm
                if (timeMillis > now) {
                    val intent = Intent(context, PrayerAlarmReceiver::class.java).apply {
                        action = PrayerAlarmReceiver.ACTION_PRAYER_ALARM
                        putExtra(PrayerAlarmReceiver.EXTRA_PRAYER_TYPE, prayerType.id)
                        putExtra(PrayerAlarmReceiver.EXTRA_CITY_NAME, city.name)
                        putExtra(PrayerAlarmReceiver.EXTRA_TIME_MILLIS, timeMillis)
                    }

                    val requestCode = (dayOffset * 100) + prayerType.ordinal
                    val pendingIntent = PendingIntent.getBroadcast(
                        context,
                        requestCode,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )

                    scheduleExactOrInexact(alarmManager, timeMillis, pendingIntent)
                }

                // Pre-prayer reminder alarm (except for Sunrise)
                if (preReminderEnabled && prayerType != PrayerType.SUNRISE) {
                    val reminderTimeMillis = timeMillis - (preReminderMinutes * 60_000L)
                    if (reminderTimeMillis > now) {
                        val reminderIntent = Intent(context, PrayerAlarmReceiver::class.java).apply {
                            action = PrayerAlarmReceiver.ACTION_PRE_PRAYER_REMINDER
                            putExtra(PrayerAlarmReceiver.EXTRA_PRAYER_TYPE, prayerType.id)
                            putExtra(PrayerAlarmReceiver.EXTRA_MINUTES_REMAINING, preReminderMinutes)
                        }

                        val reminderRequestCode = (dayOffset * 100) + prayerType.ordinal + 10
                        val reminderPendingIntent = PendingIntent.getBroadcast(
                            context,
                            reminderRequestCode,
                            reminderIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                        )

                        scheduleExactOrInexact(alarmManager, reminderTimeMillis, reminderPendingIntent)
                    }
                }
            }
        }
    }

    private fun scheduleExactOrInexact(alarmManager: AlarmManager, timeMillis: Long, pendingIntent: PendingIntent) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeMillis, pendingIntent)
                } else {
                    alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeMillis, pendingIntent)
                }
            } else {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeMillis, pendingIntent)
            }
        } catch (e: Exception) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, timeMillis, pendingIntent)
        }
    }
}

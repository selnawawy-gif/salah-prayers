package com.example.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import com.example.data.local.PrayerRecordEntity
import com.example.data.local.SalahDatabase
import com.example.model.PrayerStatus
import com.example.model.PrayerType
import com.example.util.PreferencesManager
import com.example.widget.DailyVerseWidgetProvider
import com.example.widget.PrayerTimesWidgetProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PrayerAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action ?: return
        val prefs = PreferencesManager.getInstance(context)

        when (action) {
            ACTION_PRAYER_ALARM -> {
                val prayerId = intent.getStringExtra(EXTRA_PRAYER_TYPE) ?: return
                val prayerType = PrayerType.fromId(prayerId)
                val cityName = intent.getStringExtra(EXTRA_CITY_NAME) ?: prefs.getUserCity().name
                val timeMillis = intent.getLongExtra(EXTRA_TIME_MILLIS, System.currentTimeMillis())

                val timeFormatter = SimpleDateFormat(
                    if (prefs.getUse24Hour()) "HH:mm" else "h:mm a",
                    Locale.getDefault()
                )
                val formattedTime = timeFormatter.format(Date(timeMillis))

                val alertMode = prefs.getAlertMode(prayerType)

                // Check auto-silent mode during prayer
                val isAutoSilent = prefs.getAutoSilentDuringPrayer() && prayerType != PrayerType.SUNRISE
                if (isAutoSilent) {
                    val duration = prefs.getSilentDurationMinutes()
                    NotificationHelper.showSilentModeActiveNotification(context, duration, prayerType.displayName)
                }

                NotificationHelper.showPrayerNotification(
                    context = context,
                    prayerType = prayerType,
                    cityName = cityName,
                    formattedTime = formattedTime,
                    alertMode = alertMode
                )

                // Refresh widgets
                PrayerTimesWidgetProvider.updateAllWidgets(context)
            }

            ACTION_PRE_PRAYER_REMINDER -> {
                val prayerId = intent.getStringExtra(EXTRA_PRAYER_TYPE) ?: return
                val prayerType = PrayerType.fromId(prayerId)
                val minutesRemaining = intent.getIntExtra(EXTRA_MINUTES_REMAINING, 15)

                NotificationHelper.showPrePrayerReminder(context, prayerType, minutesRemaining)
            }

            ACTION_MARK_PRAYED -> {
                val prayerId = intent.getStringExtra(EXTRA_PRAYER_TYPE) ?: return
                val prayerType = PrayerType.fromId(prayerId)
                val todayDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

                CoroutineScope(Dispatchers.IO).launch {
                    val db = SalahDatabase.getInstance(context)
                    val dao = db.salahDao()
                    val existing = dao.getPrayerRecordForDate(todayDate) ?: PrayerRecordEntity(date = todayDate)

                    val updated = when (prayerType) {
                        PrayerType.FAJR -> existing.copy(fajrStatus = PrayerStatus.PRAYED_ON_TIME.name)
                        PrayerType.DHUHR -> existing.copy(dhuhrStatus = PrayerStatus.PRAYED_ON_TIME.name)
                        PrayerType.ASR -> existing.copy(asrStatus = PrayerStatus.PRAYED_ON_TIME.name)
                        PrayerType.MAGHRIB -> existing.copy(maghribStatus = PrayerStatus.PRAYED_ON_TIME.name)
                        PrayerType.ISHA -> existing.copy(ishaStatus = PrayerStatus.PRAYED_ON_TIME.name)
                        PrayerType.SUNRISE -> existing
                    }

                    dao.insertOrUpdatePrayerRecord(updated)
                    PrayerTimesWidgetProvider.updateAllWidgets(context)
                }
            }

            ACTION_END_SILENT_MODE -> {
                NotificationHelper.dismissSilentModeNotification(context)
            }
        }
    }

    companion object {
        const val ACTION_PRAYER_ALARM = "com.example.salah.ACTION_PRAYER_ALARM"
        const val ACTION_PRE_PRAYER_REMINDER = "com.example.salah.ACTION_PRE_PRAYER_REMINDER"
        const val ACTION_MARK_PRAYED = "com.example.salah.ACTION_MARK_PRAYED"
        const val ACTION_END_SILENT_MODE = "com.example.salah.ACTION_END_SILENT_MODE"

        const val EXTRA_PRAYER_TYPE = "extra_prayer_type"
        const val EXTRA_CITY_NAME = "extra_city_name"
        const val EXTRA_TIME_MILLIS = "extra_time_millis"
        const val EXTRA_MINUTES_REMAINING = "extra_minutes_remaining"
    }
}

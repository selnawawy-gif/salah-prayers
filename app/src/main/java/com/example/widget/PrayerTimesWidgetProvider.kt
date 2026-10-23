package com.example.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.MainActivity
import com.example.R
import com.example.data.local.SalahDatabase
import com.example.model.PrayerStatus
import com.example.model.PrayerType
import com.example.util.PrayerCalculator
import com.example.util.PreferencesManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class PrayerTimesWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        updateAllWidgets(context)
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == ACTION_REFRESH_PRAYER_WIDGET) {
            updateAllWidgets(context)
        }
    }

    companion object {
        const val ACTION_REFRESH_PRAYER_WIDGET = "com.example.salah.ACTION_REFRESH_PRAYER_WIDGET"

        fun updateAllWidgets(context: Context) {
            CoroutineScope(Dispatchers.IO).launch {
                val appWidgetManager = AppWidgetManager.getInstance(context)
                val thisWidget = ComponentName(context, PrayerTimesWidgetProvider::class.java)
                val appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget)
                if (appWidgetIds.isEmpty()) return@launch

                val prefs = PreferencesManager.getInstance(context)
                val city = prefs.getUserCity()
                val calcMethod = prefs.getCalculationMethod()
                val juristicMethod = prefs.getJuristicMethod()
                val highLat = prefs.getHighLatitudeRule()
                val adjustments = prefs.getAllMinuteAdjustments()
                val use24h = prefs.getUse24Hour()

                val now = System.currentTimeMillis()
                val todayCal = Calendar.getInstance()
                val times = PrayerCalculator.calculateTimes(
                    calendar = todayCal,
                    latitude = city.latitude,
                    longitude = city.longitude,
                    timezoneOffsetHours = city.timezoneOffsetHours,
                    method = calcMethod,
                    juristicMethod = juristicMethod,
                    highLatRule = highLat,
                    minuteAdjustments = adjustments
                )

                // Read logged prayers from DB
                val todayDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                val db = SalahDatabase.getInstance(context)
                val record = db.salahDao().getPrayerRecordForDate(todayDate)

                var completedCount = 0
                val prayerStatusMap = mapOf(
                    PrayerType.FAJR to (record?.fajrStatus ?: "NOT_LOGGED"),
                    PrayerType.DHUHR to (record?.dhuhrStatus ?: "NOT_LOGGED"),
                    PrayerType.ASR to (record?.asrStatus ?: "NOT_LOGGED"),
                    PrayerType.MAGHRIB to (record?.maghribStatus ?: "NOT_LOGGED"),
                    PrayerType.ISHA to (record?.ishaStatus ?: "NOT_LOGGED")
                )

                prayerStatusMap.values.forEach { status ->
                    if (status == PrayerStatus.PRAYED_ON_TIME.name ||
                        status == PrayerStatus.PRAYED_JAMAAH.name ||
                        status == PrayerStatus.PRAYED_LATE.name) {
                        completedCount++
                    }
                }

                // Format times
                val timeFormat = SimpleDateFormat(if (use24h) "HH:mm" else "h:mm a", Locale.getDefault())
                val gridFormat = SimpleDateFormat(if (use24h) "HH:mm" else "h:mm", Locale.getDefault())

                val prayers = listOf(
                    PrayerType.FAJR to times.fajr,
                    PrayerType.SUNRISE to times.sunrise,
                    PrayerType.DHUHR to times.dhuhr,
                    PrayerType.ASR to times.asr,
                    PrayerType.MAGHRIB to times.maghrib,
                    PrayerType.ISHA to times.isha
                )

                // Determine next prayer
                var nextPrayerType = PrayerType.FAJR
                var nextPrayerTimeMillis = times.fajr
                for ((type, millis) in prayers) {
                    if (millis > now) {
                        nextPrayerType = type
                        nextPrayerTimeMillis = millis
                        break
                    }
                }
                // If all passed, next is tomorrow Fajr
                if (nextPrayerTimeMillis <= now) {
                    val tomorrowCal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 1) }
                    val tomorrowTimes = PrayerCalculator.calculateTimes(
                        calendar = tomorrowCal,
                        latitude = city.latitude,
                        longitude = city.longitude,
                        timezoneOffsetHours = city.timezoneOffsetHours,
                        method = calcMethod,
                        juristicMethod = juristicMethod,
                        highLatRule = highLat,
                        minuteAdjustments = adjustments
                    )
                    nextPrayerType = PrayerType.FAJR
                    nextPrayerTimeMillis = tomorrowTimes.fajr
                }

                val diffMillis = maxOf(0L, nextPrayerTimeMillis - now)
                val hours = diffMillis / (1000 * 60 * 60)
                val minutes = (diffMillis / (1000 * 60)) % 60
                val countdownStr = if (hours > 0) "in ${hours}h ${minutes}m" else "in ${minutes}m"

                for (appWidgetId in appWidgetIds) {
                    val views = RemoteViews(context.packageName, R.layout.widget_prayer_times)

                    views.setTextViewText(R.id.widget_location, city.name)
                    views.setTextViewText(R.id.widget_next_prayer_name, "Next: ${nextPrayerType.displayName}")
                    views.setTextViewText(R.id.widget_next_prayer_countdown, countdownStr)
                    views.setTextViewText(R.id.widget_next_prayer_time, timeFormat.format(Date(nextPrayerTimeMillis)))

                    // Progress bar
                    val progressPercent = (completedCount * 20).coerceIn(0, 100)
                    views.setProgressBar(R.id.widget_progress_bar, 100, progressPercent, false)
                    views.setTextViewText(R.id.widget_progress_text, "$completedCount/5 Done")

                    // Individual times
                    views.setTextViewText(R.id.widget_fajr_time, gridFormat.format(Date(times.fajr)))
                    views.setTextViewText(R.id.widget_dhuhr_time, gridFormat.format(Date(times.dhuhr)))
                    views.setTextViewText(R.id.widget_asr_time, gridFormat.format(Date(times.asr)))
                    views.setTextViewText(R.id.widget_maghrib_time, gridFormat.format(Date(times.maghrib)))
                    views.setTextViewText(R.id.widget_isha_time, gridFormat.format(Date(times.isha)))

                    // Click to open main app
                    val openAppIntent = Intent(context, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    }
                    val openPendingIntent = PendingIntent.getActivity(
                        context,
                        0,
                        openAppIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                    views.setOnClickPendingIntent(R.id.widget_root, openPendingIntent)

                    appWidgetManager.updateAppWidget(appWidgetId, views)
                }
            }
        }
    }
}

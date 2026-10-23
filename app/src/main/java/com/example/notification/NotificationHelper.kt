package com.example.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R
import com.example.model.AlertMode
import com.example.model.PrayerType

object NotificationHelper {
    const val CHANNEL_PRAYER_ALERTS = "channel_prayer_alerts"
    const val CHANNEL_PRE_PRAYER = "channel_pre_prayer"
    const val CHANNEL_FOCUS_MODE = "channel_focus_mode"

    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION_EVENT)
                .build()

            // Prayer Alerts Channel
            val prayerChannel = NotificationChannel(
                CHANNEL_PRAYER_ALERTS,
                "Prayer Time Adhan & Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifies when it is time for each of the five daily prayers"
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 500, 250, 500)
                setSound(defaultSoundUri, audioAttributes)
            }

            // Pre-prayer reminders channel
            val prePrayerChannel = NotificationChannel(
                CHANNEL_PRE_PRAYER,
                "Pre-Prayer Reminders",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Gentle reminders 10 to 15 minutes before prayer time"
                enableVibration(true)
            }

            // Quiet Focus channel
            val focusChannel = NotificationChannel(
                CHANNEL_FOCUS_MODE,
                "Salah Silent & Focus Mode",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Status notification while quiet focus mode or auto-silent is active"
            }

            notificationManager.createNotificationChannels(listOf(prayerChannel, prePrayerChannel, focusChannel))
        }
    }

    fun showPrayerNotification(
        context: Context,
        prayerType: PrayerType,
        cityName: String,
        formattedTime: String,
        alertMode: AlertMode
    ) {
        if (alertMode == AlertMode.OFF) return

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("OPEN_PRAYER", prayerType.id)
        }
        val contentPendingIntent = PendingIntent.getActivity(
            context,
            prayerType.ordinal,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Action: Mark as Prayed
        val markPrayedIntent = Intent(context, PrayerAlarmReceiver::class.java).apply {
            action = PrayerAlarmReceiver.ACTION_MARK_PRAYED
            putExtra(PrayerAlarmReceiver.EXTRA_PRAYER_TYPE, prayerType.id)
        }
        val markPrayedPendingIntent = PendingIntent.getBroadcast(
            context,
            prayerType.ordinal + 100,
            markPrayedIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_PRAYER_ALERTS)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("🕌 Time for ${prayerType.displayName} (${prayerType.arabicName})")
            .setContentText("It is now prayer time in $cityName ($formattedTime). 'Hayya 'ala-s-Salah'.")
            .setStyle(NotificationCompat.BigTextStyle().bigText(
                "It is now time for ${prayerType.displayName} in $cityName ($formattedTime).\n" +
                "\"Indeed, prayer has been decreed upon the believers a decree of specified times.\""
            ))
            .setContentIntent(contentPendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .addAction(android.R.drawable.checkbox_on_background, "Mark Prayed", markPrayedPendingIntent)

        when (alertMode) {
            AlertMode.FULL_ADHAN, AlertMode.SHORT_TONE -> {
                builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                builder.setVibrate(longArrayOf(0, 500, 200, 500, 200, 500))
            }
            AlertMode.VIBRATE_ONLY -> {
                builder.setSound(null)
                builder.setVibrate(longArrayOf(0, 400, 200, 400))
            }
            AlertMode.SILENT_NOTIFICATION -> {
                builder.setSound(null)
                builder.setVibrate(null)
            }
            AlertMode.OFF -> return
        }

        notificationManager.notify(prayerType.ordinal + 1000, builder.build())
    }

    fun showPrePrayerReminder(
        context: Context,
        prayerType: PrayerType,
        minutesRemaining: Int
    ) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val contentPendingIntent = PendingIntent.getActivity(
            context,
            prayerType.ordinal + 500,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_PRE_PRAYER)
            .setSmallIcon(android.R.drawable.ic_popup_reminder)
            .setContentTitle("⏳ ${prayerType.displayName} in $minutesRemaining minutes")
            .setContentText("Prepare for ${prayerType.displayName} (${prayerType.arabicName}). Take wudu and find a quiet space.")
            .setContentIntent(contentPendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        notificationManager.notify(prayerType.ordinal + 2000, notification)
    }

    fun showSilentModeActiveNotification(context: Context, durationMinutes: Int, prayerName: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = NotificationCompat.Builder(context, CHANNEL_FOCUS_MODE)
            .setSmallIcon(android.R.drawable.ic_lock_silent_mode)
            .setContentTitle("🔕 Auto-Silent Mode Active")
            .setContentText("Quiet mode active for $durationMinutes minutes during $prayerName prayer.")
            .setOngoing(true)
            .setAutoCancel(false)
            .build()

        notificationManager.notify(9999, notification)
    }

    fun dismissSilentModeNotification(context: Context) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(9999)
    }
}

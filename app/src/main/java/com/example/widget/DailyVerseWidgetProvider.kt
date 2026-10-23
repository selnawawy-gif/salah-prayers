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
import com.example.data.QuranData
import com.example.data.local.SalahDatabase
import com.example.util.PreferencesManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DailyVerseWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        updateAllWidgets(context)
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == ACTION_RENEW_DAILY_VERSE) {
            val prefs = PreferencesManager.getInstance(context)
            prefs.incrementVerseIndex(QuranData.INITIAL_DAILY_VERSES.size)
            updateAllWidgets(context)
        }
    }

    companion object {
        const val ACTION_RENEW_DAILY_VERSE = "com.example.salah.ACTION_RENEW_DAILY_VERSE"

        fun updateAllWidgets(context: Context) {
            CoroutineScope(Dispatchers.IO).launch {
                val appWidgetManager = AppWidgetManager.getInstance(context)
                val thisWidget = ComponentName(context, DailyVerseWidgetProvider::class.java)
                val appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget)
                if (appWidgetIds.isEmpty()) return@launch

                val prefs = PreferencesManager.getInstance(context)
                val db = SalahDatabase.getInstance(context)

                // Get verses from DB or fallback to initial list
                val dbVerses = db.salahDao().getAllVerses()
                val verses = if (dbVerses.isNotEmpty()) {
                    dbVerses.map {
                        QuranData.INITIAL_DAILY_VERSES.firstOrNull { v -> v.id == it.id }
                            ?: com.example.model.DailyVerse(it.id, it.arabicText, it.translation, it.reference, it.theme, it.isFavorite)
                    }
                } else {
                    QuranData.INITIAL_DAILY_VERSES
                }

                val currentIndex = prefs.getCurrentVerseIndex().coerceIn(0, maxOf(0, verses.size - 1))
                val verse = verses[currentIndex]

                for (appWidgetId in appWidgetIds) {
                    val views = RemoteViews(context.packageName, R.layout.widget_daily_verse)

                    views.setTextViewText(R.id.widget_verse_arabic, verse.arabicText)
                    views.setTextViewText(R.id.widget_verse_translation, "\"${verse.translation}\"")
                    views.setTextViewText(R.id.widget_verse_reference, verse.reference)

                    // Renew button PendingIntent
                    val renewIntent = Intent(context, DailyVerseWidgetProvider::class.java).apply {
                        action = ACTION_RENEW_DAILY_VERSE
                    }
                    val renewPendingIntent = PendingIntent.getBroadcast(
                        context,
                        appWidgetId,
                        renewIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                    views.setOnClickPendingIntent(R.id.widget_btn_renew_verse, renewPendingIntent)

                    // Open Quran tab in app
                    val openAppIntent = Intent(context, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                        putExtra("OPEN_TAB", "quran")
                    }
                    val openPendingIntent = PendingIntent.getActivity(
                        context,
                        appWidgetId + 200,
                        openAppIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                    views.setOnClickPendingIntent(R.id.widget_verse_root, openPendingIntent)

                    appWidgetManager.updateAppWidget(appWidgetId, views)
                }
            }
        }
    }
}

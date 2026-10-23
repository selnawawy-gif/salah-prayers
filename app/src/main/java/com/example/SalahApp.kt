package com.example

import android.app.Application
import com.example.data.SalahRepository
import com.example.notification.NotificationHelper
import com.example.notification.PrayerAlarmScheduler
import com.example.widget.DailyVerseWidgetProvider
import com.example.widget.PrayerTimesWidgetProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SalahApp : Application() {

    lateinit var repository: SalahRepository
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this
        repository = SalahRepository(this)

        // Initialize Notification Channels
        NotificationHelper.createNotificationChannels(this)

        CoroutineScope(Dispatchers.IO).launch {
            repository.initializeSeedDataIfNeeded()
            PrayerAlarmScheduler.scheduleAllPrayers(this@SalahApp)
            PrayerTimesWidgetProvider.updateAllWidgets(this@SalahApp)
            DailyVerseWidgetProvider.updateAllWidgets(this@SalahApp)
        }
    }

    companion object {
        lateinit var instance: SalahApp
            private set
    }
}

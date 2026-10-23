package com.example.data

import android.content.Context
import com.example.data.local.DailyVerseEntity
import com.example.data.local.PrayerRecordEntity
import com.example.data.local.SalahDatabase
import com.example.data.local.SupplicationEntity
import com.example.data.local.SyncLogEntity
import com.example.model.DailyVerse
import com.example.model.PrayerSchedule
import com.example.model.PrayerStatus
import com.example.model.PrayerTimeItem
import com.example.model.PrayerType
import com.example.model.SupplicationItem
import com.example.notification.PrayerAlarmScheduler
import com.example.util.HijriCalendarHelper
import com.example.util.PrayerCalculator
import com.example.util.PreferencesManager
import com.example.widget.DailyVerseWidgetProvider
import com.example.widget.PrayerTimesWidgetProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class SalahRepository(private val context: Context) {
    private val database = SalahDatabase.getInstance(context)
    private val dao = database.salahDao()
    private val prefs = PreferencesManager.getInstance(context)

    suspend fun initializeSeedDataIfNeeded() = withContext(Dispatchers.IO) {
        val existingVerses = dao.getAllVerses()
        if (existingVerses.isEmpty()) {
            val entities = QuranData.INITIAL_DAILY_VERSES.map {
                DailyVerseEntity(
                    id = it.id,
                    arabicText = it.arabicText,
                    translation = it.translation,
                    reference = it.reference,
                    theme = it.theme,
                    isFavorite = it.isFavorite
                )
            }
            dao.insertVerses(entities)
        }

        val existingSupplications = dao.observeAllSupplications()
        // Seed supplications
        val initialSupps = SupplicationData.ITEMS.map {
            SupplicationEntity(
                id = it.id,
                category = it.category.name,
                title = it.title,
                arabicText = it.arabicText,
                translation = it.translation,
                transliteration = it.transliteration,
                reference = it.reference,
                targetCount = it.targetCount,
                virtue = it.virtue
            )
        }
        dao.insertSupplications(initialSupps)
    }

    /**
     * "Update Now" sync action:
     * - Refreshes database data
     * - Recalculates prayer times
     * - Re-schedules alarms
     * - Pushes instant updates to Home Screen Widgets
     * - Records sync log
     */
    suspend fun performSync(): SyncResult = withContext(Dispatchers.IO) {
        try {
            // Seed or update database
            initializeSeedDataIfNeeded()

            // Re-schedule alarms
            PrayerAlarmScheduler.scheduleAllPrayers(context)

            // Update both widgets
            PrayerTimesWidgetProvider.updateAllWidgets(context)
            DailyVerseWidgetProvider.updateAllWidgets(context)

            val now = System.currentTimeMillis()
            prefs.setLastSyncTimestamp(now)

            dao.insertSyncLog(
                SyncLogEntity(
                    syncTimestampMillis = now,
                    syncAction = "MANUAL_SYNC",
                    details = "Prayer times recalculated, alarms verified, and widgets updated for ${prefs.getUserCity().name}."
                )
            )

            SyncResult.Success(now)
        } catch (e: Exception) {
            SyncResult.Error(e.message ?: "Sync failed")
        }
    }

    fun observeLatestSyncLog(): Flow<SyncLogEntity?> = dao.observeLatestSyncLog()

    /**
     * Generates today's complete prayer schedule with next prayer detection and progress.
     */
    fun observeTodaySchedule(): Flow<PrayerSchedule> = flow {
        val todayDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        dao.observePrayerRecordForDate(todayDate).collect { record ->
            val city = prefs.getUserCity()
            val method = prefs.getCalculationMethod()
            val juristic = prefs.getJuristicMethod()
            val highLat = prefs.getHighLatitudeRule()
            val adjustments = prefs.getAllMinuteAdjustments()
            val use24h = prefs.getUse24Hour()

            val calendar = Calendar.getInstance()
            val times = PrayerCalculator.calculateTimes(
                calendar = calendar,
                latitude = city.latitude,
                longitude = city.longitude,
                timezoneOffsetHours = city.timezoneOffsetHours,
                method = method,
                juristicMethod = juristic,
                highLatRule = highLat,
                minuteAdjustments = adjustments
            )

            val now = System.currentTimeMillis()
            val timeFormatter = SimpleDateFormat(if (use24h) "HH:mm" else "h:mm a", Locale.getDefault())

            val rawPrayers = listOf(
                PrayerType.FAJR to times.fajr,
                PrayerType.SUNRISE to times.sunrise,
                PrayerType.DHUHR to times.dhuhr,
                PrayerType.ASR to times.asr,
                PrayerType.MAGHRIB to times.maghrib,
                PrayerType.ISHA to times.isha
            )

            val statusMap = mapOf(
                PrayerType.FAJR to parseStatus(record?.fajrStatus),
                PrayerType.DHUHR to parseStatus(record?.dhuhrStatus),
                PrayerType.ASR to parseStatus(record?.asrStatus),
                PrayerType.MAGHRIB to parseStatus(record?.maghribStatus),
                PrayerType.ISHA to parseStatus(record?.ishaStatus)
            )

            // Next prayer determination
            var nextIndex = -1
            for (i in rawPrayers.indices) {
                if (rawPrayers[i].second > now) {
                    nextIndex = i
                    break
                }
            }

            // Current prayer is the one before next
            val currentIndex = if (nextIndex > 0) nextIndex - 1 else if (nextIndex == 0) -1 else rawPrayers.lastIndex

            val items = rawPrayers.mapIndexed { index, (type, timeMillis) ->
                PrayerTimeItem(
                    type = type,
                    timeMillis = timeMillis,
                    formattedTime = timeFormatter.format(Date(timeMillis)),
                    isPast = timeMillis <= now,
                    isCurrent = index == currentIndex,
                    isNext = index == nextIndex,
                    status = statusMap[type] ?: PrayerStatus.NOT_LOGGED
                )
            }

            val nextItem = if (nextIndex >= 0) items[nextIndex] else items.first()
            val remainingMillis = if (nextIndex >= 0) {
                maxOf(0L, nextItem.timeMillis - now)
            } else {
                // Next is tomorrow Fajr
                val tomorrowCal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 1) }
                val tomTimes = PrayerCalculator.calculateTimes(
                    calendar = tomorrowCal,
                    latitude = city.latitude,
                    longitude = city.longitude,
                    timezoneOffsetHours = city.timezoneOffsetHours,
                    method = method,
                    juristicMethod = juristic,
                    highLatRule = highLat,
                    minuteAdjustments = adjustments
                )
                maxOf(0L, tomTimes.fajr - now)
            }

            // Calculate progress today (based on completed prayers)
            val obligatoryCount = PrayerType.getObligatoryPrayers().size
            var completedCount = 0
            statusMap.values.forEach { s ->
                if (s == PrayerStatus.PRAYED_ON_TIME || s == PrayerStatus.PRAYED_JAMAAH || s == PrayerStatus.PRAYED_LATE) {
                    completedCount++
                }
            }
            val progress = (completedCount.toFloat() / obligatoryCount.toFloat()).coerceIn(0f, 1f)

            val hijri = HijriCalendarHelper.getHijriDate(calendar)

            emit(
                PrayerSchedule(
                    dateString = todayDate,
                    hijriDate = "${hijri.formatEn()} • ${hijri.formatAr()}",
                    cityName = city.name,
                    items = items,
                    nextPrayer = nextItem,
                    nextPrayerRemainingMillis = remainingMillis,
                    progressToday = progress
                )
            )
        }
    }.flowOn(Dispatchers.IO)

    suspend fun updatePrayerStatus(dateString: String, prayerType: PrayerType, newStatus: PrayerStatus) = withContext(Dispatchers.IO) {
        val existing = dao.getPrayerRecordForDate(dateString) ?: PrayerRecordEntity(date = dateString)
        val updated = when (prayerType) {
            PrayerType.FAJR -> existing.copy(fajrStatus = newStatus.name, updatedAtMillis = System.currentTimeMillis())
            PrayerType.DHUHR -> existing.copy(dhuhrStatus = newStatus.name, updatedAtMillis = System.currentTimeMillis())
            PrayerType.ASR -> existing.copy(asrStatus = newStatus.name, updatedAtMillis = System.currentTimeMillis())
            PrayerType.MAGHRIB -> existing.copy(maghribStatus = newStatus.name, updatedAtMillis = System.currentTimeMillis())
            PrayerType.ISHA -> existing.copy(ishaStatus = newStatus.name, updatedAtMillis = System.currentTimeMillis())
            PrayerType.SUNRISE -> existing
        }
        dao.insertOrUpdatePrayerRecord(updated)
        PrayerTimesWidgetProvider.updateAllWidgets(context)
    }

    suspend fun getPrayerRecord(dateString: String): PrayerRecordEntity? = withContext(Dispatchers.IO) {
        dao.getPrayerRecordForDate(dateString)
    }

    fun observeRecentRecords(): Flow<List<PrayerRecordEntity>> = dao.observeRecentPrayerRecords()

    suspend fun calculateStreakAndStats(): PrayerStats = withContext(Dispatchers.IO) {
        val allRecords = dao.getAllPrayerRecords()
        if (allRecords.isEmpty()) return@withContext PrayerStats(0, 0, 0f)

        // Calculate consecutive streak (days where all 5 prayers are completed)
        val sorted = allRecords.sortedByDescending { it.date }
        var streak = 0
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val cal = Calendar.getInstance()

        for (record in sorted) {
            val isAllCompleted = isDayFullyCompleted(record)
            if (isAllCompleted) {
                streak++
            } else {
                // If today is incomplete, don't break streak yet if today is still in progress
                val todayStr = sdf.format(Date())
                if (record.date != todayStr) {
                    break
                }
            }
        }

        // 7-day adherence rate
        val past7 = sorted.take(7)
        var totalPrayersPossible = past7.size * 5
        var totalPrayersDone = 0
        past7.forEach { rec ->
            listOf(rec.fajrStatus, rec.dhuhrStatus, rec.asrStatus, rec.maghribStatus, rec.ishaStatus).forEach { st ->
                if (st == PrayerStatus.PRAYED_ON_TIME.name || st == PrayerStatus.PRAYED_JAMAAH.name || st == PrayerStatus.PRAYED_LATE.name) {
                    totalPrayersDone++
                }
            }
        }
        val rate = if (totalPrayersPossible > 0) (totalPrayersDone.toFloat() / totalPrayersPossible) * 100f else 0f

        PrayerStats(
            currentStreakDays = streak,
            totalLoggedDays = allRecords.size,
            weeklyAdherencePercent = rate
        )
    }

    private fun isDayFullyCompleted(record: PrayerRecordEntity): Boolean {
        val valid = setOf(PrayerStatus.PRAYED_ON_TIME.name, PrayerStatus.PRAYED_JAMAAH.name, PrayerStatus.PRAYED_LATE.name, PrayerStatus.EXCUSED.name)
        return record.fajrStatus in valid &&
                record.dhuhrStatus in valid &&
                record.asrStatus in valid &&
                record.maghribStatus in valid &&
                record.ishaStatus in valid
    }

    private fun parseStatus(str: String?): PrayerStatus {
        if (str == null) return PrayerStatus.NOT_LOGGED
        return try {
            PrayerStatus.valueOf(str)
        } catch (e: Exception) {
            PrayerStatus.NOT_LOGGED
        }
    }

    // Daily Verses
    fun observeDailyVerses(): Flow<List<DailyVerse>> = dao.observeAllVerses().map { list ->
        if (list.isEmpty()) QuranData.INITIAL_DAILY_VERSES
        else list.map { DailyVerse(it.id, it.arabicText, it.translation, it.reference, it.theme, it.isFavorite) }
    }

    suspend fun addCustomVerse(arabic: String, translation: String, reference: String, theme: String) = withContext(Dispatchers.IO) {
        val entity = DailyVerseEntity(
            id = "custom_v_${System.currentTimeMillis()}",
            arabicText = arabic,
            translation = translation,
            reference = reference,
            theme = theme,
            isCustomAdded = true
        )
        dao.insertVerse(entity)
        DailyVerseWidgetProvider.updateAllWidgets(context)
    }

    // Supplications
    fun observeSupplications(category: com.example.model.SupplicationCategory): Flow<List<SupplicationItem>> =
        dao.observeSupplicationsByCategory(category.name).map { list ->
            list.map {
                SupplicationItem(
                    id = it.id,
                    category = category,
                    title = it.title,
                    arabicText = it.arabicText,
                    translation = it.translation,
                    transliteration = it.transliteration,
                    reference = it.reference,
                    targetCount = it.targetCount,
                    virtue = it.virtue
                )
            }
        }
}

data class PrayerStats(
    val currentStreakDays: Int,
    val totalLoggedDays: Int,
    val weeklyAdherencePercent: Float
)

sealed class SyncResult {
    data class Success(val timestampMillis: Long) : SyncResult()
    data class Error(val message: String) : SyncResult()
}

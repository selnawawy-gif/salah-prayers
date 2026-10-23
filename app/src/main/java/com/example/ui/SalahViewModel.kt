package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.SalahApp
import com.example.data.PresetCities
import com.example.data.QuranData
import com.example.data.SalahRepository
import com.example.data.SyncResult
import com.example.data.local.PrayerRecordEntity
import com.example.model.*
import com.example.notification.PrayerAlarmScheduler
import com.example.sensor.CompassSensorManager
import com.example.util.PreferencesManager
import com.example.util.QiblaCalculator
import com.example.widget.DailyVerseWidgetProvider
import com.example.widget.PrayerTimesWidgetProvider
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class SalahScreen(val title: String, val icon: String) {
    PRAYER_TIMES("Prayer", "🕌"),
    QIBLA("Qibla", "🧭"),
    QURAN_DUAS("Quran & Duas", "📖"),
    HISTORY("Tracker", "📊"),
    SETTINGS("Settings", "⚙️")
}

class SalahViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = (application as SalahApp).repository
    val prefs = PreferencesManager.getInstance(application)
    val compassManager = CompassSensorManager(application)

    // Active Navigation Screen
    private val _currentScreen = MutableStateFlow(SalahScreen.PRAYER_TIMES)
    val currentScreen: StateFlow<SalahScreen> = _currentScreen.asStateFlow()

    // Sync State
    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    private val _syncMessage = MutableStateFlow<String?>(null)
    val syncMessage: StateFlow<String?> = _syncMessage.asStateFlow()

    // Prayer Schedule (updated every second for live countdown)
    private val _schedule = MutableStateFlow<PrayerSchedule?>(null)
    val schedule: StateFlow<PrayerSchedule?> = _schedule.asStateFlow()

    // Prayer History & Stats
    val recentRecords: StateFlow<List<PrayerRecordEntity>> = repository.observeRecentRecords()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _prayerStats = MutableStateFlow(com.example.data.PrayerStats(0, 0, 0f))
    val prayerStats: StateFlow<com.example.data.PrayerStats> = _prayerStats.asStateFlow()

    // Qibla state
    val compassHeading: StateFlow<Float> = compassManager.azimuthDegrees
    val compassAccuracy: StateFlow<Int> = compassManager.accuracy
    val isCompassAvailable: StateFlow<Boolean> = compassManager.isSensorAvailable

    private val _qiblaBearing = MutableStateFlow(0f)
    val qiblaBearing: StateFlow<Float> = _qiblaBearing.asStateFlow()

    private val _distanceToKaabaKm = MutableStateFlow(0)
    val distanceToKaabaKm: StateFlow<Int> = _distanceToKaabaKm.asStateFlow()

    // Quran & Duas
    val surahs = QuranData.SURAHS
    private val _selectedSurah = MutableStateFlow(QuranData.SURAHS.first())
    val selectedSurah: StateFlow<QuranSurah> = _selectedSurah.asStateFlow()

    private val _isPlayingAudio = MutableStateFlow(false)
    val isPlayingAudio: StateFlow<Boolean> = _isPlayingAudio.asStateFlow()

    private val _audioProgress = MutableStateFlow(0f)
    val audioProgress: StateFlow<Float> = _audioProgress.asStateFlow()

    private val _selectedSupplicationCategory = MutableStateFlow(SupplicationCategory.MORNING)
    val selectedSupplicationCategory: StateFlow<SupplicationCategory> = _selectedSupplicationCategory.asStateFlow()

    val supplications: StateFlow<List<SupplicationItem>> = _selectedSupplicationCategory
        .flatMapLatest { category -> repository.observeSupplications(category) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Interactive Tasbih Counter Map
    private val _tasbihCounts = MutableStateFlow<Map<String, Int>>(emptyMap())
    val tasbihCounts: StateFlow<Map<String, Int>> = _tasbihCounts.asStateFlow()

    // Daily Verses
    val dailyVerses: StateFlow<List<DailyVerse>> = repository.observeDailyVerses()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), QuranData.INITIAL_DAILY_VERSES)

    private val _isDetectingRegion = MutableStateFlow(false)
    val isDetectingRegion: StateFlow<Boolean> = _isDetectingRegion.asStateFlow()

    private var countdownTickerJob: Job? = null
    private var audioSimulationJob: Job? = null

    init {
        updateQiblaCalculations()
        startScheduleTicker()
        loadStats()

        if (prefs.getIsAutoLocation()) {
            autoDetectRegion()
        }

        // Observe compass changes to trigger haptics when aligned
        viewModelScope.launch {
            compassHeading.collect {
                compassManager.checkAndVibrateOnAlignment(_qiblaBearing.value)
            }
        }
    }

    fun autoDetectRegion(forceNotification: Boolean = false) {
        viewModelScope.launch {
            _isDetectingRegion.value = true
            try {
                val detection = com.example.util.RegionAutoDetector.detectRegion(getApplication())
                prefs.setUserCity(detection.city)
                prefs.setCalculationMethod(detection.recommendedMethod)
                prefs.setIsAutoLocation(true)

                updateQiblaCalculations()
                PrayerAlarmScheduler.scheduleAllPrayers(getApplication())
                PrayerTimesWidgetProvider.updateAllWidgets(getApplication())
                DailyVerseWidgetProvider.updateAllWidgets(getApplication())

                _syncMessage.value = "📍 Region detected: ${detection.city.name}, ${detection.city.country} (${detection.detectionSource})"
                delay(3500L)
                _syncMessage.value = null
            } catch (e: Exception) {
                if (forceNotification) {
                    _syncMessage.value = "Could not auto-detect: ${e.localizedMessage}"
                    delay(3000L)
                    _syncMessage.value = null
                }
            } finally {
                _isDetectingRegion.value = false
            }
        }
    }

    fun selectScreen(screen: SalahScreen) {
        _currentScreen.value = screen
        if (screen == SalahScreen.QIBLA) {
            compassManager.startListening()
        } else {
            compassManager.stopListening()
        }
    }

    private fun updateQiblaCalculations() {
        val city = prefs.getUserCity()
        val bearing = QiblaCalculator.calculateQiblaBearing(city.latitude, city.longitude)
        val dist = QiblaCalculator.calculateDistanceToKaabaKm(city.latitude, city.longitude)
        _qiblaBearing.value = bearing
        _distanceToKaabaKm.value = dist
    }

    private fun startScheduleTicker() {
        countdownTickerJob?.cancel()
        countdownTickerJob = viewModelScope.launch {
            while (isActive) {
                repository.observeTodaySchedule().take(1).collect { sched ->
                    _schedule.value = sched
                }
                delay(1000L) // Refresh countdown every second
            }
        }
    }

    fun refreshStats() {
        viewModelScope.launch {
            _prayerStats.value = repository.calculateStreakAndStats()
        }
    }

    private fun loadStats() {
        refreshStats()
    }

    /**
     * "Update Now" action: syncs database, re-runs calculations, updates widgets,
     * updates alarms and logs the sync event.
     */
    fun performManualSync() {
        viewModelScope.launch {
            _isSyncing.value = true
            _syncMessage.value = "Syncing Salah database and prayer schedules..."
            when (val result = repository.performSync()) {
                is SyncResult.Success -> {
                    updateQiblaCalculations()
                    refreshStats()
                    val timeStr = SimpleDateFormat("h:mm:ss a", Locale.getDefault()).format(Date(result.timestampMillis))
                    _syncMessage.value = "Updated successfully at $timeStr"
                }
                is SyncResult.Error -> {
                    _syncMessage.value = "Sync error: ${result.message}"
                }
            }
            delay(3500L)
            _isSyncing.value = false
            _syncMessage.value = null
        }
    }

    fun logPrayerStatus(dateString: String, prayerType: PrayerType, status: PrayerStatus) {
        viewModelScope.launch {
            repository.updatePrayerStatus(dateString, prayerType, status)
            refreshStats()
        }
    }

    fun selectSurah(surah: QuranSurah) {
        _selectedSurah.value = surah
        stopAudioSimulation()
    }

    fun toggleAudioSimulation() {
        if (_isPlayingAudio.value) {
            stopAudioSimulation()
        } else {
            startAudioSimulation()
        }
    }

    private fun startAudioSimulation() {
        _isPlayingAudio.value = true
        audioSimulationJob?.cancel()
        audioSimulationJob = viewModelScope.launch {
            var current = _audioProgress.value
            while (isActive && current < 1.0f) {
                delay(200L)
                current += 0.01f
                _audioProgress.value = current
            }
            _isPlayingAudio.value = false
            _audioProgress.value = 0f
        }
    }

    private fun stopAudioSimulation() {
        _isPlayingAudio.value = false
        audioSimulationJob?.cancel()
    }

    fun selectSupplicationCategory(category: SupplicationCategory) {
        _selectedSupplicationCategory.value = category
    }

    fun incrementTasbih(id: String, targetCount: Int) {
        val current = _tasbihCounts.value[id] ?: 0
        val next = if (current >= targetCount) 0 else current + 1
        _tasbihCounts.value = _tasbihCounts.value + (id to next)
    }

    fun resetTasbih(id: String) {
        _tasbihCounts.value = _tasbihCounts.value + (id to 0)
    }

    // Customization Settings updates
    fun selectCity(city: UserCity) {
        prefs.setIsAutoLocation(false)
        prefs.setUserCity(city)
        updateQiblaCalculations()
        PrayerAlarmScheduler.scheduleAllPrayers(getApplication())
        PrayerTimesWidgetProvider.updateAllWidgets(getApplication())
        DailyVerseWidgetProvider.updateAllWidgets(getApplication())
    }

    fun setCalculationMethod(method: CalculationMethod) {
        prefs.setCalculationMethod(method)
        PrayerAlarmScheduler.scheduleAllPrayers(getApplication())
        PrayerTimesWidgetProvider.updateAllWidgets(getApplication())
    }

    fun setJuristicMethod(method: JuristicMethod) {
        prefs.setJuristicMethod(method)
        PrayerAlarmScheduler.scheduleAllPrayers(getApplication())
        PrayerTimesWidgetProvider.updateAllWidgets(getApplication())
    }

    fun setThemePreset(preset: AppThemePreset) {
        prefs.setThemePreset(preset)
    }

    fun setDarkMode(mode: String) {
        prefs.setDarkMode(mode)
    }

    fun setThemeMode(mode: com.example.model.AppThemeMode) {
        prefs.setThemeMode(mode)
    }

    fun cycleThemeMode(): com.example.model.AppThemeMode {
        return prefs.cycleThemeMode()
    }

    fun set24HourClock(use24: Boolean) {
        prefs.setUse24Hour(use24)
        PrayerTimesWidgetProvider.updateAllWidgets(getApplication())
    }

    fun setPrayerAlertMode(prayer: PrayerType, mode: AlertMode) {
        prefs.setAlertMode(prayer, mode)
    }

    fun setAutoSilentDuringPrayer(enabled: Boolean) {
        prefs.setAutoSilentDuringPrayer(enabled)
    }

    fun setSilentDurationMinutes(minutes: Int) {
        prefs.setSilentDurationMinutes(minutes)
    }

    fun setPrePrayerReminder(enabled: Boolean) {
        prefs.setPrePrayerReminder(enabled)
        PrayerAlarmScheduler.scheduleAllPrayers(getApplication())
    }

    fun setPrePrayerMinutes(minutes: Int) {
        prefs.setPrePrayerMinutes(minutes)
        PrayerAlarmScheduler.scheduleAllPrayers(getApplication())
    }

    fun setQuietFocusMode(enabled: Boolean) {
        prefs.setQuietFocusMode(enabled)
    }

    fun setMinuteAdjustment(prayer: PrayerType, minutes: Int) {
        prefs.setMinuteAdjustment(prayer, minutes)
        PrayerAlarmScheduler.scheduleAllPrayers(getApplication())
        PrayerTimesWidgetProvider.updateAllWidgets(getApplication())
    }

    fun setAppLanguage(lang: com.example.model.AppLanguage) {
        prefs.setAppLanguage(lang)
    }

    override fun onCleared() {
        super.onCleared()
        compassManager.stopListening()
        countdownTickerJob?.cancel()
        audioSimulationJob?.cancel()
    }
}

package com.example.util

import android.content.Context
import android.content.SharedPreferences
import com.example.data.PresetCities
import com.example.model.AlertMode
import com.example.model.AppThemePreset
import com.example.model.CalculationMethod
import com.example.model.HighLatitudeRule
import com.example.model.JuristicMethod
import com.example.model.PrayerType
import com.example.model.UserCity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PreferencesManager(context: Context) {
    private val prefs: SharedPreferences =
        context.applicationContext.getSharedPreferences("salah_app_preferences", Context.MODE_PRIVATE)

    // Observable settings state flow
    private val _themePreset = MutableStateFlow(getThemePreset())
    val themePreset: StateFlow<AppThemePreset> = _themePreset.asStateFlow()

    private val _darkMode = MutableStateFlow(getDarkMode())
    val darkMode: StateFlow<String> = _darkMode.asStateFlow() // "system", "light", "dark"

    private val _use24Hour = MutableStateFlow(getUse24Hour())
    val use24Hour: StateFlow<Boolean> = _use24Hour.asStateFlow()

    private val _userCity = MutableStateFlow(getUserCity())
    val userCity: StateFlow<UserCity> = _userCity.asStateFlow()

    private val _calculationMethod = MutableStateFlow(getCalculationMethod())
    val calculationMethod: StateFlow<CalculationMethod> = _calculationMethod.asStateFlow()

    private val _juristicMethod = MutableStateFlow(getJuristicMethod())
    val juristicMethod: StateFlow<JuristicMethod> = _juristicMethod.asStateFlow()

    private val _autoSilentEnabled = MutableStateFlow(getAutoSilentDuringPrayer())
    val autoSilentEnabled: StateFlow<Boolean> = _autoSilentEnabled.asStateFlow()

    private val _quietFocusModeEnabled = MutableStateFlow(getQuietFocusMode())
    val quietFocusModeEnabled: StateFlow<Boolean> = _quietFocusModeEnabled.asStateFlow()

    // City & Location
    fun getUserCity(): UserCity {
        val name = prefs.getString("city_name", PresetCities.DEFAULT_CITY.name) ?: PresetCities.DEFAULT_CITY.name
        val country = prefs.getString("city_country", PresetCities.DEFAULT_CITY.country) ?: PresetCities.DEFAULT_CITY.country
        val lat = prefs.getFloat("city_lat", PresetCities.DEFAULT_CITY.latitude.toFloat()).toDouble()
        val lng = prefs.getFloat("city_lng", PresetCities.DEFAULT_CITY.longitude.toFloat()).toDouble()
        val tz = prefs.getFloat("city_tz", PresetCities.DEFAULT_CITY.timezoneOffsetHours.toFloat()).toDouble()
        return UserCity(name, country, lat, lng, tz)
    }

    fun setUserCity(city: UserCity) {
        prefs.edit()
            .putString("city_name", city.name)
            .putString("city_country", city.country)
            .putFloat("city_lat", city.latitude.toFloat())
            .putFloat("city_lng", city.longitude.toFloat())
            .putFloat("city_tz", city.timezoneOffsetHours.toFloat())
            .apply()
        _userCity.value = city
    }

    // Calculation Method
    fun getCalculationMethod(): CalculationMethod {
        val id = prefs.getString("calc_method", CalculationMethod.MUSLIM_WORLD_LEAGUE.id) ?: CalculationMethod.MUSLIM_WORLD_LEAGUE.id
        return CalculationMethod.fromId(id)
    }

    fun setCalculationMethod(method: CalculationMethod) {
        prefs.edit().putString("calc_method", method.id).apply()
        _calculationMethod.value = method
    }

    // Juristic Method
    fun getJuristicMethod(): JuristicMethod {
        val ord = prefs.getInt("juristic_method", JuristicMethod.STANDARD.ordinal)
        return JuristicMethod.fromOrdinal(ord)
    }

    fun setJuristicMethod(method: JuristicMethod) {
        prefs.edit().putInt("juristic_method", method.ordinal).apply()
        _juristicMethod.value = method
    }

    // High Latitude Rule
    fun getHighLatitudeRule(): HighLatitudeRule {
        val ord = prefs.getInt("high_lat_rule", HighLatitudeRule.ANGLE_BASED.ordinal)
        return HighLatitudeRule.fromOrdinal(ord)
    }

    fun setHighLatitudeRule(rule: HighLatitudeRule) {
        prefs.edit().putInt("high_lat_rule", rule.ordinal).apply()
    }

    // Theme Preset
    fun getThemePreset(): AppThemePreset {
        val name = prefs.getString("app_theme", AppThemePreset.EMERALD.name) ?: AppThemePreset.EMERALD.name
        return try {
            AppThemePreset.valueOf(name)
        } catch (e: Exception) {
            AppThemePreset.EMERALD
        }
    }

    fun setThemePreset(preset: AppThemePreset) {
        prefs.edit().putString("app_theme", preset.name).apply()
        _themePreset.value = preset
    }

    // Dark Mode: "system", "light", "dark"
    fun getDarkMode(): String = prefs.getString("dark_mode", "system") ?: "system"

    fun setDarkMode(mode: String) {
        prefs.edit().putString("dark_mode", mode).apply()
        _darkMode.value = mode
    }

    // 24 Hour clock
    fun getUse24Hour(): Boolean = prefs.getBoolean("use_24_hour", false)

    fun setUse24Hour(value: Boolean) {
        prefs.edit().putBoolean("use_24_hour", value).apply()
        _use24Hour.value = value
    }

    // Per-Prayer Alert Modes
    fun getAlertMode(prayer: PrayerType): AlertMode {
        val defaultMode = if (prayer == PrayerType.SUNRISE) AlertMode.SILENT_NOTIFICATION.name else AlertMode.FULL_ADHAN.name
        val name = prefs.getString("alert_mode_${prayer.id}", defaultMode) ?: defaultMode
        return try {
            AlertMode.valueOf(name)
        } catch (e: Exception) {
            AlertMode.FULL_ADHAN
        }
    }

    fun setAlertMode(prayer: PrayerType, mode: AlertMode) {
        prefs.edit().putString("alert_mode_${prayer.id}", mode.name).apply()
    }

    // Pre-Prayer Reminder (e.g. 15 minutes before)
    fun getPrePrayerReminder(): Boolean = prefs.getBoolean("pre_prayer_reminder", true)
    fun setPrePrayerReminder(enabled: Boolean) {
        prefs.edit().putBoolean("pre_prayer_reminder", enabled).apply()
    }

    fun getPrePrayerMinutes(): Int = prefs.getInt("pre_prayer_minutes", 15)
    fun setPrePrayerMinutes(minutes: Int) {
        prefs.edit().putInt("pre_prayer_minutes", minutes).apply()
    }

    // Auto-Silent during Salah
    fun getAutoSilentDuringPrayer(): Boolean = prefs.getBoolean("auto_silent_salah", true)
    fun setAutoSilentDuringPrayer(enabled: Boolean) {
        prefs.edit().putBoolean("auto_silent_salah", enabled).apply()
        _autoSilentEnabled.value = enabled
    }

    fun getSilentDurationMinutes(): Int = prefs.getInt("silent_duration_minutes", 20)
    fun setSilentDurationMinutes(minutes: Int) {
        prefs.edit().putInt("silent_duration_minutes", minutes).apply()
    }

    // Quiet Focus Mode
    fun getQuietFocusMode(): Boolean = prefs.getBoolean("quiet_focus_mode", false)
    fun setQuietFocusMode(enabled: Boolean) {
        prefs.edit().putBoolean("quiet_focus_mode", enabled).apply()
        _quietFocusModeEnabled.value = enabled
    }

    fun getQuietFocusStartHour(): Int = prefs.getInt("quiet_focus_start", 22) // 10 PM
    fun getQuietFocusEndHour(): Int = prefs.getInt("quiet_focus_end", 6) // 6 AM
    fun setQuietFocusHours(startHour: Int, endHour: Int) {
        prefs.edit()
            .putInt("quiet_focus_start", startHour)
            .putInt("quiet_focus_end", endHour)
            .apply()
    }

    // Minute adjustments per prayer
    fun getMinuteAdjustment(prayer: PrayerType): Int {
        return prefs.getInt("adj_${prayer.id}", 0)
    }

    fun setMinuteAdjustment(prayer: PrayerType, minutes: Int) {
        prefs.edit().putInt("adj_${prayer.id}", minutes).apply()
    }

    fun getAllMinuteAdjustments(): Map<PrayerType, Int> {
        return PrayerType.values().associateWith { getMinuteAdjustment(it) }
    }

    // Daily Verse index for widget renewable cycle
    fun getCurrentVerseIndex(): Int = prefs.getInt("current_daily_verse_index", 0)
    fun incrementVerseIndex(maxCount: Int): Int {
        val next = (getCurrentVerseIndex() + 1) % maxOf(1, maxCount)
        prefs.edit().putInt("current_daily_verse_index", next).apply()
        return next
    }

    // Last Sync timestamp
    fun getLastSyncTimestamp(): Long = prefs.getLong("last_sync_timestamp", 0L)
    fun setLastSyncTimestamp(time: Long) {
        prefs.edit().putLong("last_sync_timestamp", time).apply()
    }

    companion object {
        @Volatile
        private var INSTANCE: PreferencesManager? = null

        fun getInstance(context: Context): PreferencesManager {
            return INSTANCE ?: synchronized(this) {
                val instance = PreferencesManager(context)
                INSTANCE = instance
                instance
            }
        }
    }
}

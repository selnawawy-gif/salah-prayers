package com.example.model

enum class PrayerType(
    val id: String,
    val displayName: String,
    val arabicName: String,
    val isObligatory: Boolean
) {
    FAJR("fajr", "Fajr", "الفجر", true),
    SUNRISE("sunrise", "Sunrise", "الشروق", false),
    DHUHR("dhuhr", "Dhuhr", "الظهر", true),
    ASR("asr", "Asr", "العصر", true),
    MAGHRIB("maghrib", "Maghrib", "المغرب", true),
    ISHA("isha", "Isha", "العشاء", true);

    companion object {
        fun getObligatoryPrayers(): List<PrayerType> = listOf(FAJR, DHUHR, ASR, MAGHRIB, ISHA)
        fun fromId(id: String): PrayerType = values().firstOrNull { it.id.equals(id, ignoreCase = true) } ?: FAJR
    }
}

enum class PrayerStatus(val label: String, val arabicLabel: String, val colorHex: Long) {
    NOT_LOGGED("Not Logged", "غير مسجل", 0xFF718096),
    PRAYED_ON_TIME("On Time", "حاضر", 0xFF2E7D32),
    PRAYED_JAMAAH("In Jama'ah", "جماعة", 0xFF0D9488),
    PRAYED_LATE("Late / Qada", "قضاء", 0xFFD97706),
    MISSED("Missed", "فائتة", 0xFFDC2626),
    EXCUSED("Excused", "عذر", 0xFF6B7280)
}

enum class CalculationMethod(
    val id: String,
    val title: String,
    val fajrAngle: Double,
    val ishaAngle: Double,
    val ishaMinutesAfterMaghrib: Int? = null
) {
    MUSLIM_WORLD_LEAGUE("mwl", "Muslim World League", 18.0, 17.0),
    ISNA("isna", "ISNA (North America)", 15.0, 15.0),
    EGYPT("egypt", "Egyptian General Authority", 19.5, 17.5),
    UMM_AL_QURA("makkah", "Umm Al-Qura (Makkah)", 18.5, 0.0, ishaMinutesAfterMaghrib = 90),
    KARACHI("karachi", "Univ. of Islamic Sciences, Karachi", 18.0, 18.0),
    DUBAI("dubai", "UAE / Dubai Awqaf", 18.2, 18.2),
    TEHRAN("tehran", "Institute of Geophysics, Tehran", 17.7, 14.0);

    companion object {
        fun fromId(id: String): CalculationMethod =
            values().firstOrNull { it.id.equals(id, ignoreCase = true) } ?: MUSLIM_WORLD_LEAGUE
    }
}

enum class JuristicMethod(val title: String, val shadowFactor: Double) {
    STANDARD("Shafi'i, Maliki, Hanbali (Shadow 1x)", 1.0),
    HANAFI("Hanafi (Shadow 2x)", 2.0);

    companion object {
        fun fromOrdinal(ord: Int): JuristicMethod =
            values().getOrNull(ord) ?: STANDARD
    }
}

enum class HighLatitudeRule(val title: String) {
    ANGLE_BASED("Angle-based fraction"),
    MIDDLE_OF_NIGHT("Middle of the night"),
    SEVENTH_OF_NIGHT("One-seventh of the night");

    companion object {
        fun fromOrdinal(ord: Int): HighLatitudeRule =
            values().getOrNull(ord) ?: ANGLE_BASED
    }
}

enum class AppThemePreset(val displayName: String, val primaryHex: Long, val secondaryHex: Long) {
    EMERALD("Emerald Sanctuary", 0xFF0D5C3A, 0xFFD4AF37),
    ROYAL_GOLD("Royal Mosque", 0xFF1B3B2B, 0xFFE5B842),
    MIDNIGHT_BLUE("Midnight Kaaba", 0xFF132742, 0xFF38BDF8),
    DESERT_ROSE("Desert Twilight", 0xFF7C2D3A, 0xFFF59E0B),
    FOREST_SERENITY("Forest Oasis", 0xFF164E43, 0xFF10B981)
}

enum class AlertMode(val title: String) {
    FULL_ADHAN("Full Adhan"),
    SHORT_TONE("Short Beep/Takbir"),
    VIBRATE_ONLY("Vibrate Only"),
    SILENT_NOTIFICATION("Silent Alert"),
    OFF("Off")
}

data class PrayerTimeItem(
    val type: PrayerType,
    val timeMillis: Long,
    val formattedTime: String,
    val isPast: Boolean,
    val isCurrent: Boolean,
    val isNext: Boolean,
    val status: PrayerStatus = PrayerStatus.NOT_LOGGED
)

data class PrayerSchedule(
    val dateString: String, // YYYY-MM-DD
    val hijriDate: String,
    val cityName: String,
    val items: List<PrayerTimeItem>,
    val nextPrayer: PrayerTimeItem?,
    val nextPrayerRemainingMillis: Long,
    val progressToday: Float // 0.0 to 1.0
)

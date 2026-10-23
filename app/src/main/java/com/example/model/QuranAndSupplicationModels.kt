package com.example.model

data class QuranAyah(
    val numberInSurah: Int,
    val arabicText: String,
    val translation: String,
    val transliteration: String = "",
    val revelationContext: String = ""
)

data class QuranSurah(
    val number: Int,
    val englishName: String,
    val arabicName: String,
    val englishTranslation: String,
    val totalVerses: Int,
    val revelationType: String, // "Meccan" or "Medinan"
    val ayahs: List<QuranAyah>,
    val audioDurationSeconds: Int = 180
)

data class DailyVerse(
    val id: String,
    val arabicText: String,
    val translation: String,
    val reference: String,
    val theme: String,
    val isFavorite: Boolean = false
)

enum class SupplicationCategory(val title: String, val icon: String) {
    MORNING("Morning Adhkar", "🌅"),
    EVENING("Evening Adhkar", "🌇"),
    AFTER_PRAYER("After Prayer", "🕌"),
    DAILY_DUAS("Daily Supplications", "🤲"),
    SLEEP("Before Sleep", "🌙")
}

data class SupplicationItem(
    val id: String,
    val category: SupplicationCategory,
    val title: String,
    val arabicText: String,
    val translation: String,
    val transliteration: String,
    val reference: String,
    val targetCount: Int = 1,
    val virtue: String = ""
)

data class UserCity(
    val name: String,
    val country: String,
    val latitude: Double,
    val longitude: Double,
    val timezoneOffsetHours: Double
)

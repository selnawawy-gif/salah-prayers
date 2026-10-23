package com.example

import com.example.model.CalculationMethod
import com.example.model.JuristicMethod
import com.example.model.PrayerType
import com.example.util.HijriCalendarHelper
import com.example.util.PrayerCalculator
import com.example.util.QiblaCalculator
import org.junit.Assert.*
import org.junit.Test
import java.util.Calendar

class ExampleUnitTest {
    @Test
    fun prayerCalculator_calculatesChronologicalTimes() {
        val calendar = Calendar.getInstance().apply {
            set(2026, Calendar.OCTOBER, 23, 12, 0, 0)
        }
        val times = PrayerCalculator.calculateTimes(
            calendar = calendar,
            latitude = 21.4225, // Makkah
            longitude = 39.8262,
            timezoneOffsetHours = 3.0,
            method = CalculationMethod.UMM_AL_QURA,
            juristicMethod = JuristicMethod.STANDARD
        )

        assertTrue("Fajr should precede Sunrise", times.fajr < times.sunrise)
        assertTrue("Sunrise should precede Dhuhr", times.sunrise < times.dhuhr)
        assertTrue("Dhuhr should precede Asr", times.dhuhr < times.asr)
        assertTrue("Asr should precede Maghrib", times.asr < times.maghrib)
        assertTrue("Maghrib should precede Isha", times.maghrib < times.isha)
    }

    @Test
    fun qiblaCalculator_bearingAndDistanceAreValid() {
        // Cairo coordinates: 30.0444, 31.2357
        val cairoBearing = QiblaCalculator.calculateQiblaBearing(30.0444, 31.2357)
        assertTrue("Cairo Qibla bearing should be south-east ~136°", cairoBearing in 120f..150f)

        val distanceKm = QiblaCalculator.calculateDistanceToKaabaKm(30.0444, 31.2357)
        assertTrue("Distance between Cairo and Mecca should be ~1280 km", distanceKm in 1100..1500)
    }

    @Test
    fun hijriCalendar_returnsValidDate() {
        val cal = Calendar.getInstance().apply {
            set(2026, Calendar.OCTOBER, 23)
        }
        val hijri = HijriCalendarHelper.getHijriDate(cal)
        assertTrue("Hijri day should be 1..30", hijri.day in 1..30)
        assertTrue("Hijri month should be 1..12", hijri.month in 1..12)
        assertTrue("Hijri year should be around 1448 AH", hijri.year in 1446..1450)
        assertTrue("Month name should not be blank", hijri.monthNameEn.isNotBlank())
    }

    @Test
    fun tajweedData_rulesAreLoadedAndContainExamples() {
        val rules = com.example.data.TajweedData.RULES
        assertTrue("Should have multiple Tajweed rules defined", rules.size >= 8)
        rules.forEach { rule ->
            assertTrue("Rule title must not be blank: ${rule.id}", rule.title.isNotBlank())
            assertTrue("Rule must have at least one example: ${rule.id}", rule.examples.isNotEmpty())
            rule.examples.forEach { example ->
                assertTrue("Example Arabic text must not be blank", example.arabicText.isNotBlank())
                assertTrue("Example Surah reference must not be blank", example.surahReference.isNotBlank())
            }
        }
    }

    @Test
    fun tajweedData_quizQuestionsHaveValidCorrectIndices() {
        val questions = com.example.data.TajweedData.QUIZ_QUESTIONS
        assertTrue("Should have Tajweed quiz questions", questions.isNotEmpty())
        questions.forEach { q ->
            assertTrue("Question must have options", q.options.size >= 2)
            assertTrue("Correct index must be within range", q.correctIndex in q.options.indices)
            assertTrue("Explanation must be provided", q.explanation.isNotBlank())
        }
    }

    @Test
    fun appStrings_returnsTranslationsAcrossLanguages() {
        for (lang in com.example.model.AppLanguage.entries) {
            val fajr = com.example.util.AppStrings.getPrayerName(com.example.model.PrayerType.FAJR, lang)
            val navPrayer = com.example.util.AppStrings.t("nav_prayer", lang)
            assertTrue("Fajr name should not be blank for ${lang.displayName}", fajr.isNotBlank())
            assertTrue("Nav title should not be blank for ${lang.displayName}", navPrayer.isNotBlank())
        }
    }
}

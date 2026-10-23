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
}

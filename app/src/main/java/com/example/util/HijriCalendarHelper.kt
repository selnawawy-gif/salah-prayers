package com.example.util

import java.util.Calendar
import kotlin.math.floor

object HijriCalendarHelper {

    private val HIJRI_MONTH_NAMES_EN = listOf(
        "Muharram", "Safar", "Rabi' al-Awwal", "Rabi' al-Thani",
        "Jumada al-Awwal", "Jumada al-Thani", "Rajab", "Sha'ban",
        "Ramadan", "Shawwal", "Dhu al-Qi'dah", "Dhu al-Hijjah"
    )

    private val HIJRI_MONTH_NAMES_AR = listOf(
        "محرم", "صفر", "ربيع الأول", "ربيع الثاني",
        "جمادى الأولى", "جمادى الآخرة", "رجب", "شعبان",
        "رمضان", "شوال", "ذو القعدة", "ذو الحجة"
    )

    data class HijriDate(
        val day: Int,
        val month: Int, // 1..12
        val year: Int,
        val monthNameEn: String,
        val monthNameAr: String
    ) {
        fun formatEn(): String = "$day $monthNameEn $year AH"
        fun formatAr(): String = "$day $monthNameAr $year هـ"
    }

    /**
     * Converts a Gregorian calendar date to Hijri using astronomical epoch calculation.
     */
    fun getHijriDate(calendar: Calendar = Calendar.getInstance(), dayAdjustment: Int = 0): HijriDate {
        val y = calendar.get(Calendar.YEAR)
        val m = calendar.get(Calendar.MONTH) + 1
        val d = calendar.get(Calendar.DAY_OF_MONTH)

        var julianDay = julianDay(y, m, d) + dayAdjustment

        // Epoch of Islamic Calendar: July 16, 622 CE (JD 1948439.5)
        val l = (julianDay - 1948440.0) + 10632.0
        val n = floor((l - 1.0) / 10631.0)
        val lPrime = l - 10631.0 * n + 354.0

        val j = (floor((10985.0 - lPrime) / 5316.0)) * (floor((50.0 * lPrime) / 17719.0)) +
                (floor(lPrime / 5670.0)) * (floor((43.0 * lPrime) / 15238.0))
        val lSecond = lPrime - (floor((30.0 - j) / 15.0)) * (floor((17719.0 * j) / 50.0)) -
                (floor(j / 16.0)) * (floor((15238.0 * j) / 43.0)) + 29.0

        val hijriMonth = floor((24.0 * lSecond) / 709.0).toInt()
        val hijriDay = (lSecond - floor((709.0 * hijriMonth) / 24.0)).toInt()
        val hijriYear = (30.0 * n + j - 30.0).toInt()

        val normalizedMonth = ((hijriMonth - 1).coerceIn(0, 11))
        return HijriDate(
            day = hijriDay.coerceIn(1, 30),
            month = normalizedMonth + 1,
            year = hijriYear,
            monthNameEn = HIJRI_MONTH_NAMES_EN[normalizedMonth],
            monthNameAr = HIJRI_MONTH_NAMES_AR[normalizedMonth]
        )
    }

    private fun julianDay(year: Int, month: Int, day: Int): Double {
        var y = year
        var m = month
        if (m <= 2) {
            y -= 1
            m += 12
        }
        val a = floor(y / 100.0)
        val b = 2 - a + floor(a / 4.0)
        return floor(365.25 * (y + 4716)) + floor(30.6001 * (m + 1)) + day + b - 1524.5
    }
}

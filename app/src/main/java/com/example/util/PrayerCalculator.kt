package com.example.util

import com.example.model.CalculationMethod
import com.example.model.HighLatitudeRule
import com.example.model.JuristicMethod
import com.example.model.PrayerType
import java.util.Calendar
import java.util.Date
import java.util.TimeZone
import kotlin.math.*

object PrayerCalculator {

    data class CalculatedTimes(
        val fajr: Long,
        val sunrise: Long,
        val dhuhr: Long,
        val asr: Long,
        val maghrib: Long,
        val isha: Long
    )

    fun calculateTimes(
        calendar: Calendar,
        latitude: Double,
        longitude: Double,
        timezoneOffsetHours: Double,
        method: CalculationMethod = CalculationMethod.MUSLIM_WORLD_LEAGUE,
        juristicMethod: JuristicMethod = JuristicMethod.STANDARD,
        highLatRule: HighLatitudeRule = HighLatitudeRule.ANGLE_BASED,
        minuteAdjustments: Map<PrayerType, Int> = emptyMap()
    ): CalculatedTimes {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        // Julian date
        val d = julianDay(year, month, day) - 2451545.0

        // Mean solar longitude
        val l0 = fixAngle(280.460 + 0.9856474 * d)
        // Mean anomaly
        val m = fixAngle(357.528 + 0.9856003 * d)
        // Ecliptic longitude
        val lambda = fixAngle(l0 + 1.915 * sin(Math.toRadians(m)) + 0.020 * sin(Math.toRadians(2 * m)))
        // Obliquity of ecliptic
        val obli = 23.439 - 0.0000004 * d

        // Sun declination
        val sinDelta = sin(Math.toRadians(obli)) * sin(Math.toRadians(lambda))
        val delta = Math.toDegrees(asin(sinDelta))

        // Right ascension
        val ra = fixAngle(Math.toDegrees(atan2(
            cos(Math.toRadians(obli)) * sin(Math.toRadians(lambda)),
            cos(Math.toRadians(lambda))
        )))

        // Equation of time in hours
        val eqt = (l0 - ra) / 15.0

        // Solar noon (Dhuhr) in fractional hours
        val dhuhrBase = 12.0 + timezoneOffsetHours - (longitude / 15.0) - eqt

        // Sun radius & atmospheric refraction: -0.833 degrees
        val sunriseAngle = -0.833

        // Hour angle for Sunrise / Sunset
        val sunRiseHourAngle = hourAngle(sunriseAngle, latitude, delta)
        val sunriseHour = dhuhrBase - sunRiseHourAngle / 15.0
        val sunsetHour = dhuhrBase + sunRiseHourAngle / 15.0

        // Fajr Hour Angle
        val fajrAngle = -method.fajrAngle
        val fajrHourAngle = hourAngle(fajrAngle, latitude, delta, fallbackAngle = 18.0)
        var fajrHour = dhuhrBase - fajrHourAngle / 15.0

        // Asr calculation
        val shadowFactor = juristicMethod.shadowFactor
        val deltaPhiDiff = abs(latitude - delta)
        val asrAltRad = atan(1.0 / (shadowFactor + tan(Math.toRadians(deltaPhiDiff))))
        val asrAltDeg = Math.toDegrees(asrAltRad)
        val asrHourAngle = hourAngle(asrAltDeg, latitude, delta)
        val asrHour = dhuhrBase + asrHourAngle / 15.0

        // Maghrib
        val maghribHour = sunsetHour

        // Isha calculation
        var ishaHour: Double
        if (method.ishaMinutesAfterMaghrib != null && method.ishaMinutesAfterMaghrib > 0) {
            ishaHour = maghribHour + (method.ishaMinutesAfterMaghrib / 60.0)
        } else {
            val ishaAngle = -method.ishaAngle
            val ishaHourAngle = hourAngle(ishaAngle, latitude, delta, fallbackAngle = 17.0)
            ishaHour = dhuhrBase + ishaHourAngle / 15.0
        }

        // High latitude adjustments if needed
        val nightDurationHours = 24.0 - (sunsetHour - sunriseHour)
        if (abs(latitude) > 48.5) {
            when (highLatRule) {
                HighLatitudeRule.MIDDLE_OF_NIGHT -> {
                    val halfNight = nightDurationHours / 2.0
                    if (fajrHour < sunriseHour - halfNight) fajrHour = sunriseHour - halfNight
                    if (ishaHour > sunsetHour + halfNight) ishaHour = sunsetHour + halfNight
                }
                HighLatitudeRule.SEVENTH_OF_NIGHT -> {
                    val portion = nightDurationHours / 7.0
                    if (fajrHour < sunriseHour - portion) fajrHour = sunriseHour - portion
                    if (ishaHour > sunsetHour + portion) ishaHour = sunsetHour + portion
                }
                HighLatitudeRule.ANGLE_BASED -> {
                    val fajrPortion = (method.fajrAngle / 60.0) * nightDurationHours
                    val ishaPortion = (method.ishaAngle / 60.0) * nightDurationHours
                    if (fajrHour < sunriseHour - fajrPortion) fajrHour = sunriseHour - fajrPortion
                    if (ishaHour > sunsetHour + ishaPortion) ishaHour = sunsetHour + ishaPortion
                }
            }
        }

        // Convert fractional hours into exact milliseconds for today
        val baseMidnightCal = (calendar.clone() as Calendar).apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val midnightMillis = baseMidnightCal.timeInMillis

        fun toMillis(fractionalHours: Double, type: PrayerType): Long {
            val totalSeconds = (fractionalHours * 3600.0).toLong()
            val adjustmentMinutes = minuteAdjustments[type] ?: 0
            return midnightMillis + (totalSeconds * 1000L) + (adjustmentMinutes * 60_000L)
        }

        return CalculatedTimes(
            fajr = toMillis(fajrHour, PrayerType.FAJR),
            sunrise = toMillis(sunriseHour, PrayerType.SUNRISE),
            dhuhr = toMillis(dhuhrBase, PrayerType.DHUHR),
            asr = toMillis(asrHour, PrayerType.ASR),
            maghrib = toMillis(maghribHour, PrayerType.MAGHRIB),
            isha = toMillis(ishaHour, PrayerType.ISHA)
        )
    }

    private fun hourAngle(altitude: Double, latitude: Double, delta: Double, fallbackAngle: Double = 15.0): Double {
        val altRad = Math.toRadians(altitude)
        val latRad = Math.toRadians(latitude)
        val deltaRad = Math.toRadians(delta)

        val cosH = (sin(altRad) - sin(latRad) * sin(deltaRad)) / (cos(latRad) * cos(deltaRad))
        return if (cosH < -1.0) {
            180.0
        } else if (cosH > 1.0) {
            0.0
        } else {
            Math.toDegrees(acos(cosH))
        }
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

    private fun fixAngle(angle: Double): Double {
        var a = angle % 360.0
        if (a < 0) a += 360.0
        return a
    }
}

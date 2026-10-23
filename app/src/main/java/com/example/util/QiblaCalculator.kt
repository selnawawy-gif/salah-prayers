package com.example.util

import kotlin.math.*

object QiblaCalculator {
    const val KAABA_LATITUDE = 21.422487
    const val KAABA_LONGITUDE = 39.826206
    private const val EARTH_RADIUS_KM = 6371.0

    /**
     * Calculates the true bearing (in degrees clockwise from True North, 0..360)
     * towards the Kaaba in Mecca from the provided user coordinates.
     */
    fun calculateQiblaBearing(userLat: Double, userLng: Double): Float {
        val phi1 = Math.toRadians(userLat)
        val phi2 = Math.toRadians(KAABA_LATITUDE)
        val deltaLambda = Math.toRadians(KAABA_LONGITUDE - userLng)

        val y = sin(deltaLambda)
        val x = cos(phi1) * tan(phi2) - sin(phi1) * cos(deltaLambda)

        var qiblaRad = atan2(y, x)
        var qiblaDeg = Math.toDegrees(qiblaRad)

        // Normalize to 0..360
        qiblaDeg = (qiblaDeg + 360.0) % 360.0
        return qiblaDeg.toFloat()
    }

    /**
     * Calculates the great-circle distance in kilometers to the Kaaba.
     */
    fun calculateDistanceToKaabaKm(userLat: Double, userLng: Double): Int {
        val phi1 = Math.toRadians(userLat)
        val phi2 = Math.toRadians(KAABA_LATITUDE)
        val deltaPhi = Math.toRadians(KAABA_LATITUDE - userLat)
        val deltaLambda = Math.toRadians(KAABA_LONGITUDE - userLng)

        val a = sin(deltaPhi / 2.0).pow(2) + cos(phi1) * cos(phi2) * sin(deltaLambda / 2.0).pow(2)
        val c = 2.0 * atan2(sqrt(a), sqrt(1.0 - a))
        return (EARTH_RADIUS_KM * c).roundToInt()
    }
}

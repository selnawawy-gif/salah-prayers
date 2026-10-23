package com.example.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import androidx.core.content.ContextCompat
import com.example.data.PresetCities
import com.example.model.CalculationMethod
import com.example.model.UserCity
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.util.Locale
import java.util.TimeZone
import kotlin.coroutines.resume

object RegionAutoDetector {

    data class DetectionResult(
        val city: UserCity,
        val recommendedMethod: CalculationMethod,
        val detectionSource: String // "GPS", "Network", "Locale & Timezone"
    )

    /**
     * Auto-detects the user's region and city.
     * Uses GPS/FusedLocation if permission is granted, otherwise falls back
     * to device TimeZone & Locale to determine the region.
     */
    suspend fun detectRegion(context: Context): DetectionResult = withContext(Dispatchers.IO) {
        val hasFine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val hasCoarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

        if (hasFine || hasCoarse) {
            val location = fetchGpsLocation(context)
            if (location != null) {
                val address = reverseGeocode(context, location.latitude, location.longitude)
                val tzOffset = (TimeZone.getDefault().getOffset(System.currentTimeMillis()) / 3600000.0)

                val cityName = address?.locality
                    ?: address?.subAdminArea
                    ?: address?.adminArea
                    ?: findClosestPresetCity(location.latitude, location.longitude).name
                val countryName = address?.countryName ?: Locale.getDefault().displayCountry
                val countryCode = address?.countryCode ?: Locale.getDefault().country

                val userCity = UserCity(
                    name = cityName,
                    country = countryName,
                    latitude = location.latitude,
                    longitude = location.longitude,
                    timezoneOffsetHours = tzOffset
                )

                val recommendedMethod = getRecommendedCalculationMethod(countryCode, location.latitude, location.longitude)
                return@withContext DetectionResult(userCity, recommendedMethod, "GPS / Fused Location")
            }
        }

        // Fallback: detect region from system Timezone & Locale
        return@withContext detectFromLocaleAndTimezone()
    }

    private suspend fun fetchGpsLocation(context: Context): Location? {
        return try {
            val fusedClient = LocationServices.getFusedLocationProviderClient(context)
            val cts = CancellationTokenSource()

            suspendCancellableCoroutine { continuation ->
                fusedClient.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, cts.token)
                    .addOnSuccessListener { loc ->
                        if (loc != null) {
                            continuation.resume(loc)
                        } else {
                            // Try lastLocation
                            fusedClient.lastLocation.addOnSuccessListener { lastLoc ->
                                continuation.resume(lastLoc ?: getFallbackLocationFromManager(context))
                            }.addOnFailureListener {
                                continuation.resume(getFallbackLocationFromManager(context))
                            }
                        }
                    }
                    .addOnFailureListener {
                        continuation.resume(getFallbackLocationFromManager(context))
                    }
            }
        } catch (e: Exception) {
            getFallbackLocationFromManager(context)
        }
    }

    private fun getFallbackLocationFromManager(context: Context): Location? {
        return try {
            val lm = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager ?: return null
            var best: Location? = null
            for (provider in lm.getProviders(true)) {
                val loc = lm.getLastKnownLocation(provider) ?: continue
                if (best == null || loc.accuracy < best.accuracy) {
                    best = loc
                }
            }
            best
        } catch (e: Exception) {
            null
        }
    }

    private fun reverseGeocode(context: Context, lat: Double, lng: Double): Address? {
        return try {
            val geocoder = Geocoder(context, Locale.getDefault())
            val results = geocoder.getFromLocation(lat, lng, 1)
            results?.firstOrNull()
        } catch (e: Exception) {
            null
        }
    }

    fun detectFromLocaleAndTimezone(): DetectionResult {
        val currentTz = TimeZone.getDefault()
        val currentTzId = currentTz.id
        val tzOffset = currentTz.getOffset(System.currentTimeMillis()) / 3600000.0
        val countryCode = Locale.getDefault().country.uppercase()

        // Match against curated preset cities
        val matchedCity = PresetCities.CITIES.firstOrNull { city ->
            city.country.equals(Locale.getDefault().displayCountry, ignoreCase = true) ||
            currentTzId.contains(city.name, ignoreCase = true)
        } ?: PresetCities.CITIES.minByOrNull { city ->
            Math.abs(city.timezoneOffsetHours - tzOffset)
        } ?: PresetCities.DEFAULT_CITY

        val userCity = matchedCity.copy(timezoneOffsetHours = tzOffset)
        val recommendedMethod = getRecommendedCalculationMethod(countryCode, userCity.latitude, userCity.longitude)

        return DetectionResult(userCity, recommendedMethod, "Device Region (${currentTz.displayName})")
    }

    fun getRecommendedCalculationMethod(countryCode: String, lat: Double, lng: Double): CalculationMethod {
        val cc = countryCode.uppercase()
        return when {
            cc in listOf("SA", "QA", "KW", "OM", "YE", "BH") -> CalculationMethod.UMM_AL_QURA
            cc == "AE" -> CalculationMethod.DUBAI
            cc in listOf("EG", "LY", "SD", "JO", "PS", "LB", "SY", "MA", "DZ", "TN", "IQ") -> CalculationMethod.EGYPT
            cc in listOf("PK", "IN", "BD", "AF") -> CalculationMethod.KARACHI
            cc in listOf("US", "CA") -> CalculationMethod.ISNA
            cc == "IR" -> CalculationMethod.TEHRAN
            else -> CalculationMethod.MUSLIM_WORLD_LEAGUE
        }
    }

    private fun findClosestPresetCity(lat: Double, lng: Double): UserCity {
        return PresetCities.CITIES.minByOrNull { city ->
            val dLat = city.latitude - lat
            val dLng = city.longitude - lng
            dLat * dLat + dLng * dLng
        } ?: PresetCities.DEFAULT_CITY
    }
}

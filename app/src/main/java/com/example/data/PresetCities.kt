package com.example.data

import com.example.model.UserCity

object PresetCities {
    val CITIES = listOf(
        UserCity("Makkah", "Saudi Arabia", 21.4225, 39.8262, 3.0),
        UserCity("Madinah", "Saudi Arabia", 24.4672, 39.6111, 3.0),
        UserCity("Riyadh", "Saudi Arabia", 24.7136, 46.6753, 3.0),
        UserCity("Cairo", "Egypt", 30.0444, 31.2357, 2.0),
        UserCity("Istanbul", "Turkey", 41.0082, 28.9784, 3.0),
        UserCity("Dubai", "United Arab Emirates", 25.2048, 55.2708, 4.0),
        UserCity("Abu Dhabi", "United Arab Emirates", 24.4539, 54.3773, 4.0),
        UserCity("Doha", "Qatar", 25.2854, 51.5310, 3.0),
        UserCity("Amman", "Jordan", 31.9454, 35.9284, 3.0),
        UserCity("Kuwait City", "Kuwait", 29.3759, 47.9774, 3.0),
        UserCity("Casablanca", "Morocco", 33.5731, -7.5898, 1.0),
        UserCity("London", "United Kingdom", 51.5074, -0.1278, 0.0),
        UserCity("Paris", "France", 48.8566, 2.3522, 1.0),
        UserCity("Berlin", "Germany", 52.5200, 13.4050, 1.0),
        UserCity("New York", "United States", 40.7128, -74.0060, -5.0),
        UserCity("Los Angeles", "United States", 34.0522, -118.2437, -8.0),
        UserCity("Chicago", "United States", 41.8781, -87.6298, -6.0),
        UserCity("Toronto", "Canada", 43.6532, -79.3832, -5.0),
        UserCity("Jakarta", "Indonesia", -6.2088, 106.8456, 7.0),
        UserCity("Kuala Lumpur", "Malaysia", 3.1390, 101.6869, 8.0),
        UserCity("Karachi", "Pakistan", 24.8607, 67.0011, 5.0),
        UserCity("Lahore", "Pakistan", 31.5204, 74.3587, 5.0),
        UserCity("Dhaka", "Bangladesh", 23.8103, 90.4125, 6.0),
        UserCity("Sydney", "Australia", -33.8688, 151.2093, 10.0),
        UserCity("Melbourne", "Australia", -37.8136, 144.9631, 10.0),
        UserCity("Tokyo", "Japan", 35.6762, 139.6503, 9.0),
        UserCity("Singapore", "Singapore", 1.3521, 103.8198, 8.0)
    )

    val DEFAULT_CITY = CITIES[0] // Makkah
}
